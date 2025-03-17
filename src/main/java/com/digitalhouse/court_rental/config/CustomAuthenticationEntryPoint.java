package com.digitalhouse.court_rental.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    /**
     * Este método se invoca automáticamente cuando un usuario intenta acceder a un recurso protegido
     * sin estar autenticado o cuando su autenticación es inválida.
     *
     * @param request La solicitud HTTP entrante.
     * @param response La respuesta HTTP que se enviará al cliente.
     * @param authException La excepción que se generó debido a la falta de autenticación.
     * @throws IOException Si ocurre un error al escribir en la respuesta.
     * @throws ServletException Si ocurre un error relacionado con el servlet.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {

        // Envía un error HTTP 401 (Unauthorized) como respuesta al cliente,
        // indicando que no tiene permiso para acceder al recurso solicitado.
        response.sendError(HttpStatus.UNAUTHORIZED.value(), authException.getMessage());
    }
}
