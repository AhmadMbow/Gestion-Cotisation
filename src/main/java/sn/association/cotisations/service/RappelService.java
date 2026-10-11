package sn.association.cotisations.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sn.association.cotisations.entity.Membre;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Envoi des rappels mensuels aux membres en retard de cotisation.
 *
 * Pas de planification automatique (Quartz / @Schedule) — déclenchement manuel
 * par l'admin depuis la page "Membres en retard".
 */
@ApplicationScoped
public class RappelService {

    private static final Logger log = LoggerFactory.getLogger(RappelService.class);

    @Inject CotisationService cotisationService;
    @Inject MailService mailService;
    @Inject NotificationService notificationService;

    public static class Resultat {
        public int envoyes;
        public int echecs;
        public int totalEnRetard;
    }

    /**
     * Envoie un rappel à chaque membre en retard sur la cotisation du mois courant.
     */
    public Resultat envoyerRappelsMoisCourant() {
        Resultat r = new Resultat();
        List<Membre> retards = cotisationService.membresEnRetardCeMois();
        r.totalEnRetard = retards.size();

        LocalDate now = LocalDate.now();
        int mois = now.getMonthValue();
        int annee = now.getYear();

        for (Membre m : retards) {
            int nbMoisDus = cotisationService.moisDusParMembre(m).size();
            BigDecimal totalDu = cotisationService.montantTotalDu(m);

            String moisLibelle = EmailTemplate.mois(mois, annee);
            String sujet = "Rappel — Cotisation de " + moisLibelle;
            EmailTemplate mail = EmailTemplate.titre("Petit rappel pour votre cotisation")
                    .intro("Bonjour " + m.getPrenom() + ",")
                    .intro("Vous n'avez pas encore réglé votre cotisation de " + moisLibelle + ".")
                    .ligne("Mois en attente", String.valueOf(nbMoisDus))
                    .total("Montant total dû", EmailTemplate.fcfa(totalDu))
                    .conclusion("Vous pouvez régulariser en quelques secondes depuis l'application Cotisen "
                            + "(Wave, Orange Money ou Free Money). Merci !");

            try {
                mailService.send(m.getEmail(), sujet, mail.texte(), mail.html());
                notificationService.rappel(m, nbMoisDus, totalDu);
                r.envoyes++;
            } catch (MessagingException e) {
                log.error("Échec rappel à {}", m.getEmail(), e);
                r.echecs++;
            }
        }
        notificationService.rappelsEnvoyes(r.envoyes);
        return r;
    }
}
