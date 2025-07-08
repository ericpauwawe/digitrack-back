package com.altaroad.digitrack.service;

import com.altaroad.digitrack.model.Material;
import com.altaroad.digitrack.repository.MaterialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class MaterialService {


    private int notWastePercentageThresold;
    public static final String DECHETS_EN_MELANGE = "DECHETS EN MELANGE";
    private final MaterialRepository materialRepository;

    @Autowired
    public MaterialService(MaterialRepository materialRepository, @Value("${material.seuil-dominant:85}") int notWastePercentageThresold) {
        this.materialRepository = materialRepository;
        this.notWastePercentageThresold = notWastePercentageThresold;
    }


    public String recognizeMaterial(Map<String, Integer> repartition) {
        log.info("Starting material recognition with repartition: {}", repartition);
        if (isRepartitionEmpty(repartition)) {
            log.info("Repartition is empty or null, returning: {}", DECHETS_EN_MELANGE);
            return DECHETS_EN_MELANGE;
        }

        Map.Entry<String, Integer> dominantEntry = findMainEntry(repartition);
        if (dominantEntry == null) {
            log.info("No dominant entry found, returning: {}", DECHETS_EN_MELANGE);
            return DECHETS_EN_MELANGE;
        }

        String result = getMaterialLabelOrDefault(dominantEntry, repartition.size());
        log.info("Material recognition completed. Result: {}", result);
        return result;
    }

    private boolean isRepartitionEmpty(Map<String, Integer> repartition) {
        return repartition == null || repartition.isEmpty();
    }

    private Map.Entry<String, Integer> findMainEntry(Map<String, Integer> repartition) {
        return repartition.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);
    }

    private String getMaterialLabelOrDefault(Map.Entry<String, Integer> dominantEntry, int repartitionSize) {
        String label = dominantEntry.getKey();
        int percentage = dominantEntry.getValue();
        log.info("Processing dominant entry - label: {}, percentage: {}%", label, percentage);
        Material mainMaterial = materialRepository.findByLabel(label);

        if (mainMaterial == null || mainMaterial.isWaste() || percentage < notWastePercentageThresold) {
            log.info("Material is null, waste, or below threshold ({}%). Returning: {}", notWastePercentageThresold, DECHETS_EN_MELANGE);
            return DECHETS_EN_MELANGE;
        }

        if (repartitionSize == 1 && percentage == 100) {
            log.info("Single material with 100% detected: {}", mainMaterial.getLabel());
            return mainMaterial.getLabel();
        }

        String result = mainMaterial.getLabel() + "+DECHETS";
        log.info("Mixed material detected: {}", result);
        return result;
    }
}
