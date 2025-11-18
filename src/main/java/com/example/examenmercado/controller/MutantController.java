package com.example.examenmercado.controller;

import com.example.examenmercado.dto.DnaRequest;
import com.example.examenmercado.dto.StatsResponse;
import com.example.examenmercado.service.MutantService;
import com.example.examenmercado.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Tag(name = "Mutant Detector", description = "API para la detección de ADN mutante y estadísticas.")
public class MutantController {

    private final MutantService mutantService;
    private final StatsService statsService;

    @PostMapping("/mutant")
    @Operation(summary = "Verificar si un ADN es mutante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ADN es de un mutante."),
            @ApiResponse(responseCode = "403", description = "ADN es de un humano."),
            @ApiResponse(responseCode = "400", description = "El formato de ADN es inválido o no es una matriz NxN.")
    })
    public ResponseEntity<Void> checkMutant(@Valid @RequestBody DnaRequest request) {

        // La validación de formato (NxN, A/T/C/G) ocurre en el MutantDetector
        // y la validación de DTO (@NotNull, @NotEmpty) ocurre aquí con @Valid

        boolean isMutantResult = mutantService.analyzeDna(request.getDna());

        if (isMutantResult) {
            // Requerimiento: HTTP 200 OK si es mutante
            return ResponseEntity.ok().build();
        } else {
            // Requerimiento: HTTP 403 Forbidden si no es mutante
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/stats")
    @Operation(summary = "Obtener estadísticas de las verificaciones de ADN")
    @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas exitosamente.")
    public ResponseEntity<StatsResponse> getStats() {
        StatsResponse stats = statsService.getStats();
        // El DTO StatsResponse se serializa automáticamente a JSON con snake_case
        return ResponseEntity.ok(stats);
    }
}
