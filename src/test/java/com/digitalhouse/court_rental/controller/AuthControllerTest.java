package com.digitalhouse.court_rental.controller;

import com.digitalhouse.court_rental.dto.AuthRequestDto;
import com.digitalhouse.court_rental.dto.AuthResponseDTO;
import com.digitalhouse.court_rental.entity.User;
import com.digitalhouse.court_rental.enums.AuthStatus;
import com.digitalhouse.court_rental.service.Auth.AuthService;
import com.digitalhouse.court_rental.service.UserService;
import com.digitalhouse.court_rental.token.VerificationToken;
import com.digitalhouse.court_rental.token.VerificationTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private VerificationTokenRepository tokenRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void testLogin_Success() throws Exception {
        AuthRequestDto authRequestDto = new AuthRequestDto();
        authRequestDto.setEmail("john.doe@example.com");
        authRequestDto.setPassword("password");

        AuthResponseDTO authResponseDTO = AuthResponseDTO.builder()
                .token("mockJwtToken")
                .fullName("John Doe")
                .role("ROLE_USER")
                .authStatus(AuthStatus.LOGIN_SUCCESS)
                .build();

        when(authService.login(any(String.class), any(String.class))).thenReturn(authResponseDTO);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"john.doe@example.com\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mockJwtToken"))
                .andExpect(jsonPath("$.fullName").value("John Doe"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"))
                .andExpect(jsonPath("$.authStatus").value("LOGIN_SUCCESS"));

        verify(authService, times(1)).login("john.doe@example.com", "password");
    }

    @Test
    void testLogin_Failure() throws Exception {
        when(authService.login(any(String.class), any(String.class)))
                .thenThrow(new RuntimeException("Usuario no encontrado"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"john.doe@example.com\",\"password\":\"wrongpassword\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Usuario no encontrado"))
                .andExpect(jsonPath("$.authStatus").value("LOGIN_FAILED"));

        verify(authService, times(1)).login("john.doe@example.com", "wrongpassword");
    }

    @Test
    void testSignUp_Success() throws Exception {
        AuthRequestDto authRequestDto = new AuthRequestDto();
        authRequestDto.setEmail("john.doe@example.com");
        authRequestDto.setPassword("password");

        User mockUser = new User();
        mockUser.setName("John");
        mockUser.setLastName("Doe");
        mockUser.setEmail("john.doe@example.com");

        when(authService.signUp(any(AuthRequestDto.class))).thenReturn(mockUser);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"john.doe@example.com\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("John Doe"))
                .andExpect(jsonPath("$.message").value("Usuario creado con éxito. Por favor, revise su correo electrónico y verifique su cuenta para completar el registro."))
                .andExpect(jsonPath("$.authStatus").value("USER_CREATED_SUCCESSFULLY"));

        verify(authService, times(1)).signUp(any(AuthRequestDto.class));
    }
    @Test
    void testVerifyEmail_InvalidToken() throws Exception {
        when(tokenRepository.findByToken("invalidToken")).thenReturn(null);

        MockHttpServletResponse response = mockMvc.perform(get("/api/auth/verifyEmail")
                        .param("token", "invalidToken"))
                .andReturn()
                .getResponse();

        assertEquals("http://localhost:3000/verification?status=invalid-token", response.getRedirectedUrl());
        verify(tokenRepository, times(1)).findByToken("invalidToken");
        verify(authService, never()).validateToken(anyString());
    }
}