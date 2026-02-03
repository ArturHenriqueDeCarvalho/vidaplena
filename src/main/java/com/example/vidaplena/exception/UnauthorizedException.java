package com.example.vidaplena.exception;

/**
 * Exception lançada quando há falha na autenticação ou autorização.
 * 
 * <p>
 * Retorna HTTP 401 (Unauthorized).
 * </p>
 * 
 * @author VIDA PLENA Team
 * @since 1.0
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
