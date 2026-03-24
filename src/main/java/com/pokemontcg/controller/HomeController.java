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
    @FXML private Label lblDominantType;
    @FXML private Label lblDominantCount;

    private CatalogRepository repository = new CatalogRepository();

    @FXML
    public void initialize() {
        updateStatistics();
    }

    private void updateStatistics() {
        new Thread(() -> {
            try {
                List<CatalogEntry> allCards = repository.findAll();
                int totalQuantity = allCards.stream().mapToInt(CatalogEntry::getQuantity).sum();
                
                // Para este MVP, vamos apenas contar o total. 
                // A lógica de "Tipo Dominante" pode ser expandida depois.
                
                Platform.runLater(() -> {
                    lblTotalCards.setText(String.format("%,d", totalQuantity));
                    // Se não houver cartas, mostramos valores padrão
                    if (totalQuantity == 0) {
                        lblDominantType.setText("None yet");
                        lblDominantCount.setText("0 CARDS CATALOGED");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
