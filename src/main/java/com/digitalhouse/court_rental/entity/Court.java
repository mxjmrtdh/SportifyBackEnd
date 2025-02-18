package com.digitalhouse.court_rental.entity;

import com.digitalhouse.court_rental.entity.court.City;
import com.digitalhouse.court_rental.entity.court.CourtStatus;
import com.digitalhouse.court_rental.entity.court.Sport;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "court")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Court {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idCourt;

    @Column(nullable = false, length = 100, unique = true)
    private String courtName;

    @Column(nullable = false, length = 500)
    private String courtDescription;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(nullable = false, length = 255)
    private String neighborhood;

    @ManyToOne
    @JoinColumn(name = "id_city", nullable = false)
    private City city;

    @ManyToOne
    @JoinColumn(name = "id_sport", nullable = false)
    private Sport sport;

    @ManyToOne
    @JoinColumn(name = "id_court_status", nullable = false)
    private CourtStatus courtStatus;
}
