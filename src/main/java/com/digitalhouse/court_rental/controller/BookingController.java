package com.digitalhouse.court_rental.controller;

import com.digitalhouse.court_rental.dto.BookingDTO;
import com.digitalhouse.court_rental.entity.Booking;
import com.digitalhouse.court_rental.entity.Court;
import com.digitalhouse.court_rental.service.BookingService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/bookings")
public class BookingController {

    private BookingService bookingService;

    @GetMapping("/search")
    public ResponseEntity<List<Court>> searchAvailableCourts(
            @RequestParam(required = false) Integer  cityId,
            @RequestParam(required = false) Integer  sportId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime) {

        List<Court> courts = bookingService.searchAvailableCourts(cityId, sportId, date, startTime, endTime);
        return ResponseEntity.ok(courts);
    }

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createBooking(@RequestBody BookingDTO bookingDTO,
                                           Authentication authentication
                                           ) {
        try {
            Booking newBooking = bookingService.createBooking(bookingDTO, authentication);
            return ResponseEntity.ok(newBooking);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{courtId}/availability")
    public ResponseEntity<?> getAvailability(@PathVariable Long courtId) {
        try {
            Map<String, List<LocalDate>> availability = bookingService.getAvailability(courtId);
            return ResponseEntity.ok(availability);
        } catch (Exception e) {
            return ResponseEntity.status(   HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener disponibilidad. Intente más tarde.");
        }
    }
}
