package com.altaroad.digitrack.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class FluxDto {
    private String id;
    private Map<String, Integer> repartition;

    @JsonCreator
    public FluxDto(
            @JsonProperty("id") String id,
            @JsonProperty("repartition") Map<String, Integer> repartition
    ) {
        this.id = id;
        this.repartition = repartition;
    }
}
