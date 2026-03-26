package com.pokemontcg.controller;

import com.pokemontcg.model.CatalogEntry;
import com.pokemontcg.service.CatalogService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Controlador para uma linha individual da tabela de catálogo.
 */
public class CatalogRowController {

    @FXML private ImageView imgMini;
    @FXML private Label lblName;
    @FXML private Label lblSetId;
    @FXML private Label lblStage;
    @FXML private Label lblType;
    @FXML private Label lblRarity;
    @FXML private Label lblQty;
    
    private CatalogEntry entry;
    private Runnable onDeleteCallback;
    private CatalogService catalogService;

    public void setService(CatalogService service) {
        this.catalogService = service;
    }

    public void setOnDeleteCallback(Runnable callback) {
        this.onDeleteCallback = callback;
    }

    public void setRowData(CatalogEntry entry) {
        if (entry == null) return;
        this.entry = entry;
        
        // Proteção contra campos nulos
        String name = entry.getCardName() != null ? entry.getCardName() : "Card sem nome";
        lblName.setText(name);
        
        String seriesName = entry.getSeriesName() != null ? entry.getSeriesName() : "Desconhecida";
        String cardId = entry.getCardId() != null ? entry.getCardId() : "???";
        lblSetId.setText(seriesName + " • " + cardId);
        
        lblStage.setText(entry.getStage() != null ? entry.getStage() : "Básico");
        lblQty.setText(String.format("%02d", entry.getQuantity()));
        lblRarity.setText(entry.getRarity() != null ? entry.getRarity() : "Comum");
        
        // Carregar imagem miniatura em background
        if (entry.getImageUrl() != null && !entry.getImageUrl().isEmpty()) {
            final String url = entry.getImageUrl();
            Thread t = new Thread(() -> {
                try {
                    Image img = com.pokemontcg.api.PersistentImageCache.getImage(url, 40, 50);
                    javafx.application.Platform.runLater(() -> {
                        if (img != null && imgMini != null) imgMini.setImage(img);
                    });
                } catch (Exception e) {
                    // Log silencioso para falhas de imagem
                }
            });
            t.setDaemon(true); // Não impede a JVM de fechar
            t.start();
        }
        
        updateTypeStyling(entry.getType());
    }

    @FXML
    public void handleDelete(javafx.scene.input.MouseEvent event) {
        if (event != null) event.consume(); 
        if (entry != null && entry.getCardId() != null && catalogService != null) {
            try {
                catalogService.removeCardFromCatalog(entry.getCardId());
                if (onDeleteCallback != null) {
                    onDeleteCallback.run();
                }
            } catch (Exception e) {
                System.err.println("[DEBUG] Erro ao deletar: " + e.getMessage());
            }
        }
    }

    @FXML
    public void handleShowDetail(javafx.scene.input.MouseEvent event) {
        if (entry != null) {
            MainController.getInstance().showCardDetail(entry);
        }
    }

    private void updateTypeStyling(String type) {
        if (type == null) {
            lblType.setText("???");
            lblType.getStyleClass().setAll("type-badge-pill", "type-colorless");
            return;
        }

        String cleanType = type.toLowerCase().trim();
        lblType.setText(type.toUpperCase());
        lblType.getStyleClass().clear();
        lblType.getStyleClass().add("type-badge-pill");
        
        String styleClass = "type-colorless";
        switch (cleanType) {
            case "fire": case "fogo": styleClass = "type-fire"; break;
            case "water": case "água": styleClass = "type-water"; break;
            case "lightning": case "elétrico": styleClass = "type-lightning"; break;
            case "grass": case "planta": styleClass = "type-grass"; break;
            case "psychic": case "psíquico": styleClass = "type-psychic"; break;
            case "darkness": case "noturno": case "sombrio": styleClass = "type-darkness"; break;
            case "dragon": case "dragão": styleClass = "type-dragon"; break;
            case "metal": styleClass = "type-metal"; break;
            case "fighting": case "lutador": styleClass = "type-fighting"; break;
            case "fairy": case "fada": styleClass = "type-fairy"; break;
        }
        lblType.getStyleClass().add(styleClass);
        // Remove style inline para usar classes do CSS
        lblType.setStyle(null);
    }
}
