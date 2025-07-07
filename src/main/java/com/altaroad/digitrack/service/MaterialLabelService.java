package com.altaroad.digitrack.service;

import com.altaroad.digitrack.model.MaterialLabel;
import com.altaroad.digitrack.repository.MaterialLabelRepository;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class MaterialLabelService {

    private final MaterialLabelRepository repository;


    public String getLabel(Map<String, Integer> repartition) {
        List<MaterialLabel> rules = repository.findAll();
        for (MaterialLabel rule : rules) {
            int category1Percentage = repartition.getOrDefault(rule.getCategory1(), 0);

            int category2percentage = rule.getCategory2() != null
                    ? repartition.getOrDefault(rule.getCategory2(), 0)
                    : 0;
            if (category1Percentage >= rule.getMinPct1() && category1Percentage <= rule.getMaxPct1()
                    && category2percentage >= rule.getMinPct2() && category2percentage <= rule.getMaxPct2()) {

                log.info("matiere correspondante : {} pour la repartition {}", rule.getLabel(), repartition);
                return rule.getLabel();
            }
        }
        log.info("aucune matiere correspondante pour la repartition {}", repartition);
        return "DECHETS EN MELANGE";
    }
}
