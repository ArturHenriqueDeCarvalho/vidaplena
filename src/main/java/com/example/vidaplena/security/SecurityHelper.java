package com.example.vidaplena.security;

import com.example.vidaplena.domain.entity.User;
import com.example.vidaplena.exception.UnauthorizedException;
import com.example.vidaplena.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Helper para obter o usuário autenticado.
 * 
 * @author VIDA PLENA Team
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class SecurityHelper {

    private final UserService userService;

    /**
     * Retorna o usuário atualmente autenticado.
     * 
     * @return Usuário autenticado
     * @throws UnauthorizedException se não houver usuário autenticado
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Usuário não autenticado");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            return userService.findByEmail(email);
        }

        throw new UnauthorizedException("Usuário não autenticado");
    }

    /**
     * Retorna o email do usuário autenticado.
     */
    public String getCurrentUserEmail() {
        return getCurrentUser().getEmail();
    }
}
