package com.digitalhouse.court_rental.entity.court;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class SportStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idSportStatus;

    private String sportStatus;

    @OneToMany(mappedBy = "sportStatus")
    private List<Sport> sports;
}
