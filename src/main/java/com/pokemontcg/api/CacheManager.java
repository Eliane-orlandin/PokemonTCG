package com.pokemontcg.api;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Gerenciador de cache simples em memória com limite LRU.
 * Comentários explicativos: O objetivo aqui é evitar chamadas repetidas à API
 * para o mesmo termo de busca durante uma mesma sessão de uso.
 * Limite de 500 entradas para evitar consumo excessivo de memória.
 */
public class CacheManager {

    // Limite máximo de entradas no cache para evitar uso excessivo de memória
    private static final int MAX_ENTRIES = 500;

    // LinkedHashMap com accessOrder=true remove automaticamente as entradas menos acessadas (LRU)
    private static final Map<String, Object> cache = new LinkedHashMap<>(16, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Object> eldest) {
            return size() > MAX_ENTRIES;
        }
    };

    /**
     * Adiciona um item ao cache.
     */
    public static void put(String key, Object value) {
        if (key != null && value != null) {
            cache.put(key, value);
            System.out.println("[Cache] Item salvo: " + key);
        }
    }

    /**
     * Recupera um item do cache.
     * Retorna o objeto se existir, ou nulo se não estiver no depósito.
     */
    public static Object get(String key) {
        if (cache.containsKey(key)) {
            System.out.println("[Cache] Item recuperado do cache: " + key);
            return cache.get(key);
        }
        return null;
    }

    /**
     * Verifica se o item já está no cache.
     */
    public static boolean has(String key) {
        return cache.containsKey(key);
    }

    /**
     * Limpa toda a memória do cache (Útil caso o usuário mude o idioma, por exemplo).
     */
    public static void clear() {
        cache.clear();
        System.out.println("[Cache] Memória de busca limpa.");
    }
}

