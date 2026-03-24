package com.pokemontcg.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import java.io.IOException;

/**
 * Controlador principal da janela (Main Frame).
 * Comentários explicativos: Esta classe orquestra a navegação entre as 
 * sub-telas (Busca, Catálogo, Exportação) dentro da área central (StackPane).
 */
public class MainController {

    // O StackPane central definido no main.fxml
    @FXML
    private StackPane contentArea;

    /**
     * Chamada automaticamente quando o FXML é carregado. 
     * Vamos iniciar mostrando a tela de busca por padrão.
     */
    @FXML
    public void initialize() {
        showSearch();
    }

    /**
     * Carrega a tela de busca na área central.
     */
    @FXML
    public void showSearch() {
        loadView("/fxml/search.fxml");
    }

    /**
     * Carrega a tela do catálogo pessoal.
     */
    @FXML
    public void showCatalog() {
        loadView("/fxml/catalog.fxml");
    }

    /**
     * Carrega a tela de opções de exportação.
     */
    @FXML
    public void showExport() {
        loadView("/fxml/export.fxml");
    }

    /**
     * Método auxiliar genérico para carregar qualquer arquivo FXML 
     * e injetá-lo na área de conteúdo central.
     */
    private void loadView(String fxmlPath) {
        try {
            // Carregamos o arquivo FXML da pasta de recursos
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node view = loader.load();
            
            // Limpamos o que estava antes e mostramos a nova tela
            contentArea.getChildren().setAll(view);
            
            System.out.println("[Menu] Tela carregada com sucesso: " + fxmlPath);
            
        } catch (IOException e) {
            System.err.println("Erro crítico: Não foi possível carregar a tela " + fxmlPath);
            e.printStackTrace();
            // Aqui poderíamos mostrar um aviso visual para o usuário
        }
    }
}
