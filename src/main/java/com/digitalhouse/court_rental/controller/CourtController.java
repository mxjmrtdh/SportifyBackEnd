package com.digitalhouse.court_rental.controller;

import com.digitalhouse.court_rental.dto.CourtDTO;
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
    public ResponseEntity<Court> createCourt(@RequestBody @Valid Court court) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courtService.createCourt(court));
    }

    @GetMapping("/search")
    public List<CourtDTO> getAllCourts() {
        return courtService.getAllCourts();
    }

    @GetMapping("/search/{id}")
    public CourtDTO getCourtById(@PathVariable Long id) {
        return courtService.getCourtById(id);
    }

//    @GetMapping("/random")
//    public ResponseEntity<List<Court>> getRandomCourts() {
//        return ResponseEntity.ok(courtService.getRandomCourts(10));
//    }
}
