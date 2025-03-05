package com.digitalhouse.court_rental.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeatureRequestDTO {
    private String feature;
    private String imageUrl;
    private int statusId;
}
