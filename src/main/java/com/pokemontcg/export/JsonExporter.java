package com.pokemontcg.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pokemontcg.exception.ExportException;
import com.pokemontcg.model.CatalogEntry;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Ferramenta oficial para exportar o catálogo pessoal em formato JSON.
 * Comentários explicativos: Usamos a biblioteca Jackson para mapear os 
 * objetos Java para o formato JSON formatado.
 */
public class JsonExporter {

    private final ObjectMapper mapper;

    public JsonExporter() {
        this.mapper = new ObjectMapper();
        // Configuramos para o JSON ficar bonito e legível no bloco de notas
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
        // Ativamos o suporte para datas do Java 8+ (JavaTimeModule)
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    /**
     * Executa a exportação de uma lista para o caminho de destino.
     */
    public void export(List<CatalogEntry> entries, Path destination) {
        try {
            File destFile = destination.toFile();
            mapper.writeValue(destFile, entries);
            System.out.println("[Export] Catálogo exportado para JSON: " + destFile.getAbsolutePath());
        } catch (IOException e) {
            throw new ExportException("Não conseguimos salvar seu arquivo JSON. O caminho é válido?", e);
        }
    }
}
