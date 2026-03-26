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
    private static final Map<String, String> POKEMON_RARITIES = new HashMap<>();
    private static final Map<String, String> TRAINER_RARITIES = new HashMap<>();
    private static final Map<String, String> ENERGY_RARITIES = new HashMap<>();

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
        RARITY_TRANSLATIONS.put("Rara Secreta", "Rara Secreta");
        RARITY_TRANSLATIONS.put("Ultra Rara", "Ultra Rara");
        RARITY_TRANSLATIONS.put("Hiper Rara", "Hiper Rara");
        RARITY_TRANSLATIONS.put("Ilustração Rara", "Ilustração Rara");
        RARITY_TRANSLATIONS.put("Arte Completa de Treinador", "Arte Completa de Treinador");
        RARITY_TRANSLATIONS.put("ACE SPEC Raro", "ACE SPEC Raro");

        // Raridades específicas de Pokémon
        POKEMON_RARITIES.put("Comum", "Comum");
        POKEMON_RARITIES.put("Incomum", "Incomum");
        POKEMON_RARITIES.put("Rara", "Rara");
        POKEMON_RARITIES.put("Rara Holo", "Rara Holo");
        POKEMON_RARITIES.put("Rara Holo V", "Rara Holo V");
        POKEMON_RARITIES.put("Rara Holo VMAX", "Rara Holo VMAX");
        POKEMON_RARITIES.put("Rara Holo VSTAR", "Rara Holo VSTAR");
        POKEMON_RARITIES.put("Rara Radiante", "Rara Radiante");
        POKEMON_RARITIES.put("Rara Secreta", "Rara Secreta");
        POKEMON_RARITIES.put("Ultra Rara", "Ultra Rara");
        POKEMON_RARITIES.put("Hiper Rara", "Hiper Rara");
        POKEMON_RARITIES.put("Ilustração Rara", "Ilustração Rara");
        POKEMON_RARITIES.put("ACE SPEC Raro", "ACE SPEC Raro");

        // Raridades específicas de Treinador
        TRAINER_RARITIES.put("Comum", "Comum");
        TRAINER_RARITIES.put("Incomum", "Incomum");
        TRAINER_RARITIES.put("Rara", "Rara");
        TRAINER_RARITIES.put("Rara Holo", "Rara Holo");
        TRAINER_RARITIES.put("Rara Secreta", "Rara Secreta");
        TRAINER_RARITIES.put("Ultra Rara", "Ultra Rara");
        TRAINER_RARITIES.put("Hiper Rara", "Hiper Rara");
        TRAINER_RARITIES.put("Ilustração Rara", "Ilustração Rara");
        TRAINER_RARITIES.put("Arte Completa de Treinador", "Arte Completa de Treinador");

        // Raridades específicas de Energia
        ENERGY_RARITIES.put("Comum", "Comum");
        ENERGY_RARITIES.put("Incomum", "Incomum");
        ENERGY_RARITIES.put("Rara", "Rara");
        ENERGY_RARITIES.put("Rara Holo", "Rara Holo");
        ENERGY_RARITIES.put("Rara Secreta", "Rara Secreta");
        ENERGY_RARITIES.put("Hiper Rara", "Hiper Rara");
        ENERGY_RARITIES.put("Ilustração Rara", "Ilustração Rara");

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
                updateRarityOptions(newVal);
                updateSetOptions(newVal);
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
            updateRarityOptions("Todas as Categorias"); // Inicia bloqueado
            
            // Listener para limpar busca e pesquisar instantaneamente
            comboRarity.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (txtSearch != null && newVal != null && !newVal.equals(oldVal) && !newVal.equals("Selecione Categoria")) {
                    txtSearch.clear();
                    handleSearch();
                }
            });
        }
        
        // Inicializa Séries
        if (comboSet != null) {
            updateSetOptions("Todas as Categorias"); // Inicia bloqueado

            // Listener para limpar busca e pesquisar instantaneamente
            comboSet.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (txtSearch != null && newVal != null && !newVal.equals(oldVal) && !newVal.equals("Selecione Categoria")) {
                    txtSearch.clear();
                    handleSearch();
                }
            });
        }
        
        // Garante estado inicial consistente chamando os updates
        updateTypeOptions("Todas as Categorias");
        updateRarityOptions("Todas as Categorias");
        updateSetOptions("Todas as Categorias");
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
        
        // A limpeza será feita dentro do Platform.runLater para evitar flickers
        // if (flowResults != null) flowResults.getChildren().clear();
        
        // Cópias finais para uso dentro da Lambda (Thread)
        final String finalSearchName = searchName;
        final String finalSearchLocalId = searchLocalId;
        final String finalCategory = apiCategory;
        final String finalType = apiType;
        final String finalRarity = apiRarity;
        final String finalSet = apiSet;

        Thread t = new Thread(() -> {
            try {
                // Dispara a busca com todos os filtros ativos (agora incluindo o parâmetro inteligente)
                List<Card> results = cardService.searchCards(finalSearchName, finalCategory, finalType, finalRarity, finalSet, finalSearchLocalId);
                
                Platform.runLater(() -> {
                    if (results.isEmpty()) {
                        lblStatus.setText("Nenhum resultado encontrado para os filtros selecionados.");
                    } else {
                        // Constrói mensagem de status baseada nos filtros ativos
                        StringBuilder status = new StringBuilder();
                        status.append(results.size()).append(" resultados encontrados");
                        
                        if (query != null && !query.isEmpty()) {
                            status.append(" para \"").append(query).append("\"");
                        }
                        if (selectedCategory != null && !selectedCategory.contains("Todas")) {
                            status.append(" em ").append(selectedCategory);
                        }
                        
                        lblStatus.setText(status.append(".").toString());
                        
                        // Limpa e renderiza na UI Thread
                        if (flowResults != null) {
                            flowResults.getChildren().clear();
                        }
                        loadCardsInGrid(results);
                    }
                });
                
            } catch (Exception e) {
                System.err.println("❌ Erro na busca: " + e.getMessage());
            }
        });
        t.setDaemon(true); // Não impede a JVM de fechar
        t.start();
    }

    /**
     * Atualiza as opções do ComboBox de Tipo de acordo com a categoria selecionada.
     */
    private void updateTypeOptions(String category) {
        if (comboType == null) return;
        
        comboType.getItems().clear();
        
        if (category == null || category.equals("Todas as Categorias")) {
            comboType.getItems().add("Selecione Categoria");
            comboType.getSelectionModel().selectFirst();
            comboType.setDisable(true);
            return;
        }

        comboType.setDisable(false);
        comboType.getItems().add("Todos");
        
        if ("Treinador".equals(category)) {
            comboType.getItems().addAll(TRAINER_SUBTYPES.keySet().stream().sorted().collect(Collectors.toList()));
        } else if ("Energia".equals(category)) {
            comboType.getItems().addAll(ENERGY_SUBTYPES.keySet().stream().sorted().collect(Collectors.toList()));
        } else if ("Pokémon".equals(category)) {
            comboType.getItems().addAll(TYPE_TRANSLATIONS.keySet().stream().sorted().collect(Collectors.toList()));
        }
        
        comboType.getSelectionModel().selectFirst();
    }

    /**
     * Atualiza as opções do ComboBox de Raridade de acordo com a categoria selecionada.
     */
    private void updateRarityOptions(String category) {
        if (comboRarity == null) return;
        
        comboRarity.getItems().clear();
        
        if (category == null || category.equals("Todas as Categorias")) {
            comboRarity.getItems().add("Selecione Categoria");
            comboRarity.getSelectionModel().selectFirst();
            comboRarity.setDisable(true);
            return;
        }

        comboRarity.setDisable(false);
        comboRarity.getItems().add("Todas as Raridades");
        
        if ("Treinador".equals(category)) {
            comboRarity.getItems().addAll(TRAINER_RARITIES.keySet().stream().sorted().collect(Collectors.toList()));
        } else if ("Energia".equals(category)) {
            comboRarity.getItems().addAll(ENERGY_RARITIES.keySet().stream().sorted().collect(Collectors.toList()));
        } else if ("Pokémon".equals(category)) {
            comboRarity.getItems().addAll(POKEMON_RARITIES.keySet().stream().sorted().collect(Collectors.toList()));
        }
        
        comboRarity.getSelectionModel().selectFirst();
    }

    /**
     * Atualiza as opções do ComboBox de Série de acordo com a categoria selecionada.
     */
    private void updateSetOptions(String category) {
        if (comboSet == null) return;
        
        comboSet.getItems().clear();
        
        if (category == null || category.equals("Todas as Categorias")) {
            comboSet.getItems().add("Selecione Categoria");
            comboSet.getSelectionModel().selectFirst();
            comboSet.setDisable(true);
            return;
        }

        comboSet.setDisable(false);
        comboSet.getItems().add("Todas as Séries");
        comboSet.getItems().addAll(SERIES_TRANSLATIONS.keySet().stream().sorted().collect(Collectors.toList()));
        
        comboSet.getSelectionModel().selectFirst();
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
                System.err.println("❌ Erro crítico ao renderizar card \"" + card.getName() + "\": " + e.getMessage());
                e.printStackTrace(); // Mostra o erro real no console para debug
            }
        }
    }
}
