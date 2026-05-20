package sn.association.cotisations.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import sn.association.cotisations.entity.Membre;
import sn.association.cotisations.entity.Role;

import java.io.IOException;

/**
 * Protège les URLs /admin/* et /membre/*.
 * - Non connecté → redirige vers /login
 * - Connecté mais mauvais rôle → 403
 */
@WebFilter(filterName = "AuthFilter", urlPatterns = {"/admin/*", "/membre/*", "/profil"})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        Membre user = (session != null) ? (Membre) session.getAttribute("user") : null;

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String path = req.getRequestURI().substring(req.getContextPath().length());

        if (path.startsWith("/admin") && user.getRole() != Role.ADMIN) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès réservé aux administrateurs.");
            return;
        }

        chain.doFilter(request, response);
    }
}
