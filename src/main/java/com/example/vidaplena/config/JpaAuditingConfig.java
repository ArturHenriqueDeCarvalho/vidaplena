package com.example.vidaplena.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Configuração para auditoria automática do JPA.
 * 
 * <p>
 * Habilita o preenchimento automático dos campos de auditoria nas entidades
 * que estendem
 * {@link com.example.vidaplena.domain.entity.base.BaseAuditableEntity}:
 * </p>
 * <ul>
 * <li><b>@CreatedBy:</b> Preenchido com email do usuário logado</li>
 * <li><b>@LastModifiedBy:</b> Atualizado com email do usuário logado</li>
 * <li><b>@CreatedDate:</b> Preenchido automaticamente</li>
 * <li><b>@LastModifiedDate:</b> Atualizado automaticamente</li>
 * </ul>
 * 
 * <p>
 * <b>Integração com Spring Security:</b>
 * </p>
 * <ul>
 * <li>Obtém o usuário atual do SecurityContext</li>
 * <li>Se não houver usuário autenticado, usa "SYSTEM"</li>
 * <li>Útil para operações automáticas (DataInitializer, jobs, etc.)</li>
 * </ul>
 * 
 * @author VIDA PLENA Team
 * @since 2.0
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditingConfig {

    /**
     * Provedor de auditor que retorna o usuário atual.
     * 
     * <p>
     * Lógica de resolução:
     * </p>
     * <ol>
     * <li>Se há usuário autenticado: retorna email do usuário</li>
     * <li>Se não há autenticação: retorna "SYSTEM"</li>
     * <li>Se é autenticação anônima: retorna "SYSTEM"</li>
     * </ol>
     * 
     * @return AuditorAware que fornece o nome do usuário atual
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null ||
                    !authentication.isAuthenticated() ||
                    authentication instanceof AnonymousAuthenticationToken) {
                return Optional.of("SYSTEM");
            }

            // Retorna o email do usuário (username no Spring Security)
            return Optional.of(authentication.getName());
        };
    }
}
