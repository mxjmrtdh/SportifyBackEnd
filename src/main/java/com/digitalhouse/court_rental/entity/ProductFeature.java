package com.digitalhouse.court_rental.entity;

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
    private Court court;

    @ManyToOne
    @JoinColumn(name = "id_feature")
    private Feature feature;
}
