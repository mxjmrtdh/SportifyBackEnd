package com.digitalhouse.court_rental.repository;

import com.digitalhouse.court_rental.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long>{
    @Query("""
        SELECT COUNT(b) > 0 
        FROM Booking b 
        WHERE b.court.id = :courtId 
        AND b.bookingDate = :bookingDate
        AND (
            (:startTime < b.endTime AND :endTime > b.startTime)
        )
    """)
    boolean existsOverlappingBooking(
            @Param("courtId") int courtId,
            @Param("bookingDate") LocalDate bookingDate,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );

    @Query("SELECT b.bookingDate FROM Booking b WHERE b.court.id = :courtId")
    List<LocalDate> findReservedDatesByCourt(@Param("courtId") Long courtId);

    @Query("""
    SELECT b.startTime, b.endTime FROM Booking b 
    WHERE b.court.id = :courtId AND b.bookingDate = :bookingDate
            """)
    List<Object[]> findReservedTimesByCourtAndDate (
            @Param("courtId") Long courtId,
            @Param("bookingDate") LocalDate bookingDate
    );
}

