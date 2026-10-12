package sn.association.cotisations.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sn.association.cotisations.dao.CompteDAO;
import sn.association.cotisations.dao.PasswordResetTokenDAO;
import sn.association.cotisations.entity.Compte;
import sn.association.cotisations.entity.PasswordResetToken;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

/**
 * Liens « choisir / réinitialiser mon mot de passe » envoyés par email.
 * Partagé par le site web, l'API mobile et l'email de bienvenue.
 */
@ApplicationScoped
public class PasswordResetService {

    private static final Logger log = LoggerFactory.getLogger(PasswordResetService.class);

    /** Validité d'un lien « mot de passe oublié ». */
    public static final Duration TTL_REINITIALISATION = Duration.ofMinutes(30);
    /** Validité du lien de l'email de bienvenue. */
    public static final Duration TTL_BIENVENUE = Duration.ofDays(7);

    @Inject PasswordResetTokenDAO tokenDAO;
    @Inject CompteDAO compteDAO;
    @Inject MailService mailService;

    private final SecureRandom random = new SecureRandom();

    /** Crée un jeton à usage unique et renvoie le lien vers la page de choix du mot de passe. */
    public String creerLien(Compte c, Duration ttl, String baseUrl) {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        tokenDAO.save(new PasswordResetToken(c, token, LocalDateTime.now().plus(ttl)));
        return baseUrl + "/reset-password?token=" + token;
    }

    /**
     * Envoie un lien de réinitialisation si l'email correspond à un compte.
     * Ne révèle jamais si le compte existe (message générique côté appelant).
     */
    public void demander(String email, String baseUrl) throws MessagingException {
        if (email == null || email.isBlank()) return;
        Optional<Compte> opt = compteDAO.findByEmail(email.trim().toLowerCase());
        if (opt.isEmpty()) {
            log.info("Tentative de reset pour email inconnu : {}", email);
            return;
        }
        Compte m = opt.get();
        String lien = creerLien(m, TTL_REINITIALISATION, baseUrl);
        EmailTemplate mail = EmailTemplate.titre("Réinitialisation de votre mot de passe")
                .intro("Bonjour " + m.getPrenom() + ",")
                .intro("Vous avez demandé à réinitialiser votre mot de passe Cotisen. "
                        + "Ce lien est valable " + TTL_REINITIALISATION.toMinutes() + " minutes.")
                .bouton("Choisir un nouveau mot de passe", lien)
                .conclusion("Si vous n'êtes pas à l'origine de cette demande, ignorez simplement ce message : "
                        + "votre mot de passe actuel reste valable.");
        mailService.send(m.getEmail(), "Réinitialisation de votre mot de passe", mail.texte(), mail.html());
    }

    /**
     * URL publique de l'application, pour les liens des emails.
     * Variable APP_BASE_URL si définie (indispensable derrière un proxy), sinon déduite de la requête.
     */
    public static String baseUrl(HttpServletRequest req) {
        String env = System.getenv("APP_BASE_URL");
        if (env == null || env.isBlank()) env = System.getenv("RENDER_EXTERNAL_URL"); // fourni par Render
        if (env != null && !env.isBlank()) return env.replaceAll("/+$", "");
        StringBuilder url = new StringBuilder();
        url.append(req.getScheme()).append("://").append(req.getServerName());
        int port = req.getServerPort();
        if ((req.getScheme().equals("http") && port != 80)
                || (req.getScheme().equals("https") && port != 443)) {
            url.append(':').append(port);
        }
        return url + req.getContextPath();
    }
}
