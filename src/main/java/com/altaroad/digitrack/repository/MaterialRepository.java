package com.altaroad.digitrack.repository;

import com.altaroad.digitrack.model.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
    Material findByLabel(String label);
}
