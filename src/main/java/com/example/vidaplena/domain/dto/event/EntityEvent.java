package com.example.vidaplena.domain.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Evento genérico para auditoria de operações em entidades.
 * 
 * <p>
 * Utiliza generics para suportar qualquer tipo de entidade (User, Appointment,
 * etc.).
 * Publicado no Kafka sempre que uma entidade é criada, atualizada ou deletada.
 * </p>
 * 
 * <p>
 * <b>Estrutura do Evento:</b>
 * </p>
 * <ul>
 * <li><b>eventId:</b> UUID único do evento (rastreabilidade)</li>
 * <li><b>eventType:</b> Tipo da operação (CREATED, UPDATED, DELETED,
 * RESTORED)</li>
 * <li><b>entityType:</b> Nome da entidade (User, Appointment, etc.)</li>
 * <li><b>entityId:</b> ID da entidade afetada</li>
 * <li><b>entityData:</b> Dados da entidade (DTO)</li>
 * <li><b>timestamp:</b> Quando o evento ocorreu</li>
 * <li><b>userId:</b> Email do usuário que executou a ação</li>
 * <li><b>userName:</b> Nome do usuário que executou a ação</li>
 * <li><b>metadata:</b> Dados extras (IP, user agent, etc.)</li>
 * </ul>
 * 
 * <p>
 * <b>Exemplo de Uso:</b>
 * </p>
 * 
 * <pre>
 * EntityEvent&lt;UserResponse&gt; event = EntityEvent.&lt;UserResponse&gt;builder()
 *         .eventType(EventType.CREATED)
 *         .entityType("User")
 *         .entityId(user.getId().toString())
 *         .entityData(userResponse)
 *         .userId("admin@vidaplena.com")
 *         .userName("Administrador")
 *         .build();
 * 
 * eventProducer.publishEvent(event);
 * </pre>
 * 
 * @param <T> Tipo do DTO da entidade
 * @author VIDA PLENA Team
 * @since 2.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EntityEvent<T> {

    /**
     * Identificador único do evento (UUID).
     * Gerado automaticamente se não fornecido.
     */
    @Builder.Default
    private String eventId = UUID.randomUUID().toString();

    /**
     * Tipo do evento (CREATED, UPDATED, DELETED, RESTORED).
     */
    private EventType eventType;

    /**
     * Nome da entidade (User, Appointment, MedicalSpecialty, etc.).
     */
    private String entityType;

    /**
     * ID da entidade afetada (como String).
     */
    private String entityId;

    /**
     * Dados da entidade (DTO).
     * Pode ser UserResponse, AppointmentResponse, etc.
     */
    private T entityData;

    /**
     * Timestamp do evento.
     * Gerado automaticamente se não fornecido.
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * Email do usuário que executou a ação.
     * Obtido do Spring Security Context.
     */
    private String userId;

    /**
     * Nome do usuário que executou a ação.
     */
    private String userName;

    /**
     * Metadados adicionais do evento.
     * Pode conter: IP, user agent, dados anteriores (para UPDATE), etc.
     */
    @Builder.Default
    private Map<String, Object> metadata = new HashMap<>();

    /**
     * Tipos de eventos suportados.
     */
    public enum EventType {
        /**
         * Entidade foi criada.
         */
        CREATED,

        /**
         * Entidade foi atualizada.
         */
        UPDATED,

        /**
         * Entidade foi deletada (soft delete).
         */
        DELETED,

        /**
         * Entidade deletada foi restaurada.
         */
        RESTORED
    }

    /**
     * Adiciona um metadado ao evento.
     * 
     * @param key   Chave do metadado
     * @param value Valor do metadado
     * @return Este evento (para encadeamento)
     */
    public EntityEvent<T> addMetadata(String key, Object value) {
        this.metadata.put(key, value);
        return this;
    }
}
