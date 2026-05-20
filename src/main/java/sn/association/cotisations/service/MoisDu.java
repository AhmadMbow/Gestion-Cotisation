package sn.association.cotisations.service;

import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * Représente un mois pour lequel un membre doit encore une cotisation.
 * DTO immuable utilisé par les vues JSP.
 */
public final class MoisDu {

    private static final Locale FR = Locale.FRENCH;

    private final int mois;
    private final int annee;
    private final String libelle;

    public MoisDu(YearMonth ym) {
        this.mois = ym.getMonthValue();
        this.annee = ym.getYear();
        String nomMois = Month.of(mois).getDisplayName(TextStyle.FULL, FR);
        // Capitalize first letter ("janvier" → "Janvier")
        this.libelle = Character.toUpperCase(nomMois.charAt(0)) + nomMois.substring(1) + " " + annee;
    }

    public int getMois() { return mois; }
    public int getAnnee() { return annee; }
    public String getLibelle() { return libelle; }

    public YearMonth toYearMonth() {
        return YearMonth.of(annee, mois);
    }
}
