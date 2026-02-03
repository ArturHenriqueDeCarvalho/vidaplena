package com.example.vidaplena.kafka.producer;

import com.example.vidaplena.domain.dto.event.EntityEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Produtor genérico de eventos Kafka para auditoria de entidades.
 * 
 * <p>
 * Publica eventos de CRUD (Create, Read, Update, Delete) para qualquer entidade
 * do sistema. Utiliza generics para suportar diferentes tipos de DTOs.
 * </p>
 * 
 * <p>
 * <b>Tópicos Kafka:</b>
 * </p>
 * <ul>
 * <li><b>entity-{entityType}-created:</b> Entidade criada</li>
 * <li><b>entity-{entityType}-updated:</b> Entidade atualizada</li>
 * <li><b>entity-{entityType}-deleted:</b> Entidade deletada</li>
 * <li><b>entity-{entityType}-restored:</b> Entidade restaurada</li>
 * </ul>
 * 
 * <p>
 * <b>Exemplos de Tópicos:</b>
 * </p>
 * <ul>
 * <li>entity-user-created</li>
 * <li>entity-appointment-updated</li>
 * <li>entity-medicalspecialty-deleted</li>
 * </ul>
 * 
 * <p>
 * <b>Logs Estruturados:</b> Utiliza emojis para facilitar visualização:
 * </p>
 * <ul>
 * <li>📤 Publicando evento</li>
 * <li>✅ Evento publicado com sucesso</li>
 * <li>❌ Erro ao publicar evento</li>
 * </ul>
 * 
 * <p>
 * <b>Habilitação:</b> Ativo apenas se `kafka.enabled=true` no application.yml
 * </p>
 * 
 * @author VIDA PLENA Team
 * @since 2.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true", matchIfMissing = false)
public class GenericEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Publica um evento genérico no Kafka.
     * 
     * <p>
     * O tópico é construído dinamicamente baseado no tipo de entidade e evento:
     * <code>entity-{entityType}-{eventType}</code>
     * </p>
     * 
     * <p>
     * A chave da mensagem é o ID da entidade, garantindo que eventos da mesma
     * entidade vão para a mesma partição (ordem preservada).
     * </p>
     * 
     * @param <T>   Tipo do DTO da entidade
     * @param event Evento a ser publicado
     * @return CompletableFuture com resultado do envio
     */
    public <T> CompletableFuture<SendResult<String, Object>> publishEvent(EntityEvent<T> event) {
        String topic = buildTopicName(event);

        log.info("📤 Publicando evento: eventId={}, type={}, entity={}, entityId={}, user={}",
                event.getEventId(),
                event.getEventType(),
                event.getEntityType(),
                event.getEntityId(),
                event.getUserId());

        return kafkaTemplate.send(topic, event.getEntityId(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        logSuccess(event, result);
                    } else {
                        logError(event, ex);
                    }
                });
    }

    /**
     * Constrói o nome do tópico baseado no evento.
     * 
     * <p>
     * Formato: <code>entity-{entityType}-{eventType}</code>
     * </p>
     * <p>
     * Exemplos:
     * </p>
     * <ul>
     * <li>entity-user-created</li>
     * <li>entity-appointment-updated</li>
     * </ul>
     * 
     * @param event Evento
     * @return Nome do tópico
     */
    private String buildTopicName(EntityEvent<?> event) {
        return String.format("entity-%s-%s",
                event.getEntityType().toLowerCase(),
                event.getEventType().name().toLowerCase());
    }

    /**
     * Loga sucesso na publicação do evento.
     * 
     * @param event  Evento publicado
     * @param result Resultado do envio
     */
    private void logSuccess(EntityEvent<?> event, SendResult<String, Object> result) {
        log.info("✅ Evento publicado com sucesso: eventId={}, topic={}, partition={}, offset={}",
                event.getEventId(),
                result.getRecordMetadata().topic(),
                result.getRecordMetadata().partition(),
                result.getRecordMetadata().offset());
    }

    /**
     * Loga erro na publicação do evento.
     * 
     * @param event Evento que falhou
     * @param ex    Exceção ocorrida
     */
    private void logError(EntityEvent<?> event, Throwable ex) {
        log.error("❌ Erro ao publicar evento: eventId={}, type={}, entity={}, entityId={}, error={}",
                event.getEventId(),
                event.getEventType(),
                event.getEntityType(),
                event.getEntityId(),
                ex.getMessage(),
                ex);
    }
}
