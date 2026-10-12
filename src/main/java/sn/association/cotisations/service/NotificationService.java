package sn.association.cotisations.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sn.association.cotisations.dao.NotificationDAO;
import sn.association.cotisations.entity.Amende;
import sn.association.cotisations.entity.Compte;
import sn.association.cotisations.entity.Cotisation;
import sn.association.cotisations.entity.Groupe;
import sn.association.cotisations.entity.Membre;
import sn.association.cotisations.entity.ModePaiement;
import sn.association.cotisations.entity.Notification;
import sn.association.cotisations.entity.Notification.Type;
import sn.association.cotisations.entity.PaiementAbonnement;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Prévient les membres et les administrateurs : notification dans l'application
 * et email (reçu de paiement, nouvelle amende, bienvenue).
 *
 * Tout est « best effort » : un problème d'email ou de notification n'annule
 * jamais un paiement déjà enregistré.
 */
@ApplicationScoped
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    @Inject NotificationDAO notificationDAO;
    @Inject MailService mailService;

    // ------------------------------------------------------------------
    // Paiements
    // ------------------------------------------------------------------

    /**
     * Reçu de paiement par email + notifications.
     * @param parAdmin true si l'encaissement a été saisi par un administrateur
     */
    public void paiement(Membre m, List<Cotisation> cotisations, List<Amende> amendes,
                         ModePaiement mode, boolean parAdmin) {
        if (cotisations.isEmpty() && amendes.isEmpty()) return;

        BigDecimal total = cotisations.stream().map(Cotisation::getMontant).reduce(BigDecimal.ZERO, BigDecimal::add)
                .add(amendes.stream().map(Amende::getMontant).reduce(BigDecimal.ZERO, BigDecimal::add));
        String moisLibelle = cotisations.stream()
                .sorted(Comparator.comparing(Cotisation::getAnnee).thenComparing(Cotisation::getMois))
                .map(c -> EmailTemplate.mois(c.getMois(), c.getAnnee()))
                .collect(Collectors.joining(", "));
        String reference = !cotisations.isEmpty()
                ? References.recu(cotisations.get(0))
                : References.amende(amendes.get(0));

        String resume = (cotisations.isEmpty() ? "" : "Cotisation " + moisLibelle)
                + (cotisations.isEmpty() || amendes.isEmpty() ? "" : " et ")
                + (amendes.isEmpty() ? "" : amendes.size() + " amende" + (amendes.size() > 1 ? "s" : ""));

        notifier(m, Type.PAIEMENT, "Paiement confirmé",
                resume + " réglé (" + EmailTemplate.fcfa(total) + "). Votre reçu est disponible.");
        if (!parAdmin) {
            notifierAdmins(m.getGroupe(), Type.PAIEMENT, "Nouveau paiement",
                    m.getPrenom() + " " + m.getNom() + " a payé " + EmailTemplate.fcfa(total)
                            + " via " + libelle(mode) + ".");
        }

        EmailTemplate mail = EmailTemplate.titre("Merci, votre paiement est confirmé")
                .intro("Bonjour " + m.getPrenom() + ",")
                .intro("Nous avons bien reçu votre paiement. Voici votre reçu.")
                .ligne("Reçu n°", reference)
                .ligne("Date", EmailTemplate.date(LocalDate.now()))
                .ligne("Moyen de paiement", libelle(mode));
        for (Cotisation c : cotisations) {
            mail.ligne("Cotisation " + EmailTemplate.mois(c.getMois(), c.getAnnee()), EmailTemplate.fcfa(c.getMontant()));
        }
        for (Amende a : amendes) {
            mail.ligne("Amende du " + EmailTemplate.date(a.getDateGeneration()), EmailTemplate.fcfa(a.getMontant()));
        }
        mail.total("Total payé", EmailTemplate.fcfa(total))
            .conclusion("Conservez cet e-mail : il fait office de reçu. Merci pour votre contribution !");
        mailService.sendAsync(m.getEmail(), "Reçu de paiement " + reference, mail.texte(), mail.html());
    }

    // ------------------------------------------------------------------
    // Amendes
    // ------------------------------------------------------------------

    public void amendesGenerees(List<Amende> amendes) {
        for (Amende a : amendes) {
            Membre m = a.getMembre();
            LocalDate d = a.getDateGeneration();
            String mois = EmailTemplate.mois(d.getMonthValue(), d.getYear());
            notifier(m, Type.AMENDE, "Nouvelle amende",
                    "Cotisation de " + mois + " non réglée : amende de " + EmailTemplate.fcfa(a.getMontant()) + ".");
            EmailTemplate mail = EmailTemplate.titre("Une amende a été ajoutée à votre compte")
                    .intro("Bonjour " + m.getPrenom() + ",")
                    .intro("Votre cotisation de " + mois + " n'a pas encore été réglée. "
                            + "Conformément au règlement du groupe « " + m.getGroupe().getNom()
                            + " », une amende pour retard a été générée.")
                    .ligne("Motif", "Retard de cotisation · " + mois)
                    .ligne("Date", EmailTemplate.date(d))
                    .total("Montant", EmailTemplate.fcfa(a.getMontant()))
                    .conclusion("Vous pouvez régler votre cotisation et cette amende depuis l'application Cotisen "
                            + "(Wave, Orange Money ou Mixx by Yas).");
            mailService.sendAsync(m.getEmail(), "Nouvelle amende — " + mois, mail.texte(), mail.html());
        }
        if (!amendes.isEmpty()) {
            int n = amendes.size();
            notifierAdmins(amendes.get(0).getMembre().getGroupe(), Type.AMENDE, "Amendes générées",
                    n + " amende" + (n > 1 ? "s" : "") + " créée" + (n > 1 ? "s" : "") + " pour retard de paiement.");
        }
    }

    // ------------------------------------------------------------------
    // Membres
    // ------------------------------------------------------------------

    /**
     * Membre ajouté par un administrateur. Avec un lien : le compte vient d'être créé et
     * la personne choisit son mot de passe ; sans lien : elle avait déjà un compte.
     */
    public void bienvenue(Membre m, String lienMotDePasse) {
        String groupe = m.getGroupe().getNom();
        notifierAdmins(m.getGroupe(), Type.MEMBRE, "Nouveau membre",
                m.getPrenom() + " " + m.getNom() + " (" + References.numero(m) + ") a été ajouté.");
        notifier(m, Type.INFO, "Bienvenue !", "Vous faites partie de « " + groupe + " ». Bonne cotisation !");
        if (lienMotDePasse == null) {
            EmailTemplate mail = EmailTemplate.titre("Vous avez rejoint « " + groupe + " »")
                    .intro("Bonjour " + m.getPrenom() + ",")
                    .intro("Un administrateur vous a ajouté au groupe « " + groupe + " ». Il apparaît "
                            + "désormais dans votre application Cotisen.")
                    .ligne("N° de membre", References.numero(m))
                    .ligne("Membre depuis", EmailTemplate.date(m.getDateAdhesion()))
                    .conclusion("Connectez-vous avec votre compte habituel pour suivre vos cotisations.");
            mailService.sendAsync(m.getEmail(), "Nouveau groupe : " + groupe, mail.texte(), mail.html());
            return;
        }
        EmailTemplate mail = EmailTemplate.titre("Bienvenue dans « " + groupe + " », " + m.getPrenom() + " !")
                .intro("Votre compte Cotisen vient d'être créé. Vous pourrez y payer vos cotisations "
                        + "et suivre votre situation à tout moment.")
                .ligne("N° de membre", References.numero(m))
                .ligne("Identifiant", m.getEmail())
                .ligne("Membre depuis", EmailTemplate.date(m.getDateAdhesion()))
                .bouton("Choisir mon mot de passe", lienMotDePasse)
                .conclusion("Ce lien est valable " + PasswordResetService.TTL_BIENVENUE.toDays() + " jours. "
                        + "Passé ce délai, utilisez « Mot de passe oublié ? » sur l'écran de connexion.");
        mailService.sendAsync(m.getEmail(), "Bienvenue sur Cotisen", mail.texte(), mail.html());
    }

    /** Notification accompagnant un email de rappel. */
    public void rappel(Membre m, int nbMois, BigDecimal totalDu) {
        notifier(m, Type.RAPPEL, "Rappel de paiement",
                "Vous avez " + nbMois + " mois de cotisation en attente (" + EmailTemplate.fcfa(totalDu)
                        + "). Merci de régulariser.");
    }

    /** Un membre a rejoint le groupe avec le code d'invitation. */
    public void membreRejoint(Membre m) {
        notifierAdmins(m.getGroupe(), Type.MEMBRE, "Nouveau membre",
                m.getPrenom() + " " + m.getNom() + " a rejoint le groupe avec le code d'invitation.");
        notifier(m, Type.INFO, "Bienvenue !",
                "Vous faites partie de « " + m.getGroupe().getNom() + " ». Bonne cotisation !");
    }

    /** Reçu d'abonnement par e-mail. */
    public void abonnement(Compte c, PaiementAbonnement p) {
        EmailTemplate mail = EmailTemplate.titre("Votre forfait " + p.getForfait().getLibelle() + " est actif")
                .intro("Bonjour " + c.getPrenom() + ",")
                .intro("Merci pour votre abonnement à Cotisen. Voici votre reçu.")
                .ligne("Reçu n°", References.abonnement(p))
                .ligne("Forfait", p.getForfait().getLibelle())
                .ligne("Période", EmailTemplate.date(p.getDebut()) + " → " + EmailTemplate.date(p.getFin()))
                .ligne("Moyen de paiement", libelle(p.getModePaiement()))
                .total("Total payé", EmailTemplate.fcfa(p.getMontant()))
                .conclusion("Conservez cet e-mail : il fait office de reçu.");
        mailService.sendAsync(c.getEmail(), "Reçu d'abonnement " + References.abonnement(p),
                mail.texte(), mail.html());
    }

    public void rappelsEnvoyes(Groupe g, int n) {
        if (n > 0) {
            notifierAdmins(g, Type.RAPPEL, "Rappels envoyés",
                    n + " membre" + (n > 1 ? "s" : "") + " en retard " + (n > 1 ? "ont" : "a") + " reçu un rappel.");
        }
    }

    // ------------------------------------------------------------------

    private void notifier(Membre m, Type type, String titre, String message) {
        enregistrer(new Notification(m.getGroupe(), m, type, titre, message));
    }

    /** Notification pour le fil des administrateurs du groupe. */
    private void notifierAdmins(Groupe g, Type type, String titre, String message) {
        enregistrer(new Notification(g, null, type, titre, message));
    }

    private void enregistrer(Notification n) {
        String titre = n.getTitre();
        try {
            notificationDAO.save(n);
        } catch (RuntimeException e) {
            log.warn("Notification « {} » non enregistrée : {}", titre, e.getMessage());
        }
    }

    public static String libelle(ModePaiement mode) {
        if (mode == null) return "Espèces";
        return switch (mode) {
            case WAVE -> "Wave";
            case ORANGE_MONEY -> "Orange Money";
            case FREE_MONEY -> "Mixx by Yas";
            case VIREMENT -> "Virement";
            case ESPECES -> "Espèces";
        };
    }
}
