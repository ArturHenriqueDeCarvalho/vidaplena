package com.example.vidaplena.mapper;

import com.example.vidaplena.domain.dto.request.AppointmentStatusRequest;
import com.example.vidaplena.domain.dto.response.AppointmentStatusResponse;
import com.example.vidaplena.domain.entity.AppointmentStatus;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversão entre AppointmentStatus e seus DTOs.
 * 
 * @author VIDA PLENA Team
 * @since 1.0
 */
@Component
public class AppointmentStatusMapper
        implements EntityMapper<AppointmentStatus, AppointmentStatusRequest, AppointmentStatusResponse> {

    @Override
    public AppointmentStatus toEntity(AppointmentStatusRequest request) {
        return AppointmentStatus.builder()
                .code(request.getCode())
                .description(request.getName())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();
    }

    @Override
    public AppointmentStatusResponse toResponse(AppointmentStatus entity) {
        return AppointmentStatusResponse.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getDescription())
                .description(entity.getDescription())
                .active(entity.getActive())
                .build();
    }

    @Override
    public void updateEntity(AppointmentStatus entity, AppointmentStatusRequest request) {
        if (request.getCode() != null) {
            entity.setCode(request.getCode());
        }
        if (request.getName() != null) {
            entity.setDescription(request.getName());
        }
        if (request.getDescription() != null) {
            entity.setDescription(request.getDescription());
        }
        if (request.getActive() != null) {
            entity.setActive(request.getActive());
        }
    }
}
