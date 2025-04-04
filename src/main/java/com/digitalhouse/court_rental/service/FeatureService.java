package com.digitalhouse.court_rental.service;

import com.digitalhouse.court_rental.dto.FeatureDTO;
import com.digitalhouse.court_rental.dto.FeatureRequestDTO;
import com.digitalhouse.court_rental.entity.Feature;
import com.digitalhouse.court_rental.entity.Status;
import com.digitalhouse.court_rental.repository.FeatureRepository;
import com.digitalhouse.court_rental.repository.StatusRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
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

    public Feature addFeature(FeatureRequestDTO featureRequest, MultipartFile  image) {
        Feature feature = new Feature();
        feature.setFeature(featureRequest.getFeature());

        Status status = statusRepository.findById(24)
                .orElseThrow(() -> new RuntimeException("Status 24 not found"));
        feature.setStatus(status);

        if (image != null) {
            try {
                String imageLink = imgurService.uploadFile(image);
                feature.setImage_url(imageLink);
            } catch (Exception e) {
                log.error("Error uploading image: {}", e.getMessage(), e);
                throw new RuntimeException("Error uploading image: " + e.getMessage());
            }
        }

        return featureRepository.save(feature);
    }

    @Transactional
    public void updateStatus(Long id) {
        featureRepository.updateFeatureStatus(id, 25);
    }

    @Transactional
    public void updateFeature(Long id, String featureName, MultipartFile image) {
        Feature feature = featureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feature not found"));

        feature.setFeature(featureName);

        Status status = statusRepository.findById(24)
                .orElseThrow(() -> new RuntimeException("Status 24 not found"));
        feature.setStatus(status);

        if (image != null) {
            try {
                String imageLink = imgurService.uploadFile(image);
                feature.setImage_url(imageLink);
            } catch (Exception e) {
                log.error("Error uploading image: {}", e.getMessage(), e);
                throw new RuntimeException("Error uploading image: " + e.getMessage());
            }
        }

        featureRepository.save(feature);
    }
}
