package com.pokemontcg.service;

import com.pokemontcg.api.CacheManager;
import com.pokemontcg.api.TcgDexClient;
import com.pokemontcg.model.Card;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CardServiceTest {

    private CardService service;
    private boolean apiCalled = false;

    @BeforeEach
    void setUp() {
        apiCalled = false;
        CacheManager.clear();
        
        TcgDexClient stubClient = new TcgDexClient() {
            @Override
            public List<Card> searchCards(String name, String category, String type, String rarity, String series, String localId) {
                apiCalled = true;
                List<Card> list = new ArrayList<>();
                Card c = new Card();
                c.setName(name);
                list.add(c);
                return list;
            }
        };
        
        service = new CardService(stubClient);
    }

    @Test
    void deveBuscarNaApiQuandoCacheVazio() {
        List<Card> results = service.searchByName("Pikachu");
        
        assertTrue(apiCalled);
        assertEquals(1, results.size());
        assertEquals("Pikachu", results.get(0).getName());
    }

    @Test
    void deveRetornarDoCacheQuandoDisponivel() {
        // Primeira chamada chama a API
        service.searchByName("Pikachu");
        assertTrue(apiCalled);
        
        // Reseta flag
        apiCalled = false;
        
        // Segunda chamada deve usar o cache
        List<Card> results = service.searchByName("Pikachu");
        assertFalse(apiCalled);
        assertEquals("Pikachu", results.get(0).getName());
    }
}
