package com.digitalhouse.court_rental.repository;

import com.digitalhouse.court_rental.entity.court.City;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Integer>{
}
