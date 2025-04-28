package com.ms.agendamento.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OwnerDTO {
    
    private Long id;

    private String name;

    private String cpf;

    private String email;

    private String phone;
   
}
