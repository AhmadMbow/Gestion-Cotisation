package sn.association.cotisations.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import sn.association.cotisations.entity.ApiToken;
import sn.association.cotisations.util.JPAUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ApiTokenDAO {

    public ApiToken save(ApiToken t) {
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

    public Optional<ApiToken> findByHash(String tokenHash) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<ApiToken> r = em.createQuery(
                    "SELECT t FROM ApiToken t JOIN FETCH t.membre WHERE t.tokenHash = :h", ApiToken.class)
                    .setParameter("h", tokenHash)
                    .setMaxResults(1)
                    .getResultList();
            return r.isEmpty() ? Optional.empty() : Optional.of(r.get(0));
        } finally {
            em.close();
        }
    }

    public void deleteByHash(String tokenHash) {
        executeUpdate("DELETE FROM ApiToken t WHERE t.tokenHash = :h", "h", tokenHash);
    }

    /** Déconnecte tous les appareils d'un membre (désactivation, nouveau mot de passe…). */
    public void deleteByMembre(Integer membreNumero) {
        executeUpdate("DELETE FROM ApiToken t WHERE t.membre.numero = :n", "n", membreNumero);
    }

    public void deleteExpired() {
        executeUpdate("DELETE FROM ApiToken t WHERE t.expireAt < :now", "now", LocalDateTime.now());
    }

    private void executeUpdate(String jpql, String param, Object value) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery(jpql).setParameter(param, value).executeUpdate();
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
