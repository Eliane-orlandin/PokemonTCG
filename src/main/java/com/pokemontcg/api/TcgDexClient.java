package com.pokemontcg.api;

import com.pokemontcg.exception.ApiException;
import net.tcgdex.sdk.TCGdex;
import net.tcgdex.sdk.models.CardResume;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Cliente responsável por interagir com o SDK oficial da TCGdex (net.tcgdex.sdk).
 * Comentários explicativos: Atualizado para a versão 2.0.2 com métodos prefixados com 'fetch'
 * e que retornam Arrays convencionais.
 */
public class TcgDexClient {

    private final TCGdex sdk;

    public TcgDexClient() {
        // Inicializa para português
        this.sdk = new TCGdex("pt");
    }

    /**
     * Busca cards pelo nome.
     */
    public List<com.pokemontcg.model.Card> searchByName(String name) {
        try {
            // No SDK 2.0.2, o método retorna um Array: CardResume[] 
            CardResume[] results = sdk.fetchCards(); 
            
            if (results == null) return new java.util.ArrayList<>();

            return Arrays.stream(results)
                .filter(c -> c.getName() != null && c.getName().toLowerCase().contains(name.toLowerCase()))
                .map(this::mapResumeToInternalCard)
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            throw new ApiException("Erro ao buscar cards por nome: " + name, e);
        }
    }

    /**
     * Busca detalhes de um card pelo ID.
     */
    public com.pokemontcg.model.Card findById(String cardId) {
        try {
            // No SDK 2.0.2, o método é fetchCard(id)
            net.tcgdex.sdk.models.Card apiCard = sdk.fetchCard(cardId);
            if (apiCard == null) return null;
            
            return mapFullToInternalCard(apiCard);
        } catch (Exception e) {
            throw new ApiException("Erro ao buscar detalhes do card com ID: " + cardId, e);
        }
    }

    private com.pokemontcg.model.Card mapResumeToInternalCard(CardResume resume) {
        com.pokemontcg.model.Card card = new com.pokemontcg.model.Card();
        card.setId(resume.getId());
        card.setLocalId(resume.getLocalId());
        card.setName(resume.getName());
        card.setImage(resume.getImage() + "/low.jpg");
        return card;
    }

    private com.pokemontcg.model.Card mapFullToInternalCard(net.tcgdex.sdk.models.Card apiCard) {
        com.pokemontcg.model.Card card = new com.pokemontcg.model.Card();
        
        card.setId(apiCard.getId()); 
        card.setLocalId(apiCard.getLocalId());
        card.setName(apiCard.getName());
        card.setImage(apiCard.getImage() + "/high.jpg");
        
        if (apiCard.getCategory() != null) {
            card.setCategory(apiCard.getCategory());
        }
        
        card.setRarity(apiCard.getRarity());
        
        if (apiCard.getSet() != null) {
            card.setSetId(apiCard.getSet().getId());
            card.setSetName(apiCard.getSet().getName());
        }
        
        card.setHp(apiCard.getHp());
        card.setTypes(apiCard.getTypes());
        
        return card;
    }
}
