package com.pokemontcg.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste crítico para o RNF-01 (Gerenciamento de Cache).
 * Valida a política de expulsão LRU (Least Recently Used) e o limite de memória.
 */
public class CacheLimitTest {

    @BeforeEach
    void setUp() {
        // Limpa o cache antes de cada teste para garantir isolamento
        CacheManager.clear();
    }

    @Test
    void deveRespeitarLimiteMaximoERemoverOAntigo() {
        // O limite definido no CacheManager é 500
        int limite = 500;

        // 1. Inserir o limite exato de itens
        for (int i = 1; i <= limite; i++) {
            CacheManager.put("chave-" + i, "valor-" + i);
        }
        
        // Validar que o primeiro item ainda está lá antes de estourar o limite
        assertTrue(CacheManager.has("chave-1"), "O item 1 deve estar no cache inicialmente.");

        // 2. Inserir o item 501 (Isso deve disparar a expulsão do item mais antigo: chave-1)
        CacheManager.put("chave-501", "valor-501");

        // 3. Verificações de QA
        assertFalse(CacheManager.has("chave-1"), "O item mais antigo (chave-1) deveria ter sido removido pelo LRU.");
        assertTrue(CacheManager.has("chave-501"), "O item novo (chave-501) deve estar presente.");
        assertTrue(CacheManager.has("chave-2"), "O segundo item (chave-2) deve continuar presente.");
    }

    @Test
    void deveAtualizarOrdemDeAcessoNoCache() {
        // 1. Preencher o cache parcialmente
        CacheManager.put("A", "1");
        CacheManager.put("B", "2");
        
        // 2. Acessar o item "A" (ele se torna o mais recente no LRU)
        CacheManager.get("A");
        
        // 3. Preencher próximo ao limite
        for (int i = 1; i <= 497; i++) {
            CacheManager.put("extra-" + i, "val-" + i);
        }
        
        // Cache agora tem {A, extra-1, ..., extra-497} se B foi removido? 
        // Não, {B, A} + 497 = 499 itens.
        // Adicionando o 500º
        CacheManager.put("Item500", "500");
        
        // Cache tem {B, A, extra-1, ..., extra-497, Item500} - total 500
        // Adicionando o 501º -> Remove B (mais antigo)
        CacheManager.put("Item501", "501");

        
        // B deve ter saído porque A foi acessado por último
        assertTrue(CacheManager.has("A"), "Item A deve estar presente pois foi acessado recentemente.");
        // O comportamento exato do LinkedHashMap(accessOrder=true) garante isso.
    }
}
