package com.altaroad.digitrack.controller;

import com.altaroad.digitrack.dto.RecomatFluxDto;
import com.altaroad.digitrack.service.MaterialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recomat/flux")
@RequiredArgsConstructor
public class RecomatFluxController {

    private final MaterialService materialService;


    @Operation(
            summary = "Reconnaître la matière dominante d’un flux",
            description = "Retourne la matière principale détectée dans un flux de déchets ou matériaux à partir d’une répartition fournie.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RecomatFluxDto.class, example = "{\"repartition\": {\"CARTON\": 85, \"PLASTIQUE\": 15}}")
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Le matériau dominant reconnu.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{\"label\": \"CARTON\", \"confidence\": 100}")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Erreur de format d’entrée ou données invalides",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{\"error\": \"Repartition manquante ou malformée\"}")
                            )
                    )
            }
    )



    @PostMapping("/recognize")
    public ResponseEntity<String> analyzeFlux(@RequestBody RecomatFluxDto recomatFluxDto) {
        if (recomatFluxDto == null || recomatFluxDto.getRepartition() == null)
            return ResponseEntity.badRequest().body("Données de répartition manquantes");

        return ResponseEntity.ok(materialService.recognizeMaterial(recomatFluxDto.getRepartition()));
    }
}