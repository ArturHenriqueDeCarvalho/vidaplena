package com.example.vidaplena.service;

import com.example.vidaplena.domain.dto.request.CreateUserRequest;
import com.example.vidaplena.domain.dto.request.LoginRequest;
import com.example.vidaplena.domain.dto.response.LoginResponse;
import com.example.vidaplena.domain.dto.response.UserResponse;
import com.example.vidaplena.domain.entity.User;
import com.example.vidaplena.domain.enums.UserRole;
import com.example.vidaplena.exception.UnauthorizedException;
import com.example.vidaplena.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para AuthService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private LoginRequest loginRequest;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(UUID.randomUUID())
                .name("Test User")
                .email("test@vidaplena.com")
                .password("$2a$10$hashedPassword")
                .role(UserRole.ADMIN)
                .build();
        // active, createdAt, updatedAt são preenchidos automaticamente

        loginRequest = LoginRequest.builder()
                .email("test@vidaplena.com")
                .password("password123")
                .build();

        userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(testUser.getEmail())
                .password(testUser.getPassword())
                .authorities("ROLE_ADMIN")
                .build();
    }

    @Test
    @DisplayName("Deve fazer login com credenciais válidas")
    void shouldLoginSuccessfullyWithValidCredentials() {
        // Arrange
        when(userService.findByEmail(loginRequest.getEmail())).thenReturn(testUser);
        when(passwordEncoder.matches(loginRequest.getPassword(), testUser.getPassword())).thenReturn(true);
        when(userDetailsService.loadUserByUsername(testUser.getEmail())).thenReturn(userDetails);
        when(jwtTokenProvider.generateToken(userDetails)).thenReturn("valid.jwt.token");

        // Act
        LoginResponse response = authService.login(loginRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("valid.jwt.token");
        assertThat(response.getType()).isEqualTo("Bearer");
        assertThat(response.getExpiresIn()).isEqualTo(86400000L);
        assertThat(response.getUser()).isNotNull();
        assertThat(response.getUser().getEmail()).isEqualTo(testUser.getEmail());

        verify(userService).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder).matches(loginRequest.getPassword(), testUser.getPassword());
        verify(jwtTokenProvider).generateToken(userDetails);
    }

    @Test
    @DisplayName("Deve lançar exceção com senha incorreta")
    void shouldThrowExceptionWithInvalidPassword() {
        // Arrange
        when(userService.findByEmail(loginRequest.getEmail())).thenReturn(testUser);
        when(passwordEncoder.matches(loginRequest.getPassword(), testUser.getPassword())).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Email ou senha inválidos");

        verify(userService).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder).matches(loginRequest.getPassword(), testUser.getPassword());
        verify(jwtTokenProvider, never()).generateToken(any());
    }

    @Test
    @DisplayName("Deve gerar token JWT no login")
    void shouldGenerateJwtTokenOnLogin() {
        // Arrange
        String expectedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.token";
        when(userService.findByEmail(anyString())).thenReturn(testUser);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtTokenProvider.generateToken(any(UserDetails.class))).thenReturn(expectedToken);

        // Act
        LoginResponse response = authService.login(loginRequest);

        // Assert
        assertThat(response.getToken()).isEqualTo(expectedToken);
        verify(jwtTokenProvider).generateToken(userDetails);
    }

    @Test
    @DisplayName("Deve registrar novo usuário com sucesso")
    void shouldRegisterNewUserSuccessfully() {
        // Arrange
        CreateUserRequest createRequest = CreateUserRequest.builder()
                .name("New User")
                .email("newuser@vidaplena.com")
                .password("password123")
                .role(UserRole.DOCTOR)
                .build();

        UserResponse expectedResponse = UserResponse.builder()
                .id(UUID.randomUUID())
                .name(createRequest.getName())
                .email(createRequest.getEmail())
                .role(createRequest.getRole())
                .active(true)
                .build();

        when(userService.create(createRequest)).thenReturn(expectedResponse);

        // Act
        UserResponse response = authService.register(createRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo(createRequest.getEmail());
        assertThat(response.getName()).isEqualTo(createRequest.getName());
        verify(userService).create(createRequest);
    }

    @Test
    @DisplayName("Deve retornar dados do usuário no login")
    void shouldReturnUserDataOnLogin() {
        // Arrange
        when(userService.findByEmail(anyString())).thenReturn(testUser);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtTokenProvider.generateToken(any())).thenReturn("token");

        // Act
        LoginResponse response = authService.login(loginRequest);

        // Assert
        assertThat(response.getUser()).isNotNull();
        assertThat(response.getUser().getId()).isEqualTo(testUser.getId());
        assertThat(response.getUser().getName()).isEqualTo(testUser.getName());
        assertThat(response.getUser().getEmail()).isEqualTo(testUser.getEmail());
        assertThat(response.getUser().getRole()).isEqualTo(testUser.getRole());
        assertThat(response.getUser().getActive()).isTrue();
    }
}
