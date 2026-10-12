package sn.association.cotisations.api;

import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sn.association.cotisations.entity.Forfait;
import sn.association.cotisations.entity.PaiementAbonnement;
import sn.association.cotisations.service.AbonnementService;

import java.io.IOException;

/**
 * POST /api/abonnement   {forfait, mode} → paie un mois du forfait (paiement simulé)
 *                        → le reçu du paiement
 */
@WebServlet(name = "AbonnementApiServlet", urlPatterns = {"/api/abonnement"})
public class AbonnementApiServlet extends ApiServlet {

    @Inject AbonnementService abonnementService;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        run(req, resp, (rq, rs) -> {
            JsonObject b = corps(rq);
            String code = texte(b, "forfait");
            Forfait forfait;
            try {
                forfait = code == null ? null : Forfait.valueOf(code);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Forfait inconnu : " + code);
            }
            PaiementAbonnement p = abonnementService.souscrire(compte(rq), forfait, mode(b));
            return Dto.of(p);
        });
    }
}
