package com.pokemontcg.repository;

import com.pokemontcg.exception.DatabaseException;
import com.pokemontcg.model.CatalogEntry;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de Integração com SQLite Real.
 * Comentários explicativos: Usamos um banco de teste isolado para validar o CRUD.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CatalogRepositoryTest {

    private CatalogRepository repository;
    private static final String TEST_DB = "catalog_test.db";

    @BeforeAll
    void setupSuite() {
        // Redireciona o banco para o arquivo de teste
        DatabaseManager.setDatabasePath(TEST_DB);
        repository = new CatalogRepository();
    }

    @BeforeEach
    void setUp() {
        // Reinicializa o schema antes de cada teste para ter um banco limpo
        DatabaseManager.initDatabase();
    }

    @AfterAll
    void tearDownSuite() throws IOException {
        // Volta para o banco padrão e limpa os arquivos de teste
        DatabaseManager.setDatabasePath("catalog.db");
        Files.deleteIfExists(Paths.get(TEST_DB));
        Files.deleteIfExists(Paths.get(TEST_DB + ".bak1"));
        Files.deleteIfExists(Paths.get(TEST_DB + ".bak2"));
        Files.deleteIfExists(Paths.get(TEST_DB + ".bak3"));
    }

    private CatalogEntry createTestEntry(String cardId, String name) {
        CatalogEntry entry = new CatalogEntry();
        entry.setCardId(cardId);
        entry.setCardName(name);
        entry.setSeriesId("base");    // Campo NOT NULL
        entry.setSeriesName("Base Set"); // Campo NOT NULL
        entry.setQuantity(1);
        entry.setCategory("Pokémon");
        entry.setLanguage("pt"); // Campo NOT NULL com default, mas bom garantir
        return entry;
    }

    @Test
    void deveSalvarEBuscarCardNoBanco() {
        CatalogEntry entry = createTestEntry("test-pikachu", "Pikachu");

        repository.save(entry);

        Optional<CatalogEntry> saved = repository.findByCardId("test-pikachu");
        assertTrue(saved.isPresent());
        assertEquals("Pikachu", saved.get().getCardName());
    }

    @Test
    void deveIncrementarQuantidadeAoSalvarExistente() {
        CatalogEntry p1 = createTestEntry("dup-pikachu", "Pikachu Duplicado");
        p1.setQuantity(2);
        repository.save(p1);

        CatalogEntry p2 = createTestEntry("dup-pikachu", "Pikachu Duplicado");
        p2.setQuantity(3);
        repository.save(p2);

        Optional<CatalogEntry> result = repository.findByCardId("dup-pikachu");
        assertTrue(result.isPresent());
        assertEquals(5, result.get().getQuantity(), "A quantidade deve ser somada (2 + 3 = 5)");
    }

    @Test
    void deveRemoverCardDoBanco() {
        CatalogEntry entry = createTestEntry("to-delete", "Sumir daqui");
        repository.save(entry);

        assertTrue(repository.existsByCardId("to-delete"));

        repository.delete("to-delete");

        assertFalse(repository.existsByCardId("to-delete"));
    }

    @Test
    void deveListarTodosOsCards() {
        CatalogEntry c1 = createTestEntry("c1", "Abra");
        CatalogEntry c2 = createTestEntry("c2", "Zubat");

        repository.save(c1);
        repository.save(c2);

        List<CatalogEntry> all = repository.findAll();
        // Garantimos que a lista contenha pelo menos os dois novos cards
        assertTrue(all.stream().anyMatch(e -> e.getCardName().equals("Abra")));
        assertTrue(all.stream().anyMatch(e -> e.getCardName().equals("Zubat")));
        
        // Verifica se o primeiro da lista (em ordem alfabética) é o Abra
        assertEquals("Abra", all.get(0).getCardName());
    }

    @Test
    void deveRealizarRollbackEmCasoDeFalhaSimulada() {
        // Tenta salvar um card inválido (card_id é NOT NULL no banco)
        CatalogEntry entryInvalida = createTestEntry(null, "Card Sem ID");
        
        // O repositório deve lançar DatabaseException e disparar rollback interno
        assertThrows(DatabaseException.class, () -> {
            repository.save(entryInvalida);
        });

        // Verificamos se o banco continua íntegro e não salvou o card incompleto
        List<CatalogEntry> all = repository.findAll();
        assertTrue(all.stream().noneMatch(e -> "Card Sem ID".equals(e.getCardName())), 
                   "O card não deve ter sido salvo por causa do erro (Rollback acionado)");
    }
}
