package com.example.vidaplena.service;

import com.example.vidaplena.domain.dto.request.CreateUserRequest;
import com.example.vidaplena.domain.dto.response.UserResponse;
import com.example.vidaplena.domain.entity.User;
import com.example.vidaplena.domain.enums.UserRole;
import com.example.vidaplena.exception.BusinessException;
import com.example.vidaplena.exception.ResourceNotFoundException;
import com.example.vidaplena.mapper.UserMapper;
import com.example.vidaplena.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para UserService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private CreateUserRequest createUserRequest;
    private UserResponse testUserResponse;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(UUID.randomUUID())
                .name("Test User")
                .email("test@vidaplena.com")
                .password("hashedPassword")
                .role(UserRole.DOCTOR)
                .active(true)
                .build();

        createUserRequest = CreateUserRequest.builder()
                .name("New User")
                .email("newuser@vidaplena.com")
                .password("password123")
                .role(UserRole.RECEPTIONIST)
                .build();

        testUserResponse = UserResponse.builder()
                .id(testUser.getId())
                .name(testUser.getName())
                .email(testUser.getEmail())
                .role(testUser.getRole())
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Deve criar usuário com sucesso")
    void shouldCreateUserSuccessfully() {
        // Arrange
        when(userRepository.existsByEmail(createUserRequest.getEmail())).thenReturn(false);
        when(userMapper.toEntity(createUserRequest)).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toResponse(testUser)).thenReturn(testUserResponse);

        // Act
        UserResponse response = userService.create(createUserRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo(testUser.getEmail());
        verify(userRepository).existsByEmail(createUserRequest.getEmail());
        verify(userMapper).toEntity(createUserRequest);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar usuário com email duplicado")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Arrange
        when(userRepository.existsByEmail(createUserRequest.getEmail())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.create(createUserRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Email já cadastrado");

        verify(userRepository).existsByEmail(createUserRequest.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Deve aplicar hash BCrypt na senha")
    void shouldHashPasswordWithBCrypt() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userMapper.toEntity(any(CreateUserRequest.class))).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toResponse(any(User.class))).thenReturn(testUserResponse);

        // Act
        userService.create(createUserRequest);

        // Assert
        verify(userMapper).toEntity(createUserRequest);
    }

    @Test
    @DisplayName("Deve buscar usuário por email com sucesso")
    void shouldFindUserByEmailSuccessfully() {
        // Arrange
        when(userRepository.findByEmailAndActiveTrue(testUser.getEmail()))
                .thenReturn(Optional.of(testUser));

        // Act
        User foundUser = userService.findByEmail(testUser.getEmail());

        // Assert
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getEmail()).isEqualTo(testUser.getEmail());
        verify(userRepository).findByEmailAndActiveTrue(testUser.getEmail());
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não encontrado por email")
    void shouldThrowExceptionWhenUserNotFoundByEmail() {
        // Arrange
        String email = "notfound@vidaplena.com";
        when(userRepository.findByEmailAndActiveTrue(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.findByEmail(email))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Usuário não encontrado");

        verify(userRepository).findByEmailAndActiveTrue(email);
    }

    @Test
    @DisplayName("Deve buscar usuário por ID com sucesso")
    void shouldFindUserByIdSuccessfully() {
        // Arrange
        UUID userId = testUser.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // Act
        User foundUser = userService.findById(userId);

        // Assert
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(userId);
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não encontrado por ID")
    void shouldThrowExceptionWhenUserNotFoundById() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.findById(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Usuário não encontrado");

        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("Deve listar todos os usuários ativos")
    void shouldListAllActiveUsers() {
        // Arrange
        User user2 = User.builder()
                .id(UUID.randomUUID())
                .name("User 2")
                .email("user2@vidaplena.com")
                .password("hashedPassword")
                .role(UserRole.ADMIN)
                .active(true)
                .build();

        UserResponse user2Response = UserResponse.builder()
                .id(user2.getId())
                .name(user2.getName())
                .email(user2.getEmail())
                .role(user2.getRole())
                .active(true)
                .build();

        when(userRepository.findByActiveTrue()).thenReturn(Arrays.asList(testUser, user2));
        when(userMapper.toResponse(testUser)).thenReturn(testUserResponse);
        when(userMapper.toResponse(user2)).thenReturn(user2Response);

        // Act
        List<UserResponse> users = userService.getAllActiveUsers();

        // Assert
        assertThat(users).hasSize(2);
        assertThat(users).extracting(UserResponse::getEmail)
                .containsExactlyInAnyOrder(testUser.getEmail(), user2.getEmail());
        verify(userRepository).findByActiveTrue();
    }

    @Test
    @DisplayName("Deve desativar usuário (soft delete)")
    void shouldDeactivateUser() {
        // Arrange
        UUID userId = testUser.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        userService.deactivateUser(userId);

        // Assert
        verify(userRepository).findById(userId);
        verify(userRepository).save(argThat(user -> !user.getActive() && user.isDeleted() // Verifica soft delete
        ));
    }
}
