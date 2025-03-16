package com.digitalhouse.court_rental.controller;

import com.digitalhouse.court_rental.entity.Booking;
import com.digitalhouse.court_rental.entity.Court;
import com.digitalhouse.court_rental.entity.User;
import com.digitalhouse.court_rental.repository.UserRepository;
import com.digitalhouse.court_rental.service.BookingService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/api/bookings")
public class BookingController {

    private BookingService bookingService;
    private UserRepository userService;

    @GetMapping("/search")
    public ResponseEntity<List<Court>> searchAvailableCourts(
            @RequestParam int cityId,
            @RequestParam int sportId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime) {

        List<Court> courts = bookingService.searchAvailableCourts(cityId, sportId, date, startTime, endTime);
        return ResponseEntity.ok(courts);
    }

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> createBooking(@RequestBody Booking booking,
                                           Authentication authentication
                                           ) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado.");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        Optional<User> optionalUser = userService.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No se pudo recuperar el usuario autenticado.");
        }

        User authenticatedUser = optionalUser.get();

        booking.setUser(authenticatedUser);

        if (booking.getCourt() == null) {
            return ResponseEntity.badRequest().body("Debe especificar una cancha válida para la reserva.");
        }

        if (booking.getCourt().getIdCourt() <= 0) {
            return ResponseEntity.badRequest().body("El ID de la cancha debe ser un número válido.");
        }

        try {
            Booking newBooking = bookingService.createBooking(booking, authenticatedUser);
            return ResponseEntity.ok(newBooking);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
