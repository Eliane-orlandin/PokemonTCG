package com.pokemontcg;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Ponto de entrada oficial da aplicação Pokémon TCG Catalog (Versão Visual).
 * Comentários explicativos: Esta classe agora carrega o arquivo FXML principal 
 * e aplica o conjunto de estilos CSS ao aplicativo.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        
        System.out.println("[Debug] 1. Inicializando Banco de Dados...");
        try {
            com.pokemontcg.repository.DatabaseManager.initDatabase();
            System.out.println("[Debug] ✓ Banco de dados inicializado.");
        } catch (Exception e) {
            System.err.println("Erro crítico no banco de dados: " + e.getMessage());
            e.printStackTrace();
        }

        try {
            System.out.println("[Debug] 2. Carregando Fontes Customizadas...");
            Font.loadFont(getClass().getResourceAsStream("/fonts/Inter-Regular.ttf"), 12);
            Font.loadFont(getClass().getResourceAsStream("/fonts/Inter-Bold.ttf"), 12);
            Font.loadFont(getClass().getResourceAsStream("/fonts/PlusJakartaSans-Regular.ttf"), 12);
            Font.loadFont(getClass().getResourceAsStream("/fonts/PokemonHollow.ttf"), 12);
            System.out.println("[Debug] ✓ Fontes carregadas em memória.");

            System.out.println("[Debug] 3. Carregando FXML Principal (main.fxml)...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Parent root = loader.load();
            System.out.println("[Debug] ✓ FXML carregado com sucesso.");
            
            System.out.println("[Debug] 4. Configurando Cena e CSS...");
            Scene scene = new Scene(root, 1200, 800);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            
            primaryStage.setTitle("The Glass Trainer's Codex | Pokémon TCG Catalog");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            
            System.out.println("[Debug] 5. Abrindo Stage (Janela)...");
            primaryStage.show();
            
            System.out.println("[App] Interface gráfica carregada com sucesso!");
            
        } catch (Exception e) {
            System.err.println("[Erro Fatal] Houve uma falha durante o carregamento da interface:");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
