package com.pokemontcg.export;

import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.pokemontcg.exception.ExportException;
import com.pokemontcg.model.CatalogEntry;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Ferramenta oficial para exportar o catálogo pessoal em formato CSV.
 * Comentários explicativos: Usamos a biblioteca OpenCSV para que o processo de 
 * mapear os nomes das colunas e os dados seja automático.
 */
public class CsvExporter {

    /**
     * Executa a exportação de uma lista para o caminho de destino em CSV.
     */
    public void export(List<CatalogEntry> entries, Path destination) {
        try (Writer writer = Files.newBufferedWriter(destination)) {
            
            // O StatefulBeanToCsvBuilder facilita o mapeamento automático para a planilha
            StatefulBeanToCsv<CatalogEntry> beanToCsv = new StatefulBeanToCsvBuilder<CatalogEntry>(writer)
                    .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .build();

            beanToCsv.write(entries);
            System.out.println("[Export] Catálogo exportado para CSV: " + destination.toAbsolutePath());
            
        } catch (Exception e) {
            throw new ExportException("Não conseguimos salvar seu arquivo CSV (Excel). O caminho é válido ou o arquivo está aberto?", e);
        }
    }
}
