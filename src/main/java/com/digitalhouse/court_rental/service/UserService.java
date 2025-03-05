package com.digitalhouse.court_rental.service;

import com.digitalhouse.court_rental.dto.AuthRequestDTO;
import com.digitalhouse.court_rental.dto.AuthResponseDTO;
import com.digitalhouse.court_rental.dto.UserRequestDto;
import com.digitalhouse.court_rental.entity.Rol;
import com.digitalhouse.court_rental.entity.User;
import com.digitalhouse.court_rental.enums.NameRol;
import com.digitalhouse.court_rental.repository.RolRepository;
import com.digitalhouse.court_rental.repository.UserRepository;
import com.digitalhouse.court_rental.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.digitalhouse.court_rental.entity.court.Country;
import com.digitalhouse.court_rental.repository.*;


import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RolRepository rolRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    private final CountryRepository countryRepository;

    public User registerUser(UserRequestDto  userRequestDto) {

        if (userRepository.existsByEmail(userRequestDto.getEmail())) {
            throw new RuntimeException("The email is already registered.");
        }

        Country country = countryRepository.findById(Math.toIntExact(userRequestDto.getCountryId()))
                .orElseThrow(() -> new RuntimeException("Country not found"));

        User user = new User();
        user.setName(userRequestDto.getName());
        user.setLastName(userRequestDto.getLastName());
        user.setEmail(userRequestDto.getEmail());
        user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        user.setPhoneNumber(userRequestDto.getPhoneNumber());
        user.setBirthdate(userRequestDto.getBirthdate());
        user.setRegistrationDate(LocalDateTime.now());
        user.setCountry(country);
        user.setStatusId(7);

        Rol rolUser = rolRepository.findByName(NameRol.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        Set<Rol> roles = new HashSet<>();
        roles.add(rolUser);
        user.setRoles(roles);

        return userRepository.save(user);
    }

    public AuthResponseDTO authenticate(AuthRequestDTO authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );
        }catch (Exception e){
            throw new RuntimeException("Invalid email or password. Please try again.");
        }

        User user = userRepository.findByEmail(authRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String role = user.getRoles().iterator().next().getName().name();


        final String jwt = jwtUtil.generateToken(authRequest.getEmail(), role);
        return new AuthResponseDTO(jwt, user.getName()+" "+user.getLastName(),role);
    }

}
