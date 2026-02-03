package com.example.vidaplena.service;

import com.example.vidaplena.domain.dto.request.CreateUserRequest;
import com.example.vidaplena.domain.dto.response.UserResponse;
import com.example.vidaplena.domain.entity.User;
import com.example.vidaplena.exception.BusinessException;
import com.example.vidaplena.exception.ResourceNotFoundException;
import com.example.vidaplena.mapper.EntityMapper;
import com.example.vidaplena.mapper.UserMapper;
import com.example.vidaplena.repository.UserRepository;
import com.example.vidaplena.service.base.BaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service para gerenciamento de usuários.
 * 
 * <p>
 * Estende {@link BaseService} para herdar operações CRUD comuns,
 * mantendo apenas lógica específica de usuários.
 * </p>
 * 
 * @author VIDA PLENA Team
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService extends BaseService<User, UUID, CreateUserRequest, UserResponse> {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    protected JpaRepository<User, UUID> getRepository() {
        return userRepository;
    }

    @Override
    protected EntityMapper<User, CreateUserRequest, UserResponse> getMapper() {
        return userMapper;
    }

    @Override
    protected String getEntityName() {
        return "Usuário";
    }

    /**
     * Sobrescreve o método create para adicionar validação de email único.
     * 
     * @param request Dados do usuário a ser criado
     * @return Dados do usuário criado
     * @throws BusinessException se o email já estiver em uso
     */
    @Override
    @Transactional
    public UserResponse create(CreateUserRequest request) {
        log.info("Criando novo usuário com email: {}", request.getEmail());

        // Validação específica: email único
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email já cadastrado: " + request.getEmail());
        }

        UserResponse response = super.create(request);
        log.info("Usuário criado com sucesso: ID={}, Email={}", response.getId(), response.getEmail());

        return response;
    }

    /**
     * Busca um usuário pelo email.
     * 
     * @param email Email do usuário
     * @return Usuário encontrado
     * @throws ResourceNotFoundException se o usuário não for encontrado
     */
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmailAndActiveTrue(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "email", email));
    }

    /**
     * Retorna todos os usuários ativos.
     * 
     * @return Lista de usuários ativos
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getAllActiveUsers() {
        return userRepository.findByActiveTrue().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Desativa um usuário (soft delete).
     * 
     * @param id ID do usuário a ser desativado
     */
    @Transactional
    public void deactivateUser(UUID id) {
        User user = findById(id);
        user.setActive(false);
        user.setDeleted(true); // Garante compatibilidade com soft delete
        userRepository.save(user);
        log.info("Usuário desativado: ID={}", id);
    }

    /**
     * Atualiza a senha de um usuário.
     * 
     * @param id              ID do usuário
     * @param encodedPassword Nova senha já codificada (BCrypt)
     */
    @Transactional
    public void updatePassword(UUID id, String encodedPassword) {
        User user = findById(id);
        user.setPassword(encodedPassword);
        userRepository.save(user);
        log.info("Senha atualizada para usuário: ID={}", id);
    }
}
