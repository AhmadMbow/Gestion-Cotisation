package sn.association.cotisations.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import sn.association.cotisations.entity.Membre;
import sn.association.cotisations.entity.Role;
import sn.association.cotisations.service.AuthService;

import java.io.IOException;
import java.util.Optional;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

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
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        Optional<Membre> opt = authService.authenticate(email, password);
        if (opt.isEmpty()) {
            req.setAttribute("error", "Email ou mot de passe incorrect.");
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
            return;
        }

        Membre m = opt.get();
        // Régénérer la session pour éviter le session fixation
        HttpSession old = req.getSession(false);
        if (old != null) old.invalidate();
        HttpSession session = req.getSession(true);
        session.setAttribute("user", m);
        session.setAttribute("role", m.getRole().name());
        session.setMaxInactiveInterval(30 * 60);

        redirectToDashboard(req, resp, m);
    }

    private void redirectToDashboard(HttpServletRequest req, HttpServletResponse resp, Membre m)
            throws IOException {
        String target = m.getRole() == Role.ADMIN ? "/admin/dashboard" : "/membre/dashboard";
        resp.sendRedirect(req.getContextPath() + target);
    }
}
