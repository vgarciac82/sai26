package com.syc.egresos.servlet;

import java.io.IOException;
import java.math.BigDecimal;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.syc.egresos.DetallePago;
import com.syc.egresos.PagoCalendarioBussinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.contabilidad.servlet.ResponseSender;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "RegistroCalendarioServlet", urlPatterns = { "/egresos/GeneraCalendario" })
public class RegistroCalendarioServlet extends HttpServlet implements GestionInterface {

    private static final Logger log = LoggerFactory.getLogger(RegistroCalendarioServlet.class);

    private static final long serialVersionUID = -2666876165571640076L;

    private String jniName;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            ResponseSender.sendClientSimpleMessage(resp, false, "Su sesion a caducado. Por favor reingrese al sistema.");
            return;
        }
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null) {
            ResponseSender.sendClientSimpleMessage(resp, false, "Su sesion a caducado. Por favor reingrese al sistema.");
            return;
        }
        Caso c = (Caso) session.getAttribute(ATT_CASE);
        if (c == null) {
            ResponseSender.sendClientSimpleMessage(resp, false, "Su sesion a caducado. Por favor reingrese al sistema.");
            return;
        }
        try {
            String tipoPago = c.getTipoCaso().getGavetaAsociada();
            int folioPago = Integer.parseInt(c.getFolio().substring(9));
            BigDecimal importeBruto = new BigDecimal(req.getParameter("importeEP"));
            String ep = req.getParameter("ep");
            String idTipoConcepto = req.getParameter("idTipoConcepto");
            String idTipoMovimiento = req.getParameter("idTipoMovimiento");
            DetallePago renglon = new DetallePago();
            renglon.setEp(ep);
            renglon.setTipoPago(tipoPago);
            renglon.setFolioPago(folioPago);
            renglon.setImporteBruto(importeBruto);
            renglon.setIdTipoConcepto(idTipoConcepto);
            renglon.setIdTipoMovimiento(idTipoMovimiento);
            renglon.setImporteRetencion(new BigDecimal("0.00"));
            PagoCalendarioBussinessLogic pcbl = new PagoCalendarioBussinessLogic(jniName);
            int insertados = pcbl.insertaCalendarioPagoCompromiso(renglon);
            ResponseSender.sendClientSimpleMessage(resp, true, String.valueOf(insertados));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ResponseSender.sendClientSimpleMessage(resp, false, e.toString());
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            InitialContext ic = new InitialContext();
            jniName = (String) ic.lookup("java:comp/env/dataSourceRefName");
            if (jniName == null) {
                jniName = "jdbc/gestion";
                log.info("Object: {}", "Environment Entry \"dataSourceRefName\" nula usando default \"" + jniName + "\"");
            } else
                log.info("Object: {}", "dataSourceRefName=" + jniName);
        } catch (NamingException exc) {
            jniName = "jdbc/gestion";
            log.info("Object: {}", "Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
        }
    }
}
