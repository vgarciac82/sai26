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
import com.jenkov.prizetags.tree.itf.ITree;
import com.syc.contable.AdecuacionBusinessLogic;
import com.syc.contable.RectificacionPresupuestariaBusinessLogic;
import com.syc.contable.RectificacionPresupuestariaMilBusinessLogic;
import com.syc.contable.core.CLCNoPagadaException;
import com.syc.contable.core.Rectificacion;
import com.syc.contable.core.URInaccesibleException;
import com.syc.fortimax.core.DocumentoBussinessLogic;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoDatoManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;
import java.nio.file.Paths;

@WebServlet(name = "RectificacionPresupuestariaServlet", urlPatterns = { "/gstnmngr/RectificacionPresupuestaria" })
public class RectificacionPresupuestariaServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(RectificacionPresupuestariaServlet.class);

    String mensaje = "";

    // para
    AdecuacionBusinessLogic adecProy = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);

    // obtener
    // el
    // ejercicio
    // fiscal
    // en
    // diferentes
    // funciones
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
        // sirve para saber si en el finally redirijo
        // a rectificacionprespuestal si se está
        // haciendo la búsqueda manual
        boolean busqueda = false;
        Rectificacion clcFiltrada = null;
        RectificacionPresupuestariaBusinessLogic recPresBL = new RectificacionPresupuestariaBusinessLogic(GestionInterface.ATT_CONEXION);
        RectificacionPresupuestariaMilBusinessLogic rpmbl = new RectificacionPresupuestariaMilBusinessLogic(GestionInterface.ATT_CONEXION);
        String generaExcel = request.getParameter("generaExcel");
        String borraTodo = request.getParameter("borraTodo");
        /*
		 * SE GENERA EL EXCEL DE CAPTURA (CARGA)
		 */
        if (generaExcel != null && !"".equals(generaExcel) && "1".equals(generaExcel)) {
            try {
                creaExcelRectificacionCarga("Rectificacion", response, request);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else /*
		 * SE GENERA EL EXCEL DE CAPTURA (CARGA)
		 */
        if (generaExcel != null && !"".equals(generaExcel) && "2".equals(generaExcel)) {
            try {
                creaExcelRectificacionCargaMil("RectificacionMil", response, request);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            /*
			 * ESTO BORRA DE LA BASE DE DATOS LOS REGISTROS Y LIMPIA EL ARCHIVO
			 * PARA SUBIR UNA RECTIFICACION NUEVA EN EL MISMO FOLIO
			 */
        } else if (borraTodo != null && !"".equals(borraTodo) && "1".equals(borraTodo)) {
            try {
                DocumentoBussinessLogic dbl = new DocumentoBussinessLogic();
                String folio = request.getParameter("folioRectificacion");
                rpmbl.borraRectificacionMil(Integer.parseInt(folio));
                dbl.limpiaDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 2, 1);
                CasoBusinessLogic casoTx = new CasoBusinessLogic("jdbc/gestion");
                ITree tree = casoTx.getArbolCaso(c);
                session.setAttribute("tree.model", tree);
                response.sendRedirect(basePath + "plantillasCasos/rectificacionPresupuestalMil.jsp");
            } catch (SQLException e) {
                response.sendRedirect(basePath + "plantillasCasos/rectificacionPresupuestalMil.jsp");
                e.printStackTrace();
            } catch (Exception e) {
                response.sendRedirect(basePath + "plantillasCasos/rectificacionPresupuestalMil.jsp");
                e.printStackTrace();
            }
            /*
			 * APARTADO RECTIFICACION MIL
			 */
        } else if (request.getParameter("aplica") != null && request.getParameter("aplica").equals("1")) {
            try {
                PrintWriter out = response.getWriter();
                Map<String, String> m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
                String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
                rpmbl.actualizaFechaAplicacion(folioR);
                out.println(rpmbl.aplicaRectificacion(folioR, usuario, adecProy.obtenEjercicioFiscal(), usuario.getU_Ramo(), usuario.getU_UR(), c, usuario.getPropiedad("CCENTROCONTABLE").getValor(), c.getCasoDato("FECHA_AP_CONT").getValor(), m, prefixPath, usuario.getLogin()));
            } catch (Exception ex) {
                log.warn(ex.getMessage(), ex);
            }
        } else if (request.getParameter("aplica") != null && request.getParameter("aplica").equals("2")) {
            // AUTORIZACION
            // RECTIFICACION
            // MIL
            try {
                PrintWriter out = response.getWriter();
                Map<String, String> m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
                String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
                rpmbl.actualizaFechaAplicacion(folioR);
                out.println(rpmbl.autorizaRectificacion(c, m, prefixPath, usuario.getLogin(), usuario));
            } catch (Exception ex) {
                log.warn(ex.getMessage(), ex);
            }
            /* CAPTURA MANUAL DE LA RECTIFICACION */
        } else {
            try {
                busqueda = true;
                String folioSICOP = (request.getParameter("folioSICOP") == null) ? " " : request.getParameter("folioSICOP").trim();
                String CXP = (request.getParameter("CXP") == null) ? " " : request.getParameter("CXP").trim();
                String contrarrecibo = (request.getParameter("contrarrecibo") == null) ? " " : request.getParameter("contrarrecibo").trim();
                session.setAttribute("folioSICOP", folioSICOP);
                session.setAttribute("CXP", CXP);
                try {
                    if (usuario.getGrupo("AUTORIZADOR_RECTIFICACION") != null)
                        clcFiltrada = recPresBL.filtrar(folioSICOP, "GERENCIA", CXP, contrarrecibo);
                    else
                        clcFiltrada = recPresBL.filtrar(folioSICOP, usuario.getU_UR(), CXP, contrarrecibo);
                } catch (CLCNoPagadaException e) {
                    mensaje = "La CXP " + CXP + " no se encuentra en estatus de pagada";
                } catch (URInaccesibleException e) {
                    mensaje = e.getMessage();
                }
                if (clcFiltrada != null) {
                    session.setAttribute("RECTIFICACION", clcFiltrada);
                    mensaje = "B&uacute;squeda completa";
                } else {
                    mensaje = "No se pudo determinar el registro con el que se va a trabajar,intente con ambos filtros o consulte con el administrador";
                }
                session.setAttribute("mensaje", mensaje);
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
                mensaje = e.toString();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                mensaje = e.toString();
            } finally {
                if (busqueda)
                    response.sendRedirect(basePath + "plantillasCasos/rectificacionPresupuestal.jsp?busqueda=SI");
            }
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
            /*
			 * System.out.println("Renglon " + i ); System.out.print(" clc: " +
			 * valuesCLC[i]); System.out.print(" cxp: " +valuesCXP[i]);
			 * System.out.print(" ep: " +valuesEP[i]); System.out.print(" mes: "
			 * +valuesMes[i]); System.out.print(" impore: " +valuesImporte[i]);
			 * System.out.println("");
			 */
        }
        wb.write(response.getOutputStream());
    }

    public void creaExcelRectificacionCargaMil(String folio, HttpServletResponse response, HttpServletRequest request) throws IOException, SQLException {
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
        String[] valuesTipoConcepto = request.getParameterValues("tipoConcepto");
        String[] valuesTipoMovimiento = request.getParameterValues("tipoMovimiento");
        String[] valuesEPDebe = request.getParameterValues("epDebe");
        String[] valuesMesDebe = request.getParameterValues("mesDebe");
        String[] valuesImporteDebe = request.getParameterValues("importeDebe");
        String[] valuesConcepto = request.getParameterValues("concepto");
        String cuentaPorPagar = request.getParameter("cuentaPorPagar");
        String cuentaPorPagarCLC = request.getParameter("cuentaPorPagarCLC");
        String nFolioSICOP = request.getParameter("nFolioSICOP");
        String nFolioSIAFF = request.getParameter("nFolioSIAFF");
        String tipoRectificacion = request.getParameter("tipoRectificacion");
        // String tipoConcepto = request.getParameter("tipoConcepto");
        double totalDice = 0;
        int terminaDices = 0;
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
        celda.setCellValue("CUENTA POR PAGAR SAI");
        celda = fila.createCell(9);
        celda.setCellValue("CUENTA POR PAGAR CLC");
        // celda = fila.createCell(9);
        // celda.setCellValue("TIPO CONCEPTO");
        celda = fila.createCell(10);
        celda.setCellValue("FOLIO DEPENDECIA CLC");
        celda = fila.createCell(11);
        celda.setCellValue("FOLIO SIAFF");
        celda = fila.createCell(12);
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
        celda.setCellValue(totalDice);
        celda = fila.createCell(5);
        celda.setCellValue(valuesTipoMovimiento[0]);
        celda = fila.createCell(6);
        //
        celda.setCellValue("1");
        celda = fila.createCell(7);
        //
        celda.setCellValue("OREN.A03.");
        celda = fila.createCell(8);
        celda.setCellValue(cuentaPorPagar);
        celda = fila.createCell(9);
        celda.setCellValue(cuentaPorPagarCLC);
        // celda = fila.createCell(9);
        // celda.setCellValue(tipoConcepto);
        celda = fila.createCell(10);
        celda.setCellValue(nFolioSICOP);
        celda = fila.createCell(11);
        celda.setCellValue(nFolioSIAFF);
        celda = fila.createCell(12);
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
        celda = fila.createCell(5);
        celda.setCellValue("TIPO CONCEPTO");
        celda = fila.createCell(6);
        celda.setCellValue("TIPO MOVIMIENTO");
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
            celda = fila.createCell(5);
            celda.setCellValue(valuesTipoConcepto[i]);
            celda = fila.createCell(6);
            celda.setCellValue(valuesTipoMovimiento[i]);
            terminaDices++;
            /*
			 * System.out.println("Renglon " + i ); System.out.print(" clc: " +
			 * valuesCLC[i]); System.out.print(" cxp: " +valuesCXP[i]);
			 * System.out.print(" ep: " +valuesEP[i]); System.out.print(" mes: "
			 * +valuesMes[i]); System.out.print(" impore: " +valuesImporte[i]);
			 * System.out.println("");
			 */
        }
        for (int i = 0; i < valuesEPDebe.length; i++) {
            fila = hs.createRow(i + 5 + terminaDices);
            celda = fila.createCell(0);
            celda.setCellValue(String.valueOf(i + 1));
            celda = fila.createCell(1);
            celda.setCellValue("DEBE");
            celda = fila.createCell(2);
            celda.setCellValue(valuesEPDebe[i]);
            celda = fila.createCell(3);
            celda.setCellValue(valuesMesDebe[i]);
            celda = fila.createCell(4);
            celda.setCellValue(valuesImporteDebe[i]);
            celda = fila.createCell(5);
            celda.setCellValue(valuesConcepto[i].trim());
            celda = fila.createCell(6);
            celda.setCellValue("N/A");
        }
        wb.write(response.getOutputStream());
    }
}
