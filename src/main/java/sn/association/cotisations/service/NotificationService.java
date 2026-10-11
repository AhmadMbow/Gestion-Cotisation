package sn.association.cotisations.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sn.association.cotisations.dao.NotificationDAO;
import sn.association.cotisations.entity.Amende;
import sn.association.cotisations.entity.Cotisation;
import sn.association.cotisations.entity.Membre;
import sn.association.cotisations.entity.ModePaiement;
import sn.association.cotisations.entity.Notification;
import sn.association.cotisations.entity.Notification.Type;

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
            notifier(null, Type.PAIEMENT, "Nouveau paiement",
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
                            + "Conformément au règlement de l'association, une amende pour retard a été générée.")
                    .ligne("Motif", "Retard de cotisation · " + mois)
                    .ligne("Date", EmailTemplate.date(d))
                    .total("Montant", EmailTemplate.fcfa(a.getMontant()))
                    .conclusion("Vous pouvez régler votre cotisation et cette amende depuis l'application Cotisen "
                            + "(Wave, Orange Money ou Free Money).");
            mailService.sendAsync(m.getEmail(), "Nouvelle amende — " + mois, mail.texte(), mail.html());
        }
        if (!amendes.isEmpty()) {
            int n = amendes.size();
            notifier(null, Type.AMENDE, "Amendes générées",
                    n + " amende" + (n > 1 ? "s" : "") + " créée" + (n > 1 ? "s" : "") + " pour retard de paiement.");
        }
    }

    // ------------------------------------------------------------------
    // Membres
    // ------------------------------------------------------------------

    /** Email de bienvenue avec le lien pour choisir son mot de passe. */
    public void bienvenue(Membre m, String lienMotDePasse) {
        notifier(null, Type.MEMBRE, "Nouveau membre",
                m.getPrenom() + " " + m.getNom() + " (" + References.numero(m) + ") a été ajouté.");
        notifier(m, Type.INFO, "Bienvenue !", "Votre espace Cotisen est prêt. Bonne cotisation !");
        EmailTemplate mail = EmailTemplate.titre("Bienvenue dans l'association, " + m.getPrenom() + " !")
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

    public void rappelsEnvoyes(int n) {
        if (n > 0) {
            notifier(null, Type.RAPPEL, "Rappels envoyés",
                    n + " membre" + (n > 1 ? "s" : "") + " en retard " + (n > 1 ? "ont" : "a") + " reçu un rappel.");
        }
    }

    // ------------------------------------------------------------------

    private void notifier(Membre m, Type type, String titre, String message) {
        try {
            notificationDAO.save(new Notification(m, type, titre, message));
        } catch (RuntimeException e) {
            log.warn("Notification « {} » non enregistrée : {}", titre, e.getMessage());
        }
    }

    public static String libelle(ModePaiement mode) {
        if (mode == null) return "Espèces";
        return switch (mode) {
            case WAVE -> "Wave";
            case ORANGE_MONEY -> "Orange Money";
            case FREE_MONEY -> "Free Money";
            case VIREMENT -> "Virement";
            case ESPECES -> "Espèces";
        };
    }
}
