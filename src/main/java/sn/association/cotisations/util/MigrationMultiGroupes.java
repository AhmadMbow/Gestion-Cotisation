package sn.association.cotisations.util;

import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sn.association.cotisations.entity.Groupe;
import sn.association.cotisations.service.GroupeService;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Passage d'une base « une seule association » au modèle multi-groupes (MySQL).
 *
 * Exécutée au démarrage, après la mise à jour du schéma par Hibernate (qui a déjà créé
 * les tables compte / groupe et les nouvelles colonnes). Ne fait rien si la base est
 * neuve ou déjà migrée (la colonne membre.email n'existe plus).
 *
 * 0. sauvegarde : copie de chaque table de l'ancien modèle dans « avant_saas_<table> »
 *    (même base), pour pouvoir revenir en arrière ;
 * 1. un compte par membre (identité + mot de passe) ;
 * 2. le groupe historique n° 1 (montants de l'ancienne table parametre), dont le premier
 *    administrateur est propriétaire avec le forfait Illimité ;
 * 3. membres, notifications, jetons, connexions et liens de réinitialisation rattachés
 *    au groupe ou au compte ;
 * 4. suppression des anciennes colonnes (après la copie, dans cet ordre).
 */
public final class MigrationMultiGroupes {

    private static final Logger log = LoggerFactory.getLogger(MigrationMultiGroupes.class);

    private MigrationMultiGroupes() {
    }

    public static void executer() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.unwrap(Session.class).doWork(conn -> {
                if (!colonneExiste(conn, "membre", "email")) return;
                log.info("Migration vers le modèle multi-groupes…");
                sauvegarder(conn);
                copierDonnees(conn);
                supprimerAnciennesColonnes(conn);
                log.info("Migration multi-groupes terminée.");
            });
        } finally {
            em.close();
        }
    }

    /** Tables touchées par la migration (et celles qui en dépendent). */
    private static final List<String> TABLES_ANCIEN_MODELE = List.of(
            "membre", "cotisation", "amende", "notification", "api_token", "connexion",
            "password_reset_token", "parametre");

    /**
     * Copie intégrale de l'ancien modèle avant toute modification. CREATE TABLE … LIKE garde
     * la clé primaire (exigée par certains hébergeurs MySQL, dont Aiven) ; une copie déjà
     * présente n'est jamais écrasée.
     */
    private static void sauvegarder(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {
            for (String table : TABLES_ANCIEN_MODELE) {
                String copie = "avant_saas_" + table;
                if (!tableExiste(conn, table) || tableExiste(conn, copie)) continue;
                st.executeUpdate("CREATE TABLE " + copie + " LIKE " + table);
                int n = st.executeUpdate("INSERT INTO " + copie + " SELECT * FROM " + table);
                log.info("Sauvegarde : {} → {} ({} lignes).", table, copie, n);
            }
        }
    }

    private static void copierDonnees(Connection conn) throws SQLException {
        boolean auto = conn.getAutoCommit();
        conn.setAutoCommit(false);
        try (Statement st = conn.createStatement()) {
            // 1. Comptes
            st.executeUpdate(
                    "INSERT INTO compte (prenom, nom, email, telephone, date_naissance, mot_de_passe, "
                    + "date_creation, forfait) "
                    + "SELECT m.prenom, m.nom, m.email, m.telephone, m.date_naissance, m.mot_de_passe, NOW(), 'GRATUIT' "
                    + "FROM membre m WHERE NOT EXISTS (SELECT 1 FROM compte c WHERE c.email = m.email)");
            st.executeUpdate("UPDATE membre m JOIN compte c ON c.email = m.email "
                    + "SET m.compte_id = c.id WHERE m.compte_id IS NULL");

            // 2. Groupe historique
            if (!existe(conn, "SELECT 1 FROM groupe WHERE id = " + Groupe.HISTORIQUE)) {
                BigDecimal cotisation = new BigDecimal("5000");
                BigDecimal amende = new BigDecimal("2000");
                if (tableExiste(conn, "parametre")) {
                    try (ResultSet rs = st.executeQuery(
                            "SELECT montant_cotisation, montant_amende FROM parametre WHERE id = 1")) {
                        if (rs.next()) {
                            cotisation = rs.getBigDecimal(1);
                            amende = rs.getBigDecimal(2);
                        }
                    }
                }
                Integer proprietaire = null;
                try (ResultSet rs = st.executeQuery(
                        "SELECT compte_id FROM membre WHERE role = 'ADMIN' ORDER BY numero LIMIT 1")) {
                    if (rs.next()) proprietaire = rs.getInt(1);
                }
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO groupe (id, nom, code_invitation, montant_cotisation, montant_amende, "
                        + "proprietaire_id, date_creation, actif) VALUES (?, ?, ?, ?, ?, ?, NOW(), 1)")) {
                    ps.setInt(1, Groupe.HISTORIQUE);
                    ps.setString(2, nomGroupeHistorique());
                    ps.setString(3, GroupeService.genererCode(new SecureRandom()));
                    ps.setBigDecimal(4, cotisation);
                    ps.setBigDecimal(5, amende);
                    ps.setObject(6, proprietaire);
                    ps.executeUpdate();
                }
                // L'association d'origine n'est pas limitée par les forfaits.
                if (proprietaire != null) {
                    st.executeUpdate("UPDATE compte SET forfait = 'ILLIMITE', forfait_expire_le = '2099-12-31' "
                            + "WHERE id = " + proprietaire);
                }
            }

            // 3. Rattachements
            st.executeUpdate("UPDATE membre SET groupe_id = " + Groupe.HISTORIQUE + " WHERE groupe_id IS NULL");
            st.executeUpdate("UPDATE notification SET groupe_id = " + Groupe.HISTORIQUE + " WHERE groupe_id IS NULL");
            for (String table : List.of("api_token", "connexion", "password_reset_token")) {
                if (colonneExiste(conn, table, "membre_numero")) {
                    st.executeUpdate("UPDATE " + table + " t JOIN membre m ON m.numero = t.membre_numero "
                            + "SET t.compte_id = m.compte_id WHERE t.compte_id IS NULL");
                }
            }
            conn.commit();
        } catch (SQLException | RuntimeException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(auto);
        }
    }

    /** Les données sont copiées : on retire les colonnes de l'ancien modèle. */
    private static void supprimerAnciennesColonnes(Connection conn) throws SQLException {
        for (String table : List.of("api_token", "connexion", "password_reset_token")) {
            supprimerColonne(conn, table, "membre_numero");
        }
        for (String colonne : List.of("prenom", "nom", "email", "telephone", "date_naissance", "mot_de_passe")) {
            supprimerColonne(conn, "membre", colonne);
        }
    }

    private static void supprimerColonne(Connection conn, String table, String colonne) throws SQLException {
        if (!colonneExiste(conn, table, colonne)) return;
        List<String> cles = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT CONSTRAINT_NAME FROM information_schema.KEY_COLUMN_USAGE "
                + "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = ? "
                + "AND REFERENCED_TABLE_NAME IS NOT NULL")) {
            ps.setString(1, table);
            ps.setString(2, colonne);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) cles.add(rs.getString(1));
            }
        }
        try (Statement st = conn.createStatement()) {
            for (String cle : cles) st.executeUpdate("ALTER TABLE " + table + " DROP FOREIGN KEY `" + cle + "`");
            st.executeUpdate("ALTER TABLE " + table + " DROP COLUMN " + colonne);
        }
        log.info("Colonne {}.{} supprimée.", table, colonne);
    }

    private static boolean colonneExiste(Connection conn, String table, String colonne) throws SQLException {
        DatabaseMetaData md = conn.getMetaData();
        try (ResultSet rs = md.getColumns(conn.getCatalog(), null, table, colonne)) {
            return rs.next();
        }
    }

    private static boolean tableExiste(Connection conn, String table) throws SQLException {
        DatabaseMetaData md = conn.getMetaData();
        try (ResultSet rs = md.getTables(conn.getCatalog(), null, table, new String[]{"TABLE"})) {
            return rs.next();
        }
    }

    private static boolean existe(Connection conn, String sql) throws SQLException {
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            return rs.next();
        }
    }

    static String nomGroupeHistorique() {
        String v = System.getenv("GROUPE_HISTORIQUE_NOM");
        return v != null && !v.isBlank() ? v.trim() : "Association Cotisen";
    }
}
