package com.example.vidaplena.domain.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Classe base para entidades com auditoria automática.
 * 
 * <p>
 * Estende {@link BaseEntity} e adiciona campos de auditoria que são
 * preenchidos automaticamente pelo Spring Data JPA:
 * </p>
 * <ul>
 * <li><b>@CreatedDate:</b> Data de criação (automática)</li>
 * <li><b>@CreatedBy:</b> Usuário que criou (automático via Spring
 * Security)</li>
 * <li><b>@LastModifiedDate:</b> Data da última modificação (automática)</li>
 * <li><b>@LastModifiedBy:</b> Usuário que modificou (automático via Spring
 * Security)</li>
 * </ul>
 * 
 * <p>
 * <b>Rastreabilidade Completa:</b>
 * </p>
 * <ul>
 * <li>Quem criou o registro e quando</li>
 * <li>Quem modificou o registro e quando</li>
 * <li>Quem deletou o registro e quando (via BaseEntity)</li>
 * </ul>
 * 
 * <p>
 * <b>Uso:</b> Entidades que precisam de auditoria completa (User, Appointment,
 * etc.)
 * devem estender esta classe.
 * </p>
 * 
 * @author VIDA PLENA Team
 * @since 2.0
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class BaseAuditableEntity extends BaseEntity {

    /**
     * Data e hora de criação do registro.
     * Preenchido automaticamente pelo Spring Data JPA.
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Usuário que criou o registro.
     * Preenchido automaticamente via Spring Security (email do usuário logado).
     */
    @CreatedBy
    @Column(name = "created_by", length = 100, updatable = false)
    private String createdBy;

    /**
     * Data e hora da última modificação do registro.
     * Atualizado automaticamente pelo Spring Data JPA.
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Usuário que fez a última modificação.
     * Atualizado automaticamente via Spring Security (email do usuário logado).
     */
    @LastModifiedBy
    @Column(name = "updated_by", length = 100)
    private String updatedBy;
}
