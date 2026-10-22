package com.campusresolve.util;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class PasswordUtilTest {
    @Test void hashesAreSaltedAndVerify() {
        String first = PasswordUtil.hash("Campus@123");
        String second = PasswordUtil.hash("Campus@123");
        assertNotEquals(first, second);
        assertTrue(PasswordUtil.matches("Campus@123", first));
        assertFalse(PasswordUtil.matches("wrong", first));
    }

    @Test void legacySeedHashRemainsCompatible() {
        String legacy = PasswordUtil.sha256("Campus@123");
        assertTrue(PasswordUtil.matches("Campus@123", legacy));
        assertTrue(PasswordUtil.needsUpgrade(legacy));
        assertFalse(PasswordUtil.needsUpgrade(PasswordUtil.hash("Campus@123")));
    }
}
