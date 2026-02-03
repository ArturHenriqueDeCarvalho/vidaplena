package com.example.vidaplena.kafka;

import com.example.vidaplena.domain.dto.event.AppointmentEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumidor Kafka para eventos de atendimento.
 * 
 * <p>
 * Processa eventos de atendimentos para realizar ações como:
 * </p>
 * <ul>
 * <li>Envio de notificações</li>
 * <li>Auditoria e logging</li>
 * <li>Atualização de métricas</li>
 * <li>Integração com sistemas externos</li>
 * </ul>
 * 
 * @author VIDA PLENA Team
 * @since 1.0
 */
@Component
@Slf4j
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true", matchIfMissing = false)
public class AppointmentEventConsumer {

    /**
     * Consome eventos de criação de atendimento.
     * 
     * @param event Evento recebido
     */
    @KafkaListener(topics = "appointment-created", groupId = "vidaplena-group")
    public void consumeCreatedEvent(AppointmentEvent event) {
        log.info("=== EVENTO RECEBIDO: ATENDIMENTO CRIADO ===");
        log.info("ID: {}", event.getAppointmentId());
        log.info("Paciente: {}", event.getPatient());
        log.info("Médico: {}", event.getDoctorName());
        log.info("Especialidade: {}", event.getSpecialtyName());
        log.info("Data Agendada: {}", event.getScheduledDate());
        log.info("Realizado por: {}", event.getPerformedBy());

        // Aqui você pode adicionar lógica para:
        // - Enviar email/SMS de confirmação para o paciente
        // - Notificar o médico sobre novo atendimento
        // - Registrar em sistema de auditoria
        // - Atualizar dashboard de métricas
    }

    /**
     * Consome eventos de atualização de atendimento.
     * 
     * @param event Evento recebido
     */
    @KafkaListener(topics = "appointment-updated", groupId = "vidaplena-group")
    public void consumeUpdatedEvent(AppointmentEvent event) {
        log.info("=== EVENTO RECEBIDO: ATENDIMENTO ATUALIZADO ===");
        log.info("ID: {}", event.getAppointmentId());
        log.info("Paciente: {}", event.getPatient());
        log.info("Novo Status: {}", event.getStatus());
        log.info("Realizado por: {}", event.getPerformedBy());

        // Aqui você pode adicionar lógica para:
        // - Notificar paciente sobre mudança de status
        // - Enviar lembretes se status mudou para IN_PROGRESS
        // - Registrar histórico de mudanças
    }

    /**
     * Consome eventos de remoção de atendimento.
     * 
     * @param event Evento recebido
     */
    @KafkaListener(topics = "appointment-deleted", groupId = "vidaplena-group")
    public void consumeDeletedEvent(AppointmentEvent event) {
        log.info("=== EVENTO RECEBIDO: ATENDIMENTO REMOVIDO ===");
        log.info("ID: {}", event.getAppointmentId());
        log.info("Paciente: {}", event.getPatient());
        log.info("Realizado por: {}", event.getPerformedBy());

        // Aqui você pode adicionar lógica para:
        // - Notificar paciente sobre cancelamento
        // - Liberar horário do médico
        // - Registrar em sistema de auditoria
    }
}
