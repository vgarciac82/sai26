package com.syc.reportes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.Map;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.util.Util;
import com.syc.reportes.core.ReporteNotasManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class ReporteNotasBusinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(ReporteNotasBusinessLogic.class);

    public ReporteNotasBusinessLogic(String jniName) {
        super.init(jniName);
    }

    /**
     * Genera el reporte de notas a los estados financieros.
     *
     * @param req
     *            Request
     * @param resp
     *            Response
     * @param plantilla
     *            Las plantillas que pueden usarse en excel o word
     * @throws Exception
     *             si ocurre algun error
     */
    public void generaReporteResumen(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantilla) throws Exception {
        Connection conn = null;
        String file = null;
        try {
            log.debug("Iniciando notas");
            conn = getConnection();
            String tipo = req.getParameter("Moneda");
            String fechaFin = req.getParameter("Fecha");
            int miles = Integer.parseInt(tipo);
            log.debug("Object: {}", "Parametros para las notas: " + tipo + ", " + fechaFin + ", " + miles);
            // int mesFin = Integer.parseInt(fechaFin.substring(5,7));
            // int anio = Integer.parseInt(fechaFin.substring(0,4));
            log.debug("Llamando el reporte");
            file = ReporteNotasManager.generaReporteResumenManager(conn, fechaFin, miles, plantilla);
            File f = new File(file);
            resp.setContentType("application/vnd.ms-excel");
            resp.addHeader("Content-Disposition", "inline; filename=\"" + f.getName() + "\"; ");
            ServletOutputStream out = resp.getOutputStream();
            Util.doDownload(out, file, file, "");
            out.flush();
            out.close();
        } finally {
            CloseObject.closeObject(conn, false);
            if (file != null) {
                File f = new File(file);
                if (!f.delete())
                    f.deleteOnExit();
            }
        }
    }

    public void generaReporteDetalle(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantilla) throws Exception {
        Connection conn = null;
        String file = null;
        try {
            conn = getConnection();
            String tipo = req.getParameter("Moneda");
            String fechaFin = req.getParameter("Fecha");
            int miles = Integer.parseInt(tipo);
            // int mesFin = Integer.parseInt(fechaFin.substring(5,7));
            // int anio = Integer.parseInt(fechaFin.substring(0,4));
            file = ReporteNotasManager.generaReporteNotasDetManager(conn, fechaFin, miles, plantilla);
            File f = new File(file);
            resp.setContentType("application/vnd.ms-excel");
            resp.addHeader("Content-Disposition", "inline; filename=\"" + f.getName() + "\"; ");
            ServletOutputStream out = resp.getOutputStream();
            Util.doDownload(out, file, file, "");
            out.flush();
            out.close();
        } finally {
            CloseObject.closeObject(conn, false);
            if (file != null) {
                File f = new File(file);
                if (!f.delete())
                    f.deleteOnExit();
            }
        }
    }

    public void generaReporteResumenWord(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantilla) throws Exception {
        Connection conn = null;
        String path = plantilla.get("REPNOTASWORD");
        XWPFDocument document = new XWPFDocument(new FileInputStream(new File(path)));
        File fGenerado = null;
        try {
            conn = getConnection();
            String tipo = req.getParameter("Moneda");
            String fechaFin = req.getParameter("Fecha");
            int miles = Integer.parseInt(tipo);
            String efectivo = new String(req.getParameter("efectivo").getBytes("ISO-8859-1"), "UTF-8");
            String derechos = new String(req.getParameter("derechos").getBytes("ISO-8859-1"), "UTF-8");
            String derAnt = new String(req.getParameter("derechos_antig").getBytes("ISO-8859-1"), "UTF-8");
            String almacen = new String(req.getParameter("almacenes").getBytes("ISO-8859-1"), "UTF-8");
            String cxp = new String(req.getParameter("cxp").getBytes("ISO-8859-1"), "UTF-8");
            String otrascxp = new String(req.getParameter("otrascxp").getBytes("ISO-8859-1"), "UTF-8");
            String pasivo = new String(req.getParameter("pasivo").getBytes("ISO-8859-1"), "UTF-8");
            String pasivo2 = new String(req.getParameter("pasivo2").getBytes("ISO-8859-1"), "UTF-8");
            String eventos = new String(req.getParameter("eventos").getBytes("ISO-8859-1"), "UTF-8");
            String eventos2 = new String(req.getParameter("eventos2").getBytes("ISO-8859-1"), "UTF-8");
            String eventos3 = new String(req.getParameter("eventos3").getBytes("ISO-8859-1"), "UTF-8");
            String fechaAut = new String(req.getParameter("fechaAut").getBytes("ISO-8859-1"), "UTF-8");
            ReporteNotasManager.generaReporteNotasWord(conn, fechaFin, miles, document, efectivo, derechos, derAnt, almacen, cxp, otrascxp, pasivo, pasivo2, eventos, eventos2, eventos3, fechaAut);
            fGenerado = File.createTempFile("Notas_Estados_Financieros", ".docx", new File(System.getProperty("java.io.tmpdir")));
            FileOutputStream word = new FileOutputStream(fGenerado);
            document.write(word);
            word.close();
            Util.doDownload(resp, fGenerado.getAbsolutePath(), "Notas_Estados_Financieros.docx", "application/vnd.openxmlformats-");
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            throw ex;
        } finally {
            CloseObject.closeObject(conn, false);
            if (fGenerado != null)
                if (!fGenerado.delete())
                    fGenerado.deleteOnExit();
        }
    }
}
