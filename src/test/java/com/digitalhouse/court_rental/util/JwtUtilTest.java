package com.digitalhouse.court_rental.util;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtils jwtUtils;
    private String testToken;

    @BeforeEach
    void setUp() {
//        Dotenv dotenv = Dotenv.load();
//        jwtUtil = new JwtUtil(dotenv.get("JWT_SECRET"));
//        jwtUtil = new JwtUtil();
        testToken = jwtUtils.generateToken("test@example.com");
    }

    @Test
    void testGenerateToken() {
        assertNotNull(testToken);
    }

//    @Test
//    void testExtractEmail() {
//        assertEquals("test@example.com", jwtUtil.extractEmail(testToken));
//    }
//
//    @Test
//    void testExtractRole() {
//        assertEquals("ROLE_USER", jwtUtil.extractRole(testToken));
//    }
//
//    @Test
//    void testIsTokenValid() {
//        assertTrue(jwtUtil.isTokenValid(testToken, "test@example.com"));
//    }

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