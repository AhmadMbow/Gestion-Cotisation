package sn.association.cotisations.util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import sn.association.cotisations.dao.CompteDAO;
import sn.association.cotisations.dao.GroupeDAO;
import sn.association.cotisations.dao.MembreDAO;
import sn.association.cotisations.entity.Compte;
import sn.association.cotisations.entity.Forfait;
import sn.association.cotisations.entity.Groupe;
import sn.association.cotisations.entity.Membre;
import sn.association.cotisations.entity.Role;
import sn.association.cotisations.service.GroupeService;

import java.security.SecureRandom;
import java.time.LocalDate;

/**
 * Initialise et ferme proprement l'EntityManagerFactory au cycle de vie de l'application.
 * Migre une ancienne base vers le modèle multi-groupes, puis amorce un compte administrateur
 * et le groupe historique si la base est vide (premier déploiement sur une base vierge).
 */
@WebListener
public class AppLifecycleListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Initialise l'EMF (déclenche aussi la création/maj du schéma via Hibernate)
        JPAUtil.getEntityManagerFactory();
        sce.getServletContext().log("EntityManagerFactory initialisé.");
        MigrationMultiGroupes.executer();
        amorcerAdminSiBaseVide(sce);
        KeepAlive.demarrer();
    }

    /**
     * Si aucun compte n'existe encore, crée un administrateur par défaut, propriétaire du
     * groupe historique (forfait Illimité).
     * Identifiants surchargés par variables d'environnement ADMIN_EMAIL / ADMIN_PASSWORD,
     * sinon valeurs par défaut (admin@asso.sn / admin123) — à changer après la 1re connexion.
     */
    private void amorcerAdminSiBaseVide(ServletContextEvent sce) {
        try {
            CompteDAO comptes = new CompteDAO();
            if (comptes.count() > 0) {
                return; // la base contient déjà des comptes : rien à faire
            }
            String email = envOuDefaut("ADMIN_EMAIL", "admin@asso.sn");
            Compte c = new Compte();
            c.setPrenom("Admin");
            c.setNom("Système");
            c.setEmail(email.trim().toLowerCase());
            c.setMotDePasse(PasswordUtil.hash(envOuDefaut("ADMIN_PASSWORD", "admin123")));
            c.setForfait(Forfait.ILLIMITE);
            c.setForfaitExpireLe(LocalDate.of(2099, 12, 31));
            c = comptes.save(c);

            Groupe g = new Groupe();
            g.setNom(MigrationMultiGroupes.nomGroupeHistorique());
            g.setCodeInvitation(GroupeService.genererCode(new SecureRandom()));
            g.setProprietaire(c);
            g = new GroupeDAO().save(g);
            new MembreDAO().save(new Membre(c, g, Role.ADMIN, LocalDate.now()));
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
        KeepAlive.arreter();
        JPAUtil.close();
        sce.getServletContext().log("EntityManagerFactory fermé.");
    }
}
