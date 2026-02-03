package com.example.vidaplena.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuração de segurança básica.
 * 
 * <p>
 * Define beans necessários para a camada de segurança.
 * </p>
 * 
 * @author VIDA PLENA Team
 * @since 1.0
 */
@Configuration
public class SecurityBeansConfig {

    /**
     * Bean para codificação de senhas usando BCrypt.
     * 
     * @return PasswordEncoder configurado
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
