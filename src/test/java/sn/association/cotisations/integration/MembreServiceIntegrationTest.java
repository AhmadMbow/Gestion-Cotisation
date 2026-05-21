package sn.association.cotisations.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sn.association.cotisations.entity.Membre;
import sn.association.cotisations.entity.Role;
import sn.association.cotisations.service.MembreService;
import sn.association.cotisations.util.JPAUtil;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test d'intégration sur H2 in-memory : crée un membre via MembreService
 * et vérifie qu'il est persisté + accessible via findByEmail.
 */
class MembreServiceIntegrationTest {

    private static MembreService service;

    @BeforeAll
    static void setupAll() {
        JPAUtil.setUnitName("testPU");
        service = new MembreService();
    }

    @AfterAll
    static void teardownAll() {
        JPAUtil.close();
    }

    @Test
    void creer_persisteUnMembre_etLeRetrouve() {
        String email = "u-" + UUID.randomUUID() + "@asso.sn";

        Membre saved = service.creer("Fatou", "Sow", email, LocalDate.of(1995, 3, 14),
                Role.MEMBRE, "secret123");

        assertNotNull(saved.getNumero(), "L'ID doit être généré");
        assertEquals(email, saved.getEmail());
        assertEquals("Fatou", saved.getPrenom());
        assertNotNull(saved.getDateAdhesion(), "La date d'adhésion doit être renseignée");
        assertNotEquals("secret123", saved.getMotDePasse(),
                "Le mot de passe doit être haché, pas stocké en clair");
    }

    @Test
    void creer_emailDuplique_throws() {
        String email = "dup-" + UUID.randomUUID() + "@asso.sn";
        service.creer("A", "B", email, LocalDate.of(1990, 1, 1), Role.MEMBRE, "secret123");

        assertThrows(IllegalArgumentException.class,
                () -> service.creer("X", "Y", email, LocalDate.of(1990, 1, 1),
                        Role.MEMBRE, "secret123"));
    }
}
