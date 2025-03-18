package com.digitalhouse.court_rental.repository;

import com.digitalhouse.court_rental.entity.court.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CityRepository extends JpaRepository<City, Integer> {
    @Query("SELECT c FROM City c WHERE c.region.id = :idRegion")
    List<City> findByRegionId(@Param("idRegion") Integer idRegion);

    @Query("SELECT c FROM City c JOIN FETCH c.region r JOIN FETCH r.country")
    List<City> findAllCities();
}
