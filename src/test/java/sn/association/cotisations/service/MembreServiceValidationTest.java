package sn.association.cotisations.service;

import org.junit.jupiter.api.Test;
import sn.association.cotisations.entity.Role;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests de validation pure de MembreService.creer().
 * On exploite le fait que valider() est appelée AVANT toute interaction BDD :
 * une entrée invalide lève IllegalArgumentException sans atteindre le DAO.
 */
class MembreServiceValidationTest {

    private final MembreService service = new MembreService();

    @Test
    void creer_prenomVide_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> service.creer("", "Diop", "a@b.sn", LocalDate.of(2000, 1, 1),
                        Role.MEMBRE, "secret123"));
    }

    @Test
    void creer_nomVide_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> service.creer("Aminata", "  ", "a@b.sn", LocalDate.of(2000, 1, 1),
                        Role.MEMBRE, "secret123"));
    }

    @Test
    void creer_emailSansArobase_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> service.creer("Aminata", "Diop", "pas-un-email", LocalDate.of(2000, 1, 1),
                        Role.MEMBRE, "secret123"));
    }

    @Test
    void creer_motDePasseTropCourt_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> service.creer("Aminata", "Diop", "a@b.sn", LocalDate.of(2000, 1, 1),
                        Role.MEMBRE, "abc"));
    }

    @Test
    void creer_motDePasseNull_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> service.creer("Aminata", "Diop", "a@b.sn", LocalDate.of(2000, 1, 1),
                        Role.MEMBRE, null));
    }
}
