package sn.association.cotisations.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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

    private static String unitName = "cotisationsPU";
    private static EntityManagerFactory emf;

    private JPAUtil() {
    }

    /**
     * Permet aux tests d'intégration de basculer sur un autre persistence-unit (ex: H2 en mémoire).
     * Doit être appelé AVANT toute utilisation de l'EMF — ferme l'EMF actuel si déjà ouvert.
     */
    public static synchronized void setUnitName(String name) {
        if (emf != null && emf.isOpen()) emf.close();
        emf = null;
        unitName = name;
    }

    public static synchronized EntityManagerFactory getEntityManagerFactory() {
        if (emf == null || !emf.isOpen()) {
            emf = Persistence.createEntityManagerFactory(unitName, jdbcOverrides());
        }
        return emf;
    }

    private static Map<String, Object> jdbcOverrides() {
        Map<String, Object> props = new HashMap<>();
        putIfSet(props, "jakarta.persistence.jdbc.url",      System.getenv("DB_URL"));
        putIfSet(props, "jakarta.persistence.jdbc.user",     System.getenv("DB_USER"));
        putIfSet(props, "jakarta.persistence.jdbc.password", System.getenv("DB_PASSWORD"));
        // DATABASE_URL (« mysql://user:mdp@hôte:port/base?ssl-mode=REQUIRED », format
        // donné par Aiven et la plupart des hébergeurs) l'emporte s'il est défini.
        String databaseUrl = System.getenv("DATABASE_URL");
        if (databaseUrl != null && !databaseUrl.isBlank()) {
            props.putAll(depuisDatabaseUrl(databaseUrl.trim()));
        }
        return props;
    }

    /** Convertit une URL « mysql://… » en paramètres JDBC (URL, utilisateur, mot de passe). */
    static Map<String, Object> depuisDatabaseUrl(String databaseUrl) {
        URI uri = URI.create(databaseUrl);
        if (!"mysql".equalsIgnoreCase(uri.getScheme())) {
            throw new IllegalArgumentException("DATABASE_URL doit commencer par mysql://");
        }
        String base = uri.getPath() == null || uri.getPath().length() <= 1
                ? "defaultdb" : uri.getPath().substring(1);
        String query = uri.getRawQuery() == null ? "" : uri.getRawQuery();
        boolean ssl = query.contains("ssl-mode=REQUIRED") || query.contains("sslmode=require")
                || query.contains("ssl-mode=VERIFY");
        String jdbc = "jdbc:mysql://" + uri.getHost() + ":" + (uri.getPort() > 0 ? uri.getPort() : 3306)
                + "/" + base + "?serverTimezone=UTC&characterEncoding=UTF-8"
                + (ssl ? "&sslMode=REQUIRED" : "&useSSL=false&allowPublicKeyRetrieval=true");

        Map<String, Object> props = new HashMap<>();
        props.put("jakarta.persistence.jdbc.url", jdbc);
        String info = uri.getRawUserInfo();
        if (info != null) {
            int i = info.indexOf(':');
            props.put("jakarta.persistence.jdbc.user", decode(i < 0 ? info : info.substring(0, i)));
            if (i >= 0) props.put("jakarta.persistence.jdbc.password", decode(info.substring(i + 1)));
        }
        return props;
    }

    private static String decode(String s) {
        return URLDecoder.decode(s, StandardCharsets.UTF_8);
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
