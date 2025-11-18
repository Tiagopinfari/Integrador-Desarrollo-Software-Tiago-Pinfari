package com.example.examenmercado.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Slf4j // Usando Lombok para el logger
public class MutantDetector {

    // Longitud requerida de la secuencia (4) y umbral para mutante (> 1 secuencia)
    private static final int SEQUENCE_LENGTH = 4;
    private static final int MUTANT_SEQUENCES_THRESHOLD = 2;
    private static final Set<Character> VALID_BASES = Set.of('A', 'T', 'C', 'G'); // Optimizacion O(1)

    /**
     * Determina si una secuencia de ADN pertenece a un mutante.
     * @param dna El array de Strings que representa la matriz NxN del ADN.
     * @return true si se encuentran MÁS DE UNA secuencia de cuatro letras iguales, false en caso contrario.
     */
    public boolean isMutant(String[] dna) {
        // Validación del Input (Fail-fast y NxN)
        if (dna == null || dna.length < SEQUENCE_LENGTH) {
            return false;
        }

        final int N = dna.length;
        char[][] matrix = new char[N][];
        int foundSequences = 0;

        // Conversión a char[][] y validación NxN/Caracteres (Optimización #1)
        for (int i = 0; i < N; i++) {
            String row = dna[i];
            if (row == null || row.length() != N) {
                log.warn("El ADN es inválido: no es una matriz NxN o contiene filas nulas/invalidas.");
                return false;
            }

            matrix[i] = row.toUpperCase().toCharArray();
            for (char base : matrix[i]) {
                if (!VALID_BASES.contains(base)) {
                    log.warn("El ADN es inválido: contiene carácter no permitido: {}", base);
                    return false;
                }
            }
        }

        // Búsqueda en un solo recorrido (Single Pass - Optimización #2)
        for (int row = 0; row < N; row++) {
            for (int col = 0; col < N; col++) {

                // 1. Horizontal (→) - Boundary Checking (Optimización #3)
                if (col <= N - SEQUENCE_LENGTH) {
                    if (checkSequence(matrix, row, col, 0, 1)) {
                        foundSequences++;
                        if (foundSequences >= MUTANT_SEQUENCES_THRESHOLD) {
                            return true; // EARLY TERMINATION! (Optimización #4)
                        }
                    }
                }

                // 2. Vertical (↓) - Boundary Checking
                if (row <= N - SEQUENCE_LENGTH) {
                    if (checkSequence(matrix, row, col, 1, 0)) {
                        foundSequences++;
                        if (foundSequences >= MUTANT_SEQUENCES_THRESHOLD) {
                            return true;
                        }
                    }
                }

                // 3. Diagonal Descendente (↘) - Boundary Checking
                if (row <= N - SEQUENCE_LENGTH && col <= N - SEQUENCE_LENGTH) {
                    if (checkSequence(matrix, row, col, 1, 1)) {
                        foundSequences++;
                        if (foundSequences >= MUTANT_SEQUENCES_THRESHOLD) {
                            return true;
                        }
                    }
                }

                // 4. Diagonal Ascendente (↗) - Boundary Checking
                if (row >= SEQUENCE_LENGTH - 1 && col <= N - SEQUENCE_LENGTH) {
                    if (checkSequence(matrix, row, col, -1, 1)) { // deltaX = -1 (subir)
                        foundSequences++;
                        if (foundSequences >= MUTANT_SEQUENCES_THRESHOLD) {
                            return true;
                        }
                    }
                }
            }
        }

        // Si se completa la matriz y no se encontraron 2 secuencias, no es mutante.
        return false;
    }

    /**
     * Método auxiliar para verificar una secuencia de 4 caracteres iguales
     * a partir de (startX, startY) en una dirección definida por (deltaX, deltaY).
     * @param deltaX: Cambio en la fila (1 para abajo, -1 para arriba, 0 para horizontal)
     * @param deltaY: Cambio en la columna (1 para derecha, -1 para izquierda, 0 para vertical)
     */
    private boolean checkSequence(char[][] matrix, int startX, int startY, int deltaX, int deltaY) {
        final char base = matrix[startX][startY];

        // Comprobación directa (Direct Comparison - Optimización #5)
        return matrix[startX + deltaX][startY + deltaY] == base &&
                matrix[startX + 2 * deltaX][startY + 2 * deltaY] == base &&
                matrix[startX + 3 * deltaX][startY + 3 * deltaY] == base;
    }
}
