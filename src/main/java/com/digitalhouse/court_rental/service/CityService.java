package com.digitalhouse.court_rental.service;

import com.digitalhouse.court_rental.dto.CityDTO;
import com.digitalhouse.court_rental.dto.RegionDTO;
import com.digitalhouse.court_rental.dto.CountryDTO;
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

    public List<CityDTO> findAllCities() {
        List<City> cities = cityRepository.findAllCities();
        return cities.stream()
                .map(city -> new CityDTO(
                        (long) city.getIdCity(),
                        city.getCityName(),
                        new RegionDTO(
                                (long) city.getRegion().getIdRegion(),
                                city.getRegion().getRegionName(),
                                new CountryDTO(
                                        city.getRegion().getCountry().getIdCountry(),
                                        city.getRegion().getCountry().getCountryName()
                                )
                        )
                ))
                .collect(Collectors.toList());
    }
}
