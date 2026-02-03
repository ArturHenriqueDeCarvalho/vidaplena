package com.example.vidaplena.controller;

import com.example.vidaplena.controller.base.BaseController;
import com.example.vidaplena.domain.dto.request.MedicalSpecialtyRequest;
import com.example.vidaplena.domain.dto.response.MedicalSpecialtyResponse;
import com.example.vidaplena.domain.entity.MedicalSpecialty;
import com.example.vidaplena.service.MedicalSpecialtyService;
import com.example.vidaplena.service.base.BaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para gerenciamento de especialidades médicas.
 * 
 * @author VIDA PLENA Team
 * @since 1.0
 */
@RestController
@RequestMapping("/api/specialties")
@RequiredArgsConstructor
@Tag(name = "Especialidades", description = "Gerenciamento de especialidades médicas")
public class MedicalSpecialtyController
        extends BaseController<MedicalSpecialty, Long, MedicalSpecialtyRequest, MedicalSpecialtyResponse> {

    private final MedicalSpecialtyService specialtyService;

    @Override
    protected BaseService<MedicalSpecialty, Long, MedicalSpecialtyRequest, MedicalSpecialtyResponse> getService() {
        return specialtyService;
    }

    /**
     * Lista todas as especialidades ativas.
     */
    @GetMapping("/active")
    @Operation(summary = "Listar especialidades ativas", description = "Retorna todas as especialidades médicas ativas disponíveis para agendamento")
    public ResponseEntity<List<MedicalSpecialtyResponse>> getAllActiveSpecialties() {
        List<MedicalSpecialtyResponse> specialties = specialtyService.getAllActiveSpecialties();
        return ResponseEntity.ok(specialties);
    }

    // Sobrescrever métodos para adicionar @PreAuthorize

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MedicalSpecialtyResponse> create(@RequestBody MedicalSpecialtyRequest request) {
        return super.create(request);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MedicalSpecialtyResponse> update(@PathVariable Long id,
            @RequestBody MedicalSpecialtyRequest request) {
        return super.update(id, request);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return super.delete(id);
    }
}
