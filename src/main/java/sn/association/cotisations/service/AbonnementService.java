package sn.association.cotisations.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import sn.association.cotisations.dao.CompteDAO;
import sn.association.cotisations.dao.GroupeDAO;
import sn.association.cotisations.dao.PaiementAbonnementDAO;
import sn.association.cotisations.entity.Compte;
import sn.association.cotisations.entity.Forfait;
import sn.association.cotisations.entity.ModePaiement;
import sn.association.cotisations.entity.PaiementAbonnement;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;

/**
 * Abonnements mensuels. Le paiement mobile est simulé (comme les cotisations) :
 * l'enregistrement vaut confirmation, en attendant un prestataire de paiement réel.
 */
@ApplicationScoped
public class AbonnementService {

    private static final EnumSet<ModePaiement> MODES = EnumSet.of(
            ModePaiement.WAVE, ModePaiement.ORANGE_MONEY, ModePaiement.FREE_MONEY);

    @Inject CompteDAO compteDAO;
    @Inject GroupeDAO groupeDAO;
    @Inject PaiementAbonnementDAO paiementDAO;
    @Inject NotificationService notificationService;

    public List<PaiementAbonnement> historique(Compte c) {
        return paiementDAO.findByCompte(c.getId());
    }

    public long groupesCrees(Compte c) {
        return groupeDAO.countParProprietaire(c.getId());
    }

    /**
     * Paie un mois du forfait choisi.
     * Même forfait encore actif → prolongation d'un mois ; sinon il démarre aujourd'hui.
     * Un forfait inférieur ne peut être pris qu'après l'expiration du forfait en cours.
     */
    public PaiementAbonnement souscrire(Compte c, Forfait forfait, ModePaiement mode) {
        if (forfait == null || forfait == Forfait.GRATUIT) {
            throw new IllegalArgumentException("Choisissez un forfait payant.");
        }
        if (mode == null || !MODES.contains(mode)) {
            throw new IllegalArgumentException("Payez avec Wave, Orange Money ou Mixx by Yas.");
        }
        Forfait actuel = c.forfaitEffectif();
        LocalDate aujourdhui = LocalDate.now();
        LocalDate debut;
        if (actuel == forfait) {
            debut = c.getForfaitExpireLe().plusDays(1);
        } else if (actuel.ordinal() > forfait.ordinal()) {
            throw new IllegalStateException("Votre forfait " + actuel.getLibelle() + " est actif jusqu'au "
                    + EmailTemplate.date(c.getForfaitExpireLe()) + ". Vous pourrez en changer à son expiration.");
        } else {
            debut = aujourdhui;
        }
        LocalDate fin = debut.plusMonths(1).minusDays(1);

        c.setForfait(forfait);
        c.setForfaitExpireLe(fin);
        compteDAO.save(c);

        PaiementAbonnement p = new PaiementAbonnement();
        p.setCompte(c);
        p.setForfait(forfait);
        p.setMontant(BigDecimal.valueOf(forfait.getPrixMensuel()));
        p.setModePaiement(mode);
        p.setDebut(debut);
        p.setFin(fin);
        p = paiementDAO.save(p);
        notificationService.abonnement(c, p);
        return p;
    }
}
