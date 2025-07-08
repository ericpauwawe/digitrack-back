package com.altaroad.digitrack.service;

import com.altaroad.digitrack.model.Material;
import com.altaroad.digitrack.repository.MaterialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class MaterialServiceTest {

    public static final String CARTON = "CARTON";
    public static final String TERRE = "TERRE";
    public static final String SABLE = "SABLE";
    private MaterialService materialService;

    @Mock
    private MaterialRepository materialRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        materialService = new MaterialService(materialRepository, 85);
    }

    @Test
     void testRecognizeMaterial_WhenRepartitionIsNullOrEmpty_ShouldReturnDefault() {
        //given: no repartition provided

        //when
        String resultNull = materialService.recognizeMaterial(null);
        String resultEmpty = materialService.recognizeMaterial(Map.of());

        //then
        assertThat(resultNull).isEqualTo(MaterialService.DECHETS_EN_MELANGE);
        assertThat(resultEmpty).isEqualTo(MaterialService.DECHETS_EN_MELANGE);
    }

    @Test
     void testRecognizeMaterial_WhenSingleCartonWithFullPercentage_ShouldReturnCarton() {
        //given
        Map<String, Integer> repartition = Map.of(CARTON, 100);
        Material carton = new Material(CARTON, false);
        when(materialRepository.findByLabel(CARTON)).thenReturn(carton);

        //when
        String result = materialService.recognizeMaterial(repartition);

        //then
        assertThat(result).isEqualTo(CARTON);
    }

    @Test
     void testRecognizeMaterial_WhenNoDominantMaterialFound_ShouldReturnDefault() {
        //given
        Map<String, Integer> repartition = Map.of(TERRE, 50, SABLE, 50);
        when(materialRepository.findByLabel(TERRE)).thenReturn(null);

        //when
        String result = materialService.recognizeMaterial(repartition);

        //then
        assertThat(result).isEqualTo(MaterialService.DECHETS_EN_MELANGE);
    }

    @Test
     void testRecognizeMaterial_WhenTerreIsWaste_ShouldReturnDefault() {
        //given
        Map<String, Integer> repartition = Map.of(TERRE, 70, SABLE, 30);
        Material terre = new Material(TERRE, true);
        when(materialRepository.findByLabel(TERRE)).thenReturn(terre);

        //when
        String result = materialService.recognizeMaterial(repartition);

        //then
        assertThat(result).isEqualTo(MaterialService.DECHETS_EN_MELANGE);
    }

    @Test
    void testRecognizeMaterial_WhenCartonMixedWithWaste_ShouldReturnCartonPlusWaste() {
        //given
        Map<String, Integer> repartition = Map.of(CARTON, 85, SABLE, 20);
        Material carton = new Material(CARTON, false);
        when(materialRepository.findByLabel(CARTON)).thenReturn(carton);

        //when
        String result = materialService.recognizeMaterial(repartition);

        //then
        assertThat(result).isEqualTo("CARTON+DECHETS");
    }

    @Test
    void testRecognizeMaterial_WhenCarton55AndSable45_ShouldReturnCartonPlusWaste() {
        //given
        Map<String, Integer> repartition = Map.of(CARTON, 55, SABLE, 45);
        Material carton = new Material(CARTON, false);
        when(materialRepository.findByLabel(CARTON)).thenReturn(carton);

        //when
        String result = materialService.recognizeMaterial(repartition);

        //then
        assertThat(result).isEqualTo("DECHETS EN MELANGE");
    }

    @Test
    void testRecognizeMaterial_WhenCarton40AndSable30AndTerre30_ShouldReturnDefault() {
        //given
        Map<String, Integer> repartition = Map.of(CARTON, 40, SABLE, 30, TERRE, 30);
        Material carton = new Material(CARTON, false);
        when(materialRepository.findByLabel(CARTON)).thenReturn(carton);

        //when
        String result = materialService.recognizeMaterial(repartition);

        //then
        assertThat(result).isEqualTo(MaterialService.DECHETS_EN_MELANGE);
    }


}
