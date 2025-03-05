package com.digitalhouse.court_rental.repository;

import com.digitalhouse.court_rental.entity.Feature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeatureRepository extends JpaRepository<Feature, Long> {

    List<Feature> findByStatus_IdStatus(int idStatus);

    @Modifying
    @Query("UPDATE Feature f SET f.status.idStatus = :newStatus WHERE f.idFeature = :id")
    void updateFeatureStatus(@Param("id") Long id, @Param("newStatus") int newStatus);
}
