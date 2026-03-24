package com.pokemontcg.model;

import java.time.LocalDateTime;

/**
 * Representa um card que já faz parte do seu catálogo pessoal.
 * Comentários explicativos: Esta classe é um POJO (Plain Old Java Object) que 
 * mapeia exatamente os campos da tabela 'catalog' no banco de dados.
 */
public class CatalogEntry {
    
    private Integer id;           // Auto-incremento do banco
    private String cardId;        // ID único (ex: swsh1-1) 
    private String cardName;      // Nome amigável do Pokémon
    private String seriesId;      // ID da série (ex: base)
    private String seriesName;    // Nome da série
    private String type;          // Tipo (ex: Fire) 
    private String rarity;        // Raridade (ex: Rare Holo)
    private String imageUrl;      // Link para a imagem
    private int quantity;         // Quantos cards você possui
    private String language;      // Idioma do card (default: 'pt')
    private String notes;         // Suas observações
    private LocalDateTime addedAt; // Quando foi cadastrado
    private LocalDateTime updatedAt; // Última alteração

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getRarity() { return rarity; }
    public void setRarity(String rarity) { this.rarity = rarity; }

    // Construtor vazio (Necessário para frameworks de mapeamento se usados no futuro)
    public CatalogEntry() {
        this.language = "pt";
    }

    // Getters e Setters (As portas de entrada e saída dos dados)
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getCardId() { return cardId; }
    public void setCardId(String cardId) { this.cardId = cardId; }

    public String getCardName() { return cardName; }
    public void setCardName(String cardName) { this.cardName = cardName; }

    public String getSeriesId() { return seriesId; }
    public void setSeriesId(String seriesId) { this.seriesId = seriesId; }

    public String getSeriesName() { return seriesName; }
    public void setSeriesName(String seriesName) { this.seriesName = seriesName; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getAddedAt() { return addedAt; }
    public void setAddedAt(LocalDateTime addedAt) { this.addedAt = addedAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "CatalogEntry{" + "cardName='" + cardName + '\'' + ", quantity=" + quantity + '}';
    }
}
