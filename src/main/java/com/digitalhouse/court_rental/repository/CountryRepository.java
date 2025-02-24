package com.digitalhouse.court_rental.repository;

import com.digitalhouse.court_rental.dto.CountryDTO;
import com.digitalhouse.court_rental.entity.court.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CountryRepository extends JpaRepository<Country, Integer> {

    @Query("SELECT new com.digitalhouse.court_rental.dto.CountryDTO(c.idCountry, c.countryName) FROM Country c")
    List<CountryDTO> findAllCountries();
}
