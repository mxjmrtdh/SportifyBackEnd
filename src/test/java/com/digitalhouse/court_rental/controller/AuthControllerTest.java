package com.digitalhouse.court_rental.controller;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
//    @Mock
//    private UserService userService;
//
//    @InjectMocks
//    private AuthController authController;
//
//    @BeforeEach
//    void setUp() {
//    }
//
//    @Test
//    void testRegisterUser() {
//        UserRequestDto userRequestDto = new UserRequestDto();
//        User userMock = new User();
//        when(userService.registerUser(any(UserRequestDto.class))).thenReturn(userMock);
//
//        ResponseEntity<?> response = authController.registerUser(userRequestDto);
//
//        assertNotNull(response);
//        assertEquals(200, response.getStatusCode().value());
//        assertEquals("User successfully registered.", response.getBody());
//        verify(userService, times(1)).registerUser(any(UserRequestDto.class));
//    }
//
//    @Test
//    void testLogin() {
//        AuthRequestDTO authRequest = new AuthRequestDTO();
//        AuthResponseDTO authResponse = new AuthResponseDTO("dummy-token", "Jhon Doe", "USER");
//        when(userService.authenticate(any(AuthRequestDTO.class))).thenReturn(authResponse);
//
//        ResponseEntity<AuthResponseDTO> response = authController.login(authRequest);
//
//        assertNotNull(response);
//        assertEquals(200, response.getStatusCode().value());
//        assertEquals("dummy-token", response.getBody().getToken());
//        verify(userService, times(1)).authenticate(any(AuthRequestDTO.class));
//    }
}