package com.digitalhouse.court_rental.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AuthenticationConfig {
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        // Devuelve el AuthenticationManager que maneja el proceso de autenticación.
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        // Crea un proveedor de autenticación basado en DAO (acceso a datos).
        var authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService); // Establece el servicio para cargar usuarios.
        authenticationProvider.setPasswordEncoder(passwordEncoder); // Establece el codificador de contraseñas.

        return authenticationProvider; // Devuelve el proveedor configurado.
    }
    @Bean
    public PasswordEncoder bCryptPasswordEncoder() {
        // Devuelve un codificador de contraseñas que utiliza BCrypt.
        return new BCryptPasswordEncoder();
    }
}
