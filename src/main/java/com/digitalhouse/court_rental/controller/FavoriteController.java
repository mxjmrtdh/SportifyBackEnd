package com.digitalhouse.court_rental.controller;

import com.digitalhouse.court_rental.entity.Court;
import com.digitalhouse.court_rental.entity.User;
import com.digitalhouse.court_rental.service.Auth.AuthServiceImpl;
import com.digitalhouse.court_rental.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final AuthServiceImpl userService;

    @PostMapping("/{courtId}/toggle")
    public ResponseEntity<String> toggleFavorite(@PathVariable Long courtId) {
        User user = getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(401).body("Usuario no autenticado");
        }

        favoriteService.toggleFavorite(user.getId_user(), courtId);
        return ResponseEntity.ok("Estado de favorito actualizado");
    }

    @GetMapping
    public ResponseEntity<List<Court>> getUserFavorites() {
        User user = getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(401).body(null);
        }

        return ResponseEntity.ok(favoriteService.getUserFavorites(user.getId_user()));
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        String email = authentication.getName(); // Obtener email desde el token
        return userService.findByEmail(email).orElse(null); // Buscar usuario en la base de datos
    }

}
