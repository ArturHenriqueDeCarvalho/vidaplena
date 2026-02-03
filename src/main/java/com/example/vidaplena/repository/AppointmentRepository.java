package com.example.vidaplena.repository;

import com.example.vidaplena.domain.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository para operações de persistência da entidade Appointment.
 * 
 * @author VIDA PLENA Team
 * @since 1.0
 */
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    /**
     * Busca atendimentos por médico.
     * 
     * @param doctorId ID do médico
     * @return Lista de atendimentos do médico
     */
    List<Appointment> findByDoctorId(UUID doctorId);

    /**
     * Busca atendimentos por código de status.
     * 
     * @param statusCode Código do status
     * @return Lista de atendimentos com o status
     */
    @Query("SELECT a FROM Appointment a WHERE a.status.code = :statusCode")
    List<Appointment> findByStatusCode(@Param("statusCode") String statusCode);

    /**
     * Busca atendimentos em um intervalo de datas.
     * 
     * @param start Data inicial
     * @param end   Data final
     * @return Lista de atendimentos no intervalo
     */
    List<Appointment> findByScheduledDateBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Busca atendimentos criados por um usuário específico.
     * 
     * @param createdBy Email do usuário que criou
     * @return Lista de atendimentos criados pelo usuário
     */
    List<Appointment> findByCreatedBy(String createdBy);

    /**
     * Busca atendimentos por paciente (busca parcial, case-insensitive).
     * 
     * @param patient Nome do paciente
     * @return Lista de atendimentos do paciente
     */
    List<Appointment> findByPatientContainingIgnoreCase(String patient);
}
