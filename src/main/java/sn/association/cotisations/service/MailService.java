package sn.association.cotisations.service;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Envoi d'emails via SMTP (Jakarta Mail).
 *
 * Config par variables d'environnement :
 *   SMTP_HOST       (ex: smtp.gmail.com)
 *   SMTP_PORT       (ex: 587, défaut 587)
 *   SMTP_USER       (compte SMTP — facultatif pour un serveur de test sans authentification)
 *   SMTP_PASSWORD   (mot de passe / app password)
 *   SMTP_FROM       (expéditeur, défaut = SMTP_USER)
 *   SMTP_FROM_NAME  (nom affiché, défaut "Cotisen")
 *   SMTP_STARTTLS   ("true"/"false", défaut true)
 *
 * Ou, si BREVO_API_KEY est défini, envoi par l'API HTTPS de Brevo (hébergeurs
 * qui bloquent le SMTP, comme l'offre gratuite de Render). L'expéditeur reste
 * SMTP_FROM / SMTP_FROM_NAME (adresse à valider dans Brevo).
 *
 * Sans SMTP_HOST ni BREVO_API_KEY, les emails sont écrits dans les logs (mode dev).
 */
@ApplicationScoped
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    private final String host = System.getenv("SMTP_HOST");
    private final String port = envOrDefault("SMTP_PORT", "587");
    private final String user = System.getenv("SMTP_USER");
    private final String password = System.getenv("SMTP_PASSWORD");
    private final String from = envOrDefault("SMTP_FROM", envOrDefault("SMTP_USER", "no-reply@cotisen.sn"));
    private final String fromName = envOrDefault("SMTP_FROM_NAME", "Cotisen");
    private final boolean starttls = !"false".equalsIgnoreCase(envOrDefault("SMTP_STARTTLS", "true"));
    private final String brevoKey = System.getenv("BREVO_API_KEY");
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();

    /** Envois en arrière-plan : une requête de l'app n'attend jamais le serveur SMTP. */
    private final ExecutorService executor = Executors.newFixedThreadPool(2, r -> {
        Thread t = new Thread(r, "mail-sender");
        t.setDaemon(true);
        return t;
    });

    public boolean isConfigured() {
        return viaBrevo() || (host != null && !host.isBlank());
    }

    private boolean viaBrevo() {
        return brevoKey != null && !brevoKey.isBlank();
    }

    /** Email en texte brut (envoi immédiat, erreurs remontées à l'appelant). */
    public void send(String to, String subject, String body) throws MessagingException {
        send(to, subject, body, null);
    }

    /**
     * Email HTML avec une version texte de secours (envoi immédiat).
     * @param html version HTML, ou null pour un email texte seul
     */
    public void send(String to, String subject, String text, String html) throws MessagingException {
        if (!isConfigured()) {
            log.warn("SMTP non configuré — email NON envoyé. Destinataire={}, Sujet={}\n--- Corps ---\n{}\n--- Fin ---",
                    to, subject, text);
            return;
        }
        if (viaBrevo()) {
            envoyerViaBrevo(to, subject, text, html);
            return;
        }

        boolean auth = user != null && !user.isBlank();
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", String.valueOf(auth));
        props.put("mail.smtp.starttls.enable", String.valueOf(starttls));
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "15000");

        Session session = auth
                ? Session.getInstance(props, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, password);
                    }
                })
                : Session.getInstance(props);

        MimeMessage msg = new MimeMessage(session);
        try {
            msg.setFrom(new InternetAddress(from, fromName, StandardCharsets.UTF_8.name()));
        } catch (UnsupportedEncodingException e) {
            msg.setFrom(new InternetAddress(from));
        }
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        msg.setSubject(subject, StandardCharsets.UTF_8.name());

        if (html == null) {
            msg.setText(text, StandardCharsets.UTF_8.name());
        } else {
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(text, StandardCharsets.UTF_8.name());
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(html, "text/html; charset=UTF-8");
            MimeMultipart alt = new MimeMultipart("alternative");
            alt.addBodyPart(textPart);
            alt.addBodyPart(htmlPart);
            msg.setContent(alt);
        }

        Transport.send(msg);
        log.info("Email envoyé à {} ({}).", to, subject);
    }

    /** API transactionnelle de Brevo (HTTPS, port 443). */
    private void envoyerViaBrevo(String to, String subject, String text, String html) throws MessagingException {
        JsonObjectBuilder body = Json.createObjectBuilder()
                .add("sender", Json.createObjectBuilder().add("name", fromName).add("email", from))
                .add("to", Json.createArrayBuilder().add(Json.createObjectBuilder().add("email", to)))
                .add("subject", subject)
                .add("textContent", text);
        if (html != null) body.add("htmlContent", html);

        HttpRequest req = HttpRequest.newBuilder(URI.create("https://api.brevo.com/v3/smtp/email"))
                .timeout(Duration.ofSeconds(20))
                .header("api-key", brevoKey)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.build().toString(), StandardCharsets.UTF_8))
                .build();
        try {
            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() / 100 != 2) {
                throw new MessagingException("Brevo a refusé l'envoi (" + resp.statusCode() + ") : " + resp.body());
            }
            log.info("Email envoyé via Brevo à {} ({}).", to, subject);
        } catch (java.io.IOException e) {
            throw new MessagingException("Brevo injoignable : " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new MessagingException("Envoi interrompu", e);
        }
    }

    /** Envoi en arrière-plan ; un échec est seulement journalisé. */
    public void sendAsync(String to, String subject, String text, String html) {
        executor.submit(() -> {
            try {
                send(to, subject, text, html);
            } catch (MessagingException | RuntimeException e) {
                log.error("Échec d'envoi de l'email « {} » à {}", subject, to, e);
            }
        });
    }

    @PreDestroy
    void shutdown() {
        executor.shutdown();
    }

    private static String envOrDefault(String key, String def) {
        String v = System.getenv(key);
        return (v == null || v.isBlank()) ? def : v;
    }
}
