package com.pokemontcg.export;

import com.pokemontcg.model.CatalogEntry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CsvExporterTest {

    @TempDir
    Path tempDir;

    @Test
    void deveExportarParaCsvCorretamente() throws IOException {
        CsvExporter exporter = new CsvExporter();
        Path filePath = tempDir.resolve("catalog.csv");
        
        CatalogEntry entry = new CatalogEntry();
        entry.setCardId("test-csv");
        entry.setCardName("Bulbasaur");
        entry.setSeriesName("Base Set");
        entry.setQuantity(10);
        entry.setAddedAt(LocalDateTime.now());
        entry.setUpdatedAt(LocalDateTime.now());
        
        List<CatalogEntry> entries = Collections.singletonList(entry);
        
        exporter.export(entries, filePath);
        
        assertTrue(Files.exists(filePath));
        String content = Files.readString(filePath);
        System.out.println("DEBUG-CSV: " + content);
        
        String contentUpper = content.toUpperCase();
        assertAll(
            () -> assertTrue(contentUpper.contains("ID"), "Deve conter ID no cabeçalho"),
            () -> assertTrue(contentUpper.contains("NOME"), "Deve conter Nome no cabeçalho"),
            () -> assertTrue(contentUpper.contains("TEST-CSV"), "Deve conter o cardId"),
            () -> assertTrue(contentUpper.contains("BULBASAUR"), "Deve conter o cardName"),
            () -> assertTrue(content.contains("10"), "Deve conter a quantidade")
        );
    }
}
