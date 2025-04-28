package com.ms.cadastro.exception;

// Exceção lançada quando uma raça não é encontrada no banco de dados
public class BreedNotFoundException extends RuntimeException {
    public BreedNotFoundException(String message) {
        super(message);
    }
}
