package com.pokemontcg.service;

import com.pokemontcg.model.CatalogEntry;
import com.pokemontcg.repository.CatalogRepository;
import com.pokemontcg.repository.DatabaseManager;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste de Integração: Export Service.
 * Comentários explicativos: Validamos se a geração de arquivos CSV e JSON 
 * está produzindo arquivos reais, com cabeçalhos e dados corretos.
 */
public class ExportServiceIntegrationTest {

    private CatalogRepository repository;
    private ExportService service;
    private Path tempCsvFile;
    private Path tempJsonFile;

    @BeforeAll
    static void setupDatabase() {
        DatabaseManager.setDatabasePath("catalog_export_test.db");
        DatabaseManager.initDatabase();
    }

    @BeforeEach
    void setUp() throws IOException {
        repository = new CatalogRepository();
        service = new ExportService();
        
        // Limpa o banco antes de cada teste
        List<CatalogEntry> all = repository.findAll();
        for (CatalogEntry e : all) {
            repository.delete(e.getCardId());
        }

        // Prepara arquivos temporários para teste
        tempCsvFile = Files.createTempFile("export_test", ".csv");
        tempJsonFile = Files.createTempFile("export_test", ".json");
    }

    @AfterEach
    void tearDown() throws IOException {
        // Remove arquivos temporários
        Files.deleteIfExists(tempCsvFile);
        Files.deleteIfExists(tempJsonFile);
    }

    @Test
    void deveExportarCatalogoParaCsvComSucesso() throws IOException {
        // 1. Prepara dados no banco
        CatalogEntry entry = new CatalogEntry();
        entry.setCardId("bw1-1");
        entry.setCardName("Snivy");
        entry.setQuantity(4);
        entry.setSeriesName("Black & White");
        entry.setSeriesId("bw1");
        entry.setCategory("Pokemon");
        entry.setStage("Basic");
        entry.setRarity("Common");
        repository.save(entry);

        // 2. Executa a exportação
        service.exportCatalogToCsv(tempCsvFile);

        // 3. Valida se o arquivo existe e tem conteúdo
        assertTrue(Files.exists(tempCsvFile), "O arquivo CSV deve ser criado.");
        List<String> lines = Files.readAllLines(tempCsvFile);
        
        // No nosso CsvExporter, o cabeçalho é escrito manualmente na primeira linha
        assertFalse(lines.isEmpty(), "O arquivo não deve estar vazio.");
        assertTrue(lines.get(0).contains("ID"), "O cabeçalho deve conter ID.");
        assertTrue(lines.stream().anyMatch(l -> l.contains("Snivy")), "O nome do card deve estar no CSV.");
        assertTrue(lines.stream().anyMatch(l -> l.contains("4")), "A quantidade deve estar no CSV.");
    }

    @Test
    void deveExportarCatalogoParaJsonComSucesso() throws IOException {
        // 1. Prepara dados
        CatalogEntry entry = new CatalogEntry();
        entry.setCardId("sm1-10");
        entry.setCardName("Butterfree");
        entry.setQuantity(1);
        entry.setSeriesName("Sun & Moon");
        entry.setSeriesId("sm1");
        entry.setCategory("Pokemon");
        entry.setStage("Stage 2");
        entry.setRarity("Rare");
        repository.save(entry);

        // 2. Executa exportação
        service.exportCatalogToJson(tempJsonFile);

        // 3. Valida conteúdo JSON de forma flexível (independente de espaços ou indentação)
        assertTrue(Files.exists(tempJsonFile), "O arquivo JSON deve ser criado.");
        String content = Files.readString(tempJsonFile);
        
        assertTrue(content.contains("Butterfree"), "O JSON deve conter o nome do card.");
        assertTrue(content.contains("cardName"), "O JSON deve conter a chave 'cardName'.");
        assertTrue(content.contains("sm1-10"), "O JSON deve conter o cardId.");
    }
}
