package com.digitalhouse.court_rental.service.Auth;

import com.digitalhouse.court_rental.Listener.RegistrationCompleteEvent;
import com.digitalhouse.court_rental.Repo.UserRepo;
import com.digitalhouse.court_rental.controller.AuthRequestDto;
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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    @Autowired
    private AuthenticationManager authenticationManager;// Se encarga de autenticar al usuario con su nombre de usuario y contraseña.
    @Autowired
    private PasswordEncoder passwordEncoder; // Codifica las contraseñas antes de guardarlas en la base de datos.
    @Autowired
    private UserRepo userRepo; // Repositorio que interactúa con la base de datos para las operaciones relacionadas con el usuario.

    @Autowired
    private final CountryRepository countryRepository;

    @Autowired
    private final RolRepository rolRepository;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    /**
     * Método que permite realizar el login de un usuario. Autentica al usuario
     * utilizando el nombre de usuario y la contraseña, y devuelve un token JWT.
     *
     * @param email El nombre de usuario que se quiere autenticar.
     * @param password La contraseña asociada al nombre de usuario.
     * @return El token JWT generado.
     */
    @Override
    public AuthResponseDTO login(String email, String password) {
        // Busca al usuario en la base de datos
        Optional<User> optionalUser = userRepo.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado");
        }

        User user = optionalUser.get();

        //Verifica si el usuario está habilitado
        if (user.getStatusId()!=7) {
            throw new RuntimeException("La cuenta no esta activa");
        }

        // Crea un token de autenticación con el nombre de usuario y la contraseña
        var authToken = new UsernamePasswordAuthenticationToken(email, password);

        // Autentica al usuario utilizando el AuthenticationManager
        var authenticate = authenticationManager.authenticate(authToken);

        //Obtenemos los roles del usuario
        String role = user.getRoles().iterator().next().getName().name();

        // Genera y devuelve un token JWT utilizando el nombre de usuario autenticado.
        final String jwt = JwtUtils.generateToken(((UserDetails) (authenticate.getPrincipal())).getUsername());

        return new AuthResponseDTO(jwt,user.getName() + " " + user.getLastName(), role);
    }

    /**
     * Método que verifica la validez de un token JWT. Extrae el nombre de
     * usuario del token y lo devuelve si el token es válido.
     *
     * @param token El token JWT que se desea verificar.
     * @return El nombre de usuario extraído del token si es válido.
     * @throws RuntimeException Si el token no es válido.
     */
    @Override
    public String verifyToken(String token) {
        // Obtiene el nombre de usuario del token.
        var usernameOptional = JwtUtils.getUsernameFromToken(token);
        // Si el token es válido, devuelve el nombre de usuario.
        if (usernameOptional.isPresent()) {
            return usernameOptional.get();
        }
        // Si el token no es válido, lanza una excepción.
        throw new RuntimeException("Token invalid");
    }

    /**
     * Método que maneja el registro de un nuevo usuario. Verifica que el nombre
     * de usuario y el correo no estén registrados previamente. Cifra la
     * contraseña y guarda al usuario en la base de datos.
     *
     * @param authRequestDto Es el objeto de creación del usuario
     * @return El token JWT generado para el usuario recién registrado.
     * @throws RuntimeException Si el nombre de usuario o el correo electrónico
     * ya están en uso.
     */
    @Override
    public User signUp(AuthRequestDto authRequestDto) {
        // Verificar si el email ya existe
        if (userRepo.existsByEmail(authRequestDto.getEmail())) {
            throw new RuntimeException("El correo electrónico ya existe");
        }
        // Crear un nuevo objeto Usuario
        User user = new User();
        user.setName(authRequestDto.getName());
        user.setLastName(authRequestDto.getLastName());
        user.setEmail(authRequestDto.getEmail());  // Asignar el email
        user.setPassword(passwordEncoder.encode(authRequestDto.getPassword())); // La contraseña se cifra antes de guardarse
        user.setPhoneNumber(authRequestDto.getPhoneNumber());
        user.setBirthdate(authRequestDto.getBirthdate());

        Country country = countryRepository.findById(Math.toIntExact(authRequestDto.getCountryId()))
                .orElseThrow(() -> new RuntimeException("Country not found"));

        user.setCountry(country);

        // Establecer la fecha y hora de registro
        user.setRegistrationDate(LocalDateTime.now());
        user.setStatusId(7); // Asegurarnos que empiece habilitado

        //Asignamos los roles
        Rol rolUser = rolRepository.findByName(NameRol.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        Set<Rol> roles = new HashSet<>();
        roles.add(rolUser);
        user.setRoles(roles);

        user = userRepo.save(user);

        // Aquí deberías publicar el evento de registro
        eventPublisher.publishEvent(new RegistrationCompleteEvent(user, "..."));

        return user;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        // Busca un usuario en la base de datos utilizando su correo electrónico.
        // Retorna un Optional que contiene al usuario si existe, o vacío si no se encuentra.
        return userRepo.findByEmail(email);
    }

    @Override
    public void saveUserVerificationToken(User theUser, String token) {
        // Crea un nuevo token de verificación para el usuario proporcionado.
        // Se utiliza una clase llamada VerificationToken para almacenar el token y el usuario asociado.
        var verificationToken = new VerificationToken(token, theUser);
        // Guarda el token de verificación en el repositorio correspondiente.
        tokenRepository.save(verificationToken);
    }

    @Override
    public String validateToken(String theToken) {
        // Busca el token de verificación en el repositorio utilizando su valor.
        VerificationToken token = tokenRepository.findByToken(theToken);
        if (token == null) {
            // Si el token no se encuentra, imprime un mensaje en la consola y retorna un mensaje de error.
            System.out.println("Token no encontrado en la base de datos.");
            return "Token de verificación no válido";
        }

        // Obtiene el usuario asociado al token.
        User user = token.getUser();
//        System.out.println("Estado actual del usuario: " + user.isEnabled());

        // Verifica si el token ha expirado comparando la fecha de expiración con la fecha actual.
        Calendar calendar = Calendar.getInstance();
        if ((token.getExpirationTime().getTime() - calendar.getTime().getTime()) <= 0) {
            // Si el token ha expirado, lo elimina del repositorio y retorna un mensaje de expiración.
            tokenRepository.delete(token);
            System.out.println("Token expirado y eliminado.");
            return "expired";
        }

        // Si el token es válido, habilita al usuario cambiando su estado a activo.
        user.setStatusId(7);
        try {
            // Guarda los cambios en el usuario en el repositorio.
            userRepo.save(user);
            System.out.println("Usuario actualizado. Nuevo estado: " + user.getStatusId());
            // Elimina el token usado después de completar la verificación.
            tokenRepository.delete(token);
            return "valido";
        } catch (Exception e) {
            // Si ocurre un error al guardar el usuario, imprime el error en la consola y retorna un mensaje de error.
            System.out.println("Error al guardar usuario: " + e.getMessage());
            e.printStackTrace();
            return "Error al actualizar usuario";
        }
    }
}
