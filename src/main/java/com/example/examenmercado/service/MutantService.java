package com.example.examenmercado.service;

import com.example.examenmercado.entity.DnaRecord;
import com.example.examenmercado.exception.DnaHashCalculationException;
import com.example.examenmercado.repository.DnaRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor // Lombok: Inyecta dependencias automáticamente (MutantDetector y Repository)
@Slf4j
public class MutantService {

    private final MutantDetector mutantDetector;
    private final DnaRecordRepository dnaRecordRepository;

    /**
     * Analiza una secuencia de ADN. Implementa la lógica de caché/deduplicación.
     * 1. Calcula hash.
     * 2. Busca en BD (caché).
     * 3. Si no existe, llama al algoritmo y guarda.
     * @param dna La secuencia de ADN.
     * @return true si es mutante, false si es humano.
     */
    @Transactional
    public boolean analyzeDna(String[] dna) {
        // 1. Calcular Hash para Deduplicación
        String dnaHash = calculateDnaHash(dna);

        // 2. Consultar caché (Base de Datos - Solo 1 registro por ADN)
        Optional<DnaRecord> existingRecord = dnaRecordRepository.findByDnaHash(dnaHash);

        if (existingRecord.isPresent()) {
            log.info("ADN {} encontrado en caché. Resultado: {}", dnaHash, existingRecord.get().isMutant());
            return existingRecord.get().isMutant(); // Retorna resultado cacheado
        }

        // 3. Analizar ADN con el algoritmo (si no está en caché)
        boolean isMutantResult = mutantDetector.isMutant(dna);

        // 4. Guardar resultado en BD (Persistencia)
        DnaRecord newRecord = new DnaRecord();
        newRecord.setDnaHash(dnaHash);
        newRecord.setMutant(isMutantResult);
        newRecord.setCreatedAt(LocalDateTime.now());
        dnaRecordRepository.save(newRecord);

        log.info("Nuevo ADN analizado y guardado. Hash: {}, Mutante: {}", dnaHash, isMutantResult);

        return isMutantResult;
    }

    /**
     * Calcula el hash SHA-256 de la secuencia de ADN.
     * Esta función garantiza que el mismo ADN siempre genere el mismo hash.
     * @param dna Array de Strings del ADN.
     * @return Hash SHA-256 en formato hexadecimal (64 caracteres).
     */
    private String calculateDnaHash(String[] dna) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // Concatenar el array de Strings en uno solo para el cálculo
            String dnaString = String.join("", dna);
            byte[] hashBytes = digest.digest(dnaString.getBytes(StandardCharsets.UTF_8));

            // Convertir el array de bytes a formato hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 es un algoritmo estándar, pero es buena práctica manejar la excepción
            log.error("Error al calcular el hash SHA-256", e);
            throw new DnaHashCalculationException("Error al calcular el hash de ADN.", e);
        }
    }
}
