package com.digitalhouse.court_rental.controller;

import com.digitalhouse.court_rental.dto.CountryDTO;
import com.digitalhouse.court_rental.service.CountryService;
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

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CountryControllerTest {
    private MockMvc mockMvc;

    @Mock
    private CountryService countryService;

    @InjectMocks
    private CountryController countryController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(countryController).build();
    }

    @Test
    void testGetAllCountries_Success() throws Exception {

        when(countryService.getAllCountries()).thenReturn(List.of(
                new CountryDTO(1, "Argentina"),
                new CountryDTO(2, "Brasil")
        ));

        mockMvc.perform(get("/api/countries/search")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].idCountry").value(1))
                .andExpect(jsonPath("$[0].countryName").value("Argentina"))
                .andExpect(jsonPath("$[1].idCountry").value(2))
                .andExpect(jsonPath("$[1].countryName").value("Brasil"));
    }

    @Test
    void testGetAllCountries_EmptyList() throws Exception {
        when(countryService.getAllCountries()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/countries/search")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

}