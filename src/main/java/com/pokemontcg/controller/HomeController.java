package com.pokemontcg.controller;

import com.pokemontcg.model.CatalogEntry;
import com.pokemontcg.repository.CatalogRepository;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.util.List;

/**
 * Controlador para a tela de Dashboard (Home).
 * Calcula estatísticas em tempo real baseadas no banco de dados SQLite.
 */
public class HomeController {

    @FXML private Label lblTotalCards;
    @FXML private Label lblRareCards;

    private CatalogRepository repository = new CatalogRepository();

    @FXML
    public void initialize() {
        updateStatistics();
    }

    @FXML
    public void handleSearchShortcut() {
        System.out.println("[Dashboard] Atalho: Buscar Novos Cards");
        // A lógica de navegação centralizada deve ser chamada via evento ou referência ao MainController se necessário
    }

    @FXML
    public void handleCollectionShortcut() {
        System.out.println("[Dashboard] Atalho: Ver Minha Coleção");
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
                    if (rarity.contains("rare") || rarity.contains("promo") || rarity.contains("vmax") || rarity.contains("vstar")) {
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
