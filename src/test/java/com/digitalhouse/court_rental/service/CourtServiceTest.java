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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    @Mock
    private ImgurService imgurService;

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
        sampleCourt.setImageUrl(new ArrayList<>());
    }

    @Test
    void testCreateCourt_Success() throws IOException {
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

        List<MultipartFile> mockImages = new ArrayList<>();
        MultipartFile mockImage = mock(MultipartFile.class);
        mockImages.add(mockImage);

        when(courtRepository.findByCourtName(requestDTO.getName())).thenReturn(Optional.empty());
        when(sportRepository.findById(1L)).thenReturn(Optional.of(sampleSport));
        when(cityRepository.findById(1)).thenReturn(Optional.of(sampleCity));
        when(statusRepository.findById(1)).thenReturn(Optional.of(sampleStatus));
        when(imgurService.uploadFile(mockImage)).thenReturn("image_url");
        when(courtRepository.save(any(Court.class))).thenReturn(sampleCourt);

        Court result = courtService.createCourt(requestDTO, mockImages);
        assertNotNull(result);
        assertEquals("Court 1", result.getCourtName());
        verify(courtRepository, times(1)).save(any(Court.class));
    }

    @Test
    void testGetCourtById_Success() {
        when(courtRepository.getCourtById(1L)).thenReturn(Collections.singletonList(
                new Object[]{1, "Court 1", "Tennis", "Cusco", "Active", "Description", 10, BigDecimal.valueOf(50), "Address", "Neighborhood", "image_url", "feature", "feature_image"}
        ));

        CourtDTO result = courtService.getCourtById(1);
        assertNotNull(result);
        assertEquals("Court 1", result.getName());
        assertEquals("Tennis", result.getSport());
    }

    @Test
    void testGetCourtById_NotFound() {
        when(courtRepository.getCourtById(99L)).thenReturn(Collections.emptyList());
        Exception exception = assertThrows(EntityNotFoundException.class, () -> courtService.getCourtById(99));
        assertEquals("Court not found", exception.getMessage());
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
}