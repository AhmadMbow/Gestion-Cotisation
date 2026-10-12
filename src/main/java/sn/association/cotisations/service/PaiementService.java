package sn.association.cotisations.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import sn.association.cotisations.dao.CotisationDAO;
import sn.association.cotisations.entity.Amende;
import sn.association.cotisations.entity.Cotisation;
import sn.association.cotisations.entity.Membre;
import sn.association.cotisations.entity.ModePaiement;
import sn.association.cotisations.entity.StatutAmende;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Paiement groupé (plusieurs mois et/ou amendes en une fois), utilisé par l'application mobile.
 * Tout est vérifié avant la moindre écriture, pour ne pas enregistrer un paiement à moitié.
 */
@ApplicationScoped
public class PaiementService {

    @Inject CotisationService cotisationService;
    @Inject AmendeService amendeService;
    @Inject CotisationDAO cotisationDAO;
    @Inject NotificationService notificationService;

    public record Resultat(List<Cotisation> cotisations, List<Amende> amendes) {
    }

    public Resultat payer(Membre m, List<YearMonth> periodes, List<Integer> amendeIds,
                          ModePaiement mode, boolean parAdmin) {
        Set<YearMonth> mois = new LinkedHashSet<>(periodes);
        Set<Integer> ids = new LinkedHashSet<>(amendeIds);
        if (mois.isEmpty() && ids.isEmpty()) {
            throw new IllegalArgumentException("Sélectionnez au moins un mois ou une amende.");
        }
        if (mode == null) throw new IllegalArgumentException("Moyen de paiement manquant.");

        YearMonth maintenant = YearMonth.now();
        YearMonth adhesion = YearMonth.from(m.getDateAdhesion());
        for (YearMonth p : mois) {
            if (p.isAfter(maintenant)) {
                throw new IllegalArgumentException("Impossible de payer une cotisation dans le futur.");
            }
            if (p.isBefore(adhesion)) {
                throw new IllegalArgumentException("Le mois " + EmailTemplate.mois(p.getMonthValue(), p.getYear())
                        + " est antérieur à l'adhésion.");
            }
            if (cotisationDAO.existsPaiementPourPeriode(m.getNumero(), p.getMonthValue(), p.getYear())) {
                throw new IllegalStateException("La cotisation de "
                        + EmailTemplate.mois(p.getMonthValue(), p.getYear()) + " est déjà payée.");
            }
        }
        for (Integer id : ids) {
            Amende a = amendeService.trouver(id);
            if (!a.getMembre().getNumero().equals(m.getNumero())) {
                throw new IllegalArgumentException("Cette amende n'appartient pas à ce membre.");
            }
            if (a.getStatutPaiement() == StatutAmende.PAYEE) {
                throw new IllegalStateException("Cette amende est déjà payée.");
            }
        }

        List<Cotisation> cotisations = new ArrayList<>();
        for (YearMonth p : mois.stream().sorted().toList()) {
            cotisations.add(cotisationService.enregistrer(m.getNumero(), m.getGroupe().getMontantCotisation(),
                    p.getMonthValue(), p.getYear(), mode));
        }
        List<Amende> amendes = new ArrayList<>();
        for (Integer id : ids) {
            amendes.add(amendeService.marquerPayee(id, mode));
        }
        notificationService.paiement(m, cotisations, amendes, mode, parAdmin);
        return new Resultat(cotisations, amendes);
    }
}
