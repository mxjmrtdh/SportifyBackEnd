package com.digitalhouse.court_rental.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

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
