package com.ms.cadastro.exception;

// Exceção lançada quando um tutor (owner) não é encontrado no banco de dados
public class OwnerNotFoundException extends RuntimeException {
    public OwnerNotFoundException(String message) {
        super(message); // Passa a mensagem para a superclasse RuntimeException
    }
}
