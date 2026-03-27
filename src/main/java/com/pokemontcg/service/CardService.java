package com.pokemontcg.service;

import com.pokemontcg.api.CacheManager;
import com.pokemontcg.api.TcgDexClient;
import com.pokemontcg.model.Card;
import java.util.ArrayList;
import java.util.List;

/**
 * Serviço responsável pela lógica de busca e gerenciamento de cards da API.
 */
public class CardService {

    private final TcgDexClient apiClient;

    public CardService() {
        this.apiClient = new TcgDexClient();
    }

    public CardService(TcgDexClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Busca cards utilizando múltiplos filtros.
     */
    @SuppressWarnings("unchecked")
    public List<Card> searchCards(String name, String category, String type, String rarity, String series, String localId) {
        String cacheKey = String.format("SEARCH_%s_%s_%s_%s_%s_%s", 
            name, category, type, rarity, series, localId).toLowerCase();

        if (CacheManager.has(cacheKey)) {
            return (List<Card>) CacheManager.get(cacheKey);
        }

        try {
            List<Card> results = apiClient.searchCards(name, category, type, rarity, series, localId);
            CacheManager.put(cacheKey, results);
            return results;
        } catch (Exception e) {
            System.err.println("[CardService] Erro na busca: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Card> searchByName(String name) {
        return searchCards(name, null, null, null, null, null);
    }

    @SuppressWarnings("unchecked")
    public List<Card> searchBySeries(String seriesName) {
        if (seriesName == null || seriesName.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String cacheKey = "SERIES_" + seriesName.toLowerCase();

        if (CacheManager.has(cacheKey)) {
            return (List<Card>) CacheManager.get(cacheKey);
        }

        try {
            List<Card> results = apiClient.searchBySeries(seriesName);
            CacheManager.put(cacheKey, results);
            return results;
        } catch (Exception e) {
            System.err.println("[CardService] Erro ao buscar por série: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Card getCardDetails(String cardId) {
        if (cardId == null || cardId.isEmpty()) {
            return null;
        }

        String cacheKey = "CARD_" + cardId;

        if (CacheManager.has(cacheKey)) {
            return (Card) CacheManager.get(cacheKey);
        }

        try {
            Card card = apiClient.findById(cardId);
            if (card != null) {
                // Proteção: caso a API retorne o objeto mas o campo name esteja nulo
                if (card.getName() == null) {
                    card.setName("Dados indisponíveis");
                }
                CacheManager.put(cacheKey, card);
                return card;
            }
            return createFallbackCard(cardId);
        } catch (Exception e) {
            System.err.println("[CardService] Erro ao buscar detalhes do card: " + e.getMessage());
            return createFallbackCard(cardId);
        }
    }

    /**
     * Cria um card padrão para evitar crashes na interface quando a API falha.
     */
    private Card createFallbackCard(String id) {
        Card fallback = new Card();
        fallback.setId(id);
        fallback.setName("Dados indisponíveis");
        return fallback;
    }
}
