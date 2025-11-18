package com.example.examenmercado.repository;

import com.example.examenmercado.entity.DnaRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DnaRecordRepository extends JpaRepository<DnaRecord, Long> {

    /**
     * Buscar un registro de ADN por su hash. Usado para la deduplicación (caché).
     * @param dnaHash El hash SHA-256 de la secuencia de ADN.
     * @return Un Optional que contiene el registro si se encuentra.
     */
    Optional<DnaRecord> findByDnaHash(String dnaHash);

    /**
     * Contar cuántos registros tienen el campo isMutant con el valor dado.
     * Usado para el endpoint /stats.
     * @param isMutant true (mutantes) o false (humanos).
     * @return El número de registros.
     */
    long countByIsMutant(boolean isMutant);
}