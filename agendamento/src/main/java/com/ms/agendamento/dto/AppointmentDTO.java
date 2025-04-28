package com.ms.agendamento.dto;

import java.time.LocalDateTime;
import java.util.Set;

import com.ms.agendamento.enums.AppointmentType;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

//DTO para criação de agendamento manual via API REST.
//Simples, direto, com apenas os dados que vêm do front-end para solicitar um agendamento

@Data
public class AppointmentDTO {

    @NotNull
    private Long petId;

    @NotNull
    private Set<AppointmentType> types;

    @NotNull
    @Future
    private LocalDateTime dateTime;
}
