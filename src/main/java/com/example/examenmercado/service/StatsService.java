package com.example.examenmercado.service;

import com.example.examenmercado.dto.StatsResponse;
import com.example.examenmercado.repository.DnaRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final DnaRecordRepository dnaRecordRepository;

    public StatsResponse getStats() {
        long countMutantDna = dnaRecordRepository.countByIsMutant(true);
        long countHumanDna = dnaRecordRepository.countByIsMutant(false);

        double ratio = calculateRatio(countMutantDna, countHumanDna);

        return new StatsResponse(countMutantDna, countHumanDna, ratio);
    }

    private double calculateRatio(long countMutant, long countHuman) {
        if (countHuman == 0) {
            return (double) countMutant;
        }

        return (double) countMutant / countHuman;
    }
}