package sn.association.cotisations.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import sn.association.cotisations.entity.Notification;
import sn.association.cotisations.util.JPAUtil;

import java.util.List;

@ApplicationScoped
public class NotificationDAO {

    private static final int LIMITE = 60;

    public Notification save(Notification n) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if (n.getId() == null) em.persist(n);
            else n = em.merge(n);
            em.getTransaction().commit();
            return n;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    /** Notifications d'un membre, les plus récentes d'abord. */
    public List<Notification> findByMembre(Integer membreNumero) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT n FROM Notification n WHERE n.membre.numero = :n " +
                    "ORDER BY n.dateCreation DESC, n.id DESC", Notification.class)
                    .setParameter("n", membreNumero)
                    .setMaxResults(LIMITE)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /** Notifications destinées aux administrateurs. */
    public List<Notification> findPourAdmins() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT n FROM Notification n WHERE n.membre IS NULL " +
                    "ORDER BY n.dateCreation DESC, n.id DESC", Notification.class)
                    .setMaxResults(LIMITE)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /** Marque comme lues les notifications d'un membre, ou celles des admins si null. */
    public void marquerLues(Integer membreNumero) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if (membreNumero == null) {
                em.createQuery("UPDATE Notification n SET n.lue = true WHERE n.membre IS NULL")
                        .executeUpdate();
            } else {
                em.createQuery("UPDATE Notification n SET n.lue = true WHERE n.membre.numero = :n")
                        .setParameter("n", membreNumero)
                        .executeUpdate();
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
