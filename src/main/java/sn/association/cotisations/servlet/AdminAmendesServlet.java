package sn.association.cotisations.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sn.association.cotisations.entity.Amende;
import sn.association.cotisations.service.AmendeService;
import sn.association.cotisations.service.MembreService;
import sn.association.cotisations.util.FlashUtil;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Routes admin pour les amendes :
 *   GET  /admin/amendes                  → liste de toutes les amendes
 *   GET  /admin/amendes/nouveau          → formulaire amende manuelle
 *   POST /admin/amendes/nouveau          → enregistrement
 *   POST /admin/amendes/generer-auto     → génération automatique pour les retards du mois
 *   POST /admin/amendes/payer            → marquer une amende comme PAYEE
 *   POST /admin/amendes/delete           → suppression
 */
@WebServlet(name = "AdminAmendesServlet", urlPatterns = {
        "/admin/amendes",
        "/admin/amendes/nouveau",
        "/admin/amendes/generer-auto",
        "/admin/amendes/payer",
        "/admin/amendes/delete"
})
public class AdminAmendesServlet extends HttpServlet {

    private final AmendeService amendeService = new AmendeService();
    private final MembreService membreService = new MembreService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        switch (req.getServletPath()) {
            case "/admin/amendes":
                req.setAttribute("amendes", amendeService.listerTout());
                req.setAttribute("montantStandard", AmendeService.MONTANT_STANDARD);
                req.getRequestDispatcher("/WEB-INF/views/admin/amendes-list.jsp").forward(req, resp);
                return;

            case "/admin/amendes/nouveau":
                req.setAttribute("membres", membreService.listerTous());
                req.setAttribute("montantStandard", AmendeService.MONTANT_STANDARD);
                req.getRequestDispatcher("/WEB-INF/views/admin/amende-form.jsp").forward(req, resp);
                return;

            default:
                resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        switch (req.getServletPath()) {
            case "/admin/amendes/nouveau":       creer(req, resp);          return;
            case "/admin/amendes/generer-auto":  genererAuto(req, resp);    return;
            case "/admin/amendes/payer":         marquerPayee(req, resp);   return;
            case "/admin/amendes/delete":        supprimer(req, resp);      return;
            default: resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }

    private void creer(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        try {
            String membreParam = req.getParameter("membre");
            String montantParam = req.getParameter("montant");
            if (membreParam == null || montantParam == null) {
                throw new IllegalArgumentException("Tous les champs sont obligatoires.");
            }
            Integer membreNumero = Integer.valueOf(membreParam);
            BigDecimal montant = new BigDecimal(montantParam);

            Amende a = amendeService.generer(membreNumero, montant);
            FlashUtil.success(req, "Amende de " + montant + " FCFA générée pour "
                    + a.getMembre().getPrenom() + " " + a.getMembre().getNom() + ".");
            resp.sendRedirect(req.getContextPath() + "/admin/amendes");

        } catch (IllegalArgumentException e) {
            req.setAttribute("erreur", e.getMessage());
            req.setAttribute("membres", membreService.listerTous());
            req.setAttribute("montantStandard", AmendeService.MONTANT_STANDARD);
            req.getRequestDispatcher("/WEB-INF/views/admin/amende-form.jsp").forward(req, resp);
        }
    }

    private void genererAuto(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int created = amendeService.genererPourRetardsMoisCourant();
        if (created == 0) {
            FlashUtil.success(req, "Aucune amende à générer : tous les retardataires en ont déjà une ce mois (ou il n'y a pas de retard).");
        } else {
            FlashUtil.success(req, created + " amende(s) générée(s) automatiquement pour les retards du mois.");
        }
        resp.sendRedirect(req.getContextPath() + "/admin/amendes");
    }

    private void marquerPayee(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Integer id = Integer.valueOf(req.getParameter("id"));
            Amende a = amendeService.marquerPayee(id);
            FlashUtil.success(req, "Amende N°" + a.getId() + " marquée comme payée.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            FlashUtil.error(req, e.getMessage());
        } catch (NullPointerException e) {
            FlashUtil.error(req, "ID amende manquant.");
        }
        resp.sendRedirect(req.getContextPath() + "/admin/amendes");
    }

    private void supprimer(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Integer id = Integer.valueOf(req.getParameter("id"));
            amendeService.supprimer(id);
            FlashUtil.success(req, "Amende N°" + id + " supprimée.");
        } catch (IllegalArgumentException | NullPointerException e) {
            FlashUtil.error(req, "Suppression impossible.");
        }
        resp.sendRedirect(req.getContextPath() + "/admin/amendes");
    }
}
