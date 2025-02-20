package com.digitalhouse.court_rental.service;

import com.digitalhouse.court_rental.dto.CourtDTO;
import com.digitalhouse.court_rental.dto.CourtRequestDTO;
import com.digitalhouse.court_rental.entity.Court;
import com.digitalhouse.court_rental.entity.Status;
import com.digitalhouse.court_rental.entity.court.City;
import com.digitalhouse.court_rental.entity.court.Sport;
import com.digitalhouse.court_rental.repository.CityRepository;
import com.digitalhouse.court_rental.repository.CourtRepository;
import com.digitalhouse.court_rental.repository.SportRepository;
import com.digitalhouse.court_rental.repository.StatusRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourtServiceTest {
    @Mock
    private CourtRepository courtRepository;
    @Mock
    private SportRepository sportRepository;
    @Mock
    private CityRepository cityRepository;
    @Mock
    private StatusRepository statusRepository;

    @InjectMocks
    private CourtService courtService;

    private Court sampleCourt;
    private Sport sampleSport;
    private City sampleCity;
    private Status sampleStatus;

    @BeforeEach
    void setUp() {
        sampleSport = new Sport();
        sampleSport.setIdSport(1);
        sampleSport.setSportName("Tennis");

        sampleCity = new City();
        sampleCity.setIdCity(1);
        sampleCity.setCityName("Cusco");

        sampleStatus = new Status();
        sampleStatus.setIdStatus(1);
        sampleStatus.setStatus("Active");

        sampleCourt = new Court();
        sampleCourt.setIdCourt(1);
        sampleCourt.setCourtName("Court 1");
        sampleCourt.setCourtDescription("A great tennis court");
        sampleCourt.setCapacity(10);
        sampleCourt.setPricePerHour(BigDecimal.valueOf(50.00));
        sampleCourt.setSport(sampleSport);
        sampleCourt.setCity(sampleCity);
        sampleCourt.setStatus(sampleStatus);
    }

   /* @Test
    void testCreateCourt_Success() {
        CourtRequestDTO requestDTO = new CourtRequestDTO();
        requestDTO.setName("Court 1");
        requestDTO.setDescription("A great court");
        requestDTO.setCapacity(10);
        requestDTO.setPricePerHour(BigDecimal.valueOf(50.00));
        requestDTO.setAddress("123 Street");
        requestDTO.setNeighborhood("Downtown");
        requestDTO.setSportId(1);
        requestDTO.setCityId(1);
        requestDTO.setStatusId(1);

        when(courtRepository.findByCourtName(requestDTO.getName())).thenReturn(Optional.empty());
        when(sportRepository.findById(1)).thenReturn(Optional.of(sampleSport));
        when(cityRepository.findById(1)).thenReturn(Optional.of(sampleCity));
        when(statusRepository.findById(1)).thenReturn(Optional.of(sampleStatus));
        when(courtRepository.save(any(Court.class))).thenReturn(sampleCourt);

        Court result = courtService.createCourt(requestDTO);
        assertNotNull(result);
        assertEquals("Court 1", result.getCourtName());
        verify(courtRepository, times(1)).save(any(Court.class));
    }*/

    /*@Test
    void testCreateCourt_AlreadyExists() {
        CourtRequestDTO requestDTO = new CourtRequestDTO();
        requestDTO.setName("Court 1");
        requestDTO.setDescription("A great court");
        requestDTO.setCapacity(10);
        requestDTO.setPricePerHour(BigDecimal.valueOf(50.00));
        requestDTO.setAddress("123 Street");
        requestDTO.setNeighborhood("Downtown");
        requestDTO.setSportId(1);
        requestDTO.setCityId(1);
        requestDTO.setStatusId(1);

        when(courtRepository.findByCourtName(requestDTO.getName())).thenReturn(Optional.of(sampleCourt));

        //Exception exception = assertThrows(IllegalArgumentException.class, () -> courtService.createCourt(requestDTO));
        //assertEquals("La cancha ya está registrada", exception.getMessage());
    }*/

    @Test
    void testGetAllCourts_Success() {
        when(courtRepository.findByStatus_IdStatus(1)).thenReturn(Collections.singletonList(sampleCourt));

        List<CourtDTO> result = courtService.getAllCourts();
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Court 1", result.getFirst().getName());
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
        assertEquals("Court not found or inactive", exception.getMessage());
    }

    @Test
    void testDeleteCourt_Success() {
        Status inactiveStatus = new Status();
        inactiveStatus.setIdStatus(2);
        inactiveStatus.setStatus("Inactive");

        when(courtRepository.findById(1L)).thenReturn(Optional.of(sampleCourt));
        when(statusRepository.findById(2)).thenReturn(Optional.of(inactiveStatus));
        when(courtRepository.save(any(Court.class))).thenReturn(sampleCourt);

        courtService.deleteCourt(1L);
        assertEquals(2, sampleCourt.getStatus().getIdStatus());
        verify(courtRepository, times(1)).save(sampleCourt);
    }

    @Test
    void testDeleteCourt_NotFound() {
        when(courtRepository.findById(99L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> courtService.deleteCourt(99L));
        assertEquals("Court not found", exception.getMessage());
        assertThrows(EntityNotFoundException.class, () -> courtService.deleteCourt(1L));

    }
    @Test
    void testGetRandomCourts() {
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(new Object[]{
                1, "Cancha A", "Cancha de fútbol 5", "Fútbol", 10, BigDecimal.valueOf(50.00),
                "Activo", "123 Calle Principal", "Centro", "Lima"
        });

        when(courtRepository.getRandomCourts()).thenReturn(mockResults);

        List<CourtDTO> result = courtService.getRandomCourts();

        assertEquals(1, result.size());
        assertEquals("Cancha A", result.get(0).getName());
        assertEquals("Fútbol", result.get(0).getSport());
        assertEquals(BigDecimal.valueOf(50.00), result.get(0).getPricePerHour());
    }

    @Test
    void testGetCourtsBySport() {
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(new Object[]{
                1, "Cancha B", "Cancha de tenis", "Tenis", 4, BigDecimal.valueOf(30.00),
                "Activo", "456 Calle Secundaria", "Surco", "Lima"
        });

        when(courtRepository.searchByCategory(1)).thenReturn(mockResults);

        List<CourtDTO> result = courtService.getCourtsBySport(1);

        assertEquals(1, result.size());
        assertEquals("Cancha B", result.get(0).getName());
        assertEquals("Tenis", result.get(0).getSport());
        assertEquals(BigDecimal.valueOf(30.00), result.get(0).getPricePerHour());
    }



}