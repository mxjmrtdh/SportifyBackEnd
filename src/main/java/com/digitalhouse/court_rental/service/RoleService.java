package com.digitalhouse.court_rental.service;

import com.digitalhouse.court_rental.entity.Rol;
import com.digitalhouse.court_rental.entity.User;
import com.digitalhouse.court_rental.enums.NameRol;
import com.digitalhouse.court_rental.repository.RolRepository;
import com.digitalhouse.court_rental.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final UserRepository userRepository;
    private final RolRepository rolRepository;

    public void updateUserRole(String adminEmail, Long userId, NameRol newRole) {
        User adminUser = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        if (!hasPermissionToChangeRoles(adminUser)) {
            throw new RuntimeException("Only SUPER_ADMIN and ADMIN can change user roles");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Set<Rol> newRoles = new HashSet<>();

        if (newRole == NameRol.ROLE_ADMIN) {
            Rol adminRole = rolRepository.findByName(NameRol.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Admin role not found"));
            newRoles.add(adminRole);
        }

        Rol userRole = rolRepository.findByName(NameRol.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("User role not found"));
        newRoles.add(userRole);

        user.setRoles(newRoles);
        userRepository.save(user);
    }

    private boolean hasPermissionToChangeRoles(User user) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getName() == NameRol.ROLE_SUPER_ADMIN || role.getName() == NameRol.ROLE_ADMIN);
    }
}
