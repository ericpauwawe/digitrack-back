package com.altaroad.digitrack.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class RecomatFluxDto {

    private Map<String, Integer> repartition;

    @JsonCreator
    public RecomatFluxDto(@JsonProperty("repartition") Map<String, Integer> repartition) {
        this.repartition = repartition;
    }
}