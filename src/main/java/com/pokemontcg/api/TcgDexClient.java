package com.pokemontcg.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pokemontcg.exception.ApiException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.time.Duration;
import java.util.List;

/**
 * Cliente responsável por interagir com a API REST da TCGdex.
 * Refatorado para usar requisições HTTP diretas com filtros,
 * superando as limitações de performance do SDK oficial.
 */
public class TcgDexClient {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl = "https://api.tcgdex.net/v2/pt";

    public TcgDexClient() {
        // Timeout de 15 segundos para evitar threads bloqueadas indefinidamente
        this.httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(15))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Busca cards usando múltiplos filtros combinados no servidor.
     */
    public List<com.pokemontcg.model.Card> searchCards(String name, String category, String type, String rarity, String series, String localId) {
        StringBuilder urlBuilder = new StringBuilder(baseUrl).append("/cards?");
        boolean first = true;

        // Filtro por nome
        if (name != null && !name.trim().isEmpty()) {
            urlBuilder.append("name=like:").append(encodeParam(name));
            first = false;
        }

        // Filtro por LocalId (Número da carta no set)
        if (localId != null && !localId.trim().isEmpty()) {
            if (!first) urlBuilder.append("&");
            urlBuilder.append("localId=").append(encodeParam(localId));
            first = false;
        }

        // Filtro por Categoria (Endpoint /pt aceita termos como 'Energia' ou 'Treinador')
        if (category != null && !category.isEmpty() && !category.toLowerCase().startsWith("tod")) {
            if (!first) urlBuilder.append("&");
            urlBuilder.append("category=").append(encodeParam(category));
            first = false;
        }

        // Filtro por Tipos/Subtipos (Dinâmico por categoria)
        if (type != null && !type.isEmpty() && !type.toLowerCase().startsWith("tod")) {
            if (!first) urlBuilder.append("&");
            
            // Escolhe o parâmetro correto da API baseado na categoria
            String paramName = "types"; // Padrão para Pokémon
            if ("Treinador".equals(category)) {
                paramName = "trainerType";
            } else if ("Energia".equals(category)) {
                paramName = "energyType";
            }
            
            urlBuilder.append(paramName).append("=").append(encodeParam(type));
            first = false;
        }

        // Filtro por Raridades (Endpoint /pt aceita termos como 'Incomum' ou 'Rara Holo')
        if (rarity != null && !rarity.isEmpty() && !rarity.toLowerCase().startsWith("tod")) {
            if (!first) urlBuilder.append("&");
            urlBuilder.append("rarity=").append(encodeParam(rarity));
            first = false;
        }

        // Filtro por Séries (Na v2v /pt para cartas, 'set.name=like:' traz resultados globais da série)
        if (series != null && !series.isEmpty() && !series.toLowerCase().startsWith("tod")) {
            if (!first) urlBuilder.append("&");
            urlBuilder.append("set.name=like:").append(encodeParam(series));
            first = false;
        }

        return fetchCardsFromUrl(urlBuilder.toString());
    }

    /**
     * Busca cards por nome usando o novo motor de busca genérico.
     */
    public List<com.pokemontcg.model.Card> searchByName(String name) {
        return searchCards(name, null, null, null, null, null);
    }

    /**
     * Busca cards baseados na série usando filtro de servidor.
     */
    public List<com.pokemontcg.model.Card> searchBySeries(String seriesQuery) {
        // Na API v2, filtros de série podem ser feitos via ?set.name=... ou similar
        String url = baseUrl + "/cards?set.name=like:" + encodeParam(seriesQuery);
        return fetchCardsFromUrl(url);
    }

    /**
     * Busca detalhes completos de um card pelo ID.
     */
    public com.pokemontcg.model.Card findById(String cardId) {
        String url = baseUrl + "/cards/" + cardId;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("[API] findById: status " + response.statusCode() + " para cardId: " + cardId);
                return null;
            }

            JsonNode root = objectMapper.readTree(response.body());
            return mapFullJsonToCard(root);

        } catch (Exception e) {
            throw new ApiException("Erro ao buscar detalhes do card: " + cardId, e);
        }
    }

    private List<com.pokemontcg.model.Card> fetchCardsFromUrl(String url) {
        List<com.pokemontcg.model.Card> cards = new ArrayList<>();
        try {
            System.out.println("[API] Request: " + url);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode root = objectMapper.readTree(response.body());
                if (root.isArray()) {
                    for (JsonNode node : root) {
                        cards.add(mapResumeJsonToCard(node));
                    }
                }
            }
            System.out.println("[API] " + cards.size() + " resultados obtidos.");
            return cards;
        } catch (Exception e) {
            throw new ApiException("Erro na requisição à API: " + url, e);
        }
    }

    private com.pokemontcg.model.Card mapResumeJsonToCard(JsonNode node) {
        com.pokemontcg.model.Card card = new com.pokemontcg.model.Card();
        card.setId(node.path("id").asText());
        card.setLocalId(node.path("localId").asText());
        card.setName(node.path("name").asText());
        
        String image = node.path("image").asText();
        if (image != null && !image.isEmpty() && !image.equals("null")) {
            card.setImage(image + "/low.jpg");
        } else {
            card.setImage("");
        }

        // Mapeia campos adicionais se presentes no resumo
        if (node.has("rarity")) card.setRarity(node.path("rarity").asText());
        if (node.has("stage")) card.setStage(node.path("stage").asText());
        
        if (node.has("types") && node.path("types").isArray()) {
            List<String> types = new java.util.ArrayList<>();
            for (JsonNode t : node.path("types")) {
                types.add(t.asText());
            }
            card.setTypes(types);
        }

        return card;
    }

    private com.pokemontcg.model.Card mapFullJsonToCard(JsonNode node) {
        com.pokemontcg.model.Card card = new com.pokemontcg.model.Card();
        card.setId(node.path("id").asText());
        card.setLocalId(node.path("localId").asText());
        card.setName(node.path("name").asText());
        
        String image = node.path("image").asText();
        if (image != null && !image.isEmpty() && !image.equals("null")) {
            card.setImage(image + "/high.jpg");
        }

        card.setCategory(node.path("category").asText());
        card.setRarity(node.path("rarity").asText());
        card.setHp(node.path("hp").asInt(0));
        card.setStage(node.path("stage").asText("Básico")); // Mapeia o estágio (Básico, Estágio 1, etc.)

        JsonNode setNode = node.path("set");
        if (!setNode.isMissingNode()) {
            card.setSetId(setNode.path("id").asText());
            card.setSetName(setNode.path("name").asText());
            
            // A API TCGdex coloca a série dentro do objeto 'set'
            JsonNode seriesNode = setNode.path("series");
            if (!seriesNode.isMissingNode()) {
                card.setSeriesId(seriesNode.path("id").asText("base"));
                card.setSeriesName(seriesNode.path("name").asText("Expansão"));
            } else {
                card.setSeriesId("base");
                card.setSeriesName("Expansão");
            }
        } else {
            card.setSeriesId("base");
            card.setSeriesName("Expansão");
        }

        List<String> types = new ArrayList<>();
        JsonNode typesNode = node.path("types");
        if (typesNode.isArray()) {
            for (JsonNode t : typesNode) {
                types.add(t.asText());
            }
        }
        card.setTypes(types);

        return card;
    }

    /**
     * Codifica parâmetros de URL corretamente (acentos, espaços, caracteres especiais).
     */
    private String encodeParam(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
