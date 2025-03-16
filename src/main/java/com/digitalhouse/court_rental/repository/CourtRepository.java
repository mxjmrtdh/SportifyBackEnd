package com.digitalhouse.court_rental.repository;

import com.digitalhouse.court_rental.entity.Court;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourtRepository extends JpaRepository<Court, Long>, JpaSpecificationExecutor<Court> {
    Optional<Court> findByCourtName(String courtName);

    @Query(value = "CALL GetCourts(:page, :size)", nativeQuery = true)
    List<Object[]> getCourts(@Param("page") int page, @Param("size") int size);

    @Query(value = "SELECT COUNT(*) FROM court_booking.court WHERE id_status = 1", nativeQuery = true)
    long countTotalCourts();

    @Query(value = "CALL court_booking.GetCourtsbyId(:id)", nativeQuery = true)
    List<Object[]> getCourtById(@Param("id") Long id);

    @Query(value = "CALL courtRand()", nativeQuery = true)
    List<Object[]> getRandomCourts();

    List<Court> findByStatus_IdStatus(int idStatus);

    @Modifying
    @Query("UPDATE Court c SET c.status.idStatus = 2 WHERE c.idCourt = :id")
    void deleteCourt(@Param("id") Long id);

    @Query(value = "CALL searchByCategory(:sportId)", nativeQuery = true)
    List<Object[]> searchByCategory(@Param("sportId") int sportId);

    /*
    @Query("SELECT c FROM Court c WHERE "
            + "(:cityId IS NULL OR c.city.id = :cityId) "
            + "AND (:sportId IS NULL OR c.sport.id = :sportId) "
            + "AND (:bookingDate IS NULL OR NOT EXISTS (SELECT b FROM Booking b WHERE b.court = c AND b.bookingDate = :bookingDate "
            + "AND (:startTime IS NULL OR b.startTime < :endTime) "
            + "AND (:endTime IS NULL OR b.endTime > :startTime)))")
    List<Court> searchAvailableCourts(@Param("cityId") Integer  cityId,
                                      @Param("sportId") Integer  sportId,
                                      @Param("bookingDate") LocalDate bookingDate,
                                      @Param("startTime") LocalTime startTime,
                                      @Param("endTime") LocalTime endTime);

     */
}
