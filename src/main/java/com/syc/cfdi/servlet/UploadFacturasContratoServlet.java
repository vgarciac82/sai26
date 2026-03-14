package com.syc.cfdi.servlet;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
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
import org.apache.commons.fileupload2.core.FileItem;
import org.apache.commons.lang.StringUtils;
import com.jenkov.prizetags.tree.itf.ITree;
import com.syc.cfdi.ComponentesFactura;
import com.syc.cfdi.FacturaBusinessLogic;
import com.syc.cfdi.core.ExtraccionFacturas;
import com.syc.cfdi.utils.FacturaUtils;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import com.syc.obrapublica.EjercicioFiscalBusinessLogic;
import com.syc.obrapublica.ObraPublicaContractBusinessLogic;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "UploadFacturasContratoServlet", urlPatterns = { "/uploadFacturasContrato" })
public class UploadFacturasContratoServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = -795967476374791070L;

    private static String jniName = "jdbc/gestion";

    private static final Logger log = LoggerFactory.getLogger(UploadFacturasContratoServlet.class);

    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        String msgRetorno = "";
        Usuario u = null;
        Caso c = null;
        String tipoContrato = "";
        String folioSAI = "";
        String idContrato = "";
        String rfc = "";
        BigDecimal montoConIVA = null;
        BigDecimal montoIVA = null;
        int tipoFacturaGlobal = 1;
        String tipoFactura = "";
        boolean validaContraSAT = false;
        boolean notificaFacturasInvalidasSAT = false;
        boolean notificaFacturasEFA = false;
        boolean permiteFacturasVersionAnterior = false;
        if (session == null) {
            msgRetorno = "Su sesion a terminado. Por favor ingrese nuevamente al sistema.";
        } else {
            u = (Usuario) session.getAttribute(ATT_USER);
            // Se quita la toma del caso de la session por el motivo del uso de
            // varias pantallas del SAI.
            if (u == null) {
                msgRetorno = "Su sesion a terminado. Por favor ingrese nuevamente al sistema.";
            }
        }
        if ("".equals(msgRetorno)) {
            CasoBusinessLogic cbl = new CasoBusinessLogic(jniName);
            /*
			 * VGC20160802 Se agrega cambio para validar mediante el WS del SAT
			 * que el CFDI exista y este vigente.
			 */
            ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic(jniName);
            validaContraSAT = "S".equalsIgnoreCase(cabl.getSystemSetting("ACTIVA_VALIDACION_SAT"));
            notificaFacturasEFA = "S".equalsIgnoreCase(cabl.getSystemSetting("NOTIFICA_ERROR_VALIDACION_EFA"));
            notificaFacturasInvalidasSAT = "S".equalsIgnoreCase(cabl.getSystemSetting("NOTIFICA_ERROR_VALIDACION_SAT"));
            List<?> fileItems = null;
            Iterator<?> iter = null;
            DataInputStream archivoCargaStream = null;
            String nombreDestino = "";
            ConfiguraAplicativoBusinessLogic configApp = null;
            try {
                configApp = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
                boolean esSAIAlterno = "true".equalsIgnoreCase(configApp.getSystemSetting("SAI_AMBIENTAL")) || "true".equalsIgnoreCase(configApp.getSystemSetting("SAI_FONDEN"));
                EjercicioFiscalBusinessLogic adbl = new EjercicioFiscalBusinessLogic(jniName);
                int ejercicioFiscal = Integer.parseInt(adbl.getEjercicioFiscalActivo().getaEjercicioFiscal());
                fileItems = Util.parseRequest(req, TEMP_DIR, -1);
                iter = fileItems.iterator();
                String nombreArchivo = "";
                while (iter.hasNext()) {
                    FileItem item = (FileItem) iter.next();
                    if (item.isFormField()) {
                        if ("TipoContrato".equals(item.getFieldName()))
                            tipoContrato = item.getString();
                        else if ("cIdRFC".equals(item.getFieldName()))
                            rfc = StringUtils.trimToEmpty(item.getString("UTF-8"));
                        else if ("IDContrato".equals(item.getFieldName()))
                            idContrato = item.getString();
                        else if ("montoConIVA".equals(item.getFieldName()) && null != item.getString() && !"null".equalsIgnoreCase(item.getString()))
                            montoConIVA = new BigDecimal(item.getString());
                        else if ("montoIVA".equals(item.getFieldName()) && null != item.getString() && !"null".equalsIgnoreCase(item.getString()))
                            montoIVA = new BigDecimal(item.getString());
                        else if ("folioSAI".equals(item.getFieldName()))
                            folioSAI = item.getString();
                        else if ("tipoFacturaGlobal".equals(item.getFieldName()) && null != item.getString() && !"null".equalsIgnoreCase(item.getString()))
                            tipoFacturaGlobal = Integer.parseInt(item.getString());
                        else if ("seleccionTipo".equals(item.getFieldName()))
                            tipoFactura = item.getString();
                        item.delete();
                        continue;
                    }
                    archivoCargaStream = new DataInputStream(item.getInputStream());
                    nombreArchivo = item.getName();
                    String extension = Util.getFileExtencion(nombreArchivo);
                    if (!"zip".equalsIgnoreCase(extension))
                        throw new Exception("No se puede procesar archivos [" + extension + "] Corrija e intente de nuevo");
                    if (StringUtils.isEmpty(folioSAI))
                        throw new Exception("El folio del contrato es un dato requerido.");
                    log.info("Object: {}", "Copiando archivo :" + nombreArchivo);
                    nombreDestino = FacturaUtils.generaNombreZip(TEMP_DIR, extension);
                    Util.copiaArchivo(archivoCargaStream, nombreDestino);
                    item.delete();
                }
                boolean esNotaCredito = "CREDITO".equalsIgnoreCase(tipoFactura);
                if (c == null && !StringUtils.isEmpty(folioSAI)) {
                    c = cbl.getCaso(folioSAI);
                } else {
                    if (esSAIAlterno && "OB".equalsIgnoreCase(tipoContrato)) {
                        ObraPublicaContractBusinessLogic opcbl = new ObraPublicaContractBusinessLogic(jniName);
                        c = opcbl.cargaCasoContrato(idContrato);
                    }
                }
                if (c == null)
                    throw new Exception("No se encontro caso con el folio: " + folioSAI);
                if (c.getIdGabinete() == -1) {
                    c.getCasoDato("FOLIO").setValor(c.getFolio());
                    c.getCasoDato("FECHA_DOCUMENTO").setValor(Util.getToday());
                    c.getCasoDato("EJERCICIO_FISCAL").setValor(String.valueOf(ejercicioFiscal));
                    c.getCasoDato("OPERADOR").setValor(c.getCasoOperacion(0).getResponsable());
                    c.setIdGabinete(cbl.creaExpediente(u.getLogin(), c));
                }
                /*
				 * VGC20160802 Se agrega cambio para validar mediante el WS del
				 * SAT que el CFDI exista y este vigente.
				 */
                FacturaBusinessLogic fbl = new FacturaBusinessLogic(jniName, validaContraSAT, u);
                fbl.setNotificaErroresSAT(notificaFacturasInvalidasSAT);
                fbl.setNotificaErroresEFA(notificaFacturasEFA);
                fbl.setPermiteVersionAnterior(permiteFacturasVersionAnterior);
                /*
				 * VGC20160802 Se agrega cambio para permitir o rechazar
				 * facturas con version anterior a 3.3
				 */
                fbl.setPermiteVersionAnterior(permiteFacturasVersionAnterior);
                /*
				 * VGC20160802 Se agrega cambio para validar mediante el WS del
				 * SAT que el CFDI exista y este vigente.
				 */
                ExtraccionFacturas ef = null;
                if ("OB".equalsIgnoreCase(tipoContrato)) {
                    ef = fbl.extraeFacturasContrato(nombreDestino, tipoContrato, rfc, idContrato, montoConIVA, montoIVA, tipoFacturaGlobal, esNotaCredito);
                } else {
                    ef = fbl.extraeFacturasContrato(nombreDestino, tipoContrato, rfc, idContrato, montoConIVA, montoIVA, esNotaCredito);
                }
                if (ef.getErrores().size() == 0) {
                    Map<String, ComponentesFactura> facturas = ef.getFacturas();
                    fbl.insertaFacturaContrato(c, u, tipoContrato, idContrato, ejercicioFiscal, facturas, esNotaCredito);
                } else {
                    String token = "";
                    for (int i = 0; i < ef.getErrores().size(); i++) {
                        msgRetorno += token + ef.getErrores().get(i);
                        token = "<br>";
                    }
                    throw new Exception(msgRetorno);
                }
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
                        log.error("Error occurred", "Error cerrando flujo DataInputStream" + e);
                    }
                archivoCargaStream = null;
                configApp = null;
                if (!"".equals(nombreDestino)) {
                    File toDelete = new File(nombreDestino);
                    if (!toDelete.delete())
                        toDelete.deleteOnExit();
                }
            }
        } else {
            session.setAttribute("RESULT", msgRetorno);
            resp.sendRedirect("Generador/UploadCFDIContrato.jsp?TipoContrato=" + tipoContrato + "&RFC=" + rfc + "&IDContrato" + idContrato);
            return;
        }
        session.setAttribute("RESULT", msgRetorno);
        resp.sendRedirect("Generador/UploadCFDIContrato.jsp?TipoContrato=" + tipoContrato + "&RFC=" + rfc + "&IDContrato" + idContrato);
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
        try {
            File f = new File(TEMP_DIR);
            if (!f.exists())
                if (!f.mkdirs())
                    throw new Exception("No se puede crear el directorio temporal " + TEMP_DIR);
        } catch (Exception e) {
            log.error("Object: {}", "No fue posible crear automaticamente el directorio temporal: " + TEMP_DIR + " Solicite su creacion manual");
        }
    }
}
