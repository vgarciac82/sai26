package com.syc.contable.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletFileUpload;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.syc.contable.AnteProyectoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionFileReceiverServlet;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;
import org.apache.commons.fileupload2.core.DiskFileItemFactory;
import java.nio.file.Paths;

@WebServlet(name = "AnteProyectoLayoutServlet", urlPatterns = { "/gstnmngr/AnteProyectoLayoutServlet" })
public class LayoutAnteProyectoServlet extends HttpServlet {

    /**
     * Constructor of the object.
     */
    public LayoutAnteProyectoServlet() {
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
        try {
            creaXls(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialization of the servlet. <br>
     *
     * @throws ServletException if an error occurs
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

    public void creaXls(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
        AnteProyectoBusinessLogic antProy = new AnteProyectoBusinessLogic(GestionInterface.ATT_CONEXION);
        HttpSession session = request.getSession(false);
        //BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(response.getOutputStream()));
        //DecimalFormat formateadorDecimal = new DecimalFormat("###.##");
        //DecimalFormat formateadorCero = new DecimalFormat("#");
        String cUnidadResponsable = "";
        ArrayList<ArrayList<String>> arrAnteProyecto = null;
        ArrayList<Object> arrmDatosAnteProyecto = new ArrayList<>();
        boolean cGrupoUSR = false;
        Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
        Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        if (usuario == null) {
            response.sendRedirect("../index.jsp");
            return;
        }
        cGrupoUSR = (usuario.getGrupos() != null && usuario.getGrupos().containsKey("AUTORIZADOR_ANTEPROYECTO")) ? true : false;
        if (!cGrupoUSR) {
            cUnidadResponsable = usuario.getU_UR();
        } else {
            cUnidadResponsable = request.getParameter("cUnidadResponsable");
        }
        if (session.getAttribute("objcUniEjecutora") != null)
            cUnidadResponsable = (String) session.getAttribute("objcUniEjecutora");
        try {
            arrAnteProyecto = antProy.RecuperaAnteProyecto(cUnidadResponsable, c);
        } finally {
        }
        DiskFileItemFactory factory = DiskFileItemFactory.builder().get();
        //determina el directorio temporal
        JakartaServletFileUpload upload = new JakartaServletFileUpload(factory);
        //
        String file_name = c.getFolio();
        int iPaso = c.getCasoOperacion(0).getIdOperacion();
        response.setContentType("application/vnd.ms-excel");
        response.addHeader("Content-Disposition", "inline; filename=\"AnteProyecto_" + file_name + ".xls\"; charset=UTF-8\" pageEncoding=\"utf-8\"");
        // crear archivo
        String cFileExcel = getServletContext().getRealPath("/upload") + "/plantillaAnteProyecto.xls";
        InputStream inp = new FileInputStream(cFileExcel);
        //selecciona la primer hoja del excel
        Workbook wb = new HSSFWorkbook(inp);
        Sheet sheet = wb.getSheetAt(0);
        if (sheet == null) {
            sheet = wb.createSheet();
        }
        //Inserta el folio SAI dentro de la primera hoja (FAP01) dentro de excel
        Row filaUnidadSai = sheet.getRow(0);
        if (filaUnidadSai == null) {
            filaUnidadSai = sheet.createRow(0);
        }
        // declara campos para recuperar datos del detalle del ante proyecto
        String cUnidadArch = "";
        int nConsecutivo = 0;
        int nRowxls = 1;
        String cEP = "";
        //Double mMontoCalculado=0.0;
        //Double mMontoOptimo=0.0;
        //Double mMontoIreductible=0.0;
        String mMontoCalculado = "";
        String mMontoOptimo = "";
        String mMontoIreductible = "";
        int nPorcentajeReduccion = 0;
        int nPorcentajeIncremento = 0;
        String cPorcentajeReduccion = "";
        String cPorcentajeIncremento = "";
        String cRechazo = "";
        //se agrega el encavezado
        Row filaDetalleSaih = sheet.getRow(nRowxls);
        if (filaDetalleSaih == null) {
            filaDetalleSaih = sheet.createRow(nRowxls);
        }
        Cell celdaUnidadSaiHc = filaUnidadSai.getCell(0);
        if (celdaUnidadSaiHc == null) {
            celdaUnidadSaiHc = filaUnidadSai.createCell(0);
        }
        celdaUnidadSaiHc.setCellValue("Consecutivo");
        Cell celdaUnidadSaiHep = filaUnidadSai.getCell(1);
        if (celdaUnidadSaiHep == null) {
            celdaUnidadSaiHep = filaUnidadSai.createCell(1);
        }
        celdaUnidadSaiHep.setCellValue("Clave EP");
        Cell celdaUnidadSaiHm1 = filaUnidadSai.getCell(2);
        if (celdaUnidadSaiHm1 == null) {
            celdaUnidadSaiHm1 = filaUnidadSai.createCell(2);
        }
        celdaUnidadSaiHm1.setCellValue("Monto Calculado");
        Cell celdaUnidadSaiHm2 = filaUnidadSai.getCell(3);
        if (celdaUnidadSaiHm2 == null) {
            celdaUnidadSaiHm2 = filaUnidadSai.createCell(3);
        }
        celdaUnidadSaiHm2.setCellValue("Monto Optimo");
        Cell celdaUnidadSaiHm3 = filaUnidadSai.getCell(4);
        if (celdaUnidadSaiHm3 == null) {
            celdaUnidadSaiHm3 = filaUnidadSai.createCell(4);
        }
        celdaUnidadSaiHm3.setCellValue("Monto Ireductible");
        Cell celdaUnidadSaiHm4 = filaUnidadSai.getCell(5);
        if (celdaUnidadSaiHm4 == null) {
            celdaUnidadSaiHm4 = filaUnidadSai.createCell(5);
        }
        celdaUnidadSaiHm4.setCellValue("Porcentaje de Reduccion");
        Cell celdaUnidadSaiHm5 = filaUnidadSai.getCell(6);
        if (celdaUnidadSaiHm5 == null) {
            celdaUnidadSaiHm5 = filaUnidadSai.createCell(6);
        }
        celdaUnidadSaiHm5.setCellValue("Porcentaje de Incremento");
        if (iPaso == 2 || iPaso == 4) {
            Cell celdaUnidadSaiHm6 = filaUnidadSai.getCell(7);
            if (celdaUnidadSaiHm6 == null) {
                celdaUnidadSaiHm6 = filaUnidadSai.createCell(7);
            }
            celdaUnidadSaiHm6.setCellValue("Motivo de Rechazo");
        }
        //detalle
        for (int i = 0; i < arrAnteProyecto.size(); i++) {
            arrmDatosAnteProyecto = (ArrayList) arrAnteProyecto.get(i);
            //new Integer(request.getParameter("id_oper")).intValue();
            Row filaDetalleSai = sheet.getRow(nRowxls + i);
            if (filaDetalleSai == null) {
                filaDetalleSai = sheet.createRow(nRowxls + i);
            }
            nConsecutivo = (Integer) arrmDatosAnteProyecto.get(0);
            cUnidadArch = (String) arrmDatosAnteProyecto.get(1);
            cEP = (String) arrmDatosAnteProyecto.get(2);
            mMontoCalculado = (String) arrmDatosAnteProyecto.get(3);
            mMontoOptimo = (String) arrmDatosAnteProyecto.get(4);
            mMontoIreductible = (String) arrmDatosAnteProyecto.get(5);
            cPorcentajeReduccion = (String) arrmDatosAnteProyecto.get(6);
            cPorcentajeIncremento = (String) arrmDatosAnteProyecto.get(7);
            cRechazo = (String) arrmDatosAnteProyecto.get(8);
            Cell celdaConsecSai = filaDetalleSai.createCell(0);
            Cell celdaEPSai = filaDetalleSai.createCell(1);
            Cell celdamCalculadoSai = filaDetalleSai.createCell(2);
            Cell celdamOptimoSai = filaDetalleSai.createCell(3);
            Cell celdamIreductibleSai = filaDetalleSai.createCell(4);
            Cell celdaiDismSai = filaDetalleSai.createCell(5);
            Cell celdaiReducSai = filaDetalleSai.createCell(6);
            Cell celdaiRechazoSai = filaDetalleSai.createCell(7);
            celdaConsecSai.setCellValue(String.valueOf(nConsecutivo));
            celdaEPSai.setCellValue(cEP);
            celdamCalculadoSai.setCellValue(mMontoCalculado);
            celdamOptimoSai.setCellValue(mMontoOptimo);
            celdamIreductibleSai.setCellValue(mMontoIreductible);
            celdaiDismSai.setCellValue(String.valueOf(nPorcentajeReduccion));
            celdaiReducSai.setCellValue(String.valueOf(nPorcentajeIncremento));
            if (iPaso == 3) {
                celdaiRechazoSai.setCellValue(String.valueOf(cRechazo));
            }
        }
        wb.write(response.getOutputStream());
        wb.close();
        antProy.marcaAutoImport(c);
    }
}
