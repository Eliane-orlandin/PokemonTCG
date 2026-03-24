package com.pokemontcg.service;

import com.pokemontcg.exception.ExportException;
import com.pokemontcg.export.JsonExporter;
import com.pokemontcg.export.CsvExporter;
import com.pokemontcg.model.CatalogEntry;
import com.pokemontcg.repository.CatalogRepository;
import java.nio.file.Path;
import java.util.List;

/**
 * Serviço responsável por exportar os dados do catálogo para formatos externos.
 * Comentários explicativos: Esta classe orquestra os exportadores específicos 
 * para JSON e CSV.
 */
public class ExportService {

    private final CatalogRepository repository;
    private final JsonExporter jsonExporter;
    private final CsvExporter csvExporter;

    public ExportService() {
        this.repository = new CatalogRepository();
        this.jsonExporter = new JsonExporter();
        this.csvExporter = new CsvExporter();
    }

    /**
     * Exporta todo o catálogo para um arquivo JSON.
     */
    public void exportCatalogToJson(Path destination) {
        List<CatalogEntry> entries = repository.findAll();
        if (entries.isEmpty()) {
            throw new ExportException("Não há nada no catálogo para exportar!");
        }
        jsonExporter.export(entries, destination);
    }

    /**
     * Exporta todo o catálogo para um arquivo CSV (Excel).
     */
    public void exportCatalogToCsv(Path destination) {
        List<CatalogEntry> entries = repository.findAll();
        if (entries.isEmpty()) {
            throw new ExportException("Não há nada no catálogo para exportar!");
        }
        csvExporter.export(entries, destination);
    }
}
