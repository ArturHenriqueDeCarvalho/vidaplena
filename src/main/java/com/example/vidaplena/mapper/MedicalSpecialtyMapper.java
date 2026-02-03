package com.example.vidaplena.mapper;

import com.example.vidaplena.domain.dto.request.MedicalSpecialtyRequest;
import com.example.vidaplena.domain.dto.response.MedicalSpecialtyResponse;
import com.example.vidaplena.domain.entity.MedicalSpecialty;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversão entre MedicalSpecialty e seus DTOs.
 * 
 * @author VIDA PLENA Team
 * @since 1.0
 */
@Component
public class MedicalSpecialtyMapper
        implements EntityMapper<MedicalSpecialty, MedicalSpecialtyRequest, MedicalSpecialtyResponse> {

    @Override
    public MedicalSpecialty toEntity(MedicalSpecialtyRequest request) {
        return MedicalSpecialty.builder()
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();
    }

    @Override
    public MedicalSpecialtyResponse toResponse(MedicalSpecialty entity) {
        return MedicalSpecialtyResponse.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .description(entity.getDescription())
                .active(entity.getActive())
                .build();
    }

    @Override
    public void updateEntity(MedicalSpecialty entity, MedicalSpecialtyRequest request) {
        if (request.getCode() != null) {
            entity.setCode(request.getCode());
        }
        if (request.getName() != null) {
            entity.setName(request.getName());
        }
        if (request.getDescription() != null) {
            entity.setDescription(request.getDescription());
        }
        if (request.getActive() != null) {
            entity.setActive(request.getActive());
        }
    }
}
