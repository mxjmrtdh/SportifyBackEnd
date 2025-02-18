package com.digitalhouse.court_rental.controller;

import com.digitalhouse.court_rental.dto.CourtDTO;
import com.digitalhouse.court_rental.dto.CourtRequestDTO;
import com.digitalhouse.court_rental.entity.Court;
import com.digitalhouse.court_rental.service.CourtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courts")
@RequiredArgsConstructor
public class CourtController {

    private final CourtService courtService;
    @PostMapping("/add")
    public ResponseEntity<Court> createCourt(@RequestBody @Valid CourtRequestDTO courtRequest) {
        Court newCourt = courtService.createCourt(courtRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCourt);
    }

    @GetMapping("/search")
    public List<CourtDTO> getAllCourts() {
        return courtService.getAllCourts();
    }

    @GetMapping("/search/{id}")
    public CourtDTO getCourtById(@PathVariable Long id) {
        return courtService.getCourtById(id);
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
}
