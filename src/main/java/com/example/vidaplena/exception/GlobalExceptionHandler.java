package com.example.vidaplena.exception;

import com.example.vidaplena.domain.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Handler global para tratamento de exceções da aplicação.
 * 
 * <p>
 * Captura exceções lançadas pelos controllers e retorna respostas
 * padronizadas com códigos HTTP apropriados.
 * </p>
 * 
 * @author VIDA PLENA Team
 * @since 1.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

        /**
         * Trata exceções de recurso não encontrado.
         * 
         * @param ex      Exception lançada
         * @param request Requisição HTTP
         * @return ResponseEntity com erro 404
         */
        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
                        ResourceNotFoundException ex,
                        HttpServletRequest request) {

                ErrorResponse error = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.NOT_FOUND.value())
                                .message(ex.getMessage())
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        /**
         * Trata exceções de regras de negócio.
         * 
         * @param ex      Exception lançada
         * @param request Requisição HTTP
         * @return ResponseEntity com erro 400
         */
        @ExceptionHandler(BusinessException.class)
        public ResponseEntity<ErrorResponse> handleBusinessException(
                        BusinessException ex,
                        HttpServletRequest request) {

                ErrorResponse error = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .message(ex.getMessage())
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        /**
         * Trata exceções de autenticação/autorização.
         * 
         * @param ex      Exception lançada
         * @param request Requisição HTTP
         * @return ResponseEntity com erro 401
         */
        @ExceptionHandler({ UnauthorizedException.class, InvalidTokenException.class })
        public ResponseEntity<ErrorResponse> handleUnauthorizedException(
                        RuntimeException ex,
                        HttpServletRequest request) {

                ErrorResponse error = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.UNAUTHORIZED.value())
                                .message(ex.getMessage())
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        /**
         * Trata exceções de acesso negado (Spring Security).
         * 
         * @param ex      Exception lançada
         * @param request Requisição HTTP
         * @return ResponseEntity com erro 403
         */
        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ErrorResponse> handleAccessDeniedException(
                        AccessDeniedException ex,
                        HttpServletRequest request) {

                ErrorResponse error = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.FORBIDDEN.value())
                                .message("Acesso negado: você não tem permissão para acessar este recurso")
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }

        /**
         * Trata exceções de validação (Bean Validation).
         * 
         * @param ex      Exception lançada
         * @param request Requisição HTTP
         * @return ResponseEntity com erro 400 e detalhes dos campos inválidos
         */
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidationException(
                        MethodArgumentNotValidException ex,
                        HttpServletRequest request) {

                Map<String, String> errors = new HashMap<>();
                ex.getBindingResult().getAllErrors().forEach(error -> {
                        String fieldName = ((FieldError) error).getField();
                        String errorMessage = error.getDefaultMessage();
                        errors.put(fieldName, errorMessage);
                });

                ErrorResponse error = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .message("Erro de validação")
                                .path(request.getRequestURI())
                                .details(errors)
                                .build();

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        /**
         * Trata exceções genéricas não capturadas.
         * 
         * @param ex      Exception lançada
         * @param request Requisição HTTP
         * @return ResponseEntity com erro 500
         */
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGenericException(
                        Exception ex,
                        HttpServletRequest request) {

                ErrorResponse error = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .message("Erro interno do servidor")
                                .path(request.getRequestURI())
                                .build();

                // Log da exception para debugging
                ex.printStackTrace();

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
}
