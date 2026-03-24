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
     * Adiciona um card que foi buscado na API para dentro do catálogo local.
     */
    public void addCardToCatalog(Card card, int quantity, String notes) {
        if (card == null) return;
        
        // Se a quantidade for inválida, forçamos para o mínimo de 1
        int validQuantity = Math.max(1, quantity);

        // Verifica se o card já está no catálogo
        if (repository.existsByCardId(card.getId())) {
            // Se já existe, apenas atualizamos a quantidade
            Optional<CatalogEntry> existing = repository.findByCardId(card.getId());
            if (existing.isPresent()) {
                CatalogEntry entry = existing.get();
                entry.setQuantity(entry.getQuantity() + validQuantity);
                repository.update(entry);
            }
        } else {
            // Se é novo, criamos uma nova entrada ('ficha técnica') completa
            CatalogEntry newEntry = new CatalogEntry();
            newEntry.setCardId(card.getId());
            newEntry.setCardName(card.getName());
            newEntry.setSeriesId(card.getSeriesId());
            newEntry.setSeriesName(card.getSeriesName());
            newEntry.setImageUrl(card.getImage());
            newEntry.setQuantity(validQuantity);
            newEntry.setNotes(notes);
            newEntry.setAddedAt(LocalDateTime.now());
            newEntry.setUpdatedAt(LocalDateTime.now());

            repository.save(newEntry);
        }
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
