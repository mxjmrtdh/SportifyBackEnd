package com.digitalhouse.court_rental.service;

import com.digitalhouse.court_rental.dto.RegionDTO;
import com.digitalhouse.court_rental.entity.court.Region;
import com.digitalhouse.court_rental.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegionService {
    private final RegionRepository regionRepository;

    public List<RegionDTO> getRegionsByCountry(Long idCountry) {
        List<Region> regions = regionRepository.findByCountryId(idCountry);
        return regions.stream()
                .map(region -> new RegionDTO((long) region.getIdRegion(), region.getRegionName(), null))
                .collect(Collectors.toList());
    }
}
