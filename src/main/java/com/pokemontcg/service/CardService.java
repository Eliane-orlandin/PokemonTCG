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
     * Busca cards pelo nome, utilizando o cache para evitar chamadas duplicadas.
     */
    @SuppressWarnings("unchecked")
    public List<Card> searchByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return new java.util.ArrayList<>();
        }

        String cacheKey = "SEARCH_" + name.toLowerCase();

        // 1. Tenta recuperar do cache
        if (CacheManager.has(cacheKey)) {
            return (List<Card>) CacheManager.get(cacheKey);
        }

        // 2. Se não estiver no cache, busca na API
        List<Card> results = apiClient.searchByName(name);

        // 3. Salva no cache para a próxima busca
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
