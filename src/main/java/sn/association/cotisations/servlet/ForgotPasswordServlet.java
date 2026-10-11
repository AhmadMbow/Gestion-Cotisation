package sn.association.cotisations.servlet;

import jakarta.inject.Inject;
import jakarta.mail.MessagingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sn.association.cotisations.service.PasswordResetService;
import sn.association.cotisations.util.FlashUtil;

import java.io.IOException;

@WebServlet(name = "ForgotPasswordServlet", urlPatterns = {"/forgot-password"})
public class ForgotPasswordServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(ForgotPasswordServlet.class);

    @Inject PasswordResetService passwordResetService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/auth/forgot-password.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String email = req.getParameter("email");
        if (email == null || email.isBlank()) {
            req.setAttribute("error", "Veuillez saisir votre email.");
            doGet(req, resp);
            return;
        }

        try {
            passwordResetService.demander(email, PasswordResetService.baseUrl(req));
        } catch (MessagingException e) {
            log.error("Échec d'envoi du mail de réinitialisation à {}", email, e);
            req.setAttribute("error",
                    "Impossible d'envoyer l'email pour le moment. Contactez l'administrateur.");
            doGet(req, resp);
            return;
        }

        // Message unique anti-énumération
        FlashUtil.success(req,
                "Si cet email correspond à un compte, un lien de réinitialisation vous a été envoyé.");
        resp.sendRedirect(req.getContextPath() + "/login");
    }
}
