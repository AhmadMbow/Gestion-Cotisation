package sn.association.cotisations.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Gabarit des emails Cotisen : version HTML (aux couleurs de l'application)
 * et version texte, construites à partir des mêmes éléments.
 *
 * <pre>
 * EmailTemplate.titre("Paiement confirmé")
 *     .intro("Bonjour Aminata, …")
 *     .ligne("Mois", "Octobre 2026")
 *     .total("Total payé", "5 000 F")
 *     .bouton("Ouvrir Cotisen", url)
 * </pre>
 */
public final class EmailTemplate {

    private static final String OR = "#E0A10E";
    private static final String ENCRE = "#2A2112";
    private static final String GRIS = "#8C8270";

    private final String titre;
    private final List<String> paragraphes = new ArrayList<>();
    private final List<String[]> lignes = new ArrayList<>();
    private String[] total;
    private String[] bouton;
    private String conclusion;

    private EmailTemplate(String titre) {
        this.titre = titre;
    }

    public static EmailTemplate titre(String titre) {
        return new EmailTemplate(titre);
    }

    public EmailTemplate intro(String texte) {
        paragraphes.add(texte);
        return this;
    }

    public EmailTemplate ligne(String libelle, String valeur) {
        lignes.add(new String[]{libelle, valeur});
        return this;
    }

    public EmailTemplate total(String libelle, String valeur) {
        total = new String[]{libelle, valeur};
        return this;
    }

    public EmailTemplate bouton(String libelle, String url) {
        bouton = new String[]{libelle, url};
        return this;
    }

    public EmailTemplate conclusion(String texte) {
        conclusion = texte;
        return this;
    }

    public String texte() {
        StringBuilder sb = new StringBuilder(titre).append("\n\n");
        for (String p : paragraphes) sb.append(p).append("\n\n");
        for (String[] l : lignes) sb.append("  - ").append(l[0]).append(" : ").append(l[1]).append('\n');
        if (total != null) sb.append("  = ").append(total[0]).append(" : ").append(total[1]).append('\n');
        if (!lignes.isEmpty() || total != null) sb.append('\n');
        if (bouton != null) sb.append(bouton[0]).append(" : ").append(bouton[1]).append("\n\n");
        if (conclusion != null) sb.append(conclusion).append("\n\n");
        sb.append("-- Cotisen");
        return sb.toString();
    }

    public String html() {
        StringBuilder rows = new StringBuilder();
        for (String[] l : lignes) {
            rows.append("<tr><td style=\"padding:11px 0;border-bottom:1px solid #EDE4D0;color:").append(GRIS)
                .append(";font-size:14px\">").append(esc(l[0]))
                .append("</td><td align=\"right\" style=\"padding:11px 0;border-bottom:1px solid #EDE4D0;color:")
                .append(ENCRE).append(";font-size:14px;font-weight:700\">").append(esc(l[1])).append("</td></tr>");
        }
        if (total != null) {
            rows.append("<tr><td style=\"padding:14px 0 0;color:").append(ENCRE)
                .append(";font-size:15px;font-weight:800\">").append(esc(total[0]))
                .append("</td><td align=\"right\" style=\"padding:14px 0 0;color:#C8901A;font-size:20px;font-weight:800\">")
                .append(esc(total[1])).append("</td></tr>");
        }

        StringBuilder body = new StringBuilder();
        body.append("<h1 style=\"margin:0 0 14px;font-size:22px;line-height:1.3;color:").append(ENCRE).append("\">")
            .append(esc(titre)).append("</h1>");
        for (String p : paragraphes) {
            body.append("<p style=\"margin:0 0 14px;font-size:15px;line-height:1.55;color:#4A4234\">")
                .append(esc(p)).append("</p>");
        }
        if (rows.length() > 0) {
            body.append("<table role=\"presentation\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" ")
                .append("style=\"margin:8px 0 18px;background:#FBF8F1;border-radius:14px;padding:4px 16px 14px\">")
                .append(rows).append("</table>");
        }
        if (bouton != null) {
            body.append("<p style=\"margin:22px 0\"><a href=\"").append(esc(bouton[1]))
                .append("\" style=\"display:inline-block;background:#111111;color:#ffffff;text-decoration:none;")
                .append("font-weight:700;font-size:15px;padding:14px 26px;border-radius:30px\">")
                .append(esc(bouton[0])).append("</a></p>");
        }
        if (conclusion != null) {
            body.append("<p style=\"margin:0;font-size:13px;line-height:1.5;color:").append(GRIS).append("\">")
                .append(esc(conclusion)).append("</p>");
        }

        return "<!doctype html><html lang=\"fr\"><head><meta charset=\"utf-8\">"
            + "<meta name=\"viewport\" content=\"width=device-width,initial-scale=1\"></head>"
            + "<body style=\"margin:0;padding:0;background:#F6F4EE;"
            + "font-family:-apple-system,'Segoe UI',Roboto,Helvetica,Arial,sans-serif\">"
            + "<table role=\"presentation\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\"><tr>"
            + "<td align=\"center\" style=\"padding:28px 12px\">"
            + "<table role=\"presentation\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" "
            + "style=\"max-width:560px;background:#ffffff;border-radius:20px;overflow:hidden\">"
            + "<tr><td bgcolor=\"" + OR + "\" style=\"background:" + OR + ";padding:22px 28px;"
            + "color:#ffffff;font-size:26px;font-weight:800;letter-spacing:-0.5px\">Coti"
            + "<span style=\"color:#FFF1B8\">sen</span>.</td></tr>"
            + "<tr><td style=\"padding:28px\">" + body + "</td></tr></table>"
            + "<p style=\"margin:16px 0 0;font-size:12px;color:" + GRIS + "\">"
            + "Association Cotisen · Cet e-mail vous est envoyé automatiquement.</p>"
            + "</td></tr></table></body></html>";
    }

    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }

    // ---------------------------------------------------------------------
    // Formats communs aux emails
    // ---------------------------------------------------------------------

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /** 125000 → « 125 000 F » */
    public static String fcfa(BigDecimal montant) {
        DecimalFormatSymbols sym = new DecimalFormatSymbols(Locale.FRANCE);
        sym.setGroupingSeparator(' ');
        DecimalFormat df = new DecimalFormat("#,##0", sym);
        return df.format(montant.setScale(0, RoundingMode.HALF_UP)) + " F";
    }

    /** (10, 2026) → « Octobre 2026 » */
    public static String mois(int mois, int annee) {
        String nom = Month.of(mois).getDisplayName(TextStyle.FULL, Locale.FRENCH);
        return Character.toUpperCase(nom.charAt(0)) + nom.substring(1) + " " + annee;
    }

    public static String date(LocalDate d) {
        return d == null ? "" : d.format(DATE);
    }
}
