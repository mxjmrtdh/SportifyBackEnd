package com.digitalhouse.court_rental.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CityDTO {
    private Long id;
    private String name;
    private RegionDTO region;
}
