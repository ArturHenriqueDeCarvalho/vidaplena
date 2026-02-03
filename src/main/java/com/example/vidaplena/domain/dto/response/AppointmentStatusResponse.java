package com.example.vidaplena.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de resposta para status de atendimento.
 * 
 * @author VIDA PLENA Team
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentStatusResponse {

    /**
     * ID do status.
     */
    private Long id;

    /**
     * Código único do status.
     */
    private String code;

    /**
     * Nome amigável do status.
     */
    private String name;

    /**
     * Descrição detalhada do status.
     */
    private String description;

    /**
     * Indica se o status está ativo.
     */
    private Boolean active;
}
