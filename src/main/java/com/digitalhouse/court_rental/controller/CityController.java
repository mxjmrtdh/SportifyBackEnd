package com.digitalhouse.court_rental.controller;

import com.digitalhouse.court_rental.dto.CityDTO;
import com.digitalhouse.court_rental.service.CityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cities")
public class CityController {

    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping("/by-region/{idRegion}")
    public ResponseEntity<List<CityDTO>> getCitiesByRegion(@PathVariable Integer idRegion) {
        List<CityDTO> cities = cityService.findByRegionId(idRegion);
        return ResponseEntity.ok(cities);
    }
}
