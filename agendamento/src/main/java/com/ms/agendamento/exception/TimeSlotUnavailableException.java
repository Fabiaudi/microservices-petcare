package com.ms.agendamento.exception;

public class TimeSlotUnavailableException extends RuntimeException {
    public TimeSlotUnavailableException() {
        super("Time slot is full. Please choose another time.");
    }

    public TimeSlotUnavailableException(String message) {
        super(message);
    }
}
