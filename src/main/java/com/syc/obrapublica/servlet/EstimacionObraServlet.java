package com.syc.obrapublica.servlet;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.obrapublica.ObraPublicaBusinessLogic;
import com.syc.sai.contabilidad.servlet.ResponseSender;
import com.syc.ws.obrapublica.core.EstimacionObra;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "EstimacionObraServlet", urlPatterns = { "/EstimacionObra" })
public class EstimacionObraServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 960968562779591000L;

    private static final Logger log = LoggerFactory.getLogger(ObraPublicaServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            ResponseSender.sendError(resp, "Sin session. Por favor reingrese al sistema.");
            return;
        }
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null) {
            ResponseSender.sendError(resp, "Sin usuario en session. Por favor reingrese al sistema.");
            return;
        }
        String uri = req.getRequestURI();
        String action = uri.substring(uri.lastIndexOf("/") + 1, uri.length());
        log.debug(action);
        if ("EstimacionObra".equals(action)) {
            ObraPublicaBusinessLogic Obra = new ObraPublicaBusinessLogic();
            try {
                EstimacionObra estimacion = EstimacionObra.instanceFromRequest(req);
                Obra.registraEstimacionObra(estimacion, u);
                ResponseSender.sendClientSimpleMessage(resp, true, "Estimacion registrada exitosamente.");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                ResponseSender.sendClientSimpleMessage(resp, false, "Ocurrio el siguiente error al insertar la estimacion de obra: " + e);
            }
            return;
        }
    }
}
