package com.pokemontcg.model;

import java.util.List;

/**
 * Representa os dados de um card vindo da API externa.
 * Comentários explicativos: Esta classe serve como um DTO (Data Transfer Object)
 * para exibirmos as informações na tela de busca antes do usuário decidir 
 * salvar no seu catálogo pessoal.
 */
public class Card {
    
    private String id;          // ID único na API (ex: swsh1-1)
    private String localId;     // ID local dentro da coleção
    private String name;        // Nome original ou traduzido
    private String image;       // URL da imagem oficial (PNG/JPG)
    private String category;    // Pokemon, Treinador ou Energia
    private String rarity;      // Comum, Incomum, Ultra Raro, etc.
    private String setId;       // Código da coleção (ex: swsh1)
    private String setName;     // Nome da coleção (ex: Espada e Escudo)
    private String seriesId;    // Código da série (ex: base)
    private String seriesName;  // Nome da série (ex: Base)
    private Integer hp;         // Pontos de vida (opcional, pode ser nulo)
    private List<String> types; // Tipos (Fogo, Água, etc.)

    // Construtores
    public Card() {}

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getLocalId() { return localId; }
    public void setLocalId(String localId) { this.localId = localId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getRarity() { return rarity; }
    public void setRarity(String rarity) { this.rarity = rarity; }

    public String getSetId() { return setId; }
    public void setSetId(String setId) { this.setId = setId; }

    public String getSetName() { return setName; }
    public void setSetName(String setName) { this.setName = setName; }

    public String getSeriesId() { return seriesId; }
    public void setSeriesId(String seriesId) { this.seriesId = seriesId; }

    public String getSeriesName() { return seriesName; }
    public void setSeriesName(String seriesName) { this.seriesName = seriesName; }

    public Integer getHp() { return hp; }
    public void setHp(Integer hp) { this.hp = hp; }

    public List<String> getTypes() { return types; }
    public void setTypes(List<String> types) { this.types = types; }

    @Override
    public String toString() {
        return "Card{" + "name='" + name + '\'' + ", id='" + id + '\'' + '}';
    }
}
