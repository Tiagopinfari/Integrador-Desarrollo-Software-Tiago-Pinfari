package com.example.examenmercado.exception;

// Excepción de Runtime para errores en el cálculo del hash SHA-256
public class DnaHashCalculationException extends RuntimeException {
    public DnaHashCalculationException(String message, Throwable cause) {
        super(message, cause);
    }
}