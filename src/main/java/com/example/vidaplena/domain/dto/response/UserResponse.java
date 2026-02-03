package com.example.vidaplena.domain.dto.response;

import com.example.vidaplena.domain.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO para resposta de dados de usuário.
 * 
 * <p>
 * Não expõe informações sensíveis como senha.
 * </p>
 * 
 * @author VIDA PLENA Team
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    /**
     * ID do usuário.
     */
    private UUID id;

    /**
     * Nome do usuário.
     */
    private String name;

    /**
     * Email do usuário.
     */
    private String email;

    /**
     * Perfil de acesso.
     */
    private UserRole role;

    /**
     * Indica se o usuário está ativo.
     */
    private Boolean active;

    /**
     * Data de criação.
     */
    private LocalDateTime createdAt;
}
