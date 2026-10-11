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
import sn.association.cotisations.entity.Membre;
import sn.association.cotisations.entity.Role;
import sn.association.cotisations.service.ApiTokenService;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

/**
 * Authentifie les appels /api/* par l'en-tête « Authorization: Bearer &lt;jeton&gt; ».
 * /api/admin/* est réservé aux administrateurs.
 */
@WebFilter(filterName = "ApiAuthFilter", urlPatterns = {"/api/*"})
public class ApiAuthFilter implements Filter {

    /** Routes accessibles sans être connecté. */
    private static final Set<String> PUBLIQUES = Set.of(
            "/api/auth/login",
            "/api/auth/mot-de-passe-oublie",
            "/api/sante");

    @Inject ApiTokenService apiTokenService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String path = req.getRequestURI().substring(req.getContextPath().length());

        if (PUBLIQUES.contains(path)) {
            chain.doFilter(request, response);
            return;
        }

        String header = req.getHeader("Authorization");
        String jeton = header != null && header.startsWith("Bearer ") ? header.substring(7).trim() : null;
        Optional<Membre> membre = apiTokenService.verifier(jeton);
        if (membre.isEmpty()) {
            ApiServlet.erreur(resp, HttpServletResponse.SC_UNAUTHORIZED,
                    "Session expirée. Reconnectez-vous.");
            return;
        }
        if (path.startsWith("/api/admin/") && membre.get().getRole() != Role.ADMIN) {
            ApiServlet.erreur(resp, HttpServletResponse.SC_FORBIDDEN, "Accès réservé aux administrateurs.");
            return;
        }
        req.setAttribute(ApiServlet.UTILISATEUR, membre.get());
        req.setAttribute(ApiServlet.JETON, jeton);
        chain.doFilter(request, response);
    }
}
