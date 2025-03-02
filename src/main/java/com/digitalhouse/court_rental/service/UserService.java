package com.digitalhouse.court_rental.service;

import com.digitalhouse.court_rental.dto.UserDTO;
import com.digitalhouse.court_rental.dto.UserRequestDto;
import com.digitalhouse.court_rental.entity.Rol;
import com.digitalhouse.court_rental.entity.User;
import com.digitalhouse.court_rental.entity.court.City;
import com.digitalhouse.court_rental.enums.NameRol;
import com.digitalhouse.court_rental.repository.CityRepository;
import com.digitalhouse.court_rental.repository.DocumentTypeRepository;
import com.digitalhouse.court_rental.repository.RolRepository;
import com.digitalhouse.court_rental.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.digitalhouse.court_rental.entity.DocumentType;


import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RolRepository rolRepository;
    private final CityRepository cityRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final DocumentTypeRepository documentTypeRepository;

    public User registerUser(UserRequestDto  userRequestDto) {

        if (userRepository.existsByEmail(userRequestDto.getEmail())) {
            throw new RuntimeException("The email is already registered.");
        }

        DocumentType documentType = documentTypeRepository.findById(userRequestDto.getIdDocumentType())
                .orElseThrow(() -> new RuntimeException("Document type not found"));

        City city = cityRepository.findById(Math.toIntExact(userRequestDto.getCityId()))
                .orElseThrow(() -> new RuntimeException("City not found"));

        User user = new User();
        user.setDocument(userRequestDto.getDocument());
        user.setName(userRequestDto.getName());
        user.setLastName(userRequestDto.getLastName());
        user.setEmail(userRequestDto.getEmail());
        user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        user.setPhoneNumber(userRequestDto.getPhoneNumber());
        user.setBirthdate(userRequestDto.getBirthdate());
        user.setRegistrationDate(LocalDateTime.now());
        user.setCity(city);
        user.setDocumentType(documentType);
        user.setStatusId(7);

        Rol rolUser = rolRepository.findByName(NameRol.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        Set<Rol> roles = new HashSet<>();
        roles.add(rolUser);
        user.setRoles(roles);

        return userRepository.save(user);
    }

}
