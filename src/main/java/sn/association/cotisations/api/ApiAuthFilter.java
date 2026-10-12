package sn.association.cotisations.api;

import jakarta.inject.Inject;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sn.association.cotisations.dao.MembreDAO;
import sn.association.cotisations.entity.Compte;
import sn.association.cotisations.entity.Membre;
import sn.association.cotisations.entity.Role;
import sn.association.cotisations.entity.StatutMembre;
import sn.association.cotisations.service.ApiTokenService;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Authentifie les appels /api/* par l'en-tête « Authorization: Bearer &lt;jeton&gt; » (→ compte),
 * puis résout le groupe demandé dans l'en-tête « X-Groupe: &lt;id&gt; » (→ adhésion du compte).
 *
 * Les routes d'un groupe (paiements, notifications, /api/admin/*) exigent une adhésion active ;
 * /api/admin/* exige en plus le rôle d'administrateur dans ce groupe.
 *
 * CORS : la version web de l'application peut appeler l'API depuis une autre origine.
 * Origines autorisées : variable CORS_ORIGINS (liste séparée par des virgules), sinon
 * http://localhost et http://127.0.0.1 sur n'importe quel port (développement).
 * L'API utilise des jetons « Bearer », jamais de cookies : rien n'est envoyé automatiquement.
 */
@WebFilter(filterName = "ApiAuthFilter", urlPatterns = {"/api/*"})
public class ApiAuthFilter implements Filter {

    /** Routes accessibles sans être connecté. */
    private static final Set<String> PUBLIQUES = Set.of(
            "/api/auth/login",
            "/api/auth/inscription",
            "/api/auth/mot-de-passe-oublie",
            "/api/sante");

    /** Routes qui portent sur un groupe. */
    private static final Set<String> DE_GROUPE = Set.of("/api/paiements", "/api/notifications/lues");

    private static final List<String> ORIGINES = origines();

    @Inject ApiTokenService apiTokenService;
    @Inject MembreDAO membreDAO;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String path = req.getRequestURI().substring(req.getContextPath().length());

        if (cors(req, resp) && "OPTIONS".equals(req.getMethod())) {
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT); // pré-vérification du navigateur
            return;
        }
        if (PUBLIQUES.contains(path)) {
            chain.doFilter(request, response);
            return;
        }

        String header = req.getHeader("Authorization");
        String jeton = header != null && header.startsWith("Bearer ") ? header.substring(7).trim() : null;
        Optional<Compte> compte = apiTokenService.verifier(jeton);
        if (compte.isEmpty()) {
            ApiServlet.erreur(resp, HttpServletResponse.SC_UNAUTHORIZED, "Session expirée. Reconnectez-vous.");
            return;
        }
        req.setAttribute(ApiServlet.COMPTE, compte.get());
        req.setAttribute(ApiServlet.JETON, jeton);

        Optional<Membre> membre = adhesion(compte.get(), req.getHeader("X-Groupe"));
        membre.ifPresent(m -> req.setAttribute(ApiServlet.UTILISATEUR, m));

        boolean admin = path.startsWith("/api/admin/");
        if (admin || DE_GROUPE.contains(path)) {
            if (membre.isEmpty()) {
                ApiServlet.erreur(resp, HttpServletResponse.SC_FORBIDDEN, "Vous ne faites pas partie de ce groupe.");
                return;
            }
            if (membre.get().getStatut() != StatutMembre.ACTIF) {
                ApiServlet.erreur(resp, HttpServletResponse.SC_FORBIDDEN,
                        "Votre adhésion à ce groupe a été désactivée.");
                return;
            }
            if (admin && membre.get().getRole() != Role.ADMIN) {
                ApiServlet.erreur(resp, HttpServletResponse.SC_FORBIDDEN, "Accès réservé aux administrateurs.");
                return;
            }
        }
        chain.doFilter(request, response);
    }

    /** En-têtes CORS si l'origine est autorisée. */
    private static boolean cors(HttpServletRequest req, HttpServletResponse resp) {
        String origine = req.getHeader("Origin");
        if (origine == null || !autorisee(origine)) return false;
        resp.setHeader("Access-Control-Allow-Origin", origine);
        resp.setHeader("Vary", "Origin");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, X-Groupe");
        resp.setHeader("Access-Control-Max-Age", "600");
        return true;
    }

    static boolean autorisee(String origine) {
        if (ORIGINES.isEmpty()) return origine.matches("http://(localhost|127\\.0\\.0\\.1)(:\\d+)?");
        return ORIGINES.contains(origine);
    }

    private static List<String> origines() {
        String v = System.getenv("CORS_ORIGINS");
        if (v == null || v.isBlank()) return List.of();
        return Arrays.stream(v.split(",")).map(String::trim).filter(o -> !o.isEmpty()).toList();
    }

    private Optional<Membre> adhesion(Compte c, String groupe) {
        if (groupe == null || groupe.isBlank()) return Optional.empty();
        try {
            return membreDAO.findByCompteEtGroupe(c.getId(), Integer.parseInt(groupe.trim()))
                    .filter(m -> m.getGroupe().isActif());
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
