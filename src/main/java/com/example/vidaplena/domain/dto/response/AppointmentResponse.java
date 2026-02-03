package com.example.vidaplena.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO para resposta de dados de atendimento.
 * 
 * @author VIDA PLENA Team
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponse {

    /**
     * ID do atendimento.
     */
    private UUID id;

    /**
     * Nome do paciente.
     */
    private String patient;

    /**
     * Dados do médico responsável.
     */
    private UserResponse doctor;

    /**
     * Status atual do atendimento.
     */
    private AppointmentStatusResponse status;

    /**
     * Especialidade médica do atendimento.
     */
    private SpecialtyResponse specialty;

    /**
     * Data e hora agendada.
     */
    private LocalDateTime scheduledDate;

    /**
     * Observações sobre o atendimento.
     */
    private String notes;

    /**
     * Email do usuário que criou o atendimento.
     * Preenchido automaticamente via @CreatedBy.
     */
    private String createdBy;

    /**
     * Data de criação.
     */
    private LocalDateTime createdAt;

    /**
     * Data da última atualização.
     */
    private LocalDateTime updatedAt;

    /**
     * DTO interno para resposta de status de atendimento.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AppointmentStatusResponse {
        private Long id;
        private String code;
        private String description;
    }

    /**
     * DTO interno para resposta de especialidade médica.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SpecialtyResponse {
        private Long id;
        private String code;
        private String name;
        private String description;
    }
}
