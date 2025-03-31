package com.digitalhouse.court_rental.dto;

import com.digitalhouse.court_rental.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public class UserDTO {

    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    private LocalDate birthdate;

    private Integer statusId;

    private Integer country;

    private Set<String> roles;

    public UserDTO() {
    }
    public UserDTO(User user) {
        this.id = user.getId_user();
        this.name = user.getName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.birthdate = user.getBirthdate();
        this.statusId = user.getStatusId();
        this.country = user.getCountry() != null ? user.getCountry().getIdCountry() : null;
        //this.country = user.getCountry() != null ? user.getCountry().getCountryName() : null;
        this.roles = user.getRoles().stream().map(rol -> rol.getName().name()).collect(Collectors.toSet());
    }
}
