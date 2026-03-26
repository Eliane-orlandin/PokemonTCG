package com.pokemontcg.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pokemontcg.model.Card;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TcgDexClient {
    private static final String BASE_URL = "https://api.tcgdex.net/v2/pt";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public TcgDexClient() {
        this.httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public List<Card> searchCards(String name) throws Exception {
        return searchCards(name, null, null, null, null, null);
    }

    public List<Card> searchCards(String name, String category, String type, String rarity, String series, String localId) throws Exception {
        StringBuilder url = new StringBuilder(BASE_URL).append("/cards?");
        
        if (name != null && !name.isEmpty()) {
            url.append("name=like:").append(URLEncoder.encode(name, StandardCharsets.UTF_8)).append("&");
        }
        if (category != null && !category.isEmpty()) {
            url.append("category=").append(URLEncoder.encode(category, StandardCharsets.UTF_8)).append("&");
        }
        if (type != null && !type.isEmpty()) {
            url.append("types=").append(URLEncoder.encode(type, StandardCharsets.UTF_8)).append("&");
        }
        if (rarity != null && !rarity.isEmpty()) {
            url.append("rarity=").append(URLEncoder.encode(rarity, StandardCharsets.UTF_8)).append("&");
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url.toString()))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) return new ArrayList<>();

        JsonNode root = objectMapper.readTree(response.body());
        List<Card> cards = new ArrayList<>();
        if (root.isArray()) {
            for (JsonNode node : root) {
                cards.add(mapSimpleJsonToCard(node));
            }
        }
        return cards;
    }

    public List<Card> searchBySeries(String seriesName) throws Exception {
        return searchCards(null, null, null, null, seriesName, null);
    }

    public Card findById(String id) throws Exception {
        return getCardDetails(id);
    }

    public Card getCardDetails(String id) throws Exception {
        String url = BASE_URL + "/cards/" + id;
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            throw new Exception("Erro ao buscar card: " + id);
        }

        JsonNode node = objectMapper.readTree(response.body());
        return mapFullJsonToCard(node);
    }

    private Card mapSimpleJsonToCard(JsonNode node) {
        Card card = new Card();
        card.setId(node.path("id").asText());
        card.setLocalId(node.path("localId").asText());
        card.setName(node.path("name").asText());
        String image = node.path("image").asText();
        if (image != null && !image.isEmpty() && !image.equals("null")) {
            card.setImage(image + "/low.jpg");
        }
        return card;
    }

    private Card mapFullJsonToCard(JsonNode node) {
        Card card = new Card();
        card.setId(node.path("id").asText());
        card.setLocalId(node.path("localId").asText());
        card.setName(node.path("name").asText());
        
        String image = node.path("image").asText();
        if (image != null && !image.isEmpty() && !image.equals("null")) {
            card.setImage(image + "/high.jpg");
        }

        card.setCategory(node.path("category").asText("---"));
        card.setRarity(node.path("rarity").asText("---"));
        
        // Em TCGdex v2: trainerType contém o subtipo (Item, Suporte)
        if (node.has("trainerType")) {
            card.setTrainerType(node.path("trainerType").asText());
        } else {
            card.setTrainerType(node.path("stage").asText("---"));
        }
        card.setStage(card.getTrainerType());

        JsonNode setNode = node.path("set");
        if (!setNode.isMissingNode()) {
            card.setSetId(setNode.path("id").asText());
            card.setSetName(setNode.path("name").asText());
            JsonNode seriesNode = setNode.path("series");
            if (!seriesNode.isMissingNode()) {
                card.setSeriesId(seriesNode.path("id").asText("base"));
                card.setSeriesName(seriesNode.path("name").asText("Expansão"));
            }
        }

        // Mapear HP / PS
        JsonNode hpNode = node.path("hp");
        if (hpNode.isMissingNode()) hpNode = node.path("ps");
        card.setHp(hpNode.asInt(0));

        // Tipos
        List<String> types = new ArrayList<>();
        JsonNode typesNode = node.path("types");
        if (typesNode.isArray()) {
            for (JsonNode t : typesNode) types.add(t.asText());
        }
        card.setTypes(types);

        // Habilidades (abilities / habilidades)
        JsonNode abilitiesNode = node.path("abilities");
        if (abilitiesNode.isMissingNode() || !abilitiesNode.isArray()) abilitiesNode = node.path("habilidades");
        if (abilitiesNode.isArray()) {
            List<Card.Ability> abilities = new ArrayList<>();
            for (JsonNode ab : abilitiesNode) {
                Card.Ability a = new Card.Ability();
                a.setName(ab.path("name").isMissingNode() ? ab.path("nome").asText() : ab.path("name").asText());
                a.setDescription(ab.path("effect").isMissingNode() ? ab.path("efeito").asText() : ab.path("effect").asText());
                abilities.add(a);
            }
            card.setAbilities(abilities);
        }

        // Ataques (attacks / ataques)
        JsonNode attacksNode = node.path("attacks");
        if (attacksNode.isMissingNode() || !attacksNode.isArray()) attacksNode = node.path("ataques");
        if (attacksNode.isArray()) {
            List<Card.Attack> attacks = new ArrayList<>();
            for (JsonNode atk : attacksNode) {
                Card.Attack a = new Card.Attack();
                a.setName(atk.path("name").isMissingNode() ? atk.path("nome").asText() : atk.path("name").asText());
                a.setDamage(atk.path("damage").isMissingNode() ? atk.path("dano").asText() : atk.path("damage").asText());
                a.setDescription(atk.path("effect").isMissingNode() ? atk.path("efeito").asText() : atk.path("effect").asText());
                attacks.add(a);
            }
            card.setAttacks(attacks);
        }

        // Fraqueza
        JsonNode weakNode = node.path("weaknesses");
        if (weakNode.isMissingNode()) weakNode = node.path("weakness");
        if (weakNode.isMissingNode()) weakNode = node.path("fraquezas");
        if (weakNode.isArray() && weakNode.size() > 0) {
            JsonNode w = weakNode.get(0);
            card.setWeakness(w.path("type").asText() + " " + w.path("value").asText());
        }

        // Resistência
        JsonNode resNode = node.path("resistances");
        if (resNode.isMissingNode()) resNode = node.path("resistance");
        if (resNode.isMissingNode()) resNode = node.path("resistencias");
        if (resNode.isArray() && resNode.size() > 0) {
            JsonNode r = resNode.get(0);
            card.setResistance(r.path("type").asText() + " " + r.path("value").asText());
        }
        
        // Recuo
        JsonNode retreatNode = node.path("retreat");
        if (retreatNode.isMissingNode()) retreatNode = node.path("recuo");
        card.setRetreatCost(retreatNode.asText());

        // Flavor Text
        if (node.has("effect")) {
            card.setFlavorText(node.path("effect").asText());
        } else if (node.has("description")) {
            card.setFlavorText(node.path("description").asText());
        } else if (node.has("flavorText")) {
            card.setFlavorText(node.path("flavorText").asText());
        }

        return card;
    }
}
