package com.pokemontcg.model;

import com.opencsv.bean.CsvBindByName;
import java.time.LocalDateTime;

/**
 * Representa um card que já faz parte do seu catálogo pessoal.
 */
public class CatalogEntry {
    
    @CsvBindByName(column = "ID")
    private Integer id;

    @CsvBindByName(column = "Card ID")
    private String cardId;

    @CsvBindByName(column = "Nome")
    private String cardName;

    @CsvBindByName(column = "Série")
    private String seriesName;

    @CsvBindByName(column = "Tipo")
    private String type;

    @CsvBindByName(column = "Raridade")
    private String rarity;

    @CsvBindByName(column = "Estágio")
    private String stage;

    @CsvBindByName(column = "Categoria")
    private String category;

    @CsvBindByName(column = "Quantidade")
    private int quantity;

    @CsvBindByName(column = "Idioma")
    private String language;

    @CsvBindByName(column = "Observações")
    private String notes;

    @CsvBindByName(column = "Data de Adição")
    private LocalDateTime addedAt;

    @CsvBindByName(column = "Última Atualização")
    private LocalDateTime updatedAt;
    
    private String seriesId;
    private String localId;
    private String imageUrl;
    private String trainerType;

    public CatalogEntry() {
        this.language = "pt";
    }

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getCardId() { return cardId; }
    public void setCardId(String cardId) { this.cardId = cardId; }

    public String getLocalId() { return localId; }
    public void setLocalId(String localId) { this.localId = localId; }

    public String getCardName() { return cardName; }
    public void setCardName(String cardName) { this.cardName = cardName; }

    public String getSeriesId() { return seriesId; }
    public void setSeriesId(String seriesId) { this.seriesId = seriesId; }

    public String getSeriesName() { return seriesName; }
    public void setSeriesName(String seriesName) { this.seriesName = seriesName; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getRarity() { return rarity; }
    public void setRarity(String rarity) { this.rarity = rarity; }

    public String getStage() { return stage; }
    public void setStage(String stage) { this.stage = stage; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getTrainerType() { return trainerType; }
    public void setTrainerType(String trainerType) { this.trainerType = trainerType; }

    public LocalDateTime getAddedAt() { return addedAt; }
    public void setAddedAt(LocalDateTime addedAt) { this.addedAt = addedAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "CatalogEntry{" + "cardName='" + cardName + '\'' + ", quantity=" + quantity + '}';
    }
}
