package com.example.vidaplena.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para requisição de troca de senha.
 * 
 * @author VIDA PLENA Team
 * @since 1.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para troca de senha")
public class ChangePasswordRequest {

    @NotBlank(message = "Senha atual é obrigatória")
    @Schema(description = "Senha atual do usuário", example = "senhaAtual123")
    private String currentPassword;

    @NotBlank(message = "Nova senha é obrigatória")
    @Size(min = 6, message = "Nova senha deve ter no mínimo 6 caracteres")
    @Schema(description = "Nova senha do usuário", example = "novaSenha456")
    private String newPassword;

    @NotBlank(message = "Confirmação de senha é obrigatória")
    @Schema(description = "Confirmação da nova senha", example = "novaSenha456")
    private String confirmPassword;
}
