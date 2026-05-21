package sn.association.cotisations.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import sn.association.cotisations.dao.AmendeDAO;
import sn.association.cotisations.dao.CotisationDAO;
import sn.association.cotisations.dao.MembreDAO;
import sn.association.cotisations.entity.Amende;
import sn.association.cotisations.entity.Cotisation;
import sn.association.cotisations.entity.Membre;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet(name = "ExportServlet", urlPatterns = {
        "/admin/exports",
        "/admin/exports/membres.xlsx",
        "/admin/exports/cotisations.xlsx",
        "/admin/exports/amendes.xlsx"
})
public class ExportServlet extends HttpServlet {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final MembreDAO membreDAO = new MembreDAO();
    private final CotisationDAO cotisationDAO = new CotisationDAO();
    private final AmendeDAO amendeDAO = new AmendeDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getServletPath();
        switch (path) {
            case "/admin/exports/membres.xlsx"     -> exportMembres(resp);
            case "/admin/exports/cotisations.xlsx" -> exportCotisations(resp);
            case "/admin/exports/amendes.xlsx"     -> exportAmendes(resp);
            default                                -> req.getRequestDispatcher(
                    "/WEB-INF/views/admin/exports.jsp").forward(req, resp);
        }
    }

    // -------------------- MEMBRES --------------------
    private void exportMembres(HttpServletResponse resp) throws IOException {
        prepareXlsxResponse(resp, "membres-" + LocalDate.now() + ".xlsx");
        try (Workbook wb = new XSSFWorkbook(); OutputStream out = resp.getOutputStream()) {
            Sheet sh = wb.createSheet("Membres");
            CellStyle header = headerStyle(wb);

            String[] cols = {"Numéro", "Prénom", "Nom", "Email",
                             "Date naissance", "Date adhésion", "Statut", "Rôle"};
            Row h = sh.createRow(0);
            for (int i = 0; i < cols.length; i++) {
                Cell c = h.createCell(i);
                c.setCellValue(cols[i]);
                c.setCellStyle(header);
            }

            List<Membre> membres = membreDAO.findAll();
            int r = 1;
            for (Membre m : membres) {
                Row row = sh.createRow(r++);
                row.createCell(0).setCellValue(m.getNumero() != null ? m.getNumero() : 0);
                row.createCell(1).setCellValue(safe(m.getPrenom()));
                row.createCell(2).setCellValue(safe(m.getNom()));
                row.createCell(3).setCellValue(safe(m.getEmail()));
                row.createCell(4).setCellValue(m.getDateNaissance() != null ? m.getDateNaissance().format(DATE_FMT) : "");
                row.createCell(5).setCellValue(m.getDateAdhesion() != null ? m.getDateAdhesion().format(DATE_FMT) : "");
                row.createCell(6).setCellValue(m.getStatut() != null ? m.getStatut().name() : "");
                row.createCell(7).setCellValue(m.getRole() != null ? m.getRole().name() : "");
            }
            autoSize(sh, cols.length);
            wb.write(out);
        }
    }

    // -------------------- COTISATIONS --------------------
    private void exportCotisations(HttpServletResponse resp) throws IOException {
        prepareXlsxResponse(resp, "cotisations-" + LocalDate.now() + ".xlsx");
        try (Workbook wb = new XSSFWorkbook(); OutputStream out = resp.getOutputStream()) {
            Sheet sh = wb.createSheet("Cotisations");
            CellStyle header = headerStyle(wb);

            String[] cols = {"ID", "Membre", "Email", "Période", "Montant (FCFA)",
                             "Date paiement", "Mode", "Statut"};
            Row h = sh.createRow(0);
            for (int i = 0; i < cols.length; i++) {
                Cell c = h.createCell(i);
                c.setCellValue(cols[i]);
                c.setCellStyle(header);
            }

            List<Cotisation> cotisations = cotisationDAO.findAll();
            int r = 1;
            for (Cotisation c : cotisations) {
                Row row = sh.createRow(r++);
                row.createCell(0).setCellValue(c.getId() != null ? c.getId() : 0);
                Membre m = c.getMembre();
                row.createCell(1).setCellValue(m != null ? (m.getPrenom() + " " + m.getNom()) : "");
                row.createCell(2).setCellValue(m != null ? safe(m.getEmail()) : "");
                row.createCell(3).setCellValue(String.format("%02d/%d", c.getMois(), c.getAnnee()));
                row.createCell(4).setCellValue(c.getMontant() != null ? c.getMontant().doubleValue() : 0);
                row.createCell(5).setCellValue(c.getDatePaiement() != null ? c.getDatePaiement().format(DATE_FMT) : "");
                row.createCell(6).setCellValue(c.getModePaiement() != null ? c.getModePaiement().name() : "");
                row.createCell(7).setCellValue(c.getStatut() != null ? c.getStatut().name() : "");
            }
            autoSize(sh, cols.length);
            wb.write(out);
        }
    }

    // -------------------- AMENDES --------------------
    private void exportAmendes(HttpServletResponse resp) throws IOException {
        prepareXlsxResponse(resp, "amendes-" + LocalDate.now() + ".xlsx");
        try (Workbook wb = new XSSFWorkbook(); OutputStream out = resp.getOutputStream()) {
            Sheet sh = wb.createSheet("Amendes");
            CellStyle header = headerStyle(wb);

            String[] cols = {"ID", "Membre", "Email", "Montant (FCFA)", "Date génération", "Statut"};
            Row h = sh.createRow(0);
            for (int i = 0; i < cols.length; i++) {
                Cell c = h.createCell(i);
                c.setCellValue(cols[i]);
                c.setCellStyle(header);
            }

            List<Amende> amendes = amendeDAO.findAll();
            int r = 1;
            for (Amende a : amendes) {
                Row row = sh.createRow(r++);
                row.createCell(0).setCellValue(a.getId() != null ? a.getId() : 0);
                Membre m = a.getMembre();
                row.createCell(1).setCellValue(m != null ? (m.getPrenom() + " " + m.getNom()) : "");
                row.createCell(2).setCellValue(m != null ? safe(m.getEmail()) : "");
                row.createCell(3).setCellValue(a.getMontant() != null ? a.getMontant().doubleValue() : 0);
                row.createCell(4).setCellValue(a.getDateGeneration() != null ? a.getDateGeneration().format(DATE_FMT) : "");
                row.createCell(5).setCellValue(a.getStatutPaiement() != null ? a.getStatutPaiement().name() : "");
            }
            autoSize(sh, cols.length);
            wb.write(out);
        }
    }

    // -------------------- helpers --------------------
    private void prepareXlsxResponse(HttpServletResponse resp, String filename) {
        resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
    }

    private CellStyle headerStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle();
        Font f = wb.createFont();
        f.setBold(true);
        s.setFont(f);
        s.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setBorderBottom(BorderStyle.THIN);
        return s;
    }

    private void autoSize(Sheet sh, int cols) {
        for (int i = 0; i < cols; i++) sh.autoSizeColumn(i);
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}
