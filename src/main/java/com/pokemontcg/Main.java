package com.pokemontcg;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Scene;

/**
 * Ponto de entrada oficial da aplicação Pokémon TCG Catalog (Versão Visual).
 * Comentários explicativos: Esta classe agora carrega o arquivo FXML principal 
 * e aplica o conjunto de estilos CSS ao aplicativo.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        
        // 1. Inicializa o banco de dados antes de tudo
        try {
            com.pokemontcg.repository.DatabaseManager.initDatabase();
        } catch (Exception e) {
            System.err.println("Erro crítico no banco de dados: " + e.getMessage());
        }

        try {
            // 2. Carrega o Layout Principal da Navegação (Nave Mãe)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Parent root = loader.load();
            
            // 3. Define a Janela e o Tamanho base (1000x700 como planejado)
            Scene scene = new Scene(root, 1000, 700);
            
            // 4. Configurações Finais do Stage (Janela)
            primaryStage.setTitle("POKÉMON TCG — Catalog v1.0");
            primaryStage.setScene(scene);
            
            // Impede que a janela fique menor que o ideal
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            
            primaryStage.show();
            
            System.out.println("[App] Interface gráfica iniciada com sucesso!");
            
        } catch (Exception e) {
            System.err.println("Erro fatal ao carregar a interface gráfica:");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
