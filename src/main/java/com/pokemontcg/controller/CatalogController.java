package com.pokemontcg.controller;

import com.pokemontcg.model.CatalogEntry;
import com.pokemontcg.service.CatalogService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.util.List;

/**
 * Controlador da tela de Catálogo Pessoal (Minha Coleção).
 * Busca as cartas salvas no SQLite através do CatalogRepository e
 * preenche a tabela visual dinâmica.
 */
public class CatalogController {

    @FXML private VBox vboxCatalogRows;

    private final CatalogService catalogService;
    
    public CatalogController() {
        this.catalogService = new CatalogService();
    }

    @FXML
    public void initialize() {
        loadCatalogFromDatabase();
    }

    /**
     * Carrega as entradas do banco e preenche a lista visual.
     */
    private void loadCatalogFromDatabase() {
        new Thread(() -> {
            try {
                List<CatalogEntry> entries = catalogService.getAllCardsInCatalog();
                
                Platform.runLater(() -> {
                    vboxCatalogRows.getChildren().clear();
                    renderRows(entries);
                    System.out.println("[Catalog] " + entries.size() + " itens carregados do SQLite.");
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
                controller.setOnDeleteCallback(() -> loadCatalogFromDatabase()); // Atualiza a lista ao deletar
                
                vboxCatalogRows.getChildren().add(rowNode);
                
            } catch (IOException e) {
                System.err.println("Erro ao carregar catalog_row: " + e.getMessage());
            }
        }
    }
}
