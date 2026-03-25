package com.pokemontcg.controller;

import com.pokemontcg.model.CatalogEntry;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import com.pokemontcg.service.CatalogService;

/**
 * Controlador para o componente individual de cada carta (Card Item).
 */
public class CardItemController {

    @FXML private VBox cardRoot;
    @FXML private StackPane imageContainer;
    @FXML private ImageView cardImage;
    @FXML private Label lblSetId;
    @FXML private Label lblName;
    @FXML private Label lblType;
    @FXML private Label lblRarity;

    // Dados temporários para salvamento
    private String currentCardId;
    private String currentSeriesId;
    private String currentSeriesName;
    private String currentImageUrl;
    private String currentType;
    private String currentRarity;

    private CatalogService catalogService = new CatalogService();

    /**
     * Define os dados da carta no componente.
     */
    public void setCardData(String name, String cardId, String displaySetId, String imageUrl, String type, String rarity, String seriesId, String seriesName) {
        System.out.println("[DEBUG] CardItemController: Configurando dados para -> " + name);
        this.currentCardId = cardId;
        lblSetId.setText(displaySetId);
        
        this.currentImageUrl = imageUrl;
        this.currentSeriesId = (seriesId != null) ? seriesId : "base";
        this.currentSeriesName = (seriesName != null) ? seriesName : "Expansão"; 
        this.currentType = type;
        this.currentRarity = rarity;
        
        lblName.setText(name);
        lblRarity.setText(rarity);
        
        updateTypeBadge(type);
        
        if (imageUrl != null && !imageUrl.isEmpty()) {
            new Thread(() -> {
                Image img = new Image(imageUrl, 180, 250, true, true);
                javafx.application.Platform.runLater(() -> cardImage.setImage(img));
            }).start();
        }
    }

    @FXML
    public void handleAddToCollection() {
        System.out.println("[DEBUG] CardItemController: Adicionando à coleção -> " + lblName.getText());
        try {
            CatalogEntry entry = new CatalogEntry();
            entry.setCardId(currentCardId);
            entry.setCardName(lblName.getText());
            entry.setImageUrl(currentImageUrl);
            entry.setSeriesId((currentSeriesId == null || currentSeriesId.isEmpty()) ? "base" : currentSeriesId);
            entry.setSeriesName((currentSeriesName == null || currentSeriesName.isEmpty()) ? "Expansão" : currentSeriesName);
            entry.setType(currentType);
            entry.setRarity(currentRarity);
            entry.setQuantity(1);
            
            catalogService.saveEntry(entry);
            
            System.out.println("[DEBUG] CardItemController: Card salvo no SQLite com sucesso!");
        } catch (Exception e) {
            System.err.println("[DEBUG] CardItemController: Erro ao salvar card: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateTypeBadge(String type) {
        if (type == null) return;
        
        // Limpa classes de tipo anteriores para evitar conflitos
        lblType.getStyleClass().removeAll(
            "type-fire", "type-water", "type-grass", "type-lightning", 
            "type-psychic", "type-fighting", "type-darkness", "type-metal", 
            "type-fairy", "type-dragon", "type-colorless"
        );
        
        String cleanType = type.toLowerCase().trim();
        String styleClass = "type-colorless"; // Default
        String displayType = type.toUpperCase();
        
        switch (cleanType) {
            case "fire": styleClass = "type-fire"; displayType = "FOGO"; break;
            case "water": styleClass = "type-water"; displayType = "ÁGUA"; break;
            case "lightning": styleClass = "type-lightning"; displayType = "ELÉTRICO"; break;
            case "grass": styleClass = "type-grass"; displayType = "PLANTA"; break;
            case "psychic": styleClass = "type-psychic"; displayType = "PSÍQUICO"; break;
            case "darkness": styleClass = "type-darkness"; displayType = "NOTURNO"; break;
            case "dragon": styleClass = "type-dragon"; displayType = "DRAGÃO"; break;
            case "metal": styleClass = "type-metal"; displayType = "METAL"; break;
            case "fighting": styleClass = "type-fighting"; displayType = "LUTADOR"; break;
            case "fairy": styleClass = "type-fairy"; displayType = "FADA"; break;
        }
        
        lblType.setText(displayType);
        lblType.getStyleClass().add(styleClass);
        // O padding e radius agora vêm da classe base .type-badge-pill no FXML ou CSS
    }
}
