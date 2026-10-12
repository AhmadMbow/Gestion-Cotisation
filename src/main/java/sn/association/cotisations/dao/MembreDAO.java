package sn.association.cotisations.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import sn.association.cotisations.entity.Groupe;
import sn.association.cotisations.entity.Membre;
import sn.association.cotisations.entity.StatutMembre;
import sn.association.cotisations.util.JPAUtil;

import java.util.List;
import java.util.Optional;

/**
 * Adhésions (membres d'un groupe). Les méthodes sans groupe concernent le groupe
 * historique, géré par l'interface web.
 */
@ApplicationScoped
public class MembreDAO {

    public Optional<Membre> findById(Integer numero) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return Optional.ofNullable(em.find(Membre.class, numero));
        } finally {
            em.close();
        }
    }

    /** Adhésion d'un compte à un groupe. */
    public Optional<Membre> findByCompteEtGroupe(Integer compteId, int groupe) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT m FROM Membre m WHERE m.compte.id = :c AND m.groupe.id = :g", Membre.class)
                    .setParameter("c", compteId)
                    .setParameter("g", groupe)
                    .getResultStream()
                    .findFirst();
        } finally {
            em.close();
        }
    }

    /** Toutes les adhésions d'un compte (groupes actifs), les plus anciennes d'abord. */
    public List<Membre> findByCompte(Integer compteId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT m FROM Membre m WHERE m.compte.id = :c AND m.groupe.actif = true " +
                    "ORDER BY m.numero", Membre.class)
                    .setParameter("c", compteId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Membre> findAll() {
        return findByGroupe(Groupe.HISTORIQUE);
    }

    public List<Membre> findByGroupe(int groupe) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT m FROM Membre m WHERE m.groupe.id = :g ORDER BY m.compte.nom, m.compte.prenom",
                    Membre.class)
                    .setParameter("g", groupe)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Membre> findActifs() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT m FROM Membre m WHERE m.statut = :statut AND m.groupe.id = :g " +
                    "ORDER BY m.compte.nom", Membre.class)
                    .setParameter("statut", StatutMembre.ACTIF)
                    .setParameter("g", Groupe.HISTORIQUE)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Membre> findEnRetardPourMois(int mois, int annee) {
        return findEnRetardPourMois(Groupe.HISTORIQUE, mois, annee);
    }

    /**
     * Membres ACTIFS (rôle MEMBRE) du groupe qui n'ont pas payé leur cotisation pour le mois donné.
     */
    public List<Membre> findEnRetardPourMois(int groupe, int mois, int annee) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT m FROM Membre m WHERE m.statut = sn.association.cotisations.entity.StatutMembre.ACTIF " +
                    "AND m.role = sn.association.cotisations.entity.Role.MEMBRE AND m.groupe.id = :g " +
                    "AND NOT EXISTS (SELECT 1 FROM Cotisation c " +
                    "                WHERE c.membre = m AND c.mois = :m AND c.annee = :a " +
                    "                AND c.statut = sn.association.cotisations.entity.StatutCotisation.PAYE) " +
                    "ORDER BY m.compte.nom, m.compte.prenom", Membre.class)
                    .setParameter("g", groupe)
                    .setParameter("m", mois)
                    .setParameter("a", annee)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public long countActifs() {
        return countActifs(Groupe.HISTORIQUE);
    }

    /** Membres actifs du groupe (administrateurs compris) : sert aux limites des forfaits. */
    public long countActifs(int groupe) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT COUNT(m) FROM Membre m WHERE m.statut = :statut AND m.groupe.id = :g", Long.class)
                    .setParameter("statut", StatutMembre.ACTIF)
                    .setParameter("g", groupe)
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
