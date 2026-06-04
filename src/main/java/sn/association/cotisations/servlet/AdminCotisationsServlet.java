package sn.association.cotisations.servlet;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sn.association.cotisations.entity.Cotisation;
import sn.association.cotisations.entity.ModePaiement;
import sn.association.cotisations.service.CotisationService;
import sn.association.cotisations.service.MembreService;
import sn.association.cotisations.util.FlashUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Routes admin pour les cotisations :
 *   GET  /admin/cotisations            → liste de toutes les cotisations
 *   GET  /admin/cotisations/nouveau    → formulaire ajout (sélection membre + mois)
 *   POST /admin/cotisations/nouveau    → enregistrement
 *   GET  /admin/cotisations/retards    → membres en retard pour le mois courant
 */
@WebServlet(name = "AdminCotisationsServlet", urlPatterns = {
        "/admin/cotisations",
        "/admin/cotisations/nouveau",
        "/admin/cotisations/retards"
})
public class AdminCotisationsServlet extends HttpServlet {

    @Inject CotisationService cotisationService;
    @Inject MembreService membreService;
    @Inject sn.association.cotisations.service.ParametreService parametreService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        switch (req.getServletPath()) {
            case "/admin/cotisations":
                List<Cotisation> all = cotisationService.listerTout();
                req.setAttribute("cotisations", all);
                req.getRequestDispatcher("/WEB-INF/views/admin/cotisations-list.jsp").forward(req, resp);
                return;

            case "/admin/cotisations/nouveau":
                LocalDate now = LocalDate.now();
                req.setAttribute("membres", membreService.listerTous());
                req.setAttribute("modes", ModePaiement.values());
                req.setAttribute("moisCourant", now.getMonthValue());
                req.setAttribute("anneeCourante", now.getYear());
                req.setAttribute("montantParDefaut", parametreService.montantCotisation());
                req.getRequestDispatcher("/WEB-INF/views/admin/cotisation-form.jsp").forward(req, resp);
                return;

            case "/admin/cotisations/retards":
                java.util.List<sn.association.cotisations.entity.Membre> retards = cotisationService.membresEnRetardCeMois();
                // Map id → nombre de mois dus, pour affichage par ligne
                java.util.Map<Integer, Integer> nbMoisDus = new java.util.HashMap<>();
                java.util.Map<Integer, java.math.BigDecimal> montantDu = new java.util.HashMap<>();
                for (sn.association.cotisations.entity.Membre m : retards) {
                    nbMoisDus.put(m.getNumero(), cotisationService.moisDusParMembre(m).size());
                    montantDu.put(m.getNumero(), cotisationService.montantTotalDu(m));
                }
                req.setAttribute("membresEnRetard", retards);
                req.setAttribute("nbMoisDus", nbMoisDus);
                req.setAttribute("montantDu", montantDu);
                req.setAttribute("moisCourant", LocalDate.now().getMonthValue());
                req.setAttribute("anneeCourante", LocalDate.now().getYear());
                req.getRequestDispatcher("/WEB-INF/views/admin/cotisations-retards.jsp").forward(req, resp);
                return;

            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        if (!"/admin/cotisations/nouveau".equals(req.getServletPath())) {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }

        try {
            String membreParam = req.getParameter("membre");
            String montantParam = req.getParameter("montant");
            String moisParam = req.getParameter("mois");
            String anneeParam = req.getParameter("annee");
            String modeParam = req.getParameter("modePaiement");
            if (membreParam == null || montantParam == null || moisParam == null
                    || anneeParam == null || modeParam == null) {
                throw new IllegalArgumentException("Tous les champs sont obligatoires.");
            }
            Integer membreNumero = Integer.valueOf(membreParam);
            BigDecimal montant = new BigDecimal(montantParam);
            int mois = Integer.parseInt(moisParam);
            int annee = Integer.parseInt(anneeParam);
            ModePaiement mode = ModePaiement.valueOf(modeParam);

            Cotisation c = cotisationService.enregistrer(membreNumero, montant, mois, annee, mode);
            FlashUtil.success(req,
                    "Cotisation de " + c.getMembre().getPrenom() + " " + c.getMembre().getNom()
                    + " pour " + mois + "/" + annee + " enregistrée.");
            resp.sendRedirect(req.getContextPath() + "/admin/cotisations");

        } catch (IllegalArgumentException | IllegalStateException e) {
            // IllegalArgumentException couvre aussi NumberFormatException
            req.setAttribute("erreur", e.getMessage());
            req.setAttribute("membres", membreService.listerTous());
            req.setAttribute("modes", ModePaiement.values());
            req.setAttribute("moisCourant", LocalDate.now().getMonthValue());
            req.setAttribute("anneeCourante", LocalDate.now().getYear());
            req.setAttribute("montantParDefaut", parametreService.montantCotisation());
            req.getRequestDispatcher("/WEB-INF/views/admin/cotisation-form.jsp").forward(req, resp);
        }
    }
}
