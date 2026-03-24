package com.pokemontcg.controller;

import com.pokemontcg.api.TcgDexClient;
import com.pokemontcg.model.Card;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import java.util.List;

/**
 * Controlador da tela de Busca (Search API).
 * Gerencia a entrada de texto do usuário, consulta a API TCGdex
 * e renderiza dinamicamente os resultados na grade (FlowPane).
 */
public class SearchController {

    @FXML private TextField txtSearch;
    @FXML private Label lblStatus;
    @FXML private FlowPane flowResults;

    private TcgDexClient tcgClient;

    @FXML
    public void initialize() {
        this.tcgClient = new TcgDexClient();
    }

    /**
     * Acionado quando o botão SEARCH é clicado ou Enter é pressionado.
     */
    @FXML
    public void handleSearch() {
        String query = txtSearch.getText().trim();
        System.out.println("[DEBUG] SearchController: Iniciando busca por -> " + query);
        
        if (query.isEmpty()) {
            lblStatus.setText("⚠ Please enter a name to search.");
            return;
        }

        flowResults.getChildren().clear();
        lblStatus.setText("🔍 Searching for \"" + query + "\"... Please wait.");

        new Thread(() -> {
            try {
                System.out.println("[DEBUG] SearchController: Chamando API TCGdex...");
                List<Card> results = tcgClient.searchByName(query);
                System.out.println("[DEBUG] SearchController: API retornou " + results.size() + " resultados");
                
                Platform.runLater(() -> {
                    if (results.isEmpty()) {
                        System.out.println("[DEBUG] SearchController: Nenhum card encontrado");
                        lblStatus.setText("❌ No cards found for \"" + query + "\".");
                    } else {
                        System.out.println("[DEBUG] SearchController: Chamando renderizador de grade...");
                        lblStatus.setText("✅ Found " + results.size() + " cards matching \"" + query + "\".");
                        loadCardsInGrid(results);
                    }
                });
                
            } catch (Exception e) {
                Platform.runLater(() -> {
                    lblStatus.setText("❌ Error during search: " + e.getMessage());
                });
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Carrega cada card encontrado injetando o componente card_item.fxml na grade.
     */
    private void loadCardsInGrid(List<Card> cards) {
        // Limita a exibição inicial para performance
        int limit = Math.min(cards.size(), 40); 
        System.out.println("[App] Iniciando renderização de " + limit + " cards...");

        for (int i = 0; i < limit; i++) {
            Card card = cards.get(i);
            try {
                System.out.println("[DEBUG] SearchController: Carregando card_item.fxml para -> " + card.getName());
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/card_item.fxml"));
                Node cardNode = loader.load();
                
                CardItemController controller = loader.getController();
                
                // Formata os dados com segurança
                String cardName = (card.getName() != null) ? card.getName() : "Unknown Card";
                String type = (card.getTypes() != null && !card.getTypes().isEmpty()) ? card.getTypes().get(0) : "Colorless";
                String rarity = (card.getRarity() != null) ? card.getRarity() : "Common";
                
                // Monta o ID de exibição (SetId • LocalId)
                String displaySetId = (card.getSetId() != null ? card.getSetId() : "API") 
                                     + " • " + 
                                     (card.getLocalId() != null ? card.getLocalId() : "???");

                controller.setCardData(
                    cardName,
                    card.getId(), // ID Único Real (ex: swsh1-1)
                    displaySetId,  // String de Exibição (ex: swsh1 • 1)
                    card.getImage(),
                    type,
                    rarity,
                    card.getSeriesId(),
                    card.getSeriesName()
                );
                
                flowResults.getChildren().add(cardNode);
                System.out.println("[DEBUG] SearchController: Card injetado visualmente.");
                
            } catch (Exception e) {
                final String errorMsg = e.getMessage();
                Platform.runLater(() -> {
                    lblStatus.setText("❌ Error rendering cards: " + errorMsg);
                });
                System.err.println("❌ Erro crítico ao renderizar card: " + errorMsg);
                e.printStackTrace();
                break; // Para no primeiro erro para não inundar o log
            }
        }
    }
}
