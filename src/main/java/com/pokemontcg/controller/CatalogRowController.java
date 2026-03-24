package com.pokemontcg.controller;

import com.pokemontcg.model.CatalogEntry;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Controlador para uma linha individual da tabela de catálogo.
 */
public class CatalogRowController {

    @FXML private CheckBox checkSelect;
    @FXML private ImageView imgMini;
    @FXML private Label lblName;
    @FXML private Label lblSetId;
    @FXML private Label lblSetName;
    @FXML private Label lblType;
    @FXML private Label lblRarity;
    @FXML private Label lblQty;

    public void setRowData(CatalogEntry entry) {
        lblName.setText(entry.getCardName());
        lblSetId.setText(entry.getCardId());
        lblSetName.setText(entry.getSeriesName());
        lblQty.setText(String.format("%02d", entry.getQuantity()));
        
        // Raridade baseada no entry (usando um campo dummy se não existir no model interno por enquanto)
        lblRarity.setText("✪ Rare Holo"); 
        
        // Carregar imagem miniatura
        if (entry.getImageUrl() != null && !entry.getImageUrl().isEmpty()) {
            new Thread(() -> {
                Image img = new Image(entry.getImageUrl(), 40, 50, true, true);
                javafx.application.Platform.runLater(() -> imgMini.setImage(img));
            }).start();
        }
        
        // Estilização do badge de tipo
        updateTypeBadge("Fire"); // Dummy para teste inicial
    }

    private void updateTypeBadge(String type) {
        lblType.setText(type.toUpperCase());
        String color = "#78909C"; // Gray default
        if (type.equalsIgnoreCase("fire")) color = "#FF7043";
        else if (type.equalsIgnoreCase("water")) color = "#42A5F5";
        else if (type.equalsIgnoreCase("lightning")) color = "#FBC02D";
        
        lblType.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-background-radius: 12; -fx-padding: 3 12; -fx-font-weight: bold; -fx-font-size: 9;");
    }
}
