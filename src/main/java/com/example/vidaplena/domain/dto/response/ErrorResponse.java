package com.example.vidaplena.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para resposta de erro padronizada.
 * 
 * <p>
 * Utilizado pelo GlobalExceptionHandler para retornar erros
 * de forma consistente em toda a API.
 * </p>
 * 
 * @author VIDA PLENA Team
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    /**
     * Timestamp do erro.
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * Código de status HTTP.
     */
    private Integer status;

    /**
     * Mensagem de erro.
     */
    private String message;

    /**
     * Caminho da requisição que gerou o erro.
     */
    private String path;

    /**
     * Detalhes adicionais do erro (opcional).
     */
    private Object details;
}
