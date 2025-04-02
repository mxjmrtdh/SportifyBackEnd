package com.digitalhouse.court_rental.dto;

import com.digitalhouse.court_rental.enums.AuthStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponseDTO {
    private String token;
    private String fullName;
    private String role;
    private String email;

    private String message;
    AuthStatus authStatus;// Representa el estado de la operación de autenticación o registro.
}
