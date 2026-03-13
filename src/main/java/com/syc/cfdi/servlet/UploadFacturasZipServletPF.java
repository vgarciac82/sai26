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
import com.jenkov.prizetags.tree.itf.ITree;
import com.syc.cfdi.ComponentesFactura;
import com.syc.cfdi.FacturaBusinessLogic;
import com.syc.cfdi.core.ExtraccionFacturas;
import com.syc.cfdi.utils.FacturaUtils;
import com.syc.contable.AdecuacionBusinessLogic;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import com.syc.sai.contabilidad.servlet.ResponseSender;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "UploadFacturasServletPF", urlPatterns = { "/uploadFacturasPF" })
public class UploadFacturasZipServletPF extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = -795967476374791070L;

    private static String jniName = "jdbc/gestion";

    private static final Logger log = LoggerFactory.getLogger(UploadFacturasZipServletPF.class);

    private static String TEMP_DIR = "";

    private boolean validaContraSAT = false;

    private boolean notificaFacturasInvalidasSAT = false;

    private boolean notificaFacturasEFA = false;

    private boolean permiteFacturasVersionAnterior = false;

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        if (session == null)
            ResponseSender.sendClientSimpleMessage(resp, false, "Su sesion a caducado. Por favor reingrese al sistema.");
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null)
            ResponseSender.sendClientSimpleMessage(resp, false, "Su sesion a caducado. Por favor reingrese al sistema.");
        Caso c = (Caso) session.getAttribute(ATT_CASE);
        if (c == null)
            ResponseSender.sendClientSimpleMessage(resp, false, "Su sesion a caducado. Por favor reingrese al sistema.");
        String accion = req.getParameter("accion");
        if (StringUtils.isEmpty(accion))
            ResponseSender.sendClientSimpleMessage(resp, false, "Llamada sin el parametro accion.");
        if ("NUM_FACTURAS".equals(accion)) {
            try {
                /*
				 * VGC20160802 Se agrega cambio para validar mediante el WS del
				 * SAT que el CFDI exista y este vigente.
				 */
                FacturaBusinessLogic fbl = new FacturaBusinessLogic(jniName, validaContraSAT, u);
                fbl.setNotificaErroresSAT(notificaFacturasInvalidasSAT);
                fbl.setNotificaErroresEFA(notificaFacturasEFA);
                fbl.setPermiteVersionAnterior(permiteFacturasVersionAnterior);
                int nFolioPago = Integer.parseInt(req.getParameter("folioPago"), 10);
                boolean esPagoConFacturas = fbl.esPagoConFacturas(c.getTipoCaso().getGavetaAsociada(), nFolioPago);
                ResponseSender.sendClientSimpleMessage(resp, true, String.valueOf(esPagoConFacturas));
            } catch (Exception e) {
                ResponseSender.sendClientSimpleMessage(resp, false, "Ocurrio el siguiente error: " + e.toString());
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        String msgRetorno = "";
        Usuario u = null;
        Caso c = null;
        String tipoPago = "";
        String tipoModulo = "";
        String tipoComprobacion = "";
        String noOficio = "";
        String monto = "";
        String rfc = "";
        if (session == null) {
            msgRetorno = "Su sesion a terminado. Por favor ingrese nuevamente al sistema.";
        } else {
            u = (Usuario) session.getAttribute(ATT_USER);
            c = (Caso) session.getAttribute(ATT_CASE);
            if (c == null || u == null) {
                msgRetorno = "Su sesion a terminado. Por favor ingrese nuevamente al sistema.";
            }
        }
        if ("".equals(msgRetorno)) {
            CasoBusinessLogic cbl = new CasoBusinessLogic(jniName);
            List<?> fileItems = null;
            Iterator<?> iter = null;
            DataInputStream archivoCargaStream = null;
            String nombreDestino = "";
            try {
                if (c.getIdGabinete() == -1) {
                    AdecuacionBusinessLogic adbl = new AdecuacionBusinessLogic(jniName);
                    c.getCasoDato("FOLIO").setValor(c.getFolio());
                    c.getCasoDato("FECHA_DOCUMENTO").setValor(Util.getToday());
                    c.getCasoDato("EJERCICIO_FISCAL").setValor(adbl.obtenEjercicioFiscal());
                    c.getCasoDato("OPERADOR").setValor(c.getCasoOperacion(0).getResponsable());
                    c.setIdGabinete(cbl.creaExpediente(u.getLogin(), c));
                }
                fileItems = Util.parseRequest(req, UploadFacturasZipServletPF.TEMP_DIR, -1);
                iter = fileItems.iterator();
                String nombreArchivo = "";
                while (iter.hasNext()) {
                    FileItem item = (FileItem) iter.next();
                    if (item.isFormField()) {
                        if ("tipo_pago".equals(item.getFieldName()))
                            tipoPago = item.getString();
                        else if ("tipoComprobacion".equals(item.getFieldName()))
                            tipoComprobacion = item.getString();
                        else if ("noOficio".equals(item.getFieldName()))
                            noOficio = item.getString();
                        else if ("monto".equals(item.getFieldName()))
                            monto = item.getString();
                        else if ("cIdRFC".equals(item.getFieldName()))
                            rfc = StringUtils.deleteWhitespace(item.getString());
                        item.delete();
                        continue;
                    }
                    monto.replace("$", "");
                    archivoCargaStream = new DataInputStream(item.getInputStream());
                    nombreArchivo = item.getName();
                    String extension = Util.getFileExtencion(nombreArchivo);
                    if (!"zip".equalsIgnoreCase(extension) && ("comprobacion".equals(tipoComprobacion) || "credito".equalsIgnoreCase(tipoComprobacion)))
                        throw new Exception("No se puede procesar archivos [" + extension + "] Corrija e intente de nuevo");
                    nombreDestino = FacturaUtils.generaNombreZip(UploadFacturasZipServletPF.TEMP_DIR, extension);
                    log.info("Copiando archivo :" + nombreArchivo);
                    Util.copiaArchivo(archivoCargaStream, nombreDestino);
                    item.delete();
                }
                if ("comprobacion".equals(tipoComprobacion) || "credito".equals(tipoComprobacion))
                    cargaFacturasPF(nombreDestino, tipoPago, c, u, Double.parseDouble(monto), rfc, "credito".equalsIgnoreCase(tipoComprobacion), tipoModulo);
                else
                    insertaOficioCtoFederalizado(c, u, nombreDestino, noOficio, Double.parseDouble(monto));
                ITree tree = cbl.getArbolCaso(c);
                session.setAttribute(ATT_CASE, c);
                session.setAttribute("tree.model", tree);
                msgRetorno = "Archivo cargado exitosamente";
            } catch (Exception e) {
                log.error(e.getMessage(), e);
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
            resp.sendRedirect("Generador/UploadFacturasPF.jsp?tipo_pago=" + tipoPago + "&msgError=" + msgRetorno);
        }
        session.setAttribute("RESULT", msgRetorno);
        resp.sendRedirect("Generador/UploadFacturasPF.jsp?tipo_pago=" + tipoPago);
    }

    private void cargaFacturasPF(String nombreDestino, String tipoPago, Caso c, Usuario u, double montoNoComprobable, String rfc, boolean esNotaDeCredito, String tipoModulo) throws Exception {
        String msgRetorno = "";
        /*
		 * VGC20160802 Se agrega cambio para validar mediante el WS del SAT que
		 * el CFDI exista y este vigente.
		 */
        FacturaBusinessLogic fbl = new FacturaBusinessLogic(jniName, validaContraSAT, u);
        fbl.setNotificaErroresSAT(notificaFacturasInvalidasSAT);
        fbl.setNotificaErroresEFA(notificaFacturasEFA);
        fbl.setPermiteVersionAnterior(permiteFacturasVersionAnterior);
        int nFolioPago = Integer.parseInt(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1), 10);
        ExtraccionFacturas ef = fbl.extraeFacturas(nombreDestino, tipoPago, rfc, nFolioPago, true, esNotaDeCredito, tipoModulo);
        if (ef.getErrores().size() == 0) {
            Map<String, ComponentesFactura> facturas = ef.getFacturas();
            fbl.insertaFacturas(c, u, facturas, esNotaDeCredito);
        } else {
            String token = "";
            for (int i = 0; i < ef.getErrores().size(); i++) {
                msgRetorno += token + ef.getErrores().get(i);
                token = "<br>";
            }
            throw new Exception(msgRetorno);
        }
    }

    private void insertaOficioCtoFederalizado(Caso c, Usuario u, String rutaArchivo, String noOficio, double montoOficio) throws Exception {
        /*
		 * VGC20160802 Se agrega cambio para validar mediante el WS del SAT que
		 * el CFDI exista y este vigente.
		 */
        FacturaBusinessLogic fbl = new FacturaBusinessLogic(jniName, validaContraSAT, u);
        fbl.setNotificaErroresSAT(notificaFacturasInvalidasSAT);
        fbl.setNotificaErroresEFA(notificaFacturasEFA);
        fbl.setPermiteVersionAnterior(permiteFacturasVersionAnterior);
        fbl.insertaOficioCtoFederalizado(c, u, rutaArchivo, noOficio, montoOficio);
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
        /*
		 * VGC20160802 Se agrega cambio para validar mediante el WS del SAT que
		 * el CFDI exista y este vigente.
		 */
        ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic(jniName);
        validaContraSAT = "S".equalsIgnoreCase(cabl.getSystemSetting("ACTIVA_VALIDACION_SAT"));
        notificaFacturasEFA = "S".equalsIgnoreCase(cabl.getSystemSetting("NOTIFICA_ERROR_VALIDACION_EFA"));
        notificaFacturasInvalidasSAT = "S".equalsIgnoreCase(cabl.getSystemSetting("NOTIFICA_ERROR_VALIDACION_SAT"));
        permiteFacturasVersionAnterior = "S".equalsIgnoreCase(StringUtils.trimToEmpty(cabl.getSystemSetting("PERMITE_VERSION_MENOR")));
    }
}
