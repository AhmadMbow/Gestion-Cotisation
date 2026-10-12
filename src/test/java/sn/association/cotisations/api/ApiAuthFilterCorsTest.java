package sn.association.cotisations.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Origines CORS autorisées par défaut (sans CORS_ORIGINS) : localhost uniquement. */
class ApiAuthFilterCorsTest {

    @Test
    void localhost_autorise() {
        assertTrue(ApiAuthFilter.autorisee("http://localhost:5050"));
        assertTrue(ApiAuthFilter.autorisee("http://127.0.0.1:8080"));
        assertTrue(ApiAuthFilter.autorisee("http://localhost"));
    }

    @Test
    void autresOrigines_refusees() {
        assertFalse(ApiAuthFilter.autorisee("https://exemple.com"));
        assertFalse(ApiAuthFilter.autorisee("http://localhost.exemple.com"));
        assertFalse(ApiAuthFilter.autorisee("http://localhost:5050.exemple.com"));
        assertFalse(ApiAuthFilter.autorisee("null"));
    }
}
