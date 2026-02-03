package com.example.vidaplena.domain.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO para requisição de atualização de atendimento.
 * 
 * <p>
 * Permite atualizar o status, data agendada, médico, especialidade e
 * observações do atendimento.
 * </p>
 * 
 * @author VIDA PLENA Team
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAppointmentRequest {

    /**
     * Código do novo status (ex: "IN_PROGRESS", "COMPLETED").
     */
    @NotBlank(message = "Código do status é obrigatório")
    private String statusCode;

    /**
     * Nova data e hora agendada (opcional, deve ser no futuro se fornecida).
     */
    @Future(message = "Data agendada deve ser no futuro")
    private LocalDateTime scheduledDate;

    /**
     * Novo ID do médico responsável (opcional).
     */
    private UUID doctorId;

    /**
     * Novo ID da especialidade médica (opcional).
     */
    private Long specialtyId;

    /**
     * Observações atualizadas sobre o atendimento.
     */
    @Size(max = 1000, message = "Observações devem ter no máximo 1000 caracteres")
    private String notes;
}
