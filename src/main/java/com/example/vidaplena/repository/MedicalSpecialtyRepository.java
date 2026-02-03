package com.example.vidaplena.repository;

import com.example.vidaplena.domain.entity.MedicalSpecialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para gerenciamento de especialidades médicas.
 * 
 * @author VIDA PLENA Team
 * @since 1.0
 */
@Repository
public interface MedicalSpecialtyRepository extends JpaRepository<MedicalSpecialty, Long> {

    /**
     * Busca uma especialidade pelo código único.
     * 
     * @param code Código da especialidade
     * @return Optional contendo a especialidade se encontrada
     */
    Optional<MedicalSpecialty> findByCode(String code);

    /**
     * Retorna todas as especialidades ativas.
     * 
     * @return Lista de especialidades ativas
     */
    List<MedicalSpecialty> findByActiveTrue();
}
