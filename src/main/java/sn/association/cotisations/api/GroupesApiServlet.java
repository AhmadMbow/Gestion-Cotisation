package sn.association.cotisations.api;

import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sn.association.cotisations.dao.MembreDAO;
import sn.association.cotisations.entity.Membre;
import sn.association.cotisations.service.GroupeService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * POST /api/groupes             {nom, montantCotisation, montantAmende} → crée un groupe (selon le forfait)
 * POST /api/groupes/rejoindre   {code} → rejoint un groupe avec son code d'invitation
 *
 * Réponse : le groupe, vu par l'utilisateur.
 */
@WebServlet(name = "GroupesApiServlet", urlPatterns = {"/api/groupes", "/api/groupes/*"})
public class GroupesApiServlet extends ApiServlet {

    @Inject GroupeService groupeService;
    @Inject MembreDAO membreDAO;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        run(req, resp, (rq, rs) -> {
            List<String> p = chemin(rq);
            JsonObject b = corps(rq);
            Membre m;
            if (p.isEmpty()) {
                BigDecimal cotisation = montant(b, "montantCotisation");
                BigDecimal amende = montant(b, "montantAmende");
                m = groupeService.creer(compte(rq), texte(b, "nom"),
                        cotisation != null ? cotisation : new BigDecimal("5000"),
                        amende != null ? amende : new BigDecimal("2000"));
            } else if (p.size() == 1 && p.get(0).equals("rejoindre")) {
                m = groupeService.rejoindre(compte(rq), texte(b, "code"));
            } else {
                throw new ApiException(HttpServletResponse.SC_NOT_FOUND, "Ressource introuvable.");
            }
            return Dto.groupe(m, membreDAO.countActifs(m.getGroupe().getId()));
        });
    }
}
