package com.pokemontcg.controller;

import com.pokemontcg.model.CatalogEntry;
import com.pokemontcg.repository.CatalogRepository;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import java.util.List;

/**
 * Controlador para a tela de Dashboard (Home).
 * Calcula estatísticas em tempo real baseadas no banco de dados SQLite.
 */
public class HomeController {

    @FXML private Label lblTotalCards;
    @FXML private Label lblRareCards;
    @FXML private VBox dashboardVBox;

    private CatalogRepository repository = new CatalogRepository();

    @FXML
    public void initialize() {
        setupDashboardClip();
        updateStatistics();
    }

    private void setupDashboardClip() {
        // Cria um retângulo de recorte com as mesmas medidas de arredondamento
        Rectangle clip = new Rectangle();
        clip.setArcWidth(60); // Diâmetro de 60 para raio de 30px
        clip.setArcHeight(60);
        
        // Faz o recorte acompanhar o tamanho dinâmico do container
        clip.widthProperty().bind(dashboardVBox.widthProperty());
        clip.heightProperty().bind(dashboardVBox.heightProperty());
        
        dashboardVBox.setClip(clip);
    }
    
    @FXML
    public void handleNavigateCatalog() {
        System.out.println("[Dashboard] Redirecionando para Coleção...");
        if (MainController.getInstance() != null) {
            MainController.getInstance().navigateCatalog(null);
        }
    }

    @FXML
    public void handleNavigateRareCards() {
        System.out.println("[Dashboard] Redirecionando para Cartas Raras...");
        if (MainController.getInstance() != null) {
            MainController.getInstance().navigateCatalog("rare");
        }
    }

    private void updateStatistics() {
        new Thread(() -> {
            try {
                List<CatalogEntry> allCards = repository.findAll();
                int totalQuantity = 0;
                int rareCount = 0;
                
                for (CatalogEntry entry : allCards) {
                    totalQuantity += entry.getQuantity();
                    String rarity = entry.getRarity() != null ? entry.getRarity().toLowerCase() : "";
                    
                    // Lógica sincronizada com o filtro do Catálogo
                    boolean isRare = rarity.contains("rara") || 
                                     rarity.contains("holo") || 
                                     rarity.contains("ultra") || 
                                     rarity.contains("secret") || 
                                     rarity.contains("secreta") || 
                                     rarity.contains("vmax") || 
                                     rarity.contains("vstar") || 
                                     rarity.contains("ilustração") ||
                                     rarity.contains("especial") ||
                                     rarity.contains("shiny") ||
                                     rarity.contains("promo");

                    if (isRare) {
                        rareCount += entry.getQuantity();
                    }
                }
                
                final int finalTotal = totalQuantity;
                final int finalRare = rareCount;
                
                Platform.runLater(() -> {
                    lblTotalCards.setText(String.format("%,d", finalTotal));
                    lblRareCards.setText(String.format("%,d", finalRare));
                });
            } catch (Exception e) {
                System.err.println("Erro ao atualizar estatísticas: " + e.getMessage());
            }
        }).start();
    }
}
