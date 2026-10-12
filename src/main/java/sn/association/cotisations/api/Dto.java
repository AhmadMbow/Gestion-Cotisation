package sn.association.cotisations.api;

import sn.association.cotisations.entity.Amende;
import sn.association.cotisations.entity.Compte;
import sn.association.cotisations.entity.Connexion;
import sn.association.cotisations.entity.Cotisation;
import sn.association.cotisations.entity.Forfait;
import sn.association.cotisations.entity.Groupe;
import sn.association.cotisations.entity.Membre;
import sn.association.cotisations.entity.Notification;
import sn.association.cotisations.entity.PaiementAbonnement;
import sn.association.cotisations.entity.Role;
import sn.association.cotisations.entity.StatutAmende;
import sn.association.cotisations.entity.StatutMembre;
import sn.association.cotisations.service.References;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Objets renvoyés en JSON à l'application mobile (champs publics → JSON-B).
 * Les montants sont des entiers en francs CFA ; les dates au format ISO-8601.
 */
public final class Dto {

    private Dto() {
    }

    public static class MembreDto {
        public int numero;
        public String reference;
        public String prenom;
        public String nom;
        public String email;
        public String telephone;
        public LocalDate dateNaissance;
        public LocalDate dateAdhesion;
        public boolean actif;
        public String role;
    }

    public static class CotisationDto {
        public int id;
        public int membre;
        public long montant;
        public LocalDate datePaiement;
        public int mois;
        public int annee;
        public String mode;
        public String statut;
        public String reference;
    }

    public static class AmendeDto {
        public int id;
        public int membre;
        public long montant;
        public LocalDate dateGeneration;
        public boolean payee;
        public LocalDate datePaiement;
        public String mode;
        public String reference;
    }

    public static class NotificationDto {
        public int id;
        public String type;
        public String titre;
        public String message;
        public LocalDateTime date;
        public boolean lue;
        public boolean pourAdmins;
    }

    public static class ConnexionDto {
        public LocalDateTime date;
        public String appareil;
    }

    public static class ParametresDto {
        public long montantCotisation;
        public long montantAmende;
    }

    public static class CompteDto {
        public int id;
        public String prenom;
        public String nom;
        public String email;
        public String telephone;
        public LocalDate dateNaissance;
    }

    /** Un forfait du catalogue (prix mensuel en FCFA ; null = illimité). */
    public static class ForfaitDto {
        public String code;
        public String libelle;
        public String accroche;
        public int prix;
        public Integer maxGroupes;
        public Integer maxMembres;
        public boolean rapports;
        public boolean relances;
    }

    public static class PaiementAbonnementDto {
        public String reference;
        public String forfait;
        public long montant;
        public String mode;
        public LocalDateTime date;
        public LocalDate debut;
        public LocalDate fin;
    }

    /** Forfait du compte et utilisation de ses limites. */
    public static class AbonnementDto {
        public String forfait;
        public LocalDate expireLe;
        public long groupesCrees;
        public List<PaiementAbonnementDto> paiements;
    }

    /** Un groupe dont l'utilisateur fait partie, vu par lui. */
    public static class GroupeDto {
        public int id;
        public String nom;
        /** Code d'invitation (administrateurs seulement). */
        public String code;
        public String role;
        public boolean actif;
        public int monNumero;
        public boolean proprietaire;
        public long nbMembres;
        /** Limite du forfait du propriétaire (null = illimité). */
        public Integer maxMembres;
        public String forfait;
        public boolean rapports;
        public boolean relances;
        public long montantCotisation;
        public long montantAmende;
    }

    /** Tout ce dont l'application a besoin, en un seul appel. */
    public static class Donnees {
        public CompteDto compte;
        public AbonnementDto abonnement;
        public List<ForfaitDto> forfaits;
        public List<GroupeDto> groupes;
        /** Groupe demandé dans l'en-tête X-Groupe (null si aucun). */
        public GroupeDto groupe;
        public MembreDto utilisateur;
        public ParametresDto parametres;
        public List<MembreDto> membres;
        public List<CotisationDto> cotisations;
        public List<AmendeDto> amendes;
        public List<NotificationDto> notifications;
        public List<ConnexionDto> connexions;
        public LocalDateTime serveurMaintenant = LocalDateTime.now();
    }

    public static class Session {
        public String jeton;
        public LocalDateTime expireLe;
        public CompteDto compte;
    }

    public static class PaiementResultat {
        public List<CotisationDto> cotisations;
        public List<AmendeDto> amendes;
    }

    // ------------------------------------------------------------------

    public static MembreDto of(Membre m) {
        MembreDto d = new MembreDto();
        d.numero = m.getNumero();
        d.reference = References.numero(m);
        d.prenom = m.getPrenom();
        d.nom = m.getNom();
        d.email = m.getEmail();
        d.telephone = m.getTelephone();
        d.dateNaissance = m.getDateNaissance();
        d.dateAdhesion = m.getDateAdhesion();
        d.actif = m.getStatut() == StatutMembre.ACTIF;
        d.role = m.getRole().name();
        return d;
    }

    /** Fiche réduite d'un autre membre, pour un simple membre (pas de coordonnées). */
    public static MembreDto apercu(Membre m) {
        MembreDto d = of(m);
        d.email = null;
        d.telephone = null;
        d.dateNaissance = null;
        return d;
    }

    public static CompteDto of(Compte c) {
        CompteDto d = new CompteDto();
        d.id = c.getId();
        d.prenom = c.getPrenom();
        d.nom = c.getNom();
        d.email = c.getEmail();
        d.telephone = c.getTelephone();
        d.dateNaissance = c.getDateNaissance();
        return d;
    }

    public static ForfaitDto of(Forfait f) {
        ForfaitDto d = new ForfaitDto();
        d.code = f.name();
        d.libelle = f.getLibelle();
        d.accroche = f.getAccroche();
        d.prix = f.getPrixMensuel();
        d.maxGroupes = limite(f.getMaxGroupes());
        d.maxMembres = limite(f.getMaxMembresParGroupe());
        d.rapports = f.isRapports();
        d.relances = f.isRelances();
        return d;
    }

    public static PaiementAbonnementDto of(PaiementAbonnement p) {
        PaiementAbonnementDto d = new PaiementAbonnementDto();
        d.reference = References.abonnement(p);
        d.forfait = p.getForfait().name();
        d.montant = p.getMontant().longValue();
        d.mode = p.getModePaiement().name();
        d.date = p.getDatePaiement();
        d.debut = p.getDebut();
        d.fin = p.getFin();
        return d;
    }

    /** Le groupe d'une adhésion, vu par ce membre. */
    public static GroupeDto groupe(Membre m, long nbMembres) {
        Groupe g = m.getGroupe();
        Forfait f = g.forfait();
        boolean admin = m.getRole() == Role.ADMIN;
        GroupeDto d = new GroupeDto();
        d.id = g.getId();
        d.nom = g.getNom();
        d.code = admin ? g.getCodeInvitation() : null;
        d.role = m.getRole().name();
        d.actif = m.getStatut() == StatutMembre.ACTIF;
        d.monNumero = m.getNumero();
        d.proprietaire = g.getProprietaire() != null && g.getProprietaire().equals(m.getCompte());
        d.nbMembres = nbMembres;
        d.maxMembres = limite(f.getMaxMembresParGroupe());
        d.forfait = f.name();
        d.rapports = f.isRapports();
        d.relances = f.isRelances();
        d.montantCotisation = g.getMontantCotisation().longValue();
        d.montantAmende = g.getMontantAmende().longValue();
        return d;
    }

    private static Integer limite(int v) {
        return v == Integer.MAX_VALUE ? null : v;
    }

    public static CotisationDto of(Cotisation c) {
        CotisationDto d = new CotisationDto();
        d.id = c.getId();
        d.membre = c.getMembre().getNumero();
        d.montant = c.getMontant().longValue();
        d.datePaiement = c.getDatePaiement();
        d.mois = c.getMois();
        d.annee = c.getAnnee();
        d.mode = c.getModePaiement().name();
        d.statut = c.getStatut().name();
        d.reference = References.recu(c);
        return d;
    }

    public static AmendeDto of(Amende a) {
        AmendeDto d = new AmendeDto();
        d.id = a.getId();
        d.membre = a.getMembre().getNumero();
        d.montant = a.getMontant().longValue();
        d.dateGeneration = a.getDateGeneration();
        d.payee = a.getStatutPaiement() == StatutAmende.PAYEE;
        d.datePaiement = a.getDatePaiement();
        d.mode = a.getModePaiement() == null ? null : a.getModePaiement().name();
        d.reference = References.amende(a);
        return d;
    }

    public static NotificationDto of(Notification n) {
        NotificationDto d = new NotificationDto();
        d.id = n.getId();
        d.type = n.getType().name();
        d.titre = n.getTitre();
        d.message = n.getMessage();
        d.date = n.getDateCreation();
        d.lue = n.isLue();
        d.pourAdmins = n.getMembre() == null;
        return d;
    }

    public static ConnexionDto of(Connexion c) {
        ConnexionDto d = new ConnexionDto();
        d.date = c.getDateConnexion();
        String ua = c.getUserAgent() == null ? "" : c.getUserAgent();
        d.appareil = ua.startsWith("Cotisen") ? "Application mobile" : "Navigateur web";
        return d;
    }
}
