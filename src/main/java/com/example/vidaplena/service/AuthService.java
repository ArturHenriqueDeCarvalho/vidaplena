package com.example.vidaplena.service;

import com.example.vidaplena.domain.dto.request.ChangePasswordRequest;
import com.example.vidaplena.domain.dto.request.CreateUserRequest;
import com.example.vidaplena.domain.dto.request.LoginRequest;
import com.example.vidaplena.domain.dto.response.LoginResponse;
import com.example.vidaplena.domain.dto.response.UserResponse;
import com.example.vidaplena.domain.entity.User;
import com.example.vidaplena.exception.BadRequestException;
import com.example.vidaplena.exception.UnauthorizedException;
import com.example.vidaplena.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service para autenticação e registro de usuários.
 * 
 * <p>
 * Responsável por validar credenciais e gerar tokens JWT.
 * </p>
 * 
 * @author VIDA PLENA Team
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    /**
     * Realiza o login de um usuário.
     * 
     * @param request Credenciais de login
     * @return Resposta com token JWT e dados do usuário
     * @throws UnauthorizedException se as credenciais forem inválidas
     */
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        log.info("Tentativa de login para email: {}", request.getEmail());

        // Buscar usuário por email
        User user = userService.findByEmail(request.getEmail());

        // Validar senha
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Falha no login: senha incorreta para email: {}", request.getEmail());
            throw new UnauthorizedException("Email ou senha inválidos");
        }

        log.info("Login bem-sucedido para usuário: {}", user.getEmail());

        // Gerar token JWT
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtTokenProvider.generateToken(userDetails);

        // Mapear usuário para response
        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .active(user.getActive())
                .createdAt(user.getCreatedAt())
                .build();

        return LoginResponse.builder()
                .token(token)
                .type("Bearer")
                .expiresIn(86400000L) // 24 horas
                .user(userResponse)
                .build();
    }

    /**
     * Registra um novo usuário no sistema.
     * 
     * @param request Dados do novo usuário
     * @return Dados do usuário criado
     */
    @Transactional
    public UserResponse register(CreateUserRequest request) {
        log.info("Registrando novo usuário: {}", request.getEmail());
        return userService.create(request);
    }

    /**
     * Altera a senha do usuário autenticado.
     * 
     * @param email   Email do usuário autenticado
     * @param request Dados da troca de senha
     * @throws UnauthorizedException se a senha atual estiver incorreta
     * @throws BadRequestException   se a nova senha e confirmação não conferirem
     */
    @Transactional
    public void changePassword(String email, ChangePasswordRequest request) {
        log.info("Solicitação de troca de senha para: {}", email);

        // Buscar usuário
        User user = userService.findByEmail(email);

        // Validar senha atual
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            log.warn("Falha na troca de senha: senha atual incorreta para: {}", email);
            throw new UnauthorizedException("Senha atual incorreta");
        }

        // Validar confirmação de nova senha
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Nova senha e confirmação não conferem");
        }

        // Validar se nova senha é diferente da atual
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new BadRequestException("Nova senha deve ser diferente da senha atual");
        }

        // Atualizar senha
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        userService.updatePassword(user.getId(), encodedPassword);

        log.info("Senha alterada com sucesso para: {}", email);
    }
}
