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
 * Entidade que representa as especialidades médicas disponíveis.
 * 
 * <p>
 * <b>Decisão Arquitetural:</b> Especialidades são armazenadas em tabela ao
 * invés de
 * Enum para permitir escalabilidade. Novas especialidades podem ser adicionadas
 * dinamicamente sem necessidade de redeployment da aplicação.
 * </p>
 * 
 * <p>
 * Especialidades padrão do sistema:
 * </p>
 * <ul>
 * <li><b>GENERAL_PRACTICE:</b> Clínica Geral</li>
 * <li><b>PEDIATRICS:</b> Pediatria</li>
 * <li><b>CARDIOLOGY:</b> Cardiologia</li>
 * </ul>
 * 
 * <p>
 * <b>Soft Delete Automático:</b> Utiliza {@link BaseEntity} com soft delete via
 * Hibernate.
 * Especialidades deletadas são mantidas no banco para preservar integridade
 * referencial.
 * </p>
 * 
 * @author VIDA PLENA Team
 * @since 1.0
 */
@Entity
@Table(name = "medical_specialties", uniqueConstraints = {
        @UniqueConstraint(name = "uk_specialty_code", columnNames = "code")
})
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalSpecialty extends BaseEntity {

    /**
     * Identificador único da especialidade.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    /**
     * Código único da especialidade (ex: "GENERAL_PRACTICE", "PEDIATRICS").
     * Utilizado para referência no código.
     */
    @NotBlank(message = "Código da especialidade é obrigatório")
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    /**
     * Nome amigável da especialidade (ex: "Clínica Geral", "Pediatria").
     * Exibido para o usuário final.
     */
    @NotBlank(message = "Nome da especialidade é obrigatório")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /**
     * Descrição detalhada da especialidade.
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Indica se a especialidade está ativa.
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
