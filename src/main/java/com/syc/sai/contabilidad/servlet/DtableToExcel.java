package com.syc.sai.contabilidad.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@WebServlet(name = "GeneraExcelTable", urlPatterns = { "/reports/GeneraExcelTable" })
public class DtableToExcel extends HttpServlet implements GestionInterface {

    private static final Logger log = LoggerFactory.getLogger(DtableToExcel.class);

    private static final long serialVersionUID = -5034769853645642993L;

    protected ArrayList<ArrayList<String>> Todo = null;

    protected String cCentroContable;

    protected int INICIANDATOS = 6;

    protected int numHojas = 1;

    protected int[] numColsxTabla = null;

    protected String[] nombreTitulos = null;

    private static XSSFCellStyle estiloCabecera = null;

    private static XSSFCellStyle estiloTitulo = null;

    private static XSSFCellStyle estiloNumerico = null;

    private static Calendar gc = GregorianCalendar.getInstance();

    private SimpleDateFormat fecha = new SimpleDateFormat("EEEE d' de 'MMMM' del 'yyyy", new Locale("es", "MX"));

    private String fechaFormateada = fecha.format(gc.getTime()).toUpperCase();

    protected ArrayList<String> encabezado = null;

    protected ArrayList<String> encabezadoSaldos = null;

    protected boolean fechaRight = false;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            sendError(response, "Session Terminada. Ingrese nuevamente al sistema");
            return;
        }
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null) {
            sendError(response, "No hay usuario en session. Ingrese nuevamente al sistema");
            return;
        }
        cCentroContable = u.getPropiedad("CCENTROCONTABLE").getValor();
        getInput(request);
        try {
            generarExcel(response, request);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    protected void setHojas(String hojas) {
        try {
            this.numHojas = Integer.parseInt(hojas);
        } catch (NumberFormatException e) {
            log.error(e.getMessage(), e);
        }
    }

    protected void setHojas(int hojas) {
        this.numHojas = hojas;
    }

    protected void setnumCols(String[] numCols) {
        try {
            this.numColsxTabla = new int[numHojas];
            for (int i = 0; i < numCols.length; i++) {
                this.numColsxTabla[i] = Integer.parseInt(numCols[i]);
            }
        } catch (NumberFormatException e) {
            log.error(e.getMessage(), e);
        }
    }

    protected void setnumCols(int[] numCols) {
        this.numColsxTabla = numCols;
    }

    protected void setTitulos(String[] tituloHojas) {
        this.nombreTitulos = tituloHojas;
    }

    protected String[] creaArregloVacio(int Cols) {
        String[] titulos = new String[Cols];
        for (int i = 0; i < Cols; i++) titulos[i] = "";
        return titulos;
    }

    protected String[] creaArregloconNum(int Cols, String patron) {
        String[] titulos = new String[Cols];
        for (int i = 0; i < Cols; i++) titulos[i] = patron + "" + i;
        return titulos;
    }

    protected void iniciar() {
        Todo = new ArrayList<ArrayList<String>>();
        encabezadoSaldos = new ArrayList<String>();
        encabezado = new ArrayList<String>();
        encabezado.add("COMISION NACIONAL FORESTAL");
        encabezado.add("SISTEMA DE ADMINISTRACION INTEGRAL");
        encabezado.add("FINANCIEROS");
        encabezado.add("CENTRO CONTABLE " + cCentroContable);
        encabezado.add(fechaFormateada);
        encabezado.add("");
    }

    private void getInput(HttpServletRequest request) {
        iniciar();
        if (request.getParameter("tTablas") == null)
            setHojas(1);
        else
            setHojas(request.getParameter("tTablas"));
        setnumCols(request.getParameter("colsxTabla").split("\\|"));
        if (request.getParameter("titulosxTabla").split("\\|") == null)
            setTitulos(creaArregloconNum(numHojas, "Hoja "));
        else
            setTitulos(request.getParameter("titulosxTabla").split("\\|"));
        int HojaActual;
        for (HojaActual = 0; HojaActual < numHojas; HojaActual++) {
            if (request.getParameter("encabezadosTabla" + (HojaActual)) == null)
                Todo.add(new ArrayList<String>(Arrays.asList(creaArregloVacio(numColsxTabla[HojaActual]))));
            else
                Todo.add(new ArrayList<String>(Arrays.asList(request.getParameter("encabezadosTabla" + (HojaActual)).split("\\|"))));
            if (request.getParameter("formatosTabla" + (HojaActual)) == null)
                Todo.add(new ArrayList<String>(Arrays.asList(creaArregloVacio(numColsxTabla[HojaActual]))));
            else
                Todo.add(new ArrayList<String>(Arrays.asList(request.getParameter("formatosTabla" + (HojaActual)).split("\\|"))));
            if (request.getParameter("tabla" + (HojaActual)) == null)
                Todo.add(new ArrayList<String>(Arrays.asList(creaArregloVacio(numColsxTabla[HojaActual]))));
            else
                Todo.add(new ArrayList<String>(Arrays.asList(request.getParameter("tabla" + (HojaActual)).split("\\|"))));
        }
    }

    protected void generarExcel(HttpServletResponse response, HttpServletRequest request) throws UnsupportedEncodingException, Exception {
        XSSFWorkbook wb = new XSSFWorkbook();
        setEstiloCabecera(wb);
        setEstiloTitulo(wb);
        setEstiloNumerico(wb);
        XSSFSheet[] hoja = null;
        int HojaActual;
        HojaActual = 0;
        int ContadorCols = 0;
        int in = 0;
        hoja = new XSSFSheet[numHojas];
        XSSFRow fila;
        XSSFCell celda;
        int ContadorFilas;
        int filaSaldos;
        try {
            for (int i = 0; i < Todo.size(); i += 3) {
                ContadorFilas = 0;
                hoja[HojaActual] = wb.createSheet(nombreTitulos[HojaActual]);
                // hoja[HojaActual].addMergedRegion(new
                // CellRangeAddress(0,INICIANDATOS-1,0,numColsxTabla[HojaActual]-1));
                for (ContadorFilas = 0; ContadorFilas < INICIANDATOS; ContadorFilas++) {
                    fila = hoja[HojaActual].createRow(ContadorFilas);
                    celda = fila.createCell(5);
                    celda.setCellStyle(getEstiloTitulo());
                    celda.setCellValue(encabezado.get(ContadorFilas));
                }
                if (fechaRight) {
                    fila = hoja[HojaActual].getRow(1);
                    celda = fila.createCell(8);
                    celda.setCellStyle(getEstiloTitulo());
                    /*
					 * fecha = new SimpleDateFormat("dd-MM-yyyy",new
					 * Locale("es","MX"));
					 * fechaFormateada=fecha.format(gc.getTime());
					 */
                    String DATE_FORMAT = "dd/MM/yyyy";
                    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
                    // today
                    Calendar c1 = Calendar.getInstance();
                    String fechaFormateada = sdf.format(c1.getTime());
                    celda.setCellValue(fechaFormateada);
                }
                if (encabezadoSaldos.size() > 0) {
                    fila = hoja[HojaActual].getRow(2);
                    celda = fila.createCell(11);
                    celda.setCellStyle(getEstiloTitulo());
                    celda.setCellValue("Saldo Inicial");
                    fila = hoja[HojaActual].getRow(3);
                    celda = fila.createCell(11);
                    celda.setCellStyle(getEstiloTitulo());
                    celda.setCellValue("Cargos");
                    fila = hoja[HojaActual].getRow(4);
                    celda = fila.createCell(11);
                    celda.setCellStyle(getEstiloTitulo());
                    celda.setCellValue("Abonos");
                    fila = hoja[HojaActual].getRow(5);
                    celda = fila.createCell(11);
                    celda.setCellStyle(getEstiloTitulo());
                    celda.setCellValue("Saldo Final");
                    filaSaldos = 0;
                    for (filaSaldos = 0; filaSaldos < 4; filaSaldos++) {
                        fila = hoja[HojaActual].getRow(filaSaldos + 2);
                        celda = fila.createCell(12);
                        celda.setCellStyle(getEstiloNumerico());
                        celda.setCellType(CellType.NUMERIC);
                        celda.setCellValue(Double.parseDouble(encabezadoSaldos.get(filaSaldos)));
                    }
                }
                fila = hoja[HojaActual].createRow(ContadorFilas);
                for (in = 0; in < numColsxTabla[HojaActual]; in++) {
                    celda = fila.createCell(in);
                    celda.setCellStyle(getEstiloCabecera());
                    celda.setCellValue(new String(Todo.get(i).get(in).getBytes("ISO-8859-1"), "UTF-8"));
                }
                ++ContadorFilas;
                ContadorCols = 0;
                fila = hoja[HojaActual].createRow(ContadorFilas);
                //aqui
                for (in = 0; in < Todo.get(i + 2).size(); in++) {
                    celda = fila.createCell(ContadorCols);
                    if ((Todo.get(i + 1).get(ContadorCols).compareTo("money") == 0) || (Todo.get(i + 1).get(ContadorCols).compareTo("float") == 0) || (Todo.get(i + 1).get(ContadorCols).compareTo("Double") == 0)) {
                        celda.setCellStyle(getEstiloNumerico());
                        celda.setCellType(CellType.NUMERIC);
                        try {
                            celda.setCellValue(Double.parseDouble((Todo.get(i + 2).get(in).compareTo("") == 0 ? "0" : Todo.get(i + 2).get(in))));
                        } catch (NumberFormatException e) {
                            celda.setCellValue(Todo.get(i + 2).get(in));
                        }
                    } else
                        celda.setCellValue(Todo.get(i + 2).get(in));
                    // celda.setCellValue(new
                    // String(Todo.get(i+2).get(in).getBytes("Cp1252"),"UTF-8"));
                    // celda.setCellValue(new
                    // String(Todo.get(i+2).get(in).getBytes("UTF-8"),"ISO-8859-1"));
                    ++ContadorCols;
                    if (ContadorCols == numColsxTabla[HojaActual]) {
                        ++ContadorFilas;
                        fila = hoja[HojaActual].createRow(ContadorFilas);
                        ContadorCols = 0;
                    }
                }
                // *********************//
                // Reajustando columnas //
                // *********************//
                // System.out.println("Reajustando Columnas>> "+numColsxTabla[HojaActual]);
                for (in = 0; in < numColsxTabla[HojaActual]; in++) try {
                    hoja[HojaActual].autoSizeColumn(in);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
                try {
                    FileInputStream archivo;
                    File logo = new File(getServletContext().getRealPath("/").toString() + "imagenes\\logotipo-usuario.png");
                    byte[] buffer = new byte[(int) logo.length()];
                    archivo = new FileInputStream(logo);
                    archivo.read(buffer);
                    int idImagen = wb.addPicture(buffer, HSSFWorkbook.PICTURE_TYPE_PNG);
                    CreationHelper helper = wb.getCreationHelper();
                    Drawing drawing = hoja[HojaActual].createDrawingPatriarch();
                    ClientAnchor anchor = helper.createClientAnchor();
                    anchor.setRow1(1);
                    anchor.setCol1(0);
                    Picture pict = drawing.createPicture(anchor, idImagen);
                    pict.resize();
                    // pict.getPreferredSize().getCol1();
                    // pict.getPreferredSize().getRow1();
                    archivo.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
                HojaActual++;
            }
        } catch (IllegalArgumentException iaex) {
            try {
                /*
				 * VGC20151014 Se agrega try-catch para que si excede las lineas
				 * del excel () se genere como CSV
				 */
                String file = generaCSV(request, response);
                Util.doDownload(response, file, "ReportExportCSV.csv", "text/csv");
                return;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        try {
            response.setContentType("application/vnd.ms-excel");
            response.addHeader("Content-Disposition", "attachment; filename=\"reporte" + "rpt" + "_" + System.currentTimeMillis() + ".xls\";");
            wb.write(response.getOutputStream());
            response.getOutputStream().flush();
            response.getOutputStream().close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            Todo = null;
            numColsxTabla = null;
            nombreTitulos = null;
            wb = null;
            System.gc();
        }
    }

    public String generaCSV(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return Util.arrayToCSV(this.Todo.get(0), this.Todo.get(2));
    }

    private static void setEstiloCabecera(XSSFWorkbook wb) {
        estiloCabecera = wb.createCellStyle();
        estiloCabecera.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
        estiloCabecera.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        XSSFFont font = wb.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        estiloCabecera.setFont(font);
        estiloCabecera.setBorderBottom(BorderStyle.HAIR);
        estiloCabecera.setBorderTop(BorderStyle.HAIR);
        estiloCabecera.setBorderRight(BorderStyle.HAIR);
        estiloCabecera.setBorderLeft(BorderStyle.HAIR);
    }

    private static void setEstiloTitulo(XSSFWorkbook wb) {
        estiloTitulo = wb.createCellStyle();
        // estilo.setFillForegroundColor(HSSFColor.DARK_BLUE.index);
        // estilo.setFillPattern(HSSFFillPatternType.SOLID_FOREGROUND);
        estiloTitulo.setAlignment(HorizontalAlignment.CENTER);
        XSSFFont font = wb.createFont();
        font.setBold(true);
        // font.setColor(HSSFColor.WHITE.index);
        estiloTitulo.setFont(font);
    }

    private static void setEstiloNumerico(XSSFWorkbook wb) {
        estiloNumerico = wb.createCellStyle();
        XSSFDataFormat formato = wb.createDataFormat();
        estiloNumerico.setDataFormat(formato.getFormat("###,###,##0.00"));
    }

    private static XSSFCellStyle getEstiloNumerico() {
        return estiloNumerico;
    }

    private static XSSFCellStyle getEstiloCabecera() {
        return estiloCabecera;
    }

    private static XSSFCellStyle getEstiloTitulo() {
        return estiloTitulo;
    }

    protected static void sendError(HttpServletResponse resp, String msg) throws IOException {
        PrintWriter out = resp.getWriter();
        out.println("<html>");
        out.println("\t<body>");
        out.println("\t\t<h1>Se presento el siguiente problema mientras se llenaba el reporte</h1><br>");
        out.println("\t\t<br>" + msg + "<br>");
        out.println("\t</body>");
        out.println("</html>");
        out.flush();
        out.close();
    }
}
