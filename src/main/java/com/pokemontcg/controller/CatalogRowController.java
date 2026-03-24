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
    private CatalogEntry entry;
    private Runnable onDeleteCallback;
    private com.pokemontcg.repository.CatalogRepository repository = new com.pokemontcg.repository.CatalogRepository();

    public void setOnDeleteCallback(Runnable callback) {
        this.onDeleteCallback = callback;
    }

    public void setRowData(CatalogEntry entry) {
        this.entry = entry; // Store the entry
        lblName.setText(entry.getCardName());
        lblSetId.setText(entry.getCardId());
        lblSetName.setText(entry.getSeriesName());
        lblQty.setText(String.format("%02d", entry.getQuantity()));
        
        lblRarity.setText(entry.getRarity() != null ? entry.getRarity() : "Unknown"); 
        
        // Carregar imagem miniatura
        if (entry.getImageUrl() != null && !entry.getImageUrl().isEmpty()) {
            new Thread(() -> {
                Image img = new Image(entry.getImageUrl(), 40, 50, true, true);
                javafx.application.Platform.runLater(() -> imgMini.setImage(img));
            }).start();
        }
        
        // Estilização do badge de tipo
        updateTypeBadge(entry.getType());
    }

    @FXML
    public void handleDelete() {
        if (entry != null && entry.getCardId() != null) {
            System.out.println("[DEBUG] CatalogRowController: Removendo card -> " + entry.getCardName());
            try {
                repository.delete(entry.getCardId());
                if (onDeleteCallback != null) {
                    onDeleteCallback.run();
                }
            } catch (Exception e) {
                System.err.println("[DEBUG] Erro ao deletar: " + e.getMessage());
            }
        }
    }

    private void updateTypeBadge(String type) {
        if (type == null) {
            lblType.setText("???");
            lblType.setStyle("-fx-background-color: #78909C; -fx-text-fill: white; -fx-background-radius: 12; -fx-padding: 3 12; -fx-font-weight: bold; -fx-font-size: 9;");
            return;
        }

        String displayType = type.toUpperCase();
        String color = "#78909C"; // Cinza padrão

        switch (type.toLowerCase()) {
            case "fire": color = "#FF7043"; displayType = "FOGO"; break;
            case "water": color = "#42A5F5"; displayType = "ÁGUA"; break;
            case "lightning": color = "#FBC02D"; displayType = "ELÉTRICO"; break;
            case "grass": color = "#66BB6A"; displayType = "PLANTA"; break;
            case "psychic": color = "#AB47BC"; displayType = "PSÍQUICO"; break;
            case "darkness": color = "#263238"; displayType = "NOTURNO"; break;
            case "dragon": color = "#FB8C00"; displayType = "DRAGÃO"; break;
            case "colorless": color = "#B0BEC5"; displayType = "INCOLOR"; break;
            case "metal": color = "#90A4AE"; displayType = "METAL"; break;
            case "fighting": color = "#B87333"; displayType = "LUTADOR"; break;
        }

        lblType.setText(displayType);
        lblType.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-background-radius: 12; -fx-padding: 3 12; -fx-font-weight: bold; -fx-font-size: 9;");
    }
}
