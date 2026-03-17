package com.syc.contable.servlet;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.fileupload2.core.FileItem;
import org.json.JSONObject;
import com.syc.contable.ContratoPlurianualBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

/**
 * Servlet que recibe, valida y guarda el archivo excel d
 *
 * @author Vicente Garcia Carrillo
 */
@WebServlet(name = "ContratoPlurianualServlet", urlPatterns = { "/servlet/ContratoPlurianualServlet" })
public class ContratoPlurianualServlet extends HttpServlet implements GestionInterface {

    private static final Logger log = LoggerFactory.getLogger(ContratoPlurianualServlet.class);

    private static final long serialVersionUID = 1L;

    private JSONObject jsonObj;

    /**
     * Directorio temporal donde se almacenara el archivo de carga.
     */
    private static String TEMP_DIR = "";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String mensajeRetorno = "";
        String tipoPlurianual = "";
        String tipoSolicitud = "";
        String valContra = "";
        //String Folio ="";
        String sOperacion = req.getParameter("operacion");
        jsonObj = new JSONObject();
        PrintWriter out = resp.getWriter();
        HttpSession session = req.getSession(false);
        if (session == null) {
            log.warn("Peticion sin sesion o sesion invalida");
            resp.sendRedirect("../index.jsp");
        }
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null) {
            log.warn("Peticion sin usuario o sesion invalida");
            resp.sendRedirect("../index.jsp");
        }
        if (sOperacion != "" && sOperacion != null) {
            ContratoPlurianualBusinessLogic CP = new ContratoPlurianualBusinessLogic(null);
            String mensaje = "";
            String pluriNormal = req.getParameter("pluriNormal");
            String pluriEspecial = req.getParameter("pluriEspecial");
            try {
                if (sOperacion.equals("AbrirCerrar")) {
                    CP.AbrirCerrar(pluriNormal, pluriEspecial, u.getLogin());
                }
                if (sOperacion.equals("CancelacionMasiva")) {
                    mensaje = CP.CancelacionMasiva(u.getLogin(), req);
                }
                if (mensaje != "") {
                    try {
                        jsonObj.put("Error", "Error al realizar la cancelación Masiva! \n " + mensaje);
                    } catch (Exception i) {
                        out.println("Error del error");
                    }
                }
            } catch (Exception e) {
                try {
                    jsonObj.put("Error", "Error al realizar la operacion!");
                } catch (Exception i) {
                    out.println("Error del error");
                }
            }
            return;
        }
        List<?> fileItems = null;
        Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
        Iterator<?> iter = null;
        InputStream archivoCargaIS = null;
        DataInputStream archivoCargaStream = null;
        String nombreDestino = "";
        String sFolio = "";
        sFolio = (c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).trim();
        try {
            fileItems = Util.parseRequest(req, ContratoPlurianualServlet.TEMP_DIR, -1);
            // final int folio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
            iter = fileItems.iterator();
            String nombreArchivo = "";
            int tamanio = 0;
            while (iter.hasNext()) {
                FileItem item = (FileItem) iter.next();
                if (item.isFormField()) {
                    continue;
                }
                archivoCargaIS = item.getInputStream();
                archivoCargaStream = new DataInputStream(item.getInputStream());
                nombreArchivo = item.getName();
                String extension = Util.getFileExtencion(nombreArchivo);
                if (!"xls".equalsIgnoreCase(extension))
                    mensajeRetorno = "No se puede procesar archivos [" + extension + "] solo  [xls] Corrija y vuelva a Cargar el Excel ";
                nombreDestino = ContratoPlurianualServlet.TEMP_DIR + "CARGA_PROYECTO_" + System.currentTimeMillis() + "." + extension;
                log.info("Object: {}", "Copiando archivo :" + nombreArchivo);
                Util.copiaArchivo(archivoCargaStream, nombreDestino);
                item.delete();
                log.debug("Object: " + String.valueOf("Procesando archivo:" + nombreArchivo));
                ContratoPlurianualBusinessLogic cpbl = new ContratoPlurianualBusinessLogic(u.getLogin());
                String nModificacion = req.getParameter("nmod");
                if ("".equals(nModificacion) || nModificacion == null) {
                    nModificacion = "0";
                }
                if (mensajeRetorno.length() == 0) {
                    ArrayList<String> tablaArreglo = new ArrayList<String>();
                    tablaArreglo = cpbl.cargaExcelPlurianual_EP(archivoCargaIS, sFolio, Integer.parseInt(req.getParameter("h_bEspecial")), u.getU_UR(), Integer.parseInt(nModificacion));
                    tamanio = tablaArreglo.size();
                    if (tamanio > 0) {
                        if (tablaArreglo.get(0).equals("Abierto") || tablaArreglo.get(0).equals("Cerrado")) {
                            tipoPlurianual = tablaArreglo.get(0);
                            tipoSolicitud = tablaArreglo.get(1);
                            valContra = tablaArreglo.get(2);
                        } else {
                            mensajeRetorno = tablaArreglo.get(0);
                        }
                    } else {
                        // Proceso Exitoso
                        mensajeRetorno = "ok";
                    }
                }
                log.debug("Object: " + String.valueOf(mensajeRetorno));
                /*
				 * Solo se espera un archivo por carga, por lo que al leerlo no
				 * es necesario continuar con el ciclo.
				 */
                break;
            }
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            mensajeRetorno = "No se pudo procesar el excel debido al siguiente error:\\n" + exc;
        } finally {
            if (archivoCargaStream != null)
                try {
                    archivoCargaStream.close();
                } catch (Exception e) {
                    log.error("Error occurred", "Error cerrando flujo DataInputStream" + e);
                    mensajeRetorno = "Error " + e;
                }
            if (archivoCargaIS != null)
                try {
                    archivoCargaIS.close();
                } catch (Exception e) {
                    log.error("Error occurred", "Error cerrando flujo InputStream" + e);
                }
            archivoCargaIS = null;
            archivoCargaStream = null;
            if (!"".equals(nombreDestino)) {
                File toDelete = new File(nombreDestino);
                if (!toDelete.delete())
                    toDelete.deleteOnExit();
            }
        }
        //	session.setAttribute("MENSAJE_CARGA", mensajeRetorno);
        resp.sendRedirect("../Generador/ContratosPlurianuales.jsp?msg=" + mensajeRetorno + "&tipoPlurianual=" + tipoPlurianual + "&tipoSolicitud=" + tipoSolicitud + "&valContra=" + valContra);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            InitialContext ic = new InitialContext();
            TEMP_DIR = (String) ic.lookup("java:comp/env/TemporaryDirectory");
            if (TEMP_DIR == null) {
                TEMP_DIR = "../upload/PEF/";
                log.info("Object: {}", "Environment Entry \"TEMP_DIR\" nula usando default \"" + TEMP_DIR + "\"");
            } else
                log.info("Object: {}", "dataSourceRefName=" + TEMP_DIR);
        } catch (NamingException exc) {
            TEMP_DIR = "../upload/PEF/";
            log.info("Error occurred", "Ocurrio un error que evito que se cargara la entrada \"TEMP_DIR\"" + exc);
            log.info("Object: {}", "Environment Entry \"dataSourceRefName\" no definida usando default \"" + TEMP_DIR + "\"");
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
