package com.digitalhouse.court_rental.controller;

import com.digitalhouse.court_rental.dto.CityDTO;
import com.digitalhouse.court_rental.service.CityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CityControllerTest {
    private MockMvc mockMvc;

    @Mock
    private CityService cityService;

    @InjectMocks
    private CityController cityController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(cityController).build();
    }

    @Test
    void testGetCitiesByRegion_Success() throws Exception {
        CityDTO cityDTO = new CityDTO();
        cityDTO.setId(1L);
        cityDTO.setName("Lima");

        when(cityService.findByRegionId(1)).thenReturn(List.of(cityDTO));

        mockMvc.perform(get("/api/cities/by-region/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Lima"));
    }

    @Test
    void testGetCitiesByRegion_EmptyList() throws Exception {
        when(cityService.findByRegionId(2)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/cities/by-region/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

}