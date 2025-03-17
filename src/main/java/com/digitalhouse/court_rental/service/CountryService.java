package com.digitalhouse.court_rental.service;

import com.digitalhouse.court_rental.dto.CountryDTO;
import com.digitalhouse.court_rental.entity.court.Country;
import com.digitalhouse.court_rental.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CountryService {
    private final CountryRepository countryRepository;

    public List<CountryDTO> getAllCountries() {
        return countryRepository.findAllCountries();
    }

    public Optional<Country> getCountriesById(Integer countryId) {
        return countryRepository.findById(countryId);
    }
}
