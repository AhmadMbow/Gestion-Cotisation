package sn.association.cotisations.servlet;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sn.association.cotisations.entity.Membre;
import sn.association.cotisations.entity.Role;
import sn.association.cotisations.service.MembreService;
import sn.association.cotisations.util.FlashUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;

/**
 * Routes:
 *   GET  /admin/membres                       → liste
 *   GET  /admin/membres/nouveau               → formulaire création
 *   POST /admin/membres/nouveau               → enregistrer création
 *   GET  /admin/membres/edit?id=X             → formulaire modification
 *   POST /admin/membres/edit?id=X             → enregistrer modification
 *   POST /admin/membres/delete?id=X           → suppression
 *   POST /admin/membres/toggle?id=X           → activer/désactiver
 */
@WebServlet(name = "MembresServlet", urlPatterns = {
        "/admin/membres",
        "/admin/membres/nouveau",
        "/admin/membres/edit",
        "/admin/membres/delete",
        "/admin/membres/toggle"
})
public class MembresServlet extends HttpServlet {

    @Inject MembreService membreService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getServletPath();
        switch (path) {
            case "/admin/membres":
                req.setAttribute("membres", membreService.listerTous());
                req.getRequestDispatcher("/WEB-INF/views/admin/membres-list.jsp").forward(req, resp);
                return;

            case "/admin/membres/nouveau":
                req.setAttribute("modeEdition", false);
                req.getRequestDispatcher("/WEB-INF/views/admin/membre-form.jsp").forward(req, resp);
                return;

            case "/admin/membres/edit":
                Integer id = parseId(req);
                if (id == null) { redirectListeAvecErreur(req, resp, "ID manquant."); return; }
                Optional<Membre> opt = membreService.trouver(id);
                if (opt.isEmpty()) { redirectListeAvecErreur(req, resp, "Membre introuvable."); return; }
                req.setAttribute("membre", opt.get());
                req.setAttribute("modeEdition", true);
                req.getRequestDispatcher("/WEB-INF/views/admin/membre-form.jsp").forward(req, resp);
                return;

            default:
                resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getServletPath();
        switch (path) {
            case "/admin/membres/nouveau":  creer(req, resp);   return;
            case "/admin/membres/edit":     modifier(req, resp); return;
            case "/admin/membres/delete":   supprimer(req, resp); return;
            case "/admin/membres/toggle":   basculerStatut(req, resp); return;
            default: resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }

    private void creer(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        try {
            Membre m = membreService.creer(
                    req.getParameter("prenom"),
                    req.getParameter("nom"),
                    req.getParameter("email"),
                    parseDate(req.getParameter("dateNaissance")),
                    parseRole(req.getParameter("role")),
                    req.getParameter("motDePasse"));
            FlashUtil.success(req, "Membre " + m.getPrenom() + " " + m.getNom() + " ajouté avec succès.");
            resp.sendRedirect(req.getContextPath() + "/admin/membres");
        } catch (IllegalArgumentException e) {
            req.setAttribute("erreur", e.getMessage());
            req.setAttribute("modeEdition", false);
            // Repopulate form values
            req.setAttribute("formPrenom", req.getParameter("prenom"));
            req.setAttribute("formNom", req.getParameter("nom"));
            req.setAttribute("formEmail", req.getParameter("email"));
            req.setAttribute("formDateNaissance", req.getParameter("dateNaissance"));
            req.setAttribute("formRole", req.getParameter("role"));
            req.getRequestDispatcher("/WEB-INF/views/admin/membre-form.jsp").forward(req, resp);
        }
    }

    private void modifier(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        Integer id = parseId(req);
        if (id == null) { redirectListeAvecErreur(req, resp, "ID manquant."); return; }
        try {
            Membre m = membreService.modifier(
                    id,
                    req.getParameter("prenom"),
                    req.getParameter("nom"),
                    req.getParameter("email"),
                    parseDate(req.getParameter("dateNaissance")),
                    parseRole(req.getParameter("role")),
                    req.getParameter("motDePasse"));
            FlashUtil.success(req, "Membre " + m.getPrenom() + " " + m.getNom() + " modifié avec succès.");
            resp.sendRedirect(req.getContextPath() + "/admin/membres");
        } catch (IllegalArgumentException e) {
            req.setAttribute("erreur", e.getMessage());
            req.setAttribute("modeEdition", true);
            Optional<Membre> opt = membreService.trouver(id);
            opt.ifPresent(m -> req.setAttribute("membre", m));
            req.getRequestDispatcher("/WEB-INF/views/admin/membre-form.jsp").forward(req, resp);
        }
    }

    private void supprimer(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer id = parseId(req);
        if (id == null) { redirectListeAvecErreur(req, resp, "ID manquant."); return; }
        try {
            membreService.supprimer(id);
            FlashUtil.success(req, "Membre supprimé.");
        } catch (RuntimeException e) {
            FlashUtil.error(req, "Suppression impossible : " + e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/admin/membres");
    }

    private void basculerStatut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer id = parseId(req);
        if (id == null) { redirectListeAvecErreur(req, resp, "ID manquant."); return; }
        try {
            Membre m = membreService.basculerStatut(id);
            FlashUtil.success(req, m.getPrenom() + " " + m.getNom() + " est maintenant " + m.getStatut() + ".");
        } catch (IllegalArgumentException e) {
            FlashUtil.error(req, e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/admin/membres");
    }

    private void redirectListeAvecErreur(HttpServletRequest req, HttpServletResponse resp, String msg) throws IOException {
        FlashUtil.error(req, msg);
        resp.sendRedirect(req.getContextPath() + "/admin/membres");
    }

    private Integer parseId(HttpServletRequest req) {
        try { return Integer.valueOf(req.getParameter("id")); }
        catch (Exception e) { return null; }
    }

    private LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        try { return LocalDate.parse(s); }
        catch (DateTimeParseException e) { return null; }
    }

    private Role parseRole(String s) {
        if (s == null || s.isBlank()) return Role.MEMBRE;
        try { return Role.valueOf(s); }
        catch (IllegalArgumentException e) { return Role.MEMBRE; }
    }
}
