package com.digitalhouse.court_rental.entity.court;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Id;

@Entity
@Getter
@Setter
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idCity;

    private String cityName;

    @ManyToOne
    @JoinColumn(name = "id_region")
    @JsonBackReference
    private Region region;
}
