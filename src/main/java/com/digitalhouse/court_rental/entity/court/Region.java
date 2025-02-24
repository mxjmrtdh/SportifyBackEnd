package com.digitalhouse.court_rental.entity.court;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idRegion;

    private String regionName;

    @ManyToOne
    @JoinColumn(name = "id_country")
    @JsonManagedReference
    private Country country;

    @OneToMany(mappedBy = "region")
    @JsonManagedReference
    private List<City> cities;


}
