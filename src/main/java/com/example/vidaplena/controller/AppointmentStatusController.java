package com.example.vidaplena.controller;

import com.example.vidaplena.controller.base.BaseController;
import com.example.vidaplena.domain.dto.request.AppointmentStatusRequest;
import com.example.vidaplena.domain.dto.response.AppointmentStatusResponse;
import com.example.vidaplena.domain.entity.AppointmentStatus;
import com.example.vidaplena.service.AppointmentStatusService;
import com.example.vidaplena.service.base.BaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para gerenciamento de status de atendimentos.
 * 
 * @author VIDA PLENA Team
 * @since 1.0
 */
@RestController
@RequestMapping("/api/status")
@RequiredArgsConstructor
@Tag(name = "Status", description = "Gerenciamento de status de atendimentos")
public class AppointmentStatusController
        extends BaseController<AppointmentStatus, Long, AppointmentStatusRequest, AppointmentStatusResponse> {

    private final AppointmentStatusService statusService;

    @Override
    protected BaseService<AppointmentStatus, Long, AppointmentStatusRequest, AppointmentStatusResponse> getService() {
        return statusService;
    }

    /**
     * Lista todos os status ativos.
     */
    @GetMapping("/active")
    @Operation(summary = "Listar status ativos", description = "Retorna todos os status de atendimento ativos")
    public ResponseEntity<List<AppointmentStatusResponse>> getAllActiveStatuses() {
        List<AppointmentStatusResponse> statuses = statusService.getAllActiveStatuses();
        return ResponseEntity.ok(statuses);
    }

    // Sobrescrever métodos para adicionar @PreAuthorize

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AppointmentStatusResponse> create(@RequestBody AppointmentStatusRequest request) {
        return super.create(request);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AppointmentStatusResponse> update(@PathVariable Long id,
            @RequestBody AppointmentStatusRequest request) {
        return super.update(id, request);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return super.delete(id);
    }
}
