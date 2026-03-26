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

    private String stage; // Estágio do Pokémon (Básico, Estágio 1, etc.)
    private String trainerType; // Tipo de Treinador (Item, Suporte, Estádio)
    private String flavorText; // Texto de rodapé/descrição
    private List<Attack> attacks; // Lista de ataques
    private List<Ability> abilities; // Lista de habilidades (Poke-Power, etc)
    private String retreatCost; // Custo de Recuo (ex: 2)
    private String weakness; // Fraqueza (ex: Fire x2)
    private String resistance; // Resistência (ex: Water -20)

    // Classes Internas para dados complexos
    public static class Attack {
        private String name;
        private String damage;
        private String description;
        // Getters e Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDamage() { return damage; }
        public void setDamage(String damage) { this.damage = damage; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class Ability {
        private String name;
        private String description;
        private String type;
        // Getters e Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }

    public String getStage() { return stage; }
    public void setStage(String stage) { this.stage = stage; }

    public String getFlavorText() { return flavorText; }
    public void setFlavorText(String flavorText) { this.flavorText = flavorText; }

    public String getTrainerType() { return trainerType; }
    public void setTrainerType(String trainerType) { this.trainerType = trainerType; }

    public List<Attack> getAttacks() { return attacks; }
    public void setAttacks(List<Attack> attacks) { this.attacks = attacks; }

    public List<Ability> getAbilities() { return abilities; }
    public void setAbilities(List<Ability> abilities) { this.abilities = abilities; }

    public String getRetreatCost() { return retreatCost; }
    public void setRetreatCost(String retreatCost) { this.retreatCost = retreatCost; }

    public String getWeakness() { return weakness; }
    public void setWeakness(String weakness) { this.weakness = weakness; }

    public String getResistance() { return resistance; }
    public void setResistance(String resistance) { this.resistance = resistance; }

    @Override
    public String toString() {
        return "Card{" + "name='" + name + '\'' + ", id='" + id + '\'' + '}';
    }
}
