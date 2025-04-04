package com.digitalhouse.court_rental.service;

import com.digitalhouse.court_rental.dto.CityDTO;
import com.digitalhouse.court_rental.entity.court.City;
import com.digitalhouse.court_rental.entity.court.Country;
import com.digitalhouse.court_rental.entity.court.Region;
import com.digitalhouse.court_rental.repository.CityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CityServiceTest {

    @Mock
    private CityRepository cityRepository;

    private CityService cityService;

    @BeforeEach
    void setUp() {
        cityService = new CityService(cityRepository);
    }

    @Test
    void testFindByRegionId() {
        City city1 = new City();
        city1.setIdCity(1);
        city1.setCityName("Ciudad A");

        City city2 = new City();
        city2.setIdCity(2);
        city2.setCityName("Ciudad B");

        when(cityRepository.findByRegionId(1)).thenReturn(List.of(city1, city2));

        List<CityDTO> result = cityService.findByRegionId(1);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Ciudad A", result.get(0).getName());
        assertEquals(2L, result.get(1).getId());
        assertEquals("Ciudad B", result.get(1).getName());

        verify(cityRepository, times(1)).findByRegionId(1);
    }

    @Test
    void testFindByRegionId_NoCitiesFound() {
        when(cityRepository.findByRegionId(1)).thenReturn(Collections.emptyList());

        List<CityDTO> result = cityService.findByRegionId(1);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(cityRepository, times(1)).findByRegionId(1);
    }

    @Test
    void testFindAllCities() {
        Country country = new Country();
        country.setIdCountry(1);
        country.setCountryName("Country A");

        Region region = new Region();
        region.setIdRegion(1);
        region.setRegionName("Region A");
        region.setCountry(country);

        City city1 = new City();
        city1.setIdCity(1);
        city1.setCityName("Ciudad A");
        city1.setRegion(region);

        City city2 = new City();
        city2.setIdCity(2);
        city2.setCityName("Ciudad B");
        city2.setRegion(region);

        when(cityRepository.findAllCities()).thenReturn(List.of(city1, city2));

        List<CityDTO> result = cityService.findAllCities();

        assertNotNull(result);
        assertEquals(2, result.size());

        CityDTO cityDTO1 = result.getFirst();
        assertEquals(1L, cityDTO1.getId());
        assertEquals("Ciudad A", cityDTO1.getName());
        assertNotNull(cityDTO1.getRegion());
        assertEquals(1L, cityDTO1.getRegion().getId());
        assertEquals("Region A", cityDTO1.getRegion().getName());
        assertNotNull(cityDTO1.getRegion().getCountry());
        assertEquals(1, cityDTO1.getRegion().getCountry().getIdCountry());
        assertEquals("Country A", cityDTO1.getRegion().getCountry().getCountryName());

        verify(cityRepository, times(1)).findAllCities();
    }

    @Test
    void testFindAllCities_NoCitiesFound() {
        when(cityRepository.findAllCities()).thenReturn(Collections.emptyList());

        List<CityDTO> result = cityService.findAllCities();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(cityRepository, times(1)).findAllCities();
    }
}