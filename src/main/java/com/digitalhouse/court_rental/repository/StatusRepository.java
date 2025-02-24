package com.digitalhouse.court_rental.repository;

import com.digitalhouse.court_rental.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusRepository extends JpaRepository<Status, Integer> {
}
