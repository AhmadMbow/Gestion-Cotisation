package sn.association.cotisations.api;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.stream.JsonParsingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sn.association.cotisations.entity.Membre;
import sn.association.cotisations.entity.ModePaiement;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Base des servlets de l'API JSON : lecture du corps, réponses, gestion des erreurs.
 *
 * Les erreurs métier des services sont traduites en codes HTTP :
 * IllegalArgumentException → 400, IllegalStateException → 409.
 */
public abstract class ApiServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(ApiServlet.class);
    static final Jsonb JSONB = JsonbBuilder.create();

    /** Attribut de requête posé par {@link ApiAuthFilter}. */
    public static final String UTILISATEUR = "api.utilisateur";
    public static final String JETON = "api.jeton";

    @FunctionalInterface
    protected interface Handler {
        Object handle(HttpServletRequest req, HttpServletResponse resp) throws Exception;
    }

    /** Exécute le traitement et écrit sa valeur de retour en JSON (null → 204). */
    protected void run(HttpServletRequest req, HttpServletResponse resp, Handler handler) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Cache-Control", "no-store");
        try {
            Object result = handler.handle(req, resp);
            if (result == null) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                resp.setContentType("application/json");
                JSONB.toJson(result, resp.getWriter());
            }
        } catch (ApiException e) {
            erreur(resp, e.getStatus(), e.getMessage());
        } catch (IllegalArgumentException | JsonParsingException | DateTimeParseException | ClassCastException e) {
            erreur(resp, HttpServletResponse.SC_BAD_REQUEST, message(e, "Requête invalide."));
        } catch (IllegalStateException e) {
            erreur(resp, HttpServletResponse.SC_CONFLICT, message(e, "Opération impossible."));
        } catch (Exception e) {
            log.error("Erreur API sur {} {}", req.getMethod(), req.getRequestURI(), e);
            erreur(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Erreur interne du serveur. Réessayez plus tard.");
        }
    }

    static void erreur(HttpServletResponse resp, int status, String message) throws IOException {
        if (resp.isCommitted()) return;
        resp.resetBuffer();
        resp.setStatus(status);
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        JSONB.toJson(Map.of("erreur", message), resp.getWriter());
    }

    private static String message(Exception e, String defaut) {
        return (e instanceof IllegalArgumentException || e instanceof IllegalStateException)
                && e.getMessage() != null ? e.getMessage() : defaut;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        super.service(req, resp);
    }

    // ------------------------------------------------------------------
    // Lecture de la requête
    // ------------------------------------------------------------------

    protected static Membre utilisateur(HttpServletRequest req) {
        return (Membre) req.getAttribute(UTILISATEUR);
    }

    /** Segments du chemin après le motif de la servlet : "/membres/12/statut" → [membres, 12, statut]. */
    protected static List<String> chemin(HttpServletRequest req) {
        String info = req.getPathInfo();
        List<String> parts = new ArrayList<>();
        if (info != null) {
            for (String p : info.split("/")) if (!p.isBlank()) parts.add(p);
        }
        return parts;
    }

    protected static JsonObject corps(HttpServletRequest req) throws IOException {
        try (JsonReader r = Json.createReader(req.getReader())) {
            return r.readObject();
        } catch (JsonParsingException e) {
            throw new IllegalArgumentException("Corps JSON invalide.");
        } catch (jakarta.json.JsonException e) {
            return JsonValue.EMPTY_JSON_OBJECT; // corps vide
        }
    }

    protected static String texte(JsonObject o, String cle) {
        JsonValue v = o.get(cle);
        if (v == null || v.getValueType() == JsonValue.ValueType.NULL) return null;
        return v instanceof JsonString s ? s.getString() : v.toString();
    }

    protected static LocalDate date(JsonObject o, String cle) {
        String v = texte(o, cle);
        return v == null || v.isBlank() ? null : LocalDate.parse(v.length() > 10 ? v.substring(0, 10) : v);
    }

    protected static int entier(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new ApiException(HttpServletResponse.SC_NOT_FOUND, "Ressource introuvable.");
        }
    }

    protected static ModePaiement mode(JsonObject o) {
        String v = texte(o, "mode");
        if (v == null) return null;
        try {
            return ModePaiement.valueOf(v);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Moyen de paiement inconnu : " + v);
        }
    }

    /** "periodes": [{"mois": 10, "annee": 2026}, …] */
    protected static List<YearMonth> periodes(JsonObject o) {
        List<YearMonth> res = new ArrayList<>();
        JsonArray arr = o.containsKey("periodes") ? o.getJsonArray("periodes") : null;
        if (arr == null) return res;
        for (JsonValue v : arr) {
            JsonObject p = v.asJsonObject();
            res.add(YearMonth.of(p.getInt("annee"), p.getInt("mois")));
        }
        return res;
    }

    /** "amendes": [12, 13] */
    protected static List<Integer> identifiants(JsonObject o, String cle) {
        List<Integer> res = new ArrayList<>();
        JsonArray arr = o.containsKey(cle) ? o.getJsonArray(cle) : null;
        if (arr == null) return res;
        for (int i = 0; i < arr.size(); i++) res.add(arr.getInt(i));
        return res;
    }

    protected static void exiger(boolean condition, int status, String message) {
        if (!condition) throw new ApiException(status, message);
    }
}
