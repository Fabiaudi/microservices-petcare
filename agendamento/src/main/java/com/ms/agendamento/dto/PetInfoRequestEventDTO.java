package com.ms.agendamento.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//Evento enviado pelo serviço de Agendamento solicitando dados do pet ao Cadastro.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetInfoRequestEventDTO {
    private Long petId;
}
