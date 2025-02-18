package com.digitalhouse.court_rental.service;

import com.digitalhouse.court_rental.dto.CourtDTO;
import com.digitalhouse.court_rental.entity.Court;
import com.digitalhouse.court_rental.entity.court.City;
import com.digitalhouse.court_rental.entity.court.CourtStatus;
import com.digitalhouse.court_rental.entity.court.Sport;
import com.digitalhouse.court_rental.repository.CourtRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourtServiceTest {
    @Mock
    private CourtRepository courtRepository;

    @InjectMocks
    private CourtService courtService;

    private Court sampleCourt;

    @BeforeEach
    void setUp() {
        sampleCourt = new Court();
        sampleCourt.setIdCourt(1);
        sampleCourt.setCourtName("Court 1");

        Sport sport = new Sport();
        sport.setSportName("Tennis");

        City city = new City();
        city.setCityName("Cusco");

        CourtStatus status = new CourtStatus();
        status.setCourtStatus("Open");

        sampleCourt.setSport(sport);
        sampleCourt.setCity(city);
        sampleCourt.setCourtStatus(status);
    }

    @Test
    void testCreateCourt_Success() {
        when(courtRepository.findByCourtName(sampleCourt.getCourtName())).thenReturn(Optional.empty());
        when(courtRepository.save(sampleCourt)).thenReturn(sampleCourt);

        Court result = courtService.createCourt(sampleCourt);

        assertNotNull(result);
        assertEquals(sampleCourt.getCourtName(), result.getCourtName());
        verify(courtRepository, times(1)).save(sampleCourt);
    }

    @Test
    void testCreateCourt_AlreadyExists() {
        when(courtRepository.findByCourtName(sampleCourt.getCourtName())).thenReturn(Optional.of(sampleCourt));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> courtService.createCourt(sampleCourt));

        assertEquals("La cancha ya está registrada", exception.getMessage());
        verify(courtRepository, never()).save(any(Court.class));
    }

    @Test
    void testGetAllCourts_Success() {
        when(courtRepository.findAll()).thenReturn(Arrays.asList(sampleCourt));

        List<CourtDTO> result = courtService.getAllCourts();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Court 1", result.get(0).getName());
    }

    @Test
    void testGetCourtById_Success() {
        when(courtRepository.findById(1L)).thenReturn(Optional.of(sampleCourt));

        CourtDTO result = courtService.getCourtById(1L);

        assertNotNull(result);
        assertEquals("Court 1", result.getName());
    }

    @Test
    void testGetCourtById_NotFound() {
        when(courtRepository.findById(99L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> courtService.getCourtById(99L));

        assertEquals("Court not found", exception.getMessage());
    }
}