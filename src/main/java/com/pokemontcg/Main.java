package com.pokemontcg;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;

/**
 * Ponto de entrada principal da aplicação Pokémon TCG Catalog.
 * Comentários explicativos: Esta classe herda de Application (Base do JavaFX).
 * Por enquanto, ela apenas abre uma janela simples para testarmos se o ambiente 
 * está configurado corretamente.
 */
public class Main extends Application {

    // Método que o JavaFX chama para 'dar vida' à janela
    @Override
    public void start(Stage primaryStage) {
        
        // Inicializa o banco de dados antes de carregar a tela
        try {
            com.pokemontcg.repository.DatabaseManager.initDatabase();
        } catch (Exception e) {
            System.err.println("Erro crítico ao carregar banco de dados: " + e.getMessage());
            // Em uma fase mais avançada, mostraremos um Alert aqui
        }

        // Texto simples para vermos na tela
        Label label = new Label("Pokémon TCG Catalog — Banco de Dados Conectado!");
        
        // Container que alinha as coisas na tela
        StackPane root = new StackPane();
        root.getChildren().add(label);
        
        // Define o tamanho da cena/janela
        Scene scene = new Scene(root, 600, 400);
        
        // Configurações finais da janela (Janela Principal)
        primaryStage.setTitle("Pokémon TCG Catalog - Versão 1.0 (Setup)");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // O método main clássico que inicia o JavaFX
    public static void main(String[] args) {
        launch(args);
    }
}
