package com.pokemontcg.export;

import com.opencsv.CSVWriter;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
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
 * Comentários explicativos: Forçamos a escrita do cabeçalho manualmente para 
 * garantir compatibilidade total com Excel e Google Sheets.
 */
public class CsvExporter {

    /**
     * Executa a exportação de uma lista para o caminho de destino em CSV.
     */
    public void export(List<CatalogEntry> entries, Path destination) {
        try (Writer writer = Files.newBufferedWriter(destination)) {
            
            // Escrita manual do cabeçalho para garantir que ele apareça no início do arquivo
            writer.write("Card ID,Nome,Série,Tipo,Raridade,Quantidade,Data de Adição\n");

            // Configuração da estratégia de mapeamento para os dados
            HeaderColumnNameMappingStrategy<CatalogEntry> strategy = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(CatalogEntry.class);

            StatefulBeanToCsv<CatalogEntry> beanToCsv = new StatefulBeanToCsvBuilder<CatalogEntry>(writer)
                    .withMappingStrategy(strategy)
                    .withSeparator(',')
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .build();

            beanToCsv.write(entries);
            System.out.println("[Export] Catálogo exportado para CSV: " + destination.toAbsolutePath());
            
        } catch (Exception e) {
            throw new ExportException("Não conseguimos salvar seu arquivo CSV. O caminho é válido ou o arquivo está aberto?", e);
        }
    }
}
