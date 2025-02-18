package com.digitalhouse.court_rental.repository;

import com.digitalhouse.court_rental.entity.Court;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourtRepository extends JpaRepository<Court, Long> {
    Optional<Court> findByCourtName(String courtName);

    @Query(value = "CALL courtRand()", nativeQuery = true)
    List<Object[]> getRandomCourts();

    List<Court> findByStatus_IdStatus(int idStatus);

    @Modifying
    @Query("UPDATE Court c SET c.status.idStatus = 2 WHERE c.idCourt = :id")
    void deleteCourt(@Param("id") Long id);

    @Query(value = "CALL searchByCategory(:sportId)", nativeQuery = true)
    List<Object[]> searchByCategory(@Param("sportId") int sportId);
}
