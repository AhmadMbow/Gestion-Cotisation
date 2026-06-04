package sn.association.cotisations.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import sn.association.cotisations.dao.ParametreDAO;
import sn.association.cotisations.entity.Parametre;

import java.math.BigDecimal;

/**
 * Expose les montants standards configurables par l'administrateur
 * (montant de la cotisation mensuelle et montant d'une amende de retard).
 */
@ApplicationScoped
public class ParametreService {

    @Inject ParametreDAO parametreDAO;

    public Parametre get() {
        return parametreDAO.getOuCreer();
    }

    /** Montant standard de la cotisation mensuelle, défini par l'admin. */
    public BigDecimal montantCotisation() {
        return get().getMontantCotisation();
    }

    /** Montant standard d'une amende de retard, défini par l'admin. */
    public BigDecimal montantAmende() {
        return get().getMontantAmende();
    }

    public Parametre mettreAJour(BigDecimal montantCotisation, BigDecimal montantAmende) {
        if (montantCotisation == null || montantCotisation.signum() <= 0) {
            throw new IllegalArgumentException("Le montant de la cotisation doit être strictement positif.");
        }
        if (montantAmende == null || montantAmende.signum() < 0) {
            throw new IllegalArgumentException("Le montant de l'amende ne peut pas être négatif.");
        }
        Parametre p = get();
        p.setMontantCotisation(montantCotisation);
        p.setMontantAmende(montantAmende);
        return parametreDAO.save(p);
    }
}
