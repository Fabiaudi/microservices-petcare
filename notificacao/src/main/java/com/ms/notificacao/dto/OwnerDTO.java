package com.ms.notificacao.dto;

import lombok.Data;

@Data
public class OwnerDTO {
    
    private Long id;

    private String name;

    private String cpf;

    private String email;

    private String phone;
}
