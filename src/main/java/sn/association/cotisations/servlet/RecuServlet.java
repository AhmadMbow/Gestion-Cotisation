package sn.association.cotisations.servlet;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sn.association.cotisations.entity.Cotisation;
import sn.association.cotisations.entity.Membre;
import sn.association.cotisations.entity.Role;
import sn.association.cotisations.service.CotisationService;

import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Affiche / télécharge un reçu de cotisation.
 *  - défaut : HTML imprimable (window.print())
 *  - ?format=pdf : génération PDF via iText
 *
 * Sécurité : un membre ne voit que ses propres reçus, l'admin voit tout.
 */
@WebServlet(name = "RecuServlet", urlPatterns = {"/membre/recu", "/admin/recu"})
public class RecuServlet extends HttpServlet {

    private static final NumberFormat MONEY = NumberFormat.getNumberInstance(Locale.FRANCE);

    private final CotisationService cotisationService = new CotisationService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, java.io.IOException {

        Integer id;
        try { id = Integer.valueOf(req.getParameter("id")); }
        catch (NumberFormatException | NullPointerException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID manquant.");
            return;
        }

        Cotisation c;
        try { c = cotisationService.trouver(id); }
        catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Reçu introuvable.");
            return;
        }

        Membre user = (Membre) req.getSession().getAttribute("user");
        boolean isProprio = user.getNumero().equals(c.getMembre().getNumero());
        boolean isAdmin = user.getRole() == Role.ADMIN;
        if (!isProprio && !isAdmin) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Reçu inaccessible.");
            return;
        }

        if ("pdf".equalsIgnoreCase(req.getParameter("format"))) {
            writePdf(resp, c);
            return;
        }

        req.setAttribute("cotisation", c);
        req.getRequestDispatcher("/WEB-INF/views/common/recu.jsp").forward(req, resp);
    }

    private void writePdf(HttpServletResponse resp, Cotisation c) throws java.io.IOException {
        resp.setContentType("application/pdf");
        resp.setHeader("Content-Disposition",
                "inline; filename=\"recu-" + c.getId() + ".pdf\"");

        try (OutputStream os = resp.getOutputStream();
             PdfWriter writer = new PdfWriter(os);
             PdfDocument pdf = new PdfDocument(writer);
             Document doc = new Document(pdf, PageSize.A4)) {

            doc.setMargins(40, 40, 40, 40);

            // En-tête
            Paragraph title = new Paragraph("REÇU DE COTISATION")
                    .setFontSize(20)
                    .setBold()
                    .setFontColor(new DeviceRgb(40, 95, 175))
                    .setTextAlignment(TextAlignment.CENTER);
            doc.add(title);

            doc.add(new Paragraph("Association — Gestion Cotisations")
                    .setFontSize(11)
                    .setTextAlignment(TextAlignment.CENTER));
            doc.add(new Paragraph("Dakar, Sénégal — contact@asso.sn")
                    .setFontSize(9)
                    .setFontColor(ColorConstants.GRAY)
                    .setTextAlignment(TextAlignment.CENTER));

            doc.add(new Paragraph(" "));

            // Bloc infos
            Table infos = new Table(UnitValue.createPercentArray(new float[]{1, 2}))
                    .useAllAvailableWidth();
            infos.addCell(label("Reçu N°"));
            infos.addCell(value(String.valueOf(c.getId())));
            infos.addCell(label("Date paiement"));
            infos.addCell(value(c.getDatePaiement() != null ? c.getDatePaiement().toString() : ""));
            infos.addCell(label("Période"));
            infos.addCell(value(String.format("%02d / %d", c.getMois(), c.getAnnee())));
            infos.addCell(label("Mode de paiement"));
            infos.addCell(value(c.getModePaiement() != null ? c.getModePaiement().name() : ""));
            infos.addCell(label("Statut"));
            infos.addCell(value(c.getStatut() != null ? c.getStatut().name() : ""));
            doc.add(infos);

            doc.add(new Paragraph(" "));

            // Bénéficiaire
            Membre m = c.getMembre();
            doc.add(new Paragraph("Versé par :").setBold());
            doc.add(new Paragraph(m.getPrenom() + " " + m.getNom()));
            doc.add(new Paragraph(m.getEmail()).setFontColor(ColorConstants.GRAY).setFontSize(10));
            doc.add(new Paragraph("Adhérent depuis : "
                    + (m.getDateAdhesion() != null ? m.getDateAdhesion().toString() : "—"))
                    .setFontColor(ColorConstants.GRAY).setFontSize(10));

            doc.add(new Paragraph(" "));

            // Tableau désignation / montant
            Table montants = new Table(UnitValue.createPercentArray(new float[]{3, 1}))
                    .useAllAvailableWidth();
            Cell desHead = new Cell().add(new Paragraph("Désignation").setBold());
            Cell montHead = new Cell().add(new Paragraph("Montant (FCFA)").setBold())
                    .setTextAlignment(TextAlignment.RIGHT);
            desHead.setBackgroundColor(new DeviceRgb(240, 240, 240));
            montHead.setBackgroundColor(new DeviceRgb(240, 240, 240));
            montants.addHeaderCell(desHead);
            montants.addHeaderCell(montHead);

            montants.addCell(new Cell().add(new Paragraph(
                    "Cotisation mensuelle — " + String.format("%02d/%d", c.getMois(), c.getAnnee()))));
            montants.addCell(new Cell().add(new Paragraph(MONEY.format(c.getMontant())))
                    .setTextAlignment(TextAlignment.RIGHT));

            Cell totalLabel = new Cell().add(new Paragraph("TOTAL").setBold())
                    .setBorderTop(new SolidBorder(1));
            Cell totalValue = new Cell().add(new Paragraph(MONEY.format(c.getMontant()) + " FCFA").setBold())
                    .setBorderTop(new SolidBorder(1))
                    .setTextAlignment(TextAlignment.RIGHT);
            montants.addCell(totalLabel);
            montants.addCell(totalValue);

            montants.setHorizontalAlignment(HorizontalAlignment.CENTER);
            doc.add(montants);

            doc.add(new Paragraph(" "));
            doc.add(new Paragraph("Merci pour votre participation à la vie de l'association !")
                    .setFontColor(ColorConstants.GRAY)
                    .setTextAlignment(TextAlignment.CENTER));
            doc.add(new Paragraph("Document généré automatiquement — reçu N°" + c.getId())
                    .setFontSize(8)
                    .setFontColor(ColorConstants.GRAY)
                    .setTextAlignment(TextAlignment.CENTER));
        }
    }

    private static Cell label(String s) {
        return new Cell().add(new Paragraph(s).setBold()).setBorder(null);
    }

    private static Cell value(String s) {
        return new Cell().add(new Paragraph(s)).setBorder(null);
    }
}
