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
import sn.association.cotisations.dao.MembreDAO;
import sn.association.cotisations.dao.PasswordResetTokenDAO;
import sn.association.cotisations.entity.Membre;
import sn.association.cotisations.entity.PasswordResetToken;
import sn.association.cotisations.entity.StatutMembre;
import sn.association.cotisations.service.MailService;
import sn.association.cotisations.util.FlashUtil;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@WebServlet(name = "ForgotPasswordServlet", urlPatterns = {"/forgot-password"})
public class ForgotPasswordServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(ForgotPasswordServlet.class);

    /** Durée de validité d'un lien de réinitialisation (30 minutes). */
    private static final int TOKEN_TTL_MINUTES = 30;

    @Inject MembreDAO membreDAO;
    @Inject PasswordResetTokenDAO tokenDAO;
    @Inject MailService mailService;
    private final SecureRandom random = new SecureRandom();

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
        email = email.trim().toLowerCase();

        // Message unique anti-énumération
        String genericSuccess =
                "Si cet email correspond à un compte, un lien de réinitialisation vous a été envoyé.";

        Optional<Membre> opt = membreDAO.findByEmail(email);
        if (opt.isPresent() && opt.get().getStatut() == StatutMembre.ACTIF) {
            Membre m = opt.get();
            String token = generateToken();
            tokenDAO.save(new PasswordResetToken(
                    m, token, LocalDateTime.now().plusMinutes(TOKEN_TTL_MINUTES)));

            String link = baseUrl(req) + req.getContextPath() + "/reset-password?token=" + token;
            String body = "Bonjour " + m.getPrenom() + ",\n\n"
                    + "Vous avez demandé la réinitialisation de votre mot de passe.\n"
                    + "Cliquez sur le lien ci-dessous (valable " + TOKEN_TTL_MINUTES + " minutes) :\n\n"
                    + link + "\n\n"
                    + "Si vous n'êtes pas à l'origine de cette demande, ignorez ce message.\n\n"
                    + "-- Gestion Cotisations";
            try {
                mailService.send(m.getEmail(), "Réinitialisation de votre mot de passe", body);
            } catch (MessagingException e) {
                log.error("Échec d'envoi du mail de réinitialisation à {}", m.getEmail(), e);
                req.setAttribute("error",
                        "Impossible d'envoyer l'email pour le moment. Contactez l'administrateur.");
                doGet(req, resp);
                return;
            }
        } else {
            log.info("Tentative de reset pour email inconnu ou inactif : {}", email);
        }

        FlashUtil.success(req, genericSuccess);
        resp.sendRedirect(req.getContextPath() + "/login");
    }

    private String generateToken() {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /** Reconstruit l'URL de base (scheme + host + port). */
    private static String baseUrl(HttpServletRequest req) {
        StringBuilder url = new StringBuilder();
        url.append(req.getScheme()).append("://").append(req.getServerName());
        int port = req.getServerPort();
        if ((req.getScheme().equals("http") && port != 80)
                || (req.getScheme().equals("https") && port != 443)) {
            url.append(':').append(port);
        }
        return url.toString();
    }
}
