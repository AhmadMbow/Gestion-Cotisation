package sn.association.cotisations.util;

import org.mindrot.jbcrypt.BCrypt;

public final class PasswordUtil {

    private static final int WORKLOAD = 12;

    private PasswordUtil() {
    }

    public static String hash(String plain) {
        return BCrypt.hashpw(plain, BCrypt.gensalt(WORKLOAD));
    }

    public static boolean matches(String plain, String hashed) {
        if (plain == null || hashed == null || hashed.isBlank()) return false;
        try {
            return BCrypt.checkpw(plain, hashed);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
