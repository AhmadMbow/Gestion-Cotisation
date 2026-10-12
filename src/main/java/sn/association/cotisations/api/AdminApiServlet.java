package sn.association.cotisations.api;

import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sn.association.cotisations.dao.MembreDAO;
import sn.association.cotisations.entity.Amende;
import sn.association.cotisations.entity.Forfait;
import sn.association.cotisations.entity.Groupe;
import sn.association.cotisations.entity.Membre;
import sn.association.cotisations.entity.ModePaiement;
import sn.association.cotisations.entity.Role;
import sn.association.cotisations.service.AmendeService;
import sn.association.cotisations.service.GroupeService;
import sn.association.cotisations.service.LimiteForfaitException;
import sn.association.cotisations.service.MembreService;
import sn.association.cotisations.service.NotificationService;
import sn.association.cotisations.service.PaiementService;
import sn.association.cotisations.service.PasswordResetService;
import sn.association.cotisations.service.RappelService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Actions des administrateurs du groupe demandé dans l'en-tête X-Groupe
 * (le filtre vérifie le rôle ; chaque ressource est vérifiée comme appartenant au groupe) :
 *
 * POST   /api/admin/membres                    ajouter (compte existant ou nouveau + e-mail)
 * PUT    /api/admin/membres/{n}                modifier le rôle / la date d'adhésion
 * DELETE /api/admin/membres/{n}                retirer du groupe
 * POST   /api/admin/membres/{n}/statut         activer / désactiver
 * POST   /api/admin/membres/{n}/paiements      encaisser {periodes, amendes, mode}
 * POST   /api/admin/amendes/generer            amendes du mois pour les retardataires
 * POST   /api/admin/amendes/{id}/payer         {mode}
 * POST   /api/admin/rappels                    e-mails de rappel aux retardataires (forfait payant)
 * PUT    /api/admin/groupe                     {nom, montantCotisation, montantAmende}
 * POST   /api/admin/groupe/code                nouveau code d'invitation
 */
@WebServlet(name = "AdminApiServlet", urlPatterns = {"/api/admin/*"})
public class AdminApiServlet extends ApiServlet {

    @Inject MembreService membreService;
    @Inject MembreDAO membreDAO;
    @Inject GroupeService groupeService;
    @Inject AmendeService amendeService;
    @Inject PaiementService paiementService;
    @Inject RappelService rappelService;
    @Inject NotificationService notificationService;
    @Inject PasswordResetService passwordResetService;

    private static final String INTROUVABLE = "Ressource introuvable.";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        run(req, resp, (rq, rs) -> {
            List<String> p = chemin(rq);
            Groupe g = groupe(rq);
            if (p.size() == 1 && p.get(0).equals("membres")) return creerMembre(rq, g);
            if (p.size() == 3 && p.get(0).equals("membres") && p.get(2).equals("statut")) {
                return basculerStatut(rq, entier(p.get(1)));
            }
            if (p.size() == 3 && p.get(0).equals("membres") && p.get(2).equals("paiements")) {
                Membre m = membre(rq, entier(p.get(1)));
                JsonObject body = corps(rq);
                ModePaiement mode = mode(body);
                return CompteApiServlet.resultat(paiementService.payer(
                        m, periodes(body), identifiants(body, "amendes"),
                        mode != null ? mode : ModePaiement.ESPECES, true));
            }
            if (p.size() == 2 && p.get(0).equals("amendes") && p.get(1).equals("generer")) {
                List<Amende> creees = amendeService.genererAmendesMoisCourant(g);
                notificationService.amendesGenerees(creees);
                return Map.of("creees", creees.size());
            }
            if (p.size() == 3 && p.get(0).equals("amendes") && p.get(2).equals("payer")) {
                return payerAmende(rq, entier(p.get(1)));
            }
            if (p.size() == 1 && p.get(0).equals("rappels")) {
                Forfait f = g.forfait();
                if (!f.isRelances()) {
                    throw new LimiteForfaitException("Les relances par e-mail sont incluses à partir du forfait "
                            + Forfait.ESSENTIEL.getLibelle() + ".", Forfait.ESSENTIEL);
                }
                RappelService.Resultat r = rappelService.envoyerRappelsMoisCourant(g);
                return Map.of("envoyes", r.envoyes, "echecs", r.echecs, "totalEnRetard", r.totalEnRetard);
            }
            if (p.size() == 2 && p.get(0).equals("groupe") && p.get(1).equals("code")) {
                Groupe maj = groupeService.nouveauCode(g);
                return Map.of("code", maj.getCodeInvitation());
            }
            throw new ApiException(HttpServletResponse.SC_NOT_FOUND, INTROUVABLE);
        });
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        run(req, resp, (rq, rs) -> {
            List<String> p = chemin(rq);
            JsonObject b = corps(rq);
            if (p.size() == 1 && p.get(0).equals("groupe")) {
                Groupe g = groupe(rq);
                BigDecimal cotisation = montant(b, "montantCotisation");
                BigDecimal amende = montant(b, "montantAmende");
                Groupe maj = groupeService.modifier(g, texte(b, "nom"),
                        cotisation != null ? cotisation : g.getMontantCotisation(),
                        amende != null ? amende : g.getMontantAmende());
                return Map.of("nom", maj.getNom(),
                        "montantCotisation", maj.getMontantCotisation().longValue(),
                        "montantAmende", maj.getMontantAmende().longValue());
            }
            exiger(p.size() == 2 && p.get(0).equals("membres"), HttpServletResponse.SC_NOT_FOUND, INTROUVABLE);
            int numero = entier(p.get(1));
            membre(rq, numero);
            Role role = role(b);
            exiger(numero != utilisateur(rq).getNumero() || role != Role.MEMBRE,
                    HttpServletResponse.SC_CONFLICT, "Vous ne pouvez pas retirer vos propres droits d'administrateur.");
            return Dto.of(membreService.modifierAdhesion(numero, role, date(b, "dateAdhesion")));
        });
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        run(req, resp, (rq, rs) -> {
            List<String> p = chemin(rq);
            exiger(p.size() == 2 && p.get(0).equals("membres"), HttpServletResponse.SC_NOT_FOUND, INTROUVABLE);
            int numero = entier(p.get(1));
            exiger(numero != utilisateur(rq).getNumero(), HttpServletResponse.SC_CONFLICT,
                    "Vous ne pouvez pas vous retirer vous-même du groupe.");
            Membre m = membre(rq, numero);
            exiger(!estProprietaire(m), HttpServletResponse.SC_CONFLICT,
                    "Le propriétaire du groupe ne peut pas en être retiré.");
            membreService.supprimer(numero);
            return null;
        });
    }

    private Dto.MembreDto creerMembre(HttpServletRequest req, Groupe g) throws IOException {
        JsonObject b = corps(req);
        MembreService.Ajout ajout = membreService.ajouter(g, texte(b, "prenom"), texte(b, "nom"),
                texte(b, "email"), texte(b, "telephone"), date(b, "dateNaissance"), date(b, "dateAdhesion"),
                role(b), texte(b, "motDePasse"));
        Membre m = ajout.membre();
        String lien = ajout.compteCree()
                ? passwordResetService.creerLien(m.getCompte(), PasswordResetService.TTL_BIENVENUE,
                        PasswordResetService.baseUrl(req))
                : null;
        notificationService.bienvenue(m, lien);
        return Dto.of(m);
    }

    private Dto.MembreDto basculerStatut(HttpServletRequest req, int numero) {
        exiger(numero != utilisateur(req).getNumero(), HttpServletResponse.SC_CONFLICT,
                "Vous ne pouvez pas désactiver votre propre adhésion.");
        Membre m = membre(req, numero);
        exiger(!estProprietaire(m), HttpServletResponse.SC_CONFLICT,
                "Le propriétaire du groupe ne peut pas être désactivé.");
        return Dto.of(membreService.basculerStatut(numero));
    }

    private Dto.PaiementResultat payerAmende(HttpServletRequest req, int id) throws IOException {
        Amende a = amendeService.trouver(id);
        Membre m = membre(req, a.getMembre().getNumero());
        ModePaiement mode = mode(corps(req));
        if (mode == null) mode = ModePaiement.ESPECES;
        Amende payee = amendeService.marquerPayee(id, mode);
        notificationService.paiement(m, List.of(), List.of(payee), mode, true);
        return CompteApiServlet.resultat(new PaiementService.Resultat(List.of(), List.of(payee)));
    }

    private static Groupe groupe(HttpServletRequest req) {
        return utilisateur(req).getGroupe();
    }

    /** Membre du groupe de l'administrateur (sinon : introuvable, même s'il existe ailleurs). */
    private Membre membre(HttpServletRequest req, int numero) {
        int g = groupe(req).getId();
        return membreDAO.findById(numero)
                .filter(m -> m.appartientAuGroupe(g))
                .orElseThrow(() -> new ApiException(HttpServletResponse.SC_NOT_FOUND, "Membre introuvable."));
    }

    private static boolean estProprietaire(Membre m) {
        return m.getGroupe().getProprietaire() != null && m.getGroupe().getProprietaire().equals(m.getCompte());
    }

    private static Role role(JsonObject b) {
        String r = texte(b, "role");
        return r == null ? null : Role.valueOf(r);
    }
}
