package sn.association.cotisations.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import sn.association.cotisations.dao.CotisationDAO;
import sn.association.cotisations.dao.MembreDAO;
import sn.association.cotisations.entity.Cotisation;
import sn.association.cotisations.entity.Membre;
import sn.association.cotisations.entity.ModePaiement;
import sn.association.cotisations.entity.StatutCotisation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class CotisationService {

    /** Montant mensuel par défaut d'une cotisation, en FCFA. */
    public static final BigDecimal MONTANT_PAR_DEFAUT = new BigDecimal("5000");

    @Inject CotisationDAO cotisationDAO = new CotisationDAO();
    @Inject MembreDAO membreDAO = new MembreDAO();

    public List<Cotisation> listerTout() {
        return cotisationDAO.findAll();
    }

    public List<Cotisation> historiqueMembre(Integer numero) {
        return cotisationDAO.findByMembre(numero);
    }

    public List<Membre> membresEnRetardCeMois() {
        LocalDate now = LocalDate.now();
        return membreDAO.findEnRetardPourMois(now.getMonthValue(), now.getYear());
    }

    /**
     * Enregistre un paiement de cotisation.
     * Lève IllegalStateException si une cotisation PAYE existe déjà pour ce membre/mois/année.
     */
    public Cotisation enregistrer(Integer membreNumero, BigDecimal montant,
                                  int mois, int annee, ModePaiement mode) {
        if (mois < 1 || mois > 12) throw new IllegalArgumentException("Mois invalide.");
        YearMonth periode = YearMonth.of(annee, mois);
        if (periode.isAfter(YearMonth.now())) {
            throw new IllegalArgumentException("Impossible de payer une cotisation dans le futur.");
        }
        if (montant == null || montant.signum() <= 0) {
            throw new IllegalArgumentException("Le montant doit être strictement positif.");
        }
        Membre m = membreDAO.findById(membreNumero)
                .orElseThrow(() -> new IllegalArgumentException("Membre introuvable."));

        if (cotisationDAO.existsPaiementPourPeriode(membreNumero, mois, annee)) {
            throw new IllegalStateException(
                    "Une cotisation est déjà payée pour " + mois + "/" + annee + ".");
        }

        Cotisation c = new Cotisation();
        c.setMembre(m);
        c.setMontant(montant);
        c.setMois(mois);
        c.setAnnee(annee);
        c.setDatePaiement(LocalDate.now());
        c.setModePaiement(mode != null ? mode : ModePaiement.ESPECES);
        c.setStatut(StatutCotisation.PAYE);
        return cotisationDAO.save(c);
    }

    public Cotisation trouver(Integer id) {
        return cotisationDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cotisation introuvable."));
    }

    /**
     * Calcule la liste des mois pour lesquels un membre doit encore une cotisation.
     * = chaque mois entre l'adhésion et le mois courant, qui n'a pas de Cotisation PAYE.
     * Liste triée du plus ancien au plus récent.
     */
    public List<MoisDu> moisDusParMembre(Membre m) {
        if (m == null || m.getDateAdhesion() == null) return List.of();

        YearMonth depuis = YearMonth.from(m.getDateAdhesion());
        YearMonth jusqua = YearMonth.now();

        Set<YearMonth> payes = new HashSet<>();
        for (Cotisation c : cotisationDAO.findByMembre(m.getNumero())) {
            if (c.getStatut() == StatutCotisation.PAYE) {
                payes.add(YearMonth.of(c.getAnnee(), c.getMois()));
            }
        }

        List<MoisDu> dus = new ArrayList<>();
        YearMonth courant = depuis;
        while (!courant.isAfter(jusqua)) {
            if (!payes.contains(courant)) dus.add(new MoisDu(courant));
            courant = courant.plusMonths(1);
        }
        return dus;
    }

    /**
     * Montant total dû par un membre = (nombre de mois dus) × montant standard.
     */
    public BigDecimal montantTotalDu(Membre m) {
        int n = moisDusParMembre(m).size();
        return MONTANT_PAR_DEFAUT.multiply(BigDecimal.valueOf(n));
    }
}
