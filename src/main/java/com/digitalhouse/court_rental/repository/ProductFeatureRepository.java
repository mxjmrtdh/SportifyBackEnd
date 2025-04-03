package com.digitalhouse.court_rental.repository;

import com.digitalhouse.court_rental.entity.ProductFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductFeatureRepository extends JpaRepository<ProductFeature, Integer> {
    @Modifying
    @Query("DELETE FROM ProductFeature pf WHERE pf.court.id = :courtId")
    void deleteByCourtId(@Param("courtId") Long courtId);
}
