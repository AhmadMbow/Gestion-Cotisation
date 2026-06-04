package sn.association.cotisations.service;

import org.junit.jupiter.api.Test;
import sn.association.cotisations.entity.ModePaiement;

import java.math.BigDecimal;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Validation pure de CotisationService.enregistrer() — toutes les règles testées
 * lèvent IllegalArgumentException AVANT d'atteindre la base.
 */
class CotisationServiceValidationTest {

    private final CotisationService service = new CotisationService();

    @Test
    void enregistrer_moisInferieurA1_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> service.enregistrer(1, new BigDecimal("5000"),
                        0, 2026, ModePaiement.ESPECES));
    }

    @Test
    void enregistrer_moisSuperieurA12_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> service.enregistrer(1, new BigDecimal("5000"),
                        13, 2026, ModePaiement.ESPECES));
    }

    @Test
    void enregistrer_periodeDansLeFutur_throws() {
        YearMonth futur = YearMonth.now().plusMonths(2);
        assertThrows(IllegalArgumentException.class,
                () -> service.enregistrer(1, new BigDecimal("5000"),
                        futur.getMonthValue(), futur.getYear(), ModePaiement.ESPECES));
    }

    @Test
    void enregistrer_montantNegatif_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> service.enregistrer(1, new BigDecimal("-1000"),
                        1, 2026, ModePaiement.ESPECES));
    }

    @Test
    void enregistrer_montantZero_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> service.enregistrer(1, BigDecimal.ZERO,
                        1, 2026, ModePaiement.ESPECES));
    }

    @Test
    void enregistrer_montantNull_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> service.enregistrer(1, null,
                        1, 2026, ModePaiement.ESPECES));
    }
}
