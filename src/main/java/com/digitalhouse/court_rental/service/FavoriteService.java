package com.digitalhouse.court_rental.service;

import com.digitalhouse.court_rental.entity.Court;
import com.digitalhouse.court_rental.entity.Favorite;
import com.digitalhouse.court_rental.entity.User;
import com.digitalhouse.court_rental.repository.CourtRepository;
import com.digitalhouse.court_rental.repository.FavoriteRepository;
import com.digitalhouse.court_rental.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final CourtRepository courtRepository;

    public void toggleFavorite(Long userId, Long courtId) {
        Optional<Favorite> existingFavorite = favoriteRepository.findByUserIdAndCourtId(userId, courtId);
        if (existingFavorite.isPresent()) {
            favoriteRepository.delete(existingFavorite.get());
        } else {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
            Court court = courtRepository.findById(courtId)
                    .orElseThrow(() -> new EntityNotFoundException("Cancha no encontrada"));
            favoriteRepository.save(new Favorite(user, court));
        }
    }

    public List<Court> getUserFavorites(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return favoriteRepository.findCourtsByUserId(user.getId_user());
    }
}
