package sn.association.cotisations.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {

    @Test
    void hash_isNotPlaintext() {
        String plain = "admin123";
        String hash = PasswordUtil.hash(plain);
        assertNotNull(hash);
        assertNotEquals(plain, hash);
        assertTrue(hash.startsWith("$2"), "BCrypt hash should start with $2");
    }

    @Test
    void matches_returnsTrueForCorrectPassword() {
        String hash = PasswordUtil.hash("secret-pass");
        assertTrue(PasswordUtil.matches("secret-pass", hash));
    }

    @Test
    void matches_returnsFalseForWrongPassword() {
        String hash = PasswordUtil.hash("secret-pass");
        assertFalse(PasswordUtil.matches("wrong", hash));
    }

    @Test
    void twoHashesOfSamePassword_areDifferent() {
        String h1 = PasswordUtil.hash("same");
        String h2 = PasswordUtil.hash("same");
        assertNotEquals(h1, h2, "Salt should make each hash unique");
        assertTrue(PasswordUtil.matches("same", h1));
        assertTrue(PasswordUtil.matches("same", h2));
    }

    @Test
    void matches_handlesNullAndBlank() {
        assertFalse(PasswordUtil.matches(null, "$2a$12$abc"));
        assertFalse(PasswordUtil.matches("pwd", null));
        assertFalse(PasswordUtil.matches("pwd", ""));
        assertFalse(PasswordUtil.matches("pwd", "not-a-bcrypt-hash"));
    }
}
