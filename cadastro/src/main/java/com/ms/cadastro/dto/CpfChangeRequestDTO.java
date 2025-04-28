package com.ms.cadastro.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CpfChangeRequestDTO {
    @NotBlank
    private String newCpf;
}
