package com.example.vidaplena.exception;

/**
 * Exception lançada quando uma regra de negócio é violada.
 * 
 * <p>
 * Retorna HTTP 400 (Bad Request).
 * </p>
 * 
 * @author VIDA PLENA Team
 * @since 1.0
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
