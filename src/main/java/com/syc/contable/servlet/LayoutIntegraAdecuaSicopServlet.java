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
import org.apache.poi.ss.util.CellRangeAddress;
import com.syc.contable.AdecuacionBusinessLogic;
import com.syc.contable.adecuaciones.core.Fap01;
import com.syc.contable.core.AdecuacionCalendario;
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

@WebServlet(name = "LayoutIntegraAdecuaSicopServlet", urlPatterns = { "/gstnmngr/IntegraAdecuaLayoutSicop" })
public class LayoutIntegraAdecuaSicopServlet extends HttpServlet {

    /**
     * Constructor of the object.
     */
    public LayoutIntegraAdecuaSicopServlet() {
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
        try {
            if (request.getParameter("exporta").equals("1")) {
                response.sendRedirect("../plantillasCasos/layOutIntegraAdecuacion.jsp");
                return;
            }
            if (request.getParameter("exporta").equals("2")) {
                creaSicop(request, response);
            }
            if (request.getParameter("exporta").equals("3")) {
                creaFap(request, response);
            }
            if (request.getParameter("exporta").equals("4")) {
                // creaRepIntegra(request, response);
                creaRepIntegraExcel(request, response);
            }
            if (request.getParameter("exporta").equals("5")) {
                creaRepIntegraPdf(request, response);
            }
            if (request.getParameter("exporta").equals("6")) {
                creaFap02IADE(request, response);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
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

    public void creaSicop(HttpServletRequest request, HttpServletResponse response) throws Exception {
        AdecuacionBusinessLogic adecua = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
        HttpSession session = request.getSession(false);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(response.getOutputStream()));
        DecimalFormat formateadorDecimal = new DecimalFormat("###########.00");
        DecimalFormat formateadorCero = new DecimalFormat("#");
        Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
        Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        if (usuario == null) {
            response.sendRedirect("../index.jsp");
            return;
        }
        ArrayList arrAdecuacion = null;
        int nFolio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
        try {
            arrAdecuacion = adecua.buscaIntegracionAdecCveCorta(c, nFolio, "", usuario.getLogin(), "", usuario.getU_Ramo(), "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String variable = (String) arrAdecuacion.get(arrAdecuacion.size() - 2);
        int nfolioSicop = 0;
        String folioSicop = "1";
        try {
            nFolio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
            /*
			 * si no tiene sicop voy a aumentar, por eso niego tieneSicop
			 */
            nfolioSicop = adecua.buscaConsecutivoSICOP(nFolio);
            if (nfolioSicop == 0) {
                adecua.actualizaFolioSicopEncabezadoInt("0", nFolio);
                nfolioSicop = adecua.buscaConsecutivoSICOP(nFolio);
                folioSicop = "" + nfolioSicop;
                if (session.getAttribute("objConsecutivoSICOP") != null)
                    session.removeAttribute("objConsecutivoSICOP");
                session.setAttribute("objConsecutivoSICOP", nfolioSicop);
            } else {
                nfolioSicop = adecua.buscaConsecutivoSICOP(nFolio);
                folioSicop = "" + nfolioSicop;
                if (session.getAttribute("objConsecutivoSICOP") != null)
                    session.removeAttribute("objConsecutivoSICOP");
                session.setAttribute("objConsecutivoSICOP", nfolioSicop);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        while (folioSicop.length() < 5) {
            folioSicop = "0" + folioSicop.trim();
        }
        //String file_name = c.getFolio();
        response.setContentType("application/vnd.ms-excel");
        response.addHeader("Content-Disposition", "inline; filename=\"LAYOUT-SICOP" + folioSicop + ".xls\";");
        // Para escribir en el archivo la fecha de tipo dd/mm/yyyy
        Date date = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fecha = sdf.format(date);
        WritableWorkbook writableWorkbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        WritableSheet writableSheet = writableWorkbook.createSheet("Afecta 3", 0);
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
        writableSheet.addCell(new jxl.write.Label(13, 0, variable.trim() + "_0" + folioSicop.trim()));
        writableSheet.addCell(new jxl.write.Label(14, 0, variable.trim() + "_0" + folioSicop.trim()));
        writableSheet.addCell(new jxl.write.Label(15, 0, folioSicop.trim()));
        boolean resp = false;
        try {
            int terminaAdecNormales = 0;
            for (int i = 7; i < arrAdecuacion.size() - 5; i++) {
                Saldo objSaldo = (Saldo) arrAdecuacion.get(i);
                String ep = objSaldo.getClaveSIAFF().trim() + "." + objSaldo.getClaveInterna().trim();
                if (objSaldo.getEp().equals("A")) {
                    resp = adecua.esModificado(objSaldo.getClaveSIAFF().trim());
                }
                String claveEp = adecua.obtenClaveSicop(resp, objSaldo.getEp());
                String[] splitSiaff = ep.split("\\.");
                String[] splitClaveEp = claveEp.split("\\.");
                writableSheet.addCell(new jxl.write.Label(0, i - 6, splitClaveEp[0].trim()));
                writableSheet.addCell(new jxl.write.Label(1, i - 6, splitClaveEp[1].trim()));
                writableSheet.addCell(new jxl.write.Label(2, i - 6, splitClaveEp[2].trim()));
                writableSheet.addCell(new jxl.write.Label(3, i - 6, splitClaveEp[3].trim()));
                writableSheet.addCell(new jxl.write.Label(4, i - 6, splitSiaff[1].trim()));
                writableSheet.addCell(new jxl.write.Label(5, i - 6, splitSiaff[2].trim()));
                writableSheet.addCell(new jxl.write.Label(6, i - 6, splitSiaff[0].trim()));
                writableSheet.addCell(new jxl.write.Label(7, i - 6, splitSiaff[3].trim()));
                writableSheet.addCell(new jxl.write.Label(8, i - 6, splitSiaff[4].trim()));
                writableSheet.addCell(new jxl.write.Label(9, i - 6, splitSiaff[5].trim()));
                writableSheet.addCell(new jxl.write.Label(10, i - 6, splitSiaff[6].trim()));
                writableSheet.addCell(new jxl.write.Label(11, i - 6, splitSiaff[7].trim()));
                writableSheet.addCell(new jxl.write.Label(12, i - 6, splitSiaff[8].trim()));
                writableSheet.addCell(new jxl.write.Label(13, i - 6, splitSiaff[9].substring(0, 1).trim()));
                writableSheet.addCell(new jxl.write.Label(14, i - 6, splitSiaff[9].substring(1, 2).trim()));
                writableSheet.addCell(new jxl.write.Label(15, i - 6, splitSiaff[9].substring(2, 3).trim()));
                writableSheet.addCell(new jxl.write.Label(16, i - 6, splitSiaff[9].substring(3, 5).trim()));
                writableSheet.addCell(new jxl.write.Label(17, i - 6, splitSiaff[10].trim()));
                writableSheet.addCell(new jxl.write.Label(18, i - 6, splitSiaff[11].trim()));
                writableSheet.addCell(new jxl.write.Label(19, i - 6, splitSiaff[12].trim()));
                writableSheet.addCell(new jxl.write.Label(20, i - 6, splitSiaff[13].trim()));
                String clvInterna1 = "";
                while (clvInterna1.length() < 10) {
                    clvInterna1 = "0" + clvInterna1.trim();
                }
                writableSheet.addCell(new jxl.write.Label(22, i - 6, "00"));
                writableSheet.addCell(new jxl.write.Label(21, i - 6, clvInterna1.trim()));
                writableSheet.addCell(new jxl.write.Label(23, i - 6, "000".trim()));
                writableSheet.addCell(new jxl.write.Label(24, i - 6, "000".trim()));
                //				writableSheet.addCell(new jxl.write.Label(25, i - 6, objSaldo.getProyecto()));
                writableSheet.addCell(new jxl.write.Label(25, i - 6, "00000"));
                writableSheet.addCell(new jxl.write.Label(26, i - 6, "00000".trim()));
                writableSheet.addCell(new jxl.write.Label(27, i - 6, "0000000000".trim()));
                writableSheet.addCell(new jxl.write.Label(28, i - 6, formateadorDecimal.format(new Double(objSaldo.getMontoAnual().trim()))));
                writableSheet.addCell(new jxl.write.Label(29, i - 6, "1".trim()));
                writableSheet.addCell(new jxl.write.Label(30, i - 6, formateadorDecimal.format(new Double(objSaldo.getMontoEnero().trim()))));
                writableSheet.addCell(new jxl.write.Label(31, i - 6, formateadorDecimal.format(new Double(objSaldo.getMontoFebrero().trim()))));
                writableSheet.addCell(new jxl.write.Label(32, i - 6, formateadorDecimal.format(new Double(objSaldo.getMontoMarzo().trim()))));
                writableSheet.addCell(new jxl.write.Label(33, i - 6, formateadorDecimal.format(new Double(objSaldo.getMontoAbril().trim()))));
                writableSheet.addCell(new jxl.write.Label(34, i - 6, formateadorDecimal.format(new Double(objSaldo.getMontoMayo().trim()))));
                writableSheet.addCell(new jxl.write.Label(35, i - 6, formateadorDecimal.format(new Double(objSaldo.getMontoJunio().trim()))));
                writableSheet.addCell(new jxl.write.Label(36, i - 6, formateadorDecimal.format(new Double(objSaldo.getMontoJulio().trim()))));
                writableSheet.addCell(new jxl.write.Label(37, i - 6, formateadorDecimal.format(new Double(objSaldo.getMontoAgosto().trim()))));
                writableSheet.addCell(new jxl.write.Label(38, i - 6, formateadorDecimal.format(new Double(objSaldo.getMontoSeptiembre().trim()))));
                writableSheet.addCell(new jxl.write.Label(39, i - 6, formateadorDecimal.format(new Double(objSaldo.getMontoOctubre().trim()))));
                writableSheet.addCell(new jxl.write.Label(40, i - 6, formateadorDecimal.format(new Double(objSaldo.getMontoNoviembre().trim()))));
                writableSheet.addCell(new jxl.write.Label(41, i - 6, formateadorDecimal.format(new Double(objSaldo.getMontoDiciembre().trim()))));
                terminaAdecNormales++;
            }
            int renglonInicio = terminaAdecNormales;
            ArrayList<Integer> foliosFiaf = adecua.getFoliosFIAFConsolidacion(nFolio);
            int j = 0;
            while (j < foliosFiaf.size()) {
                AdecuacionCalendario ac = adecua.adecuacionCalendarioFIAF(foliosFiaf.get(j));
                int contadorDetalleFiaf = 0;
                while (contadorDetalleFiaf < ac.saldoLength()) {
                    renglonInicio++;
                    Saldo objSaldo = ac.getSaldo(contadorDetalleFiaf);
                    if (ac.getMovimiento(contadorDetalleFiaf).equals("A")) {
                        resp = adecua.esModificado(objSaldo.getEp());
                    }
                    String claveEp = adecua.obtenClaveSicop(resp, ac.getMovimiento(contadorDetalleFiaf));
                    String[] splitSiaff = objSaldo.getEp().split("\\.");
                    String[] splitClaveEp = claveEp.split("\\.");
                    writableSheet.addCell(new jxl.write.Label(0, renglonInicio, splitClaveEp[0].trim()));
                    writableSheet.addCell(new jxl.write.Label(1, renglonInicio, splitClaveEp[1].trim()));
                    writableSheet.addCell(new jxl.write.Label(2, renglonInicio, splitClaveEp[2].trim()));
                    writableSheet.addCell(new jxl.write.Label(3, renglonInicio, splitClaveEp[3].trim()));
                    writableSheet.addCell(new jxl.write.Label(4, renglonInicio, splitSiaff[1].trim()));
                    writableSheet.addCell(new jxl.write.Label(5, renglonInicio, splitSiaff[2].trim()));
                    writableSheet.addCell(new jxl.write.Label(6, renglonInicio, splitSiaff[0].trim()));
                    writableSheet.addCell(new jxl.write.Label(7, renglonInicio, splitSiaff[3].trim()));
                    writableSheet.addCell(new jxl.write.Label(8, renglonInicio, splitSiaff[4].trim()));
                    writableSheet.addCell(new jxl.write.Label(9, renglonInicio, splitSiaff[5].trim()));
                    writableSheet.addCell(new jxl.write.Label(10, renglonInicio, splitSiaff[6].trim()));
                    writableSheet.addCell(new jxl.write.Label(11, renglonInicio, splitSiaff[7].trim()));
                    writableSheet.addCell(new jxl.write.Label(12, renglonInicio, splitSiaff[8].trim()));
                    writableSheet.addCell(new jxl.write.Label(13, renglonInicio, splitSiaff[9].substring(0, 1).trim()));
                    writableSheet.addCell(new jxl.write.Label(14, renglonInicio, splitSiaff[9].substring(1, 2).trim()));
                    writableSheet.addCell(new jxl.write.Label(15, renglonInicio, splitSiaff[9].substring(2, 3).trim()));
                    writableSheet.addCell(new jxl.write.Label(16, renglonInicio, splitSiaff[9].substring(3, 5).trim()));
                    writableSheet.addCell(new jxl.write.Label(17, renglonInicio, splitSiaff[10].trim()));
                    writableSheet.addCell(new jxl.write.Label(18, renglonInicio, splitSiaff[11].trim()));
                    writableSheet.addCell(new jxl.write.Label(19, renglonInicio, splitSiaff[12].trim()));
                    writableSheet.addCell(new jxl.write.Label(20, renglonInicio, splitSiaff[13].trim()));
                    String clvInterna1 = splitSiaff[14].trim();
                    while (clvInterna1.length() < 10) {
                        clvInterna1 = "0" + clvInterna1.trim();
                    }
                    String clvInterna2 = splitSiaff[15].trim();
                    writableSheet.addCell(new jxl.write.Label(22, renglonInicio, clvInterna2.substring(1, 3).trim()));
                    writableSheet.addCell(new jxl.write.Label(21, renglonInicio, clvInterna1.trim()));
                    writableSheet.addCell(new jxl.write.Label(23, renglonInicio, "000".trim()));
                    writableSheet.addCell(new jxl.write.Label(24, renglonInicio, "000".trim()));
                    writableSheet.addCell(new jxl.write.Label(25, renglonInicio, "00000".trim()));
                    writableSheet.addCell(new jxl.write.Label(26, renglonInicio, "00000".trim()));
                    writableSheet.addCell(new jxl.write.Label(27, renglonInicio, "0000000000".trim()));
                    if (Double.parseDouble(objSaldo.getMontoAnual()) > 0)
                        writableSheet.addCell(new jxl.write.Label(28, renglonInicio, formateadorDecimal.format(Double.parseDouble(objSaldo.getMontoAnual().trim()))));
                    else
                        writableSheet.addCell(new jxl.write.Label(28, renglonInicio, String.valueOf(formateadorCero.format(Double.parseDouble(objSaldo.getMontoAnual().trim())))));
                    writableSheet.addCell(new jxl.write.Label(29, renglonInicio, "1".trim()));
                    if (Double.parseDouble(objSaldo.getMontoEnero()) > 0)
                        writableSheet.addCell(new jxl.write.Label(30, renglonInicio, String.valueOf(formateadorDecimal.format(Double.parseDouble(objSaldo.getMontoEnero().trim())))));
                    else
                        writableSheet.addCell(new jxl.write.Label(30, renglonInicio, String.valueOf(formateadorCero.format(Double.parseDouble(objSaldo.getMontoEnero().trim())))));
                    if (Double.parseDouble(objSaldo.getMontoFebrero()) > 0)
                        writableSheet.addCell(new jxl.write.Label(31, renglonInicio, String.valueOf(formateadorDecimal.format(Double.parseDouble(objSaldo.getMontoFebrero().trim())))));
                    else
                        writableSheet.addCell(new jxl.write.Label(31, renglonInicio, String.valueOf(formateadorCero.format(Double.parseDouble(objSaldo.getMontoFebrero().trim())))));
                    if (Double.parseDouble(objSaldo.getMontoMarzo()) > 0)
                        writableSheet.addCell(new jxl.write.Label(32, renglonInicio, String.valueOf(formateadorDecimal.format(Double.parseDouble(objSaldo.getMontoMarzo().trim())))));
                    else
                        writableSheet.addCell(new jxl.write.Label(32, renglonInicio, String.valueOf(formateadorCero.format(Double.parseDouble(objSaldo.getMontoMarzo().trim())))));
                    if (Double.parseDouble(objSaldo.getMontoAbril()) > 0)
                        writableSheet.addCell(new jxl.write.Label(33, renglonInicio, String.valueOf(formateadorDecimal.format(Double.parseDouble(objSaldo.getMontoAbril().trim())))));
                    else
                        writableSheet.addCell(new jxl.write.Label(33, renglonInicio, String.valueOf(formateadorCero.format(Double.parseDouble(objSaldo.getMontoAbril().trim())))));
                    if (Double.parseDouble(objSaldo.getMontoMayo()) > 0)
                        writableSheet.addCell(new jxl.write.Label(34, renglonInicio, String.valueOf(formateadorDecimal.format(Double.parseDouble(objSaldo.getMontoMayo().trim())))));
                    else
                        writableSheet.addCell(new jxl.write.Label(34, renglonInicio, String.valueOf(formateadorCero.format(Double.parseDouble(objSaldo.getMontoMayo().trim())))));
                    if (Double.parseDouble(objSaldo.getMontoJunio()) > 0)
                        writableSheet.addCell(new jxl.write.Label(35, renglonInicio, String.valueOf(formateadorDecimal.format(Double.parseDouble(objSaldo.getMontoJunio().trim())))));
                    else
                        writableSheet.addCell(new jxl.write.Label(35, renglonInicio, String.valueOf(formateadorCero.format(Double.parseDouble(objSaldo.getMontoJunio().trim())))));
                    if (Double.parseDouble(objSaldo.getMontoJulio()) > 0)
                        writableSheet.addCell(new jxl.write.Label(36, renglonInicio, String.valueOf(formateadorDecimal.format(Double.parseDouble(objSaldo.getMontoJulio().trim())))));
                    else
                        writableSheet.addCell(new jxl.write.Label(36, renglonInicio, String.valueOf(formateadorCero.format(Double.parseDouble(objSaldo.getMontoJulio().trim())))));
                    if (Double.parseDouble(objSaldo.getMontoAgosto()) > 0)
                        writableSheet.addCell(new jxl.write.Label(37, renglonInicio, String.valueOf(formateadorDecimal.format(Double.parseDouble(objSaldo.getMontoAgosto().trim())))));
                    else
                        writableSheet.addCell(new jxl.write.Label(37, renglonInicio, String.valueOf(formateadorCero.format(Double.parseDouble(objSaldo.getMontoAgosto().trim())))));
                    if (Double.parseDouble(objSaldo.getMontoSeptiembre()) > 0)
                        writableSheet.addCell(new jxl.write.Label(38, renglonInicio, String.valueOf(formateadorDecimal.format(Double.parseDouble(objSaldo.getMontoSeptiembre().trim())))));
                    else
                        writableSheet.addCell(new jxl.write.Label(38, renglonInicio, String.valueOf(formateadorCero.format(Double.parseDouble(objSaldo.getMontoSeptiembre().trim())))));
                    if (Double.parseDouble(objSaldo.getMontoOctubre()) > 0)
                        writableSheet.addCell(new jxl.write.Label(39, renglonInicio, String.valueOf(formateadorDecimal.format(Double.parseDouble(objSaldo.getMontoOctubre().trim())))));
                    else
                        writableSheet.addCell(new jxl.write.Label(39, renglonInicio, String.valueOf(formateadorCero.format(Double.parseDouble(objSaldo.getMontoOctubre().trim())))));
                    if (Double.parseDouble(objSaldo.getMontoNoviembre()) > 0)
                        writableSheet.addCell(new jxl.write.Label(40, renglonInicio, String.valueOf(formateadorDecimal.format(Double.parseDouble(objSaldo.getMontoNoviembre().trim())))));
                    else
                        writableSheet.addCell(new jxl.write.Label(40, renglonInicio, String.valueOf(formateadorCero.format(Double.parseDouble(objSaldo.getMontoNoviembre().trim())))));
                    if (Double.parseDouble(objSaldo.getMontoDiciembre()) > 0)
                        writableSheet.addCell(new jxl.write.Label(41, renglonInicio, String.valueOf(formateadorDecimal.format(Double.parseDouble(objSaldo.getMontoDiciembre().trim())))));
                    else
                        writableSheet.addCell(new jxl.write.Label(41, renglonInicio, String.valueOf(formateadorCero.format(Double.parseDouble(objSaldo.getMontoDiciembre().trim())))));
                    contadorDetalleFiaf++;
                }
                j++;
            }
            writableWorkbook.write();
            writableWorkbook.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void creaFap(HttpServletRequest request, HttpServletResponse response) throws Exception {
        AdecuacionBusinessLogic adecua = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
        HttpSession session = request.getSession(false);
        DiskFileItemFactory factory = DiskFileItemFactory.builder().get();
        JakartaServletFileUpload upload = new JakartaServletFileUpload(factory);
        String responsableCompleto = request.getParameter("responsable");
        String responsable = "Responsable del Area";
        String area = "Area";
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
        ArrayList<Object> arrAdecAmpliacion = new ArrayList<>();
        ArrayList<ArrayList> arrAdecReduccion = new ArrayList<ArrayList>();
        ArrayList<ArrayList> arrActividadInstitucional = new ArrayList();
        String justificacionAmp = "";
        String justificacionRed = "";
        IADEBusinessLogic iadebl = new IADEBusinessLogic(jniName);
        arrAdecuacion = iadebl.cargaFAP01(folio);
        arrAdecAmpliacion = adecua.consultaFap02IADEAmpliaExcel(folio);
        arrAdecReduccion = adecua.consultaFap02IADEReduceExcel(folio);
        arrActividadInstitucional = adecua.actividadInstitucionalIADE(folio);
        justificacionAmp = adecua.justificacionAmpIADE(folio);
        justificacionRed = adecua.justificacionRedIADE(folio);
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
        Row rwJustAmp = hJustificacion.getRow(12);
        rwJustAmp.getCell(2).setCellValue(justificacionAmp);
        Row rwJustRed = hJustificacion.getRow(14);
        rwJustRed.getCell(2).setCellValue(justificacionRed);
        // Inserta el folio SAI dentro de la primera hoja (FAP01) dentro de
        // excel
        Row filaFolioSai = sheet.getRow(7);
        Cell celdaFolioSai = filaFolioSai.getCell(28);
        celdaFolioSai.setCellValue("SAI:" + c.getFolio());
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
        /*
		 * ArrayList<Integer> foliosFiaf =
		 * adecua.getFoliosFIAFConsolidacion(folio); int j=0;
		 * 
		 * while(j<foliosFiaf.size()){ int k=0; AdecuacionCalendario ac =
		 * adecua.adecuacionCalendarioFIAF(foliosFiaf.get(j));
		 * while(k<ac.saldoLength()){ Saldo objSaldo = ac.getSaldo(k); if
		 * ("R".equals(ac.getMovimiento(k))) { saldoR.add(objSaldo); } else if
		 * ("A".equals(ac.getMovimiento(k))) { saldoA.add(objSaldo); } k++; }
		 * j++; }
		 */
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
        int numfilas = imprimeSaldos(sheet, saldoR, sumaR, 15, 'R', 0, estiloBordeDerecho);
        imprimeSumas(sheet, numfilas + 5, sumaR, estiloBordeDerecho);
        insertaFila(wb, sheet, numfilas += 9, saldoA.size());
        numfilas = imprimeSaldos(sheet, saldoA, sumaA, numfilas, 'A', numfilas - 24, estiloBordeDerecho);
        imprimeSumas(sheet, numfilas + 3, sumaA, estiloBordeDerecho);
        /*entrar a funcion para llenar ampliacones y reducciones FAP02*/
        Row rwJustifiacionTRANSA = hJustificacion.getRow(10);
        rwJustifiacionTRANSA.getCell(2).setCellValue("Con base en lo dispuesto en los artículos 57 y 58 de la Ley Federal de Presupuesto y Responsabilidad Hacendaria (LFPRH), artículos 97 y 100 de su Reglamento (RLFRH), la Comisión Nacional Forestal (CONAFOR) solicita la presente adecuación presupuestaria interna.");
        Row rwJustifiacionTRANSR = hJustificacion.getRow(16);
        rwJustifiacionTRANSR.getCell(2).setCellValue("Cabe señalar que se dará cumplimiento a lo establecido en el artículo 9 del PEF 2025. Este movimiento presupuestario no afecta el cumplimiento de los objetivos y metas autorizados de esta Comisión y permite su mejor cumplimiento y es de carácter no regularizable  y se incluirá en la carpeta de la Primera Sesión Ordinaria de la Junta de Gobierno de la Comisión Nacional Forestal del 2025.");
        String fiscal = adecua.obtenEjercicioFiscal();
        imprimeFAP02(wb, fiscal, arrAdecAmpliacion, arrAdecReduccion, fecha, c.getFolio());
        imprimeMETA(wb, arrActividadInstitucional);
        //imprimeResponsable(sheet, numfilas + 8, responsable, area);
        wb.write(response.getOutputStream().toPath());
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
            for (int j = 0; j <= 29; j++) {
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

    public void creaRepIntegra(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
        AdecuacionBusinessLogic adecua = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
        HttpSession session = request.getSession(false);
        ArrayList arrIntegradas = new ArrayList();
        DiskFileItemFactory factory = DiskFileItemFactory.builder().get();
        JakartaServletFileUpload upload = new JakartaServletFileUpload(factory);
        //String responsableCompleto = request.getParameter("responsable");
        String hTitulo1 = "COMISIÓN NACIONAL DEL AGUA.";
        String hTitulo2 = "SISTEMA DE INFORMACIÓN FINANCIERA.";
        String hTitulo3 = "SUBSISTEMA DE PRESUPUESTOS DE EGRESOS.";
        int nConsecutivoSICOP = 0;
        Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
        int nFolio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
        Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        if (usuario == null) {
            response.sendRedirect("../index.jsp");
            return;
        }
        try {
            arrIntegradas = adecua.consultaIntegradas(nFolio);
        } finally {
        }
        ArrayList arrAdecuacion = (ArrayList) arrIntegradas.get(0);
        ArrayList arrAdecuacionD = (ArrayList) arrAdecuacion.get(0);
        String cUnidadCreadora = "";
        String cFolioAdecuacion = "";
        String cMontoAdecuacion = "";
        String cFechaAdecuacion = "";
        String cJustificacionAdecua = "";
        String cEjercicioFiscal = (String) arrAdecuacionD.get(0);
        String cTipoAdecuacion = (String) arrAdecuacionD.get(1);
        //String cNivelAdecua = (String) arrAdecuacionD.get(2);
        double mTotalAmpliaciones = (Double) arrAdecuacionD.get(3);
        double mTotalReducciones = (Double) arrAdecuacionD.get(4);
        String cFechaIntegracion = (String) arrAdecuacionD.get(5);
        //double mTotalLiberacion = 0;
        //String cTipoMovimiento = "";
        if (session.getAttribute("objConsecutivoSICOP") != null)
            nConsecutivoSICOP = (Integer) session.getAttribute("objConsecutivoSICOP");
        String file_name = "" + nConsecutivoSICOP;
        response.setContentType("application/vnd.ms-excel");
        response.addHeader("Content-Disposition", "inline; filename=\"Rep-Integracion_" + file_name + ".xls\"; charset=UTF-8\" pageEncoding=\"utf-8\"");
        String hTitulo4 = "LISTA DE ADECUACIONES " + cTipoAdecuacion.toString() + " POR FOLIO";
        String hSubtitulo1 = "SAI";
        String hSubtitulo2 = "Ejercicio " + cEjercicioFiscal;
        Date dSubtitulo3 = new Timestamp(System.currentTimeMillis());
        // Para escribir en el archivo la fecha de tipo dd/mm/yyyy
        //Date date = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fecha = sdf.format(dSubtitulo3);
        String cFileExcel = upload.getRepositoryPath() + "/PlantillaIntegracion.xls";
        String cTituloFor = "FOLIO INTERNO:" + nConsecutivoSICOP + "  Fecha Generación :" + fecha;
        InputStream inp = new FileInputStream(cFileExcel);
        Workbook wb = new HSSFWorkbook(inp);
        Sheet sheet = wb.getSheetAt(0);
        // SE colocan los Titulos
        Row rTitle1 = sheet.getRow(2);
        Cell cTilte1 = rTitle1.getCell(1);
        cTilte1.setCellValue(hTitulo1);
        Row rTitle2 = sheet.getRow(3);
        Cell cTilte2 = rTitle2.getCell(1);
        cTilte2.setCellValue(hTitulo2);
        Row rTitle3 = sheet.getRow(4);
        Cell cTilte3 = rTitle3.getCell(1);
        cTilte3.setCellValue(hTitulo3);
        Row rTitle4 = sheet.createRow(5);
        Cell cTilte4 = rTitle4.createCell(1);
        cTilte4.setCellValue(hTitulo4);
        Row rTitle5 = sheet.getRow(6);
        Cell cTilte5 = rTitle5.getCell(1);
        //Cell cTilte6 = rTitle5.getCell(4);
        //Cell cTilte7 = rTitle5.getCell(11);
        cTilte5.setCellValue(hSubtitulo1);
        cTilte5.setCellValue(hSubtitulo2);
        cTilte5.setCellValue(dSubtitulo3);
        Row rTitle6 = sheet.createRow(7);
        Cell cTilte8 = rTitle6.createCell(1);
        cTilte8.setCellValue(cTituloFor);
        int j = 1;
        Row rSubTiTle = sheet.createRow(8);
        Cell cellrSubTitle1 = rSubTiTle.createCell(1);
        Cell cellrSubTitle2 = rSubTiTle.createCell(2);
        Cell cellrSubTitle3 = rSubTiTle.createCell(3);
        Cell cellrSubTitle4 = rSubTiTle.createCell(4);
        Cell cellrSubTitle5 = rSubTiTle.createCell(5);
        cellrSubTitle1.setCellValue("E.C.");
        cellrSubTitle2.setCellValue("AFECTACION");
        cellrSubTitle3.setCellValue("MONTO");
        cellrSubTitle4.setCellValue("FECHA TRAMITACION");
        cellrSubTitle5.setCellValue("NOTAS");
        int nrengXLS = 9;
        while (j <= arrAdecuacion.size() - 1) {
            ArrayList arrAdecua = (ArrayList) arrAdecuacion.get(j);
            Row RowDetIntegra = sheet.createRow(nrengXLS);
            Cell cellDet1 = RowDetIntegra.createCell(0);
            Cell cellDet2 = RowDetIntegra.createCell(1);
            Cell cellDet3 = RowDetIntegra.createCell(2);
            Cell cellDet4 = RowDetIntegra.createCell(3);
            Cell cellDet5 = RowDetIntegra.createCell(4);
            cUnidadCreadora = (String) arrAdecua.get(0);
            cellDet1.setCellValue(cUnidadCreadora);
            cFolioAdecuacion = (String) arrAdecua.get(1);
            cellDet2.setCellValue(cFolioAdecuacion);
            cMontoAdecuacion = (String) arrAdecua.get(2);
            cellDet3.setCellValue(cMontoAdecuacion);
            cFechaAdecuacion = (String) arrAdecua.get(3);
            cellDet4.setCellValue(cFechaAdecuacion);
            cJustificacionAdecua = (String) arrAdecua.get(4);
            cellDet5.setCellValue(cJustificacionAdecua);
            arrAdecua = null;
            j++;
            nrengXLS++;
        }
        // coloca pie del reporte
        nrengXLS++;
        Row rSubPie = sheet.createRow(nrengXLS);
        Cell cellrPieTitle1 = rSubTiTle.createCell(1);
        Cell cellrPieTitle2 = rSubTiTle.createCell(2);
        Cell cellrPieTitle3 = rSubTiTle.createCell(3);
        Cell cellrPieTitle4 = rSubTiTle.createCell(4);
        Cell cellrPieTitle5 = rSubTiTle.createCell(5);
        Cell cellrPieTitle6 = rSubTiTle.createCell(6);
        cellrPieTitle1.setCellValue("Folio");
        cellrPieTitle2.setCellValue("FECHA GENERACION");
        cellrPieTitle3.setCellValue("NUMERO AFECTACIONES");
        cellrPieTitle4.setCellValue("AMPLIACIONES");
        cellrPieTitle5.setCellValue("REDUCCIONES");
        cellrPieTitle6.setCellValue("DIFERENCIA");
        nrengXLS++;
        Row rPiedeReporteT = sheet.createRow(nrengXLS);
        Cell cellPieD1 = rPiedeReporteT.createCell(1);
        Cell cellPieD2 = rPiedeReporteT.createCell(2);
        Cell cellPieD3 = rPiedeReporteT.createCell(3);
        Cell cellPieD4 = rPiedeReporteT.createCell(4);
        Cell cellPieD5 = rPiedeReporteT.createCell(5);
        Cell cellPieD6 = rPiedeReporteT.createCell(6);
        cellPieD1.setCellValue(nConsecutivoSICOP);
        cellPieD2.setCellValue(cFechaIntegracion);
        cellPieD3.setCellValue(j);
        cellPieD4.setCellValue(mTotalAmpliaciones);
        cellPieD5.setCellValue(mTotalReducciones);
        cellPieD6.setCellValue(mTotalAmpliaciones - mTotalReducciones);
        nrengXLS++;
        Row rPiedeReporteR = sheet.createRow(nrengXLS);
        Cell cellPieR1 = rPiedeReporteR.createCell(1);
        Cell cellPieR3 = rPiedeReporteR.createCell(3);
        Cell cellPieR4 = rPiedeReporteR.createCell(4);
        Cell cellPieR5 = rPiedeReporteR.createCell(5);
        Cell cellPieR6 = rPiedeReporteR.createCell(6);
        cellPieR1.setCellValue("TOTAL");
        cellPieR3.setCellValue(j);
        cellPieR4.setCellValue(mTotalAmpliaciones);
        cellPieR5.setCellValue(mTotalReducciones);
        cellPieR6.setCellValue(mTotalAmpliaciones - mTotalReducciones);
        wb.write(response.getOutputStream().toPath());
        wb.close();
    }

    public synchronized void creaRepIntegraExcel(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String folioIntegradas = "";
        String tokenFolio = "";
        AdecuacionBusinessLogic adecua = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
        HttpSession session = request.getSession(false);
        ArrayList arrIntegradas = new ArrayList();
        DiskFileItemFactory factory = DiskFileItemFactory.builder().get();
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
        arrIntegradas = adecua.consultaIntegradasExcel(nFolio);
        ArrayList arrAdecuacion = (ArrayList) arrIntegradas.get(0);
        ArrayList arrAdecuacionR = (ArrayList) arrAdecuacion.get(0);
        int nrenglones = arrAdecuacion.size();
        if (session.getAttribute("objConsecutivoSICOP") != null)
            nConsecutivoSICOP = (Integer) session.getAttribute("objConsecutivoSICOP");
        String file_name = "Rep-Integracion_" + nConsecutivoSICOP + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        response.setContentType("application/vnd.ms-excel");
        response.addHeader("Content-Disposition", "inline; filename=\"" + file_name + "\"; ");
        String cFileExcelPlantilla = getServletContext().getRealPath("Reportes" + File.separator + "plantilla_integra_adecua.xls");
        InputStream fs = new FileInputStream(cFileExcelPlantilla);
        Util.copiaArchivo(fs, file_name);
        fs.close();
        InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
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
            if (nRenglon == 4) {
                Cell folioSicop = (row.getCell(1) == null ? row.createCell(1) : row.getCell(1));
                folioSicop.setCellValue(nConsecutivoSICOP);
            }
            if (nRenglon == 5) {
                Cell fechac = row.getCell(1);
                fechac.setCellValue(fecha);
            }
            //int renglon = 0;
            int count = 1;
            if (nRenglon == 9) {
                for (int j = 0; j < nrenglones; j++) {
                    @SuppressWarnings("rawtypes")
                    ArrayList arrAdecuacionD = (ArrayList) arrAdecuacion.get(j);
                    Row r = sheet.createRow(9 + count);
                    for (int i = 0; i <= 4; i++) {
                        Cell ue = r.createCell(i);
                        ue.setCellValue((String) arrAdecuacionD.get(i));
                        if (i == 4) {
                            CellRangeAddress cra = new CellRangeAddress(9 + count, 9 + count, i, i + 6);
                            sheet.addMergedRegion(cra);
                        } else if (i == 1) {
                            folioIntegradas += tokenFolio + (String) arrAdecuacionD.get(i);
                            tokenFolio = ", ";
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
                cellrPieTitle1.setCellValue("No. IADE");
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
        // Crea resumen
        CellStyle estiloResumenTop = workbook.createCellStyle();
        estiloResumenTop.setBorderTop(BorderStyle.MEDIUM);
        CellStyle estiloResumenLeftTop = workbook.createCellStyle();
        estiloResumenLeftTop.setBorderTop(BorderStyle.MEDIUM);
        estiloResumenLeftTop.setBorderLeft(BorderStyle.MEDIUM);
        CellStyle estiloResumenRigthTop = workbook.createCellStyle();
        estiloResumenRigthTop.setBorderRight(BorderStyle.MEDIUM);
        estiloResumenRigthTop.setBorderTop(BorderStyle.MEDIUM);
        CellStyle estiloResumenBottom = workbook.createCellStyle();
        estiloResumenBottom.setBorderBottom(BorderStyle.MEDIUM);
        estiloResumenBottom.setWrapText(true);
        estiloResumenBottom.setVerticalAlignment(VerticalAlignment.TOP);
        CellStyle estiloResumenLeftBottom = workbook.createCellStyle();
        estiloResumenLeftBottom.setBorderBottom(BorderStyle.MEDIUM);
        estiloResumenLeftBottom.setBorderLeft(BorderStyle.MEDIUM);
        estiloResumenLeftBottom.setWrapText(true);
        estiloResumenLeftBottom.setVerticalAlignment(VerticalAlignment.TOP);
        CellStyle estiloResumenRigthBottom = workbook.createCellStyle();
        estiloResumenRigthBottom.setBorderBottom(BorderStyle.MEDIUM);
        estiloResumenRigthBottom.setBorderRight(BorderStyle.MEDIUM);
        estiloResumenRigthBottom.setWrapText(true);
        estiloResumenRigthBottom.setVerticalAlignment(VerticalAlignment.TOP);
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);
        font.setFontName("Calibri");
        font.setBold(true);
        estiloResumenTop.setFont(font);
        estiloResumenLeftTop.setFont(font);
        estiloResumenRigthTop.setFont(font);
        estiloResumenBottom.setFont(font);
        Row rwResumen = sheet.createRow(nRenglon + nrenglones + 5);
        Cell resumen = rwResumen.createCell(0);
        resumen.setCellValue("Folios Integrados");
        sheet.addMergedRegion(new CellRangeAddress(nRenglon + nrenglones + 5, nRenglon + nrenglones + 5, 0, 6));
        for (int cnt = 0; cnt <= 6; cnt++) {
            resumen = rwResumen.getCell(cnt) == null ? rwResumen.createCell(cnt) : rwResumen.getCell(cnt);
            if (cnt == 0)
                resumen.setCellStyle(estiloResumenLeftTop);
            else if (cnt == 6)
                resumen.setCellStyle(estiloResumenRigthTop);
            else
                resumen.setCellStyle(estiloResumenTop);
        }
        rwResumen = sheet.createRow(nRenglon + nrenglones + 6);
        rwResumen.setHeight((short) 1600);
        resumen = rwResumen.createCell(0);
        resumen.setCellValue(folioIntegradas);
        sheet.addMergedRegion(new CellRangeAddress(nRenglon + nrenglones + 6, nRenglon + nrenglones + 6, 0, 6));
        for (int cnt = 0; cnt <= 6; cnt++) {
            resumen = rwResumen.getCell(cnt) == null ? rwResumen.createCell(cnt) : rwResumen.getCell(cnt);
            if (cnt == 0)
                resumen.setCellStyle(estiloResumenLeftBottom);
            else if (cnt == 6)
                resumen.setCellStyle(estiloResumenRigthBottom);
            else
                resumen.setCellStyle(estiloResumenBottom);
        }
        fsArchivo.close();
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos.toPath());
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
    }

    public void creaRepIntegraPdf(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
        String reportPath = getServletContext().getRealPath("Reportes" + File.separator + "integraAdecua.jasper");
        reportesBussinesObject objReporte = new reportesBussinesObject(jniName);
        objReporte.reporteIntegraAdecuaPdf(request, response, reportPath);
    }

    public void creaFap02IADE(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AdecuacionBusinessLogic adecua = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
        HttpSession session = request.getSession(false);
        ArrayList<Object> arrAdecAmpliacion = new ArrayList();
        ArrayList<ArrayList> arrAdecReduccion = new ArrayList<ArrayList>();
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
        Date fecha_crea = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fecha = sdf.format(fecha_crea);
        try {
            arrAdecAmpliacion = adecua.consultaFap02IADEAmpliaExcel(nFolio);
            arrAdecReduccion = adecua.consultaFap02IADEReduceExcel(nFolio);
        } catch (Exception e) {
            throw new ServletException(e);
        }
        double suma = 0;
        //String folioCompleto = c.getFolio();
        //String unidadEjecutora = new String(c.getFolio().substring(5, 8));
        ArrayList arrAdecuacionReduccion = (arrAdecReduccion.size() == 0 ? null : (ArrayList) arrAdecReduccion.get(0));
        ArrayList arrAdecuacionAmpliacion = (arrAdecAmpliacion == null ? null : (ArrayList) arrAdecAmpliacion.get(0));
        String file_name = "Rep-FAP02" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";
        response.setContentType("application/vnd.ms-excel");
        response.addHeader("Content-Disposition", "inline; filename=\"" + file_name + "\"; ");
        String cFileExcelPlantilla = getServletContext().getRealPath("Reportes" + File.separator + "plantilla_FAP_02_TRANS.xls");
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
        int k = 1;
        //for (int k = 0; k < 3; k++) {
        ArrayList<?> arrInfoDeta = arrAdecuacionAmpliacion;
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
            //int renglon = 0;
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
                            //ue.setCellType(Cell.CELL_TYPE_NUMERIC);
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
            nRenglon++;
        }
        fsArchivo.close();
        File fsalida = new File(file_name);
        FileOutputStream fos = new FileOutputStream(fsalida);
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
        workbook.write(bos.toPath());
        workbook.close();
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

    public void imprimeFAP02(Workbook wb, String fiscal, ArrayList<Object> arrAdecAmpliacion, ArrayList<ArrayList> arrAdecReduccion, String fecha, String folio) {
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
        estiloMoneda.setBorderTop(BorderStyle.THIN);
        estiloMoneda.setVerticalAlignment(VerticalAlignment.CENTER);
        CellStyle estiloFecha = wb.createCellStyle();
        estiloFecha.setFont(fuenteFecha);
        estiloFecha.setAlignment(HorizontalAlignment.RIGHT);
        double suma = 0;
        int k = 1;
        //for (int k = 1; k < 3; k++) {
        ArrayList<?> arrInfoDeta = (k == 1 ? arrAdecReduccion : arrAdecAmpliacion);
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
            //int renglon = 0;
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
