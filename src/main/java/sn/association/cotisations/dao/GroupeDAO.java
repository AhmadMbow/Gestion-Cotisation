package sn.association.cotisations.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import sn.association.cotisations.entity.Groupe;
import sn.association.cotisations.util.JPAUtil;

import java.util.Optional;

@ApplicationScoped
public class GroupeDAO {

    public Optional<Groupe> findById(Integer id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return Optional.ofNullable(em.find(Groupe.class, id));
        } finally {
            em.close();
        }
    }

    public Optional<Groupe> findByCode(String code) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT g FROM Groupe g WHERE g.codeInvitation = :c", Groupe.class)
                    .setParameter("c", code)
                    .getResultStream()
                    .findFirst();
        } finally {
            em.close();
        }
    }

    /** Groupes actifs créés par ce compte (limite du forfait). */
    public long countParProprietaire(Integer compteId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT COUNT(g) FROM Groupe g WHERE g.proprietaire.id = :c AND g.actif = true", Long.class)
                    .setParameter("c", compteId)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    public Groupe save(Groupe g) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if (g.getId() == null) {
                em.persist(g);
            } else {
                g = em.merge(g);
            }
            em.getTransaction().commit();
            return g;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
