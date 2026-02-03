package com.example.vidaplena.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para resposta de login bem-sucedido.
 * 
 * <p>
 * Contém o token JWT e informações do usuário autenticado.
 * </p>
 * 
 * @author VIDA PLENA Team
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    /**
     * Token JWT para autenticação.
     */
    private String token;

    /**
     * Tipo do token (sempre "Bearer").
     */
    @Builder.Default
    private String type = "Bearer";

    /**
     * Tempo de expiração do token em milissegundos.
     */
    private Long expiresIn;

    /**
     * Dados do usuário autenticado.
     */
    private UserResponse user;
}
