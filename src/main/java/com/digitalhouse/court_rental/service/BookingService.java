package com.digitalhouse.court_rental.service;

import com.digitalhouse.court_rental.config.CourtSpecification;
import com.digitalhouse.court_rental.dto.BookingDTO;
import com.digitalhouse.court_rental.dto.CourtDTO;
import com.digitalhouse.court_rental.dto.PagedResponse;
import com.digitalhouse.court_rental.entity.Booking;
import com.digitalhouse.court_rental.entity.Court;
import com.digitalhouse.court_rental.entity.User;
import com.digitalhouse.court_rental.repository.BookingRepository;
import com.digitalhouse.court_rental.repository.CourtRepository;
import com.digitalhouse.court_rental.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.*;
import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class BookingService {

    private BookingRepository bookingRepository;
    private CourtRepository courtRepository;
    private final UserRepository userRepository;

    public PagedResponse<CourtDTO> searchAvailableCourts(
            Integer page, Integer size, List<Integer> sportId, List<Integer> cityId, LocalDate date, LocalTime time) {

        String sportIdStr = (sportId != null && !sportId.isEmpty())
                ? String.join(",", sportId.stream().map(String::valueOf).toArray(String[]::new))
                : null;

        String cityIdStr = (cityId != null && !cityId.isEmpty())
                ? String.join(",", cityId.stream().map(String::valueOf).toArray(String[]::new))
                : null;

        List<Object[]> results = courtRepository.getCourtsByFilters(page - 1, size, sportIdStr, cityIdStr, date, time);
        long totalElements = results.isEmpty() ? 0 : ((Number) results.get(0)[13]).longValue();

        Map<Integer, CourtDTO> courtMap = new HashMap<>();

        for (Object[] obj : results) {
            int courtId = obj[0] instanceof Integer ? (Integer) obj[0] : Integer.parseInt(obj[0].toString());

            CourtDTO dto = courtMap.computeIfAbsent(courtId, id -> {
                CourtDTO newDto = new CourtDTO();
                newDto.setId(courtId);
                newDto.setName((String) obj[1]);
                newDto.setSport((String) obj[2]);
                newDto.setCity((String) obj[3]);
                newDto.setStatus((String) obj[4]);
                newDto.setDescription((String) obj[5]);
                newDto.setCapacity((Integer) obj[6]);
                newDto.setPricePerHour((BigDecimal) obj[7]);
                newDto.setAddress((String) obj[8]);
                newDto.setNeighborhood((String) obj[9]);
                newDto.setImageUrl(new ArrayList<>());
                newDto.setFeatures(new ArrayList<>());
                newDto.setFeaturesImageUrl(new ArrayList<>());
                return newDto;
            });

            if (obj[10] != null && !dto.getImageUrl().contains(obj[10].toString())) {
                dto.getImageUrl().add(obj[10].toString());
            }
            if (obj[11] != null && !dto.getFeatures().contains(obj[11].toString())) {
                dto.getFeatures().add(obj[11].toString());
            }
            if (obj[12] != null && !dto.getFeaturesImageUrl().contains(obj[12].toString())) {
                dto.getFeaturesImageUrl().add(obj[12].toString());
            }
        }

        return new PagedResponse<>(
                new ArrayList<>(courtMap.values()),
                page,
                size,
                totalElements
        );
    }

    public Booking createBooking(BookingDTO bookingDTO, Authentication authentication) {
        User authenticatedUser = getAuthenticatedUser(authentication);

        Court court = courtRepository.findById((long) bookingDTO.getCourtId())
                .orElseThrow(() -> new RuntimeException("Cancha no encontrada"));

        boolean available = isCourtAvailable(court.getIdCourt(), bookingDTO.getBookingDate(), bookingDTO.getStartTime(), bookingDTO.getEndTime());
        if (!available) {
            throw new RuntimeException("La cancha ya está reservada en ese horario.");
        }

        Booking booking = new Booking();
        booking.setUser(authenticatedUser);
        booking.setCourt(court);
        booking.setBookingDate(bookingDTO.getBookingDate());
        booking.setStartTime(bookingDTO.getStartTime());
        booking.setEndTime(bookingDTO.getEndTime());
        booking.setCapacity(bookingDTO.getCapacity() != null ? bookingDTO.getCapacity() : 1);
        booking.setRegistrationDate(LocalDateTime.now());

        return bookingRepository.save(booking);
    }

    private boolean isCourtAvailable(int courtId, LocalDate bookingDate, LocalTime startTime, LocalTime endTime) {
        return !bookingRepository.existsOverlappingBooking(courtId, bookingDate, startTime, endTime);
    }

    private User getAuthenticatedUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Usuario no autenticado.");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("No se pudo recuperar el usuario autenticado."));
    }
}
