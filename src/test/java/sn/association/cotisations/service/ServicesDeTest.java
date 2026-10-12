package sn.association.cotisations.service;

import sn.association.cotisations.dao.CompteDAO;
import sn.association.cotisations.dao.CotisationDAO;
import sn.association.cotisations.dao.GroupeDAO;
import sn.association.cotisations.dao.MembreDAO;
import sn.association.cotisations.dao.NotificationDAO;
import sn.association.cotisations.dao.PaiementAbonnementDAO;
import sn.association.cotisations.entity.Compte;
import sn.association.cotisations.entity.Forfait;
import sn.association.cotisations.entity.Groupe;
import sn.association.cotisations.entity.Membre;
import sn.association.cotisations.entity.Role;

import java.security.SecureRandom;
import java.time.LocalDate;

/**
 * Services câblés à la main pour les tests d'intégration (pas de conteneur CDI),
 * sans envoi d'e-mail. Le groupe historique est créé au premier appel.
 */
public final class ServicesDeTest {

    public final CompteService comptes = new CompteService();
    public final GroupeService groupes = new GroupeService();
    public final MembreService membres = new MembreService();
    public final CotisationService cotisations = new CotisationService();
    public final AbonnementService abonnements = new AbonnementService();
    public final ParametreService parametres = new ParametreService();

    public ServicesDeTest() {
        CompteDAO compteDAO = new CompteDAO();
        GroupeDAO groupeDAO = new GroupeDAO();
        MembreDAO membreDAO = new MembreDAO();

        NotificationService notifications = new NotificationService();
        notifications.notificationDAO = new NotificationDAO();
        notifications.mailService = new MailService() {
            @Override
            public void sendAsync(String to, String subject, String text, String html) {
            }
        };

        comptes.compteDAO = compteDAO;
        parametres.groupeDAO = groupeDAO;
        groupes.groupeDAO = groupeDAO;
        groupes.membreDAO = membreDAO;
        groupes.notificationService = notifications;
        membres.membreDAO = membreDAO;
        membres.compteService = comptes;
        membres.groupeService = groupes;
        membres.parametreService = parametres;
        cotisations.cotisationDAO = new CotisationDAO();
        cotisations.membreDAO = membreDAO;
        cotisations.parametreService = parametres;
        abonnements.compteDAO = compteDAO;
        abonnements.groupeDAO = groupeDAO;
        abonnements.paiementDAO = new PaiementAbonnementDAO();
        abonnements.notificationService = notifications;

        if (groupeDAO.findById(Groupe.HISTORIQUE).isEmpty()) creerGroupeHistorique(compteDAO, groupeDAO, membreDAO);
    }

    private static void creerGroupeHistorique(CompteDAO compteDAO, GroupeDAO groupeDAO, MembreDAO membreDAO) {
        Compte c = new Compte();
        c.setPrenom("Admin");
        c.setNom("Système");
        c.setEmail("admin-historique@asso.sn");
        c.setMotDePasse("x");
        c.setForfait(Forfait.ILLIMITE); // comme après la migration
        c.setForfaitExpireLe(LocalDate.of(2099, 12, 31));
        c = compteDAO.save(c);
        Groupe g = new Groupe();
        g.setNom("Association Cotisen");
        g.setCodeInvitation(GroupeService.genererCode(new SecureRandom()));
        g.setProprietaire(c);
        g = groupeDAO.save(g);
        membreDAO.save(new Membre(c, g, Role.ADMIN, LocalDate.now()));
    }
}
