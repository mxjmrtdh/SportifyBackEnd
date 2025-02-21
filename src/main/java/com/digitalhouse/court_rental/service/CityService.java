package com.digitalhouse.court_rental.service;

import com.digitalhouse.court_rental.dto.CityDTO;
import com.digitalhouse.court_rental.entity.court.City;
import com.digitalhouse.court_rental.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CityService {

    private final CityRepository cityRepository;

    public List<CityDTO> findByRegionId(Integer idRegion) {
        List<City> cities = cityRepository.findByRegionId(idRegion);
        return cities.stream()
                .map(city -> new CityDTO((long) city.getIdCity(), city.getCityName(), null))
                .collect(Collectors.toList());
    }
}
