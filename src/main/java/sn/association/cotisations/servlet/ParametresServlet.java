package sn.association.cotisations.servlet;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sn.association.cotisations.service.ParametreService;
import sn.association.cotisations.util.FlashUtil;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Réglages globaux de l'association (montants standards) :
 *   GET  /admin/parametres   → formulaire des paramètres
 *   POST /admin/parametres   → enregistrement des montants
 */
@WebServlet(name = "ParametresServlet", urlPatterns = {"/admin/parametres"})
public class ParametresServlet extends HttpServlet {

    @Inject ParametreService parametreService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("parametre", parametreService.get());
        req.getRequestDispatcher("/WEB-INF/views/admin/parametres.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            String cotisationParam = req.getParameter("montantCotisation");
            String amendeParam = req.getParameter("montantAmende");
            if (cotisationParam == null || amendeParam == null) {
                throw new IllegalArgumentException("Tous les champs sont obligatoires.");
            }
            BigDecimal montantCotisation = new BigDecimal(cotisationParam.trim());
            BigDecimal montantAmende = new BigDecimal(amendeParam.trim());

            parametreService.mettreAJour(montantCotisation, montantAmende);
            FlashUtil.success(req, "Paramètres enregistrés avec succès.");
            resp.sendRedirect(req.getContextPath() + "/admin/parametres");

        } catch (IllegalArgumentException e) {
            // couvre aussi NumberFormatException
            req.setAttribute("erreur", e.getMessage());
            req.setAttribute("parametre", parametreService.get());
            req.getRequestDispatcher("/WEB-INF/views/admin/parametres.jsp").forward(req, resp);
        }
    }
}
