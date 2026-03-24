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
        Optional<CatalogEntry> existing = findByCardId(entry.getCardId());
        
        if (existing.isPresent()) {
            CatalogEntry toUpdate = existing.get();
            toUpdate.setQuantity(toUpdate.getQuantity() + entry.getQuantity());
            toUpdate.setUpdatedAt(LocalDateTime.now());
            update(toUpdate);
            System.out.println("[App] Quantidade incrementada para: " + toUpdate.getCardName() + " (Qtd: " + toUpdate.getQuantity() + ")");
            return;
        }

        String sql = "INSERT INTO catalog (card_id, card_name, series_id, series_name, type, rarity, image_url, quantity, language, notes, added_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = this.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            mapEntryToStatement(entry, pstmt, true);
            pstmt.executeUpdate();

            // Pega o ID gerado automaticamente
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    entry.setId(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new DatabaseException("Erro ao salvar o card no catálogo pessoal: " + entry.getCardName(), e);
        }
    }

    /**
     * Atualiza os dados de um card existente (quantidade e notas).
     */
    public void update(CatalogEntry entry) {
        String sql = "UPDATE catalog SET card_name = ?, series_id = ?, series_name = ?, type = ?, rarity = ?, image_url = ?, quantity = ?, language = ?, notes = ?, updated_at = ? " +
                     "WHERE card_id = ?";

        try (Connection conn = this.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, entry.getCardName());
            pstmt.setString(2, entry.getSeriesId());
            pstmt.setString(3, entry.getSeriesName());
            pstmt.setString(4, entry.getType());
            pstmt.setString(5, entry.getRarity());
            pstmt.setString(6, entry.getImageUrl());
            pstmt.setInt(7, entry.getQuantity());
            pstmt.setString(8, entry.getLanguage());
            pstmt.setString(9, entry.getNotes());
            pstmt.setString(10, LocalDateTime.now().format(DATE_FORMATTER));
            pstmt.setString(11, entry.getCardId());

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
        pstmt.setString(7, entry.getImageUrl());
        pstmt.setInt(8, entry.getQuantity());
        pstmt.setString(9, entry.getLanguage());
        pstmt.setString(10, entry.getNotes());
        
        String now = LocalDateTime.now().format(DATE_FORMATTER);
        if (isInsert) {
            pstmt.setString(11, now); // added_at
            pstmt.setString(12, now); // updated_at
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
        entry.setImageUrl(rs.getString("image_url"));
        entry.setQuantity(rs.getInt("quantity"));
        entry.setLanguage(rs.getString("language"));
        entry.setNotes(rs.getString("notes"));
        
        entry.setAddedAt(LocalDateTime.parse(rs.getString("added_at"), DATE_FORMATTER));
        entry.setUpdatedAt(LocalDateTime.parse(rs.getString("updated_at"), DATE_FORMATTER));
        
        return entry;
    }
}
