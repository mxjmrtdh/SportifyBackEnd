package com.digitalhouse.court_rental.controller;

import com.digitalhouse.court_rental.dto.RegionDTO;
import com.digitalhouse.court_rental.service.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/regions")
public class RegionController {

    @Autowired
    private RegionService regionService;

    @GetMapping("/by-country/{idCountry}")
    public List<RegionDTO> getRegionsByCountry(@PathVariable Long idCountry) {
        return regionService.getRegionsByCountry(idCountry);
    }
}
