package com.digitalhouse.court_rental.controller;

import com.digitalhouse.court_rental.dto.BookingDTO;
import com.digitalhouse.court_rental.dto.CourtDTO;
import com.digitalhouse.court_rental.dto.PagedResponse;
import com.digitalhouse.court_rental.entity.Booking;
import com.digitalhouse.court_rental.entity.Court;
import com.digitalhouse.court_rental.service.BookingService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/public/bookings")
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/search")
    public ResponseEntity<PagedResponse<CourtDTO>> searchAvailableCourts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) List<Integer> sportId,
            @RequestParam(required = false) List<Integer> cityId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time) {

        PagedResponse<CourtDTO> response = bookingService.searchAvailableCourts(page, size, sportId, cityId, date, time);
        return ResponseEntity.ok(response);
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
}
