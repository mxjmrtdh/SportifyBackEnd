package com.digitalhouse.court_rental.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_status")
    private int idStatus;

    @Column(name = "status", nullable = false, length = 20)
    private String status;
}
