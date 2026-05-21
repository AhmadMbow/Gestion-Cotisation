package sn.association.cotisations.servlet;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sn.association.cotisations.entity.Cotisation;
import sn.association.cotisations.entity.Membre;
import sn.association.cotisations.entity.ModePaiement;
import sn.association.cotisations.service.CotisationService;
import sn.association.cotisations.service.MoisDu;
import sn.association.cotisations.util.FlashUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Routes membre :
 *   GET  /membre/cotisations          → mon historique de cotisations
 *   GET  /membre/payer                → formulaire de paiement
 *   POST /membre/payer                → soumission paiement → redirige sur reçu
 */
@WebServlet(name = "MembreCotisationsServlet", urlPatterns = {
        "/membre/cotisations",
        "/membre/payer"
})
public class MembreCotisationsServlet extends HttpServlet {

    @Inject CotisationService cotisationService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Membre user = (Membre) req.getSession().getAttribute("user");

        List<MoisDu> moisDus = cotisationService.moisDusParMembre(user);

        switch (req.getServletPath()) {
            case "/membre/cotisations":
                List<Cotisation> hist = cotisationService.historiqueMembre(user.getNumero());
                req.setAttribute("cotisations", hist);
                req.setAttribute("moisDus", moisDus);
                req.setAttribute("montantDu", cotisationService.montantTotalDu(user));
                req.getRequestDispatcher("/WEB-INF/views/member/mes-cotisations.jsp").forward(req, resp);
                return;

            case "/membre/payer":
                LocalDate now = LocalDate.now();
                // Mois pré-rempli : le plus ancien dû s'il existe, sinon le mois courant
                int moisDefaut = !moisDus.isEmpty() ? moisDus.get(0).getMois() : now.getMonthValue();
                int anneeDefaut = !moisDus.isEmpty() ? moisDus.get(0).getAnnee() : now.getYear();

                req.setAttribute("modes", ModePaiement.values());
                req.setAttribute("moisCourant", now.getMonthValue());
                req.setAttribute("anneeCourante", now.getYear());
                req.setAttribute("moisDefaut", moisDefaut);
                req.setAttribute("anneeDefaut", anneeDefaut);
                req.setAttribute("moisDus", moisDus);
                req.setAttribute("montantParDefaut", CotisationService.MONTANT_PAR_DEFAUT);
                req.getRequestDispatcher("/WEB-INF/views/member/payer.jsp").forward(req, resp);
                return;

            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        if (!"/membre/payer".equals(req.getServletPath())) {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }

        Membre user = (Membre) req.getSession().getAttribute("user");

        try {
            String montantParam = req.getParameter("montant");
            String moisParam = req.getParameter("mois");
            String anneeParam = req.getParameter("annee");
            String modeParam = req.getParameter("modePaiement");
            if (montantParam == null || moisParam == null || anneeParam == null || modeParam == null) {
                throw new IllegalArgumentException("Tous les champs sont obligatoires.");
            }
            BigDecimal montant = new BigDecimal(montantParam);
            int mois = Integer.parseInt(moisParam);
            int annee = Integer.parseInt(anneeParam);
            ModePaiement mode = ModePaiement.valueOf(modeParam);

            Cotisation c = cotisationService.enregistrer(user.getNumero(), montant, mois, annee, mode);
            FlashUtil.success(req, "Paiement enregistré. Voici votre reçu.");
            resp.sendRedirect(req.getContextPath() + "/membre/recu?id=" + c.getId());

        } catch (IllegalArgumentException | IllegalStateException e) {
            req.setAttribute("erreur", e.getMessage());
            LocalDate now = LocalDate.now();
            List<MoisDu> moisDus = cotisationService.moisDusParMembre(user);
            req.setAttribute("modes", ModePaiement.values());
            req.setAttribute("moisCourant", now.getMonthValue());
            req.setAttribute("anneeCourante", now.getYear());
            req.setAttribute("moisDefaut", now.getMonthValue());
            req.setAttribute("anneeDefaut", now.getYear());
            req.setAttribute("moisDus", moisDus);
            req.setAttribute("montantParDefaut", CotisationService.MONTANT_PAR_DEFAUT);
            req.getRequestDispatcher("/WEB-INF/views/member/payer.jsp").forward(req, resp);
        }
    }
}
