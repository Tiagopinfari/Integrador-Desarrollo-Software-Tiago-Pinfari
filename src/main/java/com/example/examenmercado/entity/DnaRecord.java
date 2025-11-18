package com.example.examenmercado.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "dna_records", indexes = {
        // Índice en el hash para búsquedas rápidas (caché)
        @Index(name = "idx_dna_hash", columnList = "dnaHash"),
        // Índice en isMutant para conteo rápido de estadísticas
        @Index(name = "idx_is_mutant", columnList = "isMutant")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DnaRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Columna para almacenar el hash SHA-256 del ADN. Debe ser único.
    @Column(name = "dna_hash", unique = true, nullable = false, length = 64)
    private String dnaHash;

    // Columna para almacenar si es mutante o no (true/false)
    @Column(name = "is_mutant", nullable = false)
    private boolean isMutant;

    // Columna para la fecha de creación/registro
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

}
