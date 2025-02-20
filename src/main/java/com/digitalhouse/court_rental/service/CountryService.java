package com.digitalhouse.court_rental.service;

import com.digitalhouse.court_rental.dto.CountryDTO;
import com.digitalhouse.court_rental.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CountryService {
    private final CountryRepository countryRepository;

    public List<CountryDTO> getAllCountries() {
        return countryRepository.findAllCountries();
    }
}
