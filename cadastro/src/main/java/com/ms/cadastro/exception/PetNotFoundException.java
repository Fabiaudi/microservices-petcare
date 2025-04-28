package com.ms.cadastro.exception;

//Exceção lançada quando um pet não é encontrado no banco de dados.
public class PetNotFoundException extends RuntimeException {
    public PetNotFoundException(String message) {
        super(message);
    }
}
