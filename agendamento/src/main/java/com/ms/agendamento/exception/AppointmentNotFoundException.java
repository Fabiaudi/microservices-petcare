package com.ms.agendamento.exception;

/**
 * Exceção lançada quando um agendamento não é encontrado no banco de dados.
 */
public class AppointmentNotFoundException extends RuntimeException {

    public AppointmentNotFoundException(Long id) {
        super("Appointment not found with ID: " + id);
    }

    public AppointmentNotFoundException(String message) {
        super(message);
    }
}