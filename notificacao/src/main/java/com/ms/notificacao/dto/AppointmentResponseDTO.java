package com.ms.notificacao.dto;

import java.time.LocalDateTime;
import java.util.Set;

import com.ms.notificacao.enums.AppointmentType;

import lombok.Data;

//Retornado em GET /appointments, GET /appointments/{id} etc.
//Contém informações completas para exibição.
@Data
public class AppointmentResponseDTO {

    private Long id;

    private Long petId;

    private String petName;

    private String ownerEmail;

    private Set<AppointmentType> types;

    private LocalDateTime dateTime;

    private boolean automatic;

    private boolean confirmed;

    private String notes;
}
