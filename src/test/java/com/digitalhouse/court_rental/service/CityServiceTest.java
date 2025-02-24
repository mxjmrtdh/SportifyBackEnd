package com.digitalhouse.court_rental.service;

import com.digitalhouse.court_rental.dto.CityDTO;
import com.digitalhouse.court_rental.entity.court.City;
import com.digitalhouse.court_rental.repository.CityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

}