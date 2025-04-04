package com.digitalhouse.court_rental.controller;

import com.digitalhouse.court_rental.dto.SportDTO;
import com.digitalhouse.court_rental.dto.SportRequestDTO;
import com.digitalhouse.court_rental.service.SportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SportControllerTest {

    @Mock
    private SportService sportService;

    @InjectMocks
    private SportController sportController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(sportController).build();
    }

    @Test
    void testGetSportsByStatusFive() throws Exception {
        List<SportDTO> sports = Arrays.asList(
                new SportDTO(1, "Soccer"),
                new SportDTO(2, "Basketball")
        );

        when(sportService.findByStatusFive()).thenReturn(sports);

        mockMvc.perform(get("/api/public/sports/status/5"))
                .andExpect(status().isOk());

        verify(sportService, times(1)).findByStatusFive();
    }

    @Test
    void testCreateSport_Success() throws Exception {
        SportRequestDTO sportRequestDTO = new SportRequestDTO();
        sportRequestDTO.setName("Tennis");

        doNothing().when(sportService).createSport(any(SportRequestDTO.class));

        mockMvc.perform(post("/api/public/sports/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Tennis\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Deporte creado exitosamente"));

        verify(sportService, times(1)).createSport(any(SportRequestDTO.class));
    }

    @Test
    void testCreateSport_Failure() throws Exception {
        SportRequestDTO sportRequestDTO = new SportRequestDTO();
        sportRequestDTO.setName("Tennis");

        doThrow(new RuntimeException("Error al crear el deporte")).when(sportService).createSport(any(SportRequestDTO.class));

        mockMvc.perform(post("/api/public/sports/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Tennis\"}"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error al crear el deporte: Error al crear el deporte"));

        verify(sportService, times(1)).createSport(any(SportRequestDTO.class));
    }

    @Test
    void testUpdateSportStatus_Success() throws Exception {
        when(sportService.updateSportAndCourtState(eq(1))).thenReturn("Estado actualizado exitosamente");

        mockMvc.perform(put("/api/public/sports/update-status/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Estado actualizado exitosamente"));

        verify(sportService, times(1)).updateSportAndCourtState(1);
    }

    @Test
    void testUpdateSportStatus_Failure() throws Exception {
        when(sportService.updateSportAndCourtState(eq(1))).thenThrow(new RuntimeException("Error al actualizar el estado"));

        mockMvc.perform(put("/api/public/sports/update-status/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error al actualizar el deporte: Error al actualizar el estado"));

        verify(sportService, times(1)).updateSportAndCourtState(1);
    }

    @Test
    void testGetSportById_Success() throws Exception {
        SportDTO sportDTO = new SportDTO(1, "Soccer");

        when(sportService.getSportById(eq(1))).thenReturn(sportDTO);

        mockMvc.perform(get("/api/public/sports/1"))
                .andExpect(status().isOk());

        verify(sportService, times(1)).getSportById(1);
    }

    @Test
    void testGetSportById_NotFound() throws Exception {
        when(sportService.getSportById(eq(1))).thenReturn(null);

        mockMvc.perform(get("/api/public/sports/1"))
                .andExpect(status().isNotFound());

        verify(sportService, times(1)).getSportById(1);
    }

    @Test
    void testUpdateSport_Success() throws Exception {
        SportRequestDTO sportRequestDTO = new SportRequestDTO();
        sportRequestDTO.setName("Updated Sport");

        doNothing().when(sportService).updateSport(eq(1), any(SportRequestDTO.class));

        mockMvc.perform(put("/api/public/sports/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated Sport\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Deporte actualizado exitosamente"));

        verify(sportService, times(1)).updateSport(eq(1), any(SportRequestDTO.class));
    }

    @Test
    void testUpdateSport_Failure() throws Exception {
        SportRequestDTO sportRequestDTO = new SportRequestDTO();
        sportRequestDTO.setName("Updated Sport");

        doThrow(new RuntimeException("Error al actualizar el deporte")).when(sportService).updateSport(eq(1), any(SportRequestDTO.class));

        mockMvc.perform(put("/api/public/sports/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated Sport\"}"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error al actualizar el deporte: Error al actualizar el deporte"));

        verify(sportService, times(1)).updateSport(eq(1), any(SportRequestDTO.class));
    }
}