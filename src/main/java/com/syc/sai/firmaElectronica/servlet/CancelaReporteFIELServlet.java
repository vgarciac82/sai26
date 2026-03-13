package com.syc.sai.firmaElectronica.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import com.axtel.contratos.ActionsFIEL;
import com.axtel.contratos.QuestionnaireBussinessLogic;
import com.axtel.contratos.core.ImplementsActionsFIEL_ENSA;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.reportes.ConciliacionFirma;
import com.syc.gestion.reportes.EstadosFinancierosFirma;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.FirmaAutorizacionObraInterface;
import com.syc.obrapublica.businessLogic.FirmaAutEstObraBusinessLogic;
import com.syc.sai.firmaElectronica.FirmaElectronicaBusinessLogic;
import com.syc.sai.firmaElectronica.core.RecepcionMaterialFIEL;
import com.syc.sai.firmaElectronica.interfaces.SolicitudFirmaElectronica;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "CancelaReporteFIELServlet", urlPatterns = { "/firma/RechazaReporte", "/firma/rechazaTramite", "/firma/rechazaRM", "/firma/rechazaEstimacion", "/firma/rechazaENSA", "/firma/avanzaProcesoENSA" })
public class CancelaReporteFIELServlet extends HttpServlet implements GestionInterface {

    /**
     */
    private static final long serialVersionUID = -4124280043414289395L;

    private static String jniName = "jdbc/gestion";

    private static final Logger log = LoggerFactory.getLogger(CancelaReporteFIELServlet.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        String msgRetorno = "";
        Usuario u = null;
        String urlRespuesta = "../Generador/ReportesFirma.jsp";
        String tipoPago = null;
        int orden = -1;
        String foliosReporte = null;
        Map<String, String> objMap = new HashMap<String, String>();
        if (session == null) {
            msgRetorno = "Su sesion a terminado. Por favor ingrese nuevamente al sistema.";
        } else {
            u = (Usuario) session.getAttribute(ATT_USER);
            if (u == null) {
                msgRetorno = "Su sesion a terminado. Por favor ingrese nuevamente al sistema.";
            }
        }
        String idUsuario = StringUtils.reverse(u.getNumeroEmpleado());
        if ("".equals(msgRetorno)) {
            SolicitudFirmaElectronica fer = null;
            ActionsFIEL actions = null;
            FirmaElectronicaBusinessLogic febl = new FirmaElectronicaBusinessLogic(jniName);
            FirmaAutorizacionObraInterface firmaAut = null;
            try {
                Enumeration<?> parametros = req.getParameterNames();
                while (parametros.hasMoreElements()) {
                    String parametro = (String) parametros.nextElement();
                    log.trace(parametro + " = " + req.getParameter(parametro));
                    objMap.put(parametro, new String(StringUtils.trimToEmpty(req.getParameter(parametro)).getBytes("ISO-8859-1"), "UTF-8"));
                }
                if (objMap.containsKey("urlRetorno"))
                    urlRespuesta = objMap.get("urlRetorno");
                if (!objMap.containsKey("urlRetorno") && !StringUtils.isBlank(objMap.get("url_Retorno")))
                    urlRespuesta = objMap.get("url_Retorno");
                if (objMap.get("nOrden") != null)
                    orden = Integer.parseInt(objMap.get("nOrden"));
                tipoPago = objMap.get("cTipoPago");
                if ("CONTRATODIVERSO".equals(tipoPago)) {
                    fer = new RecepcionMaterialFIEL();
                    ((RecepcionMaterialFIEL) fer).setMotivoRechazo(objMap.get("motivoRechazo"));
                } else if ("APARTADO".equals(tipoPago)) {
                    fer = new QuestionnaireBussinessLogic();
                    fer.setUsuario(u);
                    ((QuestionnaireBussinessLogic) fer).setMotivoRechazo(objMap.get("motivoRechazo"));
                } else if ("OBRAPUBLICA".equals(tipoPago)) {
                    firmaAut = new FirmaAutEstObraBusinessLogic();
                } else if ("ENTERASATISFACCION".equals(tipoPago)) {
                    actions = new ImplementsActionsFIEL_ENSA();
                } else if ("CONCILIABANCOS".equals(tipoPago)) {
                    urlRespuesta = "../Generador/ConciliacionBancariaFirma.jsp";
                    fer = new ConciliacionFirma();
                    ((ConciliacionFirma) fer).setOrden(orden);
                    ((ConciliacionFirma) fer).setMotivoRechazo(objMap.get("motivoRechazo"));
                } else {
                    fer = new EstadosFinancierosFirma();
                    ((EstadosFinancierosFirma) fer).setOrden(orden);
                    ((EstadosFinancierosFirma) fer).setMotivoRechazo(objMap.get("motivoRechazo"));
                }
                foliosReporte = objMap.get("folios");
                if (!"OBRAPUBLICA".equals(tipoPago) && !"ENTERASATISFACCION".equals(tipoPago)) {
                    fer.setFolios(foliosReporte);
                    fer.setFileExtension("pdf");
                    fer.setPasswordLlave(objMap.get("passwordLlave"));
                    fer.setUsuario(u);
                    fer.setDocument(tipoPago);
                }
                List<String> logFirma = null;
                if ("CONTRATODIVERSO".equals(tipoPago)) {
                    if (objMap.get("esAutorizaVoBo") != null && "1".equalsIgnoreCase(objMap.get("esAutorizaVoBo"))) {
                        logFirma = febl.autVoBoRM((RecepcionMaterialFIEL) fer);
                    } else {
                        logFirma = febl.rechazaRM((RecepcionMaterialFIEL) fer);
                    }
                } else if ("APARTADO".equals(tipoPago))
                    logFirma = febl.rechazaRequisicion((QuestionnaireBussinessLogic) fer);
                else if ("OBRAPUBLICA".equals(tipoPago))
                    logFirma = firmaAut.rechaza(u, objMap);
                else if ("ENTERASATISFACCION".equals(tipoPago)) {
                    if ("1".equalsIgnoreCase(objMap.get("nAvanzaProceso"))) {
                        logFirma = actions.aceptProcess(u, objMap);
                    } else {
                        logFirma = actions.reject(u, objMap);
                    }
                } else if ("CONCILIABANCOS".equals(tipoPago))
                    logFirma = febl.rechazaConciliacion((ConciliacionFirma) fer);
                else
                    logFirma = febl.rechazaReporte((EstadosFinancierosFirma) fer);
                msgRetorno = Util.listToHTMLTable(logFirma);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                List<String> logException = new ArrayList<String>();
                logException.add("Ocurrio el siguiente error al cargar el archivo: <br>" + e.getMessage());
                msgRetorno = Util.listToHTMLTable(logException);
            } finally {
                fer = null;
                febl = null;
                firmaAut = null;
                actions = null;
            }
        } else {
            resp.sendRedirect(urlRespuesta + "?msgError=" + msgRetorno + "&a=" + orden + "&o=" + orden + "&u=" + idUsuario);
        }
        session.setAttribute("RESULT", msgRetorno);
        resp.sendRedirect(urlRespuesta + "?a=" + orden + "&d=" + objMap.get("cTipoPago") + "&f=" + foliosReporte + "&o=" + orden + "&u=" + idUsuario);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            InitialContext ic = new InitialContext();
            jniName = (String) ic.lookup("java:comp/env/dataSourceRefName");
            if (jniName == null) {
                jniName = "jdbc/gestion";
                log.info("Environment Entry \"dataSourceRefName\" nula usando default \"" + jniName + "\"");
            } else
                log.info("dataSourceRefName=" + jniName);
        } catch (NamingException exc) {
            jniName = "jdbc/gestion";
            log.info("Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
        }
    }
}
