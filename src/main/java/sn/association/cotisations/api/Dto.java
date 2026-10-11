package sn.association.cotisations.api;

import sn.association.cotisations.entity.Amende;
import sn.association.cotisations.entity.Connexion;
import sn.association.cotisations.entity.Cotisation;
import sn.association.cotisations.entity.Membre;
import sn.association.cotisations.entity.Notification;
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

    /** Tout ce dont l'application a besoin, en un seul appel. */
    public static class Donnees {
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
        public MembreDto utilisateur;
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
