package com.digitalhouse.court_rental.controller;

import com.digitalhouse.court_rental.dto.AuthResponseDTO;
import com.digitalhouse.court_rental.enums.AuthStatus;
import com.digitalhouse.court_rental.service.Auth.AuthService;
import com.digitalhouse.court_rental.service.Auth.AuthServiceImpl;
import com.digitalhouse.court_rental.token.VerificationToken;
import com.digitalhouse.court_rental.token.VerificationTokenRepository;
import com.digitalhouse.court_rental.dto.AuthRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService; // Inyección de dependencia del servicio encargado de la lógica de autenticación.
    private final VerificationTokenRepository tokenRepository;
    private final AuthServiceImpl userService;

    /**
     * Endpoint para el inicio de sesión (login).
     *
     * @param authRequestDto Contiene el nombre de usuario y la contraseña
     * proporcionados por el cliente.
     * @return Respuesta HTTP con un token JWT y el estado de autenticación.
     */
    @PostMapping("/login") // Define que este método manejará solicitudes POST a "/api/auth/login".
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDto authRequestDto) {
        try {
            // Llama al servicio para autenticar al usuario y generar un token JWT
            var jwtToken = authService.login(authRequestDto.getEmail(), authRequestDto.getPassword());
            // Crea un objeto de respuesta con el token y el estado de éxito

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(jwtToken);

        } catch (Exception e) {
            return buildErrorResponse(e);
        }
    }

    /**
     * Endpoint para registrar un nuevo usuario (sign-up).
     *
     * @param authRequestDto Contiene el nombre, nombre de usuario y contraseña
     * del nuevo usuario.
     * @return Respuesta HTTP con un token JWT si el registro fue exitoso, o un
     * mensaje de error si no.
     */
    @PostMapping("/register") // Define que este método manejará solicitudes POST
    public ResponseEntity<AuthResponseDTO> signUp(@RequestBody AuthRequestDto authRequestDto) {
        try {
            // Llama al servicio para registrar al usuario y generar un token JWT.
            var jwtToken = authService.signUp(authRequestDto);
            // Crea un objeto de respuesta con el token y el estado de éxito.
            var authResponseDto = AuthResponseDTO.builder()
                    .fullName(jwtToken.getName() + " " + jwtToken.getLastName())
                    .role(jwtToken.getRoles().toString())
                    .message("Usuario creado con éxito. Por favor, revise su correo electrónico y verifique su cuenta para completar el registro.")
                    .authStatus(AuthStatus.USER_CREATED_SUCCESSFULLY)
                    .build();


            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(authResponseDto);

        } catch (Exception e) {
            return buildErrorResponse(e);

        }
    }

    @GetMapping("/verifyEmail")
    public void verifyEmail(@RequestParam("token") String token, HttpServletResponse response){
        // Imprime el token recibido en la consola para verificación
        System.out.println("Recibida solicitud de verificación para token: " + token);

        try {
            // Busca el token de verificación en la base de datos utilizando el repositorio
            VerificationToken theToken = tokenRepository.findByToken(token);

            // Si no se encuentra el token, redirige al usuario a la página de verificación con un mensaje de error
            if (theToken == null) {
                System.out.println("Token no encontrado");
                response.sendRedirect("http://localhost:3000/verification?status=invalid-token");
                return;
            }

            // Valida el token y guarda el resultado
            String result = userService.validateToken(token);

            // Imprime el resultado de la validación en la consola para saber si es válido, expirado, etc.
            System.out.println("Resultado de la validación: " + result);

            // Dependiendo del resultado de la validación, redirige al usuario a diferentes páginas
            switch (result) {
                case "valido":
                    // Si el token es válido, redirige con un mensaje de éxito
                    response.sendRedirect("http://localhost:3000/verification?status=success");
                    break;
                case "expired":
                    // Si el token ya ha expirado, redirige con un mensaje de expiración
                    response.sendRedirect("http://localhost:3000/verification?status=expired");
                    break;
                default:
                    // Si el token no es válido, redirige con un mensaje de error
                    response.sendRedirect("http://localhost:3000/verification?status=invalid-token");
            }
        } catch (Exception e) {
            log.error("Error durante la verificación: {}", e.getMessage(), e);
        }
    }

    public String applicationUrl(@NotNull HttpServletRequest request) {
        // Construye la URL completa del servidor (incluye el nombre del servidor, puerto y contexto) para usarla en la verificación por correo
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    private ResponseEntity<AuthResponseDTO> buildErrorResponse(Exception e) {
        Map<String, String> errorMessages = Map.of(
                "Usuario no encontrado", "Usuario no encontrado",
                "La cuenta no ha sido verificada", "La cuenta no ha sido verificada. Por favor, revise su correo electrónico.",
                "Bad credentials", "Usuario o contraseña incorrectos",
                "Username already exists", "El nombre de usuario ya está en uso",
                "Email already exists", "El correo electrónico ya está registrado"
        );

        String errorMessage = errorMessages.entrySet().stream()
                .filter(entry -> e.getMessage().contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(e.getMessage());

        var authResponseDto = new AuthResponseDTO(null, null, null,null, errorMessage, AuthStatus.LOGIN_FAILED);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(authResponseDto);
    }

}
