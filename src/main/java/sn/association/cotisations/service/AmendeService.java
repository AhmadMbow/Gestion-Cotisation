package sn.association.cotisations.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import sn.association.cotisations.dao.AmendeDAO;
import sn.association.cotisations.dao.MembreDAO;
import sn.association.cotisations.entity.Amende;
import sn.association.cotisations.entity.Membre;
import sn.association.cotisations.entity.ModePaiement;
import sn.association.cotisations.entity.StatutAmende;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
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
        return genererAmendesMoisCourant().size();
    }

    /**
     * Comme {@link #genererPourRetardsMoisCourant()}, mais renvoie les amendes créées
     * (pour pouvoir prévenir les membres concernés).
     */
    public List<Amende> genererAmendesMoisCourant() {
        LocalDate now = LocalDate.now();
        int mois = now.getMonthValue();
        int annee = now.getYear();

        List<Membre> enRetard = cotisationService.membresEnRetardCeMois();
        List<Amende> creees = new ArrayList<>();
        for (Membre m : enRetard) {
            if (amendeDAO.existsAmendePourMois(m.getNumero(), mois, annee)) continue;

            Amende a = new Amende();
            a.setMembre(m);
            a.setMontant(parametreService.montantAmende());
            a.setDateGeneration(now);
            a.setStatutPaiement(StatutAmende.IMPAYEE);
            creees.add(amendeDAO.save(a));
        }
        return creees;
    }

    /**
     * Marque une amende comme PAYEE (en espèces). Lève IllegalStateException si elle l'est déjà.
     */
    public Amende marquerPayee(Integer id) {
        return marquerPayee(id, ModePaiement.ESPECES);
    }

    /**
     * Marque une amende comme PAYEE avec le moyen de paiement utilisé.
     */
    public Amende marquerPayee(Integer id, ModePaiement mode) {
        Amende a = trouver(id);
        if (a.getStatutPaiement() == StatutAmende.PAYEE) {
            throw new IllegalStateException("Cette amende est déjà payée.");
        }
        a.setStatutPaiement(StatutAmende.PAYEE);
        a.setDatePaiement(LocalDate.now());
        a.setModePaiement(mode != null ? mode : ModePaiement.ESPECES);
        return amendeDAO.save(a);
    }

    /**
     * Variante côté membre — uniquement si l'amende lui appartient.
     */
    public Amende payerParMembre(Integer amendeId, Integer membreNumero) {
        return payerParMembre(amendeId, membreNumero, ModePaiement.ESPECES);
    }

    public Amende payerParMembre(Integer amendeId, Integer membreNumero, ModePaiement mode) {
        Amende a = trouver(amendeId);
        if (!a.getMembre().getNumero().equals(membreNumero)) {
            throw new IllegalArgumentException("Cette amende ne vous appartient pas.");
        }
        return marquerPayee(amendeId, mode);
    }

    public void supprimer(Integer id) {
        amendeDAO.delete(id);
    }
}
