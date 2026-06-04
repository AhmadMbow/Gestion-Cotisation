package sn.association.cotisations.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import sn.association.cotisations.entity.Membre;
import sn.association.cotisations.entity.StatutMembre;
import sn.association.cotisations.util.JPAUtil;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MembreDAO {

    public Optional<Membre> findByEmail(String email) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Membre> q = em.createQuery(
                    "SELECT m FROM Membre m WHERE m.email = :email", Membre.class);
            q.setParameter("email", email);
            return Optional.of(q.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    public Optional<Membre> findById(Integer numero) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return Optional.ofNullable(em.find(Membre.class, numero));
        } finally {
            em.close();
        }
    }

    public List<Membre> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT m FROM Membre m ORDER BY m.nom, m.prenom", Membre.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Membre> findActifs() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT m FROM Membre m WHERE m.statut = :statut ORDER BY m.nom", Membre.class)
                    .setParameter("statut", StatutMembre.ACTIF)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Membres ACTIFS (rôle MEMBRE) qui n'ont pas payé leur cotisation pour le mois donné.
     */
    public List<Membre> findEnRetardPourMois(int mois, int annee) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT m FROM Membre m WHERE m.statut = sn.association.cotisations.entity.StatutMembre.ACTIF " +
                    "AND m.role = sn.association.cotisations.entity.Role.MEMBRE " +
                    "AND NOT EXISTS (SELECT 1 FROM Cotisation c " +
                    "                WHERE c.membre = m AND c.mois = :m AND c.annee = :a " +
                    "                AND c.statut = sn.association.cotisations.entity.StatutCotisation.PAYE) " +
                    "ORDER BY m.nom, m.prenom", Membre.class)
                    .setParameter("m", mois)
                    .setParameter("a", annee)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public long countActifs() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT COUNT(m) FROM Membre m WHERE m.statut = :statut", Long.class)
                    .setParameter("statut", StatutMembre.ACTIF)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    public Membre save(Membre membre) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if (membre.getNumero() == null) {
                em.persist(membre);
            } else {
                membre = em.merge(membre);
            }
            em.getTransaction().commit();
            return membre;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void delete(Integer numero) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Membre m = em.find(Membre.class, numero);
            if (m != null) em.remove(m);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
