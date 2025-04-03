package com.digitalhouse.court_rental.controller;

import com.digitalhouse.court_rental.dto.CourtDTO;
import com.digitalhouse.court_rental.dto.CourtRequestDTO;
import com.digitalhouse.court_rental.dto.PagedResponse;
import com.digitalhouse.court_rental.entity.Court;
import com.digitalhouse.court_rental.service.CourtService;
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
@RequestMapping("/api/public/courts")
@RequiredArgsConstructor
public class CourtController {

    private final CourtService courtService;
    @PostMapping(value = "/add", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Court> createCourt(
            @RequestPart("court")  String courtJson,//@Valid CourtRequestDTO courtRequest,
            @RequestPart("images") List<MultipartFile> images) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        CourtRequestDTO courtRequest = objectMapper.readValue(courtJson, CourtRequestDTO.class);

        Court newCourt = courtService.createCourt(courtRequest,images);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCourt);
    }

    @GetMapping("/search")
    public ResponseEntity<PagedResponse<CourtDTO>> getAllCourts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(courtService.getAllCourts(page, size));
    }

    @GetMapping("/search/{id}")
    public ResponseEntity<CourtDTO> getCourtById(@PathVariable Long id) {
        return ResponseEntity.ok(courtService.getCourtById(Math.toIntExact(id)));
    }

    @GetMapping("/random")
    public ResponseEntity<List<CourtDTO>> getRandomCourts() {
        return ResponseEntity.ok(courtService.getRandomCourts());
    }

    @PutMapping("/delete/{id}")
    public ResponseEntity<String> deleteCourt(@PathVariable Long id) {
        courtService.deleteCourt(id);
        return ResponseEntity.ok("Court deleted successfully");
    }

    @GetMapping("/category/{sportId}")
    public ResponseEntity<List<CourtDTO>> getCourtsBySport(@PathVariable int sportId) {
        return ResponseEntity.ok(courtService.getCourtsBySport(sportId));
    }

    @PutMapping(value = "/update/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Court> updateCourt(
            @PathVariable Long id,
            @RequestPart("court") String courtJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        CourtRequestDTO courtRequest = objectMapper.readValue(courtJson, CourtRequestDTO.class);

        Court updatedCourt = courtService.updateCourt(id, courtRequest, images);
        return ResponseEntity.ok(updatedCourt);
    }

}
