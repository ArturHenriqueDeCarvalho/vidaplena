package com.example.vidaplena.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para criação/atualização de especialidade médica.
 * 
 * @author VIDA PLENA Team
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalSpecialtyRequest {

    /**
     * Código único da especialidade (ex: "GENERAL_PRACTICE", "PEDIATRICS").
     */
    @NotBlank(message = "Código da especialidade é obrigatório")
    @Size(max = 50, message = "Código deve ter no máximo 50 caracteres")
    private String code;

    /**
     * Nome amigável da especialidade (ex: "Clínica Geral", "Pediatria").
     */
    @NotBlank(message = "Nome da especialidade é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String name;

    /**
     * Descrição detalhada da especialidade.
     */
    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    private String description;

    /**
     * Indica se a especialidade está ativa.
     */
    @Builder.Default
    private Boolean active = true;
}
