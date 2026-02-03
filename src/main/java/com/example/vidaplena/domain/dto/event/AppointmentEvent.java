package com.example.vidaplena.domain.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO para eventos de atendimento publicados no Kafka.
 * 
 * @author VIDA PLENA Team
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentEvent {

    /**
     * Tipo do evento.
     */
    private EventType eventType;

    /**
     * ID do atendimento.
     */
    private UUID appointmentId;

    /**
     * Nome do paciente.
     */
    private String patient;

    /**
     * Nome do médico.
     */
    private String doctorName;

    /**
     * Especialidade médica.
     */
    private String specialtyName;

    /**
     * Status do atendimento.
     */
    private String status;

    /**
     * Data agendada.
     */
    private LocalDateTime scheduledDate;

    /**
     * Timestamp do evento.
     */
    private LocalDateTime timestamp;

    /**
     * Usuário que realizou a ação.
     */
    private String performedBy;

    /**
     * Tipos de eventos de atendimento.
     */
    public enum EventType {
        CREATED,
        UPDATED,
        DELETED,
        STATUS_CHANGED
    }
}
