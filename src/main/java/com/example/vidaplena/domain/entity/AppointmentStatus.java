package com.example.vidaplena.domain.entity;

import com.example.vidaplena.domain.entity.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Entidade que representa os status possíveis de um atendimento.
 * 
 * <p>
 * <b>Decisão Arquitetural:</b> Status são armazenados em tabela ao invés de
 * Enum
 * para permitir escalabilidade futura. Novos status podem ser adicionados sem
 * necessidade de redeployment da aplicação.
 * </p>
 * 
 * <p>
 * Status padrão do sistema:
 * </p>
 * <ul>
 * <li><b>SCHEDULED:</b> Atendimento agendado (status inicial)</li>
 * <li><b>IN_PROGRESS:</b> Atendimento em andamento</li>
 * <li><b>COMPLETED:</b> Atendimento finalizado (imutável)</li>
 * <li><b>CANCELED:</b> Atendimento cancelado</li>
 * </ul>
 * 
 * <p>
 * <b>Soft Delete Automático:</b> Utiliza {@link BaseEntity} com soft delete via
 * Hibernate.
 * Status deletados são mantidos no banco para preservar integridade
 * referencial.
 * </p>
 * 
 * @author VIDA PLENA Team
 * @since 1.0
 */
@Entity
@Table(name = "appointment_status", uniqueConstraints = {
        @UniqueConstraint(name = "uk_status_code", columnNames = "code")
})
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentStatus extends BaseEntity {

    /**
     * Identificador único do status.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    /**
     * Código único do status (ex: "SCHEDULED", "IN_PROGRESS").
     * Utilizado para referência no código.
     */
    @NotBlank(message = "Código do status é obrigatório")
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    /**
     * Descrição amigável do status (ex: "Atendimento Agendado").
     * Pode ser exibida para o usuário final.
     */
    @NotBlank(message = "Descrição do status é obrigatória")
    @Column(name = "description", nullable = false, length = 100)
    private String description;

    /**
     * Indica se o status está ativo.
     * 
     * @deprecated Use {@link #isDeleted()} da classe base.
     *             Mantido por compatibilidade, mas será removido em versões
     *             futuras.
     */
    @Deprecated
    @Builder.Default
    @Column(name = "active", nullable = false)
    private Boolean active = true;
}
