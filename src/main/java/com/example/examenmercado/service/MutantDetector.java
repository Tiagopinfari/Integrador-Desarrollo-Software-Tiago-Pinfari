package com.example.examenmercado.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Slf4j
public class MutantDetector {

    private static final int SEQUENCE_LENGTH = 4;
    private static final int MUTANT_SEQUENCES_THRESHOLD = 2;
    private static final Set<Character> VALID_BASES = Set.of('A', 'T', 'C', 'G');

    public boolean isMutant(String[] dna) {
        if (dna == null || dna.length < SEQUENCE_LENGTH) {
            return false;
        }

        final int N = dna.length;
        char[][] matrix = new char[N][];
        int foundSequences = 0;

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

        for (int row = 0; row < N; row++) {
            for (int col = 0; col < N; col++) {

                // Horizontal
                if (col <= N - SEQUENCE_LENGTH) {
                    if (checkSequence(matrix, row, col, 0, 1)) {
                        foundSequences++;
                        if (foundSequences >= MUTANT_SEQUENCES_THRESHOLD) {
                            return true;
                        }
                    }
                }

                // Vertical
                if (row <= N - SEQUENCE_LENGTH) {
                    if (checkSequence(matrix, row, col, 1, 0)) {
                        foundSequences++;
                        if (foundSequences >= MUTANT_SEQUENCES_THRESHOLD) {
                            return true;
                        }
                    }
                }

                // Diagonal Descendente
                if (row <= N - SEQUENCE_LENGTH && col <= N - SEQUENCE_LENGTH) {
                    if (checkSequence(matrix, row, col, 1, 1)) {
                        foundSequences++;
                        if (foundSequences >= MUTANT_SEQUENCES_THRESHOLD) {
                            return true;
                        }
                    }
                }

                // Diagonal Ascendente
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

        return false;
    }

    private boolean checkSequence(char[][] matrix, int startX, int startY, int deltaX, int deltaY) {
        final char base = matrix[startX][startY];

        return matrix[startX + deltaX][startY + deltaY] == base &&
                matrix[startX + 2 * deltaX][startY + 2 * deltaY] == base &&
                matrix[startX + 3 * deltaX][startY + 3 * deltaY] == base;
    }
}
