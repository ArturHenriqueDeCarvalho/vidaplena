package com.example.vidaplena.config;

import com.example.vidaplena.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuração de segurança da aplicação.
 * 
 * <p>
 * Define endpoints públicos, protegidos e configurações de JWT.
 * </p>
 * 
 * @author VIDA PLENA Team
 * @since 1.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(AbstractHttpConfigurer::disable)
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                                // Endpoints públicos
                                                .requestMatchers("/api/auth/**").permitAll()
                                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**",
                                                                "/swagger-ui.html")
                                                .permitAll()
                                                .requestMatchers("/actuator/**").permitAll()

                                                // Endpoints de usuários (apenas ADMIN)
                                                .requestMatchers("/api/users/**").hasRole("ADMIN")

                                                // Endpoints de especialidades
                                                .requestMatchers(HttpMethod.GET, "/api/specialties/**").authenticated()
                                                .requestMatchers(HttpMethod.POST, "/api/specialties").hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.PUT, "/api/specialties/**").hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.DELETE, "/api/specialties/**")
                                                .hasRole("ADMIN")

                                                // Endpoints de status
                                                .requestMatchers(HttpMethod.GET, "/api/status/**").authenticated()
                                                .requestMatchers(HttpMethod.POST, "/api/status").hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.PUT, "/api/status/**").hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.DELETE, "/api/status/**").hasRole("ADMIN")

                                                // Endpoints de atendimentos
                                                .requestMatchers(HttpMethod.GET, "/api/appointments/**").authenticated()
                                                .requestMatchers(HttpMethod.POST, "/api/appointments")
                                                .hasAnyRole("ADMIN", "RECEPTIONIST")
                                                .requestMatchers(HttpMethod.PUT, "/api/appointments/**")
                                                .hasAnyRole("ADMIN", "DOCTOR")
                                                .requestMatchers(HttpMethod.DELETE, "/api/appointments/**")
                                                .hasRole("ADMIN")

                                                // Qualquer outra requisição precisa autenticação
                                                .anyRequest().authenticated())
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        /**
         * Configuração de CORS.
         */
        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:4200", "*"));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(List.of("*"));
                configuration.setAllowCredentials(false);
                configuration.setMaxAge(3600L);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }
}
