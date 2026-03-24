package com.pokemontcg.exception;

/**
 * Exceção customizada para tratar erros em operações no banco de dados SQLite.
 * Comentários: Usamos esta classe para isolar erros de banco de dados do restante
 * da lógica da aplicação.
 */
public class DatabaseException extends RuntimeException {
    
    // Construtor com mensagem explicativa para o log ou UI
    public DatabaseException(String message) {
        super(message);
    }
    
    // Construtor que captura a exceção SQL original (ex: erro JDBC)
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
