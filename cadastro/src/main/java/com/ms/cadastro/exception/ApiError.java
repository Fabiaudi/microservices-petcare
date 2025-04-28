package com.ms.cadastro.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

// Classe usada para padronizar as respostas de erro da API
@Data
@AllArgsConstructor
public class ApiError {
    private HttpStatus status;         // Código HTTP (ex: 404)
    private String message;            // Mensagem amigável
    private LocalDateTime timestamp;   // Momento em que o erro aconteceu
}
