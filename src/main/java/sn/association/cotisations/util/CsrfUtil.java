package sn.association.cotisations.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Génère et valide un jeton CSRF stocké en session.
 * Le même token reste valide pour toute la durée de la session.
 */
public final class CsrfUtil {

    public static final String SESSION_KEY = "_csrfToken";
    public static final String PARAM_NAME = "_csrf";

    private static final SecureRandom RANDOM = new SecureRandom();

    private CsrfUtil() {
    }

    /**
     * Renvoie le token de la session, en le créant si absent.
     * Crée la session si elle n'existe pas encore.
     */
    public static String tokenFor(HttpServletRequest req) {
        HttpSession session = req.getSession(true);
        String token = (String) session.getAttribute(SESSION_KEY);
        if (token == null) {
            token = generate();
            session.setAttribute(SESSION_KEY, token);
        }
        return token;
    }

    /**
     * Vérifie que le paramètre _csrf de la requête correspond au token de la session.
     * Renvoie false si la session est absente, le token absent, ou ne correspond pas.
     */
    public static boolean isValid(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return false;
        String sessionToken = (String) session.getAttribute(SESSION_KEY);
        if (sessionToken == null) return false;
        String submitted = req.getParameter(PARAM_NAME);
        return submitted != null && constantTimeEquals(sessionToken, submitted);
    }

    private static String generate() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) return false;
        int diff = 0;
        for (int i = 0; i < a.length(); i++) {
            diff |= a.charAt(i) ^ b.charAt(i);
        }
        return diff == 0;
    }
}
