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

public class JsonExporterTest {

    @TempDir
    Path tempDir;

    @Test
    void deveExportarParaJsonCorretamente() throws IOException {
        JsonExporter exporter = new JsonExporter();
        Path filePath = tempDir.resolve("catalog.json");
        
        CatalogEntry entry = new CatalogEntry();
        entry.setCardId("test-1");
        entry.setCardName("Test Card");
        entry.setQuantity(5);
        entry.setAddedAt(LocalDateTime.now());
        entry.setUpdatedAt(LocalDateTime.now());
        
        List<CatalogEntry> entries = Collections.singletonList(entry);
        
        exporter.export(entries, filePath);
        
        assertTrue(Files.exists(filePath));
        String content = Files.readString(filePath);
        System.out.println("DEBUG-JSON: " + content);
        assertTrue(content.contains("test-1"));
        assertTrue(content.contains("Test Card"));
        assertTrue(content.contains("\"quantity\""));
        assertTrue(content.contains("5"));
    }
}
