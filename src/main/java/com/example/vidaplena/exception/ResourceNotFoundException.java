package com.example.vidaplena.exception;

/**
 * Exception lançada quando um recurso não é encontrado.
 * 
 * <p>
 * Retorna HTTP 404 (Not Found).
 * </p>
 * 
 * @author VIDA PLENA Team
 * @since 1.0
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resource, String field, Object value) {
        super(String.format("%s não encontrado(a) com %s: %s", resource, field, value));
    }
}
