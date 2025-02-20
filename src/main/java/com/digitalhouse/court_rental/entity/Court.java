package com.digitalhouse.court_rental.entity;

import com.digitalhouse.court_rental.entity.court.City;
import com.digitalhouse.court_rental.entity.court.Sport;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

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

    private BigDecimal pricePerHour;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(nullable = false, length = 255)
    private String neighborhood;

    @ElementCollection
    @CollectionTable(name = "court_images", joinColumns = @JoinColumn(name = "id_court"))
    @Column(name = "image_url")
    private List<String> imageUrl;


    @ManyToOne
    @JoinColumn(name = "id_city", nullable = false)
    private City city;

    @ManyToOne
    @JoinColumn(name = "id_sport", nullable = false)
    private Sport sport;

    @ManyToOne
    @JoinColumn(name = "id_status", nullable = false)
    private Status status;
}
