package com.pokemontcg.repository;

import com.pokemontcg.exception.DatabaseException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 * Gerente central do banco de dados SQLite.
 * Responsável pela conexão, inicialização (schema) e backups automáticos.
 * Comentários explicativos: Centralizamos a conexão aqui para evitar repetição
 * de código nos repositórios.
 */
public class DatabaseManager {

    // Nome físico do arquivo do banco de dados no computador
    private static final String DB_NAME = "catalog.db";
    private static final String CONNECTION_URL = "jdbc:sqlite:" + DB_NAME;

    /**
     * Inicializa o banco de dados e cria as tabelas se necessário.
     * Este método deve ser chamado no início de aplicação (Main).
     */
    public static void initDatabase() {
        // Passo 1: Realizar o backup para segurança
        performBackup();

        // Passo 2: Executar o schema para criar as tabelas
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            String schema = loadSchema();
            
            // O SQLite executa vários comandos separados por ponto e vírgula
            for (String sql : schema.split(";")) {
                if (!sql.trim().isEmpty()) {
                    stmt.execute(sql);
                }
            }
            
            System.out.println("Banco de dados inicializado com sucesso!");
            
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao inicializar o banco de dados", e);
        }
    }

    /**
     * Cria uma conexão aberta com o banco de dados.
     * Importante: Quem chamar este método DEVE fechar a conexão após usar 
     * (de preferência via try-with-resources).
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(CONNECTION_URL);
    }

    /**
     * Realiza uma cópia simples de backup do arquivo .db atual.
     * Comentário: Mantemos uma cópia .bak para facilitar a recuperação em caso de falha.
     */
    private static void performBackup() {
        Path dbPath = Paths.get(DB_NAME);
        if (Files.exists(dbPath)) {
            try {
                Path backupPath = Paths.get(DB_NAME + ".bak");
                Files.copy(dbPath, backupPath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Backup automático realizado: " + backupPath.toAbsolutePath());
            } catch (Exception e) {
                // Não travamos o app se o backup falhar, apenas avisamos no log
                System.err.println("Aviso: Falha ao criar backup automático: " + e.getMessage());
            }
        }
    }

    /**
     * Carrega o arquivo SQL dos recursos (src/main/resources/db/schema.sql).
     */
    private static String loadSchema() {
        try (InputStream is = DatabaseManager.class.getResourceAsStream("/db/schema.sql")) {
            if (is == null) {
                throw new DatabaseException("Arquivo schema.sql não encontrado nos recursos!");
            }
            try (Scanner s = new Scanner(is).useDelimiter("\\A")) {
                return s.hasNext() ? s.next() : "";
            }
        } catch (Exception e) {
            throw new DatabaseException("Erro ao ler o arquivo schema.sql", e);
        }
    }
}
