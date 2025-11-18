package com.example.examenmercado.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotación de validación personalizada para asegurar que el array de ADN
 * sea una matriz NxN válida con solo caracteres A, T, C, G.
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidDnaSequenceValidator.class)
public @interface ValidDnaSequence {
    // Mensaje por defecto si la validación falla
    String message() default "La secuencia de ADN es inválida: debe ser una matriz NxN con al menos 4x4 y contener solo caracteres A, T, C, G.";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
