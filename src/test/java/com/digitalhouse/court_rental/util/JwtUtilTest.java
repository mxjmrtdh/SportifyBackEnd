package com.digitalhouse.court_rental.util;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtils jwtUtils;
    private String testToken;

    @BeforeEach
    void setUp() {
        testToken = jwtUtils.generateToken("test@example.com");
    }

    @Test
    void testGenerateToken() {
        assertNotNull(testToken);
        assertTrue(testToken.startsWith("eyJ"));
    }

    @Test
    void testValidateToken_ValidToken() {
        assertTrue(JwtUtils.validateToken(testToken));
    }

    @Test
    void testValidateToken_InvalidToken() {
        String invalidToken = "invalid.token.value";
        assertFalse(JwtUtils.validateToken(invalidToken));
    }

    @Test
    void testGetUsernameFromToken_ValidToken() {
        Optional<String> username = JwtUtils.getUsernameFromToken(testToken);
        assertTrue(username.isPresent());
        assertEquals("test@example.com", username.get());
    }

    @Test
    void testGetUsernameFromToken_InvalidToken() {
        String invalidToken = "invalid.token.value";
        Optional<String> username = JwtUtils.getUsernameFromToken(invalidToken);
        assertFalse(username.isPresent());
    }

}