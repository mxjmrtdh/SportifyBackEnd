package com.digitalhouse.court_rental.service;


import com.digitalhouse.court_rental.entity.Court;
import com.digitalhouse.court_rental.entity.Favorite;
import com.digitalhouse.court_rental.entity.User;
import com.digitalhouse.court_rental.repository.CourtRepository;
import com.digitalhouse.court_rental.repository.FavoriteRepository;
import com.digitalhouse.court_rental.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class FavoriteServiceTest {

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CourtRepository courtRepository;

    @InjectMocks
    private FavoriteService favoriteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testToggleFavorite_AddFavorite() {
        Long userId = 1L;
        Long courtId = 2L;

        when(favoriteRepository.findByUserIdAndCourtId(userId, courtId)).thenReturn(Optional.empty());

        User user = new User();
        user.setId_user(userId);
        Court court = new Court();
        court.setIdCourt(Math.toIntExact(courtId));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(courtRepository.findById(courtId)).thenReturn(Optional.of(court));

        favoriteService.toggleFavorite(userId, courtId);

        verify(favoriteRepository, times(1)).save(any(Favorite.class));
    }

    @Test
    void testToggleFavorite_RemoveFavorite() {
        Long userId = 1L;
        Long courtId = 2L;

        Favorite favorite = new Favorite();
        when(favoriteRepository.findByUserIdAndCourtId(userId, courtId)).thenReturn(Optional.of(favorite));

        favoriteService.toggleFavorite(userId, courtId);

        verify(favoriteRepository, times(1)).delete(favorite);
    }

    @Test
    void testToggleFavorite_UserNotFound() {
        Long userId = 1L;
        Long courtId = 2L;

        when(favoriteRepository.findByUserIdAndCourtId(userId, courtId)).thenReturn(Optional.empty());
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> favoriteService.toggleFavorite(userId, courtId));

        assertEquals("Usuario no encontrado", exception.getMessage());
    }

    @Test
    void testToggleFavorite_CourtNotFound() {
        Long userId = 1L;
        Long courtId = 2L;

        User user = new User();
        user.setId_user(userId);
        when(favoriteRepository.findByUserIdAndCourtId(userId, courtId)).thenReturn(Optional.empty());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(courtRepository.findById(courtId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> favoriteService.toggleFavorite(userId, courtId));

        assertEquals("Cancha no encontrada", exception.getMessage());
    }

    @Test
    void testGetUserFavorites_Success() {
        String email = "test@example.com";

        User user = new User();
        user.setId_user(1L);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        Court court1 = new Court();
        court1.setIdCourt(1);
        Court court2 = new Court();
        court2.setIdCourt(2);
        when(favoriteRepository.findCourtsByUserId(user.getId_user())).thenReturn(List.of(court1, court2));

        List<Court> favorites = favoriteService.getUserFavorites(email);

        assertNotNull(favorites);
        assertEquals(2, favorites.size());
        assertEquals(1L, favorites.get(0).getIdCourt());
        assertEquals(2L, favorites.get(1).getIdCourt());
    }

    @Test
    void testGetUserFavorites_UserNotFound() {
        String email = "test@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> favoriteService.getUserFavorites(email));

        assertEquals("Usuario no encontrado", exception.getMessage());
    }

}