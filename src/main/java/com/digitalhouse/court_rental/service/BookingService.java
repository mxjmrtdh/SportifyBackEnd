package com.digitalhouse.court_rental.service;

import com.digitalhouse.court_rental.config.CourtSpecification;
import com.digitalhouse.court_rental.dto.BookingDTO;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@AllArgsConstructor
public class BookingService {

    private BookingRepository bookingRepository;
    private CourtRepository courtRepository;
    private final UserRepository userRepository;

    public List<Court> searchAvailableCourts(Integer  cityId, Integer  sportId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        Specification<Court> spec = CourtSpecification.searchCourts(cityId, sportId, date, startTime, endTime);
        return courtRepository.findAll(spec);
        //return courtRepository.searchAvailableCourts(cityId, sportId, date, startTime, endTime);
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
