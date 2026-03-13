package com.syc.gestion.reportes.servlet;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.NumberFormat;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.log4j.Logger;
import com.syc.gestion.reportes.core.InformeSemestralBean;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.servlet.GestionFileReceiverServlet;
import jakarta.servlet.annotation.WebServlet;

// fin del metodo
@WebServlet(name = "InfoSemestralServlet", urlPatterns = { "/gstnmngr/InformeSemestral" })
public class InformeSemestralServlet extends HttpServlet {

    DataSource dataSource;

    public InformeSemestralServlet() {
        super();
    }

    public void destroy() {
        super.destroy();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    private static Logger log = Logger.getLogger(GestionFileReceiverServlet.class);

    private String jniName = null;

    private String tempDir = null;

    private String jndiName = null;

    Statement stm = null;

    ResultSet rs = null;

    Connection conn = null;

    //Variable donde se guardara el query que llama a la funcion
    private String query_sp = null;

    // registros, 13 columnas
    String[][] registros = new String[350][5];

    //nombre de la plantilla
    String cFileExcel = null;

    int nFil;

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
        tempDir = config.getInitParameter("tempDir");
        if (tempDir == null) {
            tempDir = config.getServletContext().getRealPath("/") + File.separator + "upload" + File.separator;
            File fDir = new File(tempDir);
            if (!fDir.exists())
                if (!fDir.mkdirs())
                    throw new ServletException("No se pudo crear el directorio " + tempDir);
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            if (request.getParameter("tipo_rep").equals("1")) {
                //
                datosPlantilla(request, response, request.getParameter("tipo_rep"));
            }
            if (request.getParameter("tipo_rep").equals("2")) {
                //
                datosPlantilla(request, response, request.getParameter("tipo_rep"));
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void datosPlantilla(HttpServletRequest request, HttpServletResponse response, String tReporte) throws ServletException, IOException, SQLException, ClassNotFoundException, ParseException {
        DiskFileUpload upload = new DiskFileUpload();
        upload.setRepositoryPath(tempDir);
        cFileExcel = upload.getRepositoryPath() + "\\" + "InformeSemestral.xls";
        if (tReporte.equals("1")) {
            //Es Reporte Avanc del ejercido de Contratos GENERAL
            //procedimiento para la consulta a base de datos
            query_sp = "execute sp_getreporteSemReembolso '" + request.getParameter("ParamRep") + "'";
            String aux = null;
            llenaExcel(cFileExcel, query_sp, response);
        }
        if (tReporte.equals("2")) {
            //Es reporte por AREA
        }
        try {
            //doDownload(response,cFileExcel,"InformeSemestral.xls");
        } catch (Exception e) {
            log.error(e, e);
        }
    }

    // Fin del Metodo
    public void llenaExcel(String plantilla, String query, HttpServletResponse response) throws ServletException, IOException, SQLException, ClassNotFoundException, ParseException {
        log.info("plantilla: " + plantilla);
        log.info("query detalle: " + query);
        response.setContentType("application/vnd.ms-excel");
        response.addHeader("Content-Disposition", "attachment; filename=\"infSemestral_" + System.currentTimeMillis() + ".xls\";");
        try {
            conn = DataSourceManager.getConnection(jndiName);
            stm = conn.createStatement();
            rs = stm.executeQuery(query);
            nFil = 0;
            while (rs.next()) {
                InformeSemestralBean infoB = new InformeSemestralBean();
                infoB.setPest(rs.getString("pestana"));
                infoB.setFila(rs.getString("renglon"));
                infoB.setColumna(rs.getString("columna"));
                infoB.setDato(rs.getString("dato"));
                registros[nFil][0] = infoB.getPest();
                registros[nFil][1] = infoB.getFila();
                registros[nFil][2] = infoB.getColumna();
                registros[nFil][3] = infoB.getDato();
                nFil++;
            }
            // fin del while
        }//Fin del try
         catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("cerrando conexion a base de registros", exc);
            }
        }
        try {
            //Inicia el procedimiento para insertar en la plantilla de Excel
            // variable para guardar el dato y pasarlo a la celda
            String etiqueta;
            log.info("Inica Escritura en excel");
            log.info("plantilla para la escritura: " + plantilla);
            log.info("numeros de fila:" + nFil);
            // DATOS DEL LIBRO DE EXCEL
            Workbook workbook = Workbook.getWorkbook(new File(plantilla).getAbsoluteFile());
            WritableWorkbook copy = Workbook.createWorkbook(response.getOutputStream(), workbook);
            //Hoja donde se insertaran solo cadenas
            WritableSheet Datos = copy.getSheet(2);
            //Hoja donde se insertaran cadenas y núneros
            WritableSheet hojaA1 = copy.getSheet(3);
            //Hoja donde se insertaran solo números
            WritableSheet hojaA2 = copy.getSheet(4);
            //Hoja donde se insertaran solo números
            WritableSheet hojaB = copy.getSheet(5);
            //Hoja donde se insertaran solo números
            WritableSheet hojaC = copy.getSheet(6);
            //.
            WritableSheet hoja3A = copy.getSheet(7);
            //..
            WritableSheet hoja3B = copy.getSheet(8);
            //...
            WritableSheet hoja4A = copy.getSheet(9);
            //....
            WritableSheet hoja4B = copy.getSheet(10);
            //.....
            WritableSheet hoja5 = copy.getSheet(11);
            //FORMATOS PARA LAS CELDAS
            // NumberFormat formNum = new NumberFormat("#,##0.00");
            //Formato  Principal
            WritableFont arial12n = new WritableFont(WritableFont.ARIAL, 12);
            WritableCellFormat arialFormat = new WritableCellFormat(arial12n);
            arialFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
            //limpia plantilla de datos anteriores en las filas y columnas especificas
            //------------------------
            for (int i = 0; i < nFil; i++) {
                for (int j = 0; j <= 3; j++) ;
                {
                    int columna = (int) registros[i][2].charAt(0) - 65;
                    int fila = ((int) Integer.parseInt(registros[i][1]) - 1);
                    etiqueta = " ";
                    Label label = new Label(columna, fila, etiqueta);
                    label.setCellFormat(arialFormat);
                    label.getCellFormat();
                    if (registros[i][0].equals("Datos")) {
                        Datos.addCell(label);
                    } else // fin del if pestaña Datos
                    if (registros[i][0].equals("B")) {
                        hojaB.addCell(label);
                    } else //fin del else if de pestaña cadena/num
                    // es pestaña de solo numeros
                    {
                        if (registros[i][0].equals("A-1")) {
                            hojaA1.addCell(label);
                        } else if (registros[i][0].equals("A-2")) {
                            hojaA2.addCell(label);
                        } else if (registros[i][0].equals("C")) {
                            hojaC.addCell(label);
                        } else if (registros[i][0].equals("3a")) {
                            hoja3A.addCell(label);
                        } else if (registros[i][0].equals("3b")) {
                            hoja3B.addCell(label);
                        } else if (registros[i][0].equals("4a")) {
                            hoja4A.addCell(label);
                        } else if (registros[i][0].equals("4b")) {
                            hoja4B.addCell(label);
                        } else //Es hoja5
                        {
                            hoja5.addCell(label);
                        }
                    }
                }
            }
            //------------------------
            log.info("Escribiendo en la plantilla...");
            for (// for para controlar filas
            int i = 0; // for para controlar filas
            i < nFil; // for para controlar filas
            i++) {
                // for para controlar columnas
                for (int j = 0; j <= 3; j++) ;
                {
                    // convertir la letra de la columna a posicion numerica
                    int columna = (int) registros[i][2].charAt(0) - 65;
                    // log.info("posicion de la columna "+columna);
                    //convertir la posicion de la fila a entero
                    int fila = ((int) Integer.parseInt(registros[i][1]) - 1);
                    //log.info("posicion de la fila "+fila);
                    //log.info("valor del primer registro: "+registros[i][0]);
                    if (//pestaña=dato   Si es pestaña de solo cadenas
                    registros[i][0].equals("Datos")) {
                        // log.info("Empieza en la pestaña de DATOS");
                        //posicion donde se encuentra los datos
                        etiqueta = registros[i][3];
                        Label label = new Label(columna, fila, etiqueta);
                        label.setCellFormat(arialFormat);
                        label.getCellFormat();
                        Datos.addCell(label);
                    } else // fin del if pestaña Datos
                    if (//   pestaña=cadena/num    Si es pestaña de cadenas y numeros
                    registros[i][0].equals("B")) {
                        if (//   dato==entero          Checa si el dato es numero
                        isNumeric(registros[i][3]) == true) {
                            // Se convierte el valor a numero
                            double valornum = (double) Double.parseDouble(registros[i][3]);
                            Number number = new Number(columna, fila, valornum);
                            number.setCellFormat(arialFormat);
                            number.getCellFormat();
                            hojaB.addCell(number);
                        } else //Si el dato es cadena
                        {
                            //posicion donde se encuentra los datos
                            etiqueta = registros[i][3];
                            Label label = new Label(columna, fila, etiqueta);
                            label.setCellFormat(arialFormat);
                            label.getCellFormat();
                            hojaB.addCell(label);
                        }
                    } else //fin del else if de pestaña cadena/num
                    // es pestaña de solo numeros
                    {
                        if (registros[i][0].equals("A-1")) {
                            // Se convierte el valor a numero
                            double valornum = (double) Double.parseDouble(registros[i][3]);
                            Number number = new Number(columna, fila, valornum);
                            number.setCellFormat(arialFormat);
                            number.getCellFormat();
                            hojaA1.addCell(number);
                        } else if (registros[i][0].equals("A-2")) {
                            // Se convierte el valor a numero
                            double valornum = (double) Double.parseDouble(registros[i][3]);
                            Number number = new Number(columna, fila, valornum);
                            number.setCellFormat(arialFormat);
                            number.getCellFormat();
                            hojaA2.addCell(number);
                        } else if (registros[i][0].equals("C")) {
                            // Se convierte el valor a numero
                            double valornum = (double) Double.parseDouble(registros[i][3]);
                            Number number = new Number(columna, fila, valornum);
                            number.setCellFormat(arialFormat);
                            number.getCellFormat();
                            hojaC.addCell(number);
                        } else if (registros[i][0].equals("3a")) {
                            // Se convierte el valor a numero
                            double valornum = (double) Double.parseDouble(registros[i][3]);
                            Number number = new Number(columna, fila, valornum);
                            number.setCellFormat(arialFormat);
                            number.getCellFormat();
                            hoja3A.addCell(number);
                        } else if (registros[i][0].equals("3b")) {
                            // Se convierte el valor a numero
                            double valornum = (double) Double.parseDouble(registros[i][3]);
                            Number number = new Number(columna, fila, valornum);
                            number.setCellFormat(arialFormat);
                            number.getCellFormat();
                            hoja3B.addCell(number);
                        } else if (registros[i][0].equals("4a")) {
                            // Se convierte el valor a numero
                            double valornum = (double) Double.parseDouble(registros[i][3]);
                            Number number = new Number(columna, fila, valornum);
                            number.setCellFormat(arialFormat);
                            number.getCellFormat();
                            hoja4A.addCell(number);
                        } else if (registros[i][0].equals("4b")) {
                            // Se convierte el valor a numero
                            double valornum = (double) Double.parseDouble(registros[i][3]);
                            Number number = new Number(columna, fila, valornum);
                            number.setCellFormat(arialFormat);
                            number.getCellFormat();
                            hoja4B.addCell(number);
                        } else //Es hoja5
                        {
                            // Se convierte el valor a numero
                            double valornum = (double) Double.parseDouble(registros[i][3]);
                            Number number = new Number(columna, fila, valornum);
                            number.setCellFormat(arialFormat);
                            number.getCellFormat();
                            hoja5.addCell(number);
                        }
                    }
                    //fin del ultimo else
                }
                // fin for del columnas
            }
            // fin for de filas
            copy.write();
            copy.close();
            log.info("Escritura realizada con exito");
        }//fin del Try
         catch (Exception eP) {
            log.info(eP);
            eP.printStackTrace();
        }
        // Fin del catch
    }

    // Fin del Metodo
    //-----------------------------------------------------
    private static boolean isNumeric(String cadena) {
        try {
            Double.parseDouble(cadena);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    private void doDownload(HttpServletResponse resp, String filename, String original_filename) throws IOException {
        int length = 0;
        File f = new File(filename);
        ServletOutputStream out = resp.getOutputStream();
        ServletContext context = getServletConfig().getServletContext();
        String mimetype = context.getMimeType(original_filename);
        resp.setContentType((mimetype != null) ? mimetype : "application/octet-stream");
        resp.setContentLength((int) f.length());
        resp.addHeader("Content-Disposition", "attachment; filename=\"" + original_filename + "\";");
        // 5K buffer
        byte[] bbuf = new byte[5 * 1024];
        DataInputStream in = new DataInputStream(new FileInputStream(f));
        while ((in != null) && ((length = in.read(bbuf)) != -1)) {
            out.write(bbuf, 0, length);
        }
        in.close();
        out.flush();
        out.close();
    }
}
// fin de la clase
