package com.ms.cadastro.exception;

// Exceção lançada quando tentamos cadastrar um pet que já existe
public class PetAlreadyExistsException extends RuntimeException {
    public PetAlreadyExistsException(String message) {
        super(message);
    }
}
