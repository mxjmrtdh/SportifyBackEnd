package com.digitalhouse.court_rental.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "product_feature")
public class ProductFeature {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_product_feature")
    private int idProductFeature;

    @ManyToOne
    @JoinColumn(name = "id_court")
    @JsonBackReference
    private Court court;

    @ManyToOne
    @JoinColumn(name = "id_feature")
    private Feature feature;
}
