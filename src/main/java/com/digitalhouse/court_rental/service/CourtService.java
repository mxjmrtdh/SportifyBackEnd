package com.digitalhouse.court_rental.service;

import com.digitalhouse.court_rental.dto.CourtDTO;
import com.digitalhouse.court_rental.dto.CourtRequestDTO;
import com.digitalhouse.court_rental.entity.Court;
import com.digitalhouse.court_rental.entity.Status;
import com.digitalhouse.court_rental.entity.court.City;
import com.digitalhouse.court_rental.entity.court.Sport;
import com.digitalhouse.court_rental.repository.CityRepository;
import com.digitalhouse.court_rental.repository.CourtRepository;
import com.digitalhouse.court_rental.repository.SportRepository;
import com.digitalhouse.court_rental.repository.StatusRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CourtService {
    private final CourtRepository courtRepository;
    private final SportRepository sportRepository;
    private final CityRepository cityRepository;
    private final StatusRepository statusRepository;

    public Court createCourt(CourtRequestDTO courtRequest) {
        if (courtRepository.findByCourtName(courtRequest.getName()).isPresent()) {
            throw new IllegalArgumentException("La cancha ya está registrada");
        }

        Court court = new Court();
        court.setCourtName(courtRequest.getName());
        court.setCourtDescription(courtRequest.getDescription());
        court.setCapacity(courtRequest.getCapacity());
        court.setPricePerHour(courtRequest.getPricePerHour());
        court.setAddress(courtRequest.getAddress());
        court.setNeighborhood(courtRequest.getNeighborhood());
        Sport sport = sportRepository.findById(courtRequest.getSportId())
                .orElseThrow(() -> new RuntimeException("Sport not found"));
        City city = cityRepository.findById(courtRequest.getCityId())
                .orElseThrow(() -> new RuntimeException("City not found"));
        Status status = statusRepository.findById(courtRequest.getStatusId())
                .orElseThrow(() -> new RuntimeException("Status not found"));

        court.setSport(sport);
        court.setCity(city);
        court.setStatus(status);

        return courtRepository.save(court);
    }

    public List<CourtDTO> getAllCourts() {
        List<Court> activeCourts = courtRepository.findByStatus_IdStatus(1);
        return activeCourts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CourtDTO getCourtById(Long id) {
        Court court = courtRepository.findById(id)
                .filter(c -> c.getStatus().getIdStatus() == 1)
                .orElseThrow(() -> new RuntimeException("Court not found or inactive"));
        return convertToDTO(court);
    }


    private CourtDTO convertToDTO(Court court) {
        CourtDTO courtDTO = new CourtDTO();
        courtDTO.setId(court.getIdCourt());
        courtDTO.setName(court.getCourtName());
        courtDTO.setDescription(court.getCourtDescription());
        courtDTO.setStatus(court.getStatus().getStatus());
        courtDTO.setCapacity(court.getCapacity());
        courtDTO.setPricePerHour(court.getPricePerHour());
        courtDTO.setSport(court.getSport().getSportName());
        courtDTO.setCity(court.getCity().getCityName());
        courtDTO.setAddress(court.getAddress());
        courtDTO.setNeighborhood(court.getNeighborhood());
        return courtDTO;
    }

    public List<CourtDTO> getRandomCourts() {
        List<Object[]> results = courtRepository.getRandomCourts();
        List<CourtDTO> courts = new ArrayList<>();

        for (Object[] row : results) {
            CourtDTO courtDTO = new CourtDTO();
            courtDTO.setId((int) row[0]);
            courtDTO.setName((String) row[1]);
            courtDTO.setDescription((String) row[2]);
            courtDTO.setSport((String) row[3]);
            courtDTO.setCapacity((int) row[4]);
            courtDTO.setPricePerHour((BigDecimal) row[5]);
            courtDTO.setStatus((String) row[6]);
            courtDTO.setAddress((String) row[7]);
            courtDTO.setNeighborhood((String) row[8]);
            courtDTO.setCity((String) row[9]);
            courts.add(courtDTO);
        }
        return courts;
    }

    public void deleteCourt(Long id) {
        Court court = courtRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Court not found"));

        Status inactiveStatus = statusRepository.findById(2)
                .orElseThrow(() -> new EntityNotFoundException("Inactive status not found"));

        court.setStatus(inactiveStatus);
        courtRepository.save(court);
    }

    public List<CourtDTO> getCourtsBySport(int sportId) {
        List<Object[]> results = courtRepository.searchByCategory(sportId);
        List<CourtDTO> courts = new ArrayList<>();

        for (Object[] row : results) {
            CourtDTO courtDTO = new CourtDTO();
            courtDTO.setId((int) row[0]);
            courtDTO.setName((String) row[1]);
            courtDTO.setDescription((String) row[2]);
            courtDTO.setSport((String) row[3]);
            courtDTO.setCapacity((int) row[4]);
            courtDTO.setPricePerHour((BigDecimal) row[5]);
            courtDTO.setStatus((String) row[6]);
            courtDTO.setAddress((String) row[7]);
            courtDTO.setNeighborhood((String) row[8]);
            courtDTO.setCity((String) row[9]);
            courts.add(courtDTO);
        }
        return courts;
    }

}
