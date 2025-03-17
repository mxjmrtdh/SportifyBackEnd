package com.digitalhouse.court_rental.config;

import com.digitalhouse.court_rental.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private UserDetailsService userDetailsService;// Servicio para cargar los detalles del usuario desde el nombre de usuario.

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Intenta extraer el token JWT desde la solicitud HTTP.
        var jwtTokenOptional = getTokenFromRequest(request);

        // Si el token está presente, procede a validarlo.
        jwtTokenOptional.ifPresent(jwtToken -> {
            if (JwtUtils.validateToken(jwtToken)) { // Verifica si el token es válido.

                // Extrae el nombre de usuario desde el token JWT.
                var usernameOptional = JwtUtils.getUsernameFromToken(jwtToken);

                usernameOptional.ifPresent(username -> {
                    // Carga los detalles del usuario desde el nombre de usuario.
                    var userDetails = userDetailsService.loadUserByUsername(username);

                    // Crea un objeto `UsernamePasswordAuthenticationToken` con los detalles del usuario.
                    var authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null, // Las credenciales no se necesitan porque el token ya está validado.
                            userDetails.getAuthorities() // Los roles y permisos del usuario.
                    );

                    // Agrega detalles adicionales sobre la autenticación.
                    authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // Establece el contexto de seguridad para esta solicitud.
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                });
            }
        });

        // Pasa la solicitud y la respuesta al siguiente filtro en la cadena.
        filterChain.doFilter(request, response);
    }

    /**
     * Extrae el token JWT desde el encabezado de autorización de la solicitud
     * HTTP.
     *
     * @param request La solicitud HTTP entrante.
     * @return Un `Optional` que contiene el token JWT si está presente, o vacío
     * si no lo está.
     */
    private Optional<String> getTokenFromRequest(HttpServletRequest request) {

        // Obtiene el encabezado `Authorization` de la solicitud.
        var authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Verifica que el encabezado no esté vacío y que comience con "Bearer ".
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            // Devuelve el token sin la palabra "Bearer ".
            return Optional.of(authHeader.substring(7));
        }

        // Si no se encuentra el token, devuelve un `Optional` vacío.
        return Optional.empty();
    }
}
