package com.pokemontcg.repository;

import com.pokemontcg.exception.DatabaseException;
import com.pokemontcg.model.CatalogEntry;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repositório responsável pelo CRUD (Create, Read, Update, Delete) do catálogo.
 * Comentários explicativos: Usamos PreparedStatement para evitar ataques de SQL Injection, 
 * que é uma boa prática essencial mesmo em apps locais.
 */
public class CatalogRepository {

    // O SQLite não tem tipo data, então salvamos como String ISO-8601
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    protected Connection getConnection() throws SQLException {
        return DatabaseManager.getConnection();
    }

    /**
     * Salva um novo card ou incrementa a quantidade se já existir.
     */
    public void save(CatalogEntry entry) {
        try (Connection conn = this.getConnection()) {
            conn.setAutoCommit(false); // Inicia a transação

            try {
                Optional<CatalogEntry> existing = findByCardId(entry.getCardId(), conn);
                
                if (existing.isPresent()) {
                    CatalogEntry toUpdate = existing.get();
                    toUpdate.setQuantity(toUpdate.getQuantity() + entry.getQuantity());
                    toUpdate.setUpdatedAt(LocalDateTime.now());
                    update(toUpdate, conn);
                } else {
                    insert(entry, conn);
                }
                
                conn.commit(); // Salva permanentemente
                System.out.println("[App] Operação de salvamento concluída para: " + entry.getCardName());
                
            } catch (Exception e) {
                conn.rollback(); // Desfaz tudo se der erro no meio
                throw new DatabaseException("Erro ao processar transação para o card: " + entry.getCardName(), e);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Falha crítica no banco de dados", e);
        }
    }

    private void insert(CatalogEntry entry, Connection conn) throws SQLException {
        String sql = "INSERT INTO catalog (card_id, card_name, series_id, series_name, type, rarity, stage, category, image_url, quantity, language, notes, added_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            mapEntryToStatement(entry, pstmt, true);
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    entry.setId(rs.getInt(1));
                }
            }
        }
    }

    private void update(CatalogEntry entry, Connection conn) throws SQLException {
        String sql = "UPDATE catalog SET card_name = ?, series_id = ?, series_name = ?, type = ?, rarity = ?, stage = ?, category = ?, image_url = ?, quantity = ?, language = ?, notes = ?, updated_at = ? " +
                     "WHERE card_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, entry.getCardName());
            pstmt.setString(2, entry.getSeriesId());
            pstmt.setString(3, entry.getSeriesName());
            pstmt.setString(4, entry.getType());
            pstmt.setString(5, entry.getRarity());
            pstmt.setString(6, entry.getStage());
            pstmt.setString(7, entry.getCategory());
            pstmt.setString(8, entry.getImageUrl());
            pstmt.setInt(9, entry.getQuantity());
            pstmt.setString(10, entry.getLanguage());
            pstmt.setString(11, entry.getNotes());
            pstmt.setString(12, LocalDateTime.now().format(DATE_FORMATTER));
            pstmt.setString(13, entry.getCardId());

            pstmt.executeUpdate();
        }
    }

    private Optional<CatalogEntry> findByCardId(String cardId, Connection conn) throws SQLException {
        String sql = "SELECT * FROM catalog WHERE card_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cardId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntry(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Atualiza os dados de um card existente (quantidade e notas).
     */
    public void update(CatalogEntry entry) {
        String sql = "UPDATE catalog SET card_name = ?, series_id = ?, series_name = ?, type = ?, rarity = ?, stage = ?, category = ?, image_url = ?, quantity = ?, language = ?, notes = ?, updated_at = ? " +
                     "WHERE card_id = ?";

        try (Connection conn = this.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, entry.getCardName());
            pstmt.setString(2, entry.getSeriesId());
            pstmt.setString(3, entry.getSeriesName());
            pstmt.setString(4, entry.getType());
            pstmt.setString(5, entry.getRarity());
            pstmt.setString(6, entry.getStage());
            pstmt.setString(7, entry.getCategory());
            pstmt.setString(8, entry.getImageUrl());
            pstmt.setInt(9, entry.getQuantity());
            pstmt.setString(10, entry.getLanguage());
            pstmt.setString(11, entry.getNotes());
            pstmt.setString(12, LocalDateTime.now().format(DATE_FORMATTER));
            pstmt.setString(13, entry.getCardId());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("Erro ao atualizar o card: " + entry.getCardName(), e);
        }
    }

    /**
     * Remove um card do catálogo pelo seu ID da API.
     */
    public void delete(String cardId) {
        String sql = "DELETE FROM catalog WHERE card_id = ?";

        try (Connection conn = this.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cardId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("Erro ao remover o card com ID: " + cardId, e);
        }
    }

    /**
     * Retorna a lista completa de cards no catálogo.
     */
    public List<CatalogEntry> findAll() {
        List<CatalogEntry> entries = new ArrayList<>();
        String sql = "SELECT * FROM catalog ORDER BY card_name ASC";

        try (Connection conn = this.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                entries.add(mapResultSetToEntry(rs));
            }

        } catch (SQLException e) {
            throw new DatabaseException("Erro ao listar o catálogo pessoal", e);
        }
        return entries;
    }

    /**
     * Busca um card específico pelo seu ID da API.
     */
    public Optional<CatalogEntry> findByCardId(String cardId) {
        String sql = "SELECT * FROM catalog WHERE card_id = ?";

        try (Connection conn = this.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cardId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntry(rs));
                }
            }

        } catch (SQLException e) {
            throw new DatabaseException("Erro ao buscar o card com ID: " + cardId, e);
        }
        return Optional.empty();
    }

    /**
     * Verifica se um card já está no catálogo.
     */
    public boolean existsByCardId(String cardId) {
        return findByCardId(cardId).isPresent();
    }

    // --- MÉTODOS AUXILIARES ---

    private void mapEntryToStatement(CatalogEntry entry, PreparedStatement pstmt, boolean isInsert) throws SQLException {
        pstmt.setString(1, entry.getCardId());
        pstmt.setString(2, entry.getCardName());
        pstmt.setString(3, entry.getSeriesId());
        pstmt.setString(4, entry.getSeriesName());
        pstmt.setString(5, entry.getType());
        pstmt.setString(6, entry.getRarity());
        pstmt.setString(7, entry.getStage());
        pstmt.setString(8, entry.getCategory());
        pstmt.setString(9, entry.getImageUrl());
        pstmt.setInt(10, entry.getQuantity());
        pstmt.setString(11, entry.getLanguage());
        pstmt.setString(12, entry.getNotes());
        
        String now = LocalDateTime.now().format(DATE_FORMATTER);
        if (isInsert) {
            pstmt.setString(13, now); // added_at
            pstmt.setString(14, now); // updated_at
        }
    }

    private CatalogEntry mapResultSetToEntry(ResultSet rs) throws SQLException {
        CatalogEntry entry = new CatalogEntry();
        entry.setId(rs.getInt("id"));
        entry.setCardId(rs.getString("card_id"));
        entry.setCardName(rs.getString("card_name"));
        entry.setSeriesId(rs.getString("series_id"));
        entry.setSeriesName(rs.getString("series_name"));
        entry.setType(rs.getString("type"));
        entry.setRarity(rs.getString("rarity"));
        entry.setStage(rs.getString("stage"));
        entry.setCategory(rs.getString("category"));
        entry.setImageUrl(rs.getString("image_url"));
        entry.setQuantity(rs.getInt("quantity"));
        entry.setLanguage(rs.getString("language"));
        entry.setNotes(rs.getString("notes"));
        
        // Proteção contra datas nulas no banco (dados corrompidos ou antigos)
        String addedAtStr = rs.getString("added_at");
        entry.setAddedAt(addedAtStr != null ? LocalDateTime.parse(addedAtStr, DATE_FORMATTER) : LocalDateTime.now());
        
        String updatedAtStr = rs.getString("updated_at");
        entry.setUpdatedAt(updatedAtStr != null ? LocalDateTime.parse(updatedAtStr, DATE_FORMATTER) : LocalDateTime.now());
        
        return entry;
    }
}
