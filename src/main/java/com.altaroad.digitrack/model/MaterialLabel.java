package com.altaroad.digitrack.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "material_labels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialLabel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category1", nullable = false)
    private String category1;

    @Column(name = "category2")
    private String category2;

    @Column(name = "label", nullable = false)
    private String label;

    @Column(name = "min_pct1", nullable = false)
    private Integer minPct1;

    @Column(name = "max_pct1", nullable = false)
    private Integer maxPct1;

    @Column(name = "min_pct2", nullable = false)
    private Integer minPct2;

    @Column(name = "max_pct2", nullable = false)
    private Integer maxPct2;
}
