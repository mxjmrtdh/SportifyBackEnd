package com.digitalhouse.court_rental.service;

import com.digitalhouse.court_rental.dto.PasswordUpdateDTO;
import com.digitalhouse.court_rental.dto.UserDTO;
import com.digitalhouse.court_rental.entity.User;
import com.digitalhouse.court_rental.entity.court.Country;
import com.digitalhouse.court_rental.repository.CountryRepository;
import com.digitalhouse.court_rental.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CountryRepository countryRepository;

    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(UserDTO::new).collect(Collectors.toList());
    }

    public UserDTO getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return new UserDTO(user);
    }

    public UserDTO updateUserData(String email, UserDTO userDTO, PasswordUpdateDTO passwordUpdateDTO) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (userDTO != null) {
            if (userDTO.getName() != null) {
                user.setName(userDTO.getName());
            }
            if (userDTO.getLastName() != null) {
                user.setLastName(userDTO.getLastName());
            }
            if (userDTO.getEmail() != null) {
                user.setEmail(userDTO.getEmail());
            }
            if (userDTO.getPhoneNumber() != null) {
                user.setPhoneNumber(userDTO.getPhoneNumber());
            }
            if (userDTO.getBirthdate() != null) {
                user.setBirthdate(userDTO.getBirthdate());
            }
            if (userDTO.getStatusId() != null) {
                user.setStatusId(userDTO.getStatusId());
            }
            if (userDTO.getCountry() != null) {
                Country country = countryRepository.findById(userDTO.getCountry())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Country not found"));
                user.setCountry(country);
            }

        }

        userRepository.save(user);
        System.out.println("Password updated successfully for user: " + user.getEmail());
        return new UserDTO(user);
    }

    public void updatePassword(String email, PasswordUpdateDTO passwordUpdateDTO) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(passwordUpdateDTO.getOldPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(passwordUpdateDTO.getNewPassword()));
        userRepository.save(user);
    }
}
