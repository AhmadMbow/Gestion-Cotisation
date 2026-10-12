package sn.association.cotisations.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import sn.association.cotisations.dao.GroupeDAO;
import sn.association.cotisations.entity.Groupe;

import java.math.BigDecimal;

/**
 * Montants standards d'un groupe (cotisation mensuelle et amende de retard).
 * Les méthodes sans groupe concernent le groupe historique (interface web).
 */
@ApplicationScoped
public class ParametreService {

    @Inject GroupeDAO groupeDAO;

    /** Groupe historique : ses montants s'affichent sur la page « Paramètres » du site. */
    public Groupe get() {
        return groupeDAO.findById(Groupe.HISTORIQUE)
                .orElseThrow(() -> new IllegalStateException("Groupe historique introuvable."));
    }

    public BigDecimal montantCotisation() {
        return get().getMontantCotisation();
    }

    public BigDecimal montantAmende() {
        return get().getMontantAmende();
    }

    public Groupe mettreAJour(BigDecimal montantCotisation, BigDecimal montantAmende) {
        return mettreAJour(get(), montantCotisation, montantAmende);
    }

    public Groupe mettreAJour(Groupe g, BigDecimal montantCotisation, BigDecimal montantAmende) {
        verifier(montantCotisation, montantAmende);
        g.setMontantCotisation(montantCotisation);
        g.setMontantAmende(montantAmende);
        return groupeDAO.save(g);
    }

    static void verifier(BigDecimal montantCotisation, BigDecimal montantAmende) {
        if (montantCotisation == null || montantCotisation.signum() <= 0) {
            throw new IllegalArgumentException("Le montant de la cotisation doit être strictement positif.");
        }
        if (montantAmende == null || montantAmende.signum() < 0) {
            throw new IllegalArgumentException("Le montant de l'amende ne peut pas être négatif.");
        }
    }
}
