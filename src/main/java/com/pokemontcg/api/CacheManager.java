package com.pokemontcg.api;

import java.util.HashMap;
import java.util.Map;

/**
 * Gerenciador de cache simples em memória (HashMap).
 * Comentários explicativos: O objetivo aqui é evitar chamadas repetidas à API
 * para o mesmo termo de busca durante uma mesma sessão de uso.
 */
public class CacheManager {

    // Nosso depósito de dados: a chave é o que você buscou (ex: 'Pikachu'), 
    // e o valor é o resultado que a API nos devolveu.
    private static final Map<String, Object> cache = new HashMap<>();

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
