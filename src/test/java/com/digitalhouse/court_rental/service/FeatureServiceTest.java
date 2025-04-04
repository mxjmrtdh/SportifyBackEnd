package com.digitalhouse.court_rental.service;

import com.digitalhouse.court_rental.dto.FeatureDTO;
import com.digitalhouse.court_rental.dto.FeatureRequestDTO;
import com.digitalhouse.court_rental.entity.Feature;
import com.digitalhouse.court_rental.entity.Status;
import com.digitalhouse.court_rental.repository.FeatureRepository;
import com.digitalhouse.court_rental.repository.StatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class FeatureServiceTest {

    @Mock
    private FeatureRepository featureRepository;

    @Mock
    private StatusRepository statusRepository;

    @Mock
    private ImgurService imgurService;

    @InjectMocks
    private FeatureService featureService;

    private Feature sampleFeature;
    private Status activeStatus;

    @BeforeEach
    void setUp() {
        activeStatus = new Status();
        activeStatus.setIdStatus(24);
        activeStatus.setStatus("Active");

        sampleFeature = new Feature();
        sampleFeature.setIdFeature(1);
        sampleFeature.setFeature("WiFi");
        sampleFeature.setImage_url("image_url");
        sampleFeature.setStatus(activeStatus);
    }

    @Test
    void testGetAll_Success() {
        when(featureRepository.findByStatus_IdStatus(24)).thenReturn(List.of(sampleFeature));

        List<FeatureDTO> result = featureService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("WiFi", result.getFirst().getFeature());
        assertEquals("image_url", result.getFirst().getImageUrl());
        verify(featureRepository, times(1)).findByStatus_IdStatus(24);
    }

    @Test
    void testGetAll_EmptyList() {
        when(featureRepository.findByStatus_IdStatus(24)).thenReturn(Collections.emptyList());

        List<FeatureDTO> result = featureService.getAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(featureRepository, times(1)).findByStatus_IdStatus(24);
    }

    @Test
    void testGetById_Success() {
        when(featureRepository.findById(1L)).thenReturn(Optional.of(sampleFeature));

        FeatureDTO result = featureService.getById(1L);

        assertNotNull(result);
        assertEquals("WiFi", result.getFeature());
        assertEquals("image_url", result.getImageUrl());
        verify(featureRepository, times(1)).findById(1L);
    }

    @Test
    void testGetById_NotFound() {
        when(featureRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> featureService.getById(1L));

        assertEquals("Court not found or inactive", exception.getMessage());
        verify(featureRepository, times(1)).findById(1L);
    }

    @Test
    void testAddFeature_Success() {
        FeatureRequestDTO requestDTO = new FeatureRequestDTO();
        requestDTO.setFeature("WiFi");

        MultipartFile mockImage = mock(MultipartFile.class);

        when(statusRepository.findById(24)).thenReturn(Optional.of(activeStatus));
        when(imgurService.uploadFile(mockImage)).thenReturn("uploaded_image_url");

        when(featureRepository.save(any(Feature.class))).thenAnswer(invocation -> {
            Feature savedFeature = invocation.getArgument(0);
            savedFeature.setIdFeature(1);
            return savedFeature;
        });

        Feature result = featureService.addFeature(requestDTO, mockImage);

        assertNotNull(result);
        assertEquals("WiFi", result.getFeature());
        assertEquals("uploaded_image_url", result.getImage_url());
        verify(featureRepository, times(1)).save(any(Feature.class));
    }

    @Test
    void testAddFeature_StatusNotFound() {
        FeatureRequestDTO requestDTO = new FeatureRequestDTO();
        requestDTO.setFeature("WiFi");

        when(statusRepository.findById(24)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> featureService.addFeature(requestDTO, null));

        assertEquals("Status 24 not found", exception.getMessage());
        verify(statusRepository, times(1)).findById(24);
    }

    @Test
    void testUpdateStatus_Success() {
        doNothing().when(featureRepository).updateFeatureStatus(1L, 25);

        featureService.updateStatus(1L);

        verify(featureRepository, times(1)).updateFeatureStatus(1L, 25);
    }

    @Test
    void testUpdateFeature_Success() {
        MultipartFile mockImage = mock(MultipartFile.class);

        when(featureRepository.findById(1L)).thenReturn(Optional.of(sampleFeature));
        when(statusRepository.findById(24)).thenReturn(Optional.of(activeStatus));
        when(imgurService.uploadFile(mockImage)).thenReturn("updated_image_url");

        featureService.updateFeature(1L, "Updated Feature", mockImage);

        assertEquals("Updated Feature", sampleFeature.getFeature());
        assertEquals("updated_image_url", sampleFeature.getImage_url());
        verify(featureRepository, times(1)).save(sampleFeature);
    }

    @Test
    void testUpdateFeature_NotFound() {
        when(featureRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> featureService.updateFeature(1L, "Updated Feature", null));

        assertEquals("Feature not found", exception.getMessage());
        verify(featureRepository, times(1)).findById(1L);
    }
}