package com.pokemontcg.service;

import com.pokemontcg.model.Card;
import com.pokemontcg.model.CatalogEntry;
import com.pokemontcg.repository.CatalogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class CatalogServiceTest {

    private CatalogRepository stubRepository;
    private CatalogService service;
    private boolean deleteCalled = false;
    private boolean saveCalled = false;

    @BeforeEach
    void setUp() {
        deleteCalled = false;
        saveCalled = false;
        
        // Stub manual para evitar problemas de Mockito no ambiente
        stubRepository = new CatalogRepository() {
            @Override
            public boolean existsByCardId(String cardId) {
                return "swsh1-1".equals(cardId);
            }

            @Override
            public void delete(String cardId) {
                deleteCalled = true;
            }

            @Override
            public void save(CatalogEntry entry) {
                saveCalled = true;
            }

            @Override
            public List<CatalogEntry> findAll() {
                List<CatalogEntry> list = new ArrayList<>();
                list.add(new CatalogEntry());
                return list;
            }
        };
        
        service = new CatalogService(stubRepository);
    }

    @Test
    void deveSalvarNovoCardNoCatalogo() {
        Card card = new Card();
        card.setId("new-card");
        card.setName("Pikachu");
        
        service.addCardToCatalog(card, 1, "Notes");
        
        assertTrue(saveCalled);
    }

    @Test
    void deveRemoverCardComSucesso() {
        service.removeCardFromCatalog("swsh1-1");
        assertTrue(deleteCalled);
    }

    @Test
    void deveListarTodosOsCards() {
        List<CatalogEntry> result = service.getAllCardsInCatalog();
        assertEquals(1, result.size());
    }
}
