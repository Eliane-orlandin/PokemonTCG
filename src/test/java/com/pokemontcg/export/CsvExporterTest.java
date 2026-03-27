package com.pokemontcg.export;

import com.pokemontcg.model.CatalogEntry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de unidade para o exportador CSV.
 * Comentários explicativos: Validamos o formato do cabeçalho e 
 * o uso de vírgulas como separadores padrão para PT-BR.
 */
public class CsvExporterTest {

    @TempDir
    Path tempDir;

    @Test
    void deveExportarParaCsvComCabecalho() throws Exception {
        CsvExporter exporter = new CsvExporter();
        Path filePath = tempDir.resolve("catalog_test.csv");
        
        CatalogEntry entry = new CatalogEntry();
        entry.setCardId("csv-id");
        entry.setCardName("Bulbasaur CSV");
        entry.setSeriesName("Base Set");
        entry.setQuantity(10);
        entry.setAddedAt(LocalDateTime.now());
        entry.setUpdatedAt(LocalDateTime.now());
        
        List<CatalogEntry> entries = Collections.singletonList(entry);
        
        // Execução
        exporter.export(entries, filePath);
        
        // Verificação QA
        assertTrue(Files.exists(filePath));
        String content = Files.readString(filePath);
        
        // CSV deve ter cabeçalho (usando os nomes reais das anotações @CsvBindByName)
        assertTrue(content.contains("Card ID"), "O cabeçalho Card ID deve estar presente.");
        assertTrue(content.contains("Quantidade"), "O cabeçalho Quantidade deve estar presente.");
        assertTrue(content.contains("Nome"), "O cabeçalho Nome deve estar presente.");
        
        // CSV deve ter os dados
        assertTrue(content.contains("csv-id"), "O arquivo CSV deve conter o cardId.");
        assertTrue(content.contains("Bulbasaur CSV"), "O arquivo CSV deve conter o nome.");
    }
}
