package com.example.vidaplena.controller;

import com.example.vidaplena.domain.dto.request.CreateAppointmentRequest;
import com.example.vidaplena.domain.dto.request.UpdateAppointmentRequest;
import com.example.vidaplena.domain.dto.response.AppointmentResponse;
import com.example.vidaplena.security.SecurityHelper;
import com.example.vidaplena.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller para gerenciamento de atendimentos médicos.
 * 
 * @author VIDA PLENA Team
 * @since 1.0
 */
@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Tag(name = "Atendimentos", description = "Gerenciamento de atendimentos médicos")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final SecurityHelper securityHelper;

    /**
     * Lista todos os atendimentos.
     * 
     * @return Lista de atendimentos
     */
    @GetMapping
    @Operation(summary = "Listar atendimentos", description = "Retorna todos os atendimentos do sistema")
    public ResponseEntity<List<AppointmentResponse>> getAllAppointments() {
        List<AppointmentResponse> appointments = appointmentService.findAll();
        return ResponseEntity.ok(appointments);
    }

    /**
     * Busca um atendimento por ID.
     * 
     * @param id ID do atendimento
     * @return Dados do atendimento
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar atendimento", description = "Busca um atendimento específico por ID")
    public ResponseEntity<AppointmentResponse> getAppointmentById(@PathVariable UUID id) {
        AppointmentResponse appointment = appointmentService.findByIdAsResponse(id);
        return ResponseEntity.ok(appointment);
    }

    /**
     * Busca atendimentos por médico.
     * 
     * @param doctorId ID do médico
     * @return Lista de atendimentos do médico
     */
    @GetMapping("/doctor/{doctorId}")
    @Operation(summary = "Buscar por médico", description = "Retorna todos os atendimentos de um médico específico")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsByDoctor(@PathVariable UUID doctorId) {
        List<AppointmentResponse> appointments = appointmentService.findByDoctor(doctorId);
        return ResponseEntity.ok(appointments);
    }

    /**
     * Busca atendimentos por status.
     * 
     * @param statusCode Código do status
     * @return Lista de atendimentos com o status
     */
    @GetMapping("/status/{statusCode}")
    @Operation(summary = "Buscar por status", description = "Retorna todos os atendimentos com um status específico")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsByStatus(@PathVariable String statusCode) {
        List<AppointmentResponse> appointments = appointmentService.findByStatus(statusCode);
        return ResponseEntity.ok(appointments);
    }

    /**
     * Cria um novo atendimento.
     * 
     * @param request Dados do atendimento
     * @return Dados do atendimento criado
     */
    @PostMapping
    @Operation(summary = "Criar atendimento", description = "Cria um novo atendimento (ADMIN, RECEPTIONIST)")
    public ResponseEntity<AppointmentResponse> createAppointment(@Valid @RequestBody CreateAppointmentRequest request) {
        AppointmentResponse appointment = appointmentService.createAppointment(request,
                securityHelper.getCurrentUser());
        return ResponseEntity.status(201).body(appointment);
    }

    /**
     * Atualiza um atendimento existente.
     * 
     * @param id      ID do atendimento
     * @param request Dados de atualização
     * @return Dados do atendimento atualizado
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar atendimento", description = "Atualiza um atendimento existente (ADMIN, DOCTOR)")
    public ResponseEntity<AppointmentResponse> updateAppointment(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateAppointmentRequest request) {
        AppointmentResponse appointment = appointmentService.updateAppointment(id, request,
                securityHelper.getCurrentUser());
        return ResponseEntity.ok(appointment);
    }

    /**
     * Remove um atendimento.
     * 
     * @param id ID do atendimento
     * @return Resposta vazia
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Remover atendimento", description = "Remove um atendimento (ADMIN apenas)")
    public ResponseEntity<Void> deleteAppointment(@PathVariable UUID id) {
        appointmentService.deleteAppointment(id, securityHelper.getCurrentUser());
        return ResponseEntity.noContent().build();
    }
}
