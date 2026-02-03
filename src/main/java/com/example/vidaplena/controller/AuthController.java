package com.example.vidaplena.controller;

import com.example.vidaplena.domain.dto.request.ChangePasswordRequest;
import com.example.vidaplena.domain.dto.request.CreateUserRequest;
import com.example.vidaplena.domain.dto.request.LoginRequest;
import com.example.vidaplena.domain.dto.response.LoginResponse;
import com.example.vidaplena.domain.dto.response.UserResponse;
import com.example.vidaplena.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controller para autenticação e registro de usuários.
 * 
 * @author VIDA PLENA Team
 * @since 1.0
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints para autenticação e registro de usuários")
public class AuthController {

    private final AuthService authService;

    /**
     * Realiza login de um usuário.
     * 
     * @param request Credenciais de login
     * @return Token JWT e dados do usuário
     */
    @PostMapping("/login")
    @Operation(summary = "Login", description = "Autentica um usuário e retorna um token JWT")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Registra um novo usuário.
     * 
     * @param request Dados do novo usuário
     * @return Dados do usuário criado
     */
    @PostMapping("/register")
    @Operation(summary = "Registrar", description = "Registra um novo usuário no sistema")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody CreateUserRequest request) {
        UserResponse response = authService.register(request);
        return ResponseEntity.status(201).body(response);
    }

    /**
     * Altera a senha do usuário autenticado.
     * 
     * @param request        Dados para troca de senha
     * @param authentication Dados de autenticação do usuário
     * @return Resposta sem conteúdo (204)
     */
    @PutMapping("/change-password")
    @Operation(summary = "Alterar Senha", description = "Permite que o usuário autenticado altere sua própria senha", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        authService.changePassword(email, request);
        return ResponseEntity.noContent().build();
    }
}
