package com.digitalhouse.court_rental.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import java.util.*;
import javax.crypto.SecretKey;
import org.apache.commons.lang3.time.DateUtils;

@Slf4j // Anotación para habilitar un logger (registro de logs) en esta clase.
public class JwtUtils {
    // Clave secreta utilizada para firmar y verificar los tokens JWT.
    private static final SecretKey secretKey = Jwts.SIG.HS256.key().build();

    // Identificador del emisor del token, utilizado para identificar quién generó el JWT.
    private static final String ISSUER = "server";

    // Constructor privado para evitar la instanciación de esta clase, ya que solo contiene métodos estáticos.
    private JwtUtils() {
    }

    /**
     * Valida un token JWT verificando si es parseable y tiene un formato correcto.
     * @param jwtToken El token JWT a validar.
     * @return true si el token es válido, false en caso contrario.
     */
    public static boolean validateToken(String jwtToken) {
        return parseToken(jwtToken).isPresent(); // Llama a parseToken y verifica si devuelve un objeto presente.
    }

    /**
     * Intenta analizar un token JWT y devolver sus Claims (información contenida en el token).
     * @param jwtToken El token JWT a analizar.
     * @return Un Optional con los Claims si el análisis tiene éxito; Optional.empty() en caso de error.
     */
    private static Optional<Claims> parseToken(String jwtToken) {
        // Crea un parser JWT configurado para verificar el token con la clave secreta.
        var jwtParser = Jwts.parser()
                .verifyWith(secretKey) // Configura el parser para usar la clave secreta.
                .build();

        try {
            // Intenta analizar el token y extraer los Claims.
            return Optional.of(jwtParser.parseSignedClaims(jwtToken).getPayload());
        } catch (JwtException | IllegalArgumentException e) {
            // Captura cualquier excepción durante el análisis del token.
            System.err.println("JWT Exception occurred: " + e.getMessage());
        }
        // Devuelve un Optional vacío si ocurre algún error.
        return Optional.empty();
    }
    /**
     * Extrae el nombre de usuario (subject) de un token JWT.
     * @param jwtToken El token JWT del cual extraer el nombre de usuario.
     * @return Un Optional con el nombre de usuario si el token es válido; Optional.empty() en caso contrario.
     */
    public static Optional<String> getUsernameFromToken(String jwtToken) {
        var claimsOptional = parseToken(jwtToken); // Obtiene los Claims del token.
        return claimsOptional.map(Claims::getSubject); // Extrae el subject (nombre de usuario) si los Claims están presentes.
    }

    /**
     * Genera un token JWT firmado con la clave secreta.
     * @param username El nombre de usuario que se establecerá como subject en el token.
     * @return Un string que representa el token JWT generado.
     */
    public static String generateToken(String username) {

        // Obtiene la fecha actual para establecer los campos issuedAt y expiration.
        var currentDate = new Date();

        // Define el tiempo de expiración del token en minutos.
        var jwtExpirationInMinutes = 10;

        // Calcula la fecha de expiración sumando los minutos definidos a la fecha actual.
        var expiration = DateUtils.addMinutes(currentDate, jwtExpirationInMinutes);

        // Construye y firma el token JWT.
        return Jwts.builder()
                .id(UUID.randomUUID().toString()) // Asigna un identificador único al token.
                .issuer(ISSUER) // Establece el emisor del token.
                .subject(username) // Establece el nombre de usuario como subject del token.
                .signWith(secretKey) // Firma el token con la clave secreta.
                .issuedAt(currentDate) // Establece la fecha en que el token fue emitido.
                .expiration(expiration) // Establece la fecha de expiración del token.
                .compact(); // Genera el token en formato string compacto.
    }
}
