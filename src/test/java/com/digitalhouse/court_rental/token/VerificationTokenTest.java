package com.digitalhouse.court_rental.token;

import com.digitalhouse.court_rental.entity.User;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
class VerificationTokenTest {

    @Test
    void testCalculateExpirationTime_DefaultExpiration() {
        VerificationToken token = new VerificationToken("sampleToken");
        Date expirationTime = token.getExpirationTime();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, 30);

        assertNotNull(expirationTime);
        assertEquals((float) calendar.getTime().getTime() / 1000, (float) expirationTime.getTime() / 1000, 1);
    }

    @Test
    void testConstructorWithTokenAndUser() {
        User user = new User();
        user.setId_user(1L);
        user.setName("John Doe");

        VerificationToken token = new VerificationToken("sampleToken", user);

        assertNotNull(token);
        assertEquals("sampleToken", token.getToken());
        assertEquals(user, token.getUser());
        assertNotNull(token.getExpirationTime());
    }

    @Test
    void testConstructorWithTokenOnly() {
        VerificationToken token = new VerificationToken("sampleToken");

        assertNotNull(token);
        assertEquals("sampleToken", token.getToken());
        assertNotNull(token.getExpirationTime());
    }

}