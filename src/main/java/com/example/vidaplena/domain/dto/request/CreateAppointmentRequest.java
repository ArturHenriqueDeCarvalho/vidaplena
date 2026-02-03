package com.example.vidaplena.domain.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO para requisição de criação de atendimento.
 * 
 * @author VIDA PLENA Team
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAppointmentRequest {

    /**
     * Nome do paciente.
     */
    @NotBlank(message = "Nome do paciente é obrigatório")
    @Size(min = 3, max = 100, message = "Nome do paciente deve ter entre 3 e 100 caracteres")
    private String patient;

    /**
     * ID do médico responsável.
     */
    @NotNull(message = "ID do médico é obrigatório")
    private UUID doctorId;

    /**
     * ID da especialidade médica.
     */
    @NotNull(message = "ID da especialidade é obrigatório")
    private Long specialtyId;

    /**
     * Data e hora agendada (deve ser no futuro).
     */
    @NotNull(message = "Data agendada é obrigatória")
    @Future(message = "Data agendada deve ser no futuro")
    private LocalDateTime scheduledDate;

    /**
     * Observações sobre o atendimento.
     */
    @Size(max = 1000, message = "Observações devem ter no máximo 1000 caracteres")
    private String notes;
}
