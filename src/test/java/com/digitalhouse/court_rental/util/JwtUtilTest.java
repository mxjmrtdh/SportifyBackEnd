package com.digitalhouse.court_rental.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private String testToken;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        testToken = jwtUtil.generateToken("test@example.com", "ROLE_USER");
    }

    @Test
    void testGenerateToken() {
        assertNotNull(testToken);
    }

    @Test
    void testExtractEmail() {
        assertEquals("test@example.com", jwtUtil.extractEmail(testToken));
    }

    @Test
    void testExtractRole() {
        assertEquals("ROLE_USER", jwtUtil.extractRole(testToken));
    }

    @Test
    void testIsTokenValid() {
        assertTrue(jwtUtil.isTokenValid(testToken, "test@example.com"));
    }

    /*
    @Test
    void testIsTokenExpired() {
        String expiredToken = Jwts.builder()
                .setSubject("test@example.com")
                .setIssuedAt(new Date(System.currentTimeMillis() - 10000))
                .setExpiration(new Date(System.currentTimeMillis() - 5000))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        assertFalse(jwtUtil.isTokenValid(expiredToken, "test@example.com"));
    }
     */


}