package com.digitalhouse.court_rental.service;

import com.digitalhouse.court_rental.dto.CourtDTO;
import com.digitalhouse.court_rental.entity.Court;
import com.digitalhouse.court_rental.repository.CourtRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourtService {
    private final CourtRepository courtRepository;

    public Court createCourt(Court court) {
        if (courtRepository.findByCourtName(court.getCourtName()).isPresent()) {
            throw new IllegalArgumentException("La cancha ya está registrada");
        }
        return courtRepository.save(court);
    }

    public List<CourtDTO> getAllCourts() {
        List<Court> allCourts = courtRepository.findAll();
        return allCourts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CourtDTO getCourtById(Long id) {
        Court court = courtRepository.findById(id).orElseThrow(() -> new RuntimeException("Court not found"));
        return convertToDTO(court);
    }


    private CourtDTO convertToDTO(Court court) {
        CourtDTO courtDTO = new CourtDTO();
        courtDTO.setId(court.getIdCourt());
        courtDTO.setName(court.getCourtName());
        courtDTO.setSport(court.getSport().getSportName());
        courtDTO.setCity(court.getCity().getCityName());
        courtDTO.setStatus(String.valueOf(court.getCourtStatus()));
        return courtDTO;
    }

//    public List<Court> getRandomCourts(int limit) {
//        List<Court> allCourts = courtRepository.findAll();
//        Collections.shuffle(allCourts);
//        return allCourts.stream().limit(limit).collect(Collectors.toList());
//    }
}
