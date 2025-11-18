package com.example.examenmercado.dto;

import com.example.examenmercado.validation.ValidDnaSequence;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request para verificar si una secuencia de ADN pertenece a un mutante")
public class DnaRequest {

    @NotNull(message = "El array de ADN no puede ser null.")
    @NotEmpty(message = "El array de ADN no puede estar vacío.")
    @ValidDnaSequence
    @Schema(
            description = "Secuencia de ADN como matriz NxN de Strings. Solo caracteres A, T, C, G.",
            example = "[\"ATGCGA\",\"CAGTGC\",\"TTATGT\",\"AGAAGG\",\"CCCCTA\",\"TCACTG\"]"
    )
    private String[] dna;
}
