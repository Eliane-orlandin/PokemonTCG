package com.pokemontcg.controller;

import com.pokemontcg.model.CatalogEntry;
import com.pokemontcg.model.Card;
import com.pokemontcg.service.CatalogService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;

/**
 * Controlador para o modal de detalhes da carta.
 * Versão restaurada para estabilidade.
 */
public class CardDetailController {

    @FXML private StackPane rootPane;
    @FXML private ImageView imgLarge;
    @FXML private Label lblCardName;
    @FXML private Label lblSetId;
    @FXML private Label lblStage;
    @FXML private Label lblHp;
    @FXML private Label lblWeakness;
    @FXML private Label lblResistance;
    @FXML private Label lblRetreat;
    @FXML private Label lblTypePill;
    @FXML private Label lblFlavorText;
    @FXML private VBox attacksContainer;
    
    // Rótulos estáticos para controle dinâmico
    @FXML private Label lblStageLabel;
    @FXML private Label lblHpLabel;
    @FXML private Label lblWeaknessLabel;
    @FXML private Label lblResistanceLabel;
    @FXML private Label lblRetreatLabel;
    
    @FXML private Button btnAction;
    @FXML private StackPane actionArea;
    @FXML private HBox addOptionsArea;
    @FXML private Label lblQuantity;

    private CatalogEntry entry;
    private int quantity = 1;
    private final CatalogService catalogService = new CatalogService();
    private Runnable onCloseCallback;

    public void setOnCloseCallback(Runnable callback) {
        this.onCloseCallback = callback;
    }

    public void setCardData(CatalogEntry entry) {
        this.entry = entry;
        lblCardName.setText(entry.getCardName());
        lblSetId.setText("ID: " + entry.getLocalId()); 
        lblStage.setText(entry.getStage() != null ? entry.getStage() : "Básico");
        lblHp.setText("---");
        lblWeakness.setText("---");
        lblResistance.setText("---");
        lblRetreat.setText("---");
        lblFlavorText.setText("");
        
        updateTypeBadge(entry.getType());

        if (entry.getImageUrl() != null) {
            loadImage(entry.getImageUrl());
        }

        applyCategoryLayout(entry.getCategory(), entry.getRarity(), entry.getStage());
    }

    public void setCardData(Card card) {
        if (card == null) return;
        
        lblCardName.setText(card.getName());
        lblSetId.setText("ID: " + card.getLocalId()); 
        lblStage.setText(card.getStage() != null ? card.getStage() : "Básico");
        lblHp.setText(card.getHp() != null && card.getHp() > 0 ? String.valueOf(card.getHp()) : "---");
        lblWeakness.setText(card.getWeakness() != null ? card.getWeakness() : "---");
        lblResistance.setText(card.getResistance() != null ? card.getResistance() : "---");
        lblRetreat.setText(card.getRetreatCost() != null ? card.getRetreatCost() : "---");
        
        if (card.getFlavorText() != null) {
            lblFlavorText.setText(card.getFlavorText());
        }

        updateTypeBadge(card.getTypes() != null && !card.getTypes().isEmpty() ? card.getTypes().get(0) : "Colorless");

        if (card.getImage() != null) {
            loadImage(card.getImage());
        }

        renderAttacks(card);
        updateCatalogFields(card);
        applyCategoryLayout(card.getCategory(), card.getRarity(), card.getStage());
    }

    private void applyCategoryLayout(String category, String rarity, String stage) {
        boolean isPokemon = category == null || category.equalsIgnoreCase("Pokémon") || category.equalsIgnoreCase("Pokemon");

        if (isPokemon) {
            // Layout Padrão: Pokémon
            lblStageLabel.setText("ESTÁGIO");
            lblHpLabel.setText("HP");
            lblWeaknessLabel.setText("FRAQUEZA");
            
            lblHpLabel.setVisible(true);
            lblHp.setVisible(true);
            lblWeaknessLabel.setVisible(true);
            lblWeakness.setVisible(true);
            lblResistanceLabel.setVisible(true);
            lblResistance.setVisible(true);
            lblRetreatLabel.setVisible(true);
            lblRetreat.setVisible(true);
        } else {
            // Layout Especial: Treinador / Energia
            lblStageLabel.setText("TIPO");
            lblStage.setText(stage != null ? stage : "---");
            
            lblHpLabel.setText("CATEGORIA");
            lblHp.setText(category != null ? category : "---");
            lblHp.getStyleClass().remove("data-value-hp"); // Remove cor vermelha de HP
            lblHp.getStyleClass().add("data-value");
            
            lblWeaknessLabel.setText("RARIDADE");
            lblWeakness.setText(rarity != null ? rarity : "Comum");

            // Esconder atributos de batalha irrelevantes
            lblResistanceLabel.setVisible(false);
            lblResistance.setVisible(false);
            lblRetreatLabel.setVisible(false);
            lblRetreat.setVisible(false);
        }
    }

    private void updateCatalogFields(Card card) {
        if (entry == null) {
            entry = new CatalogEntry();
        }
        entry.setCardId(card.getId());
        entry.setCardName(card.getName());
        entry.setImageUrl(card.getImage());
        entry.setLocalId(card.getLocalId());
        entry.setSeriesId(card.getSeriesId() != null ? card.getSeriesId() : "base");
        entry.setSeriesName(card.getSeriesName() != null ? card.getSeriesName() : "Expansão");
        entry.setType(card.getTypes() != null && !card.getTypes().isEmpty() ? card.getTypes().get(0) : "Colorless");
        entry.setRarity(card.getRarity() != null ? card.getRarity() : "Common");
        entry.setCategory(card.getCategory());
        entry.setStage(card.getStage());
        entry.setQuantity(1);
    }

    private void renderAttacks(Card card) {
        attacksContainer.getChildren().clear();
        attacksContainer.setSpacing(10);
        
        if (card.getAbilities() != null && !card.getAbilities().isEmpty()) {
            for (Card.Ability ability : card.getAbilities()) {
                VBox box = new VBox(2);
                Label name = new Label("★ " + ability.getName());
                name.setStyle("-fx-text-fill: #FF5252; -fx-font-weight: bold; -fx-font-size: 13;");
                name.setWrapText(true);

                Label desc = new Label(ability.getDescription());
                desc.setStyle("-fx-text-fill: #CCC; -fx-font-size: 11;");
                desc.setWrapText(true);
                
                box.getChildren().addAll(name, desc);
                attacksContainer.getChildren().add(box);
            }
        }
        
        if (card.getAttacks() != null && !card.getAttacks().isEmpty()) {
            for (Card.Attack attack : card.getAttacks()) {
                VBox box = new VBox(2);
                HBox header = new HBox(10);
                Label name = new Label("• " + attack.getName());
                name.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13;");
                
                Region spacer = new Region();
                HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
                
                Label damage = new Label(attack.getDamage());
                damage.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14;");
                
                header.getChildren().addAll(name, spacer, damage);
                
                Label desc = new Label(attack.getDescription());
                desc.setStyle("-fx-text-fill: #999; -fx-font-size: 11;");
                desc.setWrapText(true);
                
                box.getChildren().addAll(header, desc);
                attacksContainer.getChildren().add(box);
            }
        }
    }

    private void loadImage(String url) {
        Thread t = new Thread(() -> {
            try {
                Image img = com.pokemontcg.api.PersistentImageCache.getImage(url, 340, 480);
                Platform.runLater(() -> {
                    if (img != null) imgLarge.setImage(img);
                });
            } catch (Exception e) {}
        });
        t.setDaemon(true);
        t.start();
    }

    private void updateTypeBadge(String type) {
        if (type == null) return;
        lblTypePill.setText(type.toUpperCase());
        lblTypePill.getStyleClass().removeAll("type-fire", "type-water", "type-grass", "type-electric", "type-psychic", "type-fighting", "type-darkness", "type-metal", "type-fairy", "type-dragon", "type-colorless");
        lblTypePill.getStyleClass().add("type-" + type.toLowerCase().replace(" ", ""));
    }

    @FXML
    public void handleCancelAdd() {
        btnAction.setVisible(true);
        addOptionsArea.setVisible(false);
    }

    @FXML
    public void handleShowAddOptions() {
        btnAction.setVisible(false);
        addOptionsArea.setVisible(true);
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

    @FXML
    public void handlePrimaryAction() {
        if (entry == null) return;
        entry.setQuantity(quantity);
        try {
            catalogService.saveEntry(entry);
            handleClose();
        } catch (Exception e) {}
    }

    @FXML
    public void handleClose() {
        if (onCloseCallback != null) {
            onCloseCallback.run();
        }
    }
}
