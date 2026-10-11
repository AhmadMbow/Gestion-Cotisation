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

import java.io.UnsupportedEncodingException;
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
 * Si SMTP_HOST est absent, les emails sont écrits dans les logs (mode dev).
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

    /** Envois en arrière-plan : une requête de l'app n'attend jamais le serveur SMTP. */
    private final ExecutorService executor = Executors.newFixedThreadPool(2, r -> {
        Thread t = new Thread(r, "mail-sender");
        t.setDaemon(true);
        return t;
    });

    public boolean isConfigured() {
        return host != null && !host.isBlank();
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
