package com.syc.adquisiciones.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
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
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.syc.adquisiciones.businessLogic.LeeArchivosBusinessLogic;
import com.syc.adquisiciones.core.DatosArchivo;
import com.syc.adquisiciones.core.Respuesta;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import java.net.URLDecoder;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "LeeArchivos", urlPatterns = { "/servlet/LeeArchivos" })
public class LeeArchivos extends HttpServlet {

    /**
     */
    private static final long serialVersionUID = 6628479998183903209L;

    private static String tempDir = "";

    private static String jniName = null;

    private JSONArray arrayObj = null;

    private JSONObject jsonObj = null;

    private PrintWriter out = null;

    private static String prefixPath = null;

    private static Logger log = LoggerFactory.getLogger(LeeArchivos.class);

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
        out.println("<HTML>");
        out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
        out.println("  <BODY>");
        out.print("    This is ");
        out.print(this.getClass());
        out.println(", using the GET method");
        out.println("  </BODY>");
        out.println("</HTML>");
        out.flush();
        out.close();
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        Caso c = null;
        String cCentroContable = "";
        if (session == null) {
            log.warn("No hay sesion");
            response.sendRedirect("../index.jsp");
            return;
        }
        Usuario u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        if (u == null)
            throw new ServletException("Su session a caducado");
        if (u.getPropiedades() != null && u.getPropiedades().containsKey("CCENTROCONTABLE")) {
            cCentroContable = u.getPropiedad("CCENTROCONTABLE").getValor();
        }
        if (cCentroContable.isEmpty() || cCentroContable.equals("")) {
            throw new ServletException("Error: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion presupuestal, Consulte a su administrador.");
        }
        List<?> fileItems = null;
        Iterator<?> iter = null;
        String msg = "Todo bien";
        DatosArchivo datosArchivo = null;
        Respuesta resp = null;
        boolean isCorrect = true;
        LeeArchivosBusinessLogic leeArchivobusinessLogic = null;
        try {
            fileItems = Util.parseRequest(request, LeeArchivos.tempDir, -1);
            iter = fileItems.iterator();
            arrayObj = new JSONArray();
            jsonObj = new JSONObject();
            out = response.getWriter();
            leeArchivobusinessLogic = new LeeArchivosBusinessLogic();
            datosArchivo = fillObject(iter);
            prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
            datosArchivo.setcPrefixPath(prefixPath);
            switch(datosArchivo.getnOperacion()) {
                case //Cargar de EFOS
                1:
                    log.info("Inicia la carga de EFOS.");
                    if (!"xls".equalsIgnoreCase(datosArchivo.getcExtencion()) && !"xlsx".equalsIgnoreCase(datosArchivo.getcExtencion())) {
                        throw new Exception("No se puede procesar archivos [" + datosArchivo.getcExtencion() + "] Corrija e intente de nuevo.");
                    }
                    log.info("Inicia el proceso de carga de layout EFOS.");
                    msg = "Carga de layout EFOS";
                    //leeArchivobusinessLogic.cargaArchivoEFOS(datosArchivo, u);
                    resp = leeArchivobusinessLogic.readFileEXCEL(datosArchivo, u);
                    msg = resp.getMsg();
                    isCorrect = resp.isResp();
                    break;
                case //Carga contrato fisico
                2:
                    log.info("Inicia la carga de Contrato Físico.");
                    if (!"zip".equalsIgnoreCase(datosArchivo.getcExtencion())) {
                        throw new Exception("No se puede procesar archivos [" + datosArchivo.getcExtencion() + "] Corrija e intente de nuevo");
                    }
                    datosArchivo.setcNombreArchivoDestino(LeeArchivos.tempDir + "CARGA_CONTRATO_FISICO_" + System.currentTimeMillis() + "." + datosArchivo.getcExtencion());
                    c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
                    if (c == null || u == null) {
                        throw new Exception("Su sesion a terminado. Por favor ingrese nuevamente al sistema.");
                    }
                    resp = leeArchivobusinessLogic.uploadContratoFisico(session, datosArchivo, u, c, jniName);
                    msg = resp.getMsg();
                    isCorrect = resp.isResp();
                    break;
                case //carga de archivo para los contratos de terminación anticipada.
                3:
                    log.info("Inicia la carga de archivo de terminación anticipada de contrato.");
                    if (!"zip".equalsIgnoreCase(datosArchivo.getcExtencion())) {
                        throw new Exception("No se puede procesar archivos [" + datosArchivo.getcExtencion() + "] Corrija e intente de nuevo");
                    }
                    datosArchivo.setcNombreArchivoDestino(LeeArchivos.tempDir + "terminacionAnticipada" + System.currentTimeMillis() + "." + datosArchivo.getcExtencion());
                    resp = leeArchivobusinessLogic.terminacionAnticipadaContrato(datosArchivo, u, jniName);
                    msg = resp.getMsg();
                    isCorrect = resp.isResp();
                    break;
                case //Lee retorno de apartado
                4:
                    log.info("Inicia la carga de layout apartado de retorno.");
                    if (!"csv".equalsIgnoreCase(datosArchivo.getcExtencion())) {
                        throw new Exception("No se puede procesar archivos [" + datosArchivo.getcExtencion() + "] Corrija e intente de nuevo.");
                    }
                    datosArchivo.setcPrefixPath(getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator);
                    datosArchivo.setJniName(jniName);
                    log.info("Inicia la lectura de layout retorno de apartado.");
                    resp = leeArchivobusinessLogic.readLayoutApartado(request, datosArchivo, u);
                    msg = resp.getMsg();
                    isCorrect = resp.isResp();
                    break;
                case //Recisión de contratos
                5:
                    log.info("Inicia la carga de archivo recisión de contrato.");
                    if (!"zip".equalsIgnoreCase(datosArchivo.getcExtencion())) {
                        throw new Exception("No se puede procesar archivos [" + datosArchivo.getcExtencion() + "] Corrija e intente de nuevo");
                    }
                    datosArchivo.setcNombreArchivoDestino(LeeArchivos.tempDir + "recisionDeContrato" + System.currentTimeMillis() + "." + datosArchivo.getcExtencion());
                    resp = leeArchivobusinessLogic.recisionDeContrato(datosArchivo, u);
                    msg = resp.getMsg();
                    isCorrect = resp.isResp();
                    break;
                case //Carga de contratos del CAAS
                6:
                    log.info("Inicia la carga de contrataciones del CAAS por layout.");
                    if (!"xls".equalsIgnoreCase(datosArchivo.getcExtencion()) && !"xlsx".equalsIgnoreCase(datosArchivo.getcExtencion())) {
                        throw new Exception("No se puede procesar archivos [" + datosArchivo.getcExtencion() + "] Corrija e intente de nuevo.");
                    }
                    datosArchivo.setJniName(jniName);
                    datosArchivo.setcNombreArchivoDestino(LeeArchivos.tempDir + "contratosCargados" + System.currentTimeMillis() + "." + datosArchivo.getcExtencion());
                    msg = leeArchivobusinessLogic.cargaContratosCAAS(request, datosArchivo, u);
                    isCorrect = true;
                    msg = "Revisa el archivo de Excel en la columna de observaciones, el archivo te llegará por correo.";
                    break;
                case //Carga layout de presupuesto para requisiciones
                7:
                    log.info("Inicia la carga de layout de presupuesto para requisiciones.");
                    if (!"xls".equalsIgnoreCase(datosArchivo.getcExtencion()) && !"xlsx".equalsIgnoreCase(datosArchivo.getcExtencion())) {
                        throw new Exception("No se puede procesar archivos [" + datosArchivo.getcExtencion() + "] Corrija e intente de nuevo.");
                    }
                    msg = "Carga de layout presupuesto para requisiciones";
                    datosArchivo.setcNombreArchivoDestino(LeeArchivos.tempDir + "layoutPresupuestoRequi" + System.currentTimeMillis() + "." + datosArchivo.getcExtencion());
                    resp = leeArchivobusinessLogic.procesaLayoutPresupuestoRequi(datosArchivo, u, jniName);
                    msg = resp.getMsg();
                    isCorrect = resp.isResp();
                    log.info("Finaliza la carga de layout de presupuesto para requisiciones.");
                    break;
                default:
                    msg = "Operación desconocida";
                    isCorrect = false;
                    log.warn("Operación desconocida en la carga de archivos.");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            msg = (e.getMessage() == null || e.getMessage().length() <= 1 ? "Error de carga" : e.getMessage());
            isCorrect = false;
            e.printStackTrace();
        } finally {
            leeArchivobusinessLogic = null;
            u = null;
            try {
                jsonObj.put("MSG", msg);
                jsonObj.put("ISCORRECT", isCorrect);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String destino = new String(arrayObj.put(jsonObj).toString().getBytes("UTF-8"), "ISO-8859-1");
            out.println(destino);
            out.flush();
            if (out != null) {
                out.close();
            }
            out = null;
        }
    }

    public DatosArchivo fillObject(Iterator<?> iter) throws Exception {
        DatosArchivo datosArchivo = new DatosArchivo();
        datosArchivo.setJniName(jniName);
        while (iter.hasNext()) {
            FileItem item = (FileItem) iter.next();
            if (item.isFormField()) {
                if ("operacion".equals(item.getFieldName())) {
                    datosArchivo.setnOperacion((null == item.getString() || "".equals(item.getString())) ? 0 : Integer.parseInt(item.getString()));
                }
                if ("namePlantilla".equals(item.getFieldName())) {
                    datosArchivo.setNamePlantilla(((null == item.getString() || "".equals(item.getString())) ? "" : getServletContext().getRealPath("Reportes" + File.separator + item.getString())));
                }
                if ("cContratoDefinitivo".equals(item.getFieldName())) {
                    datosArchivo.setcIdContratoDefinitivo(((null == item.getString() || "".equals(item.getString())) ? "" : item.getString()));
                }
                if ("nIdCaso".equals(item.getFieldName())) {
                    datosArchivo.setnIdCaso((null == item.getString() || "".equals(item.getString())) ? 0 : Integer.parseInt(item.getString()));
                }
                if ("cContratoCNET".equals(item.getFieldName())) {
                    datosArchivo.setcContratoCNET(StringUtils.isBlank(item.getString()) ? "" : item.getString());
                }
                if ("nTipoTerminacionCont".equals(item.getFieldName())) {
                    datosArchivo.setnTipoTerminacionCont((null == item.getString() || "".equals(item.getString())) ? 0 : Integer.parseInt(item.getString()));
                }
                if ("descripcionCausa".equals(item.getFieldName())) {
                    datosArchivo.setcCausa(((null == item.getString() || "".equals(item.getString())) ? "" : URLDecoder.decode(item.getString(), "UTF-8")));
                }
                if ("fechaTermino".equals(item.getFieldName())) {
                    datosArchivo.setfFechaTermino(((null == item.getString() || "".equals(item.getString())) ? "" : item.getString()));
                }
                if ("fechaLimitePagoPendiente".equals(item.getFieldName())) {
                    datosArchivo.setfFechaLimitePagoPendiente(((null == item.getString() || "".equals(item.getString())) ? "" : item.getString()));
                }
                if ("cFolio".equals(item.getFieldName())) {
                    datosArchivo.setcFolio(((null == item.getString() || "".equals(item.getString())) ? "" : item.getString()));
                }
                if ("cIdSolicitud".equals(item.getFieldName())) {
                    datosArchivo.setcIdsolicitud((null == item.getString() || "".equals(item.getString())) ? "" : item.getString());
                }
                if ("fechaNotificacionUAF".equals(item.getFieldName())) {
                    datosArchivo.setFechaNotificacionUAF(StringUtils.isBlank(item.getString()) ? "" : item.getString());
                }
                if ("ejercicioContrato".equals(item.getFieldName())) {
                    datosArchivo.setcEjercicioContrato(StringUtils.isBlank(item.getString()) ? "" : item.getString());
                }
                if ("procedimientoCNET".equals(item.getFieldName())) {
                    datosArchivo.setcNumProcedimientoCNET(StringUtils.isBlank(item.getString()) ? "" : item.getString());
                }
                if ("expedienteCNET".equals(item.getFieldName())) {
                    datosArchivo.setcNumExpedienteCNET(StringUtils.isBlank(item.getString()) ? "" : item.getString());
                }
                if ("codigoContratoCNET".equals(item.getFieldName())) {
                    datosArchivo.setcCodigoContratoCNET(StringUtils.isBlank(item.getString()) ? "" : item.getString());
                }
                if ("nameDB".equals(item.getFieldName())) {
                    datosArchivo.setcNameDB(StringUtils.isBlank(item.getString()) ? "" : item.getString());
                }
                if ("cRFC".equals(item.getFieldName())) {
                    datosArchivo.setcIdRFC(StringUtils.isBlank(item.getString()) ? "" : item.getString());
                }
                if ("tienePagoPendiente".equals(item.getFieldName())) {
                    datosArchivo.setnTienePagoPendiente((null == item.getString() || "".equals(item.getString())) ? 0 : Integer.parseInt(item.getString()));
                }
                item.delete();
                continue;
            } else {
                datosArchivo.setArchivoStream(item.getInputStream());
                datosArchivo.setcNombreArchivo(item.getName());
                datosArchivo.setcExtencion(Util.getFileExtencion(item.getName()));
            }
        }
        return datosArchivo;
    }

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        tempDir = config.getInitParameter("tempDir");
        if (tempDir == null) {
            //tempDir = config.getServletContext().getRealPath("/") +  "upload" + File.separator;
            tempDir = config.getServletContext().getRealPath("/") + ".." + File.separator + "upload" + File.separator;
            File fDir = new File(tempDir);
            if (!fDir.exists())
                if (!fDir.mkdirs())
                    throw new ServletException("No se pudo crear el directorio " + tempDir);
        }
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
