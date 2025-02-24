package com.digitalhouse.court_rental.entity.court;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idCountry;

    private String countryName;


    @OneToMany(mappedBy = "country")
    @JsonManagedReference
    private List<Region> regions;
}
