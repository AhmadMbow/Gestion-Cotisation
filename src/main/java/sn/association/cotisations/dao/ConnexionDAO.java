package sn.association.cotisations.dao;

import jakarta.persistence.EntityManager;
import sn.association.cotisations.entity.Connexion;
import sn.association.cotisations.util.JPAUtil;

import java.util.List;

public class ConnexionDAO {

    public Connexion save(Connexion c) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(c);
            em.getTransaction().commit();
            return c;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Dernières connexions, plus récentes en tête. Le membre est chargé eagerly.
     */
    public List<Connexion> findRecents(int limit) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT c FROM Connexion c JOIN FETCH c.membre " +
                    "ORDER BY c.dateConnexion DESC", Connexion.class)
                    .setMaxResults(limit)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public long count() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT COUNT(c) FROM Connexion c", Long.class)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }
}
