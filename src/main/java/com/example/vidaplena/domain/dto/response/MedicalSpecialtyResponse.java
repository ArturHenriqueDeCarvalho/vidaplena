package com.example.vidaplena.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de resposta para especialidade médica.
 * 
 * @author VIDA PLENA Team
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalSpecialtyResponse {

    /**
     * ID da especialidade.
     */
    private Long id;

    /**
     * Código único da especialidade.
     */
    private String code;

    /**
     * Nome amigável da especialidade.
     */
    private String name;

    /**
     * Descrição detalhada da especialidade.
     */
    private String description;

    /**
     * Indica se a especialidade está ativa.
     */
    private Boolean active;
}
