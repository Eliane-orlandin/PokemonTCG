package com.pokemontcg.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * Controlador principal da janela (Main Frame).
 * Orquestra a navegação entre as telas de Dashboard (Home), Busca, 
 * Coleção Pessoal e Exportação.
 */
public class MainController {

    @FXML private StackPane contentArea;
    private static MainController instance;

    public static MainController getInstance() {
        return instance;
    }

    
    // Botões da Sidebar para controle de estado visual
    @FXML private Button btnHome;
    @FXML private Button btnSearch;
    @FXML private Button btnCatalog;
    @FXML private Button btnExport;
    @FXML private MediaView mediaViewLogo;

    private List<Button> menuButtons;

    /**
     * Inicializa a aplicação carregando a Dashboard (Home).
     */
    @FXML
    public void initialize() {
        instance = this;
        menuButtons = Arrays.asList(btnHome, btnSearch, btnCatalog, btnExport);
        handleNavigateHome();
        setupLogoAnimation();
    }

    private void setupLogoAnimation() {
        try {
            URL resource = getClass().getResource("/images/icone-animado.mp4");
            if (resource != null) {
                Media media = new Media(resource.toExternalForm());
                MediaPlayer mediaPlayer = new MediaPlayer(media);
                mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                mediaPlayer.setMute(true); // Remove o som da animação
                mediaPlayer.play();
                mediaViewLogo.setMediaPlayer(mediaPlayer);
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar animação do logo: " + e.getMessage());
        }
    }

    /**
     * Carrega o Dashboard / Home.
     */
    @FXML
    public void handleNavigateHome() {
        System.out.println("[DEBUG] MainController: Navegando para HOME");
        loadView("/fxml/home.fxml", btnHome);
    }

    @FXML
    public void handleNavigateSearch() {
        System.out.println("[DEBUG] MainController: Navegando para BUSCA");
        loadView("/fxml/search.fxml", btnSearch);
    }

    @FXML
    public void handleNavigateCatalog() {
        navigateCatalog(null);
    }

    /**
     * Navega para o catálogo com um filtro opcional de raridade.
     */
    public void navigateCatalog(String rarityFilter) {
        System.out.println("[DEBUG] MainController: Navegando para CATALOGO | Filtro: " + rarityFilter);
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/catalog.fxml"));
            Node view = loader.load();
            
            // Injeta o filtro no controlador se fornecido
            if (rarityFilter != null) {
                CatalogController controller = loader.getController();
                controller.setInitialRarityFilter(rarityFilter);
            }
            
            contentArea.getChildren().setAll(view);
            updateActiveButton(btnCatalog);
            
        } catch (IOException e) {
            System.err.println("Erro ao navegar para Catálogo");
            e.printStackTrace();
        }
    }

    @FXML
    public void handleNavigateExport() {
        System.out.println("[DEBUG] MainController: Navegando para EXPORTACAO");
        loadView("/fxml/export.fxml", btnExport);
    }

    /**
     * Carrega o FXML e atualiza o estilo do botão selecionado.
     */
    private void loadView(String fxmlPath, Button activeButton) {
        System.out.println("[DEBUG] MainController: Carregando FXML -> " + fxmlPath);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node view = loader.load();
            
            contentArea.getChildren().setAll(view);
            updateActiveButton(activeButton);
            
            System.out.println("[DEBUG] MainController: FXML carregado com sucesso!");
            
        } catch (IOException e) {
            System.err.println("Erro ao carregar FXML: " + fxmlPath);
            e.printStackTrace();
        }
    }

    /**
     * Exibe o modal de detalhes de uma carta (pode vir da busca ou do catálogo).
     */
    public void showCardDetail(com.pokemontcg.model.CatalogEntry entry) {
        if (entry == null || contentArea == null) return;
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/card_detail_modal.fxml"));
            StackPane modal = loader.load();
            CardDetailController controller = loader.getController();
            
            // Tenta obter dados completos da API em background
            Thread t = new Thread(() -> {
                try {
                    com.pokemontcg.service.CardService cardService = new com.pokemontcg.service.CardService();
                    com.pokemontcg.model.Card fullCard = cardService.getCardDetails(entry.getCardId());
                    javafx.application.Platform.runLater(() -> {
                        if (fullCard != null) {
                            controller.setCardData(fullCard);
                        } else {
                            controller.setCardData(entry);
                        }
                    });
                } catch (Exception e) {
                    javafx.application.Platform.runLater(() -> controller.setCardData(entry));
                }
            });
            t.setDaemon(true); // Não impede a JVM de fechar
            t.start();
            
            contentArea.getChildren().add(modal);
            controller.setOnCloseCallback(() -> contentArea.getChildren().remove(modal));
            
        } catch (IOException e) {
            System.err.println("[DEBUG] Erro ao carregar modal de detalhes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Remove o estilo 'active' de todos e adiciona apenas no botão clicado.
     */
    private void updateActiveButton(Button active) {
        if (menuButtons == null || active == null) return;
        
        for (Button btn : menuButtons) {
            btn.getStyleClass().remove("menu-button-active");
            if (btn == active) {
                btn.getStyleClass().add("menu-button-active");
            }
        }
    }
}
