package sn.association.cotisations.servlet;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sn.association.cotisations.dao.ConnexionDAO;
import sn.association.cotisations.entity.Connexion;
import sn.association.cotisations.entity.Membre;
import sn.association.cotisations.entity.Role;
import sn.association.cotisations.service.AuthService;
import sn.association.cotisations.util.LoginRateLimiter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(LoginServlet.class);

    @Inject AuthService authService;
    @Inject ConnexionDAO connexionDAO;
    @Inject LoginRateLimiter rateLimiter;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Si déjà connecté, on redirige vers le dashboard adapté
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            Membre m = (Membre) session.getAttribute("user");
            redirectToDashboard(req, resp, m);
            return;
        }
        req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String ip = req.getRemoteAddr();
        if (rateLimiter.isBlocked(ip)) {
            long secs = rateLimiter.secondsUntilUnblock(ip);
            req.setAttribute("error",
                    "Trop de tentatives échouées. Réessayez dans " + Math.max(1, secs / 60) + " min.");
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
            return;
        }

        String email = req.getParameter("email");
        String password = req.getParameter("password");

        Optional<Membre> opt = authService.authenticate(email, password);
        if (opt.isEmpty()) {
            rateLimiter.recordFailure(ip);
            log.warn("Échec d'authentification pour {} depuis {}", email, ip);
            req.setAttribute("error", "Email ou mot de passe incorrect.");
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
            return;
        }

        Membre m = opt.get();
        rateLimiter.recordSuccess(ip);
        // Régénérer la session pour éviter le session fixation
        HttpSession old = req.getSession(false);
        if (old != null) old.invalidate();
        HttpSession session = req.getSession(true);
        session.setAttribute("user", m);
        session.setAttribute("role", m.getRole().name());
        session.setMaxInactiveInterval(30 * 60);

        logConnexion(req, m);
        redirectToDashboard(req, resp, m);
    }

    /**
     * Enregistre la connexion en BDD. Best-effort : on n'empêche jamais le login
     * si l'audit log plante (la BDD peut être en lecture seule, etc.).
     */
    private void logConnexion(HttpServletRequest req, Membre m) {
        try {
            String ua = req.getHeader("User-Agent");
            if (ua != null && ua.length() > 255) ua = ua.substring(0, 255);
            connexionDAO.save(new Connexion(m, LocalDateTime.now(), req.getRemoteAddr(), ua));
        } catch (RuntimeException e) {
            log.warn("Impossible d'enregistrer la connexion de {} : {}", m.getEmail(), e.getMessage());
        }
    }

    private void redirectToDashboard(HttpServletRequest req, HttpServletResponse resp, Membre m)
            throws IOException {
        String target = m.getRole() == Role.ADMIN ? "/admin/dashboard" : "/membre/dashboard";
        resp.sendRedirect(req.getContextPath() + target);
    }
}
