package com.example.examenmercado.service;

import com.example.examenmercado.dto.StatsResponse;
import com.example.examenmercado.repository.DnaRecordRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

    @Mock
    private DnaRecordRepository dnaRecordRepository;

    @InjectMocks
    private StatsService statsService;

    // Constante para el delta de comparación de doubles
    private static final double DELTA = 0.0001;

    @Test
    @DisplayName("ESTADÍSTICAS: Debe calcular el ratio correctamente (40/100 = 0.4)")
    void testGetStats_StandardRatio() {
        when(dnaRecordRepository.countByIsMutant(true)).thenReturn(40L);
        when(dnaRecordRepository.countByIsMutant(false)).thenReturn(100L);

        StatsResponse stats = statsService.getStats();

        assertEquals(40, stats.getCountMutantDna());
        assertEquals(100, stats.getCountHumanDna());
        assertEquals(0.4, stats.getRatio(), DELTA);
    }

    @Test
    @DisplayName("ESTADÍSTICAS: Debe retornar ratio 0.0 cuando no hay mutantes")
    void testGetStats_ZeroMutants() {
        when(dnaRecordRepository.countByIsMutant(true)).thenReturn(0L);
        when(dnaRecordRepository.countByIsMutant(false)).thenReturn(50L);

        StatsResponse stats = statsService.getStats();

        assertEquals(0, stats.getCountMutantDna());
        assertEquals(50, stats.getCountHumanDna());
        assertEquals(0.0, stats.getRatio(), DELTA);
    }

    @Test
    @DisplayName("ESTADÍSTICAS: Debe manejar el caso de CERO HUMANOS (ratio = countMutant)")
    void testGetStats_ZeroHumans() {
        when(dnaRecordRepository.countByIsMutant(true)).thenReturn(10L);
        when(dnaRecordRepository.countByIsMutant(false)).thenReturn(0L);

        StatsResponse stats = statsService.getStats();

        assertEquals(10, stats.getCountMutantDna());
        assertEquals(0, stats.getCountHumanDna());
        assertEquals(10.0, stats.getRatio(), DELTA);
    }

    @Test
    @DisplayName("ESTADÍSTICAS: Debe retornar ratio 0.0 cuando no hay datos")
    void testGetStats_NoData() {
        when(dnaRecordRepository.countByIsMutant(true)).thenReturn(0L);
        when(dnaRecordRepository.countByIsMutant(false)).thenReturn(0L);

        StatsResponse stats = statsService.getStats();

        assertEquals(0, stats.getCountMutantDna());
        assertEquals(0, stats.getCountHumanDna());
        assertEquals(0.0, stats.getRatio(), DELTA);
    }

    @Test
    @DisplayName("ESTADÍSTICAS: Debe calcular ratio > 1 correctamente (100/50 = 2.0)")
    void testGetStats_RatioGreaterThanOne() {
        when(dnaRecordRepository.countByIsMutant(true)).thenReturn(100L);
        when(dnaRecordRepository.countByIsMutant(false)).thenReturn(50L);

        StatsResponse stats = statsService.getStats();

        assertEquals(100, stats.getCountMutantDna());
        assertEquals(50, stats.getCountHumanDna());
        assertEquals(2.0, stats.getRatio(), DELTA);
    }
}
