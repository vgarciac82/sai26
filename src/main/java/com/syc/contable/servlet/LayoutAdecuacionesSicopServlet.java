package com.syc.contable.servlet;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletFileUpload;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import com.syc.contable.AdecuacionBusinessLogic;
import com.syc.contable.adecuaciones.Adecuacion;
import com.syc.contable.adecuaciones.AdecuacionDetalle;
import com.syc.contable.adecuaciones.core.Fap01;
import com.syc.contable.core.Saldo;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.reportes.reportesBussinesObject;
import com.syc.gestion.servlet.GestionFileReceiverServlet;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;
import org.apache.commons.fileupload2.core.DiskFileItemFactory;
import java.nio.file.Paths;

@WebServlet(name = "LayoutAdecuacionesSicopServlet", urlPatterns = { "/gstnmngr/AdecuacionesLayoutSicop" })
public class LayoutAdecuacionesSicopServlet extends HttpServlet {

    /**
     */
    private static final long serialVersionUID = -6154598978287350848L;

    /**
     * Constructor of the object.
     */
    public LayoutAdecuacionesSicopServlet() {
        super();
    }

    /**
     * Destruction of the servlet. <br>
     */
    public void destroy() {
        // Just puts "destroy" string in log
        super.destroy();
        // Put your code here
    }

    /**
     * The doGet method of the servlet. <br>
     *
     * This method is called when a form has its tag value method equals to get.
     *
     * @param request
     *            the request send by the client to the server
     * @param response
     *            the response send by the server to the client
     * @throws ServletException
     *             if an error occurred
     * @throws IOException
     *             if an error occurred
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /*
		 * response.setContentType("text/html"); PrintWriter out =
		 * response.getWriter(); out.println(
		 * "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		 * out.println("<HTML>");
		 * out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
		 * out.println("  <BODY>"); out.print("    This is ");
		 * out.print(this.getClass()); out.println(", using the GET method");
		 * out.println("  </BODY>"); out.println("</HTML>"); out.flush();
		 * out.close();
		 */
    }

    /**
     * The doPost method of the servlet. <br>
     *
     * This method is called when a form has its tag value method equals to
     * post.
     *
     * @param request
     *            the request send by the client to the server
     * @param response
     *            the response send by the server to the client
     * @throws ServletException
     *             if an error occurred
     * @throws IOException
     *             if an error occurred
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getParameter("exporta").equals("1")) {
            response.sendRedirect("../plantillasCasos/layOutAdecuacion.jsp");
            return;
        }
        if (request.getParameter("exporta").equals("2")) {
            creaSicop(request, response);
        }
        if (request.getParameter("exporta").equals("3")) {
            try {
                // creaFap(request, response);
                creaFapAdec(request, response);
            } catch (Exception e) {
                throw new ServletException(e);
            }
        }
        if (request.getParameter("exporta").equals("4")) {
            creaFap02(request, response);
        }
        if (request.getParameter("exporta").equals("5")) {
            try {
                imprimeAdecuacion(request, response);
            } catch (Exception e) {
                log.error("Error generando impresion de adecuacion: " + e, e);
                try {
                    printError(response, e);
                } catch (Exception writeExc) {
                    log.warn(writeExc.getMessage(), writeExc);
                    throw new ServletException(e);
                }
            }
        }
        if (request.getParameter("exporta").equals("6")) {
            // creaFap02IADE(request, response);
        }
    }

    /**
     * Initialization of the servlet. <br>
     *
     * @throws ServletException
     *             if an error occurs
     */
    private static Logger log = LoggerFactory.getLogger(GestionFileReceiverServlet.class);

    private String jniName = null;

    private String tempDir = null;

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
        tempDir = config.getInitParameter("tempDir");
        if (tempDir == null) {
            tempDir = config.getServletContext().getRealPath("/") + File.separator + "upload" + File.separator;
            File fDir = new File(tempDir);
            if (!fDir.exists())
                if (!fDir.mkdirs())
                    throw new ServletException("No se pudo crear el directorio " + tempDir);
        }
    }

    public void creaSicop(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AdecuacionBusinessLogic adecua = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
        HttpSession session = request.getSession(false);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(response.getOutputStream()));
        DecimalFormat formateadorDecimal = new DecimalFormat("###.##");
        DecimalFormat formateadorCero = new DecimalFormat("#");
        Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
        Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        if (usuario == null) {
            response.sendRedirect("../index.jsp");
            return;
        }
        int nFolioAdecuacion = Integer.parseInt((c.getFolio().substring(9)));
        Adecuacion adecuacion = null;
        try {
            adecuacion = adecua.cargaAdecuacionProyecto(nFolioAdecuacion);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServletException(e);
        }
        String variable = String.valueOf(adecuacion.getEncabezado().getnFolioAdecuacion());
        String file_name = c.getFolio();
        response.setContentType("application/vnd.ms-excel");
        response.addHeader("Content-Disposition", "inline; filename=\"LAYOUT-SICOP" + file_name + ".xls\";");
        // Para escribir en el archivo la fecha de tipo dd/mm/yyyy
        Date date = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fecha = sdf.format(date);
        WritableWorkbook writableWorkbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        // WritableWorkbook writableWorkbook = jxl.Workbook.createWorkbook(new
        // File("LAYOUT-SICOP" + file_name + ".csv"));
        WritableSheet writableSheet = writableWorkbook.createSheet("Afecta 3", 0);
        String archivo = "";
        try {
            archivo = "H";
            archivo += "," + fecha.trim();
            archivo += "," + fecha.trim();
            archivo += ",16";
            archivo += ",16";
            archivo += ",16";
            archivo += ",RHQ";
            archivo += ",RHQ";
            archivo += ",000";
            archivo += ",9";
            archivo += ",009";
            writableSheet.addCell(new jxl.write.Label(0, 0, "H"));
            writableSheet.addCell(new jxl.write.Label(1, 0, fecha));
            writableSheet.addCell(new jxl.write.Label(2, 0, fecha));
            writableSheet.addCell(new jxl.write.Label(3, 0, "16"));
            writableSheet.addCell(new jxl.write.Label(4, 0, "16"));
            writableSheet.addCell(new jxl.write.Label(5, 0, "16"));
            writableSheet.addCell(new jxl.write.Label(6, 0, "RHQ"));
            writableSheet.addCell(new jxl.write.Label(7, 0, "RHQ"));
            writableSheet.addCell(new jxl.write.Label(8, 0, "000"));
            writableSheet.addCell(new jxl.write.Label(9, 0, "9"));
            writableSheet.addCell(new jxl.write.Label(10, 0, "099"));
            String folioSicop = "1";
            boolean tieneSicop;
            try {
                tieneSicop = adecua.tieneSicop(Integer.parseInt(c.getFolio().split("-")[2]));
                // si no tiene sicop voy a aumentar, por eso niego tieneSicop
                folioSicop = Integer.toString(adecua.obtenFolioSicop(!tieneSicop));
                if (!tieneSicop) {
                    adecua.actualizaFolioSicopEncabezado(folioSicop, Integer.parseInt(c.getFolio().split("-")[2]));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            while (folioSicop.length() < 5) {
                folioSicop = "0" + folioSicop.trim();
            }
            archivo += ",";
            archivo += ",";
            archivo += "," + variable.trim() + "_0" + folioSicop.trim();
            archivo += "," + variable.trim() + "_0" + folioSicop.trim();
            archivo += "," + folioSicop.trim();
            for (int i = 17; i <= 44; i++) archivo += ",";
            writableSheet.addCell(new jxl.write.Label(13, 0, variable.trim() + "_0" + folioSicop.trim()));
            writableSheet.addCell(new jxl.write.Label(14, 0, variable.trim() + "_0" + folioSicop.trim()));
            writableSheet.addCell(new jxl.write.Label(15, 0, folioSicop.trim()));
        } catch (Exception jxlex) {
            jxlex.printStackTrace();
        }
        boolean resp = false;
        try {
            int i = 1;
            for (Iterator<AdecuacionDetalle> iterator = adecuacion.getDetalle().iterator(); iterator.hasNext(); ) {
                AdecuacionDetalle detalle = iterator.next();
                String ep = detalle.getEP();
                if (detalle.getTipo().equalsIgnoreCase("A")) {
                    String epCorta = ep.substring(0, 55);
                    resp = adecua.esModificado(epCorta);
                }
                String claveEp = adecua.obtenClaveSicop(resp, detalle.getTipo());
                String[] splitSiaff = ep.split("\\.");
                String[] splitClaveEp = claveEp.split("\\.");
                archivo += "\r\n";
                archivo += splitClaveEp[0].trim();
                archivo += "," + splitClaveEp[1].trim();
                archivo += "," + splitClaveEp[2].trim();
                archivo += "," + splitClaveEp[3].trim();
                archivo += "," + splitSiaff[1].trim();
                archivo += "," + splitSiaff[2].trim();
                archivo += "," + splitSiaff[0].trim();
                archivo += "," + splitSiaff[3].trim();
                archivo += "," + splitSiaff[4].trim();
                archivo += "," + splitSiaff[5].trim();
                archivo += "," + splitSiaff[6].trim();
                archivo += "," + splitSiaff[7].trim();
                archivo += "," + splitSiaff[8].trim();
                archivo += "," + splitSiaff[9].substring(0, 1).trim();
                archivo += "," + splitSiaff[9].substring(1, 2).trim();
                archivo += "," + splitSiaff[9].substring(2, 3).trim();
                archivo += "," + splitSiaff[9].substring(3, 5).trim();
                archivo += "," + splitSiaff[10].trim();
                archivo += "," + splitSiaff[11].trim();
                archivo += "," + splitSiaff[12].trim();
                archivo += "," + splitSiaff[13].trim();
                writableSheet.addCell(new jxl.write.Label(0, i, splitClaveEp[0].trim()));
                writableSheet.addCell(new jxl.write.Label(1, i, splitClaveEp[1].trim()));
                writableSheet.addCell(new jxl.write.Label(2, i, splitClaveEp[2].trim()));
                writableSheet.addCell(new jxl.write.Label(3, i, splitClaveEp[3].trim()));
                writableSheet.addCell(new jxl.write.Label(4, i, splitSiaff[1].trim()));
                writableSheet.addCell(new jxl.write.Label(5, i, splitSiaff[2].trim()));
                writableSheet.addCell(new jxl.write.Label(6, i, splitSiaff[0].trim()));
                writableSheet.addCell(new jxl.write.Label(7, i, splitSiaff[3].trim()));
                writableSheet.addCell(new jxl.write.Label(8, i, splitSiaff[4].trim()));
                writableSheet.addCell(new jxl.write.Label(9, i, splitSiaff[5].trim()));
                writableSheet.addCell(new jxl.write.Label(10, i, splitSiaff[6].trim()));
                writableSheet.addCell(new jxl.write.Label(11, i, splitSiaff[7].trim()));
                writableSheet.addCell(new jxl.write.Label(12, i, splitSiaff[8].trim()));
                writableSheet.addCell(new jxl.write.Label(13, i, splitSiaff[9].substring(0, 1).trim()));
                writableSheet.addCell(new jxl.write.Label(14, i, splitSiaff[9].substring(1, 2).trim()));
                writableSheet.addCell(new jxl.write.Label(15, i, splitSiaff[9].substring(2, 3).trim()));
                writableSheet.addCell(new jxl.write.Label(16, i, splitSiaff[9].substring(3, 5).trim()));
                writableSheet.addCell(new jxl.write.Label(17, i, splitSiaff[10].trim()));
                writableSheet.addCell(new jxl.write.Label(18, i, splitSiaff[11].trim()));
                writableSheet.addCell(new jxl.write.Label(19, i, splitSiaff[12].trim()));
                writableSheet.addCell(new jxl.write.Label(20, i, splitSiaff[13].trim()));
                // splitSiaff[14].trim();
                String clvInterna1 = "";
                while (clvInterna1.length() < 10) {
                    // clvInterna1.trim();
                    clvInterna1 = "0" + clvInterna1;
                }
                archivo += "," + clvInterna1.trim();
                // String clvInterna2 = splitSiaff[15].trim();
                // + clvInterna2.substring(1, 3).trim();
                archivo += ",00";
                archivo += ",000";
                archivo += ",000";
                archivo += "," + detalle.getProyecto();
                archivo += ",00000";
                archivo += ",0000000000";
                if (detalle.getMontos().get(0) > 0)
                    archivo += "," + formateadorDecimal.format(detalle.getMontos().get(0));
                else
                    archivo += "," + formateadorCero.format(detalle.getMontos().get(0));
                archivo += ",1";
                if (detalle.getMontos().get(1) > 0)
                    archivo += "," + formateadorDecimal.format(detalle.getMontos().get(1));
                else
                    archivo += "," + formateadorCero.format(detalle.getMontos().get(1));
                if (detalle.getMontos().get(2) > 0)
                    archivo += "," + formateadorDecimal.format(detalle.getMontos().get(2));
                else
                    archivo += "," + formateadorCero.format(detalle.getMontos().get(2));
                if (detalle.getMontos().get(3) > 0)
                    archivo += "," + formateadorDecimal.format(detalle.getMontos().get(3));
                else
                    archivo += "," + formateadorCero.format(detalle.getMontos().get(3));
                if (detalle.getMontos().get(4) > 0)
                    archivo += "," + formateadorDecimal.format(detalle.getMontos().get(4));
                else
                    archivo += "," + formateadorCero.format(detalle.getMontos().get(4));
                if (detalle.getMontos().get(5) > 0)
                    archivo += "," + formateadorDecimal.format(detalle.getMontos().get(5));
                else
                    archivo += "," + formateadorCero.format(detalle.getMontos().get(5));
                if (detalle.getMontos().get(6) > 0)
                    archivo += "," + formateadorDecimal.format(detalle.getMontos().get(6));
                else
                    archivo += "," + formateadorCero.format(detalle.getMontos().get(6));
                if (detalle.getMontos().get(7) > 0)
                    archivo += "," + formateadorDecimal.format(detalle.getMontos().get(7));
                else
                    archivo += "," + formateadorCero.format(detalle.getMontos().get(7));
                if (detalle.getMontos().get(8) > 0)
                    archivo += "," + formateadorDecimal.format(detalle.getMontos().get(8));
                else
                    archivo += "," + formateadorCero.format(detalle.getMontos().get(8));
                if (detalle.getMontos().get(9) > 0)
                    archivo += "," + formateadorDecimal.format(detalle.getMontos().get(9));
                else
                    archivo += "," + formateadorCero.format(detalle.getMontos().get(9));
                if (detalle.getMontos().get(10) > 0)
                    archivo += "," + formateadorDecimal.format(detalle.getMontos().get(10));
                else
                    archivo += "," + formateadorCero.format(detalle.getMontos().get(10));
                if (detalle.getMontos().get(11) > 0)
                    archivo += "," + formateadorDecimal.format(detalle.getMontos().get(11));
                else
                    archivo += "," + formateadorCero.format(detalle.getMontos().get(11));
                if (detalle.getMontos().get(12) > 0)
                    archivo += "," + formateadorDecimal.format(detalle.getMontos().get(12));
                else
                    archivo += "," + formateadorCero.format(detalle.getMontos().get(12));
                // clvInterna2.substring(1,
                writableSheet.addCell(new jxl.write.Label(22, i, "00"));
                // 3).trim()));
                writableSheet.addCell(new jxl.write.Label(21, i, clvInterna1.trim()));
                writableSheet.addCell(new jxl.write.Label(23, i, "000".trim()));
                writableSheet.addCell(new jxl.write.Label(24, i, "000".trim()));
                // writableSheet.addCell(new jxl.write.Label(25, i,
                // detalle.getProyecto() ) );
                writableSheet.addCell(new jxl.write.Label(25, i, "00000"));
                writableSheet.addCell(new jxl.write.Label(26, i, "00000".trim()));
                writableSheet.addCell(new jxl.write.Label(27, i, "0000000000".trim()));
                // writableSheet.addCell(new jxl.write.Number(28,i-6,new
                // Double(objSaldo.getMontoAnual().trim())));
                if (detalle.getMontos().get(0) > 0)
                    writableSheet.addCell(new jxl.write.Label(28, i, formateadorDecimal.format(detalle.getMontos().get(0))));
                else
                    writableSheet.addCell(new jxl.write.Label(28, i, formateadorCero.format(detalle.getMontos().get(0))));
                writableSheet.addCell(new jxl.write.Label(29, i, "1".trim()));
                if (detalle.getMontos().get(1) > 0)
                    writableSheet.addCell(new jxl.write.Label(30, i, formateadorDecimal.format(detalle.getMontos().get(1))));
                else
                    writableSheet.addCell(new jxl.write.Label(30, i, formateadorCero.format(detalle.getMontos().get(1))));
                if (detalle.getMontos().get(2) > 0)
                    writableSheet.addCell(new jxl.write.Label(31, i, formateadorDecimal.format(detalle.getMontos().get(2))));
                else
                    writableSheet.addCell(new jxl.write.Label(31, i, formateadorCero.format(detalle.getMontos().get(2))));
                if (detalle.getMontos().get(3) > 0)
                    writableSheet.addCell(new jxl.write.Label(32, i, formateadorDecimal.format(detalle.getMontos().get(3))));
                else
                    writableSheet.addCell(new jxl.write.Label(32, i, formateadorCero.format(detalle.getMontos().get(3))));
                if (detalle.getMontos().get(4) > 0)
                    writableSheet.addCell(new jxl.write.Label(33, i, formateadorDecimal.format(detalle.getMontos().get(4))));
                else
                    writableSheet.addCell(new jxl.write.Label(33, i, formateadorCero.format(detalle.getMontos().get(4))));
                if (detalle.getMontos().get(5) > 0)
                    writableSheet.addCell(new jxl.write.Label(34, i, formateadorDecimal.format(detalle.getMontos().get(5))));
                else
                    writableSheet.addCell(new jxl.write.Label(34, i, formateadorCero.format(detalle.getMontos().get(5))));
                if (detalle.getMontos().get(6) > 0)
                    writableSheet.addCell(new jxl.write.Label(35, i, formateadorDecimal.format(detalle.getMontos().get(6))));
                else
                    writableSheet.addCell(new jxl.write.Label(35, i, formateadorCero.format(detalle.getMontos().get(6))));
                if (detalle.getMontos().get(7) > 0)
                    writableSheet.addCell(new jxl.write.Label(36, i, formateadorDecimal.format(detalle.getMontos().get(7))));
                else
                    writableSheet.addCell(new jxl.write.Label(36, i, formateadorCero.format(detalle.getMontos().get(7))));
                if (detalle.getMontos().get(8) > 0)
                    writableSheet.addCell(new jxl.write.Label(37, i, formateadorDecimal.format(detalle.getMontos().get(8))));
                else
                    writableSheet.addCell(new jxl.write.Label(37, i, formateadorCero.format(detalle.getMontos().get(8))));
                if (detalle.getMontos().get(9) > 0)
                    writableSheet.addCell(new jxl.write.Label(38, i, formateadorDecimal.format(detalle.getMontos().get(9))));
                else
                    writableSheet.addCell(new jxl.write.Label(38, i, formateadorCero.format(detalle.getMontos().get(9))));
                if (detalle.getMontos().get(10) > 0)
                    writableSheet.addCell(new jxl.write.Label(39, i, formateadorDecimal.format(detalle.getMontos().get(10))));
                else
                    writableSheet.addCell(new jxl.write.Label(39, i, formateadorCero.format(detalle.getMontos().get(10))));
                if (detalle.getMontos().get(11) > 0)
                    writableSheet.addCell(new jxl.write.Label(40, i, formateadorDecimal.format(detalle.getMontos().get(11))));
                else
                    writableSheet.addCell(new jxl.write.Label(40, i, formateadorCero.format(detalle.getMontos().get(11))));
                if (detalle.getMontos().get(12) > 0)
                    writableSheet.addCell(new jxl.write.Label(41, i, formateadorDecimal.format(detalle.getMontos().get(12))));
                else
                    writableSheet.addCell(new jxl.write.Label(41, i, formateadorCero.format(detalle.getMontos().get(12))));
                i++;
            }
            // bw.write(archivo);
            // bw.flush();
            // bw.close();
            writableWorkbook.write();
            writableWorkbook.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void creaFap(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, Exception {
        AdecuacionBusinessLogic adecua = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
        HttpSession session = request.getSession(false);
        DiskFileItemFactory factory = DiskFileItemFactory.builder().get();
        JakartaServletFileUpload upload = new JakartaServletFileUpload(factory);
        String responsableCompleto = request.getParameter("responsableC");
        String responsable = "Responsable del Area";
        String area = "Area";
        if (responsableCompleto != null && !responsable.equals("")) {
            responsable = responsableCompleto.split("><")[0];
            if (responsableCompleto.split("><").length > 1)
                area = responsableCompleto.split("><")[1];
        }
        Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
        Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        if (usuario == null) {
            response.sendRedirect("../index.jsp");
            return;
        }
        int nFolioAdecuacion = Integer.parseInt((c.getFolio().substring(9)));
        Adecuacion adecuacion = adecua.cargaAdecuacion(nFolioAdecuacion);
        String file_name = c.getFolio();
        response.setContentType("application/vnd.ms-excel");
        response.addHeader("Content-Disposition", "inline; filename=\"FAP-01" + file_name + ".xls\"; charset=UTF-8\" pageEncoding=\"utf-8\"");
        // Para escribir en el archivo la fecha de tipo dd/mm/yyyy
        Date date = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fecha = sdf.format(date);
        String cFileExcel = upload.getRepositoryPath() + "/plantillaFAP01.xls";
        InputStream inp = new FileInputStream(cFileExcel);
        Workbook wb = new HSSFWorkbook(inp);
        Sheet sheet = wb.getSheetAt(0);
        Sheet metas = wb.getSheetAt(1);
        String justificacion = adecuacion.getEncabezado().getJustificacion();
        // Justificacion que se inserta en la segunda pestaña (hoja) del excel
        Row rjustificacion = metas.getRow(56);
        Cell cjustificacion = rjustificacion.getCell(1);
        cjustificacion.setCellValue(justificacion);
        // Empieza la lógica para meter los datos en la primera pestaña (hoja)
        // del excel, el formato FAP01
        // Inserta la fecha de hoy en la hoja del FAP01 dentro de excel
        Row rfechafap = sheet.getRow(7);
        Cell cfechafap = rfechafap.getCell(29);
        cfechafap.setCellValue(fecha);
        Row rwFecha = metas.getRow(70);
        rwFecha.getCell(11).setCellValue("FECHA: " + fecha);
        Row rwFechaInte = metas.getRow(55);
        rwFechaInte.getCell(0).setCellValue("Esta afectación de indicadores estratégicos corresponde a la adecuación presupuestal No. SAI: " + c.getFolio() + " de " + fecha);
        // Inserta el folio SAI dentro de la primera hoja (FAP01) dentro de
        // excel
        Row filaFolioSai = sheet.getRow(7);
        Cell celdaFolioSai = filaFolioSai.getCell(29);
        celdaFolioSai.setCellValue("SAI:" + c.getFolio());
        List<AdecuacionDetalle> saldoR = new ArrayList<AdecuacionDetalle>();
        List<AdecuacionDetalle> saldoA = new ArrayList<AdecuacionDetalle>();
        double[] sumaR = null;
        double[] sumaA = null;
        for (Iterator<AdecuacionDetalle> i = adecuacion.getDetalle().iterator(); i.hasNext(); ) {
            try {
                // obtengo las reducciones y las ampliaciones y las guardo para
                // no recorrer todo dos veces
                AdecuacionDetalle renglon = i.next();
                if (renglon.getTipo().toUpperCase().equals("R")) {
                    saldoR.add(renglon);
                } else if (renglon.getTipo().toUpperCase().equals("A")) {
                    saldoA.add(renglon);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw e;
            }
        }
        sumaR = new double[13];
        sumaA = new double[13];
        CellStyle estiloBordeDerecho = wb.createCellStyle();
        estiloBordeDerecho.setBorderRight(BorderStyle.MEDIUM);
        estiloBordeDerecho.setAlignment(HorizontalAlignment.CENTER);
        DataFormat format = wb.createDataFormat();
        estiloBordeDerecho.setDataFormat(format.getFormat("###,###0.00"));
        Font font = wb.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName("Helvetica");
        font.setBold(true);
        estiloBordeDerecho.setFont(font);
        insertaFila(wb, sheet, 15, saldoR.size());
        // int numfilas = imprimeSaldos(sheet, saldoR, sumaR, 15, 'R', 0,
        // estiloBordeDerecho);
        // imprimeSumas(sheet, numfilas + 5, sumaR, estiloBordeDerecho);
        // insertaFila(wb, sheet, numfilas += 9, saldoA.size());
        // numfilas = imprimeSaldos(sheet, saldoA, sumaA, numfilas, 'A',
        // numfilas - 24, estiloBordeDerecho);
        // imprimeSumas(sheet, numfilas + 3, sumaA, estiloBordeDerecho);
        // imprimeResponsable(sheet, numfilas + 8, responsable, area);
        wb.write(response.getOutputStream().toPath());
    }

    public void creaFap02(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AdecuacionBusinessLogic adecua = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
        HttpSession session = request.getSession(false);
        ArrayList<ArrayList> arrAdecAmpliacion = new ArrayList();
        ArrayList<ArrayList> arrAdecReduccion = new ArrayList();
        String[] arrFirmantePuesto = new String[] { "Ingrese el nombre", "Ingrese el puesto" };
        DiskFileItemFactory factory = DiskFileItemFactory.builder().get();
        JakartaServletFileUpload upload = new JakartaServletFileUpload(factory);
        Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
        int nFolio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
        Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        if (usuario == null) {
            response.sendRedirect("../index.jsp");
            return;
        }
        String ur = usuario.getU_UR();
        Date fecha_crea = new Timestamp(System.currentTimeMillis());
        Date date = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fecha = sdf.format(fecha_crea);
        String tipoAdec = "";
        String cFileExcelPlantilla = "";
        try {
            arrAdecAmpliacion = adecua.consultaFap02AmpliaExcel(nFolio);
            arrAdecReduccion = adecua.consultaFap02ReduceExcel(nFolio);
            tipoAdec = adecua.tipoAdecuacion(nFolio);
        } catch (Exception e) {
            throw new ServletException(e);
        }
        double suma = 0;
        String folioCompleto = c.getFolio();
        String unidadEjecutora = new String(c.getFolio().substring(5, 8));
        ArrayList arrAdecuacionReduccion = (arrAdecReduccion.size() == 0 ? null : (ArrayList) arrAdecReduccion.get(0));
        ArrayList arrAdecuacionAmpliacion = (arrAdecAmpliacion == null ? null : (ArrayList) arrAdecAmpliacion.get(0));
        String file_name = "Rep-FAP02" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        response.setContentType("application/vnd.ms-excel");
        response.addHeader("Content-Disposition", "inline; filename=\"" + file_name + "\"; ");
        if (tipoAdec != "" && (tipoAdec.equals("Transferencia") || tipoAdec.equals("Reducción") || tipoAdec.equals("Ampliación"))) {
            cFileExcelPlantilla = getServletContext().getRealPath("Reportes" + File.separator + "plantilla_FAP_02_TRANS.xls");
        } else if (tipoAdec != "" && tipoAdec.equals("Calendario")) {
            cFileExcelPlantilla = getServletContext().getRealPath("Reportes" + File.separator + "plantilla_FAP_02_CAL.xls");
        }
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        Font fuenteMoneda = workbook.createFont();
        fuenteMoneda.setBold(true);
        fuenteMoneda.setFontHeightInPoints((short) 10);
        fuenteMoneda.setFontName("Arial");
        Font fuentePrograma = workbook.createFont();
        fuentePrograma.setFontHeightInPoints((short) 8);
        fuentePrograma.setBold(true);
        fuentePrograma.setFontName("Arial");
        Font fuenteFecha = workbook.createFont();
        fuenteFecha.setFontHeightInPoints((short) 8);
        fuenteFecha.setFontName("Calibri");
        CellStyle estiloPrograma = workbook.createCellStyle();
        estiloPrograma.setFont(fuentePrograma);
        estiloPrograma.setBorderBottom(BorderStyle.THIN);
        estiloPrograma.setBorderRight(BorderStyle.THIN);
        estiloPrograma.setWrapText(true);
        CellStyle estiloMoneda = workbook.createCellStyle();
        estiloMoneda.setFont(fuenteMoneda);
        estiloMoneda.setDataFormat((short) 7);
        estiloMoneda.setBorderBottom(BorderStyle.THIN);
        estiloMoneda.setVerticalAlignment(VerticalAlignment.CENTER);
        CellStyle estiloFecha = workbook.createCellStyle();
        estiloFecha.setFont(fuenteFecha);
        estiloFecha.setAlignment(HorizontalAlignment.RIGHT);
        for (int k = 0; k < 2; k++) {
            ArrayList<?> arrInfoDeta = (k == 0 ? arrAdecuacionAmpliacion : arrAdecuacionReduccion);
            int nrenglones = arrInfoDeta.size();
            suma = 0;
            String fiscal = "";
            try {
                fiscal = adecua.obtenEjercicioFiscal();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Sheet sheet0 = workbook.getSheetAt(k);
            Iterator<Row> rowIterator = sheet0.iterator();
            int nRenglon = 0;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (nRenglon == 6) {
                    Cell efiscal = row.getCell(2);
                    efiscal.setCellStyle(estiloFecha);
                    efiscal.setCellValue("FECHA: " + fecha + " SOLICITUD: " + c.getFolio());
                }
                /*
				 * if (nRenglon == 8) { Cell ue = row.getCell(1);
				 * ue.setCellValue(unidadEjecutora); Cell foliosai =
				 * row.getCell(5); foliosai.setCellValue(folioCompleto); Cell
				 * fechac = row.getCell(6); fechac.setCellValue(fecha); }
				 */
                /*
				 * if (nRenglon == 11) { Cell justifN = row.getCell(2);
				 * justifN.setCellValue((String) arrInfoEnca.get(5)); }
				 */
                /*
				 * if (nRenglon == 16) { Cell justifAR = row.getCell(2);
				 * justifAR.setCellValue((String) arrInfoEnca.get(k == 0 ? 4 :
				 * 3)); }
				 */
                int renglon = 0;
                int count = 0;
                if (nRenglon == 10) {
                    for (int j = 0; j < nrenglones; j++) {
                        ArrayList arrAdecuacionD = (ArrayList) arrInfoDeta.get(j);
                        Row r = sheet0.getRow(10 + count);
                        if (r == null) {
                            r = sheet0.createRow(10 + count);
                        }
                        for (int i = 1; i <= 2; i++) {
                            Cell ue = r.createCell(i - 1);
                            if (i == 2) {
                                ue.setCellType(CellType.NUMERIC);
                                ue.setCellStyle(estiloMoneda);
                                ue.setCellValue(Double.parseDouble((String) arrAdecuacionD.get(i).toString()));
                                suma += Double.parseDouble((String) arrAdecuacionD.get(i).toString());
                            } else {
                                ue.setCellStyle(estiloPrograma);
                                ue.setCellValue(((String) arrAdecuacionD.get(i)).toUpperCase());
                            }
                        }
                        count++;
                    }
                }
                if (nRenglon == 20) {
                    Cell sumat = row.getCell(1);
                    sumat.setCellValue(suma);
                }
                /*
				 * if (nRenglon == 49) { Cell puesto = row.getCell(0);
				 * puesto.setCellValue(arrFirmantePuesto[1]);
				 * puesto.setCellStyle(styleTexto); }
				 * 
				 * if (nRenglon == 54) { Cell nombre = row.getCell(0);
				 * nombre.setCellValue(arrFirmantePuesto[0]);
				 * nombre.setCellStyle(styleTextoBorde); }
				 */
                nRenglon++;
            }
        }
        fsArchivo.close();
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos.toPath());
        /* Cierra Flujos */
        bos.flush();
        bos.close();
        fos.close();
        ServletOutputStream out = response.getOutputStream();
        ServletContext context = getServletConfig().getServletContext();
        String mimetype = context.getMimeType(file_name);
        try {
            Util.doDownload(out, file_name, file_name, mimetype);
        } catch (Exception e) {
            throw new ServletException(e);
        }
        out.flush();
        out.close();
    }

    public int imprimeSaldos(Sheet sheet, List<Fap01> saldoR, double[] suma, int numfila, char tipo, int secuencia, CellStyle estiloBordeDerecho) {
        for (int i = 0; i < saldoR.size(); i++) {
            Fap01 objSaldo = saldoR.get(i);
            Row row = sheet.getRow(numfila);
            numfila++;
            Cell cell;
            cell = row.getCell(0);
            if (tipo == 'R')
                // lo vuelvo string para
                cell.setCellValue(String.valueOf(10));
            else // que se muestre
            // correcto en el excel
            // y no en formato de
            // pesos
            if (// lo hago con else if porque no sé si en
            tipo == 'A')
                // algún momento pueda haber más tipos
                // lo vuelvo string para
                cell.setCellValue(String.valueOf(13));
            // que se muestre
            // correcto en el excel
            // y no en formato de
            // pesos
            cell = row.getCell(1);
            // lo vuelvo
            cell.setCellValue(String.valueOf(secuencia += 1));
            // string para
            // que se
            // muestre
            // correcto en
            // el excel y no
            // en formato de
            // pesos
            cell = row.getCell(2);
            cell.setCellValue(objSaldo.getEjercicio());
            cell = row.getCell(3);
            cell.setCellValue(objSaldo.getRamo());
            cell = row.getCell(4);
            cell.setCellValue(objSaldo.getAE());
            cell = row.getCell(5);
            cell.setCellValue(objSaldo.getUnidadResponsable());
            cell = row.getCell(6);
            cell.setCellValue(objSaldo.getGf());
            cell = row.getCell(7);
            cell.setCellValue(objSaldo.getF());
            cell = row.getCell(8);
            cell.setCellValue(objSaldo.getSf());
            cell = row.getCell(9);
            cell.setCellValue(objSaldo.getPG());
            cell = row.getCell(10);
            cell.setCellValue(objSaldo.getAI());
            cell = row.getCell(11);
            cell.setCellValue(objSaldo.getPP());
            cell = row.getCell(12);
            cell.setCellValue(objSaldo.getOG());
            cell = row.getCell(13);
            cell.setCellValue(objSaldo.getTg());
            cell = row.getCell(14);
            cell.setCellValue(objSaldo.getFf());
            cell = row.getCell(15);
            cell.setCellValue(objSaldo.getEf());
            cell = row.getCell(16);
            cell.setCellValue(objSaldo.getCartera());
            for (int cnt = 0; cnt < objSaldo.getMontos().length; cnt++) {
                cell = row.getCell(17 + cnt);
                cell.setCellValue(objSaldo.getMontos()[cnt]);
                suma[cnt] += objSaldo.getMontos()[cnt];
            }
            // cell.setCellStyle(estiloBordeDerecho);
        }
        return numfila;
    }

    public int imprimeSaldos(Sheet sheet, ArrayList<Saldo> saldo, double[] suma, int numfila, char tipo, int secuencia, CellStyle estiloBordeDerecho) {
        for (int i = 0; i < saldo.size(); i++) {
            Saldo objSaldo = saldo.get(i);
            String ep = objSaldo.getClaveSIAFF() + "." + objSaldo.getClaveInterna();
            String[] splitSiaff = ep.split("\\.");
            Row row = sheet.getRow(numfila);
            numfila++;
            Cell cell;
            cell = row.getCell(0);
            if (tipo == 'R')
                // lo vuelvo string para
                cell.setCellValue(String.valueOf(10));
            else // que se muestre
            // correcto en el excel
            // y no en formato de
            // pesos
            if (// lo hago con else if porque no sé si en
            tipo == 'A')
                // algún momento pueda haber más tipos
                // lo vuelvo string para
                cell.setCellValue(String.valueOf(13));
            // que se muestre
            // correcto en el excel
            // y no en formato de
            // pesos
            cell = row.getCell(1);
            // lo vuelvo
            cell.setCellValue(String.valueOf(secuencia += 1));
            // string para
            // que se
            // muestre
            // correcto en
            // el excel y no
            // en formato de
            // pesos
            for (int j = 0; j < 16; j++) {
                cell = row.getCell(j + 2);
                // le hago trim para
                cell.setCellValue(splitSiaff[j].trim());
                // eliminar los
                // espacios que
                // puedan llegar a
                // contener algunos
                // valores
            }
            cell = row.getCell(18);
            cell.setCellValue(Double.valueOf(objSaldo.getMontoAnual()));
            cell = row.getCell(19);
            cell.setCellValue(Double.valueOf(objSaldo.getMontoEnero()));
            cell = row.getCell(20);
            cell.setCellValue(Double.valueOf(objSaldo.getMontoFebrero()));
            cell = row.getCell(21);
            cell.setCellValue(Double.valueOf(objSaldo.getMontoMarzo()));
            cell = row.getCell(22);
            cell.setCellValue(Double.valueOf(objSaldo.getMontoAbril()));
            cell = row.getCell(23);
            cell.setCellValue(Double.valueOf(objSaldo.getMontoMayo()));
            cell = row.getCell(24);
            cell.setCellValue(Double.valueOf(objSaldo.getMontoJunio()));
            cell = row.getCell(25);
            cell.setCellValue(Double.valueOf(objSaldo.getMontoJulio()));
            cell = row.getCell(26);
            cell.setCellValue(Double.valueOf(objSaldo.getMontoAgosto()));
            cell = row.getCell(27);
            cell.setCellValue(Double.valueOf(objSaldo.getMontoSeptiembre()));
            cell = row.getCell(28);
            cell.setCellValue(Double.valueOf(objSaldo.getMontoOctubre()));
            cell = row.getCell(29);
            cell.setCellValue(Double.valueOf(objSaldo.getMontoNoviembre()));
            cell = row.getCell(30);
            cell.setCellStyle(estiloBordeDerecho);
            cell.setCellValue(Double.valueOf(objSaldo.getMontoDiciembre()));
            suma[0] += Double.valueOf(objSaldo.getMontoAnual());
            suma[1] += Double.valueOf(objSaldo.getMontoEnero());
            suma[2] += Double.valueOf(objSaldo.getMontoFebrero());
            suma[3] += Double.valueOf(objSaldo.getMontoMarzo());
            suma[4] += Double.valueOf(objSaldo.getMontoAbril());
            suma[5] += Double.valueOf(objSaldo.getMontoMayo());
            suma[6] += Double.valueOf(objSaldo.getMontoJunio());
            suma[7] += Double.valueOf(objSaldo.getMontoJulio());
            suma[8] += Double.valueOf(objSaldo.getMontoAgosto());
            suma[9] += Double.valueOf(objSaldo.getMontoSeptiembre());
            suma[10] += Double.valueOf(objSaldo.getMontoOctubre());
            suma[11] += Double.valueOf(objSaldo.getMontoNoviembre());
            suma[12] += Double.valueOf(objSaldo.getMontoDiciembre());
        }
        return numfila;
    }

    public void imprimeSumas(Sheet sheet, int numfila, double[] suma, CellStyle estiloBordeDerecho) {
        // numfila
        // me
        // sirve
        // para
        // saber
        // a
        // partir
        // de
        // que
        // linea
        // voy
        // a
        // imprimir
        Row row = sheet.getRow(numfila);
        Cell cell;
        for (int i = 0; i < 13; i++) {
            cell = row.getCell(i + 17);
            cell.setCellValue(suma[i]);
        }
        cell = row.getCell(29);
        CellStyle estiloSuma = cell.getCellStyle();
        estiloSuma.setBorderRight(BorderStyle.MEDIUM);
    }

    public void insertaFila(Workbook wb, Sheet sheet, int numfila, int cantidad) {
        Row row = sheet.getRow(numfila + 1);
        Cell cell;
        cell = row.getCell(0);
        CellStyle style = cell.getCellStyle();
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        DataFormat format = wb.createDataFormat();
        style.setDataFormat(format.getFormat("###,###0.00"));
        Cell cellaux;
        for (int i = 0; i < cantidad; i++) {
            Row newRow = sheet.createRow(numfila + i);
            sheet.shiftRows(numfila + i, sheet.getLastRowNum(), 1, true, false);
            newRow = sheet.createRow(numfila + i);
            // Row newRow = sheet.getRow(numfila+i);
            // newRow = sheet.getRow(numfila+i);
            for (int j = 0; j <= 30; j++) {
                cellaux = newRow.createCell(j);
                cellaux.setCellStyle(style);
            }
        }
        // borro
        sheet.shiftRows(numfila + cantidad + 1, sheet.getLastRowNum(), -1, true, false);
        // la
        // última
        // fila
        // porque
        // me
        // queda
        // sin
        // formato
    }

    public void imprimeResponsable(Sheet sheet, int fila, String responsable, String area) {
        Row row = sheet.getRow(fila + 4);
        Cell cell;
        cell = row.getCell(19);
        CellStyle estilo = cell.getCellStyle();
        cell.setCellValue(responsable);
        // sheet.addMergedRegion(new
        // org.apache.poi.ss.util.CellRangeAddress(fila+1,fila+1,18,21));
        row = sheet.getRow(fila);
        cell = row.getCell(18);
        // cell.setCellStyle(estilo);
        cell.setCellValue(area);
    }

    private void imprimeAdecuacion(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String reportPath = getServletContext().getRealPath("Reportes" + File.separator + "ReporteAdecuaciones.jasper");
        String imgPath = getServletContext().getRealPath("Reportes" + File.separator);
        reportesBussinesObject objReporte = new reportesBussinesObject(jniName);
        objReporte.reporteImprimeAdecuacionPDF(request, response, reportPath, imgPath);
    }

    private void printError(HttpServletResponse response, Exception e) throws Exception {
        response.setContentType("text/html");
        ServletOutputStream out = response.getOutputStream();
        out.println("<html>");
        out.println("\t<body>");
        out.println("\t\t<H2>Error generando impresion de adecuacion</h2>");
        out.println("\t\t<br><br>Ocurrio el siguiente error mientras se generaba la impresion de la adecuacion");
        out.println("\t\t<br><textarea rows=\"4\" cols=\"50\">" + e + "</textarea>");
        out.println("\t\t<br><H3>Por favor reintente, Si el problema se presenta de nuevo reporte al administrador</h3>");
        out.println("\t</body>");
        out.println("</html>");
        out.flush();
        out.close();
    }

    public void creaFapAdec(HttpServletRequest request, HttpServletResponse response) throws Exception {
        AdecuacionBusinessLogic adecua = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
        HttpSession session = request.getSession(false);
        DiskFileItemFactory factory = DiskFileItemFactory.builder().get();
        JakartaServletFileUpload upload = new JakartaServletFileUpload(factory);
        String responsableCompleto = request.getParameter("responsable");
        String responsable = "Responsable del Area";
        String area = "Area";
        String tipoAdec = "";
        int nConsecutivoSICOP = 0;
        if (responsableCompleto != null && !responsable.equals("")) {
            responsable = responsableCompleto.split("><")[0];
            area = responsableCompleto.split("><")[1];
        }
        Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
        Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        if (usuario == null) {
            response.sendRedirect("../index.jsp");
            return;
        }
        int folio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
        List<Fap01> arrAdecuacion = null;
        AdecuacionBusinessLogic adec = new AdecuacionBusinessLogic(jniName);
        ArrayList<ArrayList> arrAdecAmpliacion = new ArrayList();
        //ArrayList<ArrayList> arrAdecReduccion = new ArrayList();
        ArrayList<ArrayList> arrActividadInstitucional = new ArrayList();
        arrAdecuacion = adec.cargaFAP01(folio);
        arrAdecAmpliacion = adec.consultaFap02AmpliaExcel(folio);
        //arrAdecReduccion = adec.consultaFap02ReduceExcel(folio);
        tipoAdec = adecua.tipoAdecuacion(folio);
        arrActividadInstitucional = adecua.actividadInstitucional(folio);
        String file_name = "" + nConsecutivoSICOP;
        response.setContentType("application/vnd.ms-excel");
        response.addHeader("Content-Disposition", "inline; filename=\"FAP-01" + file_name + ".xls\"; charset=UTF-8\" pageEncoding=\"utf-8\"");
        // Para escribir en el archivo la fecha de tipo dd/mm/yyyy
        Date date = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fecha = sdf.format(date);
        String cFileExcel = upload.getRepositoryPath() + "/plantillaFAP01.xls";
        InputStream inp = new FileInputStream(cFileExcel);
        Workbook wb = new HSSFWorkbook(inp);
        Sheet sheet = wb.getSheetAt(0);
        Sheet hJustificacion = wb.getSheetAt(1);
        Sheet metas = wb.getSheetAt(2);
        // Empieza la lógica para meter los datos en la primera pestaña (hoja)
        // del excel, el formato FAP01
        // Inserta la fecha de hoy en la hoja del FAP01 dentro de excel
        Row rfechafap = sheet.getRow(7);
        Cell cfechafap = rfechafap.getCell(29);
        cfechafap.setCellValue(fecha);
        Row rwFecha = metas.getRow(70);
        rwFecha.getCell(23).setCellValue("FECHA: " + fecha);
        Row rwFechaInte = metas.getRow(57);
        rwFechaInte.getCell(0).setCellValue("Esta afectación de indicadores estratégicos corresponde a la adecuación presupuestal No. SAI: " + c.getFolio() + " de " + fecha);
        Row rwFechaAdec = hJustificacion.getRow(6);
        rwFechaAdec.getCell(2).setCellValue("FECHA: " + fecha + " SOLICITUD: " + c.getFolio());
        // Inserta el folio SAI dentro de la primera hoja (FAP01) dentro de
        // excel
        Row filaFolioSai = sheet.getRow(7);
        Cell celdaFolioSai = filaFolioSai.getCell(28);
        celdaFolioSai.setCellValue("SAI:" + c.getFolio());
        /* Fin Inserta datos en encabezado de metas y hoja1 */
        List<Fap01> saldoR = new ArrayList<Fap01>();
        List<Fap01> saldoA = new ArrayList<Fap01>();
        double[] sumaR = null;
        double[] sumaA = null;
        for (int i = 0; i < arrAdecuacion.size(); i++) {
            // obtengo las reducciones y las ampliaciones y las guardo para no
            // recorrer todo dos veces
            Fap01 objSaldo = arrAdecuacion.get(i);
            if (objSaldo.getMovimiento().equalsIgnoreCase("R")) {
                saldoR.add(objSaldo);
            } else if (objSaldo.getMovimiento().equalsIgnoreCase("A")) {
                saldoA.add(objSaldo);
            }
        }
        sumaR = new double[13];
        sumaA = new double[13];
        CellStyle estiloBordeDerecho = wb.createCellStyle();
        estiloBordeDerecho.setBorderRight(BorderStyle.MEDIUM);
        estiloBordeDerecho.setBorderLeft(BorderStyle.MEDIUM);
        estiloBordeDerecho.setAlignment(HorizontalAlignment.CENTER);
        DataFormat format = wb.createDataFormat();
        estiloBordeDerecho.setDataFormat(format.getFormat("###,###0.00"));
        Font font = wb.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName("Helvetica");
        font.setBold(true);
        estiloBordeDerecho.setFont(font);
        insertaFila(wb, sheet, 15, saldoR.size());
        int numfilas = imprimeSaldos(sheet, saldoR, sumaR, 15, 'R', 0, estiloBordeDerecho);
        imprimeSumas(sheet, numfilas + 5, sumaR, estiloBordeDerecho);
        insertaFila(wb, sheet, numfilas += 9, saldoA.size());
        numfilas = imprimeSaldos(sheet, saldoA, sumaA, numfilas, 'A', numfilas - 24, estiloBordeDerecho);
        imprimeSumas(sheet, numfilas + 3, sumaA, estiloBordeDerecho);
        /* entrar a funcion para llenar ampliacones y reducciones FAP02 */
        Row rwJustifiacionTRANSA = hJustificacion.getRow(10);
        rwJustifiacionTRANSA.getCell(2).setCellValue("Con base en lo dispuesto en los artículos 57 y 58 de la Ley Federal de Presupuesto y Responsabilidad Hacendaria (LFPRH), artículos 97 y 100 de su Reglamento (RLFRH), la Comisión Nacional Forestal (CONAFOR) solicita la presente adecuación presupuestaria interna.");
        Row rwJustifiacionTRANSR = hJustificacion.getRow(16);
        rwJustifiacionTRANSR.getCell(2).setCellValue("Cabe señalar que se dará cumplimiento a lo establecido en el artículo 9 del PEF 2025. Este movimiento presupuestario no afecta el cumplimiento de los objetivos y metas autorizados de esta Comisión y permite su mejor cumplimiento y es de carácter no regularizable  y se incluirá en la carpeta de la Primera Sesión Ordinaria de la Junta de Gobierno de la Comisión Nacional Forestal del 2025.");
        String fiscal = adecua.obtenEjercicioFiscal();
        imprimeFAP02(wb, fiscal, arrAdecAmpliacion, /*, arrAdecReduccion*/
        fecha, c.getFolio());
        imprimeMETA(wb, arrActividadInstitucional);
        // imprimeResponsable(sheet, numfilas + 8, responsable, area);
        wb.write(response.getOutputStream().toPath());
    }

    public void imprimeFAP02(Workbook wb, String fiscal, ArrayList<ArrayList> arrAdecAmpliacion, /*, ArrayList<ArrayList> arrAdecReduccion*/
    String fecha, String folio) {
        Font fuenteMoneda = wb.createFont();
        fuenteMoneda.setBold(true);
        fuenteMoneda.setFontHeightInPoints((short) 10);
        fuenteMoneda.setFontName("Arial");
        Font fuentePrograma = wb.createFont();
        fuentePrograma.setFontHeightInPoints((short) 8);
        fuentePrograma.setBold(true);
        fuentePrograma.setFontName("Arial");
        Font fuenteFecha = wb.createFont();
        fuenteFecha.setFontHeightInPoints((short) 8);
        fuenteFecha.setFontName("Calibri");
        CellStyle estiloPrograma = wb.createCellStyle();
        estiloPrograma.setFont(fuentePrograma);
        estiloPrograma.setBorderBottom(BorderStyle.THIN);
        estiloPrograma.setBorderRight(BorderStyle.THIN);
        estiloPrograma.setBorderLeft(BorderStyle.THIN);
        estiloPrograma.setBorderTop(BorderStyle.THIN);
        estiloPrograma.setWrapText(true);
        CellStyle estiloMoneda = wb.createCellStyle();
        estiloMoneda.setFont(fuenteMoneda);
        estiloMoneda.setDataFormat((short) 7);
        estiloMoneda.setBorderBottom(BorderStyle.THIN);
        estiloMoneda.setVerticalAlignment(VerticalAlignment.CENTER);
        CellStyle estiloFecha = wb.createCellStyle();
        estiloFecha.setFont(fuenteFecha);
        estiloFecha.setAlignment(HorizontalAlignment.RIGHT);
        double suma = 0;
        int k = 1;
        //for (int k = 1; k < 3; k++) {
        List<?> arrInfoDeta = arrAdecAmpliacion;
        int nrenglones = arrInfoDeta.size();
        suma = 0;
        Sheet sheet0 = wb.getSheetAt(k);
        Iterator<Row> rowIterator = sheet0.iterator();
        int nRenglon = 0;
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (nRenglon == 6) {
                Cell efiscal = row.getCell(2);
                efiscal.setCellStyle(estiloFecha);
                efiscal.setCellValue("FECHA: " + fecha + " SOLICITUD: " + folio);
            }
            int renglon = 0;
            int count = 0;
            ArrayList arrAdecuacionD;
            if (nRenglon == 10) {
                for (int j = 0; j < nrenglones; j++) {
                    arrAdecuacionD = (ArrayList) arrInfoDeta.get(j);
                    for (int cntProgramas = 0; cntProgramas < arrAdecuacionD.size(); cntProgramas++) {
                        Row r = sheet0.getRow(10 + count);
                        if (r == null) {
                            r = sheet0.createRow(10 + count);
                        }
                        List<String> programa = (ArrayList<String>) arrAdecuacionD.get(cntProgramas);
                        for (int i = 1; i <= 2; i++) {
                            Cell ue = r.createCell(i - 1);
                            if (i == 2) {
                                ue.setCellType(CellType.NUMERIC);
                                ue.setCellStyle(estiloMoneda);
                                ue.setCellValue(Double.parseDouble(programa.get(i)));
                                suma += Double.parseDouble(programa.get(i));
                            } else {
                                ue.setCellStyle(estiloPrograma);
                                ue.setCellValue(programa.get(i).toUpperCase());
                            }
                        }
                        count++;
                    }
                }
            }
            if (nRenglon == 17) {
                Cell sumat = row.getCell(1);
                sumat.setCellValue(suma);
            }
            nRenglon++;
        }
        //}
    }

    private void imprimeMETA(Workbook wb, ArrayList<ArrayList> arrActividadInstitucional) {
        Font fuentePrograma = wb.createFont();
        fuentePrograma.setFontHeightInPoints((short) 12);
        fuentePrograma.setFontName("Montserrat");
        CellStyle estiloPrograma = wb.createCellStyle();
        estiloPrograma.setFont(fuentePrograma);
        estiloPrograma.setBorderRight(BorderStyle.THIN);
        estiloPrograma.setBorderLeft(BorderStyle.THIN);
        estiloPrograma.setWrapText(true);
        int k = 2;
        ArrayList<?> arrInfoDeta = arrActividadInstitucional;
        int nrenglones = arrInfoDeta.size();
        Sheet sheet0 = wb.getSheetAt(k);
        Iterator<Row> rowIterator = sheet0.iterator();
        int nRenglon = 0;
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            int count = 0;
            ArrayList arrAI;
            ArrayList arrAIP;
            if (nRenglon == 13) {
                for (int j = 0; j < nrenglones; j++) {
                    arrAI = (ArrayList) arrInfoDeta.get(j);
                    for (int cntProgramas = 0; cntProgramas < arrAI.size(); cntProgramas++) {
                        Row r = sheet0.getRow(13 + count);
                        if (r == null) {
                            r = sheet0.createRow(13 + count);
                        }
                        List<String> programa = (ArrayList<String>) arrAI.get(cntProgramas);
                        for (int i = 0; i <= 4; i++) {
                            Cell ue = r.createCell(i);
                            ue.setCellStyle(estiloPrograma);
                            ue.setCellValue(programa.get(i).toUpperCase());
                        }
                        count++;
                    }
                }
            }
            if (nRenglon == 34) {
                for (int j = 0; j < nrenglones; j++) {
                    arrAIP = (ArrayList) arrInfoDeta.get(j);
                    for (int cntProgramas = 0; cntProgramas < arrAIP.size(); cntProgramas++) {
                        Row r1 = sheet0.getRow(34 + count);
                        if (r1 == null) {
                            r1 = sheet0.createRow(34 + count);
                        }
                        List<String> AI = (ArrayList<String>) arrAIP.get(cntProgramas);
                        for (int i = 0; i <= 4; i++) {
                            Cell uai = r1.createCell(i);
                            uai.setCellStyle(estiloPrograma);
                            uai.setCellValue(AI.get(i).toUpperCase());
                        }
                        count++;
                    }
                }
            }
            nRenglon++;
        }
    }
}
