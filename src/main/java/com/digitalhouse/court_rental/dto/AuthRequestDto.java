package com.digitalhouse.court_rental.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

// Define un registro (record) llamado AuthRequestDto.
// Los records son una característica de Java que permite crear clases inmutables con menos código boilerplate.
// Este registro representa una solicitud de autenticación o registro de usuario.
@Getter
@Setter
public class AuthRequestDto{
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @Size(min = 6, message = "Password must be at least 6 characters long")
    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    private LocalDate birthdate;

    private Integer statusId;

    private Long countryId;
}