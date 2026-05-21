package sn.association.cotisations.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sn.association.cotisations.service.RappelService;
import sn.association.cotisations.util.FlashUtil;

import java.io.IOException;

/**
 * POST /admin/rappels → déclenche l'envoi des rappels aux membres en retard
 * pour la cotisation du mois courant. Redirige vers la page des retards.
 */
@WebServlet(name = "AdminRappelsServlet", urlPatterns = {"/admin/rappels"})
public class AdminRappelsServlet extends HttpServlet {

    private final RappelService rappelService = new RappelService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        RappelService.Resultat r = rappelService.envoyerRappelsMoisCourant();

        if (r.totalEnRetard == 0) {
            FlashUtil.success(req, "Aucun membre en retard ce mois-ci, aucun rappel à envoyer.");
        } else if (r.echecs == 0) {
            FlashUtil.success(req,
                    "Rappels envoyés à " + r.envoyes + " membre(s) en retard.");
        } else {
            FlashUtil.error(req, "Rappels partiels : " + r.envoyes + " envoyé(s), "
                    + r.echecs + " échec(s) sur " + r.totalEnRetard + ".");
        }

        resp.sendRedirect(req.getContextPath() + "/admin/cotisations/retards");
    }
}
