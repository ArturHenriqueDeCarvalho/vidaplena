package com.example.vidaplena.service;

import com.example.vidaplena.domain.dto.request.CreateAppointmentRequest;
import com.example.vidaplena.domain.dto.request.UpdateAppointmentRequest;
import com.example.vidaplena.domain.dto.response.AppointmentResponse;
import com.example.vidaplena.domain.dto.event.AppointmentEvent;
import com.example.vidaplena.domain.entity.Appointment;
import com.example.vidaplena.domain.entity.AppointmentStatus;
import com.example.vidaplena.domain.entity.MedicalSpecialty;
import com.example.vidaplena.domain.entity.User;
import com.example.vidaplena.domain.enums.UserRole;
import com.example.vidaplena.exception.BusinessException;
import com.example.vidaplena.exception.ResourceNotFoundException;
import com.example.vidaplena.kafka.AppointmentEventProducer;
import com.example.vidaplena.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service para gerenciamento de atendimentos médicos.
 * 
 * <p>
 * Implementa todas as regras de negócio relacionadas a atendimentos:
 * </p>
 * <ul>
 * <li>Data agendada não pode ser no passado</li>
 * <li>Status COMPLETED é imutável</li>
 * <li>Apenas médicos podem atualizar status para IN_PROGRESS ou COMPLETED</li>
 * <li>Controle de permissões por perfil de usuário</li>
 * </ul>
 * 
 * @author VIDA PLENA Team
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentStatusService statusService;
    private final UserService userService;
    private final MedicalSpecialtyService specialtyService;

    @Autowired(required = false)
    private AppointmentEventProducer eventProducer;

    /**
     * Cria um novo atendimento.
     * 
     * @param request     Dados do atendimento
     * @param currentUser Usuário que está criando (ADMIN ou RECEPTIONIST)
     * @return Dados do atendimento criado
     * @throws BusinessException se a data for no passado ou médico não existir
     */
    @Transactional
    public AppointmentResponse createAppointment(CreateAppointmentRequest request, User currentUser) {
        log.info("Criando novo atendimento para paciente: {}", request.getPatient());

        // Validar data agendada
        if (request.getScheduledDate().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Data agendada não pode ser no passado");
        }

        // Buscar médico
        User doctor = userService.findById(request.getDoctorId());
        if (doctor.getRole() != UserRole.DOCTOR) {
            throw new BusinessException("Usuário informado não é um médico");
        }

        // Buscar especialidade
        MedicalSpecialty specialty = specialtyService.findById(request.getSpecialtyId());

        // Buscar status inicial (SCHEDULED)
        AppointmentStatus scheduledStatus = statusService.findByCode("SCHEDULED");

        // Criar atendimento
        Appointment appointment = Appointment.builder()
                .patient(request.getPatient())
                .doctor(doctor)
                .specialty(specialty)
                .status(scheduledStatus)
                .scheduledDate(request.getScheduledDate())
                .notes(request.getNotes())
                .build();
        // createdBy será preenchido automaticamente via @CreatedBy

        Appointment savedAppointment = appointmentRepository.save(appointment);
        log.info("Atendimento criado com sucesso: ID={}", savedAppointment.getId());

        // Publicar evento Kafka
        publishCreatedEvent(savedAppointment, currentUser);

        return mapToResponse(savedAppointment);
    }

    /**
     * Atualiza um atendimento existente.
     * 
     * @param id          ID do atendimento
     * @param request     Dados de atualização
     * @param currentUser Usuário que está atualizando
     * @return Dados do atendimento atualizado
     * @throws BusinessException se status COMPLETED ou permissões inválidas
     */
    @Transactional
    public AppointmentResponse updateAppointment(UUID id, UpdateAppointmentRequest request, User currentUser) {
        log.info("Atualizando atendimento: ID={}", id);

        Appointment appointment = findById(id);

        // Validar se status atual é COMPLETED (imutável)
        if ("COMPLETED".equals(appointment.getStatus().getCode())) {
            throw new BusinessException("Atendimento finalizado não pode ser alterado");
        }

        // Validar data no passado (se fornecida)
        if (request.getScheduledDate() != null && request.getScheduledDate().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Data agendada não pode ser no passado");
        }

        // Atualizar médico (se fornecido)
        if (request.getDoctorId() != null) {
            User newDoctor = userService.findById(request.getDoctorId());
            if (newDoctor.getRole() != UserRole.DOCTOR) {
                throw new BusinessException("Usuário informado não é um médico");
            }
            appointment.setDoctor(newDoctor);
        }

        // Atualizar especialidade (se fornecida)
        if (request.getSpecialtyId() != null) {
            MedicalSpecialty newSpecialty = specialtyService.findById(request.getSpecialtyId());
            appointment.setSpecialty(newSpecialty);
        }

        // Atualizar data (se fornecida)
        if (request.getScheduledDate() != null) {
            appointment.setScheduledDate(request.getScheduledDate());
        }

        // Buscar novo status
        AppointmentStatus newStatus = statusService.findByCode(request.getStatusCode());

        // Validar permissões para mudança de status
        validateStatusChange(appointment, newStatus, currentUser);

        // Atualizar status e notas
        appointment.setStatus(newStatus);
        if (request.getNotes() != null) {
            appointment.setNotes(request.getNotes());
        }

        Appointment updatedAppointment = appointmentRepository.save(appointment);
        log.info("Atendimento atualizado: ID={}, Novo Status={}", id, newStatus.getCode());

        // Publicar evento Kafka
        publishUpdatedEvent(updatedAppointment, currentUser);

        return mapToResponse(updatedAppointment);
    }

    /**
     * Valida se o usuário tem permissão para alterar o status.
     */
    private void validateStatusChange(Appointment appointment, AppointmentStatus newStatus, User currentUser) {
        String newStatusCode = newStatus.getCode();

        // Apenas médicos podem atualizar para IN_PROGRESS ou COMPLETED
        if (("IN_PROGRESS".equals(newStatusCode) || "COMPLETED".equals(newStatusCode))
                && currentUser.getRole() != UserRole.DOCTOR && currentUser.getRole() != UserRole.ADMIN) {
            throw new BusinessException("Apenas médicos podem atualizar status para " + newStatusCode);
        }

        // Se for médico atualizando, validar se é o médico responsável
        if (currentUser.getRole() == UserRole.DOCTOR && !appointment.getDoctor().getId().equals(currentUser.getId())) {
            throw new BusinessException("Você só pode atualizar seus próprios atendimentos");
        }
    }

    /**
     * Busca um atendimento pelo ID.
     * 
     * @param id ID do atendimento
     * @return Atendimento encontrado
     * @throws ResourceNotFoundException se não encontrado
     */
    @Transactional(readOnly = true)
    public Appointment findById(UUID id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Atendimento", "ID", id));
    }

    /**
     * Busca um atendimento pelo ID e retorna como DTO.
     * 
     * @param id ID do atendimento
     * @return DTO do atendimento
     */
    @Transactional(readOnly = true)
    public AppointmentResponse findByIdAsResponse(UUID id) {
        Appointment appointment = findById(id);
        return mapToResponse(appointment);
    }

    /**
     * Retorna todos os atendimentos.
     * 
     * @return Lista de atendimentos
     */
    @Transactional(readOnly = true)
    public List<AppointmentResponse> findAll() {
        return appointmentRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Busca atendimentos por médico.
     * 
     * @param doctorId ID do médico
     * @return Lista de atendimentos do médico
     */
    @Transactional(readOnly = true)
    public List<AppointmentResponse> findByDoctor(UUID doctorId) {
        return appointmentRepository.findByDoctorId(doctorId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Busca atendimentos por status.
     * 
     * @param statusCode Código do status
     * @return Lista de atendimentos com o status
     */
    @Transactional(readOnly = true)
    public List<AppointmentResponse> findByStatus(String statusCode) {
        return appointmentRepository.findByStatusCode(statusCode).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Remove um atendimento (apenas ADMIN).
     * 
     * @param id          ID do atendimento
     * @param currentUser Usuário que está removendo
     * @throws BusinessException se não for ADMIN
     */
    @Transactional
    public void deleteAppointment(UUID id, User currentUser) {
        if (currentUser.getRole() != UserRole.ADMIN) {
            throw new BusinessException("Apenas administradores podem remover atendimentos");
        }

        Appointment appointment = findById(id);

        // Validar se atendimento está finalizado
        if ("COMPLETED".equals(appointment.getStatus().getCode())) {
            throw new BusinessException("Atendimentos finalizados não podem ser removidos");
        }

        // Publicar evento Kafka antes de deletar
        publishDeletedEvent(appointment, currentUser);

        appointmentRepository.delete(appointment);
        log.info("Atendimento removido: ID={}", id);
    }

    /**
     * Converte entidade Appointment para AppointmentResponse.
     */
    private AppointmentResponse mapToResponse(Appointment appointment) {
        return AppointmentResponse.builder()
                .id(appointment.getId())
                .patient(appointment.getPatient())
                .doctor(mapUserToResponse(appointment.getDoctor()))
                .specialty(mapSpecialtyToResponse(appointment.getSpecialty()))
                .status(mapStatusToResponse(appointment.getStatus()))
                .scheduledDate(appointment.getScheduledDate())
                .notes(appointment.getNotes())
                .createdBy(appointment.getCreatedBy()) // Agora é String (email)
                .createdAt(appointment.getCreatedAt())
                .updatedAt(appointment.getUpdatedAt())
                .build();
    }

    private AppointmentResponse.AppointmentStatusResponse mapStatusToResponse(AppointmentStatus status) {
        return AppointmentResponse.AppointmentStatusResponse.builder()
                .id(status.getId())
                .code(status.getCode())
                .description(status.getDescription())
                .build();
    }

    private AppointmentResponse.SpecialtyResponse mapSpecialtyToResponse(MedicalSpecialty specialty) {
        return AppointmentResponse.SpecialtyResponse.builder()
                .id(specialty.getId())
                .code(specialty.getCode())
                .name(specialty.getName())
                .description(specialty.getDescription())
                .build();
    }

    private com.example.vidaplena.domain.dto.response.UserResponse mapUserToResponse(User user) {
        return com.example.vidaplena.domain.dto.response.UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .active(user.getActive())
                .createdAt(user.getCreatedAt())
                .build();
    }

    /**
     * Publica evento de criação de atendimento no Kafka.
     */
    private void publishCreatedEvent(Appointment appointment, User performedBy) {
        if (eventProducer == null) {
            log.debug("Kafka desabilitado - evento de criação não será publicado");
            return;
        }

        try {
            AppointmentEvent event = AppointmentEvent.builder()
                    .eventType(AppointmentEvent.EventType.CREATED)
                    .appointmentId(appointment.getId())
                    .patient(appointment.getPatient())
                    .doctorName(appointment.getDoctor().getName())
                    .specialtyName(appointment.getSpecialty().getName())
                    .status(appointment.getStatus().getCode())
                    .scheduledDate(appointment.getScheduledDate())
                    .timestamp(LocalDateTime.now())
                    .performedBy(performedBy.getName())
                    .build();

            eventProducer.publishCreatedEvent(event);
        } catch (Exception e) {
            log.error("Erro ao publicar evento de criação: {}", e.getMessage(), e);
            // Não propaga exceção para não interromper o fluxo principal
        }
    }

    /**
     * Publica evento de atualização de atendimento no Kafka.
     */
    private void publishUpdatedEvent(Appointment appointment, User performedBy) {
        if (eventProducer == null) {
            log.debug("Kafka desabilitado - evento de atualização não será publicado");
            return;
        }

        try {
            AppointmentEvent event = AppointmentEvent.builder()
                    .eventType(AppointmentEvent.EventType.UPDATED)
                    .appointmentId(appointment.getId())
                    .patient(appointment.getPatient())
                    .doctorName(appointment.getDoctor().getName())
                    .specialtyName(appointment.getSpecialty().getName())
                    .status(appointment.getStatus().getCode())
                    .scheduledDate(appointment.getScheduledDate())
                    .timestamp(LocalDateTime.now())
                    .performedBy(performedBy.getName())
                    .build();

            eventProducer.publishUpdatedEvent(event);
        } catch (Exception e) {
            log.error("Erro ao publicar evento de atualização: {}", e.getMessage(), e);
            // Não propaga exceção para não interromper o fluxo principal
        }
    }

    /**
     * Publica evento de remoção de atendimento no Kafka.
     */
    private void publishDeletedEvent(Appointment appointment, User performedBy) {
        if (eventProducer == null) {
            log.debug("Kafka desabilitado - evento de remoção não será publicado");
            return;
        }

        try {
            AppointmentEvent event = AppointmentEvent.builder()
                    .eventType(AppointmentEvent.EventType.DELETED)
                    .appointmentId(appointment.getId())
                    .patient(appointment.getPatient())
                    .doctorName(appointment.getDoctor().getName())
                    .specialtyName(appointment.getSpecialty().getName())
                    .status(appointment.getStatus().getCode())
                    .scheduledDate(appointment.getScheduledDate())
                    .timestamp(LocalDateTime.now())
                    .performedBy(performedBy.getName())
                    .build();

            eventProducer.publishDeletedEvent(event);
        } catch (Exception e) {
            log.error("Erro ao publicar evento de remoção: {}", e.getMessage(), e);
            // Não propaga exceção para não interromper o fluxo principal
        }
    }
}
