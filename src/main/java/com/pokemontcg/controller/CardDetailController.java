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

/**
 * Controlador para o modal de detalhes da carta.
 * Exibe as informações completas e permite adicionar à coleção.
 */
public class CardDetailController {

    @FXML private StackPane rootPane;
    @FXML private ImageView imgLarge;
    @FXML private Label lblCardName;
    @FXML private Label lblSetId;
    @FXML private Label lblRarity;
    @FXML private Label lblType;
    @FXML private Label lblSetName;
    @FXML private Label lblHp;
    @FXML private Label lblPrice;
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

    /**
     * Define os dados vindos de uma entrada do catálogo (banco local).
     */
    public void setCardData(CatalogEntry entry) {
        this.entry = entry;
        lblCardName.setText(entry.getCardName());
        lblSetId.setText("SÉRIE: " + entry.getSeriesName() + " • ID: " + entry.getCardId());
        lblRarity.setText(entry.getRarity() != null ? entry.getRarity() : "Desconhecida");
        lblSetName.setText(entry.getSeriesName());
        lblHp.setText("---");
        lblPrice.setText("0.00");
        
        updateTypeBadge(entry.getType());

        if (entry.getImageUrl() != null) {
            loadImage(entry.getImageUrl());
        }
    }

    /**
     * Define os dados vindos diretamente da API (objeto Card completo).
     */
    public void setCardData(Card card) {
        if (card == null) return;
        
        lblCardName.setText(card.getName());
        lblSetId.setText("SÉRIE: " + card.getSeriesName() + " • ID: " + card.getLocalId());
        lblRarity.setText(card.getRarity() != null ? card.getRarity() : "Desconhecida");
        lblSetName.setText(card.getSeriesName());
        lblHp.setText(card.getHp() != null && card.getHp() > 0 ? String.valueOf(card.getHp()) : "---");
        lblPrice.setText("0.00"); 
        
        updateTypeBadge(card.getTypes() != null && !card.getTypes().isEmpty() ? card.getTypes().get(0) : null);

        if (card.getImage() != null) {
            loadImage(card.getImage());
        }
        
        // Prepara objeto para salvamento
        this.entry = new CatalogEntry();
        entry.setCardId(card.getId());
        entry.setCardName(card.getName());
        entry.setImageUrl(card.getImage());
        entry.setSeriesId(card.getSeriesId() != null ? card.getSeriesId() : "base");
        entry.setSeriesName(card.getSeriesName() != null ? card.getSeriesName() : "Expansão");
        entry.setType(card.getTypes() != null && !card.getTypes().isEmpty() ? card.getTypes().get(0) : "Colorless");
        entry.setRarity(card.getRarity() != null ? card.getRarity() : "Common");
        entry.setQuantity(1);
    }

    private void loadImage(String url) {
        Thread t = new Thread(() -> {
            try {
                Image img = com.pokemontcg.api.PersistentImageCache.getImage(url, 340, 480);
                Platform.runLater(() -> {
                    if (img != null) imgLarge.setImage(img);
                });
            } catch (Exception e) {
                System.err.println("[DEBUG] Erro ao carregar imagem: " + e.getMessage());
            }
        });
        t.setDaemon(true); // Não impede a JVM de fechar
        t.start();
    }

    private void updateTypeBadge(String type) {
        if (type == null) {
            lblType.setText("???");
            lblType.setStyle("-fx-background-color: #78909C; -fx-text-fill: white; -fx-background-radius: 12; -fx-padding: 3 12; -fx-font-weight: bold;");
            return;
        }
        
        String displayType = type.toUpperCase();
        String color = "#78909C"; // Default

        switch (type.toLowerCase()) {
            case "fire": case "fogo": color = "#FF7043"; break;
            case "water": case "água": color = "#42A5F5"; break;
            case "lightning": case "elétrico": color = "#FBC02D"; break;
            case "grass": case "planta": color = "#66BB6A"; break;
            case "psychic": case "psíquico": color = "#AB47BC"; break;
            case "darkness": case "sombrio": case "noturno": color = "#263238"; break;
            case "dragon": case "dragão": color = "#FB8C00"; break;
            case "metal": color = "#90A4AE"; break;
            case "fighting": case "lutador": color = "#A1887F"; break;
            case "fairy": case "fada": color = "#F48FB1"; break;
            case "colorless": case "incolor": color = "#78909C"; break;
        }

        lblType.setText(displayType);
        lblType.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-background-radius: 12; -fx-padding: 3 12; -fx-font-weight: bold;");
    }

    @FXML
    public void handlePrimaryAction() {
        if (entry != null) {
            System.out.println("[DEBUG] CardDetailController: Iniciando salvamento -> " + entry.getCardName() + " (Qtd: " + quantity + ")");
            try {
                // Atualiza a quantidade e metadados no objeto antes de salvar
                entry.setQuantity(quantity);
                if (entry.getStage() == null) entry.setStage("Básico");
                if (entry.getCategory() == null) entry.setCategory("Pokémon");
                if (entry.getSeriesId() == null) entry.setSeriesId("base");
                if (entry.getSeriesName() == null) entry.setSeriesName("Expansão");
                
                catalogService.saveEntry(entry);
                System.out.println("[DEBUG] CardDetailController: Sucesso ao chamar saveEntry!");
                handleClose();
            } catch (Exception e) {
                System.err.println("[DEBUG] CardDetailController: Erro ao salvar card: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.err.println("[DEBUG] CardDetailController: Erro - entry está NULL no momento do salvamento!");
        }
    }

    /**
     * Alterna a visualização para os controles de quantidade e confirmação.
     */
    @FXML
    public void handleShowAddOptions() {
        btnAction.setVisible(false);
        addOptionsArea.setVisible(true);
        quantity = 1;
        lblQuantity.setText("1");
    }

    /**
     * Incrementa a quantidade.
     */
    @FXML
    public void handleIncrement() {
        quantity++;
        lblQuantity.setText("" + quantity);
    }

    /**
     * Decrementa a quantidade (limite mínimo de 1).
     */
    @FXML
    public void handleDecrement() {
        if (quantity > 1) {
            quantity--;
            lblQuantity.setText("" + quantity);
        }
    }

    /**
     * Cancela a operação de adição e volta ao botão principal.
     */
    @FXML
    public void handleCancelAdd() {
        addOptionsArea.setVisible(false);
        btnAction.setVisible(true);
    }

    @FXML
    public void handleClose() {
        if (onCloseCallback != null) {
            onCloseCallback.run();
        }
    }
}
