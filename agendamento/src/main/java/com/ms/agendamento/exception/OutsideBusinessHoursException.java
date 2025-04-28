package com.ms.agendamento.exception;

public class OutsideBusinessHoursException extends RuntimeException {
    public OutsideBusinessHoursException() {
        super("Appointment must be scheduled during business hours (Mon–Fri, 08:00–18:00).");
    }

    public OutsideBusinessHoursException(String message) {
        super(message);
    }
}
