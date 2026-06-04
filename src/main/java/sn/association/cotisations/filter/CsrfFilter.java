package sn.association.cotisations.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sn.association.cotisations.util.CsrfUtil;

import java.io.IOException;

/**
 * - Sur chaque requête : s'assure qu'un token CSRF existe en session et
 *   l'expose comme attribut "csrfToken" pour les JSP.
 * - Sur les requêtes POST : rejette (403) si le paramètre _csrf est absent
 *   ou ne correspond pas au token de la session.
 */
@WebFilter(filterName = "CsrfFilter", urlPatterns = {"/*"})
public class CsrfFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // Toujours fournir un token aux vues (création de la session si besoin)
        String token = CsrfUtil.tokenFor(req);
        req.setAttribute("csrfToken", token);

        if ("POST".equalsIgnoreCase(req.getMethod()) && !CsrfUtil.isValid(req)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Jeton CSRF invalide ou expiré. Rechargez la page et réessayez.");
            return;
        }

        chain.doFilter(request, response);
    }
}
