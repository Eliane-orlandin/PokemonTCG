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
    private static String dbName = "catalog.db";
    private static String connectionUrl = "jdbc:sqlite:" + dbName;

    /**
     * Permite alterar o caminho do banco de dados (útil para testes isolados).
     */
    public static void setDatabasePath(String newPath) {
        dbName = newPath;
        connectionUrl = "jdbc:sqlite:" + dbName;
    }

    /**
     * Inicializa o banco de dados e cria as tabelas se necessário.
     * Este método deve ser chamado no início de aplicação (Main).
     */
    public static void initDatabase() {
        System.out.println("[Database] Iniciando processo de inicialização segura...");
        performBackup();

        try {
            // Passo 1: Criação de tabelas (conexão curta dedicada)
            try (Connection conn = getConnection();
                 Statement stmt = conn.createStatement()) {
                
                String schema = loadSchema();
                StringBuilder currentCommand = new StringBuilder();
                int commandCount = 0;
                
                // Parser robusto: ignora comentários e quebras de linha antes de executar
                for (String line : schema.split("\n")) {
                    String trimmedLine = line.trim();
                    if (trimmedLine.isEmpty() || trimmedLine.startsWith("--")) continue;
                    
                    currentCommand.append(line).append(" ");
                    if (trimmedLine.endsWith(";")) {
                        String sql = currentCommand.toString().trim();
                        if (!sql.isEmpty()) {
                            stmt.execute(sql);
                            commandCount++;
                        }
                        currentCommand.setLength(0);
                    }
                }
                System.out.println("[Database] Tabelas verificadas/criadas. Comandos: " + commandCount);
            }

            // Passo 2: Correções de dados (nova conexão para garantir que o schema foi gravado)
            try (Connection conn = getConnection()) {
                fixLegacyCategories(conn);
            }

            System.out.println("[Database] Inicialização concluída com sucesso! 🚀");
            
        } catch (SQLException e) {
            System.err.println("[Database] ERRO DE SCHEMA: " + e.getMessage());
            // Se o erro for "no such table", o SQLite pode estar em estado inconsistente
            if (e.getMessage().contains("no such table")) {
                System.err.println("[Database] Alerta: Inconsistência detectada. Tente remover o arquivo catalog.db e reiniciar.");
            }
            throw new DatabaseException("Falha na estrutura do banco", e);
        }
    }

    /**
     * Cria uma conexão aberta com o banco de dados.
     * Importante: Quem chamar este método DEVE fechar a conexão após usar 
     * (de preferência via try-with-resources).
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(connectionUrl);
    }

    /**
     * Corrigir categorias de cards antigos que foram salvos incorretamente como 'Pokémon'. 
     * RNF-01: Integridade dos dados após mudança de regra.
     */
    private static void fixLegacyCategories(Connection conn) {
        System.out.println("[Database] Iniciando correção profunda de categorias legadas...");
        
        // Termos que indicam um Treinador (no tipo ou no nome)
        String[] trainerTerms = {"Treinador", "Trainer", "Item", "Suporte", "Apoio", "Estádio", "Stadium", 
                                "Bola", "Ball", "Poção", "Potion", "Pesquisa", "Research", "Substituição", "Switch",
                                "Martelo", "Hammer", "Maca", "Recuperação", "Energy Retrieval"};
        
        // Termos que indicam uma Energia
        String[] energyTerms = {"Energia", "Energy"};

        try (Statement stmt = conn.createStatement()) {
            // 1. Corrigir Treinadores baseados em Type OU Card Name
            for (String term : trainerTerms) {
                // Atualiza se achar no tipo
                stmt.execute("UPDATE catalog SET category = 'Treinador' WHERE category IN ('Pokémon', '') AND type LIKE '%" + term + "%'");
                // Atualiza se achar no nome
                stmt.execute("UPDATE catalog SET category = 'Treinador' WHERE category IN ('Pokémon', '') AND card_name LIKE '%" + term + "%'");
            }

            // 2. Corrigir Energias baseados em Type OU Card Name
            for (String term : energyTerms) {
                stmt.execute("UPDATE catalog SET category = 'Energia' WHERE category IN ('Pokémon', '') AND type LIKE '%" + term + "%'");
                stmt.execute("UPDATE catalog SET category = 'Energia' WHERE category IN ('Pokémon', '') AND card_name LIKE '%" + term + "%'");
            }
            
            // 3. Casos especiais onde a categoria está vazia e o nome é claramente um Pokémon (default)
            stmt.execute("UPDATE catalog SET category = 'Pokémon' WHERE category IS NULL OR category = ''");
            
            System.out.println("[Database] Correção profunda finalizada!");
        } catch (SQLException e) {
            System.err.println("[Database] Erro ao corrigir categorias: " + e.getMessage());
        }
    }

    /**
     * Realiza backups rotativos (mantém os últimos 3).
     * Requisito RNF-06: Cópia automática mantendo os últimos 3 backups.
     */
    private static void performBackup() {
        Path dbPath = Paths.get(dbName);
        if (!Files.exists(dbPath)) return;

        try {
            // Rotacionar backups existentes: 2 -> 3, 1 -> 2
            for (int i = 2; i >= 1; i--) {
                Path oldSource = Paths.get(dbName + ".bak" + i);
                if (Files.exists(oldSource)) {
                    Path newDest = Paths.get(dbName + ".bak" + (i + 1));
                    Files.move(oldSource, newDest, StandardCopyOption.REPLACE_EXISTING);
                }
            }

            // Criar o backup mais recente como .bak1
            Path backupPath = Paths.get(dbName + ".bak1");
            Files.copy(dbPath, backupPath, StandardCopyOption.REPLACE_EXISTING);
            
            System.out.println("[Database] Backup rotativo realizado com sucesso (.bak1, .bak2, .bak3)");
        } catch (Exception e) {
            System.err.println("Aviso: Falha ao realizar backup rotativo: " + e.getMessage());
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
