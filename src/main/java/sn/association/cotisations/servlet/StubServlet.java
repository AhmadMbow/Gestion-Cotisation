package sn.association.cotisations.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

/**
 * Servlet temporaire qui affiche une page "En construction" pour les modules pas encore implémentés.
 * À supprimer au fur et à mesure que chaque module est codé.
 */
@WebServlet(urlPatterns = {
        "/admin/exports",
        "/profil"
})
public class StubServlet extends HttpServlet {

    private static final Map<String, String[]> TITLES = Map.ofEntries(
            Map.entry("/admin/exports",             new String[]{"Export PDF / Excel",      "admin", "Export des données au format PDF (reçus, listes) et Excel."}),
            Map.entry("/profil",                    new String[]{"Mon profil",              "membre","Consulter et modifier vos informations personnelles."})
    );

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getServletPath();
        String[] info = TITLES.getOrDefault(path, new String[]{"En construction", "admin", ""});

        req.setAttribute("pageTitre", info[0]);
        req.setAttribute("sidebar", info[1]);
        req.setAttribute("description", info[2]);
        req.getRequestDispatcher("/WEB-INF/views/common/en-construction.jsp").forward(req, resp);
    }
}
