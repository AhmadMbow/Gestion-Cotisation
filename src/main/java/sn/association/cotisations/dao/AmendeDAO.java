package sn.association.cotisations.dao;

import jakarta.persistence.EntityManager;
import sn.association.cotisations.entity.Amende;
import sn.association.cotisations.entity.StatutAmende;
import sn.association.cotisations.util.JPAUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public class AmendeDAO {

    public List<Amende> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT a FROM Amende a JOIN FETCH a.membre " +
                    "ORDER BY a.statutPaiement, a.dateGeneration DESC, a.id DESC",
                    Amende.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public Optional<Amende> findById(Integer id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Amende> r = em.createQuery(
                    "SELECT a FROM Amende a JOIN FETCH a.membre WHERE a.id = :id", Amende.class)
                    .setParameter("id", id)
                    .setMaxResults(1)
                    .getResultList();
            return r.isEmpty() ? Optional.empty() : Optional.of(r.get(0));
        } finally {
            em.close();
        }
    }

    public List<Amende> findByMembre(Integer membreNumero) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT a FROM Amende a WHERE a.membre.numero = :n ORDER BY a.dateGeneration DESC",
                    Amende.class)
                    .setParameter("n", membreNumero)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Y a-t-il déjà une amende générée pour ce membre dans le mois donné ?
     * Sert à empêcher les doublons lors de la génération automatique.
     */
    public boolean existsAmendePourMois(Integer membreNumero, int mois, int annee) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            YearMonth ym = YearMonth.of(annee, mois);
            LocalDate debut = ym.atDay(1);
            LocalDate fin = ym.atEndOfMonth();
            Long count = em.createQuery(
                    "SELECT COUNT(a) FROM Amende a WHERE a.membre.numero = :n " +
                    "AND a.dateGeneration BETWEEN :debut AND :fin", Long.class)
                    .setParameter("n", membreNumero)
                    .setParameter("debut", debut)
                    .setParameter("fin", fin)
                    .getSingleResult();
            return count != null && count > 0;
        } finally {
            em.close();
        }
    }

    public void delete(Integer id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Amende a = em.find(Amende.class, id);
            if (a != null) em.remove(a);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public long countImpayees() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT COUNT(a) FROM Amende a WHERE a.statutPaiement = :s", Long.class)
                    .setParameter("s", StatutAmende.IMPAYEE)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    public BigDecimal sumImpayeesByMembre(Integer membreNumero) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            BigDecimal r = em.createQuery(
                    "SELECT COALESCE(SUM(a.montant), 0) FROM Amende a " +
                    "WHERE a.membre.numero = :n AND a.statutPaiement = :s", BigDecimal.class)
                    .setParameter("n", membreNumero)
                    .setParameter("s", StatutAmende.IMPAYEE)
                    .getSingleResult();
            return r != null ? r : BigDecimal.ZERO;
        } finally {
            em.close();
        }
    }

    public Amende save(Amende a) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if (a.getId() == null) em.persist(a);
            else a = em.merge(a);
            em.getTransaction().commit();
            return a;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
