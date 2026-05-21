package sn.association.cotisations.servlet;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sn.association.cotisations.service.RapportService;

import java.io.IOException;

@WebServlet(name = "RapportsServlet", urlPatterns = {"/admin/rapports"})
public class RapportsServlet extends HttpServlet {

    @Inject RapportService rapportService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setAttribute("stats", rapportService.statsCles());
        req.setAttribute("evolution", rapportService.evolutionCotisations12Mois());
        req.setAttribute("modes", rapportService.repartitionModesPaiement());
        req.setAttribute("topRetards", rapportService.topRetardataires(5));
        req.getRequestDispatcher("/WEB-INF/views/admin/rapports.jsp").forward(req, resp);
    }
}
