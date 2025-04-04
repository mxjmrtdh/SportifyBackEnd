package com.digitalhouse.court_rental.service;

import com.digitalhouse.court_rental.entity.Rol;
import com.digitalhouse.court_rental.entity.User;
import com.digitalhouse.court_rental.enums.NameRol;
import com.digitalhouse.court_rental.repository.RolRepository;
import com.digitalhouse.court_rental.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private RoleService roleService;

    private User adminUser;
    private User targetUser;
    private Rol adminRole;
    private Rol userRole;

    @BeforeEach
    void setUp() {
        adminRole = new Rol();
        adminRole.setId(1L);
        adminRole.setName(NameRol.ROLE_ADMIN);

        userRole = new Rol();
        userRole.setId(2L);
        userRole.setName(NameRol.ROLE_USER);

        adminUser = new User();
        adminUser.setId_user(1L);
        adminUser.setEmail("admin@example.com");
        adminUser.setRoles(Set.of(adminRole));

        targetUser = new User();
        targetUser.setId_user(2L);
        targetUser.setEmail("user@example.com");
        targetUser.setRoles(Set.of(userRole));
    }

    @Test
    void testUpdateUserRole_Success() {
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(targetUser));
        when(rolRepository.findByName(NameRol.ROLE_ADMIN)).thenReturn(Optional.of(adminRole));
        when(rolRepository.findByName(NameRol.ROLE_USER)).thenReturn(Optional.of(userRole));

        roleService.updateUserRole("admin@example.com", 2L, NameRol.ROLE_ADMIN);

        verify(userRepository, times(1)).save(targetUser);
        assertTrue(targetUser.getRoles().contains(adminRole));
        assertTrue(targetUser.getRoles().contains(userRole));
    }

    @Test
    void testUpdateUserRole_AdminNotFound() {
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> roleService.updateUserRole("admin@example.com", 2L, NameRol.ROLE_ADMIN));

        assertEquals("Admin not found", exception.getMessage());
    }

    @Test
    void testUpdateUserRole_NoPermission() {
        User nonAdminUser = new User();
        nonAdminUser.setId_user(3L);
        nonAdminUser.setEmail("nonadmin@example.com");
        nonAdminUser.setRoles(Set.of(userRole));

        when(userRepository.findByEmail("nonadmin@example.com")).thenReturn(Optional.of(nonAdminUser));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> roleService.updateUserRole("nonadmin@example.com", 2L, NameRol.ROLE_ADMIN));

        assertEquals("Only SUPER_ADMIN and ADMIN can change user roles", exception.getMessage());
    }

    @Test
    void testUpdateUserRole_TargetUserNotFound() {
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> roleService.updateUserRole("admin@example.com", 99L, NameRol.ROLE_ADMIN));

        assertEquals("User not found", exception.getMessage());
    }


    @Test
    void testUpdateUserRole_RoleNotFound() {
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(targetUser));
        when(rolRepository.findByName(NameRol.ROLE_ADMIN)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> roleService.updateUserRole("admin@example.com", 2L, NameRol.ROLE_ADMIN));

        assertEquals("Admin role not found", exception.getMessage());
    }
}