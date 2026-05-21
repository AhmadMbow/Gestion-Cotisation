package sn.association.cotisations.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton qui expose l'EntityManagerFactory.
 * Un EMF est coûteux à créer, on en garde un seul pour toute l'application.
 *
 * Les paramètres JDBC peuvent être surchargés par variables d'environnement
 * (utile pour ne pas commiter les credentials dans persistence.xml) :
 *   DB_URL, DB_USER, DB_PASSWORD
 */
public final class JPAUtil {

    private static final String PERSISTENCE_UNIT_NAME = "cotisationsPU";
    private static EntityManagerFactory emf;

    private JPAUtil() {
    }

    public static synchronized EntityManagerFactory getEntityManagerFactory() {
        if (emf == null || !emf.isOpen()) {
            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, jdbcOverrides());
        }
        return emf;
    }

    private static Map<String, Object> jdbcOverrides() {
        Map<String, Object> props = new HashMap<>();
        putIfSet(props, "jakarta.persistence.jdbc.url",      System.getenv("DB_URL"));
        putIfSet(props, "jakarta.persistence.jdbc.user",     System.getenv("DB_USER"));
        putIfSet(props, "jakarta.persistence.jdbc.password", System.getenv("DB_PASSWORD"));
        return props;
    }

    private static void putIfSet(Map<String, Object> props, String key, String value) {
        if (value != null && !value.isBlank()) props.put(key, value);
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
