package sn.association.cotisations.api;

import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sn.association.cotisations.dao.AmendeDAO;
import sn.association.cotisations.dao.ConnexionDAO;
import sn.association.cotisations.dao.CotisationDAO;
import sn.association.cotisations.dao.MembreDAO;
import sn.association.cotisations.dao.NotificationDAO;
import sn.association.cotisations.entity.Membre;
import sn.association.cotisations.entity.Role;
import sn.association.cotisations.service.PaiementService;
import sn.association.cotisations.service.ParametreService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * GET  /api/donnees                  → toutes les données visibles par l'utilisateur
 *                                      (tout pour un admin, ses propres données pour un membre)
 * POST /api/paiements                {periodes, amendes, mode} → paiement du membre connecté
 * POST /api/notifications/lues
 * GET  /api/sante                    → {"ok": true} (sans authentification)
 */
@WebServlet(name = "CompteApiServlet",
        urlPatterns = {"/api/donnees", "/api/paiements", "/api/notifications/lues", "/api/sante"})
public class CompteApiServlet extends ApiServlet {

    @Inject MembreDAO membreDAO;
    @Inject CotisationDAO cotisationDAO;
    @Inject AmendeDAO amendeDAO;
    @Inject NotificationDAO notificationDAO;
    @Inject ConnexionDAO connexionDAO;
    @Inject ParametreService parametreService;
    @Inject PaiementService paiementService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        run(req, resp, (rq, rs) -> switch (rq.getServletPath()) {
            case "/api/sante" -> Map.of("ok", true);
            case "/api/donnees" -> donnees(utilisateur(rq));
            default -> throw new ApiException(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Méthode non autorisée.");
        });
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        run(req, resp, (rq, rs) -> {
            Membre u = utilisateur(rq);
            switch (rq.getServletPath()) {
                case "/api/paiements": {
                    JsonObject body = corps(rq);
                    PaiementService.Resultat r = paiementService.payer(
                            u, periodes(body), identifiants(body, "amendes"), mode(body), false);
                    return resultat(r);
                }
                case "/api/notifications/lues":
                    notificationDAO.marquerLues(u.getRole() == Role.ADMIN ? null : u.getNumero());
                    return null;
                default:
                    throw new ApiException(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Méthode non autorisée.");
            }
        });
    }

    Dto.Donnees donnees(Membre u) {
        Dto.Donnees d = new Dto.Donnees();
        d.utilisateur = Dto.of(u);
        d.parametres = new Dto.ParametresDto();
        d.parametres.montantCotisation = parametreService.montantCotisation().longValue();
        d.parametres.montantAmende = parametreService.montantAmende().longValue();
        if (u.getRole() == Role.ADMIN) {
            d.membres = membreDAO.findAll().stream().map(Dto::of).toList();
            d.cotisations = cotisationDAO.findAll().stream().map(Dto::of).toList();
            d.amendes = amendeDAO.findAll().stream().map(Dto::of).toList();
            d.notifications = notificationDAO.findPourAdmins().stream().map(Dto::of).toList();
        } else {
            d.membres = List.of(d.utilisateur);
            d.cotisations = cotisationDAO.findByMembre(u.getNumero()).stream().map(Dto::of).toList();
            d.amendes = amendeDAO.findByMembre(u.getNumero()).stream().map(Dto::of).toList();
            d.notifications = notificationDAO.findByMembre(u.getNumero()).stream().map(Dto::of).toList();
        }
        d.connexions = connexionDAO.findByMembre(u.getNumero(), 15).stream().map(Dto::of).toList();
        return d;
    }

    static Dto.PaiementResultat resultat(PaiementService.Resultat r) {
        Dto.PaiementResultat res = new Dto.PaiementResultat();
        res.cotisations = r.cotisations().stream().map(Dto::of).toList();
        res.amendes = r.amendes().stream().map(Dto::of).toList();
        return res;
    }
}
