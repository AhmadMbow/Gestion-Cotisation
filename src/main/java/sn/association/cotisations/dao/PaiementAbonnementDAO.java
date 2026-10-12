package sn.association.cotisations.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import sn.association.cotisations.entity.PaiementAbonnement;
import sn.association.cotisations.util.JPAUtil;

import java.util.List;

@ApplicationScoped
public class PaiementAbonnementDAO {

    /** Paiements d'abonnement d'un compte, les plus récents d'abord. */
    public List<PaiementAbonnement> findByCompte(Integer compteId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT p FROM PaiementAbonnement p WHERE p.compte.id = :c " +
                    "ORDER BY p.datePaiement DESC, p.id DESC", PaiementAbonnement.class)
                    .setParameter("c", compteId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public PaiementAbonnement save(PaiementAbonnement p) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(p);
            em.getTransaction().commit();
            return p;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
