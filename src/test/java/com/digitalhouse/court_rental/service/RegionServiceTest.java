package com.digitalhouse.court_rental.service;

import com.digitalhouse.court_rental.dto.RegionDTO;
import com.digitalhouse.court_rental.entity.court.Region;
import com.digitalhouse.court_rental.repository.RegionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegionServiceTest {
    @Mock
    private RegionRepository regionRepository;

    @InjectMocks
    private RegionService regionService;

    @Test
    void testGetRegionsByCountry_Success() {
        Region regionA = new Region();
        regionA.setIdRegion(1);
        regionA.setRegionName("Región A");

        Region regionB = new Region();
        regionB.setIdRegion(2);
        regionB.setRegionName("Región B");

        List<Region> mockRegions = List.of(regionA, regionB);

        when(regionRepository.findByCountryId(1L)).thenReturn(mockRegions);

        List<RegionDTO> result = regionService.getRegionsByCountry(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Región A", result.get(0).getName());
        assertEquals("Región B", result.get(1).getName());

        verify(regionRepository, times(1)).findByCountryId(1L);
    }


    @Test
    void testGetRegionsByCountry_EmptyList() {
        when(regionRepository.findByCountryId(2L)).thenReturn(List.of());

        List<RegionDTO> result = regionService.getRegionsByCountry(2L);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(regionRepository, times(1)).findByCountryId(2L);
    }

}