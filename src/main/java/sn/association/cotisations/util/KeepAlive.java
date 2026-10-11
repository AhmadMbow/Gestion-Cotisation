package sn.association.cotisations.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Garde le serveur éveillé sur un hébergement gratuit qui endort les services
 * sans visite (Render : après 15 min). Le serveur appelle sa propre adresse
 * publique toutes les 10 minutes.
 *
 * Activé seulement si KEEPALIVE_URL est défini, ou sur Render (RENDER_EXTERNAL_URL,
 * fourni automatiquement) ; KEEPALIVE_URL=off le désactive.
 */
public final class KeepAlive {

    private static final Logger log = LoggerFactory.getLogger(KeepAlive.class);
    private static ScheduledExecutorService scheduler;

    private KeepAlive() {
    }

    public static synchronized void demarrer() {
        String url = cible();
        if (url == null || scheduler != null) return;
        HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(20)).build();
        HttpRequest ping = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(30))
                .header("User-Agent", "Cotisen-KeepAlive")
                .GET()
                .build();
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "keep-alive");
            t.setDaemon(true);
            return t;
        });
        scheduler.scheduleAtFixedRate(() -> {
            try {
                client.send(ping, HttpResponse.BodyHandlers.discarding());
            } catch (Exception e) {
                log.debug("Keep-alive : {}", e.getMessage());
            }
        }, 10, 10, TimeUnit.MINUTES);
        log.info("Keep-alive actif : {} toutes les 10 minutes.", url);
    }

    public static synchronized void arreter() {
        if (scheduler != null) {
            scheduler.shutdownNow();
            scheduler = null;
        }
    }

    private static String cible() {
        String explicite = System.getenv("KEEPALIVE_URL");
        if ("off".equalsIgnoreCase(explicite)) return null;
        if (explicite != null && !explicite.isBlank()) return explicite;
        String render = System.getenv("RENDER_EXTERNAL_URL");
        return render == null || render.isBlank() ? null : render.replaceAll("/+$", "") + "/api/sante";
    }
}
