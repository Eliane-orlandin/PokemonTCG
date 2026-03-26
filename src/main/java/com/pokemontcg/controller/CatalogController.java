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
    @FXML private Label lblPagination;
    @FXML private javafx.scene.control.ComboBox<String> comboCategoryLocal;
    @FXML private javafx.scene.control.TextField txtSearchLocal;
    @FXML private javafx.scene.control.Button btnPrevPage;
    @FXML private javafx.scene.control.Button btnNextPage;

    private final CatalogService catalogService;
    private String rarityFilter = null;
    
    // Variáveis de Paginação
    private List<CatalogEntry> fullFilteredList;
    private int currentPage = 1;
    private final int PAGE_SIZE = 12; // Sugestão de 12 cards por página
    
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
        this.currentPage = 1; // Reseta para a primeira página ao buscar
        loadCatalogFromDatabase(query); 
    }

    @FXML
    public void handleNextPage() {
        if (fullFilteredList == null) return;
        int totalPages = (int) Math.ceil((double) fullFilteredList.size() / PAGE_SIZE);
        if (currentPage < totalPages) {
            currentPage++;
            updateViewWithCurrentPage();
        }
    }

    @FXML
    public void handlePrevPage() {
        if (currentPage > 1) {
            currentPage--;
            updateViewWithCurrentPage();
        }
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
                    this.fullFilteredList = filteredEntries;
                    updateViewWithCurrentPage();
                });
            } catch (Exception e) {
                System.err.println("[Catalogo] Erro ao carregar banco: " + e.getMessage());
                e.printStackTrace();
            }
        });
        t.setDaemon(true); 
        t.start();
    }

    /**
     * Atualiza a UI baseada na página atual e na lista filtrada.
     */
    private void updateViewWithCurrentPage() {
        if (fullFilteredList == null) return;

        int totalItems = fullFilteredList.size();
        int totalPages = (int) Math.ceil((double) totalItems / PAGE_SIZE);
        if (totalPages == 0) totalPages = 1;

        // Limite para não passar do total
        if (currentPage > totalPages) currentPage = totalPages;

        // Cálculo da sublista para a página atual
        int fromIndex = (currentPage - 1) * PAGE_SIZE;
        int toIndex = Math.min(fromIndex + PAGE_SIZE, totalItems);

        List<CatalogEntry> pageEntries = fullFilteredList.subList(fromIndex, toIndex);

        // Atualiza a tabela
        vboxCatalog.getChildren().clear();
        renderRows(pageEntries);

        // Atualiza Labels e Botões
        lblPagination.setText(String.format("Página %d de %d (%d itens)", currentPage, totalPages, totalItems));
        btnPrevPage.setDisable(currentPage == 1);
        btnNextPage.setDisable(currentPage >= totalPages);

        if (lblCatalogStats != null) {
            int totalCardsGlobal = fullFilteredList.stream().mapToInt(CatalogEntry::getQuantity).sum();
            lblCatalogStats.setText("Total de Cartas na Coleção: " + totalCardsGlobal);
        }
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
