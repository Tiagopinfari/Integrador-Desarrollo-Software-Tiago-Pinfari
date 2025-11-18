package com.example.examenmercado.service;

import com.example.examenmercado.dto.StatsResponse;
import com.example.examenmercado.repository.DnaRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final DnaRecordRepository dnaRecordRepository;

    /**
     * Obtiene y calcula las estadísticas de las verificaciones de ADN.
     * @return Un objeto StatsResponse con el conteo y el ratio.
     */
    public StatsResponse getStats() {
        // Usa los query methods del repositorio para obtener el conteo directamente de la BD
        long countMutantDna = dnaRecordRepository.countByIsMutant(true);
        long countHumanDna = dnaRecordRepository.countByIsMutant(false);

        // Calcular el ratio
        double ratio = calculateRatio(countMutantDna, countHumanDna);

        // Retornar la respuesta con el nombre de campo snake_case para JSON
        return new StatsResponse(countMutantDna, countHumanDna, ratio);
    }

    /**
     * Calcula el ratio: mutantes / humanos. Maneja el caso de división por cero.
     * @param countMutant Número de mutantes.
     * @param countHuman Número de humanos.
     * @return El ratio o 0.0 si no hay humanos para evitar división por cero.
     */
    private double calculateRatio(long countMutant, long countHuman) {
        if (countHuman == 0) {
            // Si no hay humanos, el ratio es el número de mutantes (o 0 si no hay nada).
            // Esto es un manejo de caso borde.
            return (double) countMutant;
        }
        // Conversión a double para realizar la división de punto flotante
        return (double) countMutant / countHuman;
    }
}