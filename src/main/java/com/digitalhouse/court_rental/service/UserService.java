package com.digitalhouse.court_rental.service;

import com.digitalhouse.court_rental.dto.UserDTO;
import com.digitalhouse.court_rental.entity.User;
import com.digitalhouse.court_rental.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(UserDTO::new).collect(Collectors.toList());
    }
}
