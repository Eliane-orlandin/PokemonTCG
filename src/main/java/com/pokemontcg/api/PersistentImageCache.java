package com.pokemontcg.api;

import javafx.scene.image.Image;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Utilitário de Cache Persistente para Imagens.
 * Salva as imagens baixadas em uma pasta local para evitar downloads repetidos.
 */
public class PersistentImageCache {

    private static final String CACHE_DIR = System.getProperty("user.home") + File.separator + ".pokemontcg" + File.separator + "cache";

    static {
        // Cria o diretório de cache se não existir
        try {
            Files.createDirectories(Paths.get(CACHE_DIR));
        } catch (Exception e) {
            System.err.println("Erro ao criar diretório de cache: " + e.getMessage());
        }
    }

    /**
     * Carrega uma imagem do cache local ou baixa da internet se não existir.
     * @param url A URL original da imagem.
     * @param width Largura desejada.
     * @param height Altura desejada.
     * @return Objeto Image do JavaFX.
     */
    public static Image getImage(String url, double width, double height) {
        if (url == null || url.isEmpty()) return null;

        // Gera um nome de arquivo único baseado no ID da carta (extraído da URL)
        // URL Exemplo: https://api.tcgdex.net/v2/pt/cards/swsh3.5-56/low.jpg
        String fileName = generateFileName(url);
        Path cachePath = Paths.get(CACHE_DIR, fileName);

        // 1. Verifica se já existe no disco
        if (Files.exists(cachePath)) {
            // System.out.println("[Cache] Carregando do disco: " + fileName);
            return new Image(cachePath.toUri().toString(), width, height, true, true);
        }

        // 2. Se não existir, baixa e salva
        try {
            // Usa URI ao invés de new URL() (deprecated no Java 20+)
            try (InputStream in = java.net.URI.create(url).toURL().openStream()) {
                Files.copy(in, cachePath, StandardCopyOption.REPLACE_EXISTING);
            }
            
            // Verifica se o arquivo não está vazio ou muito pequeno (corrompido)
            if (Files.size(cachePath) < 1024) { // Menos de 1KB provavelmente é erro
                Files.deleteIfExists(cachePath);
                return new Image(url, width, height, true, false); // Carrega direto da URL sem background para garantir
            }
            
            // Retorna a imagem recém-baixada (sem background loading para evitar ler enquanto o SO trava o arquivo)
            return new Image(cachePath.toUri().toString(), width, height, true, false);
        } catch (Exception e) {
            System.err.println("[Cache] Falha download: " + url + " (" + e.getMessage() + ")");
            // Fallback: tenta carregar direto da URL se falhar ao salvar
            return new Image(url, width, height, true, false);
        }
    }

    /**
     * Extrai um nome de arquivo seguro da URL.
     */
    private static String generateFileName(String url) {
        // Math.abs previne hash negativo que causaria nomes de arquivo inválidos
        String hash = Integer.toHexString(Math.abs(url.hashCode()));
        String name = url.substring(url.lastIndexOf("/") + 1).replace(".jpg", "");
        if (name.length() > 20) name = name.substring(0, 20);
        return name + "_" + hash + ".jpg";
    }
}
