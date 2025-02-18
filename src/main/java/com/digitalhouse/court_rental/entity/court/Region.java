package com.digitalhouse.court_rental.entity.court;

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
    private Country country;

    @OneToMany(mappedBy = "region")
    private List<City> cities;


}
