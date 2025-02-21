package com.digitalhouse.court_rental.service;
import com.digitalhouse.court_rental.dto.SportDTO;
import com.digitalhouse.court_rental.entity.court.Sport;
import com.digitalhouse.court_rental.repository.SportRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

@ExtendWith(MockitoExtension.class)
class SportServiceTest {
    @Mock
    private SportRepository sportRepository;

    @InjectMocks
    private SportService sportService;

    @Test
    void findByStatusFive_ShouldReturnSportsWithStatusFive() {
        int statusId = 5;

        Sport sport1 = new Sport();
        sport1.setIdSport(1);
        sport1.setSportName("Football");

        Sport sport2 = new Sport();
        sport2.setIdSport(2);
        sport2.setSportName("Basketball");

        List<Sport> sports = Arrays.asList(sport1, sport2);

        when(sportRepository.findByStatusId(statusId)).thenReturn(sports);

        List<SportDTO> result = sportService.findByStatusFive();

        assertEquals(2, result.size());
        assertEquals("Football", result.get(0).getName());
        assertEquals("Basketball", result.get(1).getName());
    }


}