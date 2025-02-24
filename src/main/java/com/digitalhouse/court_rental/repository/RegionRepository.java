package com.digitalhouse.court_rental.repository;

import com.digitalhouse.court_rental.entity.court.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RegionRepository extends JpaRepository<Region, Long> {

    @Query("SELECT r FROM Region r WHERE r.country.idCountry = :idCountry")
    List<Region> findByCountryId(@Param("idCountry") Long idCountry);
}
