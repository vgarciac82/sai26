package com.syc.obrapublica.servlet;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.obrapublica.ObraPublicaEPBusinessLogic;
import com.syc.obrapublica.ProimproInfoReaderBusinessLogic;
import com.syc.sai.contabilidad.servlet.ResponseSender;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "ProimproInterfaz", urlPatterns = { "/proimpro/readInfo" })
public class ObraPublicaInterfazProimproServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 6236774195948623837L;

    private static final Logger log = LoggerFactory.getLogger(ObraPublicaInterfazProimproServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String cartera;
        String oli;
        String ur;
        String ue;
        String capitulo;
        String action;
        String folioSAI = "";
        boolean sinOli = false;
        HttpSession session = req.getSession(false);
        Usuario u = null;
        if (session == null) {
            ResponseSender.sendError(resp, "Su sesion a terminado. Ingrese nuevamente al sistema");
            return;
        }
        u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null) {
            ResponseSender.sendError(resp, "Su sesion a terminado. Ingrese nuevamente al sistema");
            return;
        }
        action = req.getParameter("accion");
        ProimproInfoReaderBusinessLogic pirbl = new ProimproInfoReaderBusinessLogic();
        cartera = req.getParameter("cartera");
        oli = req.getParameter("oli");
        ur = req.getParameter("ur");
        ue = req.getParameter("ue");
        folioSAI = req.getParameter("folioSAI");
        if ("1".equalsIgnoreCase(req.getParameter("sinOli")))
            sinOli = true;
        if ("READ_CARTERA".equals(action)) {
            try {
                List<String> carteras = null;
                ObraPublicaEPBusinessLogic opepbl = new ObraPublicaEPBusinessLogic();
                if (sinOli)
                    carteras = opepbl.getCarterasSinOli(ur, ue);
                else
                    carteras = pirbl.getCarterasProyecto(cartera, ur, ue);
                ResponseSender.sendArrayMessages(resp, true, carteras.toArray(new String[carteras.size()]), "cartera");
            } catch (Exception e) {
                ResponseSender.sendError(resp, e.toString());
                log.error(e.getMessage(), e);
            }
        } else if ("READ_OLIS".equals(action)) {
            try {
                List<String> olis = pirbl.getOlis(cartera, ur, ue);
                ResponseSender.sendArrayMessages(resp, true, olis.toArray(new String[olis.size()]), "oli");
            } catch (Exception e) {
                ResponseSender.sendError(resp, e.toString());
                log.error(e.getMessage(), e);
            }
        } else if ("READ_IMPORTEOLI".equals(action)) {
            try {
                capitulo = req.getParameter("capitulo");
                String importeOli = pirbl.getImporteOliCarteraURUE(cartera, capitulo, oli, ur, ue);
                ResponseSender.sendClientSimpleMessage(resp, true, importeOli);
            } catch (Exception e) {
                ResponseSender.sendError(resp, e.toString());
                log.error(e.getMessage(), e);
            }
        } else if ("READ_EPS".equals(action)) {
            try {
                List<String> clavesPresupuestales = null;
                ObraPublicaEPBusinessLogic opepbl = new ObraPublicaEPBusinessLogic();
                if (sinOli)
                    clavesPresupuestales = opepbl.getEPsSinOli(cartera, oli, ur, ue, folioSAI);
                //Conafor no cuenta con una interfaz. Se leen todas las eps que contengan la cartera en la UE y UN
                //else
                //clavesPresupuestales = pirbl.getEPs(cartera, oli, ur, ue);
                List<String> eps = opepbl.getEPs(u.getLogin(), cartera, oli, ur, ue);
                ResponseSender.sendArrayMessages(resp, true, eps.toArray(new String[eps.size()]), "oli");
            } catch (Exception e) {
                ResponseSender.sendError(resp, e.toString());
                log.error(e.getMessage(), e);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // TODO Auto-generated method stub
        super.doPost(req, resp);
    }
}
