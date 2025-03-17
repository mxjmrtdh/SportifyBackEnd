package com.digitalhouse.court_rental.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class SecurityFilterChainConfig {
    private final AuthenticationEntryPoint authenticationEntryPoint; // Punto de entrada para manejar excepciones de autenticación.
    private final JWTAuthenticationFilter jwtAuthenticationFilter; // Filtro personalizado para manejar la autenticación con JWT.

    // Constructor de la clase que inicializa el `authenticationEntryPoint`.
    public SecurityFilterChainConfig(AuthenticationEntryPoint authenticationEntryPoint, JWTAuthenticationFilter jwtAuthenticationFilter) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    // Definición de un bean que configura la seguridad para las solicitudes HTTP.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        // Configuración de seguridad para solicitudes HTTP.
        // 1. Deshabilita la configuración de CORS (Cross-Origin Resource Sharing).
        // Esto significa que no se aplicarán restricciones para solicitudes desde otros orígenes.
        httpSecurity.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 2. Deshabilita la protección CSRF (Cross-Site Request Forgery), útil para APIs RESTful.
                .csrf(AbstractHttpConfigurer::disable);

        // 3. Configura las reglas de autorización para las solicitudes HTTP.
        httpSecurity.authorizeHttpRequests(
                        requestMatcher -> requestMatcher
                                .requestMatchers("/api/auth/**").permitAll() // Permite el acceso público a las rutas de login.
                                .requestMatchers("/api/public/**").permitAll() // Permite el acceso público.
                                .requestMatchers("/api/public/courts/search?page=1&size=10/**").permitAll() // Permite el acceso público a las rutas de courts.
                                .requestMatchers("/admin/**").hasAnyRole("ADMIN")// Permite el acceso público a las rutas de registro.
                                .requestMatchers("/api/auth/verifyEmail/**").permitAll()
                                .requestMatchers("/api/user/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                                .requestMatchers("/api/roles/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                                .anyRequest().authenticated() // Requiere autenticación para cualquier otra solicitud.
                )

                // 4. Configura cómo manejar excepciones de autenticación.
                .exceptionHandling(
                        exceptionConfig -> exceptionConfig.authenticationEntryPoint(authenticationEntryPoint)
                        // Utiliza el punto de entrada personalizado para manejar errores de autenticación.
                )

                // 5. Configura la gestión de sesiones.
                .sessionManagement(
                        sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        // Configura la política de sesiones como STATELESS, lo que significa que no se utilizarán sesiones de usuario (ideal para APIs con JWT).
                )

                // 6. Agrega un filtro personalizado antes del filtro estándar de autenticación de usuario y contraseña.
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // Devuelve la configuración construida.
        return httpSecurity.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
