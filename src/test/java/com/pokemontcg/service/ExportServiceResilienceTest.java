package com.pokemontcg.service;

import com.pokemontcg.exception.ExportException;
import com.pokemontcg.export.JsonExporter;
import com.pokemontcg.model.CatalogEntry;
import com.pokemontcg.repository.CatalogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * Testes de Resiliência para o ExportService.
 * Comentários explicativos: Validamos se erros de IO ou banco de dados 
 * são convertidos em ExportException amigáveis para o usuário.
 */
@ExtendWith(MockitoExtension.class)
public class ExportServiceResilienceTest {

    @Mock
    private CatalogRepository mockRepository;

    @Mock
    private JsonExporter mockJsonExporter;

    @InjectMocks
    private ExportService service;

    @Test
    void deveLancarErroQuandoCatalogoVazio() {
        // Simula catálogo vazio
        when(mockRepository.findAll()).thenReturn(Collections.emptyList());

        ExportException exception = assertThrows(ExportException.class, () -> {
            service.exportCatalogToJson(Paths.get("test.json"));
        });

        assertTrue(exception.getMessage().contains("nada no catálogo"));
    }

    @Test
    void deveLancarErroQuandoEscritaNoDiscoFalha() {
        // Simula que há dados
        CatalogEntry entry = new CatalogEntry();
        when(mockRepository.findAll()).thenReturn(Collections.singletonList(entry));

        // Simula falha de permissão no exportador
        doThrow(new ExportException("Acesso negado", new RuntimeException()))
                .when(mockJsonExporter).export(any(), any());

        ExportException exception = assertThrows(ExportException.class, () -> {
            service.exportCatalogToJson(Paths.get("/protected/path.json"));
        });

        assertTrue(exception.getMessage().contains("Acesso negado"));
    }
}
