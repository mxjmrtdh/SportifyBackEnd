package com.digitalhouse.court_rental.controller;

import com.digitalhouse.court_rental.dto.CountryDTO;
import com.digitalhouse.court_rental.dto.RegionDTO;
import com.digitalhouse.court_rental.service.RegionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.hamcrest.Matchers.*;

@ExtendWith(MockitoExtension.class)
class RegionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RegionService regionService;

    @InjectMocks
    private RegionController regionController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(regionController).build();
    }

    @Test
    void testGetRegionsByCountry_Success() throws Exception {
        CountryDTO country = new CountryDTO();
        country.setIdCountry(1);
        country.setCountryName("Argentina");

        RegionDTO region1 = new RegionDTO(1L, "Región Norte", country);
        RegionDTO region2 = new RegionDTO(2L, "Región Sur", country);

        List<RegionDTO> regions = List.of(region1, region2);

        when(regionService.getRegionsByCountry(1L)).thenReturn(regions);

        mockMvc.perform(get("/api/regions/by-country/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Región Norte"))
                .andExpect(jsonPath("$[0].country.idCountry").value(1))
                .andExpect(jsonPath("$[0].country.countryName").value("Argentina"));
    }



    @Test
    void testGetRegionsByCountry_EmptyList() throws Exception {
        when(regionService.getRegionsByCountry(99L)).thenReturn(List.of());

        mockMvc.perform(get("/api/regions/by-country/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

}