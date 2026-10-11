package sn.association.cotisations.api;

import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.mail.MessagingException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sn.association.cotisations.dao.ConnexionDAO;
import sn.association.cotisations.dao.MembreDAO;
import sn.association.cotisations.entity.Connexion;
import sn.association.cotisations.entity.Membre;
import sn.association.cotisations.service.ApiTokenService;
import sn.association.cotisations.service.AuthService;
import sn.association.cotisations.service.PasswordResetService;
import sn.association.cotisations.util.LoginRateLimiter;
import sn.association.cotisations.util.PasswordUtil;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * POST /api/auth/login               {email, motDePasse} → {jeton, expireLe, utilisateur}
 * POST /api/auth/logout              (jeton révoqué)
 * POST /api/auth/mot-de-passe        {actuel, nouveau}
 * POST /api/auth/mot-de-passe-oublie {email} → lien envoyé par email
 */
@WebServlet(name = "AuthApiServlet", urlPatterns = {"/api/auth/*"})
public class AuthApiServlet extends ApiServlet {

    private static final Logger log = LoggerFactory.getLogger(AuthApiServlet.class);

    @Inject AuthService authService;
    @Inject ApiTokenService apiTokenService;
    @Inject PasswordResetService passwordResetService;
    @Inject LoginRateLimiter rateLimiter;
    @Inject ConnexionDAO connexionDAO;
    @Inject MembreDAO membreDAO;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        run(req, resp, (rq, rs) -> {
            List<String> p = chemin(rq);
            String action = p.isEmpty() ? "" : p.get(0);
            return switch (action) {
                case "login" -> login(rq);
                case "logout" -> {
                    apiTokenService.revoquer((String) rq.getAttribute(JETON));
                    yield null;
                }
                case "mot-de-passe" -> changerMotDePasse(rq);
                case "mot-de-passe-oublie" -> motDePasseOublie(rq);
                default -> throw new ApiException(HttpServletResponse.SC_NOT_FOUND, "Ressource introuvable.");
            };
        });
    }

    private Dto.Session login(HttpServletRequest req) throws IOException {
        String ip = ip(req);
        if (rateLimiter.isBlocked(ip)) {
            long min = Math.max(1, rateLimiter.secondsUntilUnblock(ip) / 60);
            throw new ApiException(429, "Trop de tentatives échouées. Réessayez dans " + min + " min.");
        }
        JsonObject body = corps(req);
        String email = texte(body, "email");
        Optional<Membre> opt = authService.authenticate(email, texte(body, "motDePasse"));
        if (opt.isEmpty()) {
            rateLimiter.recordFailure(ip);
            log.warn("Échec d'authentification API pour {} depuis {}", email, ip);
            throw new ApiException(HttpServletResponse.SC_UNAUTHORIZED, "E-mail ou mot de passe incorrect.");
        }
        rateLimiter.recordSuccess(ip);

        Membre m = opt.get();
        String ua = req.getHeader("User-Agent");
        if (ua != null && ua.length() > 255) ua = ua.substring(0, 255);
        try {
            connexionDAO.save(new Connexion(m, LocalDateTime.now(), ip, ua));
        } catch (RuntimeException e) {
            log.warn("Impossible d'enregistrer la connexion de {} : {}", m.getEmail(), e.getMessage());
        }

        Dto.Session s = new Dto.Session();
        s.jeton = apiTokenService.emettre(m, ua);
        s.expireLe = LocalDateTime.now().plus(ApiTokenService.VALIDITE);
        s.utilisateur = Dto.of(m);
        return s;
    }

    private Object changerMotDePasse(HttpServletRequest req) throws IOException {
        Membre m = utilisateur(req);
        JsonObject body = corps(req);
        String actuel = texte(body, "actuel");
        String nouveau = texte(body, "nouveau");
        if (!PasswordUtil.matches(actuel, m.getMotDePasse())) {
            throw new IllegalArgumentException("Le mot de passe actuel est incorrect.");
        }
        if (nouveau == null || nouveau.length() < 6) {
            throw new IllegalArgumentException("Le nouveau mot de passe doit faire au moins 6 caractères.");
        }
        m.setMotDePasse(PasswordUtil.hash(nouveau));
        membreDAO.save(m);
        return null;
    }

    private Object motDePasseOublie(HttpServletRequest req) throws IOException {
        String email = texte(corps(req), "email");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("Veuillez saisir votre e-mail.");
        try {
            passwordResetService.demander(email, PasswordResetService.baseUrl(req));
        } catch (MessagingException e) {
            log.error("Échec d'envoi du mail de réinitialisation à {}", email, e);
            throw new ApiException(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "Impossible d'envoyer l'e-mail pour le moment. Réessayez plus tard.");
        }
        return null; // réponse identique que le compte existe ou non
    }

    /** IP du client (derrière Nginx, transmise dans X-Real-IP). */
    public static String ip(HttpServletRequest req) {
        String real = req.getHeader("X-Real-IP");
        return real != null && !real.isBlank() ? real : req.getRemoteAddr();
    }
}
