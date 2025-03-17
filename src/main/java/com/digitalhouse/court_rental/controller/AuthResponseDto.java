package com.digitalhouse.court_rental.controller;

import com.digitalhouse.court_rental.enums.AuthStatus;

public record AuthResponseDto(
        String token,// Contiene el token JWT generado si la autenticación o el registro fueron exitosos.
        AuthStatus authStatus ,// Representa el estado de la operación de autenticación o registro.
        String message) {
}
