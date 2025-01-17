package com.eventi.eventiApplication.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDuplicateKeyException(DataIntegrityViolationException e) {
        return new ResponseEntity<>("Errore: Stai provando ad inserire uno username/evento duplicato", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleInvalidFormatException(InvalidFormatException ex) {
        return new ResponseEntity<>("Errore: Il ruolo deve essere USER o ORGANIZER",HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleInvalidEnumException(HttpMessageNotReadableException ex) {
        if (ex.getCause() instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException) {
            return new ResponseEntity<>("Errore: Il ruolo deve essere USER o ORGANIZER", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Errore: Dati non leggibili", HttpStatus.BAD_REQUEST);
    }
}
