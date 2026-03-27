package com.pokemontcg.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa um Deck de Pokémon TCG.
 * Comentários explicativos: Utilizamos Composição aqui pois um Deck é definido 
 * intrinsecamente pelo conjunto de cartas que o formam.
 */
public class Deck {
    
    private String name;
    private List<Card> cards; // A lista de cartas que compõe o deck

    public Deck(String name) {
        this.name = name;
        // Na composição, o pai (Deck) é responsável por instanciar/gerenciar o ciclo de vida dos filhos
        this.cards = new ArrayList<>();
    }

    /**
     * Adiciona um card ao deck.
     */
    public void addCard(Card card) {
        if (card != null) {
            this.cards.add(card);
        }
    }

    /**
     * Remove um card do deck.
     */
    public void removeCard(Card card) {
        this.cards.remove(card);
    }

    // Getters e Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Card> getCards() {
        return new ArrayList<>(cards); // Retorna cópia para preservar encapsulamento
    }

    @Override
    public String toString() {
        return "Deck{" + "name='" + name + '\'' + ", cardsCount=" + cards.size() + '}';
    }
}
