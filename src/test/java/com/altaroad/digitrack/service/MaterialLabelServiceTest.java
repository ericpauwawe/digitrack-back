package com.altaroad.digitrack.service;

import com.altaroad.digitrack.model.Constants;
import com.altaroad.digitrack.model.MaterialLabel;
import com.altaroad.digitrack.repository.MaterialLabelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class MaterialLabelServiceTest {
    public static final String CARTON = "Carton";
    public static final String SABLE = "Sable";
    private MaterialLabelService service;

    @BeforeEach
    void setUp() {
        MaterialLabelRepository repository = Mockito.mock(MaterialLabelRepository.class);

        MaterialLabel pureRule = MaterialLabel.builder()
                .id(1L)
                .category1(CARTON)
                .category2(null)
                .label(Constants.LABEL_PUR)
                .minPct1(85)
                .maxPct1(100)
                .minPct2(0)
                .maxPct2(100)
                .build();

        MaterialLabel binaryRule = MaterialLabel.builder()
                .id(2L)
                .category1(CARTON)
                .category2(SABLE)
                .label(Constants.LABEL_BINAIRE_DOMINANT)
                .minPct1(50)
                .maxPct1(85)
                .minPct2(0)
                .maxPct2(50)
                .build();

        MaterialLabel ternaryRule = MaterialLabel.builder()
                .id(3L)
                .category1(CARTON)
                .category2(SABLE)
                .label(Constants.LABEL_TERNAIRE)
                .minPct1(30)
                .maxPct1(70)
                .minPct2(30)
                .maxPct2(70)
                .build();

        when(repository.findAll())
                .thenReturn(List.of(pureRule, binaryRule, ternaryRule));

        service = new MaterialLabelService(repository);
    }

    @Test
    void should_return_pure_label_when_percentage_above_or_equal_85() {
        Map<String, Integer> repartition = Map.of(CARTON, 90);
        String label = service.getLabel(repartition);
        assertThat(label).isEqualTo(Constants.LABEL_PUR);
    }

    @Test
    void should_return_binaire_dominant_when_a_between_50_and_85_and_b_between_0_and_50() {
        Map<String, Integer> repartition = Map.of(CARTON, 60, SABLE, 40);
        String label = service.getLabel(repartition);
        assertThat(label).isEqualTo(Constants.LABEL_BINAIRE_DOMINANT);
    }

    @Test
    void should_return_ternaire_when_two_categories_above_30_each() {
        Map<String, Integer> repartition = Map.of(CARTON, 40, SABLE, 35, "C", 25);
        String label = service.getLabel(repartition);
        assertThat(label).isEqualTo(Constants.LABEL_TERNAIRE);
    }

    @Test
    void should_return_dechets_en_melange_when_no_rules_match() {
        Map<String, Integer> repartition = Map.of(CARTON, 10, SABLE, 10);
        String label = service.getLabel(repartition);
        assertThat(label).isEqualTo(Constants.LABEL_DECHETS_EN_MELANGE);
    }
}
