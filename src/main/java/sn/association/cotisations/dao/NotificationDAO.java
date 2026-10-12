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

    /** Notifications destinées aux administrateurs du groupe. */
    public List<Notification> findPourAdmins(int groupe) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT n FROM Notification n WHERE n.membre IS NULL AND n.groupe.id = :g " +
                    "ORDER BY n.dateCreation DESC, n.id DESC", Notification.class)
                    .setParameter("g", groupe)
                    .setMaxResults(LIMITE)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /** Marque comme lues les notifications d'un membre. */
    public void marquerLues(Integer membreNumero) {
        executer("UPDATE Notification n SET n.lue = true WHERE n.membre.numero = :p", membreNumero);
    }

    /** Marque comme lues les notifications des administrateurs du groupe. */
    public void marquerLuesPourAdmins(int groupe) {
        executer("UPDATE Notification n SET n.lue = true WHERE n.membre IS NULL AND n.groupe.id = :p", groupe);
    }

    private void executer(String jpql, Object param) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery(jpql).setParameter("p", param).executeUpdate();
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
