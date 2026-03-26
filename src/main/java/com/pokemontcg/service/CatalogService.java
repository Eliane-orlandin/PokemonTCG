package com.pokemontcg.service;

import com.pokemontcg.model.Card;
import com.pokemontcg.model.CatalogEntry;
import com.pokemontcg.repository.CatalogRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Serviço responsável por gerenciar o catálogo pessoal (banco de dados).
 * Comentários explicativos: Esta classe orquestra o uso do CatalogRepository
 * para gerenciar os cards que o usuário decide salvar localmente.
 */
public class CatalogService {

    private final CatalogRepository repository;

    public CatalogService() {
        this.repository = new CatalogRepository();
    }

    /**
     * Construtor para injeção de dependência (facilita testes).
     */
    public CatalogService(CatalogRepository repository) {
        this.repository = repository;
    }

    /**
     * Adiciona um card que foi buscado na API para dentro do catálogo local.
     */
    public void addCardToCatalog(Card card, int quantity, String notes) {
        if (card == null) return;
        
        CatalogEntry entry = new CatalogEntry();
        entry.setCardId(card.getId());
        entry.setCardName(card.getName());
        entry.setSeriesId(card.getSeriesId());
        entry.setSeriesName(card.getSeriesName());
        entry.setImageUrl(card.getImage());
        entry.setType(card.getTypes() != null && !card.getTypes().isEmpty() ? card.getTypes().get(0) : "Colorless");
        entry.setRarity(card.getRarity());
        // Campos obrigatórios que estavam faltando — evita null sobrescrevendo DEFAULTs do SQLite
        entry.setStage(card.getStage() != null ? card.getStage() : "Básico");
        entry.setCategory(card.getCategory() != null ? card.getCategory() : "Pokémon");
        entry.setQuantity(quantity);
        entry.setNotes(notes);
        
        saveEntry(entry);
    }

    /**
     * Salva uma entrada do catálogo delegando ao repositório.
     * O repositório já contém a lógica de 'upsert' (incrementar se existir).
     */
    public void saveEntry(CatalogEntry entry) {
        repository.save(entry);
    }

    /**
     * Atualiza a quantidade ou as notas de um card existente.
     */
    public void updateCardInfo(String cardId, int newQuantity, String newNotes) {
        Optional<CatalogEntry> existing = repository.findByCardId(cardId);
        if (existing.isPresent()) {
            CatalogEntry entry = existing.get();
            entry.setQuantity(Math.max(1, newQuantity));
            entry.setNotes(newNotes);
            entry.setUpdatedAt(LocalDateTime.now());
            repository.update(entry);
        }
    }

    /**
     * Remove o card do catálogo.
     */
    public void removeCardFromCatalog(String cardId) {
        if (repository.existsByCardId(cardId)) {
            repository.delete(cardId);
        }
    }

    /**
     * Retorna todos os cards salvos na sua coleção pessoal.
     */
    public List<CatalogEntry> getAllCardsInCatalog() {
        return repository.findAll();
    }

    /**
     * Traz detalhes de um card específico do catálogo.
     */
    public Optional<CatalogEntry> getCardFromCatalog(String cardId) {
        return repository.findByCardId(cardId);
    }
}
