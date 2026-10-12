package sn.association.cotisations.servlet;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sn.association.cotisations.dao.AmendeDAO;
import sn.association.cotisations.dao.ConnexionDAO;
import sn.association.cotisations.dao.CotisationDAO;
import sn.association.cotisations.dao.MembreDAO;
import sn.association.cotisations.entity.Amende;
import sn.association.cotisations.entity.Compte;
import sn.association.cotisations.entity.Connexion;
import sn.association.cotisations.entity.Cotisation;
import sn.association.cotisations.entity.Membre;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Sauvegarde SQL des données du groupe historique (comptes, membres, cotisations,
 * amendes, connexions), téléchargeable. GET /admin/backup → application/sql en attachment.
 *
 * La base est partagée avec les autres groupes : la restauration utilise INSERT IGNORE
 * (elle remet ce qui manque) et ne vide jamais de table.
 */
@WebServlet(name = "BackupServlet", urlPatterns = {"/admin/backup"})
public class BackupServlet extends HttpServlet {

    @Inject MembreDAO membreDAO;
    @Inject CotisationDAO cotisationDAO;
    @Inject AmendeDAO amendeDAO;
    @Inject ConnexionDAO connexionDAO;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String filename = "backup-cotisations-" + LocalDate.now() + ".sql";
        resp.setContentType("application/sql; charset=UTF-8");
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        try (PrintWriter w = resp.getWriter()) {
            w.println("-- =========================================================");
            w.println("-- Sauvegarde Gestion Cotisations");
            w.println("-- Générée le " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            w.println("-- =========================================================");
            w.println("SET FOREIGN_KEY_CHECKS=0;");
            w.println("SET NAMES utf8mb4;");
            w.println();

            List<Membre> membres = membreDAO.findAll();
            dumpComptes(w, membres);
            dumpMembres(w, membres);
            dumpCotisations(w, cotisationDAO.findAll());
            dumpAmendes(w, amendeDAO.findAll());
            dumpConnexions(w, connexionDAO.findAll());

            w.println("SET FOREIGN_KEY_CHECKS=1;");
            w.println("-- Fin de la sauvegarde.");
        }
    }

    private void dumpComptes(PrintWriter w, List<Membre> list) {
        w.println("-- ---------- Comptes (" + list.size() + ") ----------");
        for (Membre m : list) {
            Compte c = m.getCompte();
            w.printf(
                "INSERT IGNORE INTO compte (id, prenom, nom, email, telephone, date_naissance, mot_de_passe, "
              + "date_creation, forfait, forfait_expire_le) VALUES (%d, %s, %s, %s, %s, %s, %s, %s, %s, %s);%n",
                c.getId(),
                sql(c.getPrenom()), sql(c.getNom()), sql(c.getEmail()), sql(c.getTelephone()),
                sql(c.getDateNaissance()), sql(c.getMotDePasse()), sql(c.getDateCreation()),
                sql(c.getForfait().name()), sql(c.getForfaitExpireLe()));
        }
        w.println();
    }

    private void dumpMembres(PrintWriter w, List<Membre> list) {
        w.println("-- ---------- Membres (" + list.size() + ") ----------");
        for (Membre m : list) {
            w.printf(
                "INSERT IGNORE INTO membre (numero, compte_id, groupe_id, date_adhesion, statut, role) "
              + "VALUES (%d, %d, %d, %s, %s, %s);%n",
                m.getNumero(), m.getCompte().getId(), m.getGroupe().getId(),
                sql(m.getDateAdhesion()), sql(m.getStatut().name()), sql(m.getRole().name()));
        }
        w.println();
    }

    private void dumpCotisations(PrintWriter w, List<Cotisation> list) {
        w.println("-- ---------- Cotisations (" + list.size() + ") ----------");
        for (Cotisation c : list) {
            w.printf(
                "INSERT IGNORE INTO cotisation (id, membre_numero, montant, date_paiement, mois, annee, mode_paiement, statut) "
              + "VALUES (%d, %d, %s, %s, %d, %d, %s, %s);%n",
                c.getId(),
                c.getMembre().getNumero(),
                c.getMontant().toPlainString(),
                sql(c.getDatePaiement()),
                c.getMois(), c.getAnnee(),
                sql(c.getModePaiement().name()),
                sql(c.getStatut().name()));
        }
        w.println();
    }

    private void dumpAmendes(PrintWriter w, List<Amende> list) {
        w.println("-- ---------- Amendes (" + list.size() + ") ----------");
        for (Amende a : list) {
            w.printf(
                "INSERT IGNORE INTO amende (id, membre_numero, montant, date_generation, statut_paiement) "
              + "VALUES (%d, %d, %s, %s, %s);%n",
                a.getId(),
                a.getMembre().getNumero(),
                a.getMontant().toPlainString(),
                sql(a.getDateGeneration()),
                sql(a.getStatutPaiement().name()));
        }
        w.println();
    }

    private void dumpConnexions(PrintWriter w, List<Connexion> list) {
        w.println("-- ---------- Connexions (" + list.size() + ") ----------");
        for (Connexion c : list) {
            w.printf(
                "INSERT IGNORE INTO connexion (id, compte_id, date_connexion, ip, user_agent) "
              + "VALUES (%d, %d, %s, %s, %s);%n",
                c.getId(),
                c.getCompte().getId(),
                sql(c.getDateConnexion()),
                sql(c.getIp()),
                sql(c.getUserAgent()));
        }
        w.println();
    }

    // ----- Helpers d'échappement -----
    private static String sql(String s) {
        if (s == null) return "NULL";
        return "'" + s.replace("\\", "\\\\").replace("'", "''") + "'";
    }

    private static String sql(LocalDate d) {
        return d == null ? "NULL" : "'" + d + "'";
    }

    private static String sql(LocalDateTime dt) {
        return dt == null ? "NULL" : "'" + dt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "'";
    }
}
