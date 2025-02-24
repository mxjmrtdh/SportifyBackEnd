package com.digitalhouse.court_rental.controller;

import com.digitalhouse.court_rental.dto.CountryDTO;
import com.digitalhouse.court_rental.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/countries")
@RequiredArgsConstructor
public class CountryController {

    private final CountryService countryService;

    @GetMapping("/search")
    public ResponseEntity<List<CountryDTO>> getAllCountries() {
        return ResponseEntity.ok(countryService.getAllCountries());
    }
}
