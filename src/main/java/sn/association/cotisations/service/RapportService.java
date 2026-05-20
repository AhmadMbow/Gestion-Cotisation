package sn.association.cotisations.service;

import sn.association.cotisations.dao.AmendeDAO;
import sn.association.cotisations.dao.CotisationDAO;
import sn.association.cotisations.dao.MembreDAO;
import sn.association.cotisations.entity.Membre;
import sn.association.cotisations.entity.ModePaiement;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RapportService {

    private static final Locale FR = Locale.FRENCH;

    private final CotisationDAO cotisationDAO = new CotisationDAO();
    private final MembreDAO membreDAO = new MembreDAO();
    private final AmendeDAO amendeDAO = new AmendeDAO();
    private final CotisationService cotisationService = new CotisationService();
    private final AmendeService amendeService = new AmendeService();

    /** Évolution des cotisations sur les 12 derniers mois. */
    public static class SerieMensuelle {
        private final List<String> labels = new ArrayList<>();
        private final List<BigDecimal> montants = new ArrayList<>();

        public List<String> getLabels() { return labels; }
        public List<BigDecimal> getMontants() { return montants; }
    }

    public SerieMensuelle evolutionCotisations12Mois() {
        // Map de toutes les périodes (YearMonth → total) initialisée à 0
        Map<YearMonth, BigDecimal> map = new HashMap<>();
        YearMonth start = YearMonth.now().minusMonths(11);
        for (int i = 0; i < 12; i++) {
            map.put(start.plusMonths(i), BigDecimal.ZERO);
        }

        for (Object[] row : cotisationDAO.sumByPeriodeLast12Months()) {
            Integer annee = (Integer) row[0];
            Integer mois = (Integer) row[1];
            BigDecimal total = (BigDecimal) row[2];
            map.put(YearMonth.of(annee, mois), total);
        }

        SerieMensuelle s = new SerieMensuelle();
        for (int i = 0; i < 12; i++) {
            YearMonth ym = start.plusMonths(i);
            String nom = Month.of(ym.getMonthValue())
                    .getDisplayName(TextStyle.SHORT, FR);
            s.labels.add(capitalize(nom) + " " + (ym.getYear() % 100));
            s.montants.add(map.get(ym));
        }
        return s;
    }

    /** Répartition des modes de paiement (label, count). */
    public static class ModeStat {
        private final String label;
        private final long count;
        public ModeStat(String label, long count) {
            this.label = label;
            this.count = count;
        }
        public String getLabel() { return label; }
        public long getCount() { return count; }
    }

    public List<ModeStat> repartitionModesPaiement() {
        // Initialiser tous les modes à 0 pour garder un ordre stable
        Map<ModePaiement, Long> map = new HashMap<>();
        for (ModePaiement m : ModePaiement.values()) map.put(m, 0L);

        for (Object[] row : cotisationDAO.countByModePaiement()) {
            ModePaiement mode = (ModePaiement) row[0];
            Long count = (Long) row[1];
            map.put(mode, count);
        }

        List<ModeStat> out = new ArrayList<>();
        for (ModePaiement m : ModePaiement.values()) {
            out.add(new ModeStat(m.name(), map.get(m)));
        }
        return out;
    }

    /** Top N membres avec le plus gros montant dû en cotisations. */
    public static class RetardStat {
        private final Membre membre;
        private final int nbMoisDus;
        private final BigDecimal montantDu;
        public RetardStat(Membre m, int n, BigDecimal montant) {
            this.membre = m;
            this.nbMoisDus = n;
            this.montantDu = montant;
        }
        public Membre getMembre() { return membre; }
        public int getNbMoisDus() { return nbMoisDus; }
        public BigDecimal getMontantDu() { return montantDu; }
    }

    public List<RetardStat> topRetardataires(int limit) {
        List<RetardStat> all = new ArrayList<>();
        for (Membre m : membreDAO.findActifs()) {
            if (m.getRole() == sn.association.cotisations.entity.Role.ADMIN) continue;
            int n = cotisationService.moisDusParMembre(m).size();
            if (n == 0) continue;
            BigDecimal montant = cotisationService.montantTotalDu(m);
            all.add(new RetardStat(m, n, montant));
        }
        all.sort(Comparator.comparing((RetardStat r) -> r.montantDu).reversed());
        return all.size() > limit ? all.subList(0, limit) : all;
    }

    /** Stats clés du tableau de bord rapports. */
    public Map<String, Object> statsCles() {
        Map<String, Object> s = new HashMap<>();
        long totalMembres = membreDAO.countActifs();
        s.put("totalMembres", totalMembres);

        BigDecimal totalAnnee = cotisationDAO.sumMontantsMoisCourant(); // proxy minimum
        // Plus précis : somme sur l'année
        BigDecimal sumAnnee = BigDecimal.ZERO;
        for (Object[] row : cotisationDAO.sumByPeriodeLast12Months()) {
            sumAnnee = sumAnnee.add((BigDecimal) row[2]);
        }
        s.put("totalCotise12Mois", sumAnnee);

        long nbEnRetard = cotisationDAO.countMembresEnRetardMoisCourant();
        s.put("nbEnRetard", nbEnRetard);
        long actifs = membreDAO.countActifs();
        // Membres actifs ayant un rôle MEMBRE — approximation via simple soustraction (admin = 1 ou 2)
        long ajourPct = actifs > 0 ? Math.max(0, (actifs - nbEnRetard) * 100 / actifs) : 0;
        s.put("ajourPourcent", ajourPct);

        s.put("nbAmendesImpayees", amendeDAO.countImpayees());

        return s;
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        // Retire les éventuels points (ex: "févr.")
        String cleaned = s.replace(".", "");
        return Character.toUpperCase(cleaned.charAt(0)) + cleaned.substring(1);
    }
}
