package com.digitalhouse.court_rental.service;

import com.digitalhouse.court_rental.dto.CountryDTO;
import com.digitalhouse.court_rental.repository.CountryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CountryServiceTest {
    @Mock
    private CountryRepository countryRepository;

    private CountryService countryService;

    @BeforeEach
    void setUp() {
        countryService = new CountryService(countryRepository);
    }

    @Test
    void testGetAllCountries() {
        CountryDTO country1 = new CountryDTO(1, "Argentina");
        CountryDTO country2 = new CountryDTO(2, "Brasil");

        when(countryRepository.findAllCountries()).thenReturn(List.of(country1, country2));

        List<CountryDTO> result = countryService.getAllCountries();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getIdCountry());
        assertEquals("Argentina", result.get(0).getCountryName());
        assertEquals(2, result.get(1).getIdCountry());
        assertEquals("Brasil", result.get(1).getCountryName());

        verify(countryRepository, times(1)).findAllCountries();
    }

}