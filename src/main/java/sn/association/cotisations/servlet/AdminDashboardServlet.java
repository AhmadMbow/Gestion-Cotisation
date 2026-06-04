package sn.association.cotisations.servlet;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sn.association.cotisations.dao.AmendeDAO;
import sn.association.cotisations.dao.CotisationDAO;
import sn.association.cotisations.dao.MembreDAO;
import sn.association.cotisations.entity.Cotisation;
import sn.association.cotisations.service.RapportService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "AdminDashboardServlet", urlPatterns = {"/admin/dashboard"})
public class AdminDashboardServlet extends HttpServlet {

    @Inject MembreDAO membreDAO;
    @Inject CotisationDAO cotisationDAO;
    @Inject AmendeDAO amendeDAO;
    @Inject RapportService rapportService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalMembres", membreDAO.countActifs());
        BigDecimal totalMois = cotisationDAO.sumMontantsMoisCourant();
        stats.put("totalCotisationsMois", totalMois);
        stats.put("totalAmendes", amendeDAO.countImpayees());
        stats.put("membresEnRetard", cotisationDAO.countMembresEnRetardMoisCourant());

        List<Cotisation> derniers = cotisationDAO.findRecents(5);

        req.setAttribute("stats", stats);
        req.setAttribute("derniersPaiements", derniers);
        req.setAttribute("evolution", rapportService.evolutionCotisations12Mois());
        req.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(req, resp);
    }
}
