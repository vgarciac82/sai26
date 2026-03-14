package com.syc.contable.servlet;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.fileupload2.core.FileItem;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.syc.contable.anteproyecto.ComparaSaiSicopBusinessLogic;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@WebServlet(name = "CargaExcelComparaSaiSicopServlet", urlPatterns = { "/gstnmngr/ComparaSaiSicop/CargaArchivo", "/gstnmngr/ComparaSaiSicop/Consulta" })
public class CargaExcelComparaSaiSicopServlet extends HttpServlet implements GestionInterface {

    private static final Logger log = LoggerFactory.getLogger(CargaExcelComparaSaiSicopServlet.class);

    private static final long serialVersionUID = 1L;

    /**
     * Directorio temporal donde se almacenara el archivo de carga.
     */
    private static String TEMP_DIR = "";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ComparaSaiSicopBusinessLogic cssBL = new ComparaSaiSicopBusinessLogic(GestionInterface.ATT_CONEXION);
        String mensajeRetorno = "";
        // ExportaExcel
        String accion = req.getRequestURI().indexOf("CargaArchivo") > 0 ? "CARGA" : "CONSULTA";
        String[] lmomentos = req.getParameterValues("mPresupuestales");
        HttpSession session = req.getSession(false);
        if (session == null) {
            log.warn("Peticion sin sesion o sesion invalida");
            resp.sendRedirect("../index.jsp");
            return;
        }
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null) {
            log.warn("Peticion sin usuario o sesion invalida");
            resp.sendRedirect("../index.jsp");
            return;
        }
        if ("CARGA".equals(accion)) {
            List<?> fileItems = null;
            Iterator<?> iter = null;
            InputStream archivoCargaIS = null;
            DataInputStream archivoCargaStream = null;
            String nombreDestino = "";
            try {
                fileItems = Util.parseRequest(req, CargaExcelComparaSaiSicopServlet.TEMP_DIR, -1);
                iter = fileItems.iterator();
                String nombreArchivo = "";
                while (iter.hasNext()) {
                    FileItem item = (FileItem) iter.next();
                    if (item.isFormField())
                        continue;
                    archivoCargaIS = item.getInputStream();
                    archivoCargaStream = new DataInputStream(item.getInputStream());
                    nombreArchivo = item.getName();
                    String extension = Util.getFileExtencion(nombreArchivo);
                    if (!"csv".equalsIgnoreCase(extension))
                        throw new Exception("No se puede procesar archivos [" + extension + "] Corrija e intente de nuevo");
                    nombreDestino = CargaExcelComparaSaiSicopServlet.TEMP_DIR + "CARGA_PROYECTO_" + System.currentTimeMillis() + "." + extension;
                    log.info("Object: {}", "Copiando archivo :" + nombreArchivo);
                    Util.copiaArchivo(archivoCargaStream, nombreDestino);
                    item.delete();
                    log.debug("Object: {}", "Procesando archivo:" + nombreArchivo);
                    ComparaSaiSicopBusinessLogic cssbl = new ComparaSaiSicopBusinessLogic(u.getLogin());
                    cssbl.cargaExcelComparaSaiSicop(nombreDestino);
                    mensajeRetorno = "Archivo cargado exitosamente";
                    /*
					 * Solo se espera un archivo por carga, por lo que al leerlo
					 * no es necesario continuar con el ciclo.
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
            session.setAttribute("MENSAJE_CARGA", mensajeRetorno);
            resp.sendRedirect("../../admin/ComparaSaiSicop.jsp");
        } else if ("CONSULTA".equals(accion)) {
            String operacion = req.getParameter("accion");
            if ("GeneraReporte".equals(operacion)) {
                try {
                    List<List<String>> arrDiferencias = null;
                    String listadoMomentos = Util.join(lmomentos, ',');
                    arrDiferencias = cssBL.consultaReporte(listadoMomentos);
                    session.setAttribute("DIFERENCIAS", arrDiferencias);
                } catch (Exception exc) {
                    log.error(exc.getMessage(), exc);
                    mensajeRetorno = "No fue posible realizar la exportación" + exc;
                }
            } else if ("ExportaExcel".equals(operacion)) {
                try {
                    exportaReporteExcel(req, resp);
                    return;
                } catch (Exception exc) {
                    log.error(exc.getMessage(), exc);
                    mensajeRetorno = "No fue posible realizar la exportación" + exc;
                }
            }
            String strRedirect = "";
            try {
                strRedirect = "../../admin/ComparaSaiSicop.jsp?" + returnRequestChecks(req);
            } catch (Exception e) {
                e.printStackTrace();
            }
            log.debug("Object: {}", "Redirect: " + strRedirect);
            resp.sendRedirect(strRedirect);
            log.debug("SALE  DE LA CONSULTA");
        }
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

    public synchronized void exportaReporteExcel(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ComparaSaiSicopBusinessLogic cssBL = new ComparaSaiSicopBusinessLogic(GestionInterface.ATT_CONEXION);
        String[] lmomentos = request.getParameterValues("mPresupuestales");
        String listadoMomentos = Util.join(lmomentos, ',');
        String file_name = "Comparativo_sai_sicop" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        String cFileExcelPlantilla = getServletContext().getRealPath("Reportes" + File.separator + "plantilla_compara_sai_sicop.xls");
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(file_name);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        Sheet sheet = workbook.getSheetAt(0);
        sheet = cssBL.consultaReporteExcel(sheet, listadoMomentos);
        fsArchivo.close();
        File fsalida = new File(file_name);
        response.setContentType("application/vnd.ms-excel");
        response.addHeader("Content-Disposition", "inline; filename=\"" + file_name + "\"; ");
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos);
        /* Cierra Flujos */
        workbook.close();
        bos.flush();
        bos.close();
        fos.close();
        ServletOutputStream out = response.getOutputStream();
        ServletContext context = getServletConfig().getServletContext();
        String mimetype = context.getMimeType(file_name);
        Util.doDownload(out, file_name, file_name, mimetype);
        out.flush();
        out.close();
        if (!fsalida.delete()) {
            fsalida.deleteOnExit();
        }
    }

    private String returnRequestChecks(HttpServletRequest req) throws Exception {
        String reqString = "";
        String token = "";
        String[] val = req.getParameterValues("mPresupuestales");
        for (int j = 0; j < val.length; j++) {
            reqString += token + "mPresupuestales" + "=" + (val[j] == null ? "" : URLEncoder.encode(new String(val[j].getBytes("ISO-8859-1"), "UTF-8")));
            token = "&";
        }
        return reqString;
    }
}
