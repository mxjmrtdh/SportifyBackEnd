package com.digitalhouse.court_rental.service;

import com.digitalhouse.court_rental.dto.AuthRequestDTO;
import com.digitalhouse.court_rental.dto.AuthResponseDTO;
import com.digitalhouse.court_rental.dto.UserRequestDto;
import com.digitalhouse.court_rental.entity.Rol;
import com.digitalhouse.court_rental.entity.User;
import com.digitalhouse.court_rental.entity.court.City;
import com.digitalhouse.court_rental.entity.DocumentType;
import com.digitalhouse.court_rental.enums.NameRol;
import com.digitalhouse.court_rental.repository.CityRepository;
import com.digitalhouse.court_rental.repository.DocumentTypeRepository;
import com.digitalhouse.court_rental.repository.RolRepository;
import com.digitalhouse.court_rental.repository.UserRepository;
import com.digitalhouse.court_rental.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private CityRepository cityRepository;

    @Mock
    private DocumentTypeRepository documentTypeRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserService userService;

    private UserRequestDto userRequestDto;
    private User user;
    private DocumentType documentType;
    private City city;
    private Rol rol;

    @BeforeEach
    void setUp() {
        userRequestDto = new UserRequestDto();
        userRequestDto.setName("John");
        userRequestDto.setLastName("Doe");
        userRequestDto.setEmail("john@example.com");
        userRequestDto.setPassword("password123");
        userRequestDto.setDocument("12345678");
        userRequestDto.setIdDocumentType(1L);
        userRequestDto.setCityId(1L);
        userRequestDto.setBirthdate(LocalDate.from(LocalDateTime.now()));

        documentType = new DocumentType();
        city = new City();
        rol = new Rol();
        rol.setName(NameRol.ROLE_USER);

        user = new User();
        user.setEmail("john@example.com");
        user.setPassword("encryptedPassword");
        user.setRoles(Set.of(rol));
    }

    @Test
    void registerUser_Success() {
        when(userRepository.existsByEmail(userRequestDto.getEmail())).thenReturn(false);
        when(documentTypeRepository.findById(userRequestDto.getIdDocumentType())).thenReturn(Optional.of(documentType));
        when(cityRepository.findById(Math.toIntExact(userRequestDto.getCityId()))).thenReturn(Optional.of(city));
        when(rolRepository.findByName(NameRol.ROLE_USER)).thenReturn(Optional.of(rol));
        when(passwordEncoder.encode(userRequestDto.getPassword())).thenReturn("encryptedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User savedUser = userService.registerUser(userRequestDto);

        assertNotNull(savedUser);
        assertEquals(userRequestDto.getEmail(), savedUser.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_EmailAlreadyExists_ThrowsException() {
        when(userRepository.existsByEmail(userRequestDto.getEmail())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> userService.registerUser(userRequestDto));
    }

    @Test
    void authenticate_Success() {
        AuthRequestDTO authRequest = new AuthRequestDTO();
        authRequest.setEmail("john@example.com");
        authRequest.setPassword("password123");

        when(userRepository.findByEmail(authRequest.getEmail())).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(authRequest.getEmail(), rol.getName().name())).thenReturn("jwtToken");

        AuthResponseDTO authResponse = userService.authenticate(authRequest);

        assertNotNull(authResponse);
        assertEquals("jwtToken", authResponse.getToken());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void authenticate_InvalidCredentials_ThrowsException() {
        AuthRequestDTO authRequest = new AuthRequestDTO();
        authRequest.setEmail("john@example.com");
        authRequest.setPassword("password123");

        doThrow(RuntimeException.class).when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThrows(RuntimeException.class, () -> userService.authenticate(authRequest));
    }
}
