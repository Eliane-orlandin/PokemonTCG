package com.pokemontcg.exception;

/**
 * Exceção customizada para tratar erros de comunicação com a API TCGdex.
 * Comentário explicativo: Usamos RuntimeException para evitar poluir o código com 'throws' obrigatórios
 * em todos os níveis, tratando o erro onde for mais conveniente na UI.
 */
public class ApiException extends RuntimeException {
    
    // Construtor básico com mensagem de erro
    public ApiException(String message) {
        super(message);
    }
    
    // Construtor que recebe a causa original (ex: um erro de rede real)
    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
