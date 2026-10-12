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
import sn.association.cotisations.entity.Compte;
import sn.association.cotisations.entity.Forfait;
import sn.association.cotisations.entity.Membre;
import sn.association.cotisations.entity.Role;
import sn.association.cotisations.entity.StatutMembre;
import sn.association.cotisations.service.AbonnementService;
import sn.association.cotisations.service.CompteService;
import sn.association.cotisations.service.PaiementService;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * GET  /api/donnees                  → compte, forfait, groupes, et les données du groupe
 *                                      demandé dans l'en-tête X-Groupe (tout pour un admin,
 *                                      ses propres données pour un membre)
 * PUT  /api/compte                   {prenom, nom, email, telephone, dateNaissance} → son profil
 * POST /api/paiements                {periodes, amendes, mode} → paiement du membre connecté
 * POST /api/notifications/lues
 * GET  /api/sante                    → {"ok": true} (sans authentification)
 */
@WebServlet(name = "CompteApiServlet",
        urlPatterns = {"/api/donnees", "/api/compte", "/api/paiements", "/api/notifications/lues", "/api/sante"})
public class CompteApiServlet extends ApiServlet {

    @Inject MembreDAO membreDAO;
    @Inject CotisationDAO cotisationDAO;
    @Inject AmendeDAO amendeDAO;
    @Inject NotificationDAO notificationDAO;
    @Inject ConnexionDAO connexionDAO;
    @Inject PaiementService paiementService;
    @Inject CompteService compteService;
    @Inject AbonnementService abonnementService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        run(req, resp, (rq, rs) -> switch (rq.getServletPath()) {
            case "/api/sante" -> Map.of("ok", true);
            case "/api/donnees" -> donnees(compte(rq), utilisateur(rq));
            default -> throw new ApiException(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Méthode non autorisée.");
        });
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        run(req, resp, (rq, rs) -> {
            exiger(rq.getServletPath().equals("/api/compte"), HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                    "Méthode non autorisée.");
            JsonObject b = corps(rq);
            Compte c = compteService.modifierProfil(compte(rq).getId(), texte(b, "prenom"), texte(b, "nom"),
                    texte(b, "email"), texte(b, "telephone"), date(b, "dateNaissance"));
            return Dto.of(c);
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
                    if (u.getRole() == Role.ADMIN) {
                        notificationDAO.marquerLuesPourAdmins(u.getGroupe().getId());
                    } else {
                        notificationDAO.marquerLues(u.getNumero());
                    }
                    return null;
                default:
                    throw new ApiException(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Méthode non autorisée.");
            }
        });
    }

    Dto.Donnees donnees(Compte c, Membre u) {
        Dto.Donnees d = new Dto.Donnees();
        d.compte = Dto.of(c);
        d.abonnement = abonnement(c);
        d.forfaits = Arrays.stream(Forfait.values()).map(Dto::of).toList();
        d.groupes = membreDAO.findByCompte(c.getId()).stream()
                .map(m -> Dto.groupe(m, membreDAO.countActifs(m.getGroupe().getId())))
                .toList();
        d.connexions = connexionDAO.findByCompte(c.getId(), 15).stream().map(Dto::of).toList();

        if (u == null || u.getStatut() != StatutMembre.ACTIF) {
            d.membres = List.of();
            d.cotisations = List.of();
            d.amendes = List.of();
            d.notifications = List.of();
            return d;
        }
        int g = u.getGroupe().getId();
        d.groupe = Dto.groupe(u, membreDAO.countActifs(g));
        d.utilisateur = Dto.of(u);
        d.parametres = new Dto.ParametresDto();
        d.parametres.montantCotisation = u.getGroupe().getMontantCotisation().longValue();
        d.parametres.montantAmende = u.getGroupe().getMontantAmende().longValue();
        if (u.getRole() == Role.ADMIN) {
            d.membres = membreDAO.findByGroupe(g).stream().map(Dto::of).toList();
            d.cotisations = cotisationDAO.findByGroupe(g).stream().map(Dto::of).toList();
            d.amendes = amendeDAO.findByGroupe(g).stream().map(Dto::of).toList();
            d.notifications = notificationDAO.findPourAdmins(g).stream().map(Dto::of).toList();
        } else {
            // Les autres membres actifs, sans leurs coordonnées (avatars de l'accueil).
            d.membres = membreDAO.findByGroupe(g).stream()
                    .filter(m -> m.getStatut() == StatutMembre.ACTIF || m.equals(u))
                    .map(m -> m.equals(u) ? d.utilisateur : Dto.apercu(m))
                    .toList();
            d.cotisations = cotisationDAO.findByMembre(u.getNumero()).stream().map(Dto::of).toList();
            d.amendes = amendeDAO.findByMembre(u.getNumero()).stream().map(Dto::of).toList();
            d.notifications = notificationDAO.findByMembre(u.getNumero()).stream().map(Dto::of).toList();
        }
        return d;
    }

    Dto.AbonnementDto abonnement(Compte c) {
        Dto.AbonnementDto a = new Dto.AbonnementDto();
        Forfait f = c.forfaitEffectif();
        a.forfait = f.name();
        a.expireLe = f == Forfait.GRATUIT ? null : c.getForfaitExpireLe();
        a.groupesCrees = abonnementService.groupesCrees(c);
        a.paiements = abonnementService.historique(c).stream().map(Dto::of).toList();
        return a;
    }

    static Dto.PaiementResultat resultat(PaiementService.Resultat r) {
        Dto.PaiementResultat res = new Dto.PaiementResultat();
        res.cotisations = r.cotisations().stream().map(Dto::of).toList();
        res.amendes = r.amendes().stream().map(Dto::of).toList();
        return res;
    }
}
