package com.digitalhouse.court_rental.service.Auth;

import com.digitalhouse.court_rental.dto.AuthRequestDto;
import com.digitalhouse.court_rental.dto.AuthResponseDTO;
import com.digitalhouse.court_rental.entity.User;

import java.util.Optional;

public interface AuthService {
    AuthResponseDTO login(String email, String password);

    User signUp(AuthRequestDto authRequestDto);

    String verifyToken(String token);

    /**
     * Busca un usuario en el sistema por su correo electrónico.
     *
     * @param email El correo electrónico del usuario.
     * @return Un `Optional` que contiene al usuario si existe, o está vacío si no se encuentra.
     */
    Optional<User> findByEmail(String email);
    /**
     * Guarda un token de verificación asociado a un usuario.
     *
     * @param theUser El usuario al que se asocia el token.
     * @param verificationToken El token de verificación generado.
     */
    void saveUserVerificationToken(User theUser, String verificationToken);
    /**
     * Valida un token de verificación y determina su estado.
     *
     * @param theToken El token a validar.
     * @return Un mensaje indicando el resultado de la validación (válido, expirado, inválido, etc.).
     */
    String validateToken(String theToken);
}
