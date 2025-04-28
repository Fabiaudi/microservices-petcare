package com.ms.cadastro.dto;

import com.ms.cadastro.enums.Species;

import lombok.Data;

@Data
public class BreedDTO {

    private Long id;
    
    private String name; 

    private Species species;

    private String imageUrl; 
}
