package com.syc.gestion.servlet;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;
import com.syc.cfdi.utils.FacturaUtils;
import com.syc.egresos.core.RelacionGastosMasivaBussinessLogic;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import com.syc.sai.firmaElectronica.FirmaElectronicaBusinessLogic;
import com.syc.sai.firmaElectronica.core.FIELChecker;
import com.syc.sai.firmaElectronica.interfaces.SolicitudFirmaElectronica;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "FirmaSolicitudPagoServlet", urlPatterns = { "/firmaSolicitudPago" })
public class FirmaSolicitudPagoServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = -7166232175690426826L;

    private static String TEMP_DIR = "";

    private static String jniName = "jdbc/gestion";

    private static final Logger log = LoggerFactory.getLogger(FirmaSolicitudPagoServlet.class);

    private ConfiguraAplicativoBusinessLogic settings = null;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        String msgRetorno = "";
        Usuario u = null;
        String tipoAutorizacion = "";
        String urlRespuesta = "Generador/AutorizaGeneracionLayouts.jsp";
        String action = "";
        Map<String, String> objMap = new HashMap<String, String>();
        boolean esTramiteMasivo = false;
        int daysTolerance = (settings.getSystemSetting("DAYS_TOLERANCE") == null ? 30 : Integer.parseInt(settings.getSystemSetting("DAYS_TOLERANCE")));
        if (session == null) {
            msgRetorno = "Su sesion a terminado. Por favor ingrese nuevamente al sistema.";
        } else {
            u = (Usuario) session.getAttribute(ATT_USER);
            if (u == null) {
                msgRetorno = "Su sesion a terminado. Por favor ingrese nuevamente al sistema.";
            }
        }
        if ("".equals(msgRetorno)) {
            String reportPath = getServletContext().getRealPath("Reportes" + File.separator);
            List<?> fileItems = null;
            Iterator<?> iter = null;
            String cerFileName = "";
            String keyFileName = "";
            DataInputStream archivoCargaStreamCer = null;
            DataInputStream archivoCargaStreamKey = null;
            try {
                fileItems = Util.parseRequest(req, FirmaSolicitudPagoServlet.TEMP_DIR, -1);
                iter = fileItems.iterator();
                while (iter.hasNext()) {
                    FileItem item = (FileItem) iter.next();
                    if (item.isFormField()) {
                        log.trace(item.getFieldName() + " = " + item.getString());
                        objMap.put(item.getFieldName(), item.getString());
                        if ("tipoAutorizacion".equalsIgnoreCase(item.getFieldName()))
                            tipoAutorizacion = item.getString();
                        item.delete();
                        continue;
                    } else {
                        if ("cerFile".equals(item.getFieldName())) {
                            archivoCargaStreamCer = new DataInputStream(item.getInputStream());
                            cerFileName = FacturaUtils.generaNombreArchivoTemporal(FirmaSolicitudPagoServlet.TEMP_DIR, item.getName(), "cer");
                            log.info("Copiando archivo :" + cerFileName);
                            Util.copiaArchivo(archivoCargaStreamCer, cerFileName);
                        } else if ("keyFile".equals(item.getFieldName())) {
                            archivoCargaStreamKey = new DataInputStream(item.getInputStream());
                            keyFileName = FacturaUtils.generaNombreArchivoTemporal(FirmaSolicitudPagoServlet.TEMP_DIR, item.getName(), "key");
                            log.info("Copiando archivo :" + keyFileName);
                            Util.copiaArchivo(archivoCargaStreamKey, keyFileName);
                            if (archivoCargaStreamKey != null)
                                try {
                                    archivoCargaStreamKey.close();
                                } catch (Exception e) {
                                    log.error("Error cerrando flujo DataInputStream" + e);
                                }
                        }
                    }
                    item.delete();
                }
                if (objMap.containsKey("urlRetorno"))
                    urlRespuesta = objMap.get("urlRetorno");
                esTramiteMasivo = "true".equalsIgnoreCase((String) (objMap.get("esTramiteMasivo")));
                action = ("VOBO".equals(tipoAutorizacion) ? "VoBoPago" : ("AUT".equals(tipoAutorizacion) ? "AutPago" : ("R_VOBO".equals(tipoAutorizacion) ? "RVoBoPago" : ("R_AUT".equals(tipoAutorizacion) ? "RAutPago" : ""))));
                String tipoPago = objMap.get("tipoPagoSeleccionado");
                FirmaElectronicaBusinessLogic febl = new FirmaElectronicaBusinessLogic(jniName);
                SolicitudFirmaElectronica sfe = febl.instanceFromWeb(tipoPago);
                if (esTramiteMasivo) {
                    int folioMasivo = Integer.parseInt(objMap.get("nFolios"));
                    RelacionGastosMasivaBussinessLogic rgmbl = new RelacionGastosMasivaBussinessLogic(jniName);
                    String folios = rgmbl.getFoliosIntegradosStr(folioMasivo);
                    sfe.setFolios(folios);
                    sfe.setMasiveHeader("tRELACIONGASTOSEncabezado_temp");
                    sfe.setMasiveDetail("tRELACIONGASTOSDetalle_temp");
                    sfe.setMasiveField("folioTempGral");
                    sfe.setMasiveID(folioMasivo);
                } else
                    sfe.setFolios(objMap.get("nFolios"));
                sfe.setPasswordLlave(objMap.get("passwordLlave"));
                sfe.setReportPath(reportPath);
                sfe.setRfcFirma(objMap.get("rfcFirma"));
                sfe.setTipoAutorizacion(objMap.get("tipoAutorizacion"));
                sfe.setUsuario(u);
                sfe.setCargaMasiva(esTramiteMasivo);
                if (StringUtils.isNotEmpty(objMap.get("folder")))
                    sfe.setFolder(objMap.get("folder"));
                if ("R_VOBO".equals(tipoAutorizacion))
                    sfe.setRefirma(true);
                try {
                    long daysUntilExpiry = FIELChecker.isCertificateExpiringSoon(cerFileName);
                    if (daysUntilExpiry <= daysTolerance) {
                        session.setAttribute("EXPIRING_SOON", true);
                        if (daysUntilExpiry <= 0)
                            session.setAttribute("EXPIRING_MSG", "Su firma electronica caducó desde hace: " + daysUntilExpiry + " dias. Por favor inicie la renovacion lo antes posible.");
                        else
                            session.setAttribute("EXPIRING_MSG", "Su firma electronica caducará en menos de " + daysUntilExpiry + " dias. Por favor inicie la renovacion lo antes posible.");
                    }
                } catch (Exception e) {
                    log.warn("No fue posible determinar la vigencia de la  FIEL " + e, e);
                }
                List<String> logFirma = febl.firmaDocumento(sfe, cerFileName, keyFileName);
                msgRetorno = Util.listToHTMLTable(logFirma);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                List<String> logException = new ArrayList<String>();
                logException.add("Ocurrio el siguiente error al cargar el archivo: <br>" + e.getMessage());
                msgRetorno = Util.listToHTMLTable(logException);
            } finally {
                if (archivoCargaStreamCer != null)
                    try {
                        archivoCargaStreamCer.close();
                    } catch (Exception e) {
                        log.error("Error cerrando flujo DataInputStream" + e);
                    }
                archivoCargaStreamCer = null;
                if (!"".equals(cerFileName)) {
                    File toDeleteCer = new File(cerFileName);
                    if (!toDeleteCer.delete())
                        toDeleteCer.deleteOnExit();
                }
                if (archivoCargaStreamKey != null)
                    try {
                        archivoCargaStreamKey.close();
                    } catch (Exception e) {
                        log.error("Error cerrando flujo DataInputStream" + e);
                    }
                archivoCargaStreamKey = null;
                if (!"".equals(keyFileName)) {
                    File toDeleteKey = new File(keyFileName);
                    if (!toDeleteKey.delete())
                        toDeleteKey.deleteOnExit();
                }
            }
        } else {
            resp.sendRedirect(urlRespuesta + "?TYPE=" + tipoAutorizacion + "&msgError=" + msgRetorno + "&a=" + action);
        }
        action = action + (esTramiteMasivo ? "Masivo" : "");
        session.setAttribute("RESULT", msgRetorno);
        resp.sendRedirect(urlRespuesta + "?TYPE=" + tipoAutorizacion + "&a=" + action + "&d=" + objMap.get("tipoPagoSeleccionado") + "&f=" + objMap.get("nFolios"));
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
        settings = new ConfiguraAplicativoBusinessLogic(jniName);
        try {
            InitialContext ic = new InitialContext();
            TEMP_DIR = (String) ic.lookup("java:comp/env/TemporaryDirectory");
            if (TEMP_DIR == null) {
                TEMP_DIR = "/temp/firmaElectronica/";
                log.info("Environment Entry \"TEMP_DIR\" nula usando default \"" + TEMP_DIR + "\"");
            } else
                log.info("dataSourceRefName=" + TEMP_DIR);
        } catch (NamingException exc) {
            TEMP_DIR = "/temp/firmaElectronica/";
            log.info("Ocurrio un error que evito que se cargara la entrada \"TEMP_DIR\"" + exc);
            log.info("Environment Entry \"dataSourceRefName\" no definida usando default \"" + TEMP_DIR + "\"");
        }
        try {
            File f = new File(TEMP_DIR);
            if (!f.exists())
                if (!f.mkdirs())
                    throw new Exception("No se puede crear el directorio temporal " + TEMP_DIR);
        } catch (Exception e) {
            log.error("No fue posible crear automaticamente el directorio temporal: " + TEMP_DIR + " Solicite su creacion manual");
        }
    }
}
