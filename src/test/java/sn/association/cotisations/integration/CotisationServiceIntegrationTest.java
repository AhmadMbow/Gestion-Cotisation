package sn.association.cotisations.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sn.association.cotisations.entity.Cotisation;
import sn.association.cotisations.entity.Membre;
import sn.association.cotisations.entity.ModePaiement;
import sn.association.cotisations.entity.Role;
import sn.association.cotisations.service.CotisationService;
import sn.association.cotisations.service.MembreService;
import sn.association.cotisations.util.JPAUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test d'intégration sur H2 : enregistre une cotisation et vérifie qu'un second
 * enregistrement pour le même membre/mois/année est refusé.
 */
class CotisationServiceIntegrationTest {

    private static MembreService membreService;
    private static CotisationService cotisationService;

    @BeforeAll
    static void setupAll() {
        JPAUtil.setUnitName("testPU");
        membreService = new MembreService();
        cotisationService = new CotisationService();
    }

    @AfterAll
    static void teardownAll() {
        JPAUtil.close();
    }

    @Test
    void enregistrer_premierPaiement_persiste() {
        Membre m = membreService.creer("Aïssatou", "Ndiaye",
                "ai-" + UUID.randomUUID() + "@asso.sn",
                LocalDate.of(1998, 7, 4), Role.MEMBRE, "secret123");

        YearMonth ym = YearMonth.now().minusMonths(1);
        Cotisation c = cotisationService.enregistrer(
                m.getNumero(), new BigDecimal("5000"),
                ym.getMonthValue(), ym.getYear(), ModePaiement.WAVE);

        assertNotNull(c.getId());
        assertEquals(ym.getMonthValue(), c.getMois());
        assertEquals(ym.getYear(), c.getAnnee());
        assertEquals(new BigDecimal("5000"), c.getMontant());
    }

    @Test
    void enregistrer_memeMembreMemePeriode_throws() {
        Membre m = membreService.creer("Doudou", "Fall",
                "du-" + UUID.randomUUID() + "@asso.sn",
                LocalDate.of(1990, 1, 1), Role.MEMBRE, "secret123");

        YearMonth ym = YearMonth.now().minusMonths(2);
        cotisationService.enregistrer(m.getNumero(), new BigDecimal("5000"),
                ym.getMonthValue(), ym.getYear(), ModePaiement.ESPECES);

        // Deuxième tentative pour le même mois → doit échouer
        assertThrows(IllegalStateException.class,
                () -> cotisationService.enregistrer(m.getNumero(), new BigDecimal("5000"),
                        ym.getMonthValue(), ym.getYear(), ModePaiement.WAVE));
    }
}
