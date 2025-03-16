package com.digitalhouse.court_rental.service;

import com.digitalhouse.court_rental.entity.Booking;
import com.digitalhouse.court_rental.entity.Court;
import com.digitalhouse.court_rental.entity.User;
import com.digitalhouse.court_rental.repository.BookingRepository;
import com.digitalhouse.court_rental.repository.CourtRepository;
import lombok.AllArgsConstructor;
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

    public List<Court> searchAvailableCourts(int cityId, int sportId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        return courtRepository.searchAvailableCourts(cityId, sportId, date, startTime, endTime);
    }


    public boolean isCourtAvailable(int courtId, LocalDate bookingDate, LocalTime startTime, LocalTime endTime) {
        return !bookingRepository.existsOverlappingBooking(courtId, bookingDate, startTime, endTime);
    }

    public Booking createBooking(Booking booking, User authenticatedUser) {

        if (!authenticatedUser.getId_user().equals(booking.getUser().getId_user())) {
            throw new RuntimeException("No puedes reservar en nombre de otro usuario.");
        }

        boolean available = isCourtAvailable(
                booking.getCourt().getIdCourt(),
                booking.getBookingDate(),
                booking.getStartTime(),
                booking.getEndTime()
        );

        if (!available) {
            throw new RuntimeException("La cancha ya está reservada en ese horario.");
        }

        if (booking.getCapacity() == null) {
            booking.setCapacity(1);
        }

        Court court = courtRepository.findById((long) booking.getCourt().getIdCourt())
                .orElseThrow(() -> new RuntimeException("Cancha no encontrada"));
        booking.setCourt(court);

        booking.setRegistrationDate(LocalDateTime.now());

        return bookingRepository.save(booking);
    }
}
