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
@RequiredArgsConstructor
@Slf4j
public class MutantService {

    private final MutantDetector mutantDetector;
    private final DnaRecordRepository dnaRecordRepository;

    @Transactional
    public boolean analyzeDna(String[] dna) {
        String dnaHash = calculateDnaHash(dna);

        Optional<DnaRecord> existingRecord = dnaRecordRepository.findByDnaHash(dnaHash);

        if (existingRecord.isPresent()) {
            log.info("ADN {} encontrado en caché. Resultado: {}", dnaHash, existingRecord.get().isMutant());
            return existingRecord.get().isMutant();
        }

        boolean isMutantResult = mutantDetector.isMutant(dna);

        DnaRecord newRecord = new DnaRecord();
        newRecord.setDnaHash(dnaHash);
        newRecord.setMutant(isMutantResult);
        newRecord.setCreatedAt(LocalDateTime.now());
        dnaRecordRepository.save(newRecord);

        log.info("Nuevo ADN analizado y guardado. Hash: {}, Mutante: {}", dnaHash, isMutantResult);

        return isMutantResult;
    }

    private String calculateDnaHash(String[] dna) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String dnaString = String.join("", dna);
            byte[] hashBytes = digest.digest(dnaString.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("Error al calcular el hash SHA-256", e);
            throw new DnaHashCalculationException("Error al calcular el hash de ADN.", e);
        }
    }
}
