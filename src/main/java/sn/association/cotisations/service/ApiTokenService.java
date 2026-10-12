package sn.association.cotisations.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import sn.association.cotisations.dao.ApiTokenDAO;
import sn.association.cotisations.entity.ApiToken;
import sn.association.cotisations.entity.Compte;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;

/**
 * Jetons « Bearer » de l'application mobile (opaques, révocables, valables 30 jours).
 */
@ApplicationScoped
public class ApiTokenService {

    public static final Duration VALIDITE = Duration.ofDays(30);

    @Inject ApiTokenDAO apiTokenDAO;

    private final SecureRandom random = new SecureRandom();

    /** Crée un jeton pour ce compte et renvoie sa valeur en clair (affichée une seule fois). */
    public String emettre(Compte c, String appareil) {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        if (appareil != null && appareil.length() > 255) appareil = appareil.substring(0, 255);
        apiTokenDAO.save(new ApiToken(c, empreinte(token), LocalDateTime.now().plus(VALIDITE), appareil));
        return token;
    }

    /** Compte associé à un jeton valide. */
    public Optional<Compte> verifier(String token) {
        if (token == null || token.isBlank()) return Optional.empty();
        return apiTokenDAO.findByHash(empreinte(token))
                .filter(t -> !t.isExpired())
                .map(ApiToken::getCompte);
    }

    public void revoquer(String token) {
        if (token != null && !token.isBlank()) apiTokenDAO.deleteByHash(empreinte(token));
    }

    public void revoquerTout(Integer compteId) {
        apiTokenDAO.deleteByCompte(compteId);
    }

    private static String empreinte(String token) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
