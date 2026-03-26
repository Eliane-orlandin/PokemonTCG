package com.pokemontcg.controller;

import com.pokemontcg.model.CatalogEntry;
import com.pokemontcg.service.CatalogService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador da tela de Catálogo Pessoal (Minha Coleção).
 * Busca as cartas salvas no SQLite através do CatalogRepository e
 * preenche a tabela visual dinâmica.
 */
public class CatalogController {

    @FXML private VBox vboxCatalog;
    @FXML private Label lblCatalogStats;
    @FXML private javafx.scene.control.ComboBox<String> comboCategoryLocal;
    @FXML private javafx.scene.control.TextField txtSearchLocal;

    private final CatalogService catalogService;
    private String rarityFilter = null;
    
    public CatalogController() {
        this.catalogService = new CatalogService();
    }

    @FXML
    public void initialize() {
        System.out.println("[Catalogo] Inicializando...");
        
        // Inicializar categorías locais
        if (comboCategoryLocal != null) {
            comboCategoryLocal.getItems().clear();
            comboCategoryLocal.getItems().addAll("Todas", "Pokémon", "Treinador", "Energia");
            comboCategoryLocal.getSelectionModel().selectFirst();
            
            comboCategoryLocal.setOnAction(e -> handleLocalSearch());
        }
        
        if (txtSearchLocal != null) {
            txtSearchLocal.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    handleLocalSearch();
                }
            });
        }
        
        // Dispara o carregamento inicial (Platform.runLater garante que o rarityFilter injetado já esteja lá)
        Platform.runLater(() -> loadCatalogFromDatabase(null));
    }

    /**
     * Permite definir um filtro de raridade antes ou logo após o carregamento.
     */
    public void setInitialRarityFilter(String rarity) {
        this.rarityFilter = rarity;
    }

    @FXML
    public void handleLocalSearch() {
        String query = txtSearchLocal.getText().toLowerCase().trim();
        System.out.println("[Catalogo] Filtrando por: " + query);
        loadCatalogFromDatabase(query); 
    }

    /**
     * Carrega as entradas do banco e preenche a lista visual com filtro opcional.
     */
    private void loadCatalogFromDatabase(String nameFilter) {
        // Captura o estado da UI ANTES de entrar na Thread de banco
        final String selectedCategory = (comboCategoryLocal != null && comboCategoryLocal.getValue() != null) 
                                        ? comboCategoryLocal.getValue() : "Todas";
        
        System.out.println("[Catalogo] Iniciando carregamento. Filtro Nome: " + nameFilter + ", Categoria: " + selectedCategory + ", FiltroDashboard: " + rarityFilter);

        Thread t = new Thread(() -> {
            try {
                // Recupera todos os cards registrados no banco
                List<CatalogEntry> entries = catalogService.getAllCardsInCatalog();
                System.out.println("[Catalogo] Registros no banco: " + (entries != null ? entries.size() : 0));

                if (entries == null) return;

                // 1. Aplicar filtro de raridade rápida (vinda do Dashboard se houver)
                if (rarityFilter != null && !rarityFilter.isEmpty()) {
                    final String rRef = rarityFilter.toLowerCase();
                    entries = entries.stream()
                        .filter(e -> {
                            String r = e.getRarity() != null ? e.getRarity().toLowerCase() : "";
                            return r.contains("rara") || r.contains("holo") || r.contains("ultra") || 
                                   r.contains("secret") || r.contains("secreta") || r.contains("vmax") || 
                                   r.contains("vstar") || r.contains("ilustração") || r.contains("especial") ||
                                   r.contains("shiny") || r.contains("promo");
                        })
                        .collect(Collectors.toList());
                    System.out.println("[Catalogo] Após filtro de raridade dashboard: " + entries.size());
                }

                // 2. Aplicar filtro local (busca por texto)
                if (nameFilter != null && !nameFilter.isEmpty()) {
                    final String nRef = nameFilter.toLowerCase();
                    entries = entries.stream()
                        .filter(e -> (e.getCardName() != null && e.getCardName().toLowerCase().contains(nRef)) ||
                                     (e.getSeriesName() != null && e.getSeriesName().toLowerCase().contains(nRef)))
                        .collect(Collectors.toList());
                    System.out.println("[Catalogo] Após filtro de nome/série: " + entries.size());
                }

                // 3. Aplicar filtro de categoria
                if (!selectedCategory.equalsIgnoreCase("Todas")) {
                    entries = entries.stream()
                        .filter(e -> e.getCategory() != null && e.getCategory().equalsIgnoreCase(selectedCategory))
                        .collect(Collectors.toList());
                    System.out.println("[Catalogo] Após filtro de categoria (" + selectedCategory + "): " + entries.size());
                }

                final List<CatalogEntry> filteredEntries = entries;

                Platform.runLater(() -> {
                    if (vboxCatalog != null) {
                        // Limpa o container dinâmico (o cabeçalho agora está fixo fora dele no FXML)
                        vboxCatalog.getChildren().clear();
                        renderRows(filteredEntries);
                    }
                    
                    if (lblCatalogStats != null) {
                        int totalCards = filteredEntries.stream().mapToInt(CatalogEntry::getQuantity).sum();
                        lblCatalogStats.setText("Total de Cartas: " + totalCards);
                    }
                });
            } catch (Exception e) {
                System.err.println("[Catalogo] Erro ao carregar banco: " + e.getMessage());
                e.printStackTrace();
            }
        });
        t.setDaemon(true); // Não impede a JVM de fechar
        t.start();
    }

    private void renderRows(List<CatalogEntry> entries) {
        if (entries == null || entries.isEmpty()) {
            return;
        }

        for (CatalogEntry entry : entries) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/catalog_row.fxml"));
                Node rowNode = loader.load();
                
                CatalogRowController controller = loader.getController();
                // Passa o serviço já existente para economizar recursos
                controller.setService(catalogService);
                controller.setRowData(entry);
                controller.setOnDeleteCallback(() -> loadCatalogFromDatabase(null));
                
                vboxCatalog.getChildren().add(rowNode);
                
            } catch (Exception e) {
                System.err.println("Erro crítico ao carregar catalog_row: " + e.getMessage());
                if (e.getCause() != null) {
                    System.err.println("Causa raiz: " + e.getCause().getMessage());
                    e.getCause().printStackTrace();
                } else {
                    e.printStackTrace();
                }
            }
        }
    }
}
