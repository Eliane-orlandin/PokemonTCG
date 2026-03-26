package com.pokemontcg.controller;

import com.pokemontcg.model.CatalogEntry;
import com.pokemontcg.service.CatalogService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
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
        // Inicializar categorías locais (exemplo)
        if (comboCategoryLocal != null) {
            comboCategoryLocal.getItems().clear();
            comboCategoryLocal.getItems().addAll("Todas", "Pokémon", "Treinador", "Energia");
            comboCategoryLocal.getSelectionModel().selectFirst();
        }
        
        // Carrega o catálogo respeitando um possível filtro pré-existente
        loadCatalogFromDatabase(null);
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
        new Thread(() -> {
            try {
                List<CatalogEntry> entries = catalogService.getAllCardsInCatalog();
                
                // 1. Aplicar filtro de raridade se estiver definido (vinda do Dashboard)
                if (rarityFilter != null && !rarityFilter.isEmpty()) {
                    final String rRef = rarityFilter.toLowerCase();
                    entries = entries.stream()
                        .filter(e -> {
                            String r = e.getRarity() != null ? e.getRarity().toLowerCase() : "";
                            // Filtro abrangente para Cartas Especiais/Raras
                            return r.contains("rara") || 
                                   r.contains("holo") || 
                                   r.contains("ultra") || 
                                   r.contains("secret") || 
                                   r.contains("secreta") || 
                                   r.contains("vmax") || 
                                   r.contains("vstar") || 
                                   r.contains("ilustração") ||
                                   r.contains("especial") ||
                                   r.contains("shiny") ||
                                   r.contains("promo");
                        })
                        .collect(Collectors.toList());
                }

                // 2. Aplicar filtro local se houver texto na busca
                if (nameFilter != null && !nameFilter.isEmpty()) {
                    final String nRef = nameFilter.toLowerCase();
                    entries = entries.stream()
                        .filter(e -> e.getCardName().toLowerCase().contains(nRef))
                        .collect(Collectors.toList());
                }

                final List<CatalogEntry> finalEntries = entries;
                Platform.runLater(() -> {
                    if (vboxCatalog != null) {
                        vboxCatalog.getChildren().clear();
                        renderRows(finalEntries);
                    }
                    if (lblCatalogStats != null) {
                        lblCatalogStats.setText("Total de Cartas: " + finalEntries.size());
                    }
                    System.out.println(String.format("[Catalog] %d itens exibidos (Nomes: %s, Raridade: %s)", 
                        finalEntries.size(), 
                        (nameFilter != null ? nameFilter : "Nenhum"),
                        (rarityFilter != null ? rarityFilter : "Nenhuma")));
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void renderRows(List<CatalogEntry> entries) {
        for (CatalogEntry entry : entries) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/catalog_row.fxml"));
                Node rowNode = loader.load();
                
                CatalogRowController controller = loader.getController();
                controller.setRowData(entry);
                controller.setOnDeleteCallback(() -> loadCatalogFromDatabase(null)); // Atualiza a lista ao deletar
                
                vboxCatalog.getChildren().add(rowNode);
                
            } catch (IOException e) {
                System.err.println("Erro ao carregar catalog_row: " + e.getMessage());
            }
        }
    }
}
