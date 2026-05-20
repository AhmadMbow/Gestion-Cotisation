package sn.association.cotisations.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * "Flash messages" — messages affichés au prochain rendu puis effacés.
 * Pattern classique pour communiquer le résultat d'une action POST après redirection.
 */
public final class FlashUtil {

    public static final String SUCCESS = "flashSuccess";
    public static final String ERROR = "flashError";

    private FlashUtil() {
    }

    public static void success(HttpServletRequest req, String message) {
        req.getSession().setAttribute(SUCCESS, message);
    }

    public static void error(HttpServletRequest req, String message) {
        req.getSession().setAttribute(ERROR, message);
    }

    /**
     * À appeler dans les JSP — lit et supprime aussitôt les messages.
     */
    public static String consume(HttpSession session, String key) {
        Object v = session.getAttribute(key);
        if (v != null) session.removeAttribute(key);
        return v != null ? v.toString() : null;
    }
}
