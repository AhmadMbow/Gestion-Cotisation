package sn.association.cotisations.util;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JPAUtilDatabaseUrlTest {

    @Test
    void urlAivenAvecSsl() {
        Map<String, Object> p = JPAUtil.depuisDatabaseUrl(
                "mysql://avnadmin:p%40ss:w0rd@mysql-abc.aivencloud.com:12345/defaultdb?ssl-mode=REQUIRED");
        assertEquals("jdbc:mysql://mysql-abc.aivencloud.com:12345/defaultdb"
                + "?serverTimezone=UTC&characterEncoding=UTF-8&sslMode=REQUIRED",
                p.get("jakarta.persistence.jdbc.url"));
        assertEquals("avnadmin", p.get("jakarta.persistence.jdbc.user"));
        assertEquals("p@ss:w0rd", p.get("jakarta.persistence.jdbc.password"));
    }

    @Test
    void urlSansSslNiBase() {
        Map<String, Object> p = JPAUtil.depuisDatabaseUrl("mysql://root@db:3306");
        assertEquals("jdbc:mysql://db:3306/defaultdb?serverTimezone=UTC&characterEncoding=UTF-8"
                + "&useSSL=false&allowPublicKeyRetrieval=true", p.get("jakarta.persistence.jdbc.url"));
        assertEquals("root", p.get("jakarta.persistence.jdbc.user"));
    }

    @Test
    void refuseUnAutreMoteur() {
        assertThrows(IllegalArgumentException.class,
                () -> JPAUtil.depuisDatabaseUrl("postgres://u:p@h:5432/db"));
    }
}
