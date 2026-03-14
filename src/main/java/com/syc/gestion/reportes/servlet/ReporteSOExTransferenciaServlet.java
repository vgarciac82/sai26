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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
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
//import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.Number;
//import jxl.write.NumberFormat;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletFileUpload;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.jfree.util.Log;
import com.syc.gestion.reportes.core.InformeSemestralBean;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.servlet.GestionFileReceiverServlet;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;
import org.apache.commons.fileupload2.core.DiskFileItemFactory;

// fin del metodo
@WebServlet(name = "ReporteSOExTransferencia", urlPatterns = { "/gstnmngr/ReporteSOExTransferencia" })
public class ReporteSOExTransferenciaServlet extends HttpServlet {

    /**
     */
    private static final long serialVersionUID = 1997502821486656613L;

    DataSource dataSource;

    public ReporteSOExTransferenciaServlet() {
        super();
    }

    public void destroy() {
        super.destroy();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    private static Logger log = LoggerFactory.getLogger(GestionFileReceiverServlet.class);

    private String jniName = null;

    private String tempDir = null;

    private String jndiName = null;

    Statement stm = null;

    ResultSet rs = null;

    Connection conn = null;

    //Variable donde se guardara el query que llama a la funcion
    private String query_sp = null;

    // registros, 13 columnas
    String[][] registros = new String[150][5];

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

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            datosPlantilla(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void datosPlantilla(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException, ClassNotFoundException, ParseException {
        DiskFileItemFactory factory = DiskFileItemFactory.builder().get();
        JakartaServletFileUpload upload = new JakartaServletFileUpload(factory);
        cFileExcel = upload.getRepositoryPath() + "\\" + "ReporteSOExTransferencia.xls";
        String soe = request.getParameter("ParamRep");
        //procedimiento para la consulta a base de datos
        query_sp = "execute sp_ReporteSOExTransferencia '" + soe + "'";
        llenaExcel(cFileExcel, query_sp, soe, response);
    }

    public void llenaExcel(String plantilla, String query, String nSoe, HttpServletResponse response) throws ServletException, IOException, SQLException, ClassNotFoundException, ParseException {
        response.setContentType("application/vnd.ms-excel");
        response.addHeader("Content-Disposition", "attachment; filename=\"reporteSOETransfer_" + System.currentTimeMillis() + ".xls\";");
        log.info("Object: {}", "plantilla: " + plantilla);
        log.info("Object: {}", "query detalle: " + query);
        conn = DataSourceManager.getConnection(jndiName);
        stm = conn.createStatement();
        try {
            //stm=conn.createStatement();
            rs = stm.executeQuery(query);
            nFil = 0;
            while (rs.next()) {
                registros[nFil][0] = rs.getString(1);
                registros[nFil][1] = rs.getString(2);
                registros[nFil][2] = rs.getString(3);
                registros[nFil][3] = rs.getString(4);
                nFil++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            conn.close();
        }
        try {
            String etiqueta;
            log.info("Inica Escritura en excel");
            log.info("Object: {}", "plantilla para la escritura: " + plantilla);
            log.info("Object: {}", "numeros de fila:" + nFil);
            // DATOS DEL LIBRO DE EXCEL
            Workbook workbook = Workbook.getWorkbook(new File(plantilla).getAbsoluteFile());
            WritableWorkbook copy = Workbook.createWorkbook(response.getOutputStream(), workbook);
            WritableSheet HojaxEstado = copy.getSheet(0);
            WritableSheet HojaxCategoria = copy.getSheet(1);
            WritableSheet HojaResumen = copy.getSheet(2);
            int fila = 0;
            int columna = 0;
            //FORMATOS PARA LAS CELDAS
            //NumberFormat formNum = new NumberFormat("#,##0.00");
            //Formato  Principal
            WritableFont arial12n = new WritableFont(WritableFont.ARIAL, 12);
            WritableCellFormat arialFormat = new WritableCellFormat(arial12n);
            arialFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
            WritableFont arial12nc = new WritableFont(WritableFont.ARIAL, 12);
            arial12nc.setBoldStyle(WritableFont.BOLD);
            WritableCellFormat arialFormatc = new WritableCellFormat(arial12nc);
            arialFormatc.setAlignment(Alignment.CENTRE);
            Label label;
            /*AQUI SE LLENAN LOS DATOS PARA LA HOJA XESTADOS*/
            String xEst = "select dEntidadFederativa, ISNULL(SUM(ImporteFactura),0)Federal ,0 Contraparte,ISNULL(SUM(ImporteFactura),0)+0 total,ISNULL" + "(SUM(ImporteFactura),0) Transferido  from  tCatalogoEntidadFederativa  tef with(nolock) left join dSOETransfer dt with(nolock) " + " on dt.cEntidadFed=convert(int,tef.cEntidadFederativa) and numero_soe='" + nSoe + "' group by dEntidadFederativa";
            fila = 5;
            // System.out.println("Query= "+xEst);
            double valornum = 0;
            Number number = null;
            try {
                //stm=conn.createStatement();
                rs = stm.executeQuery(xEst);
                int nRenglon = 1;
                while (rs.next()) {
                    valornum = (double) Double.parseDouble("" + nRenglon);
                    number = new Number(1, fila, valornum);
                    number.setCellFormat(arialFormat);
                    number.getCellFormat();
                    HojaxEstado.addCell(number);
                    label = new Label(2, fila, rs.getString(1));
                    label.setCellFormat(arialFormat);
                    label.getCellFormat();
                    HojaxEstado.addCell(label);
                    valornum = (double) Double.parseDouble(rs.getString(2));
                    number = new Number(3, fila, valornum);
                    number.setCellFormat(arialFormat);
                    number.getCellFormat();
                    HojaxEstado.addCell(number);
                    valornum = (double) Double.parseDouble(rs.getString(3));
                    number = new Number(4, fila, valornum);
                    number.setCellFormat(arialFormat);
                    number.getCellFormat();
                    HojaxEstado.addCell(number);
                    valornum = (double) Double.parseDouble(rs.getString(4));
                    number = new Number(5, fila, valornum);
                    number.setCellFormat(arialFormat);
                    number.getCellFormat();
                    HojaxEstado.addCell(number);
                    valornum = (double) Double.parseDouble(rs.getString(5));
                    number = new Number(6, fila, valornum);
                    number.setCellFormat(arialFormat);
                    number.getCellFormat();
                    HojaxEstado.addCell(number);
                    fila++;
                    nRenglon++;
                    System.out.println();
                }
                // fin del while
            }//Fin del try
             catch (SQLException e) {
                e.printStackTrace();
            }
            /*AQUI SE LLENAN LOS DATOS PARA LA HOJA XCategoria*/
            String xCat = "select cartera,dCartera,SUM(ImporteFactura)importe from dSOETransfer dt inner join tCatalogoCartera tc on dt.cartera=tc.cCartera" + " and numero_soe='" + nSoe + "' group by cartera,dCartera ";
            fila = 5;
            System.out.println("Query= " + xEst);
            Calendar gc = GregorianCalendar.getInstance();
            SimpleDateFormat fecha = new SimpleDateFormat("EEEE d' de 'MMMM' del 'yyyy", new Locale("es", "MX"));
            String fechaFormateada = fecha.format(gc.getTime()).toUpperCase();
            try {
                //conn=DataSourceManager.getConnection(jndiName);
                //stm=conn.createStatement();
                rs = stm.executeQuery(xCat);
                int nRenglon = 1;
                label = new Label(1, 3, fechaFormateada);
                label.setCellFormat(arialFormatc);
                label.getCellFormat();
                HojaxCategoria.addCell(label);
                label = new Label(1, 1, nSoe);
                label.setCellFormat(arialFormatc);
                label.getCellFormat();
                HojaxCategoria.addCell(label);
                while (rs.next()) {
                    label = new Label(1, fila, rs.getString(1));
                    label.setCellFormat(arialFormat);
                    label.getCellFormat();
                    HojaxCategoria.addCell(label);
                    label = new Label(2, fila, rs.getString(2) + "");
                    label.setCellFormat(arialFormat);
                    label.getCellFormat();
                    HojaxCategoria.addCell(label);
                    valornum = (double) Double.parseDouble(rs.getString(3));
                    number = new Number(3, fila, valornum);
                    number.setCellFormat(arialFormat);
                    number.getCellFormat();
                    HojaxCategoria.addCell(number);
                    fila++;
                    nRenglon++;
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
            label = null;
            log.info("Escribiendo en la plantilla...");
            for (int i = 0; i < nFil; i++) {
                for (int j = 0; j <= 3; j++) ;
                {
                    columna = (int) registros[i][2].charAt(0) - 65;
                    fila = ((int) Integer.parseInt(registros[i][1]) - 1);
                    if (registros[i][0].equals("Resumen")) {
                        etiqueta = registros[i][3];
                        label = new Label(columna, fila, etiqueta);
                        label.setCellFormat(arialFormat);
                        label.getCellFormat();
                        if (isNumeric(registros[i][3]) == true) {
                            valornum = (double) Double.parseDouble(registros[i][3]);
                            number = new Number(columna, fila, valornum);
                            number.setCellFormat(arialFormat);
                            number.getCellFormat();
                            HojaResumen.addCell(number);
                        } else {
                            etiqueta = registros[i][3];
                            label.setCellFormat(arialFormat);
                            label.getCellFormat();
                            HojaResumen.addCell(label);
                        }
                    }
                }
            }
            copy.write();
            copy.close();
            log.info("Escritura realizada con exito");
        } catch (Exception eP) {
            log.info(eP.getMessage(), eP);
            eP.printStackTrace();
        }
    }

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
