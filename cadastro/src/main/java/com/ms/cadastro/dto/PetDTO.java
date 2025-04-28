package com.ms.cadastro.dto;

import java.time.LocalDate;
import java.util.Set;

import com.ms.cadastro.enums.Species;
import com.ms.cadastro.enums.Temperament;

import lombok.Data;

@Data
public class PetDTO {

    private Long id; 

    private String name;

    private LocalDate birthDate;

    private Species species;

    private String breed;

    private String color;

    private Double weight;

    private String description;

    private Temperament temperament;

    private String imageUrl;

    private LocalDate lastVaccinationDate;

    private Set<OwnerDTO> owners;

}
