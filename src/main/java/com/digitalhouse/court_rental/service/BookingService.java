package com.digitalhouse.court_rental.service;

import com.digitalhouse.court_rental.dto.BookingDTO;
import com.digitalhouse.court_rental.dto.CourtDTO;
import com.digitalhouse.court_rental.dto.PagedResponse;
import com.digitalhouse.court_rental.entity.Booking;
import com.digitalhouse.court_rental.entity.Court;
import com.digitalhouse.court_rental.entity.User;
import com.digitalhouse.court_rental.repository.BookingRepository;
import com.digitalhouse.court_rental.repository.CourtRepository;
import com.digitalhouse.court_rental.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
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
        long totalElements = results.isEmpty() ? 0 : ((Number) results.getFirst()[13]).longValue();

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

    public Booking createBooking(@Valid @RequestBody BookingDTO bookingDTO, Authentication authentication) {
        User authenticatedUser = getAuthenticatedUser(authentication);

        Court court = courtRepository.findById((long) bookingDTO.getCourtId())
                .orElseThrow(() -> new RuntimeException("Cancha no encontrada"));

        boolean available = isCourtAvailable(court.getIdCourt(), bookingDTO.getBookingDate(), bookingDTO.getStartTime(), bookingDTO.getEndTime());
        if (!available) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La cancha ya está reservada en este horario. Por favor, elige otro disponible.");
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

    public Map<String, Map<LocalDate, List<LocalTime>>> getAvailability(Long courtId) {
        Map<LocalDate, List<LocalTime>> availableSlots = new HashMap<>();
        Map<LocalDate, List<LocalTime>> reservedSlots = new HashMap<>();

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusMonths(1);

        for (LocalDate date = today; date.isBefore(endDate); date = date.plusDays(1)) {
            List<Object[]> reservedTimeRanges = bookingRepository.findReservedTimesByCourtAndDate (courtId, date);
            List<LocalTime> reservedTimes = new ArrayList<>();
            List<LocalTime> availableTimes = new ArrayList<>();

            for (Object[] timeRange : reservedTimeRanges) {
                LocalTime startTime = (LocalTime) timeRange[0];
                LocalTime endTime = (LocalTime) timeRange[1];

                for (LocalTime t = startTime; t.isBefore(endTime); t = t.plusHours(1)) {
                    reservedTimes.add(t);
                }
            }

            for (int hour = 7; hour < 22; hour++) {
                LocalTime timeSlot = LocalTime.of(hour, 0);

                if (reservedTimes.contains(timeSlot)) {
                    reservedSlots.computeIfAbsent(date, k -> new ArrayList<>()).add(timeSlot);
                } else {
                    availableTimes.add(timeSlot);
                }
            }

            if (!availableTimes.isEmpty()) {
                availableSlots.put(date, availableTimes);
            }
        }

        Map<String, Map<LocalDate, List<LocalTime>>> response = new HashMap<>();
        response.put("availableSlots", availableSlots);
        response.put("reservedSlots", reservedSlots);

        return response;
    }
}
