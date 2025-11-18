package com.example.examenmercado.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Lombok para getters/setters/constructores
@Data
@NoArgsConstructor
@AllArgsConstructor
// Usa snake_case para los nombres de las propiedades en el JSON (count_mutant_dna)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "Estadísticas de verificaciones de ADN")
public class StatsResponse {

    @Schema(description = "Número de secuencias de ADN mutantes verificadas.")
    private long countMutantDna;

    @Schema(description = "Número de secuencias de ADN humanos verificadas.")
    private long countHumanDna;

    @Schema(description = "Ratio: count_mutant_dna / count_human_dna.")
    private double ratio;
}