package com.pokemontcg.service;

import com.pokemontcg.api.CacheManager;
import com.pokemontcg.api.TcgDexClient;
import com.pokemontcg.model.Card;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Testes de Resiliência para o CardService.
 * Comentários explicativos: Garantimos que falhas na API externa 
 * sejam tratadas graciosamente, sem causar crash no aplicativo JavaFX.
 */
@ExtendWith(MockitoExtension.class)
public class CardServiceResilienceTest {

    @Mock
    private TcgDexClient mockApiClient;

    @InjectMocks
    private CardService service;

    @BeforeEach
    void setUp() {
        CacheManager.clear();
    }

    @Test
    void deveRetornarVazioQuandoApiCair() throws Exception {
        // Simula erro crítico de rede (API Offline)
        when(mockApiClient.searchCards(anyString(), any(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException("API indisponível no momento"));

        List<Card> results = service.searchByName("Pikachu");

        assertNotNull(results);
        assertTrue(results.isEmpty(), "Em caso de falha de rede, o sistema deve retornar lista vazia e logar o erro.");
    }

    @Test
    void deveLidarComCardSemNomeDaApi() throws Exception {
        // Simula API retornando um card incompleto (sem nome)
        Card incompleteCard = new Card();
        incompleteCard.setId("id-incompleto");
        incompleteCard.setName(null); // Caso de borda
        
        // Ajustado para o método findById que é usado no CardService.java
        when(mockApiClient.findById(anyString())).thenReturn(incompleteCard);

        Card result = service.getCardDetails("id-incompleto");

        assertNotNull(result);
        assertEquals("id-incompleto", result.getId());
        assertEquals("Dados indisponíveis", result.getName(), "O sistema deve retornar um card de fallback amigável.");
    }
}
