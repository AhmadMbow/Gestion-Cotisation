package sn.association.cotisations.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import sn.association.cotisations.entity.Connexion;
import sn.association.cotisations.entity.Groupe;
import sn.association.cotisations.util.JPAUtil;

import java.util.List;

@ApplicationScoped
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

    public List<Connexion> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT c FROM Connexion c JOIN FETCH c.compte " +
                    "WHERE EXISTS (SELECT 1 FROM Membre m WHERE m.compte = c.compte AND m.groupe.id = :g) " +
                    "ORDER BY c.id", Connexion.class)
                    .setParameter("g", Groupe.HISTORIQUE)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Dernières connexions des membres du groupe historique, plus récentes en tête.
     */
    public List<Connexion> findRecents(int limit) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT c FROM Connexion c JOIN FETCH c.compte " +
                    "WHERE EXISTS (SELECT 1 FROM Membre m WHERE m.compte = c.compte AND m.groupe.id = :g) " +
                    "ORDER BY c.dateConnexion DESC", Connexion.class)
                    .setParameter("g", Groupe.HISTORIQUE)
                    .setMaxResults(limit)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /** Dernières connexions d'un compte. */
    public List<Connexion> findByCompte(Integer compteId, int limit) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT c FROM Connexion c WHERE c.compte.id = :n " +
                    "ORDER BY c.dateConnexion DESC", Connexion.class)
                    .setParameter("n", compteId)
                    .setMaxResults(limit)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public long count() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT COUNT(c) FROM Connexion c " +
                    "WHERE EXISTS (SELECT 1 FROM Membre m WHERE m.compte = c.compte AND m.groupe.id = :g) ", Long.class)
                    .setParameter("g", Groupe.HISTORIQUE)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }
}
