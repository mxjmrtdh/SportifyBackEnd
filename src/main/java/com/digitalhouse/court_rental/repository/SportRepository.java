package com.digitalhouse.court_rental.repository;

import com.digitalhouse.court_rental.entity.Status;
import com.digitalhouse.court_rental.entity.court.Sport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SportRepository extends JpaRepository<Sport, Long> {

    @Query("SELECT s FROM Sport s WHERE s.status.idStatus = :statusId")
    List<Sport> findByStatusId(@Param("statusId") Integer statusId);

}

