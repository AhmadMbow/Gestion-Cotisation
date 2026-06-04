package sn.association.cotisations.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import sn.association.cotisations.dao.AmendeDAO;
import sn.association.cotisations.dao.MembreDAO;
import sn.association.cotisations.entity.Amende;
import sn.association.cotisations.entity.Membre;
import sn.association.cotisations.entity.StatutAmende;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class AmendeService {

    /** Montant standard d'une amende pour retard de cotisation, en FCFA. */
    public static final BigDecimal MONTANT_STANDARD = new BigDecimal("2000");

    @Inject AmendeDAO amendeDAO;
    @Inject MembreDAO membreDAO;
    @Inject CotisationService cotisationService;
    @Inject ParametreService parametreService;

    public List<Amende> listerTout() {
        return amendeDAO.findAll();
    }

    public List<Amende> historiqueMembre(Integer numero) {
        return amendeDAO.findByMembre(numero);
    }

    public Amende trouver(Integer id) {
        return amendeDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Amende introuvable."));
    }

    /**
     * Génère manuellement une amende pour un membre donné.
     */
    public Amende generer(Integer membreNumero, BigDecimal montant) {
        if (montant == null || montant.signum() <= 0) {
            throw new IllegalArgumentException("Le montant doit être strictement positif.");
        }
        Membre m = membreDAO.findById(membreNumero)
                .orElseThrow(() -> new IllegalArgumentException("Membre introuvable."));

        Amende a = new Amende();
        a.setMembre(m);
        a.setMontant(montant);
        a.setDateGeneration(LocalDate.now());
        a.setStatutPaiement(StatutAmende.IMPAYEE);
        return amendeDAO.save(a);
    }

    /**
     * Génère automatiquement des amendes pour chaque membre actif en retard
     * de cotisation pour le mois courant. Évite les doublons sur le mois.
     * @return le nombre d'amendes effectivement créées.
     */
    public int genererPourRetardsMoisCourant() {
        LocalDate now = LocalDate.now();
        int mois = now.getMonthValue();
        int annee = now.getYear();

        List<Membre> enRetard = cotisationService.membresEnRetardCeMois();
        int created = 0;
        for (Membre m : enRetard) {
            if (amendeDAO.existsAmendePourMois(m.getNumero(), mois, annee)) continue;

            Amende a = new Amende();
            a.setMembre(m);
            a.setMontant(parametreService.montantAmende());
            a.setDateGeneration(now);
            a.setStatutPaiement(StatutAmende.IMPAYEE);
            amendeDAO.save(a);
            created++;
        }
        return created;
    }

    /**
     * Marque une amende comme PAYEE. Lève IllegalStateException si elle l'est déjà.
     */
    public Amende marquerPayee(Integer id) {
        Amende a = trouver(id);
        if (a.getStatutPaiement() == StatutAmende.PAYEE) {
            throw new IllegalStateException("Cette amende est déjà payée.");
        }
        a.setStatutPaiement(StatutAmende.PAYEE);
        return amendeDAO.save(a);
    }

    /**
     * Variante côté membre — uniquement si l'amende lui appartient.
     */
    public Amende payerParMembre(Integer amendeId, Integer membreNumero) {
        Amende a = trouver(amendeId);
        if (!a.getMembre().getNumero().equals(membreNumero)) {
            throw new IllegalArgumentException("Cette amende ne vous appartient pas.");
        }
        return marquerPayee(amendeId);
    }

    public void supprimer(Integer id) {
        amendeDAO.delete(id);
    }
}
