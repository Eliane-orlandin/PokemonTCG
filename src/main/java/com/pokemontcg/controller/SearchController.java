package com.pokemontcg.controller;

import com.pokemontcg.model.Card;
import com.pokemontcg.service.CardService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
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
    @FXML private ComboBox<String> comboCategory;     // Categoria na área de filtros
    @FXML private ComboBox<String> comboType;
    @FXML private ComboBox<String> comboRarity;
    @FXML private ComboBox<String> comboSet;
    @FXML private FlowPane flowResults;
    @FXML private Label lblStatus;

    private final CardService cardService;
    
    private static final Map<String, String> CATEGORY_TRANSLATIONS = new HashMap<>();
    private static final Map<String, String> TYPE_TRANSLATIONS = new HashMap<>();
    private static final Map<String, String> RARITY_TRANSLATIONS = new HashMap<>();
    private static final Map<String, String> SERIES_TRANSLATIONS = new HashMap<>();
    private static final Map<String, String> TRAINER_SUBTYPES = new HashMap<>();
    private static final Map<String, String> ENERGY_SUBTYPES = new HashMap<>();

    static {
        // Categorias
        CATEGORY_TRANSLATIONS.put("Pokémon", "Pokemon");
        CATEGORY_TRANSLATIONS.put("Treinador", "Treinador");
        CATEGORY_TRANSLATIONS.put("Energia", "Energia");

        // Tipos de Pokémon
        TYPE_TRANSLATIONS.put("Incolor", "Incolor");
        TYPE_TRANSLATIONS.put("Sombrio", "Sombrio");
        TYPE_TRANSLATIONS.put("Dragão", "Dragão");
        TYPE_TRANSLATIONS.put("Fada", "Fada");
        TYPE_TRANSLATIONS.put("Lutador", "Lutador");
        TYPE_TRANSLATIONS.put("Fogo", "Fogo");
        TYPE_TRANSLATIONS.put("Planta", "Planta");
        TYPE_TRANSLATIONS.put("Elétrico", "Elétrico");
        TYPE_TRANSLATIONS.put("Metal", "Metal");
        TYPE_TRANSLATIONS.put("Psíquico", "Psíquico");
        TYPE_TRANSLATIONS.put("Água", "Água");

        // Subtipos de Treinador (trainerType na API)
        TRAINER_SUBTYPES.put("Apoiador", "Apoiador");
        TRAINER_SUBTYPES.put("Estádio", "Estádio");
        TRAINER_SUBTYPES.put("Ferramenta", "Ferramenta");
        TRAINER_SUBTYPES.put("Item", "Item");

        // Tipos de Energia (energyType na API)
        ENERGY_SUBTYPES.put("Básica", "Normal");
        ENERGY_SUBTYPES.put("Especial", "Especial");

        // Raridades
        RARITY_TRANSLATIONS.put("Comum", "Comum");
        RARITY_TRANSLATIONS.put("Incomum", "Incomum");
        RARITY_TRANSLATIONS.put("Rara", "Rara");
        RARITY_TRANSLATIONS.put("Rara Holo", "Rara Holo");
        RARITY_TRANSLATIONS.put("Rara Holo V", "Rara Holo V");
        RARITY_TRANSLATIONS.put("Rara Holo VMAX", "Rara Holo VMAX");
        RARITY_TRANSLATIONS.put("Rara Holo VSTAR", "Rara Holo VSTAR");
        RARITY_TRANSLATIONS.put("Rara Radiante", "Rara Radiante");
        RARITY_TRANSLATIONS.put("Rara Secreta", "Rare Secreta");
        RARITY_TRANSLATIONS.put("Ultra Rara", "Ultra Rara");
        RARITY_TRANSLATIONS.put("Hiper Rara", "Hiper rara");
        RARITY_TRANSLATIONS.put("Ilustração Rara", "Ilustração Rara");
        RARITY_TRANSLATIONS.put("Arte Completa de Treinador", "Arte Completa de Treinador");
        RARITY_TRANSLATIONS.put("ACE SPEC Raro", "ACE SPEC Raro");

        // Séries
        SERIES_TRANSLATIONS.put("Base", "Coleção Básica");
        SERIES_TRANSLATIONS.put("Ginásio", "Ginásio");
        SERIES_TRANSLATIONS.put("Neo", "Neo");
        SERIES_TRANSLATIONS.put("EX", "EX");
        SERIES_TRANSLATIONS.put("Diamante & Pérola", "Diamante & Pérola");
        SERIES_TRANSLATIONS.put("HeartGold & SoulSilver", "HeartGold SoulSilver");
        SERIES_TRANSLATIONS.put("Preto & Branco", "Black & White");
        SERIES_TRANSLATIONS.put("XY", "XY");
        SERIES_TRANSLATIONS.put("Sol & Lua", "Sol e Lua");
        SERIES_TRANSLATIONS.put("Espada & Escudo", "Espada e Escudo");
        SERIES_TRANSLATIONS.put("Escarlate & Violeta", "Escarlate e Violeta");
        SERIES_TRANSLATIONS.put("Megaevolução", "Megaevolução");
    }

    public SearchController() {
        this.cardService = new CardService();
    }

    @FXML
    public void initialize() {
        // Inicializa Categoria
        if (comboCategory != null) {
            comboCategory.getItems().add("Todas as Categorias");
            comboCategory.getItems().addAll(CATEGORY_TRANSLATIONS.keySet().stream().sorted().collect(Collectors.toList()));
            comboCategory.getSelectionModel().selectFirst();
        }

        // Listener para o ComboBox de Categoria (Integração de Filtros)
        if (comboCategory != null) {
            comboCategory.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (txtSearch != null) txtSearch.clear(); // Limpa o input ao mudar o filtro
                updateTypeOptions(newVal);
                handleSearch(); // Busca instantânea
            });
        }

        // Listeners para os demais filtros
        if (comboType != null) {
            comboType.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) handleSearch(); // Dispara busca ao mudar
            });
        }
        
        // Inicializa Raridades
        if (comboRarity != null) {
            comboRarity.getItems().add("Todas as Raridades");
            comboRarity.getItems().addAll(RARITY_TRANSLATIONS.keySet().stream().sorted().collect(Collectors.toList()));
            comboRarity.getSelectionModel().selectFirst();
            
            // Listener para limpar busca e pesquisar instantaneamente
            comboRarity.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (txtSearch != null && newVal != null && !newVal.equals(oldVal)) {
                    txtSearch.clear();
                    handleSearch();
                }
            });
        }
        
        // Inicializa Séries
        if (comboSet != null) {
            comboSet.getItems().add("Todas as Séries");
            comboSet.getItems().addAll(SERIES_TRANSLATIONS.keySet().stream().sorted().collect(Collectors.toList()));
            comboSet.getSelectionModel().selectFirst();

            // Listener para limpar busca e pesquisar instantaneamente
            comboSet.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (txtSearch != null && newVal != null && !newVal.equals(oldVal)) {
                    txtSearch.clear();
                    handleSearch();
                }
            });
        }
    }

    /**
     * Acionado quando o botão SEARCH é clicado ou Enter é pressionado.
     */
    @FXML
    public void handleSearch() {
        System.out.println(">>> handleSearch: Iniciando busca... <<<");
        String query = (txtSearch != null) ? txtSearch.getText().trim() : "";

        // 1. Captura seleção dos filtros primeiro (retorna null se for "Todas/Todos")
        String selectedCategory = comboCategory != null ? comboCategory.getValue() : null;
        String apiCategory = CATEGORY_TRANSLATIONS.get(selectedCategory); // null se não estiver no Map
        
        String selectedType = comboType != null ? comboType.getValue() : null;
        String apiType = null;
        
        // Mapeia o tipo/subtipo baseado na categoria ativa
        if ("Treinador".equals(selectedCategory)) {
            apiType = TRAINER_SUBTYPES.get(selectedType);
        } else if ("Energia".equals(selectedCategory)) {
            apiType = ENERGY_SUBTYPES.get(selectedType);
        } else {
            apiType = TYPE_TRANSLATIONS.get(selectedType);
        }
        
        String selectedRarity = comboRarity != null ? comboRarity.getValue() : null;
        String apiRarity = RARITY_TRANSLATIONS.get(selectedRarity);

        String selectedSet = (comboSet != null) ? comboSet.getValue() : null;
        String apiSet = SERIES_TRANSLATIONS.get(selectedSet);

        // 2. Verifica se há pelo menos um critério de busca ativo (Texto ou Filtros)
        boolean hasFilter = (apiCategory != null) || (apiType != null) || (apiRarity != null) || (apiSet != null);
        
        if (query.isEmpty() && !hasFilter) {
            lblStatus.setText("Por favor, insira um nome/número ou selecione um filtro.");
            if (flowResults != null) flowResults.getChildren().clear();
            return;
        }

        lblStatus.setText("Buscando cartas... Aguarde.");
        
        String searchName = null;
        String searchLocalId = null;

        // 3. Regex para detectar se é um localId (números ou formato n/n)
        if (query.matches("\\d+(/\\d+)?")) {
            searchLocalId = query;
            System.out.println("[DEBUG] Detectada busca por LocalID: " + searchLocalId);
        } else if (!query.isEmpty()) {
            searchName = query;
            System.out.println("[DEBUG] Detectada busca por Nome: " + searchName);
        }

        // Debug dos parâmetros enviados
        System.out.println(String.format("[DEBUG] Parâmetros: Nome=%s, Cat=%s, Tipo=%s, Rar=%s, Sér=%s", 
            query, apiCategory, apiType, apiRarity, apiSet));
        
        if (flowResults != null) flowResults.getChildren().clear();
        
        // Cópias finais para uso dentro da Lambda (Thread)
        final String finalSearchName = searchName;
        final String finalSearchLocalId = searchLocalId;
        final String finalCategory = apiCategory;
        final String finalType = apiType;
        final String finalRarity = apiRarity;
        final String finalSet = apiSet;

        new Thread(() -> {
            try {
                // Dispara a busca com todos os filtros ativos (agora incluindo o parâmetro inteligente)
                List<Card> results = cardService.searchCards(finalSearchName, finalCategory, finalType, finalRarity, finalSet, finalSearchLocalId);
                
                Platform.runLater(() -> {
                    if (results.isEmpty()) {
                        lblStatus.setText("Nenhum resultado encontrado para \"" + query + "\".");
                        System.out.println("[DEBUG] Nenhum resultado para os critérios informados.");
                    } else {
                        lblStatus.setText(results.size() + " resultados encontrados para \"" + query + "\".");
                        loadCardsInGrid(results);
                    }
                });
                
            } catch (Exception e) {
                System.err.println("❌ Erro na busca: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Atualiza as opções do ComboBox de Tipo de acordo com a categoria selecionada.
     */
    private void updateTypeOptions(String category) {
        if (comboType == null) return;
        
        comboType.getItems().clear();
        comboType.getItems().add("Todos"); // Termo geral para resetar
        
        if ("Treinador".equals(category)) {
            comboType.getItems().addAll(TRAINER_SUBTYPES.keySet().stream().sorted().collect(Collectors.toList()));
        } else if ("Energia".equals(category)) {
            comboType.getItems().addAll(ENERGY_SUBTYPES.keySet().stream().sorted().collect(Collectors.toList()));
        } else {
            // Padrão ou Pokémon
            comboType.getItems().addAll(TYPE_TRANSLATIONS.keySet().stream().sorted().collect(Collectors.toList()));
        }
        
        comboType.getSelectionModel().selectFirst();
    }

    /**
     * Reseta todos os filtros e o campo de busca.
     */
    @FXML
    public void handleClearFilters() {
        System.out.println("[DEBUG] Limpando todos os filtros...");
        if (txtSearch != null) txtSearch.clear();
        if (comboCategory != null) comboCategory.getSelectionModel().selectFirst();
        if (comboType != null) comboType.getSelectionModel().selectFirst();
        if (comboRarity != null) comboRarity.getSelectionModel().selectFirst();
        if (comboSet != null) comboSet.getSelectionModel().selectFirst();
        if (lblStatus != null) lblStatus.setText("Filtros limpos. Insira um nome ou número para pesquisar.");
        
        if (flowResults != null) flowResults.getChildren().clear();
    }

    /**
     * Carrega cada card encontrado injetando o componente card_item.fxml na grade.
     */
    private void loadCardsInGrid(List<Card> cards) {
        // Limita a exibição inicial para performance (v2 API retorna muitos itens)
        int limit = Math.min(cards.size(), 50); 
        System.out.println("[App] Renderizando " + limit + " cards...");

        for (int i = 0; i < limit; i++) {
            Card card = cards.get(i);
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/card_item.fxml"));
                Node cardNode = loader.load();
                
                CardItemController controller = loader.getController();
                
                // Formatação e tratamento de valores nulos
                String type = (card.getTypes() != null && !card.getTypes().isEmpty()) ? card.getTypes().get(0) : "N/A";
                String rarity = (card.getRarity() != null) ? card.getRarity() : "Comum";
                String displayId = String.format("%s • %s", 
                    (card.getSetId() != null ? card.getSetId() : "API"),
                    (card.getLocalId() != null ? card.getLocalId() : "?"));

                controller.setCardData(
                    card.getName(),
                    card.getId(),
                    displayId,
                    card.getImage(),
                    type,
                    rarity,
                    card.getSeriesId(),
                    card.getSeriesName(),
                    card.getStage()
                );
                
                flowResults.getChildren().add(cardNode);
                
            } catch (Exception e) {
                System.err.println("❌ Erro ao renderizar card: " + card.getName());
                break;
            }
        }
    }
}
