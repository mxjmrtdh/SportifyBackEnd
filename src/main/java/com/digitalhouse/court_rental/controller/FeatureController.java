package com.digitalhouse.court_rental.controller;

import com.digitalhouse.court_rental.dto.CourtDTO;
import com.digitalhouse.court_rental.dto.CourtRequestDTO;
import com.digitalhouse.court_rental.dto.FeatureDTO;
import com.digitalhouse.court_rental.dto.FeatureRequestDTO;
import com.digitalhouse.court_rental.entity.Court;
import com.digitalhouse.court_rental.entity.Feature;
import com.digitalhouse.court_rental.service.FeatureService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class FeatureController {

    private final FeatureService featureService;
    private final ObjectMapper objectMapper;
    @GetMapping("/features")
    public List<FeatureDTO> getAll() {
            return featureService.getAll();
        }

    @GetMapping("/features/{id}")
    public FeatureDTO getById(@PathVariable Long id) {
        return featureService.getById(id);
    }

    @PostMapping(value = "/features/add", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Feature> createFeature(
            @RequestPart("feature") String featureJson,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        FeatureRequestDTO featureRequest = objectMapper.readValue(featureJson, FeatureRequestDTO.class);

        Feature newFeature = featureService.addFeature(featureRequest, image);

        return ResponseEntity.status(HttpStatus.CREATED).body(newFeature);
    }

    @PutMapping("/features/{id}/deactivate")
    public ResponseEntity<String> deactivate(@PathVariable int id) {
        featureService.updateStatus((long) id);
        return ResponseEntity.ok("Court deleted successfully");
    }

    @PutMapping(value = "/features/{id}/update", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> updateFeature(
            @PathVariable Long id,
            @RequestPart("feature") String featureJson,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        FeatureRequestDTO featureRequest = objectMapper.readValue(featureJson, FeatureRequestDTO.class);

        featureService.updateFeature(id, featureRequest.getFeature(), image);
        return ResponseEntity.ok("Feature updated successfully");
    }
}
