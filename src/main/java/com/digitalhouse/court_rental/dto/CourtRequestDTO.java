package com.digitalhouse.court_rental.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CourtRequestDTO {
    private String name;
    private int sportId;
    private int cityId;
    private int statusId;
    private String description;
    private int capacity;
    private BigDecimal pricePerHour;
    private String address;
    private String neighborhood;
}
