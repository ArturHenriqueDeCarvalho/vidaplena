package com.example.vidaplena.service;

import com.example.vidaplena.domain.dto.request.AppointmentStatusRequest;
import com.example.vidaplena.domain.dto.response.AppointmentStatusResponse;
import com.example.vidaplena.domain.entity.AppointmentStatus;
import com.example.vidaplena.exception.ResourceNotFoundException;
import com.example.vidaplena.mapper.AppointmentStatusMapper;
import com.example.vidaplena.mapper.EntityMapper;
import com.example.vidaplena.repository.AppointmentStatusRepository;
import com.example.vidaplena.service.base.BaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para gerenciamento de status de atendimentos.
 * 
 * <p>
 * Estende {@link BaseService} para herdar operações CRUD comuns,
 * mantendo apenas lógica específica de status.
 * </p>
 * 
 * @author VIDA PLENA Team
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentStatusService
        extends BaseService<AppointmentStatus, Long, AppointmentStatusRequest, AppointmentStatusResponse> {

    private final AppointmentStatusRepository statusRepository;
    private final AppointmentStatusMapper statusMapper;

    @Override
    protected JpaRepository<AppointmentStatus, Long> getRepository() {
        return statusRepository;
    }

    @Override
    protected EntityMapper<AppointmentStatus, AppointmentStatusRequest, AppointmentStatusResponse> getMapper() {
        return statusMapper;
    }

    @Override
    protected String getEntityName() {
        return "Status de atendimento";
    }

    /**
     * Inicializa os status padrão do sistema se não existirem.
     * 
     * <p>
     * Status criados:
     * </p>
     * <ul>
     * <li>SCHEDULED - Atendimento Agendado</li>
     * <li>IN_PROGRESS - Atendimento em Andamento</li>
     * <li>COMPLETED - Atendimento Finalizado</li>
     * <li>CANCELED - Atendimento Cancelado</li>
     * </ul>
     */
    @Transactional
    public void initializeDefaultStatuses() {
        log.info("Verificando status padrão do sistema...");

        createStatusIfNotExists("SCHEDULED", "Atendimento Agendado");
        createStatusIfNotExists("IN_PROGRESS", "Atendimento em Andamento");
        createStatusIfNotExists("COMPLETED", "Atendimento Finalizado");
        createStatusIfNotExists("CANCELED", "Atendimento Cancelado");

        log.info("Status padrão verificados/criados com sucesso");
    }

    /**
     * Cria um status se ele não existir.
     */
    private void createStatusIfNotExists(String code, String description) {
        if (!statusRepository.existsByCode(code)) {
            AppointmentStatus status = AppointmentStatus.builder()
                    .code(code)
                    .description(description)
                    .active(true)
                    .build();
            statusRepository.save(status);
            log.info("Status criado: {} - {}", code, description);
        }
    }

    /**
     * Busca um status pelo código.
     * 
     * @param code Código do status
     * @return Status encontrado
     * @throws ResourceNotFoundException se o status não for encontrado
     */
    @Transactional(readOnly = true)
    public AppointmentStatus findByCode(String code) {
        return statusRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Status de atendimento", "código", code));
    }

    /**
     * Retorna todos os status ativos.
     * 
     * @return Lista de status ativos
     */
    @Transactional(readOnly = true)
    public List<AppointmentStatusResponse> getAllActiveStatuses() {
        return statusRepository.findByActiveTrue().stream()
                .map(statusMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retorna todos os status (ativos e inativos).
     * 
     * @return Lista de todos os status
     */
    @Transactional(readOnly = true)
    public List<AppointmentStatusResponse> getAllStatuses() {
        return findAll();
    }
}
