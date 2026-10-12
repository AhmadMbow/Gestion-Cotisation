package sn.association.cotisations.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sn.association.cotisations.entity.Compte;
import sn.association.cotisations.entity.Forfait;
import sn.association.cotisations.entity.Membre;
import sn.association.cotisations.entity.ModePaiement;
import sn.association.cotisations.entity.PaiementAbonnement;
import sn.association.cotisations.entity.Role;
import sn.association.cotisations.service.LimiteForfaitException;
import sn.association.cotisations.service.ServicesDeTest;
import sn.association.cotisations.util.JPAUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/** Groupes, adhésions par code et limites des forfaits (H2). */
class GroupesEtAbonnementsIntegrationTest {

    private static ServicesDeTest s;

    @BeforeAll
    static void setupAll() {
        JPAUtil.setUnitName("testPU");
        s = new ServicesDeTest();
    }

    @AfterAll
    static void teardownAll() {
        JPAUtil.close();
    }

    private static Compte compte(String prenom) {
        return s.comptes.inscrire(prenom, "Test", prenom.toLowerCase() + "-" + UUID.randomUUID() + "@test.sn",
                null, "secret123");
    }

    private static Membre groupe(Compte c, String nom) {
        return s.groupes.creer(c, nom, new BigDecimal("2000"), new BigDecimal("500"));
    }

    @Test
    void forfaitGratuit_unSeulGroupe() {
        Compte c = compte("Awa");
        Membre admin = groupe(c, "Tontine");
        assertEquals(Role.ADMIN, admin.getRole());
        LimiteForfaitException e = assertThrows(LimiteForfaitException.class, () -> groupe(c, "Deuxième"));
        assertEquals(Forfait.ESSENTIEL, e.getForfaitConseille());
    }

    @Test
    void forfaitGratuit_quinzeMembresParGroupe() {
        Compte c = compte("Binta");
        Membre admin = groupe(c, "Petit groupe");
        for (int i = 0; i < 14; i++) {
            s.membres.ajouter(admin.getGroupe(), "P" + i, "N", "p" + i + "-" + UUID.randomUUID() + "@test.sn",
                    null, null, null, Role.MEMBRE, null);
        }
        assertThrows(LimiteForfaitException.class, () -> s.membres.ajouter(admin.getGroupe(), "Seize", "N",
                "seize-" + UUID.randomUUID() + "@test.sn", null, null, null, Role.MEMBRE, null));
    }

    @Test
    void rejoindreAvecLeCode_unCompteDansDeuxGroupes() {
        Membre adminA = groupe(compte("Coumba"), "Groupe A");
        Membre adminB = groupe(compte("Dieynaba"), "Groupe B");
        Compte fatou = compte("Fatou");

        Membre dansA = s.groupes.rejoindre(fatou, adminA.getGroupe().getCodeInvitation().toLowerCase());
        Membre dansB = s.groupes.rejoindre(fatou, adminB.getGroupe().getCodeInvitation());
        assertEquals(Role.MEMBRE, dansA.getRole());
        assertEquals(fatou.getId(), dansB.getCompte().getId());
        assertEquals(LocalDate.now(), dansA.getDateAdhesion());
        assertThrows(IllegalStateException.class,
                () -> s.groupes.rejoindre(fatou, adminA.getGroupe().getCodeInvitation()));
        assertThrows(IllegalArgumentException.class, () -> s.groupes.rejoindre(fatou, "ZZZZZZ"));
    }

    @Test
    void abonnement_prolongeEtLeveLaLimite() {
        Compte c = compte("Ndeye");
        groupe(c, "Premier");
        PaiementAbonnement p1 = s.abonnements.souscrire(c, Forfait.ESSENTIEL, ModePaiement.WAVE);
        assertEquals(LocalDate.now(), p1.getDebut());
        assertEquals(new BigDecimal("1000"), p1.getMontant());

        Compte maj = s.comptes.trouver(c.getId()).orElseThrow();
        assertEquals(Forfait.ESSENTIEL, maj.forfaitEffectif());
        assertDoesNotThrow(() -> groupe(maj, "Deuxième"));

        PaiementAbonnement p2 = s.abonnements.souscrire(maj, Forfait.ESSENTIEL, ModePaiement.ORANGE_MONEY);
        assertEquals(p1.getFin().plusDays(1), p2.getDebut(), "Le renouvellement prolonge la période");
        assertThrows(IllegalArgumentException.class,
                () -> s.abonnements.souscrire(maj, Forfait.GRATUIT, ModePaiement.WAVE));
        assertThrows(IllegalArgumentException.class,
                () -> s.abonnements.souscrire(maj, Forfait.PRO, ModePaiement.ESPECES));
    }

    @Test
    void forfaitExpire_redevientGratuit() {
        Compte c = new Compte();
        c.setForfait(Forfait.PRO);
        c.setForfaitExpireLe(LocalDate.now().minusDays(1));
        assertEquals(Forfait.GRATUIT, c.forfaitEffectif());
        c.setForfaitExpireLe(LocalDate.now());
        assertEquals(Forfait.PRO, c.forfaitEffectif());
    }
}
