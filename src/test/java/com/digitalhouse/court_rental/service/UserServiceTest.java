package com.digitalhouse.court_rental.service;

import com.digitalhouse.court_rental.dto.PasswordUpdateDTO;
import com.digitalhouse.court_rental.dto.UserDTO;
import com.digitalhouse.court_rental.entity.User;
import com.digitalhouse.court_rental.repository.CountryRepository;
import com.digitalhouse.court_rental.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllUsers() {
        User user1 = new User();
        user1.setId_user(1L);
        user1.setEmail("user1@example.com");

        User user2 = new User();
        user2.setId_user(2L);
        user2.setEmail("user2@example.com");

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<UserDTO> users = userService.getAllUsers();

        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals("user1@example.com", users.get(0).getEmail());
        assertEquals("user2@example.com", users.get(1).getEmail());
    }

    @Test
    void testGetCurrentUser_Success() {
        String email = "test@example.com";

        User user = new User();
        user.setId_user(1L);
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserDTO userDTO = userService.getCurrentUser(email);

        assertNotNull(userDTO);
        assertEquals(email, userDTO.getEmail());
    }

    @Test
    void testGetCurrentUser_UserNotFound() {
        String email = "notfound@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> userService.getCurrentUser(email));

        assertEquals("Usuario no encontrado", exception.getMessage());
    }

    @Test
    void testUpdateUserData_Success() {
        String email = "test@example.com";

        User user = new User();
        user.setId_user(1L);
        user.setEmail(email);

        UserDTO userDTO = new UserDTO();
        userDTO.setName("Updated Name");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserDTO updatedUser = userService.updateUserData(email, userDTO, null);

        assertNotNull(updatedUser);
        assertEquals("Updated Name", user.getName());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdateUserData_CountryNotFound() {
        String email = "test@example.com";

        User user = new User();
        user.setId_user(1L);
        user.setEmail(email);

        UserDTO userDTO = new UserDTO();
        userDTO.setCountry(99);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(countryRepository.findById(99)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.updateUserData(email, userDTO, null));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Country not found", exception.getReason());
    }


    @Test
    void testUpdatePassword_Success() {
        String email = "test@example.com";

        User user = new User();
        user.setId_user(1L);
        user.setEmail(email);
        user.setPassword("encodedOldPassword");

        PasswordUpdateDTO passwordUpdateDTO = new PasswordUpdateDTO();
        passwordUpdateDTO.setOldPassword("oldPassword");
        passwordUpdateDTO.setNewPassword("newPassword");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPassword", "encodedOldPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        userService.updatePassword(email, passwordUpdateDTO);

        assertEquals("encodedNewPassword", user.getPassword());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdatePassword_OldPasswordIncorrect() {
        String email = "test@example.com";

        User user = new User();
        user.setId_user(1L);
        user.setEmail(email);
        user.setPassword("encodedOldPassword");

        PasswordUpdateDTO passwordUpdateDTO = new PasswordUpdateDTO();
        passwordUpdateDTO.setOldPassword("wrongPassword");
        passwordUpdateDTO.setNewPassword("newPassword");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "encodedOldPassword")).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.updatePassword(email, passwordUpdateDTO));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Old password is incorrect", exception.getReason());
    }

}