package com.pokemontcg.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Controlador principal da janela (Main Frame).
 * Orquestra a navegação entre as telas de Dashboard (Home), Busca, 
 * Coleção Pessoal e Exportação.
 */
public class MainController {

    @FXML private StackPane contentArea;
    
    // Botões da Sidebar para controle de estado visual
    @FXML private Button btnHome;
    @FXML private Button btnSearch;
    @FXML private Button btnCatalog;
    @FXML private Button btnExport;

    private List<Button> menuButtons;

    /**
     * Inicializa a aplicação carregando a Dashboard (Home).
     */
    @FXML
    public void initialize() {
        menuButtons = Arrays.asList(btnHome, btnSearch, btnCatalog, btnExport);
        handleNavigateHome();
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
        System.out.println("[DEBUG] MainController: Navegando para CATALOGO");
        loadView("/fxml/catalog.fxml", btnCatalog);
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
