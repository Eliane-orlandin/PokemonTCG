package com.pokemontcg.controller;

import com.pokemontcg.model.CatalogEntry;
import com.pokemontcg.model.Card;
import com.pokemontcg.service.CatalogService;
import com.pokemontcg.service.CardService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.application.Platform;
import java.io.IOException;

/**
 * Controlador para o componente individual de cada carta (Card Item).
 */
public class CardItemController {

    @FXML private VBox cardRoot;
    @FXML private StackPane imageContainer;
    @FXML private ImageView cardImage;
    @FXML private Label lblSetId;
    @FXML private Label lblName;
    @FXML private Label lblStage;
    @FXML private HBox typesContainer;
    @FXML private Label lblRarity;
    @FXML private Label lblQuantity;

    private int quantity = 1;

    // Dados temporários para salvamento
    private String currentCardId;
    private String currentSeriesId;
    private String currentSeriesName;
    private String currentImageUrl;
    private String currentType;
    private String currentRarity;

    private CatalogService catalogService = new CatalogService();
    private CardService cardService = new CardService();

    /**
     * Define os dados da carta no componente.
     */
    public void setCardData(String name, String cardId, String displaySetId, String imageUrl, String type, String rarity, String seriesId, String seriesName, String stage) {
        System.out.println("[DEBUG] CardItemController: Configurando dados para -> " + name);
        this.currentCardId = cardId;
        this.currentImageUrl = imageUrl;
        this.currentSeriesId = (seriesId != null) ? seriesId : "base";
        this.currentSeriesName = (seriesName != null) ? seriesName : "Expansão"; 
        this.currentType = type;
        this.currentRarity = rarity;

        lblSetId.setText(displaySetId);
        lblName.setText(name);
        
        // Remove fallbacks para não exibir "Básico" ou "Comum" se não houver certeza
        lblRarity.setText(rarity != null && !rarity.equalsIgnoreCase("null") ? rarity : "");
        lblStage.setText(stage != null && !stage.equalsIgnoreCase("null") ? stage : "");
        
        this.quantity = 1;
        lblQuantity.setText("1");
        
        // Limpa tipos anteriores e adiciona o inicial (se existir)
        typesContainer.getChildren().clear();
        if (type != null && !type.equalsIgnoreCase("N/A") && !type.isEmpty()) {
            addTypeBadge(type);
        }
        
        // Carrega imagem de forma assíncrona
        if (imageUrl != null && !imageUrl.isEmpty()) {
            new Thread(() -> {
                Image img = new Image(imageUrl, 180, 250, true, true);
                Platform.runLater(() -> cardImage.setImage(img));
            }).start();
        }

        // Se dados essenciais estão faltando (comum no resumo da API), busca detalhes em background
        if (stage == null || stage.isEmpty() || rarity == null || rarity.isEmpty() || type == null || type.equalsIgnoreCase("N/A")) {
            fetchFullDetails();
        }
    }

    /**
     * Busca detalhes completos do card quando a busca inicial é resumida.
     */
    private void fetchFullDetails() {
        new Thread(() -> {
            try {
                Card fullCard = cardService.getCardDetails(currentCardId);
                if (fullCard != null) {
                    Platform.runLater(() -> {
                        // Atualiza Rarity e Stage sem placeholders genéricos
                        if (fullCard.getRarity() != null) {
                            lblRarity.setText(fullCard.getRarity());
                            this.currentRarity = fullCard.getRarity();
                        }
                        if (fullCard.getStage() != null) {
                            lblStage.setText(fullCard.getStage());
                        }
                        
                        // Atualiza todos os tipos
                        typesContainer.getChildren().clear();
                        if (fullCard.getTypes() != null) {
                            for (String t : fullCard.getTypes()) {
                                addTypeBadge(t);
                            }
                            if (!fullCard.getTypes().isEmpty()) {
                                this.currentType = fullCard.getTypes().get(0);
                            }
                        }
                    });
                }
            } catch (Exception e) {
                System.err.println("[DEBUG] Erro detalhes auto: " + e.getMessage());
            }
        }).start();
    }

    private void addTypeBadge(String type) {
        Label badge = new Label(type.toUpperCase());
        badge.getStyleClass().add("type-badge-pill");
        
        String cleanType = type.toLowerCase().trim();
        String styleClass = "type-colorless"; 
        
        switch (cleanType) {
            case "fire": case "fogo": styleClass = "type-fire"; break;
            case "water": case "água": styleClass = "type-water"; break;
            case "lightning": case "elétrico": styleClass = "type-lightning"; break;
            case "grass": case "planta": styleClass = "type-grass"; break;
            case "psychic": case "psíquico": styleClass = "type-psychic"; break;
            case "darkness": case "noturno": styleClass = "type-darkness"; break;
            case "dragon": case "dragão": styleClass = "type-dragon"; break;
            case "metal": styleClass = "type-metal"; break;
            case "fighting": case "lutador": styleClass = "type-fighting"; break;
            case "fairy": case "fada": styleClass = "type-fairy"; break;
        }
        
        badge.getStyleClass().add(styleClass);
        typesContainer.getChildren().add(badge);
    }

    @FXML
    public void handleShowDetail() {
        cardRoot.setOpacity(0.5);
        new Thread(() -> {
            try {
                Card fullCard = cardService.getCardDetails(currentCardId);
                Platform.runLater(() -> {
                    try {
                        cardRoot.setOpacity(1.0);
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/card_detail_modal.fxml"));
                        StackPane modal = loader.load();
                        CardDetailController controller = loader.getController();
                        
                        if (fullCard != null) {
                            controller.setCardData(fullCard);
                        } else {
                            CatalogEntry entry = new CatalogEntry();
                            entry.setCardId(currentCardId);
                            entry.setCardName(lblName.getText());
                            entry.setImageUrl(currentImageUrl);
                            entry.setSeriesId(currentSeriesId);
                            entry.setSeriesName(currentSeriesName);
                            entry.setType(currentType);
                            entry.setRarity(currentRarity);
                            controller.setCardData(entry);
                        }
                        
                        StackPane contentArea = (StackPane) cardRoot.getScene().lookup("#contentArea");
                        if (contentArea != null) {
                            contentArea.getChildren().add(modal);
                            controller.setOnCloseCallback(() -> contentArea.getChildren().remove(modal));
                        }
                    } catch (IOException e) {
                        System.err.println("[DEBUG] Erro ao carregar modal: " + e.getMessage());
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> cardRoot.setOpacity(1.0));
                System.err.println("[DEBUG] Erro ao buscar detalhes: " + e.getMessage());
            }
        }).start();
    }

    @FXML
    public void handleAddToCollection() {
        try {
            CatalogEntry entry = new CatalogEntry();
            entry.setCardId(currentCardId);
            entry.setCardName(lblName.getText());
            entry.setImageUrl(currentImageUrl);
            entry.setSeriesId(currentSeriesId);
            entry.setSeriesName(currentSeriesName);
            entry.setType(currentType);
            entry.setRarity(currentRarity);
            entry.setQuantity(quantity);
            
            catalogService.saveEntry(entry);
            
            // Feedback visual
            String originalText = lblQuantity.getText();
            lblQuantity.setText("✓");
            new Thread(() -> {
                try { Thread.sleep(2000); } catch (InterruptedException ex) {}
                Platform.runLater(() -> lblQuantity.setText(String.valueOf(quantity)));
            }).start();
        } catch (Exception e) {
            System.err.println("[DEBUG] Erro ao salvar card: " + e.getMessage());
        }
    }

    @FXML
    public void handleIncrement() {
        quantity++;
        lblQuantity.setText(String.valueOf(quantity));
    }

    @FXML
    public void handleDecrement() {
        if (quantity > 1) {
            quantity--;
            lblQuantity.setText(String.valueOf(quantity));
        }
    }
}
