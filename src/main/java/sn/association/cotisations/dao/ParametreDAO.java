package sn.association.cotisations.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import sn.association.cotisations.entity.Parametre;
import sn.association.cotisations.util.JPAUtil;

@ApplicationScoped
public class ParametreDAO {

    /**
     * Retourne la ligne unique de paramètres ; la crée avec les valeurs par défaut
     * si elle n'existe pas encore (premier lancement).
     */
    public Parametre getOuCreer() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Parametre p = em.find(Parametre.class, 1);
            if (p == null) {
                em.getTransaction().begin();
                p = new Parametre();
                em.persist(p);
                em.getTransaction().commit();
            }
            return p;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Parametre save(Parametre p) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            p = em.merge(p);
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
