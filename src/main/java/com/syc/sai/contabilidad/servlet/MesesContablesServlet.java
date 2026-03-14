package com.syc.sai.contabilidad.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.contabilidad.CondicionCierreMes;
import com.syc.sai.contabilidad.MesContableBusinessLogic;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@WebServlet(name = "MesesContablesServlet", urlPatterns = { "/contabilidad/CierreDeMes" })
public class MesesContablesServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 3709991013139160920L;

    private static final Logger logSrvlt = LoggerFactory.getLogger(MesesContablesServlet.class);

    List<CondicionCierreMes> condiciones;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null)
            ResponseSender.sendError(resp, "La sesion ha finalizado. Por favor reingrese al sistema.");
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null)
            ResponseSender.sendError(resp, "La sesion no contiene usuario. Por favor reingrese al sistema.");
        String action = req.getParameter("action");
        if ("CIERRA_MES_CONTABLE".equals(action)) {
            try {
                MesContableBusinessLogic mcbl = new MesContableBusinessLogic(condiciones);
                //String masivo = req.getParameter("masivo");
                String nMes = req.getParameter("nMes");
                //String mes = req.getParameter("dmes");
                String aEjercicioFiscal = req.getParameter("aEjercicioFiscal");
                String cCentroContable = req.getParameter("cCentroContable");
                String cUnidadResponsable = req.getParameter("cUnidadResponsable");
                /*if("S".equals( masivo ))
					nMes = mes;
				*/
                if (nMes == null || "".equals(nMes) || aEjercicioFiscal == null || "".equals(aEjercicioFiscal))
                    ResponseSender.sendArrayMessages(resp, true, new String[] { "Se requiere al menos el ejercicio fiscal y el mes a cerrar." }, "msg");
                else {
                    String result = mcbl.cierraMesContable(Integer.parseInt(nMes), Integer.parseInt(aEjercicioFiscal), cCentroContable, u.getLogin(), cUnidadResponsable);
                    if ("".equals(result) || null == result)
                        ResponseSender.sendArrayMessages(resp, true, new String[] { "Mes cerrado exitosamente para " + (cUnidadResponsable == null || "".equals(cUnidadResponsable) ? "todas las Unidades Responsables" : "la Unidad Responsable " + cUnidadResponsable) }, "msg");
                    else
                        ResponseSender.sendArrayMessages(resp, true, new String[] { result }, "msg");
                }
            } catch (Exception e) {
                ResponseSender.sendError(resp, "Error cerrando el mes contable. " + e.toString());
            }
        } else if ("ABRE_MES_CONTABLE".equals(action)) {
            try {
                MesContableBusinessLogic mcbl = new MesContableBusinessLogic(condiciones);
                String nMes = req.getParameter("nMes");
                String aEjercicioFiscal = req.getParameter("aEjercicioFiscal");
                String cCentroContable = req.getParameter("cCentroContable");
                String cUnidadResponsable = req.getParameter("cUnidadResponsable");
                if (nMes == null || "".equals(nMes) || aEjercicioFiscal == null || "".equals(aEjercicioFiscal))
                    ResponseSender.sendArrayMessages(resp, true, new String[] { "Se requiere al menos el ejercicio fiscal y el mes a cerrar." }, "msg");
                else {
                    String result = mcbl.abreMesContable(Integer.parseInt(nMes), Integer.parseInt(aEjercicioFiscal), cCentroContable, u.getLogin(), cUnidadResponsable);
                    if ("".equals(result) || null == result)
                        ResponseSender.sendArrayMessages(resp, true, new String[] { "Mes cerrado exitosamente " + (cUnidadResponsable == null || "".equals(cUnidadResponsable) ? "todas las Unidades Responsables" : "la Unidad Responsable " + cUnidadResponsable) }, "msg");
                    else
                        ResponseSender.sendArrayMessages(resp, true, new String[] { result }, "msg");
                }
            } catch (Exception e) {
                ResponseSender.sendError(resp, "Error cerrando el mes contable. " + e.toString());
            }
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        Enumeration<?> e = getServletConfig().getInitParameterNames();
        condiciones = new ArrayList<CondicionCierreMes>();
        for (; e.hasMoreElements(); ) {
            String name = (String) e.nextElement();
            if (name.contains("condicion")) {
                String val = getServletConfig().getInitParameter(name);
                try {
                    ClassLoader cl = getClass().getClassLoader();
                    Class<?> clase = cl.loadClass(val);
                    CondicionCierreMes condicion = (CondicionCierreMes) clase.newInstance();
                    condiciones.add(condicion);
                } catch (Exception ex) {
                    logSrvlt.error("Error GRAVE: No fue posible instanciar la condicion: " + val + " debido al error " + ex, ex);
                }
            }
        }
    }
}
