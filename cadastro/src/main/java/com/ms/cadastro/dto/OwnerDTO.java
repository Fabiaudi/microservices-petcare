package com.ms.cadastro.dto;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class OwnerDTO {
    
    private Long id;

    private String name;

    private String cpf;

    private String email;

    private String phone;
    @JsonIgnore
    private Set<PetDTO> pets;
}
