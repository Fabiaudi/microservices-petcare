package com.ms.agendamento.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppointmentNotFoundException.class)
    public ResponseEntity<ApiError> handleAppointmentNotFound(AppointmentNotFoundException ex) {
        ApiError error = new ApiError(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(OutsideBusinessHoursException.class)
    public ResponseEntity<ApiError> handleOutsideBusinessHours(OutsideBusinessHoursException ex) {
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TimeSlotUnavailableException.class)
    public ResponseEntity<ApiError> handleTimeSlotUnavailable(TimeSlotUnavailableException ex) {
        ApiError error = new ApiError(HttpStatus.CONFLICT, ex.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }
}
