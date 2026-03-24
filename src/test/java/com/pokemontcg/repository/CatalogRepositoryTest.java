package com.pokemontcg.repository;

import com.pokemontcg.model.CatalogEntry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de integração para o repositório usando banco em memória.
 */
public class CatalogRepositoryTest {

    private CatalogRepository repository;
    private Connection keepAliveConn;
    private static final String TEST_DB_URL = "jdbc:sqlite:file:testdb?mode=memory&cache=shared";

    @BeforeEach
    void setUp() throws Exception {
        // Conexão "Keep-Alive" para manter o banco em memória vivo
        keepAliveConn = DriverManager.getConnection(TEST_DB_URL);
        
        try (Statement stmt = keepAliveConn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS catalog");
            stmt.execute("CREATE TABLE catalog (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "card_id TEXT NOT NULL, " +
                    "card_name TEXT NOT NULL, " +
                    "series_id TEXT NOT NULL, " +
                    "series_name TEXT NOT NULL, " +
                    "image_url TEXT, " +
                    "type TEXT, " +
                    "rarity TEXT, " +
                    "quantity INTEGER NOT NULL DEFAULT 1, " +
                    "language TEXT NOT NULL DEFAULT 'pt', " +
                    "notes TEXT, " +
                    "added_at TEXT NOT NULL, " +
                    "updated_at TEXT NOT NULL)");
        }

        repository = new CatalogRepository() {
            @Override
            protected Connection getConnection() throws java.sql.SQLException {
                return DriverManager.getConnection(TEST_DB_URL); 
            }
        };
    }

    @AfterEach
    void tearDown() throws Exception {
        if (keepAliveConn != null) {
            keepAliveConn.close();
        }
    }

    @Test
    void deveSalvarERecuperarCard() {
        CatalogEntry entry = new CatalogEntry();
        entry.setCardId("swsh12-1");
        entry.setCardName("Pikachu");
        entry.setSeriesId("swsh12");
        entry.setSeriesName("Silver Tempest");
        entry.setQuantity(1);
        entry.setAddedAt(LocalDateTime.now());
        entry.setUpdatedAt(LocalDateTime.now());

        repository.save(entry);

        Optional<CatalogEntry> result = repository.findByCardId("swsh12-1");
        assertTrue(result.isPresent());
        assertEquals("Pikachu", result.get().getCardName());
    }

    @Test
    void deveIncrementarQuantidadeAoSalvarDuplicado() {
        CatalogEntry entry = new CatalogEntry();
        entry.setCardId("swsh12-1");
        entry.setCardName("Pikachu");
        entry.setSeriesId("swsh12");
        entry.setSeriesName("Silver Tempest");
        entry.setQuantity(2);
        entry.setAddedAt(LocalDateTime.now());
        entry.setUpdatedAt(LocalDateTime.now());

        repository.save(entry); // Primeira vez: Salva 2
        
        CatalogEntry duplicate = new CatalogEntry();
        duplicate.setCardId("swsh12-1");
        duplicate.setQuantity(3);
        
        repository.save(duplicate); // Segunda vez: Incrementa +3

        Optional<CatalogEntry> result = repository.findByCardId("swsh12-1");
        assertEquals(5, result.get().getQuantity());
    }

    @Test
    void deveDeletarCardCorretamente() {
        CatalogEntry entry = new CatalogEntry();
        entry.setCardId("delete-me");
        entry.setCardName("Target");
        entry.setSeriesId("test");
        entry.setSeriesName("Test");
        entry.setAddedAt(LocalDateTime.now());
        entry.setUpdatedAt(LocalDateTime.now());
        
        repository.save(entry);
        assertTrue(repository.existsByCardId("delete-me"));

        repository.delete("delete-me");
        assertFalse(repository.existsByCardId("delete-me"));
    }

    @Test
    void deveListarTodosOsCards() {
        repository.save(createDummy("1", "A"));
        repository.save(createDummy("2", "B"));
        
        List<CatalogEntry> all = repository.findAll();
        assertEquals(2, all.size());
    }

    private CatalogEntry createDummy(String id, String name) {
        CatalogEntry e = new CatalogEntry();
        e.setCardId(id);
        e.setCardName(name);
        e.setSeriesId("S" + id);
        e.setSeriesName("SN" + id);
        e.setAddedAt(LocalDateTime.now());
        e.setUpdatedAt(LocalDateTime.now());
        return e;
    }
}
