package com.syc.contable.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import com.jenkov.prizetags.tree.itf.ITree;
import com.syc.contable.AccountingEngine;
import com.syc.contable.AdecuacionBusinessLogic;
import com.syc.contable.ReintegrosAnexo1BusinessLogic;
import com.syc.contable.core.ReintegroDetalleMil;
import com.syc.contable.core.ReintegroEncabezadoMil;
import com.syc.fortimax.core.DocumentoBussinessLogic;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.CasoOperacionBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoDatoManager;
import com.syc.gestion.core.CasoOperacion;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "ReintegrosAnexo1Servlet", urlPatterns = { "/servlet/ReintegrosAnexo1Servlet" })
public class ReintegrosAnexo1Servlet extends HttpServlet {

    private static final long serialVersionUID = 3830246252504144684L;

    private static final Logger log = Logger.getLogger(ReintegrosAnexo1Servlet.class);

    private String jniName = null;

    private static String folioGenerator = null;

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
            folioGenerator = (String) ic.lookup("java:comp/env/folioGeneratorInterface");
            if (folioGenerator == null) {
                folioGenerator = "com.syc.gestion.custom.DefaultFolioGenerator";
                log.info("Environment Entry \"folioGeneratorInterface\" nula usando default \"" + folioGenerator + "\"");
            } else
                log.info("folioGeneratorInterface=" + folioGenerator);
        } catch (NamingException exc) {
            folioGenerator = "com.syc.gestion.custom.DefaultFolioGenerator";
            log.info("Environment Entry \"folioGeneratorInterface\" no definida usando default \"" + folioGenerator + "\"");
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    //para obtener el ejercicio fiscal en diferentes funciones
    AdecuacionBusinessLogic adecProy = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);

    ReintegrosAnexo1BusinessLogic reintegro = new ReintegrosAnexo1BusinessLogic(GestionInterface.ATT_CONEXION);

    //ReintegrosMilBusinessLogic reintegroMil = new ReintegrosMilBusinessLogic(GestionInterface.ATT_CONEXION);
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String DATE_FORMAT = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        // today
        Calendar c1 = Calendar.getInstance();
        String today = sdf.format(c1.getTime());
        String mensaje = "";
        String cancelaDocumento = request.getParameter("cancelaDocumento");
        String generaExcel = request.getParameter("generaExcel");
        String folio = request.getParameter("folio");
        String autoriza = request.getParameter("autoriza");
        String aplica = request.getParameter("aplica");
        String cancela = request.getParameter("cancela");
        String borraTodo = request.getParameter("borraTodo");
        String generaCaso = request.getParameter("generaCaso");
        String ctab = request.getParameter("ctab");
        //reintegro normal
        String fAcredit = request.getParameter("fAcredit");
        String pathURL = request.getContextPath();
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + pathURL + "/";
        if (cancelaDocumento != null && !"".equals(cancelaDocumento) && "true".equals(cancelaDocumento)) {
            try {
                //String folio = request.getParameter("folio");
                if (folio != null && !"".equals(folio)) {
                    mensaje += cancelaReintegro(folio);
                } else {
                    mensaje += "Todos los campos deben llenarse";
                }
            } catch (Exception ex) {
                mensaje += ex.toString();
            } catch (IllegalAccessError e) {
                mensaje += e.toString();
            } finally {
                response.sendRedirect("../plantillasCasos/cancelaDocumentoManual.jsp?mensaje=" + mensaje);
            }
        }
        if (generaExcel != null && !"".equals(generaExcel) && "1".equals(generaExcel)) {
            try {
                creaExcelReintegroCarga("Reintegro", response, request);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (generaExcel != null && !"".equals(generaExcel) && "7".equals(generaExcel)) {
            try {
                creaExcelReporte("Reporte", response, request);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (aplica != null && !"".equals(aplica) && "1".equals(aplica)) {
            HttpSession session = request.getSession(false);
            PrintWriter out = response.getWriter();
            if (session == null) {
                response.sendRedirect("../index.jsp");
                return;
            }
            Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
            Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
            Map<?, ?> m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
            String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
            try {
                reintegro.actualizaFechaAplicacion(new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue(), today);
                out.println(new String(reintegro.ValidaReintegro(new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue(), usuario, adecProy.obtenEjercicioFiscal(), usuario.getU_Ramo(), usuario.getU_UR(), c, usuario.getPropiedad("CCENTROCONTABLE").getValor(), c.getCasoDato("FECHA_AP_CONT").getValor(), m, prefixPath, usuario.getLogin()).getBytes("UTF-8"), "ISO-8859-1"));
            } catch (Exception ex) {
                ex.printStackTrace();
                log.warn(mensaje);
            }
        }
        if (autoriza != null && !"".equals(autoriza) && "1".equals(autoriza)) {
            HttpSession session = request.getSession(false);
            PrintWriter out = response.getWriter();
            if (session == null) {
                response.sendRedirect("../index.jsp");
                return;
            }
            Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
            Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
            Map<?, ?> m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
            String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
            try {
                String ipNombreServidor = java.net.InetAddress.getByName(request.getServerName()).toString();
                String[] ipServidor = ipNombreServidor.split("/");
                if (ipServidor[1].equals(GestionInterface.SYS_IP_PRODUCCION))
                    reintegro.correoProduccion = true;
                out.println(new String(reintegro.AutorizaReintegroNuevo(c, "", "", "", "", m, prefixPath, usuario.getLogin(), usuario, fAcredit).getBytes("UTF-8"), "ISO-8859-1"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (cancela != null && !"".equals(cancela) && "1".equals(cancela)) {
            HttpSession session = request.getSession(false);
            PrintWriter out = response.getWriter();
            if (session == null) {
                response.sendRedirect("../index.jsp");
                return;
            }
            Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
            Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
            Map<?, ?> m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
            String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
            try {
                out.println(new String(reintegro.cancelarAppContableNuevo(c, m, prefixPath, usuario.getLogin(), c.getCasoDato("FECHA_AP_CONT").getValor()).getBytes("UTF-8"), "ISO-8859-1"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (borraTodo != null && !"".equals(borraTodo) && "1".equals(borraTodo)) {
            //ESTO BORRA DE LA BASE DE DATOS LOS REGISTROS Y
            //LIMPIA EL ARCHIVO PARA SUBIR UNA RECTIFICACION NUEVA EN EL MISMO FOLIO
            try {
                HttpSession session = request.getSession(false);
                if (session == null) {
                    response.sendRedirect("../index.jsp");
                    return;
                }
                DocumentoBussinessLogic dbl = new DocumentoBussinessLogic();
                String folioReintegro = request.getParameter("folioReintegro");
                //Llamada al businesslogic
                reintegro.borraReintegro(Integer.parseInt(folioReintegro));
                Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
                dbl.limpiaDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 2, 1);
                CasoBusinessLogic casoTx = new CasoBusinessLogic("jdbc/gestion");
                ITree tree = casoTx.getArbolCaso(c);
                session.setAttribute("tree.model", tree);
                response.sendRedirect(basePath + "plantillasCasos/reintegroAnexo1.jsp");
            } catch (SQLException e) {
                response.sendRedirect(basePath + "plantillasCasos/reintegroAnexo1.jsp");
                e.printStackTrace();
            } catch (Exception e) {
                response.sendRedirect(basePath + "plantillasCasos/reintegroAnexo1.jsp");
                e.printStackTrace();
            }
        }
        if (generaCaso != null && !"".equals(generaCaso) && "1".equals(generaCaso)) {
            try {
                generaCasoInserts(response, request);
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (GestionException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String cancelaReintegro(String folio) throws SQLException {
        Connection conn = null;
        String mensaje = "";
        try {
            AccountingEngine ae = new AccountingEngine();
            conn = ae.getConnection();
            //CANCELAR REINTEGRO
            boolean reintegroResultado = ae.cancelAccountingApplication(conn, "REINTEGROANEXO1", folio, "tReintegroAnexo1Encabezado", "tReintegroAnexo1Detalle", "nFolioReintegroAnexo1");
            mensaje += ", Cancela reintegro Resultado:" + new Boolean(reintegroResultado).toString();
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            mensaje += "No ha sido posible realizar la cancelación <br> <br> " + e.toString();
            conn.rollback();
        } finally {
            if (conn != null)
                conn.close();
            conn = null;
        }
        return mensaje;
    }

    public void creaExcelReintegroCarga(String folio, HttpServletResponse response, HttpServletRequest request) throws IOException, SQLException {
        HttpSession session = request.getSession();
        Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        String file_name = "" + folio + "";
        response.setContentType("application/vnd.ms-excel");
        response.addHeader("Content-Disposition", "inline; filename=\"" + file_name + "Carga.xls\";");
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet hs = wb.createSheet();
        HSSFRow fila = hs.createRow(0);
        String DATE_FORMAT = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        // today
        Calendar c1 = Calendar.getInstance();
        String today = sdf.format(c1.getTime());
        String[] valuesCLC = request.getParameterValues("clc");
        String[] valuesCXP = request.getParameterValues("cxp");
        String[] valuesEP = request.getParameterValues("ep");
        String[] valuesMes = request.getParameterValues("mes");
        String[] valuesImporte = request.getParameterValues("importe");
        double total = 0;
        for (int i = 0; i < valuesImporte.length; i++) {
            total += Double.valueOf(valuesImporte[i]);
        }
        HSSFCell celda = fila.createCell(0);
        celda.setCellValue("EJERCICIO");
        celda = fila.createCell(1);
        celda.setCellValue("RAMO");
        celda = fila.createCell(2);
        celda.setCellValue("CLAVE UNIDAD ADMINISTRATIVA");
        celda = fila.createCell(3);
        celda.setCellValue("TOTAL");
        celda = fila.createCell(4);
        celda.setCellValue("AVISO REINTEGRO");
        celda = fila.createCell(5);
        celda.setCellValue("FECHA DE SOLICITUD");
        celda = fila.createCell(6);
        celda.setCellValue("MOVIMIENTO");
        celda = fila.createCell(7);
        celda.setCellValue("TIPO DE AVISO");
        celda = fila.createCell(8);
        celda.setCellValue("FORMA DE PAGO");
        celda = fila.createCell(9);
        celda.setCellValue("CAUSA DEL AVISO");
        celda = fila.createCell(10);
        celda.setCellValue("FECHA DE APLICACION");
        celda = fila.createCell(11);
        celda.setCellValue("FECHA DE ACREDITAMIENTO");
        celda = fila.createCell(12);
        celda.setCellValue("CLAVE RASTREO");
        celda = fila.createCell(13);
        celda.setCellValue("FICHA DE DEPOSITO");
        celda = fila.createCell(14);
        celda.setCellValue("LINEA DE CAPTURA");
        celda = fila.createCell(15);
        celda.setCellValue("CLAVE BANCARIA");
        celda = fila.createCell(16);
        celda.setCellValue("CUENTA BANCARIA");
        celda = fila.createCell(17);
        celda.setCellValue("FOLIO DE DEPENDENCIA");
        fila = hs.createRow(1);
        celda = fila.createCell(0);
        celda.setCellValue(adecProy.obtenEjercicioFiscal());
        celda = fila.createCell(1);
        celda.setCellValue(usuario.getU_Ramo());
        celda = fila.createCell(2);
        celda.setCellValue(usuario.getU_UR());
        celda = fila.createCell(3);
        celda.setCellValue(total);
        celda = fila.createCell(4);
        celda.setCellValue("N/A");
        celda = fila.createCell(5);
        celda.setCellValue(today);
        celda = fila.createCell(6);
        celda.setCellValue("N/A");
        celda = fila.createCell(7);
        celda.setCellValue("N/A");
        celda = fila.createCell(8);
        celda.setCellValue("N/A");
        celda = fila.createCell(9);
        celda.setCellValue("N/A");
        celda = fila.createCell(10);
        celda.setCellValue(today);
        celda = fila.createCell(11);
        celda.setCellValue(today);
        celda = fila.createCell(12);
        celda.setCellValue("N/A");
        celda = fila.createCell(13);
        celda.setCellValue("N/A");
        celda = fila.createCell(14);
        celda.setCellValue("N/A");
        celda = fila.createCell(15);
        celda.setCellValue("N/A");
        celda = fila.createCell(16);
        celda.setCellValue("N/A");
        celda = fila.createCell(17);
        celda.setCellValue("N/A");
        //celda = fila.createCell(18); //este sobra
        //celda.setCellValue("N/A");
        fila = hs.createRow(2);
        celda = fila.createCell(0);
        celda.setCellValue("OBSERVACIONES");
        celda = fila.createCell(1);
        celda.setCellValue("CONCEPTO");
        fila = hs.createRow(3);
        celda = fila.createCell(0);
        celda.setCellValue("N/A");
        celda = fila.createCell(1);
        celda.setCellValue("N/A");
        fila = hs.createRow(4);
        celda = fila.createCell(0);
        celda.setCellValue("CLC");
        celda = fila.createCell(1);
        celda.setCellValue("SEC CLC");
        celda = fila.createCell(2);
        celda.setCellValue("CLAVE SIAFF O MAP");
        celda = fila.createCell(3);
        celda.setCellValue("CLAVE INTERNA");
        celda = fila.createCell(4);
        celda.setCellValue("MES");
        celda = fila.createCell(5);
        celda.setCellValue("IMPORTE");
        celda = fila.createCell(6);
        celda.setCellValue("CUENTA POR PAGAR");
        for (int i = 0; i < valuesCLC.length; i++) {
            fila = hs.createRow(i + 5);
            celda = fila.createCell(0);
            celda.setCellValue(valuesCLC[i]);
            celda = fila.createCell(1);
            celda.setCellValue(i + 1);
            celda = fila.createCell(2);
            celda.setCellValue(valuesEP[i].substring(0, valuesEP[i].length() - 8));
            celda = fila.createCell(3);
            celda.setCellValue(valuesEP[i].substring(valuesEP[i].length() - 7));
            celda = fila.createCell(4);
            celda.setCellValue(valuesMes[i]);
            celda = fila.createCell(5);
            celda.setCellValue(valuesImporte[i]);
            celda = fila.createCell(6);
            celda.setCellValue(valuesCXP[i].trim());
        }
        wb.write(response.getOutputStream());
        wb.close();
    }

    public void creaExcelReporte(String folio, HttpServletResponse response, HttpServletRequest request) throws IOException, SQLException {
        String DATE_FORMAT = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        // today
        Calendar c1 = Calendar.getInstance();
        String today = sdf.format(c1.getTime());
        String file_name = "" + folio + today + "";
        response.setContentType("application/vnd.ms-excel");
        response.addHeader("Content-Disposition", "inline; filename=\"" + file_name + ".xls\";");
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet hs = wb.createSheet();
        HSSFRow fila = hs.createRow(0);
        HSSFCell celda;
        String[] valuesFolio = request.getParameterValues("folio");
        String[] valuesCLC = request.getParameterValues("clc");
        String[] valuesCXP = request.getParameterValues("cxp");
        String[] valuesEP = request.getParameterValues("ep");
        String[] valuesMes = request.getParameterValues("mes");
        String[] valuesImporte = request.getParameterValues("importe");
        String[] valuesLinea = request.getParameterValues("lc");
        String[] valuesClave = request.getParameterValues("clv");
        String[] valuesFicha = request.getParameterValues("ficha");
        String[] valuesCXPNomina = request.getParameterValues("cxpnomina");
        String[] valuesTipoConcepto = request.getParameterValues("tipoConcepto");
        String[] valuesTipoMovimiento = request.getParameterValues("tipoMovimiento");
        String tipoTramite = request.getParameter("tipotramite");
        celda = fila.createCell(0);
        celda.setCellValue("FOLIO");
        celda = fila.createCell(1);
        celda.setCellValue("CLC");
        celda = fila.createCell(2);
        celda.setCellValue("CXP");
        celda = fila.createCell(3);
        celda.setCellValue("EP");
        celda = fila.createCell(4);
        celda.setCellValue("MES");
        celda = fila.createCell(5);
        celda.setCellValue("IMPORTE");
        celda = fila.createCell(6);
        if ("1".equals(tipoTramite) || "3".equals(tipoTramite))
            celda.setCellValue("LINEA DE CAPTURA");
        else if ("4".equals(tipoTramite))
            celda.setCellValue("CXP NOMINA");
        celda = fila.createCell(7);
        if ("1".equals(tipoTramite) || "3".equals(tipoTramite))
            celda.setCellValue("CLAVE RASTREO");
        else if ("4".equals(tipoTramite))
            celda.setCellValue("TIPO CONCEPTO");
        celda = fila.createCell(8);
        if ("1".equals(tipoTramite) || "3".equals(tipoTramite))
            celda.setCellValue("FICHA DE DEPOSITO");
        else if ("4".equals(tipoTramite))
            celda.setCellValue("TIPO MOVIMIENTO");
        celda = fila.createCell(9);
        if ("3".equals(tipoTramite))
            celda.setCellValue("CXP NOMINA");
        celda = fila.createCell(10);
        if ("3".equals(tipoTramite))
            celda.setCellValue("TIPO CONCEPTO");
        celda = fila.createCell(11);
        if ("3".equals(tipoTramite))
            celda.setCellValue("TIPO MOVIMIENTO");
        for (int i = 0; i < valuesCLC.length; i++) {
            fila = hs.createRow(i + 1);
            celda = fila.createCell(0);
            celda.setCellValue(valuesFolio[i]);
            celda = fila.createCell(1);
            celda.setCellValue(valuesCLC[i]);
            celda = fila.createCell(2);
            celda.setCellValue(valuesCXP[i].trim());
            celda = fila.createCell(3);
            celda.setCellValue(valuesEP[i]);
            celda = fila.createCell(4);
            celda.setCellValue(valuesMes[i]);
            celda = fila.createCell(5);
            celda.setCellValue(valuesImporte[i]);
            celda = fila.createCell(6);
            if ("1".equals(tipoTramite) || "3".equals(tipoTramite))
                celda.setCellValue(valuesLinea[i]);
            else if ("4".equals(tipoTramite))
                celda.setCellValue(valuesCXPNomina[i].trim());
            celda = fila.createCell(7);
            if ("1".equals(tipoTramite) || "3".equals(tipoTramite))
                celda.setCellValue(valuesClave[i]);
            else if ("4".equals(tipoTramite))
                celda.setCellValue(valuesTipoConcepto[i].trim());
            celda = fila.createCell(8);
            if ("1".equals(tipoTramite) || "3".equals(tipoTramite))
                celda.setCellValue(valuesFicha[i]);
            else if ("4".equals(tipoTramite))
                celda.setCellValue(valuesTipoMovimiento[i].trim());
            celda = fila.createCell(9);
            if ("3".equals(tipoTramite))
                celda.setCellValue(valuesCXPNomina[i].trim());
            celda = fila.createCell(10);
            if ("3".equals(tipoTramite))
                celda.setCellValue(valuesTipoConcepto[i].trim());
            celda = fila.createCell(11);
            if ("3".equals(tipoTramite))
                celda.setCellValue(valuesTipoMovimiento[i].trim());
        }
        wb.write(response.getOutputStream());
        wb.close();
    }

    public void generaCasoInserts(HttpServletResponse response, HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("../index.jsp");
            return;
        }
        Caso cRein = null;
        FolioGeneratorInterface fg = null;
        try {
            ClassLoader cl = getClass().getClassLoader();
            Class<?> clase = cl.loadClass(folioGenerator);
            fg = (FolioGeneratorInterface) clase.newInstance();
        } catch (ClassNotFoundException exc) {
            log.error("Generador de folios", exc);
            throw new ServletException(exc);
        } catch (InstantiationException exc) {
            log.error("Generador de folios", exc);
            throw new ServletException(exc);
        } catch (IllegalAccessException exc) {
            log.error("Generador de folios", exc);
            throw new ServletException(exc);
        }
        Usuario u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        cRein = generaCaso(u, 50, fg, "VENTANILLA_REINTEGROANEXO1", "Reintegro Anexo1");
        int idCasoReintegro = new Integer(cRein.getFolio().substring(cRein.getFolio().lastIndexOf('-') + 1)).intValue();
        String folioCasoReintegro = cRein.getFolio();
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fecha = sdf.format(date);
        String[] valuesCLC = request.getParameterValues("clc");
        String[] valuesCXP = request.getParameterValues("cxp");
        String[] valuesEP = request.getParameterValues("ep");
        String[] valuesMes = request.getParameterValues("mes");
        String[] valuesImporte = request.getParameterValues("importe");
        String[] valuesCentroContableCxP = request.getParameterValues("centrocontablecxp");
        ReintegroEncabezadoMil re = new ReintegroEncabezadoMil();
        re.setObservaciones(new String(request.getParameter("observaciones").replaceAll("\r\n", "").replaceAll("\n", "").getBytes("ISO-8859-1"), "UTF-8"));
        re.setConcepto(new String(request.getParameter("concepto").replaceAll("\r\n", "").replaceAll("\n", "").getBytes("ISO-8859-1"), "UTF-8"));
        re.setMovimiento(request.getParameter("CatMovimientoReintegro"));
        re.setTipoAviso(request.getParameter("CatTipoCausaAvisoReintegro"));
        re.setCausaAviso(request.getParameter("CatCausaAvisoReintegro"));
        re.setFormaDePago(request.getParameter("CatFormaPagoAvisoReintegro"));
        re.setaEjercicioFiscal(adecProy.obtenEjercicioFiscal());
        re.setcCentroContable(u.getPropiedad("CCENTROCONTABLE").getValor());
        re.setcRamo(u.getU_Ramo());
        re.setcUnidadResponsableContable("RHQ");
        re.setcTipoPoliza("IN");
        re.setLc("N/A");
        re.setcUnidadResponsable(u.getU_UR());
        re.setnFolioReintegro(idCasoReintegro);
        re.setfSolicitud(fecha);
        re.setClvRastreo("N/A");
        re.setFichaDeposito("N/A");
        re.setClvBanco("N/A");
        re.setCuentaBancaria("N/A");
        ArrayList<ReintegroDetalleMil> rd = new ArrayList<ReintegroDetalleMil>();
        ReintegroDetalleMil rdaux;
        double sumaImportes = 0;
        for (int i = 0; i < valuesCLC.length; i++) {
            rdaux = new ReintegroDetalleMil();
            rdaux.setnSIAFF(valuesCLC[i]);
            rdaux.setCxp(valuesCXP[i]);
            rdaux.setEP(valuesEP[i]);
            rdaux.setMes(Integer.parseInt(valuesMes[i]));
            rdaux.setmImporteCLC(Double.parseDouble(valuesImporte[i]));
            rdaux.setnDocRenglon(String.valueOf(i + 1));
            rdaux.setRenglonPagado(String.valueOf(reintegro.getNDocRenglon(rdaux.getCxp(), rdaux.getEP(), rdaux.getMes(), 0)[0]));
            rdaux.setcCentroContable(valuesCentroContableCxP[i]);
            rdaux.setAlm(reintegro.getALM(rdaux.getCxp(), rdaux.getEP(), rdaux.getMes(), 0, Integer.parseInt(rdaux.getnDocRenglon())));
            rdaux.setcPartida(reintegro.getcPartida(rdaux.getEP()));
            rdaux.setSecCLC(String.valueOf(reintegro.secCLC(String.valueOf(rdaux.getnSIAFF()).replace(".0", ""), rdaux.getEP(), 0)));
            rdaux.setFolioDependenciaSicop(reintegro.folioDependencia(String.valueOf(rdaux.getnSIAFF()).replace(".0", ""), rdaux.getEP(), 0));
            rdaux.setRfc(reintegro.getRFC(rdaux.getCxp(), rdaux.getEP()));
            rdaux.setcEvento("REIN_ANX1");
            rdaux.setObgt(rdaux.getEP().substring(31, 36));
            sumaImportes += rdaux.getmImporteCLC();
            rd.add(rdaux);
        }
        re.setImporteLC(String.valueOf(sumaImportes));
        reintegro.insertaReintegro(re, rd, idCasoReintegro, folioCasoReintegro, u);
        CasoBusinessLogic casoTx = new CasoBusinessLogic(jniName);
        ITree tree = casoTx.getArbolCaso(cRein);
        session.setAttribute(GestionInterface.ATT_TREE, tree);
        session.setAttribute(GestionInterface.ATT_CASE, cRein);
        response.sendRedirect("../caso/exec-container.jsp");
    }

    private Caso generaCaso(Usuario u, int idTCaso, FolioGeneratorInterface fg, String opResponsable, String concepto) throws GestionException, SQLException {
        CasoBusinessLogic casoTx = new CasoBusinessLogic(jniName);
        Caso c = casoTx.IniciaCaso(u, idTCaso, fg);
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fecha = sdf.format(date);
        // Variables del caso
        c.getCasoDato("FOLIO").setValor(c.getFolio());
        c.getCasoDato("OPERADOR").setValor(u.getLogin());
        c.getCasoDato("FECHA_DOCUMENTO").setValor(fecha);
        c.getCasoDato("EJERCICIO_FISCAL").setValor(adecProy.obtenEjercicioFiscal());
        c.getCasoDato("CONCEPTO_MOV").setValor(concepto);
        c.getCasoDato("MONEDA").setValor("MXP");
        Map<String, String> m = new HashMap<String, String>();
        m.put("FOLIO", c.getFolio());
        m.put("OPERADOR", u.getLogin());
        m.put("FECHA_DOCUMENTO", fecha);
        m.put("EJERCICIO_FISCAL", adecProy.obtenEjercicioFiscal());
        m.put("CONCEPTO_MOV", "Reintegro Presupuestal");
        m.put("MONEDA", "MXP");
        c.setIdGabinete(casoTx.creaExpediente(u.getLogin(), c));
        // Guarda las variables de caso.
        c = casoTx.actualizaCasoDato(c, m);
        // Cambia el usuario al grupo ventanilla para que aparezca en el inbox.
        CasoOperacion co = ((CasoOperacion) c.getCasoOperacion().get(0));
        CasoOperacionBusinessLogic cobl = new CasoOperacionBusinessLogic(jniName);
        cobl.updateCasoResponsable(co, opResponsable);
        return c;
    }
}
