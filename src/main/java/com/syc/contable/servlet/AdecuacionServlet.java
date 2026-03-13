package com.syc.contable.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import org.json.JSONException;
import org.json.JSONObject;
import com.syc.contable.AdecuacionBusinessLogic;
import com.syc.contable.core.ResultadoSaldos;
import com.syc.contable.core.Saldo;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoDatoManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import jakarta.servlet.annotation.WebServlet;

//import java.sql.Connection;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.HashMap;
//import com.syc.gestion.reportes.core.ReporteManager;
@WebServlet(name = "AdecuacionServlet", urlPatterns = { "/gstnmngr/adecuaPresupuesto", "/gstnmngr/Adecuacion" })
public class AdecuacionServlet extends HttpServlet {

    /**
     */
    private static final long serialVersionUID = 1L;

    private static Logger log = Logger.getLogger(AdecuacionBusinessLogic.class);

    /**
     * Constructor of the object.
     */
    public AdecuacionServlet() {
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

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        PrintWriter out = null;
        JSONObject json = new JSONObject();
        Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
        String adecuacionReserva = "";
        String notificaAdecuacion = "";
        try {
            String action = request.getParameter("action");
            if ("ValidaPreAplicacion".equals(action)) {
                AdecuacionBusinessLogic adecua = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
                adecuacionReserva = request.getParameter("adecuacionReserva");
                if (c != null) {
                    int nFolio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
                    ResultadoSaldos resultadoSaldos = adecua.validaApartado(nFolio, -1);
                    if (resultadoSaldos == null || resultadoSaldos.getError() == null)
                        json.put("status", true);
                    if ("SI".equals(adecuacionReserva))
                        adecua.esReserva(nFolio, adecuacionReserva);
                    else if (resultadoSaldos != null && resultadoSaldos.getError() != null && resultadoSaldos.getError().size() > 0) {
                        json.put("status", false);
                        json.put("error", resultadoSaldos.getError());
                    }
                } else {
                    json.put("status", false);
                    json.put("error", "No se encontro caso en session,  Vuelva a ingresar al sistema");
                }
                response.setContentType("application/json");
                out = response.getWriter();
                out.print(json.toString());
                out.flush();
                out.close();
                return;
            }
            String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
            //Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
            if (c == null) {
                response.sendRedirect("../index.jsp");
                return;
            }
            Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
            ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
            AdecuacionBusinessLogic adecua = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
            boolean esAmbienteDesarrollo = "true".equalsIgnoreCase(cabl.getSystemSetting("AMBIENTE_DESARROLLO"));
            Map m = null;
            String calendario = request.getParameter("nNumCAL");
            String fcalendario = request.getParameter("DPC_fFechaCAL");
            if (!esAmbienteDesarrollo)
                adecua.correoProduccion = true;
            int nFolio = 0;
            if (request.getParameter("accion") != null && "2".equals(request.getParameter("accion"))) {
                creaExcelAdecuacionCarga(response, request);
            } else {
                out = response.getWriter();
                m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
            }
            String mensaje = "";
            String cFechaAplicacion = request.getParameter("fechaAp");
            String nNumMAP = "", fMAP = "";
            String nNumSicop = "", fSicop = "";
            List<String> resultadoAplicacion = null;
            String resultadoCancelacion = "";
            String cSuperReduccion = "";
            String cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
            String cSRInterna = "";
            boolean enviarCorreo = "true".equals(request.getParameter("enviarCorreo"));
            if (request.getParameter("accion") != null && "1".equals(request.getParameter("accion"))) {
                nFolio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
                adecua.actualizaJustificaciones(request.getParameter("justificacionA") != null ? new String(request.getParameter("justificacionA").getBytes("ISO-8859-1"), "UTF-8") : "", request.getParameter("justificacionR") != null ? new String(request.getParameter("justificacionR").getBytes("ISO-8859-1"), "UTF-8") : "", request.getParameter("justificacionN") != null ? new String(request.getParameter("justificacionN").getBytes("ISO-8859-1"), "UTF-8") : "", nFolio);
            }
            if (request.getParameter("nNumSicop") != null)
                nNumSicop = request.getParameter("nNumSicop");
            fSicop = request.getParameter("DPC_fFechaSicop");
            if (request.getParameter("nNumMAP") != null)
                nNumMAP = request.getParameter("nNumMAP");
            fMAP = request.getParameter("DPC_fFechaMAP");
            if (request.getParameter("nNumSicop") != null) {
                mensaje = "Este Documento esta en Tramite en SICOP y no se puede Cancelar.";
            }
            if (request.getParameter("nNumMAP") != null) {
                mensaje = "Este Documento esta en Tramite en MAP y no se puede Cancelar.";
            }
            if (request.getParameter("cSuperReduccion") != null)
                cSuperReduccion = request.getParameter("cSuperReduccion");
            if (request.getParameter("cSRInterna") != null)
                cSRInterna = request.getParameter("cSRInterna");
            String motivoRechazo = request.getParameter("motivoRechazo");
            boolean cancelar = (request.getParameter("cancelarDoc") != null && "Si".equals(request.getParameter("cancelarDoc")));
            boolean cAutDocto = (request.getParameter("autorizaDocto") != null && "Si".equals(request.getParameter("autorizaDocto")));
            boolean cAplicaDocto = (request.getParameter("cAplicaDocto") != null && "Si".equals(request.getParameter("cAplicaDocto")));
            if (cancelar) {
                cFechaAplicacion = c.getCasoDato("FECHA_AP_CONT").getValor();
                resultadoCancelacion = adecua.cancelarAppContableNuevo(c, m, prefixPath, usuario, cFechaAplicacion, motivoRechazo, enviarCorreo);
                // resultadoAplicacion.add(resultadoCancelacion);
            }
            if (cAutDocto) {
                resultadoAplicacion = adecua.AutorizaAdecuacionNuevo(c, nNumSicop, fSicop, nNumMAP, fMAP, m, prefixPath, usuario, cSuperReduccion, cCentroContable, cSRInterna, enviarCorreo);
            }
            if (cAplicaDocto) {
                String cAnioFiscal = adecua.obtenEjercicioFiscal();
                cFechaAplicacion = c.getCasoDato("FECHA_AP_CONT").getValor();
                nFolio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
                notificaAdecuacion = request.getParameter("notificaAdecuacion");
                adecua.aplicaAdecuacion(nFolio, usuario, cAnioFiscal, usuario.getU_Ramo(), usuario.getU_UR(), c, cCentroContable, cFechaAplicacion, m, prefixPath, cSuperReduccion, cSRInterna, notificaAdecuacion);
            }
            // json.put("msg", "Correcto");
        } catch (Exception e) {
            log.debug(e.toString(), e);
            e.printStackTrace();
            try {
                json.put("msg", e.toString());
            } catch (JSONException je) {
                je.printStackTrace();
            }
        } finally {
            if (out != null)
                out.print(json);
        }
    }

    /**
     * Initialization of the servlet. <br>
     *
     * @throws ServletException
     *             if an error occurs
     */
    public void init() throws ServletException {
        // Put your code here
    }

    public void creaExcelAdecuacionCarga(HttpServletResponse response, HttpServletRequest request) throws IOException, SQLException {
        HttpSession session = request.getSession();
        Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        List<Saldo> saldoList = new ArrayList<Saldo>();
        AdecuacionBusinessLogic adecua = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
        String file_name = "Adecuacion";
        response.setContentType("application/vnd.ms-excel");
        response.addHeader("Content-Disposition", "inline; filename=\"" + file_name + "Carga.xls\";");
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet hs = wb.createSheet();
        HSSFRow fila = hs.createRow(0);
        String sql = "";
        // sql+=(Cuenta !=""&&Cuenta
        // !=null?" AND s.nCuentaP = '"+Cuenta+"'":"");
        // sql+=(Cuenta !=""&&Cuenta
        // !=null?" AND s.nCuenta like '"+Cuenta+"%'":"");
        sql += (request.getParameter("ep") != "" && request.getParameter("ep") != null ? " AND cSubCuenta = '" + request.getParameter("ep") + "'" : "");
        sql += (request.getParameter("EjercicioFiscal") != "" && request.getParameter("EjercicioFiscal") != null ? " AND aEjercicioFiscal_1 = '" + request.getParameter("EjercicioFiscal") + "'" : "");
        sql += (request.getParameter("RamoEP") != "" && request.getParameter("RamoEP") != null ? " AND cRamo_2='" + request.getParameter("RamoEP") + "'" : "");
        sql += (request.getParameter("UnidadResponsableEP") != "" && request.getParameter("UnidadResponsableEP") != null ? " AND cUnidadResponsable_3='" + request.getParameter("UnidadResponsableEP") + "'" : "");
        sql += (request.getParameter("GrupoFuncional") != "" && request.getParameter("GrupoFuncional") != null ? " AND cGrupoFuncional_4='" + request.getParameter("GrupoFuncional") + "'" : "");
        sql += (request.getParameter("Funcion") != "" && request.getParameter("Funcion") != null ? " AND cFuncion_5='" + request.getParameter("Funcion") + "'" : "");
        sql += (request.getParameter("SubFuncion") != "" && request.getParameter("SubFuncion") != null ? " AND cSubFuncion_6='" + request.getParameter("SubFuncion") + "'" : "");
        sql += (request.getParameter("ProgramaGeneral") != "" && request.getParameter("ProgramaGeneral") != null ? " AND cProgramaGeneral_7='" + request.getParameter("ProgramaGeneral") + "'" : "");
        sql += (request.getParameter("ProgramaPresupuestario") != "" && request.getParameter("ProgramaPresupuestario") != null ? " AND cProgramaPresupuestario_9='" + request.getParameter("ProgramaPresupuestario") + "'" : "");
        sql += (request.getParameter("ActividadInstitucional") != "" && request.getParameter("ActividadInstitucional") != null ? " AND cActividadInstitucional_8='" + request.getParameter("ActividadInstitucional") + "'" : "");
        sql += (request.getParameter("Partida") != "" && request.getParameter("Partida") != null ? " AND cPartida_10='" + request.getParameter("Partida") + "'" : "");
        sql += (request.getParameter("TipoGasto") != "" && request.getParameter("TipoGasto") != null ? " AND cTipoGasto_11='" + request.getParameter("TipoGasto") + "'" : "");
        sql += (request.getParameter("FuenteFinanciamiento") != "" && request.getParameter("FuenteFinanciamiento") != null ? " AND cFuenteFinanciamiento_12='" + request.getParameter("FuenteFinanciamiento") + "'" : "");
        sql += (request.getParameter("EntidadFederativa") != "" && request.getParameter("EntidadFederativa") != null ? " AND cEntidadFederativa_13='" + request.getParameter("EntidadFederativa") + "'" : "");
        sql += (request.getParameter("Cartera") != "" && request.getParameter("Cartera") != null ? " AND cCartera_14='" + request.getParameter("Cartera") + "'" : "");
        sql += (request.getParameter("UnidadNormativa") != "" && request.getParameter("UnidadNormativa") != null ? " AND cUnidadResponsable_16='" + request.getParameter("UnidadNormativa") + "'" : "");
        sql += (request.getParameter("UnidadEjecutora") != "" && request.getParameter("UnidadEjecutora") != null ? " AND cUnidadResponsable_15='" + request.getParameter("UnidadEjecutora") + "'" : "");
        sql += (request.getParameter("ClaveCNA") != "" && request.getParameter("ClaveCNA") != null ? " AND nClaveCNA='" + request.getParameter("ClaveCNA") + "'" : "");
        String[] nCtasPresup = request.getParameter("nCtasPresup2").split(",\\s*");
        String[] dCtasPresup = request.getParameter("dCtasPresup2").split(",\\s*");
        saldoList = adecua.saldosExcelMultiReporte(sql, request.getParameter("Cuenta"), request.getParameter("cOrddeBy"), request.getParameter("cGroupBy"), request.getParameter("InfoRegMes"), request.getParameter("TipoReporte"), request.getParameter("Usuario"), nCtasPresup, dCtasPresup);
        double total = 0;
        int secuencia = 1;
        HSSFCell celda = fila.createCell(0);
        celda.setCellValue("LOGIN");
        celda = fila.createCell(1);
        celda.setCellValue("EJERCICIO");
        celda = fila.createCell(2);
        celda.setCellValue("CLAVE UNIDAD ADMINISTRATIVA");
        celda = fila.createCell(3);
        celda.setCellValue("CLAVE AFECTACION");
        celda = fila.createCell(4);
        celda.setCellValue("RAMO");
        celda = fila.createCell(5);
        celda.setCellValue("MONTO TOTAL");
        celda = fila.createCell(6);
        celda.setCellValue("HAY SECUENCIA");
        celda = fila.createCell(7);
        celda.setCellValue("HAY MONTO ANUAL");
        celda = fila.createCell(8);
        celda.setCellValue("CLAVES CON FORMATO");
        celda = fila.createCell(9);
        celda.setCellValue("NUMERO DICTAMEN");
        fila = hs.createRow(1);
        celda = fila.createCell(0);
        celda.setCellValue(usuario.getLogin());
        celda = fila.createCell(1);
        celda.setCellValue(adecua.obtenEjercicioFiscal());
        celda = fila.createCell(2);
        celda.setCellValue(usuario.getU_UR());
        celda = fila.createCell(3);
        // celda.setCellValue(usuario.getU_UR()); //clave afectacion sin valor
        celda = fila.createCell(4);
        celda.setCellValue(usuario.getU_Ramo());
        celda = fila.createCell(5);
        // celda.setCellValue(usuario.getU_UR());
        celda = fila.createCell(6);
        celda.setCellValue("SI");
        celda = fila.createCell(7);
        celda.setCellValue("SI");
        celda = fila.createCell(8);
        celda.setCellValue("SI");
        celda = fila.createCell(9);
        celda.setCellValue("N/A");
        fila = hs.createRow(2);
        celda = fila.createCell(0);
        celda.setCellValue("JUSTIFICACION");
        fila = hs.createRow(3);
        celda = fila.createCell(0);
        celda.setCellValue("");
        fila = hs.createRow(4);
        celda = fila.createCell(0);
        celda.setCellValue("SECUENCIA");
        celda = fila.createCell(1);
        celda.setCellValue("CLAVE SIAFF O MAP");
        celda = fila.createCell(2);
        celda.setCellValue("CLAVE INTERNA");
        celda = fila.createCell(3);
        celda.setCellValue("CODIGO SAF");
        celda = fila.createCell(4);
        celda.setCellValue("TIPO");
        celda = fila.createCell(5);
        celda.setCellValue("MONTO ANUAL");
        celda = fila.createCell(6);
        celda.setCellValue("ENERO");
        celda = fila.createCell(7);
        celda.setCellValue("FEBRERO");
        celda = fila.createCell(8);
        celda.setCellValue("MARZO");
        celda = fila.createCell(9);
        celda.setCellValue("ABRIL");
        celda = fila.createCell(10);
        celda.setCellValue("MAYO");
        celda = fila.createCell(11);
        celda.setCellValue("JUNIO");
        celda = fila.createCell(12);
        celda.setCellValue("JULIO");
        celda = fila.createCell(13);
        celda.setCellValue("AGOSTO");
        celda = fila.createCell(14);
        celda.setCellValue("SEPTIEMBRE");
        celda = fila.createCell(15);
        celda.setCellValue("OCTUBRE");
        celda = fila.createCell(16);
        celda.setCellValue("NOVIEMBRE");
        celda = fila.createCell(17);
        celda.setCellValue("DICIEMBRE");
        int row = 0;
        int filaDetalle = 5;
        double anual;
        for (Iterator<?> iter = saldoList.iterator(); iter.hasNext(); row++) {
            Saldo epSaldo = (Saldo) iter.next();
            anual = Double.valueOf(epSaldo.getMontoEnero()) + Double.valueOf(epSaldo.getMontoFebrero()) + Double.valueOf(epSaldo.getMontoMarzo()) + Double.valueOf(epSaldo.getMontoAbril()) + Double.valueOf(epSaldo.getMontoMayo()) + Double.valueOf(epSaldo.getMontoJunio()) + Double.valueOf(epSaldo.getMontoJulio()) + Double.valueOf(epSaldo.getMontoAgosto()) + Double.valueOf(epSaldo.getMontoSeptiembre()) + Double.valueOf(epSaldo.getMontoOctubre()) + Double.valueOf(epSaldo.getMontoNoviembre()) + Double.valueOf(epSaldo.getMontoDiciembre());
            fila = hs.createRow(filaDetalle);
            celda = fila.createCell(0);
            celda.setCellValue(secuencia);
            celda = fila.createCell(1);
            celda.setCellValue(epSaldo.getEp().substring(0, epSaldo.getEp().length() - 8));
            celda = fila.createCell(2);
            celda.setCellValue(epSaldo.getEp().substring(56));
            celda = fila.createCell(3);
            celda.setCellValue(0);
            celda = fila.createCell(4);
            celda.setCellValue("");
            celda = fila.createCell(5);
            celda.setCellValue(String.valueOf(anual));
            celda = fila.createCell(6);
            celda.setCellValue(epSaldo.getMontoEnero());
            celda = fila.createCell(7);
            celda.setCellValue(epSaldo.getMontoFebrero());
            celda = fila.createCell(8);
            celda.setCellValue(epSaldo.getMontoMarzo());
            celda = fila.createCell(9);
            celda.setCellValue(epSaldo.getMontoAbril());
            celda = fila.createCell(10);
            celda.setCellValue(epSaldo.getMontoMayo());
            celda = fila.createCell(11);
            celda.setCellValue(epSaldo.getMontoJunio());
            celda = fila.createCell(12);
            celda.setCellValue(epSaldo.getMontoJulio());
            celda = fila.createCell(13);
            celda.setCellValue(epSaldo.getMontoAgosto());
            celda = fila.createCell(14);
            celda.setCellValue(epSaldo.getMontoSeptiembre());
            celda = fila.createCell(15);
            celda.setCellValue(epSaldo.getMontoOctubre());
            celda = fila.createCell(16);
            celda.setCellValue(epSaldo.getMontoNoviembre());
            celda = fila.createCell(17);
            celda.setCellValue(epSaldo.getMontoDiciembre());
            total += anual;
            filaDetalle++;
            secuencia++;
        }
        fila = hs.getRow(1);
        celda = fila.createCell(5);
        celda.setCellValue(total);
        wb.write(response.getOutputStream());
        wb.close();
    }
}
