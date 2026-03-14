package com.syc.contable.servlet;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
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
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import com.syc.contable.AdecuacionBusinessLogic;
import com.syc.contable.core.AdecuacionCalendario;
import com.syc.contable.core.Saldo;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoDatoManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.reportes.reportesBussinesObject;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;
import org.apache.commons.fileupload2.core.DiskFileItemFactory;

@WebServlet(name = "FIAFServlet", urlPatterns = { "/gstnmngr/FIAFServlet" })
public class FIAFServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(FIAFServlet.class);

    //para obtener el ejercicio fiscal en diferentes funciones
    AdecuacionBusinessLogic adecProy = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);

    /**
     * Constructor of the object.
     */
    public FIAFServlet() {
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
     * The doPost method of the servlet. <br>
     *
     * This method is called when a form has its tag value method equals to post.
     *
     * @param request the request send by the client to the server
     * @param response the response send by the server to the client
     * @throws ServletException if an error occurred
     * @throws IOException if an error occurred
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
        Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        if (c == null) {
            response.sendRedirect("../index.jsp");
            return;
        }
        Map m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
        String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
        String accion = request.getParameter("accion");
        int folioFIAF = Integer.parseInt(request.getParameter("folioFIAF"));
        String DATE_FORMAT = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        // today
        Calendar c1 = Calendar.getInstance();
        String today = sdf.format(c1.getTime());
        String cJustificacionA = "";
        String cJustificacionR = "";
        String cJustificacionNormativa = "";
        if (request.getParameter("justificacionA") != null && request.getParameter("justificacionR") != null && request.getParameter("justificacionNormativa") != null) {
            cJustificacionA = new String(request.getParameter("justificacionA").getBytes("ISO-8859-1"), "UTF-8");
            cJustificacionR = new String(request.getParameter("justificacionR").getBytes("ISO-8859-1"), "UTF-8");
            cJustificacionNormativa = new String(request.getParameter("justificacionNormativa").getBytes("ISO-8859-1"), "UTF-8");
        }
        String[] foliosAdecuaciones = request.getParameterValues("foliosAdec[]");
        if (accion != null && !"".equals(accion)) {
            if ("1".equals(accion)) {
                try {
                    PrintWriter out = response.getWriter();
                    String aEjercicioFiscal = adecProy.obtenEjercicioFiscal();
                    out.println(new String(adecProy.integraAdecuacionesFIAF(folioFIAF, foliosAdecuaciones, cJustificacionA, cJustificacionR, usuario, cJustificacionNormativa, aEjercicioFiscal, "Ampliación", "5").getBytes("UTF-8"), "ISO-8859-1"));
                } catch (SQLException e) {
                    e.printStackTrace();
                    log.warn("Object: {}", e.getMessage());
                }
            }
            if ("2".equals(accion)) {
                String cFechaAplicacion = c.getCasoDato("FECHA_AP_CONT").getValor();
                int nFolio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
                try {
                    PrintWriter out = response.getWriter();
                    String aEjercicioFiscal = adecProy.obtenEjercicioFiscal();
                    out.println(new String(adecProy.aplicaFIAF(nFolio, usuario, aEjercicioFiscal, usuario.getU_Ramo(), usuario.getU_UR(), c, usuario.getPropiedad("CCENTROCONTABLE").getValor(), cFechaAplicacion, m, prefixPath).getBytes("UTF-8"), "ISO-8859-1"));
                } catch (SQLException e) {
                    e.printStackTrace();
                    log.warn("Object: {}", e.getMessage());
                }
            }
            if ("3".equals(accion)) {
                try {
                    PrintWriter out = response.getWriter();
                    out.println(new String(adecProy.desIntegraFIAF(folioFIAF, c, usuario.getLogin(), m, prefixPath, foliosAdecuaciones).getBytes("UTF-8"), "ISO-8859-1"));
                } catch (Exception e) {
                    e.printStackTrace();
                    log.warn("Object: {}", e.getMessage());
                }
            }
            if ("4".equals(accion)) {
                try {
                    PrintWriter out = response.getWriter();
                    out.println(new String(adecProy.agregaJustificaciones(folioFIAF, cJustificacionA, cJustificacionR, cJustificacionNormativa).getBytes("UTF-8"), "ISO-8859-1"));
                } catch (Exception e) {
                    e.printStackTrace();
                    log.warn("Object: {}", e.getMessage());
                }
            }
            if ("5".equals(accion)) {
                try {
                    String motivoRechazo = "";
                    if (request.getParameter("motivoRechazo") != null) {
                        motivoRechazo = new String(request.getParameter("motivoRechazo").getBytes("ISO-8859-1"), "UTF-8");
                    }
                    PrintWriter out = response.getWriter();
                    out.println(new String(adecProy.cancelarFIAF(c, m, prefixPath, usuario, today, motivoRechazo).getBytes("UTF-8"), "ISO-8859-1"));
                } catch (Exception e) {
                    e.printStackTrace();
                    log.warn("Object: {}", e.getMessage());
                }
            }
            if ("6".equals(accion)) {
                try {
                    String nMap = "", fMap = "", nSicop = "", fSicop = "";
                    if (request.getParameter("nMap") != null && request.getParameter("fMap") != null && request.getParameter("fSicop") != null && request.getParameter("nSicop") != null) {
                        nMap = request.getParameter("nMap");
                        fMap = request.getParameter("fMap");
                        nSicop = request.getParameter("nSicop");
                        fSicop = request.getParameter("fSicop");
                    }
                    PrintWriter out = response.getWriter();
                    out.println(new String(adecProy.autorizaIntegracionFIAF(c, nSicop, fSicop, nMap, fMap, m, prefixPath, usuario.getLogin(), usuario.getPropiedad("CCENTROCONTABLE").getValor(), usuario).getBytes("UTF-8"), "ISO-8859-1"));
                    adecProy.avanzaAdecFIAF(folioFIAF, foliosAdecuaciones);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.warn("Object: {}", e.getMessage());
                }
            }
            if ("7".equals(accion)) {
                String documento = request.getParameter("documento");
                if ("1".equals(documento)) {
                    response.sendRedirect("../plantillasCasos/layOutFIAFAdecuacion.jsp");
                    return;
                }
                if ("2".equals(documento)) {
                    try {
                        creaSicop(request, response);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                if ("3".equals(documento)) {
                    try {
                        creaFap(request, response);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                if ("4".equals(documento)) {
                    try {
                        creaRepIntegraFIAFExcel(request, response);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if ("5".equals(documento)) {
                    try {
                        creaRepIntegraFIAFPdf(request, response);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if ("6".equals(documento)) {
                    try {
                        creaFap02(request, response);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

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

    public void creaSicop(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
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
        final int nFolio = Integer.parseInt(c.getFolio().split("-")[2]);
        String file_name = c.getFolio();
        response.setContentType("application/vnd.ms-excel");
        response.addHeader("Content-Disposition", "inline; filename=\"LAYOUT-SICOP" + file_name + ".xls\";");
        // Para escribir en el archivo la fecha de tipo dd/mm/yyyy
        Date date = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fecha = sdf.format(date);
        WritableWorkbook writableWorkbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        //WritableWorkbook writableWorkbook = jxl.Workbook.createWorkbook(new File("LAYOUT-SICOP" + file_name + ".csv"));
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
            int folioSicopInt = 0;
            try {
                folioSicopInt = adecua.consecutivoSICOPFIAF(nFolio);
                //Si folioSicop es 0, entonces no ha generado ninguno, que lo obtenga
                folioSicopInt = adecua.obtenFolioSicop(folioSicopInt == 0);
                if (folioSicopInt > 0) {
                    adecua.actualizaFolioSicopFIAFEncabezado(folioSicopInt, Integer.parseInt(c.getFolio().split("-")[2]));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            String folioSicop = String.valueOf(folioSicopInt);
            while (folioSicop.length() < 5) {
                folioSicop = "0" + folioSicop.trim();
            }
            int nivel = adecua.nivelFIAF(nFolio);
            archivo += ",";
            archivo += ",";
            archivo += nivel + ",_0" + folioSicop.trim();
            archivo += nivel + ",_0" + folioSicop.trim();
            archivo += "," + folioSicop.trim();
            for (int i = 17; i <= 44; i++) archivo += ",";
            writableSheet.addCell(new jxl.write.Label(13, 0, nivel + "_0" + folioSicop.trim()));
            writableSheet.addCell(new jxl.write.Label(14, 0, nivel + "_0" + folioSicop.trim()));
            writableSheet.addCell(new jxl.write.Label(15, 0, folioSicop.trim()));
        } catch (Exception jxlex) {
            jxlex.printStackTrace();
        }
        boolean resp = false;
        AdecuacionCalendario ac = adecua.adecuacionCalendarioFIAF(nFolio);
        try {
            for (int i = 0; i < ac.saldoLength(); i++) {
                Saldo objSaldo = ac.getSaldo(i);
                if (ac.getMovimiento(i).equals("A")) {
                    resp = adecua.esModificado(objSaldo.getEp());
                }
                String claveEp = adecua.obtenClaveSicop(resp, ac.getMovimiento(i));
                String[] splitSiaff = objSaldo.getEp().split("\\.");
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
                writableSheet.addCell(new jxl.write.Label(0, i + 1, splitClaveEp[0].trim()));
                writableSheet.addCell(new jxl.write.Label(1, i + 1, splitClaveEp[1].trim()));
                writableSheet.addCell(new jxl.write.Label(2, i + 1, splitClaveEp[2].trim()));
                writableSheet.addCell(new jxl.write.Label(3, i + 1, splitClaveEp[3].trim()));
                writableSheet.addCell(new jxl.write.Label(4, i + 1, splitSiaff[1].trim()));
                writableSheet.addCell(new jxl.write.Label(5, i + 1, splitSiaff[2].trim()));
                writableSheet.addCell(new jxl.write.Label(6, i + 1, splitSiaff[0].trim()));
                writableSheet.addCell(new jxl.write.Label(7, i + 1, splitSiaff[3].trim()));
                writableSheet.addCell(new jxl.write.Label(8, i + 1, splitSiaff[4].trim()));
                writableSheet.addCell(new jxl.write.Label(9, i + 1, splitSiaff[5].trim()));
                writableSheet.addCell(new jxl.write.Label(10, i + 1, splitSiaff[6].trim()));
                writableSheet.addCell(new jxl.write.Label(11, i + 1, splitSiaff[7].trim()));
                writableSheet.addCell(new jxl.write.Label(12, i + 1, splitSiaff[8].trim()));
                writableSheet.addCell(new jxl.write.Label(13, i + 1, splitSiaff[9].substring(0, 1).trim()));
                writableSheet.addCell(new jxl.write.Label(14, i + 1, splitSiaff[9].substring(1, 2).trim()));
                writableSheet.addCell(new jxl.write.Label(15, i + 1, splitSiaff[9].substring(2, 3).trim()));
                writableSheet.addCell(new jxl.write.Label(16, i + 1, splitSiaff[9].substring(3, 5).trim()));
                writableSheet.addCell(new jxl.write.Label(17, i + 1, splitSiaff[10].trim()));
                writableSheet.addCell(new jxl.write.Label(18, i + 1, splitSiaff[11].trim()));
                writableSheet.addCell(new jxl.write.Label(19, i + 1, splitSiaff[12].trim()));
                writableSheet.addCell(new jxl.write.Label(20, i + 1, splitSiaff[13].trim()));
                String clvInterna1 = splitSiaff[14].trim();
                while (clvInterna1.length() < 10) {
                    clvInterna1 = "0" + clvInterna1.trim();
                }
                archivo += "," + clvInterna1.trim();
                String clvInterna2 = splitSiaff[15].trim();
                archivo += "," + clvInterna2.substring(1, 3).trim();
                archivo += ",000";
                archivo += ",000";
                archivo += ",00000";
                archivo += ",00000";
                archivo += ",0000000000";
                if (Double.parseDouble(objSaldo.getMontoAnual()) > 0)
                    archivo += "," + String.valueOf(formateadorDecimal.format(Double.parseDouble(objSaldo.getMontoAnual().trim())));
                else
                    archivo += "," + String.valueOf(formateadorCero.format(Double.parseDouble(objSaldo.getMontoAnual().trim())));
                archivo += ",1";
                if (Double.parseDouble(objSaldo.getMontoEnero()) > 0)
                    archivo += "," + String.valueOf(formateadorDecimal.format(Double.parseDouble(objSaldo.getMontoEnero().trim())));
                else
                    archivo += "," + String.valueOf(formateadorCero.format(Double.parseDouble(objSaldo.getMontoEnero().trim())));
                if (Double.parseDouble(objSaldo.getMontoFebrero()) > 0)
                    archivo += "," + String.valueOf(formateadorDecimal.format(Double.parseDouble(objSaldo.getMontoFebrero().trim())));
                else
                    archivo += "," + String.valueOf(formateadorCero.format(Double.parseDouble(objSaldo.getMontoFebrero().trim())));
                if (Double.parseDouble(objSaldo.getMontoMarzo()) > 0)
                    archivo += "," + String.valueOf(formateadorDecimal.format(Double.parseDouble(objSaldo.getMontoMarzo().trim())));
                else
                    archivo += "," + String.valueOf(formateadorCero.format(Double.parseDouble(objSaldo.getMontoMarzo().trim())));
                if (Double.parseDouble(objSaldo.getMontoAbril()) > 0)
                    archivo += "," + String.valueOf(formateadorDecimal.format(Double.parseDouble(objSaldo.getMontoAbril().trim())));
                else
                    archivo += "," + String.valueOf(formateadorCero.format(Double.parseDouble(objSaldo.getMontoAbril().trim())));
                if (Double.parseDouble(objSaldo.getMontoMayo()) > 0)
                    archivo += "," + String.valueOf(formateadorDecimal.format(Double.parseDouble(objSaldo.getMontoMayo().trim())));
                else
                    archivo += "," + String.valueOf(formateadorCero.format(Double.parseDouble(objSaldo.getMontoMayo().trim())));
                if (Double.parseDouble(objSaldo.getMontoJunio()) > 0)
                    archivo += "," + String.valueOf(formateadorDecimal.format(Double.parseDouble(objSaldo.getMontoJunio().trim())));
                else
                    archivo += "," + String.valueOf(formateadorCero.format(Double.parseDouble(objSaldo.getMontoJunio().trim())));
                if (Double.parseDouble(objSaldo.getMontoJulio()) > 0)
                    archivo += "," + String.valueOf(formateadorDecimal.format(Double.parseDouble(objSaldo.getMontoJulio().trim())));
                else
                    archivo += "," + String.valueOf(formateadorCero.format(Double.parseDouble(objSaldo.getMontoJulio().trim())));
                if (Double.parseDouble(objSaldo.getMontoAgosto()) > 0)
                    archivo += "," + String.valueOf(formateadorDecimal.format(Double.parseDouble(objSaldo.getMontoAgosto().trim())));
                else
                    archivo += "," + String.valueOf(formateadorCero.format(Double.parseDouble(objSaldo.getMontoAgosto().trim())));
                if (Double.parseDouble(objSaldo.getMontoSeptiembre()) > 0)
                    archivo += "," + String.valueOf(formateadorDecimal.format(Double.parseDouble(objSaldo.getMontoSeptiembre().trim())));
                else
                    archivo += "," + String.valueOf(formateadorCero.format(Double.parseDouble(objSaldo.getMontoSeptiembre().trim())));
                if (Double.parseDouble(objSaldo.getMontoOctubre()) > 0)
                    archivo += "," + String.valueOf(formateadorDecimal.format(Double.parseDouble(objSaldo.getMontoOctubre().trim())));
                else
                    archivo += "," + String.valueOf(formateadorCero.format(Double.parseDouble(objSaldo.getMontoOctubre().trim())));
                if (Double.parseDouble(objSaldo.getMontoNoviembre()) > 0)
                    archivo += "," + String.valueOf(formateadorDecimal.format(Double.parseDouble(objSaldo.getMontoNoviembre().trim())));
                else
                    archivo += "," + String.valueOf(formateadorCero.format(Double.parseDouble(objSaldo.getMontoNoviembre().trim())));
                if (Double.parseDouble(objSaldo.getMontoDiciembre()) > 0)
                    archivo += "," + String.valueOf(formateadorDecimal.format(Double.parseDouble(objSaldo.getMontoDiciembre().trim())));
                else
                    archivo += "," + String.valueOf(formateadorCero.format(Double.parseDouble(objSaldo.getMontoDiciembre().trim())));
                /*archivo+=","+objSaldo.getMontoEnero().trim();
				archivo+=","+objSaldo.getMontoFebrero().trim();
				archivo+=","+objSaldo.getMontoMarzo().trim();
				archivo+=","+objSaldo.getMontoAbril().trim();
				archivo+=","+objSaldo.getMontoMayo().trim();
				archivo+=","+objSaldo.getMontoJunio().trim();
				archivo+=","+objSaldo.getMontoJulio().trim();
				archivo+=","+objSaldo.getMontoAgosto().trim();
				archivo+=","+objSaldo.getMontoSeptiembre().trim();
				archivo+=","+objSaldo.getMontoOctubre().trim();
				archivo+=","+objSaldo.getMontoNoviembre().trim();
				archivo+=","+objSaldo.getMontoDiciembre().trim();*/
                writableSheet.addCell(new jxl.write.Label(22, i + 1, clvInterna2.substring(1, 3).trim()));
                writableSheet.addCell(new jxl.write.Label(21, i + 1, clvInterna1.trim()));
                writableSheet.addCell(new jxl.write.Label(23, i + 1, "000".trim()));
                writableSheet.addCell(new jxl.write.Label(24, i + 1, "000".trim()));
                writableSheet.addCell(new jxl.write.Label(25, i + 1, "00000".trim()));
                writableSheet.addCell(new jxl.write.Label(26, i + 1, "00000".trim()));
                writableSheet.addCell(new jxl.write.Label(27, i + 1, "0000000000".trim()));
                //writableSheet.addCell(new jxl.write.Number(28,i+1,new Double(objSaldo.getMontoAnual().trim())));
                if (Double.parseDouble(objSaldo.getMontoAnual()) > 0)
                    writableSheet.addCell(new jxl.write.Label(28, i + 1, String.valueOf(formateadorDecimal.format(Double.parseDouble(objSaldo.getMontoAnual().trim())))));
                else
                    writableSheet.addCell(new jxl.write.Label(28, i + 1, String.valueOf(formateadorCero.format(Double.parseDouble(objSaldo.getMontoAnual().trim())))));
                writableSheet.addCell(new jxl.write.Label(29, i + 1, "1".trim()));
                if (Double.parseDouble(objSaldo.getMontoEnero()) > 0)
                    writableSheet.addCell(new jxl.write.Label(30, i + 1, String.valueOf(formateadorDecimal.format(Double.parseDouble(objSaldo.getMontoEnero().trim())))));
                else
                    writableSheet.addCell(new jxl.write.Label(30, i + 1, String.valueOf(formateadorCero.format(Double.parseDouble(objSaldo.getMontoEnero().trim())))));
                if (Double.parseDouble(objSaldo.getMontoFebrero()) > 0)
                    writableSheet.addCell(new jxl.write.Label(31, i + 1, String.valueOf(formateadorDecimal.format(Double.parseDouble(objSaldo.getMontoFebrero().trim())))));
                else
                    writableSheet.addCell(new jxl.write.Label(31, i + 1, String.valueOf(formateadorCero.format(Double.parseDouble(objSaldo.getMontoFebrero().trim())))));
                if (Double.parseDouble(objSaldo.getMontoMarzo()) > 0)
                    writableSheet.addCell(new jxl.write.Label(32, i + 1, String.valueOf(formateadorDecimal.format(Double.parseDouble(objSaldo.getMontoMarzo().trim())))));
                else
                    writableSheet.addCell(new jxl.write.Label(32, i + 1, String.valueOf(formateadorCero.format(Double.parseDouble(objSaldo.getMontoMarzo().trim())))));
                if (Double.parseDouble(objSaldo.getMontoAbril()) > 0)
                    writableSheet.addCell(new jxl.write.Label(33, i + 1, String.valueOf(formateadorDecimal.format(Double.parseDouble(objSaldo.getMontoAbril().trim())))));
                else
                    writableSheet.addCell(new jxl.write.Label(33, i + 1, String.valueOf(formateadorCero.format(Double.parseDouble(objSaldo.getMontoAbril().trim())))));
                if (Double.parseDouble(objSaldo.getMontoMayo()) > 0)
                    writableSheet.addCell(new jxl.write.Label(34, i + 1, String.valueOf(formateadorDecimal.format(Double.parseDouble(objSaldo.getMontoMayo().trim())))));
                else
                    writableSheet.addCell(new jxl.write.Label(34, i + 1, String.valueOf(formateadorCero.format(Double.parseDouble(objSaldo.getMontoMayo().trim())))));
                if (Double.parseDouble(objSaldo.getMontoJunio()) > 0)
                    writableSheet.addCell(new jxl.write.Label(35, i + 1, String.valueOf(formateadorDecimal.format(Double.parseDouble(objSaldo.getMontoJunio().trim())))));
                else
                    writableSheet.addCell(new jxl.write.Label(35, i + 1, String.valueOf(formateadorCero.format(Double.parseDouble(objSaldo.getMontoJunio().trim())))));
                if (Double.parseDouble(objSaldo.getMontoJulio()) > 0)
                    writableSheet.addCell(new jxl.write.Label(36, i + 1, String.valueOf(formateadorDecimal.format(Double.parseDouble(objSaldo.getMontoJulio().trim())))));
                else
                    writableSheet.addCell(new jxl.write.Label(36, i + 1, String.valueOf(formateadorCero.format(Double.parseDouble(objSaldo.getMontoJulio().trim())))));
                if (Double.parseDouble(objSaldo.getMontoAgosto()) > 0)
                    writableSheet.addCell(new jxl.write.Label(37, i + 1, String.valueOf(formateadorDecimal.format(Double.parseDouble(objSaldo.getMontoAgosto().trim())))));
                else
                    writableSheet.addCell(new jxl.write.Label(37, i + 1, String.valueOf(formateadorCero.format(Double.parseDouble(objSaldo.getMontoAgosto().trim())))));
                if (Double.parseDouble(objSaldo.getMontoSeptiembre()) > 0)
                    writableSheet.addCell(new jxl.write.Label(38, i + 1, String.valueOf(formateadorDecimal.format(Double.parseDouble(objSaldo.getMontoSeptiembre().trim())))));
                else
                    writableSheet.addCell(new jxl.write.Label(38, i + 1, String.valueOf(formateadorCero.format(Double.parseDouble(objSaldo.getMontoSeptiembre().trim())))));
                if (Double.parseDouble(objSaldo.getMontoOctubre()) > 0)
                    writableSheet.addCell(new jxl.write.Label(39, i + 1, String.valueOf(formateadorDecimal.format(Double.parseDouble(objSaldo.getMontoOctubre().trim())))));
                else
                    writableSheet.addCell(new jxl.write.Label(39, i + 1, String.valueOf(formateadorCero.format(Double.parseDouble(objSaldo.getMontoOctubre().trim())))));
                if (Double.parseDouble(objSaldo.getMontoNoviembre()) > 0)
                    writableSheet.addCell(new jxl.write.Label(40, i + 1, String.valueOf(formateadorDecimal.format(Double.parseDouble(objSaldo.getMontoNoviembre().trim())))));
                else
                    writableSheet.addCell(new jxl.write.Label(40, i + 1, String.valueOf(formateadorCero.format(Double.parseDouble(objSaldo.getMontoNoviembre().trim())))));
                if (Double.parseDouble(objSaldo.getMontoDiciembre()) > 0)
                    writableSheet.addCell(new jxl.write.Label(41, i + 1, String.valueOf(formateadorDecimal.format(Double.parseDouble(objSaldo.getMontoDiciembre().trim())))));
                else
                    writableSheet.addCell(new jxl.write.Label(41, i + 1, String.valueOf(formateadorCero.format(Double.parseDouble(objSaldo.getMontoDiciembre().trim())))));
            }
            //bw.write(archivo);
            //bw.flush();
            //bw.close();
            writableWorkbook.write();
            writableWorkbook.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public synchronized void creaRepIntegraFIAFExcel(HttpServletRequest request, HttpServletResponse response) throws Exception {
        AdecuacionBusinessLogic adecua = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
        HttpSession session = request.getSession(false);
        ArrayList arrIntegradas = new ArrayList();
        DiskFileItemFactory factory = DiskFileItemFactory.builder().setBufferSize(1024).get();
        factory.setRepository(new File(tempDir));
        JakartaServletFileUpload upload = new JakartaServletFileUpload(factory);
        int nConsecutivoSICOP = 0;
        Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
        int nFolio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
        Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        if (usuario == null) {
            response.sendRedirect("../index.jsp");
            return;
        }
        Date fecha_crea = new Timestamp(System.currentTimeMillis());
        Date date = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fecha = sdf.format(fecha_crea);
        arrIntegradas = adecua.consultaIntegradasFIAFExcel(nFolio);
        ArrayList arrAdecuacion = (ArrayList) arrIntegradas.get(0);
        ArrayList arrAdecuacionR = (ArrayList) arrAdecuacion.get(0);
        int nrenglones = arrAdecuacion.size();
        String file_name = "Rep-Integracion_" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        response.setContentType("application/vnd.ms-excel");
        response.addHeader("Content-Disposition", "inline; filename=\"" + file_name + "\"; ");
        String cFileExcelPlantilla = getServletContext().getRealPath("Reportes" + File.separator + "plantilla_integra_adecuaFIAF.xls");
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(file_name);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        Sheet sheet = workbook.getSheetAt(0);
        CellStyle styleBorder = workbook.createCellStyle();
        styleBorder.setBorderBottom(BorderStyle.MEDIUM);
        styleBorder.setBorderLeft(BorderStyle.MEDIUM);
        styleBorder.setBorderRight(BorderStyle.MEDIUM);
        styleBorder.setBorderTop(BorderStyle.MEDIUM);
        Iterator<Row> rowIterator = sheet.iterator();
        int nRenglon = 0;
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (nRenglon == 5) {
                Cell fechac = row.getCell(1);
                fechac.setCellValue(fecha);
            }
            int count = 1;
            if (nRenglon == 9) {
                for (int j = 0; j < nrenglones; j++) {
                    ArrayList arrAdecuacionD = (ArrayList) arrAdecuacion.get(j);
                    Row r = sheet.createRow(9 + count);
                    for (int i = 0; i <= 4; i++) {
                        Cell ue = r.createCell(i);
                        ue.setCellValue((String) arrAdecuacionD.get(i));
                        if (i == 4) {
                            CellRangeAddress cra = new CellRangeAddress(9 + count, 9 + count, i, i + 6);
                            sheet.addMergedRegion(cra);
                        }
                    }
                    count++;
                }
                Row rSubPie = sheet.createRow(nRenglon + nrenglones + 2);
                Cell cellrPieTitle1 = rSubPie.createCell(0);
                Cell cellrPieTitle2 = rSubPie.createCell(1);
                Cell cellrPieTitle3 = rSubPie.createCell(2);
                Cell cellrPieTitle4 = rSubPie.createCell(3);
                Cell cellrPieTitle5 = rSubPie.createCell(4);
                Cell cellrPieTitle6 = rSubPie.createCell(5);
                Cell cellrPieTitle7 = rSubPie.createCell(6);
                cellrPieTitle1.setCellStyle(styleBorder);
                cellrPieTitle2.setCellStyle(styleBorder);
                cellrPieTitle3.setCellStyle(styleBorder);
                cellrPieTitle4.setCellStyle(styleBorder);
                cellrPieTitle5.setCellStyle(styleBorder);
                cellrPieTitle6.setCellStyle(styleBorder);
                cellrPieTitle7.setCellStyle(styleBorder);
                cellrPieTitle1.setCellValue("No. FIAF");
                cellrPieTitle2.setCellValue("NIVEL");
                cellrPieTitle3.setCellValue("TIPO");
                cellrPieTitle4.setCellValue("No. AFECTACIONES");
                cellrPieTitle5.setCellValue("AMPLIACIONES");
                cellrPieTitle6.setCellValue("REDUCCIONES");
                cellrPieTitle7.setCellValue("DIFERENCIA");
                Row r = sheet.getRow(nRenglon + nrenglones + 3);
                if (r == null)
                    r = sheet.createRow(nRenglon + nrenglones + 3);
                for (int i = 0; i <= 6; i++) {
                    Cell ci = r.getCell(i);
                    if (ci == null)
                        ci = r.createCell(i);
                    ci.setCellStyle(styleBorder);
                    ci.setCellValue((String) arrAdecuacionR.get(i + 5));
                }
                break;
            }
            nRenglon++;
        }
        fsArchivo.close();
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos);
        /* Cierra Flujos */
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

    public void creaRepIntegraFIAFPdf(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
        String reportPath = getServletContext().getRealPath("Reportes" + File.separator + "integraAdecuaFIAF.jasper");
        reportesBussinesObject objReporte = new reportesBussinesObject(jniName);
        objReporte.reporteIntegraAdecuaFIAFPdf(request, response, reportPath);
    }

    public void creaFap02(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AdecuacionBusinessLogic adecua = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
        HttpSession session = request.getSession(false);
        ArrayList arrAdecAmpliacion = new ArrayList();
        ArrayList arrAdecReduccion = new ArrayList();
        String[] arrFirmantePuesto = new String[2];
        DiskFileItemFactory factory = DiskFileItemFactory.builder().setBufferSize(1024).get();
        factory.setRepository(new File(tempDir));
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
        try {
            arrAdecAmpliacion = adecua.consultaFap02AmpliaFIAFExcel(nFolio);
            arrAdecReduccion = adecua.consultaFap02ReduceFIAFExcel(nFolio);
            arrFirmantePuesto = adecua.consultaFirmanteFIAFPuesto(ur, nFolio);
        } catch (Exception e) {
            throw new ServletException(e);
        }
        float suma = 0;
        String folioCompleto = c.getFolio();
        String unidadEjecutora = new String(c.getFolio().substring(5, 8));
        ArrayList arrAdecuacionReduccion = (arrAdecReduccion.size() == 0 ? null : (ArrayList) arrAdecReduccion.get(0));
        ArrayList arrAdecuacionResumenReduccion = (arrAdecuacionReduccion.size() == 0 ? null : (ArrayList) arrAdecuacionReduccion.get(0));
        ArrayList arrAdecuacionAmpliacion = (arrAdecAmpliacion == null ? null : (ArrayList) arrAdecAmpliacion.get(0));
        ArrayList arrAdecuacionAmpliacionResumen = (arrAdecuacionAmpliacion == null ? null : (ArrayList) arrAdecuacionAmpliacion.get(0));
        String file_name = "Rep-FAP02" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        response.setContentType("application/vnd.ms-excel");
        response.addHeader("Content-Disposition", "inline; filename=\"" + file_name + "\"; ");
        String cFileExcelPlantilla = getServletContext().getRealPath("Reportes" + File.separator + "plantilla_FAP_02.xls");
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
        Workbook workbook = new HSSFWorkbook(fsArchivo);
        CellStyle styleBorRight = workbook.createCellStyle();
        CellStyle styleBorLeft = workbook.createCellStyle();
        CellStyle styleTexto = workbook.createCellStyle();
        CellStyle styleTextoBorde = workbook.createCellStyle();
        CellStyle numberStyleTop = workbook.createCellStyle();
        CellStyle numberStyle = workbook.createCellStyle();
        DataFormat df = workbook.createDataFormat();
        numberStyleTop.setDataFormat(df.getFormat("#,###,##0.00"));
        numberStyleTop.setBorderTop(BorderStyle.THIN);
        styleBorRight.setBorderRight(BorderStyle.THIN);
        styleBorRight.setAlignment(HorizontalAlignment.RIGHT);
        styleBorLeft.setBorderLeft(BorderStyle.THIN);
        numberStyle.setDataFormat(df.getFormat("#,###,##0.00"));
        styleTexto.setAlignment(HorizontalAlignment.CENTER);
        styleTextoBorde.setAlignment(HorizontalAlignment.CENTER);
        styleTextoBorde.setBorderBottom(BorderStyle.THIN);
        for (int k = 0; k < 2; k++) {
            ArrayList<?> arrInfoEnca = (k == 0 ? arrAdecuacionResumenReduccion : arrAdecuacionAmpliacionResumen);
            ArrayList<?> arrInfoDeta = (k == 0 ? arrAdecuacionReduccion : arrAdecuacionAmpliacion);
            if (arrInfoEnca == null)
                continue;
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
                if (nRenglon == 5) {
                    Cell efiscal = row.getCell(4);
                    efiscal.setCellValue(fiscal);
                }
                if (nRenglon == 8) {
                    Cell ue = row.getCell(1);
                    ue.setCellValue(unidadEjecutora);
                    Cell foliosai = row.getCell(5);
                    foliosai.setCellValue(folioCompleto);
                    Cell fechac = row.getCell(6);
                    fechac.setCellValue(fecha);
                }
                if (nRenglon == 11) {
                    Cell justifN = row.getCell(2);
                    justifN.setCellValue((String) arrInfoEnca.get(5));
                }
                if (nRenglon == 16) {
                    Cell justifAR = row.getCell(2);
                    justifAR.setCellValue((String) arrInfoEnca.get(k == 0 ? 4 : 3));
                }
                int renglon = 0;
                int count = 0;
                if (nRenglon == 21) {
                    for (int j = 0; j < nrenglones; j++) {
                        ArrayList arrAdecuacionD = (ArrayList) arrInfoDeta.get(j);
                        Row r = sheet0.getRow(21 + count);
                        if (r == null) {
                            r = sheet0.createRow(21 + count);
                        }
                        for (int i = 0; i <= 2; i++) {
                            Cell ue = r.createCell(i);
                            if (i == 1) {
                                ue.setCellStyle(styleBorRight);
                                ue.setCellValue(Double.parseDouble((String) arrAdecuacionD.get(i + 1).toString()));
                                ue.setCellStyle(numberStyle);
                            } else {
                                ue.setCellStyle(styleBorLeft);
                                ue.setCellValue((String) arrAdecuacionD.get(i + 1));
                            }
                            if (i == 2) {
                                suma += Double.parseDouble((String) arrAdecuacionD.get(6).toString());
                            }
                        }
                        count++;
                    }
                }
                if (nRenglon == 45) {
                    Cell sumat = row.getCell(1);
                    sumat.setCellValue(suma);
                    sumat.setCellStyle(numberStyleTop);
                }
                if (nRenglon == 49) {
                    Cell puesto = row.getCell(0);
                    puesto.setCellValue(arrFirmantePuesto[1]);
                    puesto.setCellStyle(styleTexto);
                }
                if (nRenglon == 54) {
                    Cell nombre = row.getCell(0);
                    nombre.setCellValue(arrFirmantePuesto[0]);
                    nombre.setCellStyle(styleTextoBorde);
                }
                nRenglon++;
            }
        }
        fsArchivo.close();
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos);
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

    public void creaFap(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
        AdecuacionBusinessLogic adecua = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
        HttpSession session = request.getSession(false);
        DiskFileItemFactory factory = DiskFileItemFactory.builder().setBufferSize(1024).get();
        factory.setRepository(new File(tempDir));
        JakartaServletFileUpload upload = new JakartaServletFileUpload(factory);
        Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
        Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        if (usuario == null) {
            response.sendRedirect("../index.jsp");
            return;
        }
        int folio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
        String responsableCompleto = request.getParameter("responsable");
        String responsable = "Responsable del Area";
        String area = "Area";
        int nConsecutivoSICOP = adecua.consecutivoSICOPFIAF(folio);
        if (responsableCompleto != null && !responsable.equals("")) {
            responsable = responsableCompleto.split("><")[0];
            area = responsableCompleto.split("><")[1];
        }
        String file_name = "" + nConsecutivoSICOP;
        response.setContentType("application/vnd.ms-excel");
        response.addHeader("Content-Disposition", "inline; filename=\"FAP-01" + file_name + ".xls\"; charset=UTF-8\" pageEncoding=\"utf-8\"");
        // Para escribir en el archivo la fecha de tipo dd/mm/yyyy
        Date date = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fecha = sdf.format(date);
        String cFileExcel = upload.getRepositoryPath() + "/plantillaFAP01.xls";
        InputStream inp = new FileInputStream(cFileExcel);
        AdecuacionCalendario ac = adecua.adecuacionCalendarioFIAF(folio);
        Workbook wb = new HSSFWorkbook(inp);
        Sheet sheet = wb.getSheetAt(0);
        Sheet metas = wb.getSheetAt(1);
        String justificacion = ac.getJustificacion();
        // Justificacion que se inserta en la segunda pestaña (hoja) del excel
        Row rjustificacion = metas.getRow(56);
        Cell cjustificacion = rjustificacion.getCell(1);
        cjustificacion.setCellValue(justificacion);
        // Empieza la lógica para meter los datos en la primera pestaña (hoja)
        // del excel, el formato FAP01
        // Inserta la fecha de hoy en la hoja del FAP01 dentro de excel
        Row rfechafap = sheet.getRow(7);
        Cell cfechafap = rfechafap.getCell(30);
        cfechafap.setCellValue(fecha);
        // Inserta el folio SAI dentro de la primera hoja (FAP01) dentro de
        // excel
        Row filaFolioSai = sheet.getRow(7);
        Cell celdaFolioSai = filaFolioSai.getCell(29);
        celdaFolioSai.setCellValue("SAI:" + c.getFolio());
        ArrayList<Saldo> saldoR = new ArrayList<Saldo>();
        ArrayList<Saldo> saldoA = new ArrayList<Saldo>();
        double[] sumaR = new double[13];
        double[] sumaA = new double[13];
        int j = 0;
        while (j < ac.saldoLength()) {
            Saldo objSaldo = ac.getSaldo(j);
            if ("R".equals(ac.getMovimiento(j))) {
                saldoR.add(objSaldo);
            } else if ("A".equals(ac.getMovimiento(j))) {
                saldoA.add(objSaldo);
            }
            j++;
        }
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
        int numfilas = imprimeSaldos(sheet, saldoR, sumaR, 15, 'R', 0, estiloBordeDerecho);
        imprimeSumas(sheet, numfilas + 5, sumaR, estiloBordeDerecho);
        insertaFila(wb, sheet, numfilas += 9, saldoA.size());
        numfilas = imprimeSaldos(sheet, saldoA, sumaA, numfilas, 'A', numfilas - 24, estiloBordeDerecho);
        imprimeSumas(sheet, numfilas + 3, sumaA, estiloBordeDerecho);
        imprimeResponsable(sheet, numfilas + 8, responsable, area);
        wb.write(response.getOutputStream());
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
            cell = row.getCell(i + 18);
            cell.setCellValue(suma[i]);
        }
        cell = row.getCell(30);
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
        Row row = sheet.getRow(fila);
        Cell cell;
        cell = row.getCell(18);
        CellStyle estilo = cell.getCellStyle();
        cell.setCellValue(responsable);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(fila + 1, fila + 1, 18, 21));
        row = sheet.getRow(fila + 1);
        cell = row.getCell(18);
        cell.setCellStyle(estilo);
        cell.setCellValue(area);
    }
}
