package sn.association.cotisations.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sn.association.cotisations.entity.Amende;
import sn.association.cotisations.entity.Membre;
import sn.association.cotisations.service.AmendeService;
import sn.association.cotisations.util.FlashUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Routes membre :
 *   GET  /membre/amendes       → mes amendes
 *   POST /membre/amendes/payer → payer une amende
 */
@WebServlet(name = "MembreAmendesServlet", urlPatterns = {
        "/membre/amendes",
        "/membre/amendes/payer"
})
public class MembreAmendesServlet extends HttpServlet {

    private final AmendeService amendeService = new AmendeService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!"/membre/amendes".equals(req.getServletPath())) {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }

        Membre user = (Membre) req.getSession().getAttribute("user");
        List<Amende> mesAmendes = amendeService.historiqueMembre(user.getNumero());

        BigDecimal totalImpayees = mesAmendes.stream()
                .filter(a -> a.getStatutPaiement().name().equals("IMPAYEE"))
                .map(Amende::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        req.setAttribute("amendes", mesAmendes);
        req.setAttribute("totalImpayees", totalImpayees);
        req.getRequestDispatcher("/WEB-INF/views/member/mes-amendes.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        if (!"/membre/amendes/payer".equals(req.getServletPath())) {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }

        Membre user = (Membre) req.getSession().getAttribute("user");
        try {
            Integer id = Integer.valueOf(req.getParameter("id"));
            Amende a = amendeService.payerParMembre(id, user.getNumero());
            FlashUtil.success(req, "Amende de " + a.getMontant() + " FCFA payée. Merci.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            FlashUtil.error(req, e.getMessage());
        } catch (NullPointerException e) {
            FlashUtil.error(req, "ID amende manquant.");
        }
        resp.sendRedirect(req.getContextPath() + "/membre/amendes");
    }
}
