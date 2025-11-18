package com.example.examenmercado.service;

import com.example.examenmercado.entity.DnaRecord;
import com.example.examenmercado.repository.DnaRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MutantServiceTest {

    @Mock
    private MutantDetector mutantDetector;

    @Mock
    private DnaRecordRepository dnaRecordRepository;

    @InjectMocks
    private MutantService mutantService;

    private String[] mutantDna;
    private String[] humanDna;

    @BeforeEach
    void setUp() {
        mutantDna = new String[]{"ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG"};
        humanDna = new String[]{"ATGCGA", "CAGTGC", "TTATTT", "AGACGG", "GCGTCA", "TCACTG"};
    }

    @Test
    @DisplayName("SERVICIO: Debe analizar ADN mutante, llamar al detector y guardarlo en BD")
    void testAnalyzeDna_NewMutant() {
        // 1. Simular que el ADN no existe en BD (cache miss)
        when(dnaRecordRepository.findByDnaHash(anyString())).thenReturn(Optional.empty());
        // 2. Simular que el algoritmo detecta mutante
        when(mutantDetector.isMutant(mutantDna)).thenReturn(true);

        boolean result = mutantService.analyzeDna(mutantDna);

        assertTrue(result);
        // Verificar que se llamó al detector y que se guardó
        verify(mutantDetector, times(1)).isMutant(mutantDna);
        verify(dnaRecordRepository, times(1)).save(any(DnaRecord.class));
    }

    @Test
    @DisplayName("SERVICIO: Debe analizar ADN humano, llamar al detector y guardarlo en BD")
    void testAnalyzeDna_NewHuman() {
        when(dnaRecordRepository.findByDnaHash(anyString())).thenReturn(Optional.empty());
        // Simular que el algoritmo detecta humano
        when(mutantDetector.isMutant(humanDna)).thenReturn(false);

        boolean result = mutantService.analyzeDna(humanDna);

        assertFalse(result);
        verify(mutantDetector, times(1)).isMutant(humanDna);
        verify(dnaRecordRepository, times(1)).save(any(DnaRecord.class));
    }

    @Test
    @DisplayName("SERVICIO: Debe retornar resultado cacheado si el ADN ya fue analizado")
    void testAnalyzeDna_CacheHit() {
        // Simular que el ADN ya existe en BD y es mutante (cache hit)
        DnaRecord cachedRecord = new DnaRecord();
        when(dnaRecordRepository.findByDnaHash(anyString())).thenReturn(Optional.of(cachedRecord));

        boolean result = mutantService.analyzeDna(mutantDna);

        assertFalse(result);

        // Debe evitar llamar al detector y a save (Optimización de caché)
        verify(mutantDetector, never()).isMutant(any());
        verify(dnaRecordRepository, never()).save(any(DnaRecord.class));
    }

    @Test
    @DisplayName("SERVICIO: Debe guardar el registro con el resultado correcto (mutante)")
    void testAnalyzeDna_SaveCorrectResult() {
        when(dnaRecordRepository.findByDnaHash(anyString())).thenReturn(Optional.empty());
        when(mutantDetector.isMutant(mutantDna)).thenReturn(true);

        // Capturador para inspeccionar el objeto guardado
        ArgumentCaptor<DnaRecord> captor = ArgumentCaptor.forClass(DnaRecord.class);

        mutantService.analyzeDna(mutantDna);

        // Verificar que el objeto guardado tiene isMutant = true
        verify(dnaRecordRepository).save(captor.capture());
        assertTrue(captor.getValue().isMutant());
    }

    @Test
    @DisplayName("SERVICIO: Debe guardar el registro con el resultado correcto (humano)")
    void testAnalyzeDna_SaveCorrectResult_Human() {
        when(dnaRecordRepository.findByDnaHash(anyString())).thenReturn(Optional.empty());
        when(mutantDetector.isMutant(humanDna)).thenReturn(false);

        ArgumentCaptor<DnaRecord> captor = ArgumentCaptor.forClass(DnaRecord.class);

        mutantService.analyzeDna(humanDna);

        // Verificar que el objeto guardado tiene isMutant = false
        verify(dnaRecordRepository).save(captor.capture());
        assertFalse(captor.getValue().isMutant());
    }
}
