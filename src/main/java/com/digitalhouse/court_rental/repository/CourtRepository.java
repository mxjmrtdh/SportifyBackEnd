package com.digitalhouse.court_rental.repository;

import com.digitalhouse.court_rental.entity.Court;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourtRepository extends JpaRepository<Court, Long> {
    Optional<Court> findByCourtName(String courtName);

}
