package com.altaroad.digitrack.controller;

import com.altaroad.digitrack.dto.RecomatFluxDto;
import com.altaroad.digitrack.service.MaterialService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

class RecomatFluxControllerTest {

    private static final String CARTON = "CARTON";
    private static final String PLASTIQUE = "PLASTIQUE";
    private static final String DECHETS_EN_MELANGE = "DECHETS EN MELANGE";
    private static final String CARTON_DECHETS = "CARTON+DECHETS";
    public static final String DONNEES_DE_REPARTITION_MANQUANTES = "Données de répartition manquantes";

    @Mock
    private MaterialService materialService;

    @InjectMocks
    private RecomatFluxController recomatFluxController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should recognize dominant material with valid repartition")
    void testRecognizeDominantMaterialWithValidRepartition() {
        // given
        Map<String, Integer> repartition = new HashMap<>();
        repartition.put(CARTON, 90);
        repartition.put(PLASTIQUE, 10);
        RecomatFluxDto dto = new RecomatFluxDto(repartition);
        when(materialService.recognizeMaterial(repartition)).thenReturn(CARTON);

        // when
        ResponseEntity<String> response = recomatFluxController.analyzeFlux(dto);

        // then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("CARTON");
        verify(materialService).recognizeMaterial(repartition);
    }

    @Test
    @DisplayName("Should recognize mixed material with dominant material")
    void testRecognizeMixedMaterialWithDominantMaterial() {
        // given
        Map<String, Integer> repartition = new HashMap<>();
        repartition.put(CARTON, 85);
        repartition.put(PLASTIQUE, 15);
        RecomatFluxDto dto = new RecomatFluxDto(repartition);
        when(materialService.recognizeMaterial(repartition)).thenReturn(CARTON_DECHETS);

        // when
        ResponseEntity<String> response = recomatFluxController.analyzeFlux(dto);

        // then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("CARTON+DECHETS");
        verify(materialService).recognizeMaterial(repartition);
    }

    @Test
    @DisplayName("Should recognize single material at 100%")
    void testRecognizeSingleMaterialAtFullPercentage() {
        // given
        Map<String, Integer> repartition = new HashMap<>();
        repartition.put(CARTON, 100);
        RecomatFluxDto dto = new RecomatFluxDto(repartition);
        when(materialService.recognizeMaterial(repartition)).thenReturn(CARTON);

        // when
        ResponseEntity<String> response = recomatFluxController.analyzeFlux(dto);

        // then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("CARTON");
        verify(materialService).recognizeMaterial(repartition);
    }

    @Test
    @DisplayName("Should handle null request body")
    void testAnalyzeFluxWithNullRequestBody() {
        // when
        ResponseEntity<String> response = recomatFluxController.analyzeFlux(null);

        // then
        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isEqualTo(DONNEES_DE_REPARTITION_MANQUANTES);
        verifyNoInteractions(materialService);
    }

    @Test
    @DisplayName("Should handle null or missing repartition")
    void testAnalyzeFluxWithNullOrMissingRepartition() {
        // given
        RecomatFluxDto dto = new RecomatFluxDto(null);

        // when
        ResponseEntity<String> response = recomatFluxController.analyzeFlux(dto);

        // then
        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isEqualTo(DONNEES_DE_REPARTITION_MANQUANTES);
        verifyNoInteractions(materialService);
    }

    @Test
    @DisplayName("Should handle empty or no dominant repartition")
    void testRecognizeMaterialWithEmptyOrNoDominantRepartition() {
        // given
        Map<String, Integer> repartition = Collections.emptyMap();
        RecomatFluxDto dto = new RecomatFluxDto(repartition);
        when(materialService.recognizeMaterial(repartition)).thenReturn(DECHETS_EN_MELANGE);

        // when
       var response = recomatFluxController.analyzeFlux(dto);

        // then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(DECHETS_EN_MELANGE);
        verify(materialService).recognizeMaterial(repartition);
    }
}