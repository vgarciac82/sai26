package com.syc.contable.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import com.syc.contable.AdecuacionBusinessLogic;
import com.syc.contable.RectificacionAnexo1BusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoDatoManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;
import java.nio.file.Paths;

@WebServlet(name = "RectificacionAnexo1Servlet", urlPatterns = { "/gstnmngr/RectificacionAnexo1" })
public class RectificacionAnexo1Servlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(RectificacionAnexo1Servlet.class);

    String mensaje = "";

    // para obtener el ejercicio fiscal en diferentes funciones
    AdecuacionBusinessLogic adecProy = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * jakarta.servlet.http.HttpServlet#doPost(jakarta.servlet.http.HttpServletRequest
	 * , jakarta.servlet.http.HttpServletResponse)
	 */
    @SuppressWarnings("unchecked")
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("../index.jsp");
            return;
        }
        Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
        Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        if (usuario == null) {
            response.sendRedirect("../index.jsp");
            return;
        }
        String pathURL = request.getContextPath();
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + pathURL + "/";
        int folioR = 0;
        if (c != null) {
            folioR = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
        }
        // sirve para saber si en el finally redirijo a rectificacionprespuestal si se está haciendo la búsqueda manual
        RectificacionAnexo1BusinessLogic rectificaAnexo1BL = new RectificacionAnexo1BusinessLogic(GestionInterface.ATT_CONEXION);
        //APARTADO RECTIFICACION MIL
        try {
            PrintWriter out = response.getWriter();
            Map<String, String> m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
            String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
            rectificaAnexo1BL.actualizaFechaAplicacion(folioR);
            out.println(rectificaAnexo1BL.aplicaRectificacion(folioR, usuario, adecProy.obtenEjercicioFiscal(), usuario.getU_Ramo(), usuario.getU_UR(), c, usuario.getPropiedad("CCENTROCONTABLE").getValor(), c.getCasoDato("FECHA_AP_CONT").getValor(), m, prefixPath, usuario.getLogin()));
        } catch (Exception ex) {
            ex.printStackTrace();
            log.warn(ex.getMessage(), ex);
        }
    }

    public void creaExcelRectificacionCarga(String folio, HttpServletResponse response, HttpServletRequest request) throws IOException, SQLException {
        HttpSession session = request.getSession();
        Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        String file_name = "" + folio + "";
        response.setContentType("application/vnd.ms-excel");
        response.addHeader("Content-Disposition", "inline; filename=\"" + file_name + "Carga.xls\";");
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet hs = wb.createSheet();
        HSSFRow fila = hs.createRow(0);
        String[] valuesEP = request.getParameterValues("ep");
        String[] valuesMes = request.getParameterValues("mes");
        String[] valuesImporte = request.getParameterValues("importe");
        String cuentaPorPagar = request.getParameter("cuentaPorPagar");
        String nFolioSICOP = request.getParameter("nFolioSICOP");
        String nFolioSIAFF = request.getParameter("nFolioSIAFF");
        String tipoRectificacion = request.getParameter("tipoRectificacion");
        double totalDice = 0;
        for (int i = 0; i < valuesImporte.length; i++) {
            totalDice += Double.valueOf(valuesImporte[i]);
        }
        HSSFCell celda = fila.createCell(0);
        celda.setCellValue("EJERCICIO");
        celda = fila.createCell(1);
        celda.setCellValue("RAMO");
        celda = fila.createCell(2);
        celda.setCellValue("CLAVE UNIDAD ADMINISTRATIVA");
        celda = fila.createCell(3);
        celda.setCellValue("TOTAL DICE");
        celda = fila.createCell(4);
        celda.setCellValue("TOTAL DEBE");
        celda = fila.createCell(5);
        celda.setCellValue("MOVIMIENTO");
        celda = fila.createCell(6);
        celda.setCellValue("ORIGEN PRESUPUESTO");
        celda = fila.createCell(7);
        celda.setCellValue("CONTROL INTERNO");
        celda = fila.createCell(8);
        celda.setCellValue("CUENTA POR PAGAR");
        celda = fila.createCell(9);
        celda.setCellValue("FOLIO DEPENDECIA CLC");
        celda = fila.createCell(10);
        celda.setCellValue("FOLIO SIAFF");
        celda = fila.createCell(11);
        celda.setCellValue("TIPO RECTIFICACION");
        fila = hs.createRow(1);
        celda = fila.createCell(0);
        celda.setCellValue(adecProy.obtenEjercicioFiscal());
        celda = fila.createCell(1);
        celda.setCellValue(usuario.getU_Ramo());
        celda = fila.createCell(2);
        celda.setCellValue(usuario.getU_UR());
        celda = fila.createCell(3);
        celda.setCellValue(totalDice);
        celda = fila.createCell(4);
        celda.setCellValue("0");
        celda = fila.createCell(5);
        celda.setCellValue("N/A");
        celda = fila.createCell(6);
        celda.setCellValue("N/A");
        celda = fila.createCell(7);
        celda.setCellValue("N/A");
        celda = fila.createCell(8);
        celda.setCellValue(cuentaPorPagar);
        celda = fila.createCell(9);
        celda.setCellValue(nFolioSICOP);
        celda = fila.createCell(10);
        celda.setCellValue(nFolioSIAFF);
        celda = fila.createCell(11);
        celda.setCellValue(tipoRectificacion);
        fila = hs.createRow(2);
        celda = fila.createCell(0);
        celda.setCellValue("CONCEPTO");
        fila = hs.createRow(3);
        celda = fila.createCell(0);
        celda.setCellValue("N/A");
        fila = hs.createRow(4);
        celda = fila.createCell(0);
        celda.setCellValue("RENGLON");
        celda = fila.createCell(1);
        celda.setCellValue("DICE/DEBE");
        celda = fila.createCell(2);
        celda.setCellValue("EP");
        celda = fila.createCell(3);
        celda.setCellValue("MES");
        celda = fila.createCell(4);
        celda.setCellValue("IMPORTE");
        for (int i = 0; i < valuesEP.length; i++) {
            fila = hs.createRow(i + 5);
            celda = fila.createCell(0);
            celda.setCellValue(String.valueOf(i + 1));
            celda = fila.createCell(1);
            celda.setCellValue("DICE");
            celda = fila.createCell(2);
            celda.setCellValue(valuesEP[i]);
            celda = fila.createCell(3);
            celda.setCellValue(valuesMes[i]);
            celda = fila.createCell(4);
            celda.setCellValue(valuesImporte[i]);
        }
        wb.write(response.getOutputStream());
        wb.close();
    }
}
