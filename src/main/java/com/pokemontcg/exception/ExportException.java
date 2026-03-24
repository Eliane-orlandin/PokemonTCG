package com.pokemontcg.exception;

/**
 * Exceção customizada para erros durante a exportação do catálogo pessoal.
 * Comentários: Usamos esta classe para isolar erros de escrita de arquivos JSON e CSV
 * do restante da aplicação.
 */
public class ExportException extends RuntimeException {
    
    // Construtor com a mensagem de erro que você quer mostrar na interface
    public ExportException(String message) {
        super(message);
    }
    
    // Construtor que passa o erro original (ex: erro no Jackson ou OpenCSV)
    public ExportException(String message, Throwable cause) {
        super(message, cause);
    }
}
