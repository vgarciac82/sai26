/**
 * VGC-30/09/2015 Se agregan las notas de credito. Una nota de credito es identica a las facturas, con la diferencia que estas restan su valor a una factura. Debido a que no se encontro documentacion acerca de como identificar una NC de una factura, se agrego un selector para que el usuario lo indique.
 * Cuando se trata de una NC el unico cambio es que se insertan los valores en negativo y su bandera se enciende en 1 en la tabla tPagoFactura.
 * NOTA IMPORTANTE seria ideal que las NC se guardaran en una carpeta especial y no en la comun de CFDI dentro de la estructura de carpetas.
 */
package com.syc.cfdi.servlet;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
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
import org.apache.log4j.Logger;
import com.syc.cfdi.ComponentesFactura;
import com.syc.cfdi.FacturaBusinessLogic;
import com.syc.cfdi.core.ExtraccionFacturas;
import com.syc.cfdi.core.ReemplazoCFDIBusinessLogic;
import com.syc.cfdi.utils.FacturaUtils;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "ReemplazaFacturasServlet", urlPatterns = { "/reemplazoFacturas" })
public class ReemplazaFacturasZipServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = -795967476374791070L;

    private static String jniName = "jdbc/gestion";

    private static final Logger log = Logger.getLogger(ReemplazaFacturasZipServlet.class);

    private static String TEMP_DIR = "";

    private static final String RFC_NO_VALIDAR = "MET8908305M9";

    private ReemplazoCFDIBusinessLogic reemplazaFacturasBL;

    private ConfiguraAplicativoBusinessLogic cabl;

    private FacturaBusinessLogic fbl;

    private boolean validaContraSAT = false;

    private boolean notificaFacturasInvalidasSAT = false;

    private boolean notificaFacturasEFA = false;

    private boolean permiteFacturasVersionAnterior = false;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        String msgRetorno = "";
        Usuario u = null;
        String tipoPago = "";
        String tipoModulo = "";
        String contrarecibo = "";
        /*
		 * VGC20231115 Se agrega lectura de adenda de vales de combustible.
		 */
        boolean contratoValesCombustible = false;
        if (session == null) {
            msgRetorno = "Su sesion a terminado. Por favor ingrese nuevamente al sistema.";
        } else {
            u = (Usuario) session.getAttribute(ATT_USER);
            if (u == null) {
                msgRetorno = "Su sesion a terminado. Por favor ingrese nuevamente al sistema.";
            }
        }
        if ("".equals(msgRetorno)) {
            fbl.setUsuario(u);
            List<?> fileItems = null;
            Iterator<?> iter = null;
            DataInputStream archivoCargaStream = null;
            String nombreDestino = "";
            try {
                fileItems = Util.parseRequest(req, ReemplazaFacturasZipServlet.TEMP_DIR, -1);
                iter = fileItems.iterator();
                String nombreArchivo = "";
                String rfc = "";
                while (iter.hasNext()) {
                    FileItem item = (FileItem) iter.next();
                    if (item.isFormField()) {
                        if ("tipoPago".equals(item.getFieldName()))
                            tipoPago = item.getString();
                        else if ("cxp".equals(item.getFieldName()))
                            contrarecibo = StringUtils.trimToEmpty(item.getString());
                        item.delete();
                        continue;
                    }
                    archivoCargaStream = new DataInputStream(item.getInputStream());
                    nombreArchivo = item.getName();
                    String extension = Util.getFileExtencion(nombreArchivo);
                    if (!"zip".equalsIgnoreCase(extension))
                        throw new Exception("No se puede procesar archivos [" + extension + "] Corrija e intente de nuevo");
                    log.info("Copiando archivo :" + nombreArchivo);
                    nombreDestino = FacturaUtils.generaNombreZip(TEMP_DIR, extension);
                    Util.copiaArchivo(archivoCargaStream, nombreDestino);
                    item.delete();
                }
                int folioPago = reemplazaFacturasBL.getFolioPago(tipoPago, contrarecibo);
                rfc = StringUtils.trimToEmpty(reemplazaFacturasBL.getRFCPago(tipoPago, contrarecibo));
                boolean validaPagos = !(RFC_NO_VALIDAR.equalsIgnoreCase(StringUtils.trimToEmpty(rfc)));
                ExtraccionFacturas ef = fbl.extraeFacturas(nombreDestino, tipoPago, StringUtils.trim(rfc), folioPago, validaPagos, false, tipoModulo);
                if (ef.getErrores().size() == 0) {
                    Map<String, ComponentesFactura> facturas = ef.getFacturas();
                    reemplazaFacturasBL.reemplazaFacturas(tipoPago, folioPago, rfc, u, facturas);
                } else {
                    String token = "";
                    for (int i = 0; i < ef.getErrores().size(); i++) {
                        msgRetorno += token + ef.getErrores().get(i);
                        token = "<br>";
                    }
                    throw new Exception(msgRetorno);
                }
                msgRetorno = "Archivo cargado exitosamente";
            } catch (Exception e) {
                log.error(e, e);
                msgRetorno = "Ocurrio el siguiente error al cargar el archivo: <br>" + e.getMessage();
            } finally {
                if (archivoCargaStream != null)
                    try {
                        archivoCargaStream.close();
                    } catch (Exception e) {
                        log.error("Error cerrando flujo DataInputStream" + e);
                    }
                archivoCargaStream = null;
                if (!"".equals(nombreDestino)) {
                    File toDelete = new File(nombreDestino);
                    if (!toDelete.delete())
                        toDelete.deleteOnExit();
                }
            }
        } else {
            resp.sendRedirect("CFDI/ResultadoCarga.jsp?tipo_pago=" + tipoPago + "&contratoVales=" + contratoValesCombustible + "&msgError=" + msgRetorno);
        }
        session.setAttribute("RESULT", msgRetorno);
        resp.sendRedirect("CFDI/ResultadoCarga.jsp?tipo_pago=" + tipoPago + "&contratoVales=" + contratoValesCombustible);
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
            cabl = new ConfiguraAplicativoBusinessLogic(jniName);
            initConditions();
        } catch (Exception exc) {
            jniName = "jdbc/gestion";
            log.info("Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
        }
        try {
            InitialContext ic = new InitialContext();
            TEMP_DIR = (String) ic.lookup("java:comp/env/TemporaryDirectory");
            if (TEMP_DIR == null) {
                TEMP_DIR = "../upload/Facturas/";
                log.info("Environment Entry \"TEMP_DIR\" nula usando default \"" + TEMP_DIR + "\"");
            } else
                log.info("dataSourceRefName=" + TEMP_DIR);
        } catch (NamingException exc) {
            TEMP_DIR = "../upload/Facturas/";
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

    private final void initConditions() throws Exception {
        /*
		 * VGC20160802 Se agrega cambio para validar mediante el WS del SAT que
		 * el CFDI exista y este vigente.
		 */
        validaContraSAT = "S".equalsIgnoreCase(cabl.getSystemSetting("ACTIVA_VALIDACION_SAT"));
        notificaFacturasEFA = "S".equalsIgnoreCase(cabl.getSystemSetting("NOTIFICA_ERROR_VALIDACION_EFA"));
        notificaFacturasInvalidasSAT = "S".equalsIgnoreCase(cabl.getSystemSetting("NOTIFICA_ERROR_VALIDACION_SAT"));
        permiteFacturasVersionAnterior = "S".equalsIgnoreCase(cabl.getSystemSetting("PERMITE_VERSION_MENOR"));
        fbl = new FacturaBusinessLogic(jniName, validaContraSAT, null);
        /*
		 * VGC20160802 Se agrega cambio para validar mediante el WS del SAT que
		 * el CFDI exista y este vigente.
		 */
        fbl.setNotificaErroresSAT(notificaFacturasInvalidasSAT);
        fbl.setNotificaErroresEFA(notificaFacturasEFA);
        fbl.setPermiteVersionAnterior(permiteFacturasVersionAnterior);
        reemplazaFacturasBL = new ReemplazoCFDIBusinessLogic(jniName, fbl);
    }
}
