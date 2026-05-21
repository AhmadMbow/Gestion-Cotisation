package sn.association.cotisations.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import sn.association.cotisations.entity.PasswordResetToken;
import sn.association.cotisations.util.JPAUtil;

import java.time.LocalDateTime;
import java.util.Optional;

@ApplicationScoped
public class PasswordResetTokenDAO {

    public PasswordResetToken save(PasswordResetToken t) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if (t.getId() == null) em.persist(t);
            else t = em.merge(t);
            em.getTransaction().commit();
            return t;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Optional<PasswordResetToken> findByToken(String token) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return Optional.of(em.createQuery(
                            "SELECT t FROM PasswordResetToken t JOIN FETCH t.membre WHERE t.token = :tok",
                            PasswordResetToken.class)
                    .setParameter("tok", token)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    public void markUsed(Integer id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            PasswordResetToken t = em.find(PasswordResetToken.class, id);
            if (t != null) {
                t.setUsedAt(LocalDateTime.now());
                em.merge(t);
            }
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
