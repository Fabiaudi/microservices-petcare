package com.ms.notificacao.dto;

import java.time.LocalDate;
import java.util.Set;

import com.ms.notificacao.enums.Species;
import com.ms.notificacao.enums.Temperament;

import lombok.Data;

@Data
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
