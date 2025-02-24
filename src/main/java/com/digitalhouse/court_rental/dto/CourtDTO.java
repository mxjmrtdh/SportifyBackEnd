package com.digitalhouse.court_rental.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CourtDTO {
    private int id;
    private String name;
    private String sport;
    private String city;
    private String status;
    private String description;
    private int capacity;
    private BigDecimal pricePerHour;
    private String address;
    private String neighborhood;
    private List<String> imageUrl;
}
