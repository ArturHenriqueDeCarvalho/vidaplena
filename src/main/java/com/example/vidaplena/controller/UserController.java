package com.example.vidaplena.controller;

import com.example.vidaplena.domain.dto.request.CreateUserRequest;
import com.example.vidaplena.domain.dto.response.UserResponse;
import com.example.vidaplena.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller para gerenciamento de usuários.
 * 
 * <p>
 * Endpoints disponíveis apenas para ADMIN.
 * </p>
 * 
 * @author VIDA PLENA Team
 * @since 1.0
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Gerenciamento de usuários (ADMIN)")
public class UserController {

    private final UserService userService;

    /**
     * Lista todos os usuários ativos.
     * 
     * @return Lista de usuários
     */
    @GetMapping
    @Operation(summary = "Listar usuários", description = "Retorna todos os usuários ativos do sistema")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllActiveUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Busca um usuário por ID.
     * 
     * @param id ID do usuário
     * @return Dados do usuário
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário", description = "Busca um usuário específico por ID")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        UserResponse user = userService.findByIdAsResponse(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Cria um novo usuário.
     * 
     * @param request Dados do usuário
     * @return Dados do usuário criado
     */
    @PostMapping
    @Operation(summary = "Criar usuário", description = "Cria um novo usuário no sistema")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse user = userService.create(request);
        return ResponseEntity.status(201).body(user);
    }

    /**
     * Desativa um usuário (soft delete).
     * 
     * @param id ID do usuário
     * @return Resposta vazia
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Desativar usuário", description = "Desativa um usuário (soft delete)")
    public ResponseEntity<Void> deactivateUser(@PathVariable UUID id) {
        userService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }
}
