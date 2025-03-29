package com.digitalhouse.court_rental.controller;

import com.digitalhouse.court_rental.dto.PasswordUpdateDTO;
import com.digitalhouse.court_rental.dto.UserDTO;
//import com.digitalhouse.court_rental.service.UserService;
import com.digitalhouse.court_rental.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/currentUser")
    public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        UserDTO userDTO = userService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(userDTO);
    }

    @PutMapping("/update-password")
    public ResponseEntity<String> updatePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody PasswordUpdateDTO passwordUpdateDTO) {

        try {
            userService.updatePassword(userDetails.getUsername(), passwordUpdateDTO);
            return ResponseEntity.ok("Password updated successfully");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
