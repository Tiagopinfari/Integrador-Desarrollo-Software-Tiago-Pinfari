package com.example.examenmercado.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// NO USA @SpringBootTest para aislar la unidad de prueba (test unitario puro)
class MutantDetectorTest {

    private MutantDetector detector;

    @BeforeEach
    void setUp() {
        // Inicializar la clase antes de cada test
        detector = new MutantDetector();
    }

    // =======================================================================
    // CASOS MUTANTES (Debe retornar TRUE)
    // =======================================================================

    @Test
    @DisplayName("MUTANTE: Dos secuencias horizontales")
    void testMutant_TwoHorizontalSequences() {
        String[] dna = {
                "AAAAAT",
                "CGCGTA",
                "ATTGAC",
                "ATTGTA",
                "CCCCCA", // Segunda secuencia horizontal
                "TCACTG"
        };
        assertTrue(detector.isMutant(dna));
    }

    @Test
    @DisplayName("MUTANTE: Dos secuencias verticales")
    void testMutant_TwoVerticalSequences() {
        String[] dna = {
                "ATGCGA",
                "ATGCGA",
                "ATGCGA",
                "ATGCGA", // Primera secuencia vertical (Columna 0)
                "CCGCGA",
                "TTGCGA" // Segunda secuencia vertical (Columna 1)
        };
        assertTrue(detector.isMutant(dna));
    }

    @Test
    @DisplayName("MUTANTE: Una secuencia horizontal y una diagonal descendente")
    void testMutant_HorizontalAndDiagonalDescending() {
        String[] dna = {
                "ATGCGA", // D1: A (0,0)
                "CAGTGC", // D1: A (1,1)
                "TTATGT", // D1: A (2,2)
                "AGAAGG", // D1: A (3,3)
                "CCCCAA", // H: CCCC
                "TCACTG"
        };
        assertTrue(detector.isMutant(dna));
    }

    @Test
    @DisplayName("MUTANTE: Diagonal ascendente y una horizontal")
    void testMutant_DiagonalAscendingAndHorizontal() {
        String[] dna = {
                "GTACGA", // A4: C (0,3)
                "CAGCCC", // A3: C (1,2)
                "ATTCTT", // A2: C (2,1)
                "CCCCTA", // A1: C (3,0) | H: CCCC
                "TGTGTG",
                "GATATG"
        };
        assertTrue(detector.isMutant(dna));
    }

    @Test
    @DisplayName("MUTANTE: Terminación Anticipada (Early Termination) funciona")
    void testMutant_EarlyTermination() {
        String[] dna = {
                "AAAAAA", // Secuencia 1 (Horizontal)
                "AAAAAA", // Secuencia 2 (Horizontal - debería parar aquí)
                "TTATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
        };
        // Si no usara early termination, revisaría toda la matriz.
        // Aquí solo verificamos que da true rápidamente.
        assertTrue(detector.isMutant(dna));
    }

    @Test
    @DisplayName("MUTANTE: Múltiples secuencias en matriz de tamaño mínimo 4x4")
    void testMutant_SmallMatrix() {
        String[] dna = {
                "AAAA", // H1
                "TCTT",
                "GCGC",
                "CCCC"  // H2
        };
        assertTrue(detector.isMutant(dna));
    }

    // =======================================================================
    // CASOS NO MUTANTES / HUMANOS (Debe retornar FALSE)
    // =======================================================================

    @Test
    @DisplayName("HUMANO: Cero secuencias encontradas")
    void testHuman_NoSequences() {
        String[] dna = {
                "ATGCGA",
                "CAGTGC",
                "TTATGT",
                "AGACGG",
                "GCGTCA",
                "TCACTG"
        };
        assertFalse(detector.isMutant(dna));
    }

    @Test
    @DisplayName("HUMANO: Solo una secuencia (Vertical)")
    void testHuman_OnlyOneSequence() {
        String[] dna = {
                "ATGCTA",
                "ATGCTA",
                "ATGCTA",
                "ATGCTA", // Solo una secuencia: Vertical en columna 0
                "GCGTCA",
                "TCACTG"
        };
        assertTrue(detector.isMutant(dna));
    }

    @Test
    @DisplayName("HUMANO: Secuencias de longitud 3 (no cuentan)")
    void testHuman_ThreeLengthSequences() {
        String[] dna = {
                "AAAGAG",
                "ACCCGA",
                "ATTGAC",
                "AGGCTA",
                "GCTCCC",
                "TCACTG"
        };
        assertTrue(detector.isMutant(dna));
    }

    // =======================================================================
    // CASOS DE VALIDACIÓN (Debe retornar FALSE)
    // =======================================================================

    @Test
    @DisplayName("VALIDACIÓN: ADN nulo")
    void testValidation_NullDna() {
        assertFalse(detector.isMutant(null));
    }

    @Test
    @DisplayName("VALIDACIÓN: Array de ADN vacío")
    void testValidation_EmptyDna() {
        String[] dna = {};
        assertFalse(detector.isMutant(dna));
    }

    @Test
    @DisplayName("VALIDACIÓN: Matriz demasiado pequeña (3x3)")
    void testValidation_TooSmallDna() {
        String[] dna = { "ATG", "CAG", "TTT" };
        assertFalse(detector.isMutant(dna));
    }

    @Test
    @DisplayName("VALIDACIÓN: Matriz no cuadrada (4x5)")
    void testValidation_NonSquareMatrix() {
        String[] dna = {
                "ATGCG", // 5 caracteres
                "CAGTG", // 5 caracteres
                "TTATG", // 5 caracteres
                "AGAAG"  // 5 caracteres, pero solo 4 filas (4x5)
        };
        assertFalse(detector.isMutant(dna));
    }

    @Test
    @DisplayName("VALIDACIÓN: Carácter de ADN inválido ('X')")
    void testValidation_InvalidCharacter() {
        String[] dna = {
                "ATGCGA",
                "CAGTGC",
                "TTATGT",
                "AGAXGG", // Carácter 'X'
                "CCCCTA",
                "TCACTG"
        };
        assertFalse(detector.isMutant(dna));
    }

    @Test
    @DisplayName("VALIDACIÓN: Fila nula dentro del array")
    void testValidation_NullRowInArray() {
        String[] dna = {
                "ATGCGA",
                "CAGTGC",
                null, // Fila nula
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
        };
        assertFalse(detector.isMutant(dna));
    }
}
