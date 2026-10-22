package com.campusresolve.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordUtil {
    private static final String PBKDF2_PREFIX = "pbkdf2_sha256";
    private static final int ITERATIONS = 210_000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;

    private PasswordUtil() { }

    public static String sha256(String password) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte value : digest) hex.append(String.format("%02x", value));
            return hex.toString();
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }

    public static String hash(String password) {
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);
        byte[] derived = derive(password, salt, ITERATIONS);
        return PBKDF2_PREFIX + "$" + ITERATIONS + "$"
                + Base64.getEncoder().withoutPadding().encodeToString(salt) + "$"
                + Base64.getEncoder().withoutPadding().encodeToString(derived);
    }

    public static boolean matches(String password, String storedHash) {
        if (storedHash == null || storedHash.isBlank()) return false;
        if (!storedHash.startsWith(PBKDF2_PREFIX + "$")) {
            return MessageDigest.isEqual(
                    sha256(password).getBytes(StandardCharsets.US_ASCII),
                    storedHash.getBytes(StandardCharsets.US_ASCII));
        }
        String[] parts = storedHash.split("\\$", -1);
        if (parts.length != 4) return false;
        try {
            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expected = Base64.getDecoder().decode(parts[3]);
            return MessageDigest.isEqual(expected, derive(password, salt, iterations));
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    public static boolean needsUpgrade(String storedHash) {
        return storedHash != null && !storedHash.startsWith(PBKDF2_PREFIX + "$");
    }

    private static byte[] derive(String password, byte[] salt, int iterations) {
        try {
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, KEY_LENGTH);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec).getEncoded();
        } catch (Exception exception) {
            throw new IllegalStateException("PBKDF2 password hashing is unavailable.", exception);
        }
    }
}
