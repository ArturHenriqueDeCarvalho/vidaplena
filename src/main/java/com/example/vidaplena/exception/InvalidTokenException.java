package com.example.vidaplena.exception;

/**
 * Exception lançada quando um token JWT é inválido.
 * 
 * <p>
 * Retorna HTTP 401 (Unauthorized).
 * </p>
 * 
 * @author VIDA PLENA Team
 * @since 1.0
 */
public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
