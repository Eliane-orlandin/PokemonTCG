package com.pokemontcg.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CacheManagerTest {

    @BeforeEach
    void setUp() {
        CacheManager.clear();
    }

    @Test
    void deveRetornarNullQuandoCacheVazio() {
        assertNull(CacheManager.get("inexistente"));
    }

    @Test
    void deveArmazenarERecuperarValor() {
        String key = "testKey";
        String value = "testValue";
        CacheManager.put(key, value);
        
        assertTrue(CacheManager.has(key));
        assertEquals(value, CacheManager.get(key));
    }

    @Test
    void deveSubstituirValorExistenteParaMesmaChave() {
        String key = "testKey";
        CacheManager.put(key, "v1");
        CacheManager.put(key, "v2");
        
        assertEquals("v2", CacheManager.get(key));
    }

    @Test
    void deveLimparCacheCorretamente() {
        CacheManager.put("k1", "v1");
        CacheManager.clear();
        
        assertFalse(CacheManager.has("k1"));
        assertNull(CacheManager.get("k1"));
    }
}
