package com.digitalhouse.court_rental.service;

import com.digitalhouse.court_rental.dto.UserDTO;
import com.digitalhouse.court_rental.entity.Rol;
import com.digitalhouse.court_rental.entity.User;
import com.digitalhouse.court_rental.enums.NameRol;
import com.digitalhouse.court_rental.repository.RolRepository;
import com.digitalhouse.court_rental.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RolRepository rolRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public User registerUser(UserDTO userDTO) {

        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("The email is already registered.");
        }

        User user = new User();
        user.setDocument(userDTO.getDocument());
        user.setName(userDTO.getName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        user = userRepository.save(user);

        Rol rolUser = rolRepository.findByName(NameRol.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        Set<Rol> roles = new HashSet<>();
        roles.add(rolUser);
        user.setRoles(roles);

        return userRepository.save(user);
    }

}
