package com.example.vidaplena.repository;

import com.example.vidaplena.domain.entity.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para operações de persistência da entidade AppointmentStatus.
 * 
 * @author VIDA PLENA Team
 * @since 1.0
 */
@Repository
public interface AppointmentStatusRepository extends JpaRepository<AppointmentStatus, Long> {

    /**
     * Busca um status pelo código.
     * 
     * @param code Código do status (ex: "SCHEDULED")
     * @return Optional contendo o status se encontrado
     */
    Optional<AppointmentStatus> findByCode(String code);

    /**
     * Busca todos os status ativos.
     * 
     * @return Lista de status ativos
     */
    List<AppointmentStatus> findByActiveTrue();

    /**
     * Verifica se existe um status com o código informado.
     * 
     * @param code Código a verificar
     * @return true se existe, false caso contrário
     */
    boolean existsByCode(String code);
}
