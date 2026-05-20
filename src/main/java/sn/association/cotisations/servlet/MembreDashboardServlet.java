package sn.association.cotisations.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sn.association.cotisations.dao.AmendeDAO;
import sn.association.cotisations.dao.CotisationDAO;
import sn.association.cotisations.entity.Cotisation;
import sn.association.cotisations.entity.Membre;
import sn.association.cotisations.service.CotisationService;
import sn.association.cotisations.service.MoisDu;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "MembreDashboardServlet", urlPatterns = {"/membre/dashboard"})
public class MembreDashboardServlet extends HttpServlet {

    private final CotisationDAO cotisationDAO = new CotisationDAO();
    private final AmendeDAO amendeDAO = new AmendeDAO();
    private final CotisationService cotisationService = new CotisationService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Membre user = (Membre) req.getSession().getAttribute("user");
        int annee = LocalDate.now().getYear();

        BigDecimal totalPaye = cotisationDAO.sumMontantsByMembreAnnee(user.getNumero(), annee);
        BigDecimal totalAmendes = amendeDAO.sumImpayeesByMembre(user.getNumero());
        List<Cotisation> cotisations = cotisationDAO.findByMembre(user.getNumero())
                .stream().limit(10).toList();
        boolean moisCourantPaye = cotisationDAO.existsPaiementPourPeriode(
                user.getNumero(), LocalDate.now().getMonthValue(), annee);

        List<MoisDu> moisDus = cotisationService.moisDusParMembre(user);
        BigDecimal montantDu = cotisationService.montantTotalDu(user);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPaye", totalPaye);
        stats.put("totalAmendes", totalAmendes);
        stats.put("cotisationsAjour", moisCourantPaye);
        stats.put("nbMoisDus", moisDus.size());
        stats.put("montantDu", montantDu);

        req.setAttribute("stats", stats);
        req.setAttribute("cotisations", cotisations);
        req.setAttribute("moisDus", moisDus);
        req.getRequestDispatcher("/WEB-INF/views/member/dashboard.jsp").forward(req, resp);
    }
}
