package sn.association.cotisations.util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import sn.association.cotisations.dao.MembreDAO;
import sn.association.cotisations.entity.Role;
import sn.association.cotisations.service.MembreService;

/**
 * Initialise et ferme proprement l'EntityManagerFactory au cycle de vie de l'application.
 * Amorce aussi un compte administrateur par défaut si la base est vide (utile lors d'un
 * premier déploiement sur une base managée vierge, ex. Aiven).
 */
@WebListener
public class AppLifecycleListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Initialise l'EMF (déclenche aussi la création/maj du schéma via Hibernate)
        JPAUtil.getEntityManagerFactory();
        sce.getServletContext().log("EntityManagerFactory initialisé.");
        amorcerAdminSiBaseVide(sce);
    }

    /**
     * Si aucun membre n'existe encore, crée un administrateur par défaut.
     * Identifiants surchargés par variables d'environnement ADMIN_EMAIL / ADMIN_PASSWORD,
     * sinon valeurs par défaut (admin@asso.sn / admin123) — à changer après la 1re connexion.
     */
    private void amorcerAdminSiBaseVide(ServletContextEvent sce) {
        try {
            MembreDAO dao = new MembreDAO();
            if (!dao.findAll().isEmpty()) {
                return; // la base contient déjà des membres : rien à faire
            }
            String email = envOuDefaut("ADMIN_EMAIL", "admin@asso.sn");
            String motDePasse = envOuDefaut("ADMIN_PASSWORD", "admin123");
            new MembreService().creer("Admin", "Système", email, null, Role.ADMIN, motDePasse);
            sce.getServletContext().log("Compte admin par défaut créé : " + email);
        } catch (RuntimeException e) {
            // Ne pas empêcher le démarrage si l'amorçage échoue
            sce.getServletContext().log("Amorçage admin ignoré : " + e.getMessage());
        }
    }

    private String envOuDefaut(String cle, String defaut) {
        String v = System.getenv(cle);
        return (v != null && !v.isBlank()) ? v : defaut;
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        JPAUtil.close();
        sce.getServletContext().log("EntityManagerFactory fermé.");
    }
}
