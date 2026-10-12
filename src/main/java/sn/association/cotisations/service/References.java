package sn.association.cotisations.service;

import sn.association.cotisations.entity.Amende;
import sn.association.cotisations.entity.Cotisation;
import sn.association.cotisations.entity.Membre;
import sn.association.cotisations.entity.PaiementAbonnement;

/** Numéros lisibles affichés sur les reçus et dans l'application. */
public final class References {

    private References() {
    }

    /** « REC-2026-00042 » */
    public static String recu(Cotisation c) {
        return String.format("REC-%d-%05d", c.getDatePaiement().getYear(), c.getId());
    }

    /** « AMD-2026-00007 » */
    public static String amende(Amende a) {
        int annee = (a.getDatePaiement() != null ? a.getDatePaiement() : a.getDateGeneration()).getYear();
        return String.format("AMD-%d-%05d", annee, a.getId());
    }

    /** « ABO-2026-00003 » */
    public static String abonnement(PaiementAbonnement p) {
        return String.format("ABO-%d-%05d", p.getDatePaiement().getYear(), p.getId());
    }

        /** « M-0003 » */
    public static String numero(Membre m) {
        return String.format("M-%04d", m.getNumero());
    }
}
