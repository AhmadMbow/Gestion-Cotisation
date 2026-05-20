package sn.association.cotisations.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sn.association.cotisations.entity.Cotisation;
import sn.association.cotisations.entity.Membre;
import sn.association.cotisations.entity.Role;
import sn.association.cotisations.service.CotisationService;

/**
 * Génère un reçu HTML imprimable (window.print()).
 * - Un membre peut voir uniquement ses propres reçus
 * - Un admin peut voir tous les reçus
 *
 * Accessible aussi bien à /membre/recu qu'à /admin/recu pour des raisons d'AuthFilter.
 */
@WebServlet(name = "RecuServlet", urlPatterns = {"/membre/recu", "/admin/recu"})
public class RecuServlet extends HttpServlet {

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

        req.setAttribute("cotisation", c);
        req.getRequestDispatcher("/WEB-INF/views/common/recu.jsp").forward(req, resp);
    }
}
