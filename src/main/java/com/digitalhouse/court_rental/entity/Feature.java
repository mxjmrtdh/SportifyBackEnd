package com.digitalhouse.court_rental.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "feature")
public class Feature {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_feature")
    private int idFeature;

    private String feature;

    private String image_url;

    @ManyToOne
    @JoinColumn(name = "id_status", nullable = false)
    private Status status;
}
