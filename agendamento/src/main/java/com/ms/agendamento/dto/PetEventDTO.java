package com.ms.agendamento.dto;

import java.time.LocalDate;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ms.agendamento.enums.Species;
import com.ms.agendamento.enums.Temperament;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PetEventDTO {

    private Long id;

    private String name;

    private LocalDate birthDate;

    private Species species;

    private String breed;

    private String color;

    private Double weight;

    private Temperament temperament;

    private String description;

    private String imageUrl;

    private LocalDate lastVaccinationDate;

    private Set<OwnerDTO> owners;
}
