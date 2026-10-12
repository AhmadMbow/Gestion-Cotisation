package sn.association.cotisations.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import sn.association.cotisations.entity.Compte;
import sn.association.cotisations.util.JPAUtil;

import java.util.Optional;

@ApplicationScoped
public class CompteDAO {

    public Optional<Compte> findById(Integer id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return Optional.ofNullable(em.find(Compte.class, id));
        } finally {
            em.close();
        }
    }

    /** Recherche par e-mail (déjà normalisé en minuscules). */
    public Optional<Compte> findByEmail(String email) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT c FROM Compte c WHERE c.email = :e", Compte.class)
                    .setParameter("e", email)
                    .getResultStream()
                    .findFirst();
        } finally {
            em.close();
        }
    }

    public long count() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT COUNT(c) FROM Compte c", Long.class).getSingleResult();
        } finally {
            em.close();
        }
    }

    public Compte save(Compte c) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if (c.getId() == null) {
                em.persist(c);
            } else {
                c = em.merge(c);
            }
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
