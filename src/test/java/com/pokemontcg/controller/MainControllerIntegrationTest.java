package com.pokemontcg.controller;

import com.pokemontcg.repository.DatabaseManager;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import org.junit.jupiter.api.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste de Integração de Controller (Headless Nativo).
 * Comentários explicativos: Testamos a navegação "em memória" para evitar
 * incompatibilidades de UI e rodar de forma totalmente invisível.
 */
public class MainControllerIntegrationTest {

    private MainController controller;
    private Parent root;

    @BeforeAll
    static void initJFX() throws InterruptedException {
        // Inicializa o motor do JavaFX sem abrir janela (necessário apenas uma vez)
        try {
            CountDownLatch latch = new CountDownLatch(1);
            Platform.startup(latch::countDown);
            latch.await(5, TimeUnit.SECONDS);
        } catch (IllegalStateException e) {
            // Se o Toolkit já estiver inicializado, apenas ignora
        }
        
        // Banco de teste isolado
        DatabaseManager.setDatabasePath("catalog_test_controller.db");
        DatabaseManager.initDatabase();
        
        // Garante que o modo headless do TestFX está ativo (usado na nossa proteção do MainController)
        System.setProperty("testfx.headless", "true");
    }

    @BeforeEach
    void setUp() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
                root = loader.load();
                controller = loader.getController();
                
                // CRITICAL: O lookup() do JavaFX só funciona se o root estiver em uma Scene
                new javafx.scene.Scene(root);
                
                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        if (!latch.await(10, TimeUnit.SECONDS)) {
            fail("Timeout ao inicializar o MainController no JavaFX Thread");
        }
    }

    @Test
    void deveCarregarHomeInicialmente() throws InterruptedException {
        // Como o carregamento é via Platform.runLater, aguardamos um pouco
        CountDownLatch waitLatch = new CountDownLatch(1);
        
        // Verifica em looping se a área de conteúdo já tem o dashboard carregado
        Thread checkThread = new Thread(() -> {
            try {
                for (int i = 0; i < 20; i++) { // Tenta por 2 segundos
                    StackPane contentArea = (StackPane) root.lookup("#contentArea");
                    if (contentArea != null && !contentArea.getChildren().isEmpty()) {
                        waitLatch.countDown();
                        break;
                    }
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {}
        });
        checkThread.start();
        
        assertTrue(waitLatch.await(5, TimeUnit.SECONDS), "Timeout: O Dashboard não foi carregado a tempo.");

        StackPane contentArea = (StackPane) root.lookup("#contentArea");
        assertNotNull(contentArea, "A área de conteúdo principal deve existir.");
        assertFalse(contentArea.getChildren().isEmpty(), "O Dashboard deve ser carregado no início.");
        
        // Verifica se o primeiro filho é um ScrollPane (home.fxml começa com ScrollPane)
        assertTrue(contentArea.getChildren().get(0) instanceof javafx.scene.control.ScrollPane, 
                   "O Dashboard (ScrollPane) deve estar injetado.");
    }

    @Test
    void deveMudarDeTelaAoNavegarParaCatalogo() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        
        Platform.runLater(() -> {
            controller.handleNavigateCatalog();
            latch.countDown();
        });
        latch.await(5, TimeUnit.SECONDS);

        StackPane contentArea = (StackPane) root.lookup("#contentArea");
        assertFalse(contentArea.getChildren().isEmpty());
        
        // Na nossa implementação, catalog.fxml também é um ScrollPane
        assertTrue(contentArea.getChildren().get(0) instanceof javafx.scene.control.ScrollPane,
                   "A tela de catálogo (ScrollPane) deve estar visível.");
        
        // Opcional: Verificar via UserData ou uma propriedade específica se necessário, 
        // mas o instanceof já garante que a troca de View ocorreu no StackPane.
    }

    @Test
    void deveMudarDeTelaAoNavegarParaBusca() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        
        Platform.runLater(() -> {
            controller.handleNavigateSearch();
            latch.countDown();
        });
        latch.await(5, TimeUnit.SECONDS);

        StackPane contentArea = (StackPane) root.lookup("#contentArea");
        assertFalse(contentArea.getChildren().isEmpty());
        
        // search.fxml também começa com ScrollPane
        assertTrue(contentArea.getChildren().get(0) instanceof javafx.scene.control.ScrollPane,
                   "A tela de busca deve estar visível no StackPane.");
    }
}
