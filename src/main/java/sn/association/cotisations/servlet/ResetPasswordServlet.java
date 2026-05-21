package sn.association.cotisations.servlet;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sn.association.cotisations.dao.MembreDAO;
import sn.association.cotisations.dao.PasswordResetTokenDAO;
import sn.association.cotisations.entity.Membre;
import sn.association.cotisations.entity.PasswordResetToken;
import sn.association.cotisations.util.FlashUtil;
import sn.association.cotisations.util.PasswordUtil;

import java.io.IOException;
import java.util.Optional;

/**
 * GET  /reset-password?token=...  → formulaire pour saisir le nouveau mot de passe
 * POST /reset-password            → applique le nouveau mot de passe + marque le token utilisé
 */
@WebServlet(name = "ResetPasswordServlet", urlPatterns = {"/reset-password"})
public class ResetPasswordServlet extends HttpServlet {

    @Inject PasswordResetTokenDAO tokenDAO;
    @Inject MembreDAO membreDAO;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String token = req.getParameter("token");
        if (!isTokenValid(token)) {
            req.setAttribute("invalidToken", true);
        } else {
            req.setAttribute("token", token);
        }
        req.getRequestDispatcher("/WEB-INF/views/auth/reset-password.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String token = req.getParameter("token");
        String newPassword = req.getParameter("newPassword");
        String confirm = req.getParameter("confirmPassword");

        Optional<PasswordResetToken> opt =
                (token == null) ? Optional.empty() : tokenDAO.findByToken(token);

        if (opt.isEmpty() || !opt.get().isUsable()) {
            req.setAttribute("invalidToken", true);
            req.getRequestDispatcher("/WEB-INF/views/auth/reset-password.jsp").forward(req, resp);
            return;
        }

        if (newPassword == null || newPassword.length() < 6) {
            req.setAttribute("error", "Le mot de passe doit faire au moins 6 caractères.");
            req.setAttribute("token", token);
            req.getRequestDispatcher("/WEB-INF/views/auth/reset-password.jsp").forward(req, resp);
            return;
        }
        if (!newPassword.equals(confirm)) {
            req.setAttribute("error", "La confirmation ne correspond pas.");
            req.setAttribute("token", token);
            req.getRequestDispatcher("/WEB-INF/views/auth/reset-password.jsp").forward(req, resp);
            return;
        }

        PasswordResetToken prt = opt.get();
        Membre m = prt.getMembre();
        m.setMotDePasse(PasswordUtil.hash(newPassword));
        membreDAO.save(m);
        tokenDAO.markUsed(prt.getId());

        FlashUtil.success(req, "Mot de passe modifié. Vous pouvez maintenant vous connecter.");
        resp.sendRedirect(req.getContextPath() + "/login");
    }

    private boolean isTokenValid(String token) {
        if (token == null || token.isBlank()) return false;
        return tokenDAO.findByToken(token).map(PasswordResetToken::isUsable).orElse(false);
    }
}
