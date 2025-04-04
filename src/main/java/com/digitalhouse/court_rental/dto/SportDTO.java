package com.digitalhouse.court_rental.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SportDTO {
    private Long id;
    private String name;
    private String icon;
    private String description;

    public SportDTO(int i, String soccer) {
        this.id = id;
        this.name = name;
    }
}
