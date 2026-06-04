package sn.association.cotisations.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Envoi d'emails via SMTP (Jakarta Mail).
 *
 * Config par variables d'environnement :
 *   SMTP_HOST       (ex: smtp.gmail.com)
 *   SMTP_PORT       (ex: 587, défaut 587)
 *   SMTP_USER       (compte SMTP)
 *   SMTP_PASSWORD   (mot de passe / app password)
 *   SMTP_FROM       (expéditeur, défaut = SMTP_USER)
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
    private final String from = envOrDefault("SMTP_FROM", user);
    private final boolean starttls = !"false".equalsIgnoreCase(envOrDefault("SMTP_STARTTLS", "true"));

    public boolean isConfigured() {
        return host != null && !host.isBlank();
    }

    public void send(String to, String subject, String body) throws MessagingException {
        if (!isConfigured()) {
            log.warn("SMTP non configuré — email NON envoyé. Destinataire={}, Sujet={}\n--- Corps ---\n{}\n--- Fin ---",
                    to, subject, body);
            return;
        }

        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", String.valueOf(starttls));

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });

        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(from));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        msg.setSubject(subject, StandardCharsets.UTF_8.name());
        msg.setText(body, StandardCharsets.UTF_8.name());

        Transport.send(msg);
        log.info("Email envoyé à {} ({}).", to, subject);
    }

    private static String envOrDefault(String key, String def) {
        String v = System.getenv(key);
        return (v == null || v.isBlank()) ? def : v;
    }
}
