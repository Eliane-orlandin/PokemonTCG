package com.pokemontcg.controller;

import com.pokemontcg.model.Card;
import com.pokemontcg.service.CardService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador da tela de Busca (Search API).
 * Gerencia a entrada de texto do usuário, consulta a API TCGdex
 * e renderiza dinamicamente os resultados na grade (FlowPane).
 */
public class SearchController {

    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> comboCategoryMain; // Categoria na barra superior
    @FXML private ComboBox<String> comboCategory;     // Categoria na área de filtros
    @FXML private ComboBox<String> comboType;
    @FXML private ComboBox<String> comboRarity;
    @FXML private ComboBox<String> comboSet;
    @FXML private FlowPane flowResults;

    private final CardService cardService;
    
    // Mapeamentos para tradução (Exibição -> API)
    private static final Map<String, String> CATEGORY_TRANSLATIONS = new HashMap<>();
    private static final Map<String, String> TYPE_TRANSLATIONS = new HashMap<>();
    private static final Map<String, String> RARITY_TRANSLATIONS = new HashMap<>();
    private static final Map<String, String> SERIES_TRANSLATIONS = new HashMap<>();

    static {
        // Categorias
        CATEGORY_TRANSLATIONS.put("Pokémon", "Pokemon");
        CATEGORY_TRANSLATIONS.put("Treinador", "Trainer");
        CATEGORY_TRANSLATIONS.put("Energia", "Energy");

        // Tipos
        TYPE_TRANSLATIONS.put("Incolor", "Colorless");
        TYPE_TRANSLATIONS.put("Noturno", "Darkness");
        TYPE_TRANSLATIONS.put("Dragão", "Dragon");
        TYPE_TRANSLATIONS.put("Fada", "Fairy");
        TYPE_TRANSLATIONS.put("Lutador", "Fighting");
        TYPE_TRANSLATIONS.put("Fogo", "Fire");
        TYPE_TRANSLATIONS.put("Planta", "Grass");
        TYPE_TRANSLATIONS.put("Raio", "Lightning");
        TYPE_TRANSLATIONS.put("Metal", "Metal");
        TYPE_TRANSLATIONS.put("Psíquico", "Psychic");
        TYPE_TRANSLATIONS.put("Água", "Water");

        // Raridades
        RARITY_TRANSLATIONS.put("Rara ACE SPEC", "ACE SPEC Rare");
        RARITY_TRANSLATIONS.put("Rara Incrível", "Amazing Rare");
        RARITY_TRANSLATIONS.put("Rara B&W", "Black White Rare");
        RARITY_TRANSLATIONS.put("Coleção Clássica", "Classic Collection");
        RARITY_TRANSLATIONS.put("Comum", "Common");
        RARITY_TRANSLATIONS.put("Coroa", "Crown");
        RARITY_TRANSLATIONS.put("Dupla Rara", "Double rare");
        RARITY_TRANSLATIONS.put("Quatro Diamantes", "Four Diamond");
        RARITY_TRANSLATIONS.put("Treinador Full Art", "Full Art Trainer");
        RARITY_TRANSLATIONS.put("Holo Rara", "Holo Rare");
        RARITY_TRANSLATIONS.put("Holo Rara V", "Holo Rare V");
        RARITY_TRANSLATIONS.put("Holo Rara VMAX", "Holo Rare VMAX");
        RARITY_TRANSLATIONS.put("Holo Rara VSTAR", "Holo Rare VSTAR");
        RARITY_TRANSLATIONS.put("Hiper Rara", "Hyper rare");
        RARITY_TRANSLATIONS.put("Ilustração Rara", "Illustration rare");
        RARITY_TRANSLATIONS.put("LENDA", "LEGEND");
        RARITY_TRANSLATIONS.put("Mega Hiper Rara", "Mega Hyper Rare");
        RARITY_TRANSLATIONS.put("Um Diamante", "One Diamond");
        RARITY_TRANSLATIONS.put("Um Brilhante", "One Shiny");
        RARITY_TRANSLATIONS.put("Uma Estrela", "One Star");
        RARITY_TRANSLATIONS.put("Rara Radiante", "Radiant Rare");
        RARITY_TRANSLATIONS.put("Rara", "Rare");
        RARITY_TRANSLATIONS.put("Rara Holo", "Rare Holo");
        RARITY_TRANSLATIONS.put("Rara Holo LV.X", "Rare Holo LV.X");
        RARITY_TRANSLATIONS.put("Rara PRIME", "Rare PRIME");
        RARITY_TRANSLATIONS.put("Rara Secreta", "Secret Rare");
        RARITY_TRANSLATIONS.put("Ultra Rara Brilhante", "Shiny Ultra Rare");
        RARITY_TRANSLATIONS.put("Rara Brilhante", "Shiny rare");
        RARITY_TRANSLATIONS.put("Rara Brilhante V", "Shiny rare V");
        RARITY_TRANSLATIONS.put("Rara Brilhante VMAX", "Shiny rare VMAX");
        RARITY_TRANSLATIONS.put("Ilustração Especial Rara", "Special illustration rare");
        RARITY_TRANSLATIONS.put("Três Diamantes", "Three Diamond");
        RARITY_TRANSLATIONS.put("Três Estrelas", "Three Star");
        RARITY_TRANSLATIONS.put("Dois Diamantes", "Two Diamond");
        RARITY_TRANSLATIONS.put("Dois Brilhantes", "Two Shiny");
        RARITY_TRANSLATIONS.put("Duas Estrelas", "Two Star");
        RARITY_TRANSLATIONS.put("Ultra Rara", "Ultra Rare");
        RARITY_TRANSLATIONS.put("Incomum", "Uncommon");

        // Séries
        SERIES_TRANSLATIONS.put("Base", "Base");
        SERIES_TRANSLATIONS.put("Ginásio", "Gym");
        SERIES_TRANSLATIONS.put("Neo", "Neo");
        SERIES_TRANSLATIONS.put("Coleção Lendária", "Legendary Collection");
        SERIES_TRANSLATIONS.put("EX", "EX");
        SERIES_TRANSLATIONS.put("POP", "POP");
        SERIES_TRANSLATIONS.put("Kits de Treinador", "Trainer kits");
        SERIES_TRANSLATIONS.put("Diamante & Pérola", "Diamond & Pearl");
        SERIES_TRANSLATIONS.put("Platina", "Platinum");
        SERIES_TRANSLATIONS.put("HeartGold & SoulSilver", "HeartGold & SoulSilver");
        SERIES_TRANSLATIONS.put("Chamado das Lendas", "Call of Legends");
        SERIES_TRANSLATIONS.put("Preto & Branco", "Black & White");
        SERIES_TRANSLATIONS.put("Coleção McDonald's", "McDonald's Collection");
        SERIES_TRANSLATIONS.put("XY", "XY");
        SERIES_TRANSLATIONS.put("Sol & Lua", "Sun & Moon");
        SERIES_TRANSLATIONS.put("Espada & Escudo", "Sword & Shield");
        SERIES_TRANSLATIONS.put("Escarlate & Violeta", "Scarlet & Violet");
        SERIES_TRANSLATIONS.put("TCG Pocket", "Pokémon TCG Pocket");
        SERIES_TRANSLATIONS.put("Mega Evolução", "Mega Evolution");
    }

    public SearchController() {
        this.cardService = new CardService();
    }

    @FXML
    public void initialize() {
        // Inicializa Categorias (e sincroniza os dois menus)
        setupCategoryCombos();

        // Inicializa Tipos
        if (comboType != null) {
            comboType.getItems().add("Todos os Tipos");
            comboType.getItems().addAll(TYPE_TRANSLATIONS.keySet().stream().sorted().collect(Collectors.toList()));
        }
        
        // Inicializa Raridades
        if (comboRarity != null) {
            comboRarity.getItems().add("Todas as Raridades");
            comboRarity.getItems().addAll(RARITY_TRANSLATIONS.keySet().stream().sorted().collect(Collectors.toList()));
        }
        
        // Inicializa Séries
        if (comboSet != null) {
            comboSet.getItems().add("Todas as Séries");
            comboSet.getItems().addAll(SERIES_TRANSLATIONS.keySet().stream().sorted().collect(Collectors.toList()));
        }
    }

    /**
     * Configura as categorias e garante que os dois menus (superior e inferior) fiquem em sincronia.
     */
    private void setupCategoryCombos() {
        List<String> categories = CATEGORY_TRANSLATIONS.keySet().stream().sorted().collect(Collectors.toList());
        
        // Inicializa Superior
        if (comboCategoryMain != null) {
            comboCategoryMain.getItems().add("Todas as Categorias");
            comboCategoryMain.getItems().addAll(categories);
            
            // Listener para sincronizar com o inferior
            comboCategoryMain.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (comboCategory != null && (comboCategory.getValue() == null || !comboCategory.getValue().equals(newVal))) {
                    comboCategory.setValue(newVal);
                }
            });
        }

        // Inicializa Inferior
        if (comboCategory != null) {
            comboCategory.getItems().add("Todas as Categorias");
            comboCategory.getItems().addAll(categories);
            
            // Listener para sincronizar com o superior
            comboCategory.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (comboCategoryMain != null && (comboCategoryMain.getValue() == null || !comboCategoryMain.getValue().equals(newVal))) {
                    comboCategoryMain.setValue(newVal);
                }
            });
        }
    }

    /**
     * Acionado quando o botão SEARCH é clicado ou Enter é pressionado.
     */
    @FXML
    public void handleSearch() {
        String query = txtSearch.getText().trim();
        System.out.println("[DEBUG] SearchController: Iniciando busca por -> " + query);
        
        if (query.isEmpty()) {
            System.out.println("[Warn] Query vazia.");
            return;
        }

        flowResults.getChildren().clear();
        System.out.println("🔍 Searching for \"" + query + "\"... Please wait.");

        new Thread(() -> {
            try {
                System.out.println("[DEBUG] SearchController: Chamando CardService...");
                // Para simplificar agora, buscamos sempre por nome por padrão
                List<Card> results = cardService.searchByName(query);
                
                System.out.println("[DEBUG] SearchController: Service retornou " + results.size() + " resultados");
                
                Platform.runLater(() -> {
                    if (results.isEmpty()) {
                        System.out.println("[DEBUG] SearchController: Nenhum card encontrado");
                    } else {
                        loadCardsInGrid(results);
                    }
                });
                
            } catch (Exception e) {
                System.err.println("❌ Error during search: " + e.getMessage());
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
                System.err.println("❌ Erro crítico ao renderizar card: " + e.getMessage());
                e.printStackTrace();
                break; // Para no primeiro erro para não inundar o log
            }
        }
    }
}
