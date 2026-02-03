package com.example.vidaplena.repository;

import com.example.vidaplena.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository para operações de persistência da entidade User.
 * 
 * @author VIDA PLENA Team
 * @since 1.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Busca um usuário pelo email.
     * 
     * @param email Email do usuário
     * @return Optional contendo o usuário se encontrado
     */
    Optional<User> findByEmail(String email);

    /**
     * Verifica se existe um usuário com o email informado.
     * 
     * @param email Email a verificar
     * @return true se existe, false caso contrário
     */
    boolean existsByEmail(String email);

    /**
     * Busca todos os usuários ativos.
     * 
     * @return Lista de usuários ativos
     */
    List<User> findByActiveTrue();

    /**
     * Busca um usuário ativo pelo email.
     * 
     * @param email Email do usuário
     * @return Optional contendo o usuário se encontrado e ativo
     */
    Optional<User> findByEmailAndActiveTrue(String email);
}
