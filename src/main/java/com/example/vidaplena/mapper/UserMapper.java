package com.example.vidaplena.mapper;

import com.example.vidaplena.domain.dto.request.CreateUserRequest;
import com.example.vidaplena.domain.dto.response.UserResponse;
import com.example.vidaplena.domain.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversão entre User e seus DTOs.
 * 
 * @author VIDA PLENA Team
 * @since 1.0
 */
@Component
public class UserMapper implements EntityMapper<User, CreateUserRequest, UserResponse> {

    private final PasswordEncoder passwordEncoder;

    public UserMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User toEntity(CreateUserRequest request) {
        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .active(true)
                .build();
    }

    @Override
    public UserResponse toResponse(User entity) {
        return UserResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .role(entity.getRole())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    @Override
    public void updateEntity(User entity, CreateUserRequest request) {
        if (request.getName() != null) {
            entity.setName(request.getName());
        }
        if (request.getEmail() != null) {
            entity.setEmail(request.getEmail());
        }
        if (request.getPassword() != null) {
            entity.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getRole() != null) {
            entity.setRole(request.getRole());
        }
    }
}
