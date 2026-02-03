package com.example.vidaplena.domain.enums;

/**
 * Enum que representa os perfis de acesso do sistema.
 * 
 * <p>
 * Perfis disponíveis:
 * </p>
 * <ul>
 * <li><b>ADMIN:</b> Acesso total ao sistema</li>
 * <li><b>RECEPTIONIST:</b> Pode criar e consultar atendimentos (não pode
 * remover)</li>
 * <li><b>DOCTOR:</b> Pode consultar e atualizar status de atendimentos</li>
 * </ul>
 * 
 * @author VIDA PLENA Team
 * @since 1.0
 */
public enum UserRole {

    /**
     * Administrador do sistema com acesso total.
     */
    ADMIN,

    /**
     * Recepcionista que pode cadastrar e consultar atendimentos.
     */
    RECEPTIONIST,

    /**
     * Médico que pode consultar e atualizar status de atendimentos.
     */
    DOCTOR
}
