package sn.association.cotisations.util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Initialise et ferme proprement l'EntityManagerFactory au cycle de vie de l'application.
 */
@WebListener
public class AppLifecycleListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        JPAUtil.getEntityManagerFactory();
        sce.getServletContext().log("EntityManagerFactory initialisé.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        JPAUtil.close();
        sce.getServletContext().log("EntityManagerFactory fermé.");
    }
}
