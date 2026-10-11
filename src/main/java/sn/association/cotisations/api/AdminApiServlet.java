package sn.association.cotisations.api;

import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sn.association.cotisations.entity.Amende;
import sn.association.cotisations.entity.Membre;
import sn.association.cotisations.entity.ModePaiement;
import sn.association.cotisations.entity.Role;
import sn.association.cotisations.entity.StatutMembre;
import sn.association.cotisations.service.AmendeService;
import sn.association.cotisations.service.ApiTokenService;
import sn.association.cotisations.service.MembreService;
import sn.association.cotisations.service.NotificationService;
import sn.association.cotisations.service.PaiementService;
import sn.association.cotisations.service.PasswordResetService;
import sn.association.cotisations.service.RappelService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Actions réservées aux administrateurs (le filtre vérifie le rôle) :
 *
 * POST   /api/admin/membres                    créer (+ email de bienvenue)
 * PUT    /api/admin/membres/{n}                modifier
 * DELETE /api/admin/membres/{n}                supprimer
 * POST   /api/admin/membres/{n}/statut         activer / désactiver
 * POST   /api/admin/membres/{n}/paiements      encaisser {periodes, amendes, mode}
 * POST   /api/admin/amendes/generer            amendes du mois pour les retardataires
 * POST   /api/admin/amendes/{id}/payer         {mode}
 * POST   /api/admin/rappels                    emails de rappel aux retardataires
 */
@WebServlet(name = "AdminApiServlet", urlPatterns = {"/api/admin/*"})
public class AdminApiServlet extends ApiServlet {

    @Inject MembreService membreService;
    @Inject AmendeService amendeService;
    @Inject PaiementService paiementService;
    @Inject RappelService rappelService;
    @Inject NotificationService notificationService;
    @Inject PasswordResetService passwordResetService;
    @Inject ApiTokenService apiTokenService;

    private static final String INTROUVABLE = "Ressource introuvable.";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        run(req, resp, (rq, rs) -> {
            List<String> p = chemin(rq);
            if (p.size() == 1 && p.get(0).equals("membres")) return creerMembre(rq);
            if (p.size() == 3 && p.get(0).equals("membres") && p.get(2).equals("statut")) {
                return basculerStatut(rq, entier(p.get(1)));
            }
            if (p.size() == 3 && p.get(0).equals("membres") && p.get(2).equals("paiements")) {
                Membre m = membre(entier(p.get(1)));
                JsonObject body = corps(rq);
                ModePaiement mode = mode(body);
                return CompteApiServlet.resultat(paiementService.payer(
                        m, periodes(body), identifiants(body, "amendes"),
                        mode != null ? mode : ModePaiement.ESPECES, true));
            }
            if (p.size() == 2 && p.get(0).equals("amendes") && p.get(1).equals("generer")) {
                List<Amende> creees = amendeService.genererAmendesMoisCourant();
                notificationService.amendesGenerees(creees);
                return Map.of("creees", creees.size());
            }
            if (p.size() == 3 && p.get(0).equals("amendes") && p.get(2).equals("payer")) {
                return payerAmende(rq, entier(p.get(1)));
            }
            if (p.size() == 1 && p.get(0).equals("rappels")) {
                RappelService.Resultat r = rappelService.envoyerRappelsMoisCourant();
                return Map.of("envoyes", r.envoyes, "echecs", r.echecs, "totalEnRetard", r.totalEnRetard);
            }
            throw new ApiException(HttpServletResponse.SC_NOT_FOUND, INTROUVABLE);
        });
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        run(req, resp, (rq, rs) -> {
            List<String> p = chemin(rq);
            exiger(p.size() == 2 && p.get(0).equals("membres"), HttpServletResponse.SC_NOT_FOUND, INTROUVABLE);
            int numero = entier(p.get(1));
            membre(numero);
            JsonObject b = corps(rq);
            Role role = role(b);
            exiger(numero != utilisateur(rq).getNumero() || role != Role.MEMBRE,
                    HttpServletResponse.SC_CONFLICT, "Vous ne pouvez pas retirer vos propres droits d'administrateur.");
            Membre m = membreService.modifier(numero, texte(b, "prenom"), texte(b, "nom"), texte(b, "email"),
                    texte(b, "telephone"), date(b, "dateNaissance"), date(b, "dateAdhesion"), role,
                    texte(b, "motDePasse"));
            return Dto.of(m);
        });
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        run(req, resp, (rq, rs) -> {
            List<String> p = chemin(rq);
            exiger(p.size() == 2 && p.get(0).equals("membres"), HttpServletResponse.SC_NOT_FOUND, INTROUVABLE);
            int numero = entier(p.get(1));
            exiger(numero != utilisateur(rq).getNumero(), HttpServletResponse.SC_CONFLICT,
                    "Vous ne pouvez pas supprimer votre propre compte.");
            membre(numero);
            membreService.supprimer(numero);
            return null;
        });
    }

    private Dto.MembreDto creerMembre(HttpServletRequest req) throws IOException {
        JsonObject b = corps(req);
        Membre m = membreService.creer(texte(b, "prenom"), texte(b, "nom"), texte(b, "email"),
                texte(b, "telephone"), date(b, "dateNaissance"), date(b, "dateAdhesion"), role(b),
                texte(b, "motDePasse"));
        String lien = passwordResetService.creerLien(m, PasswordResetService.TTL_BIENVENUE,
                PasswordResetService.baseUrl(req));
        notificationService.bienvenue(m, lien);
        return Dto.of(m);
    }

    private Dto.MembreDto basculerStatut(HttpServletRequest req, int numero) {
        exiger(numero != utilisateur(req).getNumero(), HttpServletResponse.SC_CONFLICT,
                "Vous ne pouvez pas désactiver votre propre compte.");
        membre(numero);
        Membre m = membreService.basculerStatut(numero);
        if (m.getStatut() == StatutMembre.INACTIF) apiTokenService.revoquerTout(numero);
        return Dto.of(m);
    }

    private Dto.PaiementResultat payerAmende(HttpServletRequest req, int id) throws IOException {
        Amende a = amendeService.trouver(id);
        ModePaiement mode = mode(corps(req));
        if (mode == null) mode = ModePaiement.ESPECES;
        Membre m = a.getMembre();
        Amende payee = amendeService.marquerPayee(id, mode);
        notificationService.paiement(m, List.of(), List.of(payee), mode, true);
        return CompteApiServlet.resultat(new PaiementService.Resultat(List.of(), List.of(payee)));
    }

    private Membre membre(int numero) {
        return membreService.trouver(numero)
                .orElseThrow(() -> new ApiException(HttpServletResponse.SC_NOT_FOUND, "Membre introuvable."));
    }

    private static Role role(JsonObject b) {
        String r = texte(b, "role");
        return r == null ? null : Role.valueOf(r);
    }
}
