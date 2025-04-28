package com.ms.agendamento.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.ms.agendamento.dto.AppointmentDTO;
import com.ms.agendamento.dto.AppointmentResponseDTO;
import com.ms.agendamento.model.Appointment;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    AppointmentResponseDTO toResponseDTO(Appointment appointment);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "automatic", ignore = true),
        @Mapping(target = "confirmed", ignore = true),
        @Mapping(target = "petName", ignore = true),
        @Mapping(target = "ownerEmail", ignore = true),
        @Mapping(target = "notes", ignore = true)
    })
    Appointment toEntity(AppointmentDTO dto);
}
