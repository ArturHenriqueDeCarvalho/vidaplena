package com.example.vidaplena.kafka;

import com.example.vidaplena.domain.dto.event.AppointmentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Produtor Kafka para eventos de atendimento.
 * 
 * <p>
 * Publica eventos em tópicos Kafka quando atendimentos são criados, atualizados
 * ou removidos.
 * </p>
 * 
 * @author VIDA PLENA Team
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true", matchIfMissing = false)
public class AppointmentEventProducer {

    private final KafkaTemplate<String, AppointmentEvent> kafkaTemplate;

    private static final String TOPIC_CREATED = "appointment-created";
    private static final String TOPIC_UPDATED = "appointment-updated";
    private static final String TOPIC_DELETED = "appointment-deleted";

    /**
     * Publica evento de criação de atendimento.
     * 
     * @param event Dados do evento
     */
    public void publishCreatedEvent(AppointmentEvent event) {
        publishEvent(TOPIC_CREATED, event);
    }

    /**
     * Publica evento de atualização de atendimento.
     * 
     * @param event Dados do evento
     */
    public void publishUpdatedEvent(AppointmentEvent event) {
        publishEvent(TOPIC_UPDATED, event);
    }

    /**
     * Publica evento de remoção de atendimento.
     * 
     * @param event Dados do evento
     */
    public void publishDeletedEvent(AppointmentEvent event) {
        publishEvent(TOPIC_DELETED, event);
    }

    /**
     * Publica um evento em um tópico Kafka.
     * 
     * @param topic Tópico de destino
     * @param event Dados do evento
     */
    private void publishEvent(String topic, AppointmentEvent event) {
        try {
            CompletableFuture<SendResult<String, AppointmentEvent>> future = kafkaTemplate.send(topic,
                    event.getAppointmentId().toString(), event);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Evento publicado com sucesso: topic={}, appointmentId={}, eventType={}",
                            topic, event.getAppointmentId(), event.getEventType());
                } else {
                    log.error("Erro ao publicar evento: topic={}, appointmentId={}, error={}",
                            topic, event.getAppointmentId(), ex.getMessage(), ex);
                }
            });
        } catch (Exception e) {
            log.error("Erro inesperado ao publicar evento: topic={}, error={}", topic, e.getMessage(), e);
        }
    }
}
