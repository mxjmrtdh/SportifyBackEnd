package com.digitalhouse.court_rental.controller;

import com.digitalhouse.court_rental.enums.NameRol;
import com.digitalhouse.court_rental.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @PutMapping("/update/{userId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<String> updateUserRole(@PathVariable Long userId, @RequestParam NameRol newRole) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String superAdminEmail = authentication.getName();

        roleService.updateUserRole(superAdminEmail, userId, newRole);
        return ResponseEntity.ok("User role updated successfully");
    }
}
