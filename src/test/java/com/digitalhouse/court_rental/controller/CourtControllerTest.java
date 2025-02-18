package com.digitalhouse.court_rental.controller;

import com.digitalhouse.court_rental.dto.CourtDTO;
import com.digitalhouse.court_rental.entity.Court;
import com.digitalhouse.court_rental.service.CourtService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
class CourtControllerTest {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private CourtService courtService;

    @InjectMocks
    private CourtController courtController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(courtController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testCreateCourt() throws Exception {
        Court court = new Court();
        court.setIdCourt(1);
        court.setCourtName("Cancha A");

        when(courtService.createCourt(any(Court.class))).thenReturn(court);

        mockMvc.perform(post("/api/courts/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(court)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idCourt").value(1))
                .andExpect(jsonPath("$.courtName").value("Cancha A"));
    }

    @Test
    void testGetAllCourts() throws Exception {
        CourtDTO courtDTO = new CourtDTO();
        courtDTO.setId(1);
        courtDTO.setName("Cancha A");

        when(courtService.getAllCourts()).thenReturn(Collections.singletonList(courtDTO));

        mockMvc.perform(get("/api/courts/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Cancha A"));
    }

    @Test
    void testGetCourtById() throws Exception {
        CourtDTO courtDTO = new CourtDTO();
        courtDTO.setId(1);
        courtDTO.setName("Cancha A");

        when(courtService.getCourtById(1L)).thenReturn(courtDTO);

        mockMvc.perform(get("/api/courts/search/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Cancha A"));
    }
}