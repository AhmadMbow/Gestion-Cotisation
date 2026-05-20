package sn.association.cotisations.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Singleton qui expose l'EntityManagerFactory.
 * Un EMF est coûteux à créer, on en garde un seul pour toute l'application.
 */
public final class JPAUtil {

    private static final String PERSISTENCE_UNIT_NAME = "cotisationsPU";
    private static EntityManagerFactory emf;

    private JPAUtil() {
    }

    public static synchronized EntityManagerFactory getEntityManagerFactory() {
        if (emf == null || !emf.isOpen()) {
            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        }
        return emf;
    }

    public static EntityManager getEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
