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

            String sujet = "Rappel — Cotisation " + String.format("%02d/%d", mois, annee);
            String corps = "Bonjour " + m.getPrenom() + ",\n\n"
                    + "Vous n'avez pas encore réglé votre cotisation pour le mois "
                    + String.format("%02d/%d", mois, annee) + ".\n\n"
                    + "Récapitulatif :\n"
                    + "  - Mois en attente : " + nbMoisDus + "\n"
                    + "  - Montant total dû : " + totalDu + " FCFA\n\n"
                    + "Merci de régulariser dès que possible.\n\n"
                    + "Cordialement,\n"
                    + "-- Gestion Cotisations";

            try {
                mailService.send(m.getEmail(), sujet, corps);
                r.envoyes++;
            } catch (MessagingException e) {
                log.error("Échec rappel à {}", m.getEmail(), e);
                r.echecs++;
            }
        }
        return r;
    }
}
