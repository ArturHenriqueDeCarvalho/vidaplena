package com.example.vidaplena.domain.entity;

import com.example.vidaplena.domain.entity.base.BaseAuditableEntity;
import com.example.vidaplena.domain.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Entidade que representa um usuário do sistema.
 * 
 * <p>
 * Usuários podem ter diferentes perfis (roles) que determinam suas permissões:
 * ADMIN, RECEPTIONIST ou DOCTOR.
 * </p>
 * 
 * <p>
 * <b>Soft Delete Automático:</b> Utiliza {@link BaseAuditableEntity} que
 * implementa
 * soft delete via anotações Hibernate (@SQLDelete, @Where). Ao chamar delete(),
 * o registro não é removido, apenas marcado como deletado.
 * </p>
 * 
 * <p>
 * <b>Auditoria Automática:</b> Campos createdAt, createdBy, updatedAt,
 * updatedBy
 * são preenchidos automaticamente pelo Spring Data JPA.
 * </p>
 * 
 * @author VIDA PLENA Team
 * @since 1.0
 */
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_email", columnNames = "email")
})
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseAuditableEntity {

    /**
     * Identificador único do usuário (UUID).
     * Utiliza UUID para segurança e geração distribuída.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * Nome completo do usuário.
     */
    @NotBlank(message = "Nome é obrigatório")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /**
     * Email do usuário (único no sistema).
     * Utilizado como username para autenticação.
     */
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    /**
     * Senha do usuário (hash BCrypt).
     * Nunca deve ser exposta nas respostas da API.
     */
    @NotBlank(message = "Senha é obrigatória")
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * Perfil de acesso do usuário.
     */
    @NotNull(message = "Perfil é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private UserRole role;

    /**
     * Indica se o usuário está ativo.
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
