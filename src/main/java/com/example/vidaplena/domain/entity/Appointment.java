package com.example.vidaplena.domain.entity;

import com.example.vidaplena.domain.entity.base.BaseAuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade que representa um atendimento médico.
 * 
 * <p>
 * Um atendimento possui:
 * </p>
 * <ul>
 * <li>Paciente (nome)</li>
 * <li>Médico responsável (relacionamento com User)</li>
 * <li>Status atual (relacionamento com AppointmentStatus)</li>
 * <li>Data e hora agendada</li>
 * <li>Observações/notas</li>
 * </ul>
 * 
 * <p>
 * <b>Regras de Negócio:</b>
 * </p>
 * <ul>
 * <li>Data agendada não pode ser no passado</li>
 * <li>Status COMPLETED é imutável (não pode ser alterado)</li>
 * <li>Apenas médicos podem atualizar status para IN_PROGRESS ou COMPLETED</li>
 * </ul>
 * 
 * <p>
 * <b>Soft Delete Automático:</b> Utiliza {@link BaseAuditableEntity} que
 * implementa
 * soft delete via anotações Hibernate. Atendimentos deletados são mantidos para
 * auditoria.
 * </p>
 * 
 * @author VIDA PLENA Team
 * @since 1.0
 */
@Entity
@Table(name = "appointments", indexes = {
        @Index(name = "idx_appointment_doctor", columnList = "doctor_id"),
        @Index(name = "idx_appointment_status", columnList = "status_id"),
        @Index(name = "idx_appointment_scheduled_date", columnList = "scheduled_date")
})
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Appointment extends BaseAuditableEntity {

    /**
     * Identificador único do atendimento (UUID).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * Nome do paciente.
     */
    @NotBlank(message = "Nome do paciente é obrigatório")
    @Column(name = "patient", nullable = false, length = 100)
    private String patient;

    /**
     * Médico responsável pelo atendimento.
     */
    @NotNull(message = "Médico é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false, foreignKey = @ForeignKey(name = "fk_appointment_doctor"))
    private User doctor;

    /**
     * Status atual do atendimento.
     */
    @NotNull(message = "Status é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false, foreignKey = @ForeignKey(name = "fk_appointment_status"))
    private AppointmentStatus status;

    /**
     * Data e hora agendada para o atendimento.
     * Deve ser uma data futura.
     */
    @NotNull(message = "Data agendada é obrigatória")
    @Future(message = "Data agendada deve ser no futuro")
    @Column(name = "scheduled_date", nullable = false)
    private LocalDateTime scheduledDate;

    /**
     * Especialidade médica do atendimento.
     */
    @NotNull(message = "Especialidade é obrigatória")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialty_id", nullable = false, foreignKey = @ForeignKey(name = "fk_appointment_specialty"))
    private MedicalSpecialty specialty;

    /**
     * Observações ou notas sobre o atendimento.
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
