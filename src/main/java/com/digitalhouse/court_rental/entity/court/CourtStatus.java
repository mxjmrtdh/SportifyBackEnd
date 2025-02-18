package com.digitalhouse.court_rental.entity.court;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Id;

@Entity
@Getter
@Setter
public class CourtStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idCourtStatus;

    private String courtStatus;
}
