package sn.association.cotisations.servlet;

import jakarta.mail.MessagingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sn.association.cotisations.dao.MembreDAO;
import sn.association.cotisations.entity.Membre;
import sn.association.cotisations.entity.StatutMembre;
import sn.association.cotisations.service.MailService;
import sn.association.cotisations.util.FlashUtil;
import sn.association.cotisations.util.PasswordUtil;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Optional;

@WebServlet(name = "ForgotPasswordServlet", urlPatterns = {"/forgot-password"})
public class ForgotPasswordServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(ForgotPasswordServlet.class);

    private static final String ALPHABET =
            "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789";
    private static final int TEMP_PWD_LENGTH = 10;

    private final MembreDAO membreDAO = new MembreDAO();
    private final MailService mailService = new MailService();
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

        // Message unique : on ne révèle jamais si l'email existe (anti-énumération).
        String genericSuccess =
                "Si cet email correspond à un compte, un nouveau mot de passe vous a été envoyé.";

        Optional<Membre> opt = membreDAO.findByEmail(email);
        if (opt.isPresent() && opt.get().getStatut() == StatutMembre.ACTIF) {
            Membre m = opt.get();
            String tempPwd = generateTempPassword();
            m.setMotDePasse(PasswordUtil.hash(tempPwd));
            membreDAO.save(m);

            String body = "Bonjour " + m.getPrenom() + ",\n\n"
                    + "Votre mot de passe a été réinitialisé.\n"
                    + "Mot de passe temporaire : " + tempPwd + "\n\n"
                    + "Connectez-vous puis changez-le immédiatement depuis votre profil.\n\n"
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

    private String generateTempPassword() {
        StringBuilder sb = new StringBuilder(TEMP_PWD_LENGTH);
        for (int i = 0; i < TEMP_PWD_LENGTH; i++) {
            sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
}
