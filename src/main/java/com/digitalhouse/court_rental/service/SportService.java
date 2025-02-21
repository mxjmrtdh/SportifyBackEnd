package com.digitalhouse.court_rental.service;

import com.digitalhouse.court_rental.dto.SportDTO;
import com.digitalhouse.court_rental.entity.court.Sport;
import com.digitalhouse.court_rental.repository.SportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SportService {
    private final SportRepository sportRepository;
    public List<SportDTO> findByStatusFive() {
        int statusId = 5;

        List<Sport> sports = sportRepository.findByStatusId(statusId);
        return sports.stream()
                .map(sport -> new SportDTO((long) sport.getIdSport(), sport.getSportName()))
                .collect(Collectors.toList());
    }
}