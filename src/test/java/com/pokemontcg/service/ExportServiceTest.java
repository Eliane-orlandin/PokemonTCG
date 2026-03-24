package com.pokemontcg.service;

import com.pokemontcg.exception.ExportException;
import com.pokemontcg.export.CsvExporter;
import com.pokemontcg.export.JsonExporter;
import com.pokemontcg.model.CatalogEntry;
import com.pokemontcg.repository.CatalogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ExportServiceTest {

    private ExportService service;
    private CatalogRepository stubRepository;
    private boolean jsonExportCalled = false;
    private boolean csvExportCalled = false;

    @BeforeEach
    void setUp() {
        jsonExportCalled = false;
        csvExportCalled = false;

        stubRepository = new CatalogRepository() {
            @Override
            public List<CatalogEntry> findAll() {
                // Simula catálogo não vazio por padrão para evitar falha no export
                List<CatalogEntry> list = new ArrayList<>();
                list.add(new CatalogEntry());
                return list;
            }
        };

        JsonExporter jsonExporter = new JsonExporter() {
            @Override
            public void export(List<CatalogEntry> entries, Path destination) {
                jsonExportCalled = true;
            }
        };

        CsvExporter csvExporter = new CsvExporter() {
            @Override
            public void export(List<CatalogEntry> entries, Path destination) {
                csvExportCalled = true;
            }
        };

        service = new ExportService(stubRepository, jsonExporter, csvExporter);
    }

    @Test
    void deveChamarExportadorJson() {
        service.exportCatalogToJson(Paths.get("test.json"));
        assertTrue(jsonExportCalled);
    }

    @Test
    void deveChamarExportadorCsv() {
        service.exportCatalogToCsv(Paths.get("test.csv"));
        assertTrue(csvExportCalled);
    }

    @Test
    void deveLancarExcecaoQuandoCatalogoVazio() {
        // Redefinindo o stub para retornar vazio
        CatalogRepository emptyRepo = new CatalogRepository() {
            @Override
            public List<CatalogEntry> findAll() {
                return Collections.emptyList();
            }
        };
        service = new ExportService(emptyRepo, null, null);
        
        assertThrows(ExportException.class, () -> service.exportCatalogToJson(Paths.get("test.json")));
    }
}
