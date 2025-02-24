package com.digitalhouse.court_rental.controller;

import com.digitalhouse.court_rental.dto.SportDTO;
import com.digitalhouse.court_rental.service.SportService;
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

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SportControllerTest {
    private MockMvc mockMvc;

    @Mock
    private SportService sportService;

    @InjectMocks
    private SportController sportController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(sportController).build();
    }

    @Test
    void testGetSportsByStatusFive_Success() throws Exception {
        SportDTO sport1 = new SportDTO(1L, "Fútbol");
        SportDTO sport2 = new SportDTO(2L, "Básquetbol");

        List<SportDTO> sports = List.of(sport1, sport2);

        when(sportService.findByStatusFive()).thenReturn(sports);

        mockMvc.perform(get("/sports/status/5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Fútbol"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Básquetbol"));
    }

}