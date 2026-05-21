package sn.association.cotisations.servlet;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import sn.association.cotisations.dao.MembreDAO;
import sn.association.cotisations.entity.Membre;
import sn.association.cotisations.entity.Role;
import sn.association.cotisations.util.FlashUtil;
import sn.association.cotisations.util.PasswordUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;

/**
 * Page profil : consultation + édition des informations personnelles
 * (prénom, nom, email, date de naissance) et changement de mot de passe.
 */
@WebServlet(name = "ProfilServlet", urlPatterns = {"/profil"})
public class ProfilServlet extends HttpServlet {

    @Inject MembreDAO membreDAO;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Membre m = sessionMembre(req);
        // Recharge depuis la base pour avoir les dernières valeurs
        membreDAO.findById(m.getNumero()).ifPresent(fresh -> req.setAttribute("membre", fresh));
        req.setAttribute("activeMenu", "profil");
        req.getRequestDispatcher("/WEB-INF/views/common/profil.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Membre user = sessionMembre(req);
        String action = req.getParameter("action");

        if ("infos".equals(action)) {
            updateInfos(req, user);
        } else if ("password".equals(action)) {
            updatePassword(req, user);
        } else {
            FlashUtil.error(req, "Action inconnue.");
        }

        resp.sendRedirect(req.getContextPath() + "/profil");
    }

    // ---------------------------------------------------------------
    private void updateInfos(HttpServletRequest req, Membre sessionUser) {
        Membre m = membreDAO.findById(sessionUser.getNumero())
                .orElseThrow(() -> new IllegalStateException("Membre introuvable"));

        String prenom = trim(req.getParameter("prenom"));
        String nom = trim(req.getParameter("nom"));
        String email = trim(req.getParameter("email"));
        String dateNaisStr = trim(req.getParameter("dateNaissance"));

        if (prenom == null || nom == null || email == null) {
            FlashUtil.error(req, "Prénom, nom et email sont obligatoires.");
            return;
        }

        email = email.toLowerCase();
        if (!email.equals(m.getEmail())) {
            // Unicité de l'email
            Optional<Membre> autre = membreDAO.findByEmail(email);
            if (autre.isPresent() && !autre.get().getNumero().equals(m.getNumero())) {
                FlashUtil.error(req, "Cet email est déjà utilisé par un autre membre.");
                return;
            }
        }

        LocalDate dateNaissance = null;
        if (dateNaisStr != null) {
            try { dateNaissance = LocalDate.parse(dateNaisStr); }
            catch (DateTimeParseException e) {
                FlashUtil.error(req, "Date de naissance invalide.");
                return;
            }
        }

        m.setPrenom(prenom);
        m.setNom(nom);
        m.setEmail(email);
        m.setDateNaissance(dateNaissance);
        Membre updated = membreDAO.save(m);

        // Rafraîchir la session pour que le sidebar affiche les nouveaux nom/prénom
        req.getSession().setAttribute("user", updated);
        FlashUtil.success(req, "Vos informations ont été mises à jour.");
    }

    // ---------------------------------------------------------------
    private void updatePassword(HttpServletRequest req, Membre sessionUser) {
        Membre m = membreDAO.findById(sessionUser.getNumero())
                .orElseThrow(() -> new IllegalStateException("Membre introuvable"));

        String current = req.getParameter("currentPassword");
        String next = req.getParameter("newPassword");
        String confirm = req.getParameter("confirmPassword");

        if (current == null || next == null || confirm == null
                || current.isBlank() || next.isBlank()) {
            FlashUtil.error(req, "Veuillez remplir tous les champs de mot de passe.");
            return;
        }
        if (!PasswordUtil.matches(current, m.getMotDePasse())) {
            FlashUtil.error(req, "Le mot de passe actuel est incorrect.");
            return;
        }
        if (next.length() < 6) {
            FlashUtil.error(req, "Le nouveau mot de passe doit contenir au moins 6 caractères.");
            return;
        }
        if (!next.equals(confirm)) {
            FlashUtil.error(req, "La confirmation ne correspond pas au nouveau mot de passe.");
            return;
        }

        m.setMotDePasse(PasswordUtil.hash(next));
        Membre updated = membreDAO.save(m);
        req.getSession().setAttribute("user", updated);
        FlashUtil.success(req, "Mot de passe modifié avec succès.");
    }

    // ---------------------------------------------------------------
    private Membre sessionMembre(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) throw new IllegalStateException("Session expirée");
        Membre m = (Membre) session.getAttribute("user");
        if (m == null) throw new IllegalStateException("Utilisateur non connecté");
        return m;
    }

    private static String trim(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    @SuppressWarnings("unused")
    private static boolean isAdmin(Membre m) {
        return m != null && m.getRole() == Role.ADMIN;
    }
}
