package com.pokemontcg.service;

import com.pokemontcg.api.CacheManager;
import com.pokemontcg.api.TcgDexClient;
import com.pokemontcg.model.Card;
import java.util.List;

/**
 * Serviço responsável pela lógica de busca e gerenciamento de cards da API.
 * Comentários explicativos: Esta classe orquestra o uso do CacheManager e 
 * do TcgDexClient para garantir performance e eficiência nas buscas.
 */
public class CardService {

    private final TcgDexClient apiClient;

    public CardService() {
        this.apiClient = new TcgDexClient();
    }

    /**
     * Construtor para injeção de dependência (facilita testes).
     */
    public CardService(TcgDexClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Busca cards utilizando múltiplos filtros (nome, categoria, tipo, series, etc).
     */
    @SuppressWarnings("unchecked")
    public List<Card> searchCards(String name, String category, String type, String rarity, String series, String localId) {
        // Gera uma chave de cache única baseada em todos os parâmetros, incluindo localId
        String cacheKey = String.format("SEARCH_%s_%s_%s_%s_%s_%s", 
            name, category, type, rarity, series, localId).toLowerCase();

        if (CacheManager.has(cacheKey)) {
            return (List<Card>) CacheManager.get(cacheKey);
        }

        List<Card> results = apiClient.searchCards(name, category, type, rarity, series, localId);
        CacheManager.put(cacheKey, results);
        return results;
    }

    /**
     * Busca cards pelo nome (mantido para compatibilidade).
     */
    public List<Card> searchByName(String name) {
        return searchCards(name, null, null, null, null, null);
    }
    /**
     * Busca cards baseados na série (expansion) com uso de cache.
     * Requisito RF-01.3
     */
    @SuppressWarnings("unchecked")
    public List<Card> searchBySeries(String seriesName) {
        if (seriesName == null || seriesName.trim().isEmpty()) {
            return new java.util.ArrayList<>();
        }

        String cacheKey = "SERIES_" + seriesName.toLowerCase();

        // 1. Tenta recuperar do cache
        if (CacheManager.has(cacheKey)) {
            return (List<Card>) CacheManager.get(cacheKey);
        }

        // 2. Se não estiver no cache, busca na API
        List<Card> results = apiClient.searchBySeries(seriesName);

        // 3. Salva no cache
        CacheManager.put(cacheKey, results);

        return results;
    }

    /**
     * Busca os detalhes completos de um card específico.
     */
    public Card getCardDetails(String cardId) {
        if (cardId == null || cardId.isEmpty()) {
            return null;
        }

        String cacheKey = "CARD_" + cardId;

        // 1. Tenta recuperar do cache
        if (CacheManager.has(cacheKey)) {
            return (Card) CacheManager.get(cacheKey);
        }

        // 2. Se não estiver no cache, busca na API
        Card card = apiClient.findById(cardId);

        // 3. Salva no cache
        if (card != null) {
            CacheManager.put(cacheKey, card);
        }

        return card;
    }
}
