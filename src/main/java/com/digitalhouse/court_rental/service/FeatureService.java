package com.digitalhouse.court_rental.service;

import com.digitalhouse.court_rental.dto.CourtDTO;
import com.digitalhouse.court_rental.dto.CourtRequestDTO;
import com.digitalhouse.court_rental.dto.FeatureDTO;
import com.digitalhouse.court_rental.dto.FeatureRequestDTO;
import com.digitalhouse.court_rental.entity.Court;
import com.digitalhouse.court_rental.entity.Feature;
import com.digitalhouse.court_rental.entity.Status;
import com.digitalhouse.court_rental.entity.court.City;
import com.digitalhouse.court_rental.entity.court.Sport;
import com.digitalhouse.court_rental.repository.FeatureRepository;
import com.digitalhouse.court_rental.repository.StatusRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeatureService {
    private final FeatureRepository featureRepository;
    private final StatusRepository statusRepository;

    private final ImgurService imgurService;

    public List<FeatureDTO> getAll() {
        List<Feature> activeFeature = featureRepository.findByStatus_IdStatus(24);
        return activeFeature.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private FeatureDTO convertToDTO(Feature feature) {
        FeatureDTO featureDTO = new FeatureDTO();
        featureDTO.setIdFeature(feature.getIdFeature());
        featureDTO.setFeature(feature.getFeature());
        featureDTO.setImageUrl(feature.getImage_url());
        featureDTO.setStatusId(feature.getStatus().getIdStatus());
        return featureDTO;
    }

    public FeatureDTO getById(Long id) {
        Feature feature = featureRepository.findById(id)
                .filter(c -> c.getStatus().getIdStatus() == 24)
                .orElseThrow(() -> new RuntimeException("Court not found or inactive"));
        return convertToDTO(feature);
    }

    public Feature add(FeatureRequestDTO featureRequest, List<MultipartFile> images) throws IOException {
        Feature feature = new Feature();
        feature.setFeature(featureRequest.getFeature());
        feature.setImage_url(featureRequest.getImageUrl());
        Status status = statusRepository.findById(featureRequest.getStatusId())
            .orElseThrow(() -> new RuntimeException("Status not found"));
        feature.setStatus(status);
        String imageLink = null;
        if (images != null && !images.isEmpty()) {
            try {
                imageLink = imgurService.uploadFile(images.get(0));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        feature.setImage_url(imageLink);

        return featureRepository.save(feature);
}

    @Transactional
    public void updateStatus(Long id) {
        featureRepository.updateFeatureStatus(id, 25);
    }
}
