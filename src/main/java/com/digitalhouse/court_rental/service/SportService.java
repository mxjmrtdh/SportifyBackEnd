package com.digitalhouse.court_rental.service;

import com.digitalhouse.court_rental.dto.SportDTO;
import com.digitalhouse.court_rental.dto.SportRequestDTO;
import com.digitalhouse.court_rental.entity.Status;
import com.digitalhouse.court_rental.entity.court.Sport;
import com.digitalhouse.court_rental.repository.SportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SportService {
    private final SportRepository sportRepository;
    public List<SportDTO> findByStatusFive() {
        int statusId = 5;

        List<Sport> sports = sportRepository.findByStatusId(statusId);
        return sports.stream()
                .map(sport -> new SportDTO((long) sport.getIdSport(), sport.getSportName(), sport.getIcon(), sport.getDescription()))
                .collect(Collectors.toList());
    }

    public void createSport(SportRequestDTO sportRequestDTO) {
        Sport sport = new Sport();
        sport.setSportName(sportRequestDTO.getName());
        sport.setIcon(sportRequestDTO.getIcon());
        sport.setDescription(sportRequestDTO.getDescription());

        Status status = new Status();
        status.setIdStatus(5);
        sport.setStatus(status);

        sportRepository.save(sport);
    }

    public String updateSportAndCourtState(int sportId) {
        try {
            return sportRepository.updateSportAndCourtState(sportId);
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar el estado del deporte: " + e.getMessage());
        }
    }

    public SportDTO getSportById(int sportId) {
        Optional<Sport> sport = sportRepository.findById((long) sportId);
        return sport.map(s -> new SportDTO((long) s.getIdSport(), s.getSportName(), s.getIcon(), s.getDescription()))
                .orElse(null);
    }

    public void updateSport(int sportId, SportRequestDTO sportRequestDTO) {
        Optional<Sport> optionalSport = sportRepository.findById((long) sportId);
        if (optionalSport.isPresent()) {
            Sport sport = optionalSport.get();
            sport.setSportName(sportRequestDTO.getName());
            sport.setIcon(sportRequestDTO.getIcon());
            sport.setDescription(sportRequestDTO.getDescription());
            sportRepository.save(sport);
        } else {
            throw new RuntimeException("El deporte con ID " + sportId + " no existe.");
        }
    }
}