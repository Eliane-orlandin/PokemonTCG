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
 * Testes de unidade para o exportador JSON.
 * Comentários explicativos: Validamos se a serialização Jackson está 
 * configurada corretamente para os tipos de data java.time.
 */
public class JsonExporterTest {

    @TempDir
    Path tempDir;

    @Test
    void deveExportarParaJsonValido() throws Exception {
        JsonExporter exporter = new JsonExporter();
        Path filePath = tempDir.resolve("catalog_test.json");
        
        CatalogEntry entry = new CatalogEntry();
        entry.setCardId("test-id-123");
        entry.setCardName("Pikachu Test");
        entry.setQuantity(5);
        entry.setAddedAt(LocalDateTime.now());
        entry.setUpdatedAt(LocalDateTime.now());
        
        List<CatalogEntry> entries = Collections.singletonList(entry);
        
        // Execução
        exporter.export(entries, filePath);
        
        // Verificação QA
        assertTrue(Files.exists(filePath), "O arquivo JSON deveria ter sido criado.");
        String content = Files.readString(filePath);
        assertTrue(content.contains("test-id-123"), "O JSON deve conter o cardId.");
        assertTrue(content.contains("Pikachu Test"), "O JSON deve conter o cardName.");
    }
}
