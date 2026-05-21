package sn.association.cotisations.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sn.association.cotisations.dao.ConnexionDAO;

import java.io.IOException;

/**
 * GET /admin/connexions → liste des dernières connexions (200 max).
 */
@WebServlet(name = "AdminConnexionsServlet", urlPatterns = {"/admin/connexions"})
public class AdminConnexionsServlet extends HttpServlet {

    private static final int MAX = 200;

    private final ConnexionDAO connexionDAO = new ConnexionDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("connexions", connexionDAO.findRecents(MAX));
        req.setAttribute("total", connexionDAO.count());
        req.setAttribute("limite", MAX);
        req.getRequestDispatcher("/WEB-INF/views/admin/connexions-list.jsp").forward(req, resp);
    }
}
