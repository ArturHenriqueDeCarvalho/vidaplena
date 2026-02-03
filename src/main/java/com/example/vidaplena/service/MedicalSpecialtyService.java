package com.example.vidaplena.service;

import com.example.vidaplena.domain.dto.request.MedicalSpecialtyRequest;
import com.example.vidaplena.domain.dto.response.MedicalSpecialtyResponse;
import com.example.vidaplena.domain.entity.MedicalSpecialty;
import com.example.vidaplena.exception.ResourceNotFoundException;
import com.example.vidaplena.mapper.EntityMapper;
import com.example.vidaplena.mapper.MedicalSpecialtyMapper;
import com.example.vidaplena.repository.MedicalSpecialtyRepository;
import com.example.vidaplena.service.base.BaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para gerenciamento de especialidades médicas.
 * 
 * <p>
 * Estende {@link BaseService} para herdar operações CRUD comuns,
 * mantendo apenas lógica específica de especialidades.
 * </p>
 * 
 * @author VIDA PLENA Team
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MedicalSpecialtyService
        extends BaseService<MedicalSpecialty, Long, MedicalSpecialtyRequest, MedicalSpecialtyResponse> {

    private final MedicalSpecialtyRepository specialtyRepository;
    private final MedicalSpecialtyMapper specialtyMapper;

    @Override
    protected JpaRepository<MedicalSpecialty, Long> getRepository() {
        return specialtyRepository;
    }

    @Override
    protected EntityMapper<MedicalSpecialty, MedicalSpecialtyRequest, MedicalSpecialtyResponse> getMapper() {
        return specialtyMapper;
    }

    @Override
    protected String getEntityName() {
        return "Especialidade médica";
    }

    /**
     * Inicializa as especialidades padrão do sistema se não existirem.
     * 
     * <p>
     * Especialidades criadas:
     * </p>
     * <ul>
     * <li>GENERAL_PRACTICE - Clínica Geral</li>
     * <li>PEDIATRICS - Pediatria</li>
     * <li>CARDIOLOGY - Cardiologia</li>
     * </ul>
     */
    @Transactional
    public void initializeDefaultSpecialties() {
        log.info("Verificando especialidades padrão do sistema...");

        createSpecialtyIfNotExists("GENERAL_PRACTICE", "Clínica Geral",
                "Atendimento médico geral para diversas condições de saúde");
        createSpecialtyIfNotExists("PEDIATRICS", "Pediatria",
                "Atendimento médico especializado para crianças e adolescentes");
        createSpecialtyIfNotExists("CARDIOLOGY", "Cardiologia",
                "Atendimento médico especializado em doenças do coração e sistema cardiovascular");

        log.info("Especialidades padrão verificadas/criadas com sucesso");
    }

    /**
     * Cria uma especialidade se ela não existir.
     */
    private void createSpecialtyIfNotExists(String code, String name, String description) {
        if (specialtyRepository.findByCode(code).isEmpty()) {
            MedicalSpecialty specialty = MedicalSpecialty.builder()
                    .code(code)
                    .name(name)
                    .description(description)
                    .active(true)
                    .build();
            specialtyRepository.save(specialty);
            log.info("Especialidade criada: {} - {}", code, name);
        }
    }

    /**
     * Busca uma especialidade pelo código.
     * 
     * @param code Código da especialidade
     * @return Especialidade encontrada
     * @throws ResourceNotFoundException se a especialidade não for encontrada
     */
    @Transactional(readOnly = true)
    public MedicalSpecialty findByCode(String code) {
        return specialtyRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Especialidade médica", "código", code));
    }

    /**
     * Retorna todas as especialidades ativas.
     * 
     * @return Lista de especialidades ativas
     */
    @Transactional(readOnly = true)
    public List<MedicalSpecialtyResponse> getAllActiveSpecialties() {
        return specialtyRepository.findByActiveTrue().stream()
                .map(specialtyMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retorna todas as especialidades (ativas e inativas).
     * 
     * @return Lista de todas as especialidades
     */
    @Transactional(readOnly = true)
    public List<MedicalSpecialtyResponse> getAllSpecialties() {
        return findAll();
    }
}
