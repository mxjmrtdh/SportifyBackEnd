package com.digitalhouse.court_rental.controller;

import com.digitalhouse.court_rental.dto.SportDTO;
import com.digitalhouse.court_rental.dto.SportRequestDTO;
import com.digitalhouse.court_rental.service.SportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/sports")
public class SportController {

    @Autowired
    private SportService sportService;

    @GetMapping("/status/5")
    public List<SportDTO> getSportsByStatusFive() {
        return sportService.findByStatusFive();
    }

    @PostMapping("/add")
    public ResponseEntity<String> createSport(@RequestBody SportRequestDTO sportRequestDTO) {
        try {
            sportService.createSport(sportRequestDTO);
            return ResponseEntity.ok("Deporte creado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear el deporte: " + e.getMessage());
        }
    }

    @PutMapping("/update-status/{sportId}")
    public ResponseEntity<String> updateSportStatus(@PathVariable int sportId) {
        try {
            String result = sportService.updateSportAndCourtState(sportId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar el deporte: " + e.getMessage());
        }
    }

    @GetMapping("/{sportId}")
    public ResponseEntity<SportDTO> getSportById(@PathVariable int sportId) {
        SportDTO sportDTO = sportService.getSportById(sportId);
        return sportDTO != null ? ResponseEntity.ok(sportDTO) : ResponseEntity.notFound().build();
    }

    @PutMapping("/update/{sportId}")
    public ResponseEntity<String> updateSport(@PathVariable int sportId, @RequestBody SportRequestDTO sportRequestDTO) {
        try {
            sportService.updateSport(sportId, sportRequestDTO);
            return ResponseEntity.ok("Deporte actualizado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar el deporte: " + e.getMessage());
        }
    }

}
