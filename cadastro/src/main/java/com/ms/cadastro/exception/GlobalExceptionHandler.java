package com.ms.cadastro.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

// Classe que centraliza o tratamento de erros da aplicação
@ControllerAdvice
public class GlobalExceptionHandler {

    // Trata quando um tutor (Owner) não é encontrado
    @ExceptionHandler(OwnerNotFoundException.class)
    public ResponseEntity<ApiError> handleOwnerNotFound(OwnerNotFoundException ex) {
        ApiError error = new ApiError(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // Trata quando um pet (Pet) não é encontrado
    @ExceptionHandler(PetNotFoundException.class)
    public ResponseEntity<ApiError> handlePetNotFound(PetNotFoundException ex) {
        ApiError error = new ApiError(HttpStatus.NOT_FOUND, ex.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // Trata quando um pet com os mesmos dados já existe
    @ExceptionHandler(PetAlreadyExistsException.class)
    public ResponseEntity<ApiError> handlePetAlreadyExists(PetAlreadyExistsException ex) {
        ApiError error = new ApiError(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    // Trata quando uma raça não é encontrada
    @ExceptionHandler(BreedNotFoundException.class)
    public ResponseEntity<ApiError> handleBreedNotFound(BreedNotFoundException ex) {
        ApiError error = new ApiError(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // Trata qualquer erro genérico inesperado
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex) {
        ex.printStackTrace(); // ou logger.error(...)
        ApiError error = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno. Por favor, tente novamente mais tarde.",
                LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(OwnerCpfMismatchException.class)
    public ResponseEntity<ApiError> handleOwnerCpfMismatch(OwnerCpfMismatchException ex) {
        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                LocalDateTime.now());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(InvalidPetUpdateException.class)
    public ResponseEntity<ApiError> handleInvalidPetUpdate(InvalidPetUpdateException ex) {
        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                LocalDateTime.now());
        return ResponseEntity.badRequest().body(error);
    }

}
