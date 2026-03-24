package com.pokemontcg.controller;

import com.pokemontcg.model.CatalogEntry;
import com.pokemontcg.repository.CatalogRepository;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

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

    private CatalogRepository repository = new CatalogRepository();

    /**
     * Define os dados da carta no componente.
     */
    public void setCardData(String name, String setId, String imageUrl, String type, String rarity, String seriesId, String seriesName) {
        System.out.println("[DEBUG] CardItemController: Configurando dados para -> " + name);
        if (setId != null && setId.contains(" • ")) {
            this.currentCardId = setId.split(" • ")[0];
        } else {
            this.currentCardId = setId;
        }
        
        this.currentImageUrl = imageUrl;
        this.currentSeriesId = (seriesId != null) ? seriesId : "base";
        this.currentSeriesName = (seriesName != null) ? seriesName : "Expansão"; 
        this.currentType = type;
        this.currentRarity = rarity;
        
        lblName.setText(name);
        lblSetId.setText(setId);
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
            
            repository.save(entry);
            
            System.out.println("[DEBUG] CardItemController: Card salvo no SQLite com sucesso!");
        } catch (Exception e) {
            System.err.println("[DEBUG] CardItemController: Erro ao salvar card: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateTypeBadge(String type) {
        if (type == null) return;
        
        // Definindo cores de fundo baseadas no tipo (estilo Pokémon TCG)
        String bgColor = "#F5F5F5"; // Padrão
        String textColor = "#9E9E9E";
        String displayType = type.toUpperCase();
        
        switch (type.toLowerCase()) {
            case "fire": bgColor = "#FF7043"; textColor = "white"; displayType = "FOGO"; break;
            case "water": bgColor = "#42A5F5"; textColor = "white"; displayType = "ÁGUA"; break;
            case "lightning": bgColor = "#FFEE58"; textColor = "#FBC02D"; displayType = "ELÉTRICO"; break;
            case "grass": bgColor = "#66BB6A"; textColor = "white"; displayType = "PLANTA"; break;
            case "psychic": bgColor = "#AB47BC"; textColor = "white"; displayType = "PSÍQUICO"; break;
            case "darkness": bgColor = "#263238"; textColor = "white"; displayType = "NOTURNO"; break;
            case "dragon": bgColor = "#FB8C00"; textColor = "white"; displayType = "DRAGÃO"; break;
            case "colorless": bgColor = "#F5F5F5"; textColor = "#9E9E9E"; displayType = "INCOLOR"; break;
            case "metal": bgColor = "#B0BEC5"; textColor = "white"; displayType = "METAL"; break;
            case "fighting": bgColor = "#B87333"; textColor = "white"; displayType = "LUTADOR"; break;
        }
        
        lblType.setText(displayType);
        lblType.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: %s; -fx-background-radius: 6; -fx-padding: 3 10; -fx-font-weight: bold; -fx-font-size: 9;", bgColor, textColor));
    }
}
