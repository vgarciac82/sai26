package com.syc.contable.servlet;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.syc.contable.AnteProyectoBusinessLogic;
import com.syc.contable.core.AnteProyectoAut;
import com.syc.contable.core.AnteProyectoAutCalendario;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;
import java.nio.file.Paths;

@WebServlet(name = "CalendarioAnteProyectoAut", urlPatterns = { "/gstnmngr/CalendarioAP" })
public class CalendarioAnteProyectoAut extends HttpServlet {

    /**
     */
    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(CalendarioAnteProyectoAut.class);

    /**
     * Constructor of the object.
     */
    public CalendarioAnteProyectoAut() {
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
     * @param request the request send by the client to the server
     * @param response the response send by the server to the client
     * @throws ServletException if an error occurred
     * @throws IOException if an error occurred
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /*response.setContentType("text/html");
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
		out.close();*/
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
        AnteProyectoBusinessLogic anteProy = new AnteProyectoBusinessLogic(GestionInterface.ATT_CONEXION);
        DecimalFormat df1 = new DecimalFormat("#.0000");
        String pathURL = request.getContextPath();
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + pathURL + "/";
        if (request.getParameter("accion") != null && "1".equals(request.getParameter("accion"))) {
            //LA ACCION SIRVE PARA VER SI SE ENTREGA O PROCESA EL EXCEL
            String folio = request.getParameter("folioc");
            /**
             * ********EL RESPONSE PARA QUE NOS ENTREGUE EL EXCEL PARA GUARDAR O ABRIR EL EXPLORADOR********
             */
            response.setContentType("application/vnd.ms-excel");
            response.addHeader("Content-Disposition", "inline; filename=\"CalendarioAnteP" + folio + ".xls\";");
            /**
             * *******************************************************************************
             */
            String UR = (String) session.getAttribute("UR");
            if (request.getParameter("cUniEjecutora") != null && request.getParameter("cUniEjecutora") != null)
                UR = request.getParameter("cUniEjecutora");
            ArrayList<AnteProyectoAut> antes = null;
            try {
                antes = anteProy.getCAnteProyectoAut(UR);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Workbook wb = new HSSFWorkbook();
            Sheet sheet = wb.createSheet();
            int j;
            Row fila;
            Cell celda;
            fila = sheet.createRow(0);
            celda = fila.createCell(0);
            celda.setCellValue("nConsecutivo");
            celda = fila.createCell(1);
            celda.setCellValue("Folio");
            celda = fila.createCell(2);
            celda.setCellValue("FolioAut");
            celda = fila.createCell(3);
            celda.setCellValue("Clave SIAFF");
            celda = fila.createCell(4);
            celda.setCellValue("Clave Interna");
            celda = fila.createCell(5);
            celda.setCellValue("Unidad Responsable");
            celda = fila.createCell(6);
            celda.setCellValue("aEjercicioFiscal");
            celda = fila.createCell(7);
            celda.setCellValue("Monto Anual");
            celda = fila.createCell(8);
            celda.setCellValue("Enero");
            celda = fila.createCell(9);
            celda.setCellValue("Febrero");
            celda = fila.createCell(10);
            celda.setCellValue("Marzo");
            celda = fila.createCell(11);
            celda.setCellValue("Abril");
            celda = fila.createCell(12);
            celda.setCellValue("Mayo");
            celda = fila.createCell(13);
            celda.setCellValue("Junio");
            celda = fila.createCell(14);
            celda.setCellValue("Julio");
            celda = fila.createCell(15);
            celda.setCellValue("Agosto");
            celda = fila.createCell(16);
            celda.setCellValue("Septiembre");
            celda = fila.createCell(17);
            celda.setCellValue("Octubre");
            celda = fila.createCell(18);
            celda.setCellValue("Noviembre");
            celda = fila.createCell(19);
            celda.setCellValue("Diciembre");
            AnteProyectoAut ante;
            for (j = 1; j < antes.size(); j++) {
                fila = sheet.createRow(j);
                ante = antes.get(j - 1);
                /**
                 * ****************EMPEZAMOS A ESCRIBIR EN LAS CELDAS***************
                 */
                celda = fila.createCell(0);
                celda.setCellValue(ante.getD_NConsecutivo());
                celda = fila.createCell(1);
                celda.setCellValue(ante.getD_NFolioAnteProyecto());
                celda = fila.createCell(2);
                celda.setCellValue(ante.getFolioAnteProyectoAut());
                celda = fila.createCell(3);
                celda.setCellValue(ante.getD_CClaveSiaff());
                celda = fila.createCell(4);
                celda.setCellValue(ante.getD_CClaveInterna());
                celda = fila.createCell(5);
                celda.setCellValue(ante.getD_CUnidadEjecutora());
                celda = fila.createCell(6);
                celda.setCellValue(ante.getEjercicioFiscal());
                celda = fila.createCell(7);
                celda.setCellValue(ante.getD_MCalculado());
                celda = fila.createCell(8);
                celda.setCellValue(0);
                celda = fila.createCell(9);
                celda.setCellValue(0);
                celda = fila.createCell(10);
                celda.setCellValue(0);
                celda = fila.createCell(11);
                celda.setCellValue(0);
                celda = fila.createCell(12);
                celda.setCellValue(0);
                celda = fila.createCell(13);
                celda.setCellValue(0);
                celda = fila.createCell(14);
                celda.setCellValue(0);
                celda = fila.createCell(15);
                celda.setCellValue(0);
                celda = fila.createCell(16);
                celda.setCellValue(0);
                celda = fila.createCell(17);
                celda.setCellValue(0);
                celda = fila.createCell(18);
                celda.setCellValue(0);
                celda = fila.createCell(19);
                celda.setCellValue(0);
                /**
                 * ****************************************************************
                 */
            }
            wb.write(response.getOutputStream().toPath());
            wb.close();
        } else if (request.getParameter("accion") != null && "2".equals(request.getParameter("accion"))) {
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
            ArrayList<AnteProyectoAutCalendario> antes = new ArrayList<AnteProyectoAutCalendario>();
            ArrayList<AnteProyectoAutCalendario> fallidos = new ArrayList<AnteProyectoAutCalendario>();
            boolean inserta = true;
            boolean montos = false;
            boolean existe = false;
            boolean repetido = false;
            try {
                InputStream inp = new FileInputStream(cbl.getArchivosDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 2, 1)[0].getAbsolutePath());
                HSSFWorkbook wb = new HSSFWorkbook(inp);
                HSSFSheet sheet = wb.getSheetAt(0);
                Iterator<Row> rowIterator = sheet.rowIterator();
                int fila = 0;
                //para saltarnos la primera línea que es de encabezado y no queremos tomarla en cuenta para crear el objeto
                rowIterator.next();
                while (rowIterator.hasNext()) {
                    fila++;
                    int celda = 0;
                    HSSFRow hssfRow = (HSSFRow) rowIterator.next();
                    Iterator<Cell> cellIterator = hssfRow.cellIterator();
                    AnteProyectoAutCalendario anac = new AnteProyectoAutCalendario();
                    String montoTotal = "";
                    BigDecimal sumaTotal = new BigDecimal(0);
                    while (cellIterator.hasNext()) {
                        String valor = valorCelda(cellIterator);
                        /**
                         * **********VALIDACION MONTO TOTAL IGUAL A SUMA DE LOS MESES**********************
                         */
                        if (celda == 7) {
                            montoTotal = valor;
                        } else if (celda >= 8 && celda <= 19)
                            sumaTotal = sumaTotal.add(new BigDecimal(valor));
                        /**
                         * *****************************************************
                         */
                        //Seteamos el objeto para guardarlo y recuperarlo después para hacer el insert//
                        if (//El Consecutivo
                        celda == 0)
                            anac.setNConsecutivo(valor);
                        if (//El Folio
                        celda == 1)
                            anac.setNFolioAnteProyecto(valor);
                        if (//El Folio Aut
                        celda == 2)
                            anac.setNFolioAnteProyectoAut(valor);
                        if (//Clave SIAFF
                        celda == 3)
                            anac.setCClaveSiaff(valor);
                        if (//Clave Interna
                        celda == 4)
                            anac.setCClaveInterna(valor);
                        if (//Unidad Responsable
                        celda == 5)
                            anac.setCUnidadResponsable(valor);
                        if (//Ejercicio Fiscal
                        celda == 6)
                            anac.setAEjercicioFiscal(valor);
                        if (//Monto Anual Autorizado
                        celda == 7)
                            anac.setMAnualAutorizado(valor);
                        if (//Monto Enero
                        celda == 8)
                            anac.setMEnero(valor);
                        if (//Monto Febrero
                        celda == 9)
                            anac.setMFebrero(valor);
                        if (//Monto Marzo
                        celda == 10)
                            anac.setMMarzo(valor);
                        if (//Monto Abril
                        celda == 11)
                            anac.setMAbril(valor);
                        if (//Monto Mayo
                        celda == 12)
                            anac.setMMayo(valor);
                        if (//Monto Junio
                        celda == 13)
                            anac.setMJunio(valor);
                        if (//Monto Julio
                        celda == 14)
                            anac.setMJulio(valor);
                        if (//Monto Agosto
                        celda == 15)
                            anac.setMAgosto(valor);
                        if (//Monto Septiembre
                        celda == 16)
                            anac.setMSeptiembre(valor);
                        if (//Monto Octubre
                        celda == 17)
                            anac.setMOctubre(valor);
                        if (//Monto Noviembre
                        celda == 18)
                            anac.setMNoviembre(valor);
                        if (//Monto Diciembre
                        celda == 19)
                            anac.setMDiciembre(valor);
                        celda++;
                    }
                    if (String.valueOf(df1.format(sumaTotal)).equals(montoTotal)) {
                        //Checamos si los importes son iguales
                        montos = true;
                    } else {
                        inserta = false;
                        log.debug("Object: {}", "Los montos no coinciden para la fila " + fila + " " + String.valueOf(sumaTotal));
                    }
                    if (anteProy.getExisteDetalleAut(anac)) {
                        existe = true;
                    } else {
                        inserta = false;
                        log.debug("Object: {}", "No toda la información existe o fue alterada para la fila " + fila);
                    }
                    if (anteProy.getRepetidoCalendario(anac)) {
                        repetido = true;
                    }
                    if (!inserta) {
                        if (anac != null) {
                            //por lo pronto se validan los montos aunque falta la de existencia de ep y demás.
                            fallidos.add(anac);
                        }
                    } else if (montos && existe && !repetido) {
                        if (anac != null) {
                            //anteProy.insertaCAnteProyectoAut(anac,request.getParameter("CCENTROCONTABLE"));
                            antes.add(anac);
                        }
                    }
                }
                if (inserta) {
                    anteProy.insertaCAnteProyectoAut(antes, request.getParameter("CCENTROCONTABLE"));
                }
                wb.close();
            } catch (GestionException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                response.sendRedirect(basePath + "plantillasCasos/calendarizacionAnteProyecto.jsp");
            }
        }
    }

    private String valorCelda(Iterator<Cell> cellIterator) {
        String valor = null;
        HSSFCell hssfCell = (HSSFCell) cellIterator.next();
        if (hssfCell.getCellType() == CellType.NUMERIC) {
            if (HSSFDateUtil.isCellDateFormatted(hssfCell)) {
                Date date = HSSFDateUtil.getJavaDate(hssfCell.getNumericCellValue());
                DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
                valor = dateFormatter.format(date);
            } else {
                if (!String.valueOf(hssfCell.getNumericCellValue()).equals("") && String.valueOf(hssfCell.getNumericCellValue()) != null) {
                    valor = String.valueOf(hssfCell.getNumericCellValue());
                }
            }
        } else {
            if (!hssfCell.getStringCellValue().equals("") && hssfCell.getStringCellValue() != null) {
                valor = hssfCell.getStringCellValue();
            }
        }
        return valor;
    }

    /**
     * Initialization of the servlet. <br>
     *
     * @throws ServletException if an error occurs
     */
    public void init() throws ServletException {
        // Put your code here
    }
}
