package sn.association.cotisations.service;

import sn.association.cotisations.entity.Forfait;

/**
 * Action refusée par les limites du forfait (nombre de groupes ou de membres).
 * L'API la renvoie avec le code 402 pour que l'application propose un forfait supérieur.
 */
public class LimiteForfaitException extends IllegalStateException {

    private final Forfait forfaitConseille;

    public LimiteForfaitException(String message, Forfait forfaitConseille) {
        super(message);
        this.forfaitConseille = forfaitConseille;
    }

    public Forfait getForfaitConseille() {
        return forfaitConseille;
    }
}
