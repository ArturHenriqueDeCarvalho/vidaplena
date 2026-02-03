package com.example.vidaplena.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para criação/atualização de status de atendimento.
 * 
 * @author VIDA PLENA Team
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentStatusRequest {

    /**
     * Código único do status (ex: "SCHEDULED", "IN_PROGRESS").
     */
    @NotBlank(message = "Código do status é obrigatório")
    @Size(max = 50, message = "Código deve ter no máximo 50 caracteres")
    private String code;

    /**
     * Nome amigável do status (ex: "Atendimento Agendado").
     */
    @NotBlank(message = "Nome do status é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String name;

    /**
     * Descrição detalhada do status.
     */
    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    private String description;

    /**
     * Indica se o status está ativo.
     */
    @Builder.Default
    private Boolean active = true;
}
