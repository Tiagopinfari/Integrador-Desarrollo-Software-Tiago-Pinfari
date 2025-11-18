package com.example.examenmercado.controller;

import com.example.examenmercado.dto.StatsResponse;
import com.example.examenmercado.service.MutantService;
import com.example.examenmercado.service.StatsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Solo carga la capa Web (Controller) y simula el ambiente HTTP
@WebMvcTest(MutantController.class)
class MutantControllerTest {

    @Autowired
    private MockMvc mockMvc; // Utilidad para simular peticiones HTTP

    @Autowired
    private ObjectMapper objectMapper; // Para convertir objetos Java a JSON

    // Sustituir los servicios reales por Mocks para AISLAR el controlador
    @MockBean
    private MutantService mutantService;

    @MockBean
    private StatsService statsService;

    private final String MUTANT_URL = "/mutant";
    private final String STATS_URL = "/stats";

    // ADN mutante de ejemplo para la request
    private final String MUTANT_DNA_JSON = """
            {
                "dna": ["ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG"]
            }
            """;

    // ADN con caracter inválido para probar el validador
    private final String INVALID_DNA_CHAR_JSON = """
            {
                "dna": ["ATGCGA","CAGTGC","TTATGT","AGAZGG","CCCCTA","TCACTG"]
            }
            """;

    // =======================================================================
    // TESTS PARA POST /mutant
    // =======================================================================

    @Test
    @DisplayName("INTEGRACIÓN: POST /mutant debe retornar 200 OK cuando es mutante")
    void testCheckMutant_Returns200Ok_WhenIsMutant() throws Exception {
        // ARRANGE: Simular que el servicio devuelve TRUE (Mutante)
        when(mutantService.analyzeDna(any(String[].class))).thenReturn(true);

        // ACT & ASSERT: Simular POST y esperar 200
        mockMvc.perform(post(MUTANT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MUTANT_DNA_JSON))
                .andExpect(status().isOk()); // HTTP 200 OK
    }

    @Test
    @DisplayName("INTEGRACIÓN: POST /mutant debe retornar 403 Forbidden cuando es humano")
    void testCheckMutant_Returns403Forbidden_WhenIsHuman() throws Exception {
        // ARRANGE: Simular que el servicio devuelve FALSE (Humano)
        when(mutantService.analyzeDna(any(String[].class))).thenReturn(false);

        // ACT & ASSERT: Simular POST y esperar 403
        mockMvc.perform(post(MUTANT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MUTANT_DNA_JSON)) // Usamos el mismo JSON, el mock controla el resultado
                .andExpect(status().isForbidden()); // HTTP 403 Forbidden
    }

    @Test
    @DisplayName("INTEGRACIÓN: POST /mutant debe retornar 400 Bad Request para DNA inválido")
    void testCheckMutant_Returns400BadRequest_ForInvalidDna() throws Exception {
        // La validación ocurre en la capa del Controller/DTO gracias a @ValidDnaSequence
        // Simular un JSON que falla la validación (carácter 'Z')

        // ACT & ASSERT: Simular POST con JSON inválido y esperar 400
        mockMvc.perform(post(MUTANT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(INVALID_DNA_CHAR_JSON))
                .andExpect(status().isBadRequest()) // HTTP 400 Bad Request
                // Opcional: Verificar que el cuerpo del error contiene el mensaje de validación
                .andExpect(jsonPath("$.message").exists());

        // VERIFY: El servicio no debe ser llamado
        verify(mutantService, org.mockito.Mockito.never()).analyzeDna(any());
    }

    // =======================================================================
    // TESTS PARA GET /stats
    // =======================================================================

    @Test
    @DisplayName("INTEGRACIÓN: GET /stats debe retornar 200 OK con el JSON de estadísticas")
    void testGetStats_Returns200OkWithCorrectJson() throws Exception {
        // ARRANGE
        StatsResponse mockStats = new StatsResponse(10, 20, 0.5);
        when(statsService.getStats()).thenReturn(mockStats);

        // ACT & ASSERT
        mockMvc.perform(get(STATS_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // HTTP 200 OK
                // Verificar los campos y valores del JSON (Snake case por @JsonNaming)
                .andExpect(jsonPath("$.count_mutant_dna").value(10))
                .andExpect(jsonPath("$.count_human_dna").value(20))
                .andExpect(jsonPath("$.ratio").value(0.5));
    }

    @Test
    @DisplayName("INTEGRACIÓN: GET /stats debe retornar 200 OK incluso con datos vacíos")
    void testGetStats_Returns200Ok_WhenNoData() throws Exception {
        // ARRANGE
        StatsResponse mockStats = new StatsResponse(0, 0, 0.0);
        when(statsService.getStats()).thenReturn(mockStats);

        // ACT & ASSERT
        mockMvc.perform(get(STATS_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count_mutant_dna").value(0))
                .andExpect(jsonPath("$.count_human_dna").value(0))
                .andExpect(jsonPath("$.ratio").value(0.0));
    }
}
