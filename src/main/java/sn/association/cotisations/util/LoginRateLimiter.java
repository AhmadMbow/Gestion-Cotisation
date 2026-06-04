package sn.association.cotisations.util;

import jakarta.enterprise.context.ApplicationScoped;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Limiteur de tentatives de connexion en mémoire.
 *
 * Règle : si une IP cumule MAX_FAILURES échecs en moins de WINDOW,
 * elle est bloquée pendant BLOCK_DURATION. Le compteur est remis à zéro
 * à chaque connexion réussie.
 */
@ApplicationScoped
public class LoginRateLimiter {

    private static final int MAX_FAILURES = 5;
    private static final Duration WINDOW = Duration.ofMinutes(10);
    private static final Duration BLOCK_DURATION = Duration.ofMinutes(10);

    private final Map<String, Attempts> byIp = new ConcurrentHashMap<>();

    private static class Attempts {
        int count;
        Instant firstFailure;
        Instant blockedUntil;
    }

    /**
     * Renvoie true si l'IP est actuellement bloquée. Nettoie l'entrée si le blocage est expiré.
     */
    public boolean isBlocked(String ip) {
        Attempts a = byIp.get(ip);
        if (a == null || a.blockedUntil == null) return false;
        if (Instant.now().isBefore(a.blockedUntil)) return true;
        // Le blocage a expiré → reset
        byIp.remove(ip);
        return false;
    }

    /** Nombre de secondes restantes avant fin du blocage (0 si non bloqué). */
    public long secondsUntilUnblock(String ip) {
        Attempts a = byIp.get(ip);
        if (a == null || a.blockedUntil == null) return 0;
        long s = Duration.between(Instant.now(), a.blockedUntil).getSeconds();
        return Math.max(0, s);
    }

    /**
     * Enregistre une tentative échouée. Si le seuil est atteint, l'IP est bloquée.
     */
    public synchronized void recordFailure(String ip) {
        Instant now = Instant.now();
        Attempts a = byIp.computeIfAbsent(ip, k -> new Attempts());

        // Si la fenêtre est dépassée, on repart de zéro
        if (a.firstFailure == null || Duration.between(a.firstFailure, now).compareTo(WINDOW) > 0) {
            a.count = 0;
            a.firstFailure = now;
            a.blockedUntil = null;
        }
        a.count++;
        if (a.count >= MAX_FAILURES) {
            a.blockedUntil = now.plus(BLOCK_DURATION);
        }
    }

    /** Connexion réussie : on efface tout compteur pour cette IP. */
    public void recordSuccess(String ip) {
        byIp.remove(ip);
    }
}
