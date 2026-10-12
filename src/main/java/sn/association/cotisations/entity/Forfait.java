package sn.association.cotisations.entity;

/**
 * Forfaits d'abonnement de Cotisen (prix mensuel en FCFA).
 *
 * Les limites s'appliquent au compte qui crée les groupes (le propriétaire) :
 * nombre de groupes qu'il peut créer et nombre de membres actifs par groupe.
 * {@link #ILLIMITE} vaut « sans limite ».
 */
public enum Forfait {

    GRATUIT("Gratuit", 0, 1, 15, false, false, "L'essentiel pour démarrer"),
    ESSENTIEL("Essentiel", 1_000, 2, 50, true, true, "Pour les petites associations"),
    PRO("Pro", 2_500, 5, 200, true, true, "Pour les grandes communautés"),
    ILLIMITE("Illimité", 5_000, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true,
            "Sans aucune limite");

    private final String libelle;
    private final int prixMensuel;
    private final int maxGroupes;
    private final int maxMembresParGroupe;
    private final boolean rapports;
    private final boolean relances;
    private final String accroche;

    Forfait(String libelle, int prixMensuel, int maxGroupes, int maxMembresParGroupe,
            boolean rapports, boolean relances, String accroche) {
        this.libelle = libelle;
        this.prixMensuel = prixMensuel;
        this.maxGroupes = maxGroupes;
        this.maxMembresParGroupe = maxMembresParGroupe;
        this.rapports = rapports;
        this.relances = relances;
        this.accroche = accroche;
    }

    public String getLibelle() { return libelle; }
    public int getPrixMensuel() { return prixMensuel; }
    public int getMaxGroupes() { return maxGroupes; }
    public int getMaxMembresParGroupe() { return maxMembresParGroupe; }
    /** Rapports PDF dans l'application. */
    public boolean isRapports() { return rapports; }
    /** Relances des retardataires par e-mail. */
    public boolean isRelances() { return relances; }
    public String getAccroche() { return accroche; }

    /** Premier forfait (par prix croissant) qui autorise ce nombre de groupes et de membres. */
    public static Forfait conseille(int groupes, int membresParGroupe) {
        for (Forfait f : values()) {
            if (groupes <= f.maxGroupes && membresParGroupe <= f.maxMembresParGroupe) return f;
        }
        return ILLIMITE;
    }
}
