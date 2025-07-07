package com.altaroad.digitrack.repository;

import com.altaroad.digitrack.model.MaterialLabel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialLabelRepository extends JpaRepository<MaterialLabel, Long> {
    // Ajoutez des méthodes de requête personnalisées ici si nécessaire
}
