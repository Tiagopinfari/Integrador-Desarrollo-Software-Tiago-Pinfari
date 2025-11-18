package com.example.examenmercado.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class ValidDnaSequenceValidator implements ConstraintValidator<ValidDnaSequence, String[]> {

    // Patrón Regex para asegurar que solo contiene A, T, C, G (mayúsculas)
    private static final Pattern VALID_BASE_PATTERN = Pattern.compile("^[ATCG]+$");
    private static final int MIN_SIZE = 4;

    @Override
    public boolean isValid(String[] dna, ConstraintValidatorContext context) {
        if (dna == null || dna.length < MIN_SIZE) {
            return false;
        }

        final int N = dna.length;

        if (N < MIN_SIZE) { // Vuelve a chequear el tamaño mínimo
            return false;
        }

        for (String row : dna) {
            // 1. Verificar fila nula o longitud incorrecta (no NxN)
            if (row == null || row.length() != N) {
                return false;
            }

            // 2. Verificar caracteres válidos (A, T, C, G)
            if (!VALID_BASE_PATTERN.matcher(row.toUpperCase()).matches()) {
                return false;
            }
        }

        return true;
    }
}
