package com.digitalhouse.court_rental.service.Auth;

import com.digitalhouse.court_rental.Repo.UserRepo;
import com.digitalhouse.court_rental.dto.AuthRequestDto;
import com.digitalhouse.court_rental.dto.AuthResponseDTO;
import com.digitalhouse.court_rental.entity.Rol;
import com.digitalhouse.court_rental.entity.User;
import com.digitalhouse.court_rental.entity.court.Country;
import com.digitalhouse.court_rental.enums.NameRol;
import com.digitalhouse.court_rental.repository.CountryRepository;
import com.digitalhouse.court_rental.repository.RolRepository;
import com.digitalhouse.court_rental.token.VerificationToken;
import com.digitalhouse.court_rental.token.VerificationTokenRepository;
import com.digitalhouse.court_rental.util.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class AuthServiceImplTest {
    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepo userRepo;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private VerificationTokenRepository tokenRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private AuthServiceImpl authService;

    private User sampleUser;
    private Rol userRole;
    private Country sampleCountry;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userRole = new Rol();
        userRole.setId(1L);
        userRole.setName(NameRol.ROLE_USER);

        sampleCountry = new Country();
        sampleCountry.setIdCountry(1);
        sampleCountry.setCountryName("Sample Country");

        sampleUser = new User();
        sampleUser.setId_user(1L);
        sampleUser.setName("John");
        sampleUser.setLastName("Doe");
        sampleUser.setEmail("john.doe@example.com");
        sampleUser.setPassword("encodedPassword");
        sampleUser.setStatusId(7);
        sampleUser.setRoles(Set.of(userRole));
        sampleUser.setCountry(sampleCountry);
    }

    @Test
    void testLogin_UserNotFound() {
        String email = "nonexistent@example.com";
        String password = "password";

        when(userRepo.findByEmail(email)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.login(email, password));
        assertEquals("Usuario no encontrado", exception.getMessage());
    }

    @Test
    void testLogin_UserNotActive() {
        String email = "john.doe@example.com";
        String password = "password";

        sampleUser.setStatusId(0); // Usuario no activo
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(sampleUser));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.login(email, password));
        assertEquals("La cuenta no esta activa", exception.getMessage());
    }


    @Test
    void testSignUp_Success() {
        AuthRequestDto requestDto = new AuthRequestDto();
        requestDto.setName("John");
        requestDto.setLastName("Doe");
        requestDto.setEmail("john.doe@example.com");
        requestDto.setPassword("password");
        requestDto.setPhoneNumber("123456789");
        requestDto.setBirthdate(LocalDate.from(LocalDateTime.now()));
        requestDto.setCountryId(1L);

        when(userRepo.existsByEmail(requestDto.getEmail())).thenReturn(false);
        when(countryRepository.findById(1)).thenReturn(Optional.of(sampleCountry));
        when(rolRepository.findByName(NameRol.ROLE_USER)).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(requestDto.getPassword())).thenReturn("encodedPassword");
        when(userRepo.save(any(User.class))).thenReturn(sampleUser);

        User result = authService.signUp(requestDto);

        assertNotNull(result);
        assertEquals("John", result.getName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john.doe@example.com", result.getEmail());
        assertEquals("encodedPassword", result.getPassword());
        assertEquals(sampleCountry, result.getCountry());
        assertTrue(result.getRoles().contains(userRole));
        verify(userRepo, times(1)).save(any(User.class));
        verify(eventPublisher, times(1)).publishEvent(any());
    }

    @Test
    void testSignUp_EmailAlreadyExists() {
        AuthRequestDto requestDto = new AuthRequestDto();
        requestDto.setEmail("john.doe@example.com");

        when(userRepo.existsByEmail(requestDto.getEmail())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.signUp(requestDto));
        assertEquals("El correo electrónico ya existe", exception.getMessage());
    }

    @Test
    void testValidateToken_NotFound() {
        when(tokenRepository.findByToken("invalidToken")).thenReturn(null);

        String result = authService.validateToken("invalidToken");

        assertEquals("Token de verificación no válido", result);
    }

}