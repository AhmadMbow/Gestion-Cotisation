package sn.association.cotisations.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import sn.association.cotisations.entity.Cotisation;
import sn.association.cotisations.entity.StatutCotisation;
import sn.association.cotisations.util.JPAUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CotisationDAO {

    public Optional<Cotisation> findById(Integer id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Cotisation> r = em.createQuery(
                    "SELECT c FROM Cotisation c JOIN FETCH c.membre WHERE c.id = :id", Cotisation.class)
                    .setParameter("id", id)
                    .setMaxResults(1)
                    .getResultList();
            return r.isEmpty() ? Optional.empty() : Optional.of(r.get(0));
        } finally {
            em.close();
        }
    }

    public List<Cotisation> findByMembre(Integer membreNumero) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT c FROM Cotisation c WHERE c.membre.numero = :n ORDER BY c.annee DESC, c.mois DESC",
                    Cotisation.class)
                    .setParameter("n", membreNumero)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Cotisation> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT c FROM Cotisation c JOIN FETCH c.membre " +
                    "ORDER BY c.datePaiement DESC, c.id DESC", Cotisation.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Y a-t-il déjà une cotisation PAYE pour ce membre, ce mois et cette année ?
     */
    public boolean existsPaiementPourPeriode(Integer membreNumero, int mois, int annee) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Long count = em.createQuery(
                    "SELECT COUNT(c) FROM Cotisation c " +
                    "WHERE c.membre.numero = :n AND c.mois = :m AND c.annee = :a AND c.statut = :s",
                    Long.class)
                    .setParameter("n", membreNumero)
                    .setParameter("m", mois)
                    .setParameter("a", annee)
                    .setParameter("s", StatutCotisation.PAYE)
                    .getSingleResult();
            return count != null && count > 0;
        } finally {
            em.close();
        }
    }

    /**
     * Compte des membres ACTIFS qui n'ont pas payé leur cotisation du mois en cours.
     */
    public long countMembresEnRetardMoisCourant() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            LocalDate now = LocalDate.now();
            return em.createQuery(
                    "SELECT COUNT(m) FROM Membre m WHERE m.statut = sn.association.cotisations.entity.StatutMembre.ACTIF " +
                    "AND m.role = sn.association.cotisations.entity.Role.MEMBRE " +
                    "AND NOT EXISTS (SELECT 1 FROM Cotisation c " +
                    "                WHERE c.membre = m AND c.mois = :m AND c.annee = :a " +
                    "                AND c.statut = sn.association.cotisations.entity.StatutCotisation.PAYE)",
                    Long.class)
                    .setParameter("m", now.getMonthValue())
                    .setParameter("a", now.getYear())
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    public List<Cotisation> findRecents(int limit) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT c FROM Cotisation c JOIN FETCH c.membre ORDER BY c.datePaiement DESC",
                    Cotisation.class)
                    .setMaxResults(limit)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public BigDecimal sumMontantsMoisCourant() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            LocalDate now = LocalDate.now();
            BigDecimal r = em.createQuery(
                    "SELECT COALESCE(SUM(c.montant), 0) FROM Cotisation c " +
                    "WHERE c.mois = :m AND c.annee = :a AND c.statut = :s", BigDecimal.class)
                    .setParameter("m", now.getMonthValue())
                    .setParameter("a", now.getYear())
                    .setParameter("s", StatutCotisation.PAYE)
                    .getSingleResult();
            return r != null ? r : BigDecimal.ZERO;
        } finally {
            em.close();
        }
    }

    /**
     * Pour chaque (mois, année) sur les 12 derniers mois, retourne le total des cotisations PAYE.
     * Résultat : List<Object[]{Integer annee, Integer mois, BigDecimal total}>.
     */
    public List<Object[]> sumByPeriodeLast12Months() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            LocalDate from = LocalDate.now().minusMonths(11).withDayOfMonth(1);
            return em.createQuery(
                    "SELECT c.annee, c.mois, COALESCE(SUM(c.montant), 0) " +
                    "FROM Cotisation c " +
                    "WHERE c.statut = :s AND c.datePaiement >= :from " +
                    "GROUP BY c.annee, c.mois " +
                    "ORDER BY c.annee, c.mois", Object[].class)
                    .setParameter("s", StatutCotisation.PAYE)
                    .setParameter("from", from)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Comptage des cotisations PAYE par mode de paiement (toutes périodes).
     * Résultat : List<Object[]{ModePaiement, Long count}>.
     */
    public List<Object[]> countByModePaiement() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT c.modePaiement, COUNT(c) FROM Cotisation c " +
                    "WHERE c.statut = :s GROUP BY c.modePaiement " +
                    "ORDER BY COUNT(c) DESC", Object[].class)
                    .setParameter("s", StatutCotisation.PAYE)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public BigDecimal sumMontantsByMembreAnnee(Integer membreNumero, int annee) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            BigDecimal r = em.createQuery(
                    "SELECT COALESCE(SUM(c.montant), 0) FROM Cotisation c " +
                    "WHERE c.membre.numero = :n AND c.annee = :a AND c.statut = :s", BigDecimal.class)
                    .setParameter("n", membreNumero)
                    .setParameter("a", annee)
                    .setParameter("s", StatutCotisation.PAYE)
                    .getSingleResult();
            return r != null ? r : BigDecimal.ZERO;
        } finally {
            em.close();
        }
    }

    public Cotisation save(Cotisation c) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if (c.getId() == null) em.persist(c);
            else c = em.merge(c);
            em.getTransaction().commit();
            return c;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
