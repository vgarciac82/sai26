package com.syc.adquisiciones.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.SimpleFileResolver;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleJsonReportConfiguration;
import net.sf.jasperreports.engine.JRParameter;
import com.syc.admin.servlet.Reportes;
import com.syc.admin.servlet.ReportsException;
import com.syc.adquisiciones.core.APTEJRDataSource;
import com.syc.adquisiciones.core.APTEProveedor;
import com.syc.adquisiciones.core.APTEProveedorPartida;
import com.syc.adquisiciones.core.APTEProveedorTotales;
import com.syc.adquisiciones.core.IntercalarPedidoPDF;
import com.syc.adquisiciones.core.PedidoModHoja;
import com.syc.adquisiciones.core.PedidoModJRDataSource;
import com.syc.adquisiciones.core.PedidoModLinea;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.servlet.GestionServlet;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.utils.Formatter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporterParameter;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "SeguridadCatalogosMateriales", urlPatterns = { "/servlet/SeguridadCatalogosMateriales" })
public class SeguridadCatalogosMateriales extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private String jniName = null;

    private static Logger log = LoggerFactory.getLogger(SeguridadCatalogosMateriales.class);

    public SeguridadCatalogosMateriales() {
        super();
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
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter pw = response.getWriter();
        System.out.println("Generando reporte...");
        pw.print("Generando reporte...");
        doPost(request, response);
        System.out.println("Termino reporte...");
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RunReport(request, response);
    }

    private StringBuffer getRespuesta(String resp) {
        StringBuffer xmlOut = new StringBuffer();
        xmlOut.append("<respuesta>");
        xmlOut.append("<estado valor='");
        xmlOut.append(resp);
        xmlOut.append("'/></respuesta>");
        return xmlOut;
    }

    void RunReport(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Connection conn = null;
        InputStream in = null;
        ServletOutputStream out = null;
        JRDataSource dataSource = null;
        String reportName = req.getParameter("rn");
        if (reportName == null)
            throw new ServletException("Llamada inválida");
        if ("".equals(reportName))
            throw new ServletException("Valor inválido");
        String reportPath1 = getServletContext().getRealPath("Reportes" + File.separator);
        if (reportPath1 == null) {
            // Busca en el WAR
            URL url = getClass().getResource(reportName);
            if (url == null)
                throw new ServletException("No se encontro el reporte " + reportName);
            reportPath1 = url.getPath();
            if ((reportPath1 == null) || (reportPath1.length() == 0))
                throw new ServletException("No se encontro el reporte " + reportName);
        }
        Map<String, Object> parms1 = new HashMap<String, Object>();
        Enumeration pnames = req.getParameterNames();
        while (pnames.hasMoreElements()) {
            String name = (String) pnames.nextElement();
            String value = req.getParameter(name).toString();
            System.out.println("name  (parameter): " + name);
            System.out.println("value (parameter): " + value);
            if ("rn".equals(name) || "accion".equals(name) || "catalogo".equals(name))
                continue;
            /*
			 * Esteban Badillo. Fecha: 14/Oct/2009
			 * Descripcion:
			 * 	Se agregan los parametros para el Reporte Estadístico por Area para agregar solamente los asuntos que hayan sido
			 * 	turnados desde el area seleccionada a sus áreas hijas.
			 * */
            /*
			if( name.equals("areasHijas") )
			{
				//name = "whereIdArea";
				//value = " and vimx.remitente_area = " + req.getParameter("idArea") + " ";
				if(value.equals("true"))
					value = "1";
				else
					value = "0";
			}
			*/
            parms1.put(name, value);
            /**
             * ***************************************************************
             */
            /**
             * 				MODULO DE CAJA			  	  *
             */
            /**
             * ***************************************************************
             */
            if (req.getParameter("rn").equals("PolizaCaja.jasper")) {
                parms1.put("whereFolio", req.getParameter("whereFolio"));
                parms1.put("SUBREPORT_DIR", getServletContext().getRealPath("Reportes"));
            }
            if (req.getParameter("rn").equals("SolicitudPolizaCaja.jasper")) {
                parms1.put("whereFolio", req.getParameter("whereFolio"));
                parms1.put("SUBREPORT_DIR", getServletContext().getRealPath("Reportes"));
            }
            /**
             * ***************************************************************
             */
            /**
             * 				MODULO DE ADQUISICIONES Y SERVICIOS			  	  *
             */
            /**
             * ***************************************************************
             */
            /**
             * *****************************************************************************************************
             * 			PARAMETROS DEL REPORTE OIC1
             * *******************************************************************************************************
             */
            if (req.getParameter("rn").equals("reporteOIC1.jasper")) {
                parms1.put("cIdUnidadEjecutora", req.getParameter("cIdUnidadEjecutora"));
                parms1.put("formato", req.getParameter("formato"));
                parms1.put("SUBREPORT_DIR", getServletContext().getRealPath("Reportes"));
            }
            /**
             * *****************************************************************************************************
             * 			PARAMETROS DEL REPORTE 70_30
             * *******************************************************************************************************
             */
            if (req.getParameter("rn").equals("reporte70_30.jasper")) {
                parms1.put("cIdUnidadEjecutora", req.getParameter("cIdUnidadEjecutora"));
                parms1.put("formato", req.getParameter("formato"));
                parms1.put("SUBREPORT_DIR", getServletContext().getRealPath("Reportes"));
            }
            /**
             * *****************************************************************************************************
             * 			PARAMETROS DEL REPORTE OC TOTALIZADO
             * *******************************************************************************************************
             */
            if (req.getParameter("rn").equals("reporteOCTotalizado.jasper")) {
                parms1.put("cIdUnidadEjecutora", req.getParameter("cIdUnidadEjecutora"));
                parms1.put("formato", req.getParameter("formato"));
                parms1.put("SUBREPORT_DIR", getServletContext().getRealPath("Reportes"));
            }
            /**
             * *****************************************************************************************************
             * 			PARAMETROS DEL REPORTE OC AGRUPADO POR PARTIDAS DE CONTRATOS
             * *******************************************************************************************************
             */
            if (req.getParameter("rn").equals("reporteOCPartidaPedCont.jasper")) {
                parms1.put("cIdUnidadEjecutora", req.getParameter("cIdUnidadEjecutora"));
                parms1.put("formato", req.getParameter("formato"));
                parms1.put("SUBREPORT_DIR", getServletContext().getRealPath("Reportes"));
            }
            /**
             * ***************************************************************
             */
            /**
             * 				MODULO DE ADQUISICIONES Y SERVICIOS			  	  *
             */
            /**
             * ***************************************************************
             */
            /**
             * *****************************************************************************************************
             * 			PARAMETROS DEL CONVEDIO
             * *******************************************************************************************************
             */
            if ("reporteConvenioMod.jasper".equalsIgnoreCase(req.getParameter("rn"))) {
                parms1.put("where", req.getParameter("where"));
                parms1.put("formato", req.getParameter("formato"));
                parms1.put("SUBREPORT_DIR", getServletContext().getRealPath("Reportes"));
            }
            /**
             * *****************************************************************************************************
             * 			PARAMETROS DE REPORTE POR PARTIDA Y CAPITULO
             * *******************************************************************************************************
             */
            if (req.getParameter("rn").equals("rpt_programaAnualResumidoPorCapitulosPartidasNacionalOarea.jasper")) {
                parms1.put("cIdUnidadEjecutora", req.getParameter("cIdUnidadEjecutora"));
                parms1.put("formato", req.getParameter("formato"));
                parms1.put("SUBREPORT_DIR", getServletContext().getRealPath("Reportes"));
            }
            /**
             * *****************************************************************************************************
             * 			 PARAMETROS DE Articulos del Almacen
             * *******************************************************************************************************
             */
            if (req.getParameter("rn").equals("mCatalogoInventarioArticulosdeAlmacen.jasper")) {
                //System.out.println("Esta obteniendo los parametros del repote Anual");
                //parms1.put ("fechainicio", new Date() );
                parms1.put("cIdUnidadEjecutora", req.getParameter("cIdUnidadEjecutora"));
                parms1.put("formato", req.getParameter("formato"));
                parms1.put("SUBREPORT_DIR", getServletContext().getRealPath("Reportes"));
            }
            /**
             * *****************************************************************************************************
             * 			 PARAMETROS DE Consiliacion presupuestal Apartado VS Precomprometido
             * *******************************************************************************************************
             */
            if (req.getParameter("rn").equals("mConsiliacion_RC_Apartado_Precompromiso.jasper")) {
                //System.out.println("Esta obteniendo los parametros del repote Anual");
                //parms1.put ("fechainicio", new Date() );
                parms1.put("cIdUnidadEjecutora", req.getParameter("cIdUnidadEjecutora"));
                parms1.put("formato", req.getParameter("formato"));
                parms1.put("SUBREPORT_DIR", getServletContext().getRealPath("Reportes"));
            }
            /**
             * *****************************************************************************************************
             * 			 PARAMETROS DE Consiliacion presupuestal Apartado VS Ejercido
             * *******************************************************************************************************
             */
            if (req.getParameter("rn").equals("mConsiliacion_AP_PRE_Comp_Eje.jasper")) {
                //System.out.println("Esta obteniendo los parametros del repote Anual");
                //parms1.put ("fechainicio", new Date() );
                parms1.put("cIdUnidadEjecutora", req.getParameter("cIdUnidadEjecutora"));
                parms1.put("formato", req.getParameter("formato"));
                parms1.put("SUBREPORT_DIR", getServletContext().getRealPath("Reportes"));
            }
            /**
             * *****************************************************************************************************
             * 			 PARAMETROS DE REPORTE GENERAL
             * *******************************************************************************************************
             */
            if (req.getParameter("rn").equals("rpt_mProgramaAnualOrdenadoPorPartida_xls.jasper")) {
                //System.out.println("Esta obteniendo los parametros del repote Anual");
                //parms1.put ("fechainicio", new Date() );
                parms1.put("cIdUnidadEjecutora", req.getParameter("cIdUnidadEjecutora"));
                parms1.put("formato", req.getParameter("formato"));
                parms1.put("SUBREPORT_DIR", getServletContext().getRealPath("Reportes"));
            }
            /**
             * *****************************************************************************************************
             * 			PARAMETROS DE REPORTE GENERAL Acumulado
             * *******************************************************************************************************
             */
            if (req.getParameter("rn").equals("rpt_mProgramaAnualOrdenadoPorPartidaAcumulado.jasper")) {
                //System.out.println("Esta obteniendo los parametros del repote Anual acumulado");
                //parms1.put ("fechainicio", new Date() );
                parms1.put("cIdUnidadEjecutora", req.getParameter("cIdUnidadEjecutora"));
                parms1.put("formato", req.getParameter("formato"));
                parms1.put("SUBREPORT_DIR", getServletContext().getRealPath("Reportes"));
            }
            /**
             * *****************************************************************************************************
             * 			PARAMETROS DE REPORTE Anual por Partida y trimestre
             * *******************************************************************************************************
             */
            if (req.getParameter("rn").equals("rpt_mProgramaAnualPorPartidayTrimestre.jasper")) {
                //System.out.println("Esta obteniendo los parametros del repote Anual acumulado");
                //parms1.put ("fechainicio", new Date() );
                parms1.put("cIdUnidadEjecutora", req.getParameter("cIdUnidadEjecutora"));
                parms1.put("formato", req.getParameter("formato"));
                //System.out.println("Formato : "+req.getParameter("formato"));
                parms1.put("SUBREPORT_DIR", getServletContext().getRealPath("Reportes"));
            }
            /**
             * *****************************************************************************************************
             * 			PARAMETROS DE REPORTE Anual por Partida y trimestre
             * *******************************************************************************************************
             */
            if (req.getParameter("rn").equals("fn_mProgramaAnualMontosPorCapituloReporte.jasper")) {
                //System.out.println("Esta obteniendo los parametros del repote Anual acumulado");
                //parms1.put ("fechainicio", new Date() );
                parms1.put("cIdUnidadEjecutora", req.getParameter("cIdUnidadEjecutora"));
                parms1.put("formato", req.getParameter("formato"));
                log.info("Formato : " + req.getParameter("formato"));
                parms1.put("SUBREPORT_DIR", getServletContext().getRealPath("Reportes"));
            }
            /**
             * *****************************************************************************************************
             * 			PARAMETROS DE REPORTE Anual Resumido por Unidad y Capitulo Restando Partidas
             * *******************************************************************************************************
             */
            if (req.getParameter("rn").equals("rpt_mProgramaAnualResumidoPorUnidadyCapituloRestandoPartidas.jasper")) {
                //System.out.println("Esta obteniendo los parametros del repote Anual acumulado");
                //parms1.put ("fechainicio", new Date() );
                parms1.put("cIdUnidadEjecutora", req.getParameter("cIdUnidadEjecutora"));
                parms1.put("formato", req.getParameter("formato"));
                log.info("Formato : " + req.getParameter("formato"));
                parms1.put("SUBREPORT_DIR", getServletContext().getRealPath("Reportes"));
            }
            /**
             * *****************************************************************************************************
             * 			PARAMETROS DE REPORTE Anual Compranet
             * *******************************************************************************************************
             */
            if (req.getParameter("rn").equals("rpt_CompraNET.jasper")) {
                //System.out.println("Esta obteniendo los parametros del repote Anual acumulado");
                //parms1.put ("fechainicio", new Date() );
                parms1.put("cIdUnidadEjecutora", req.getParameter("cIdUnidadEjecutora"));
                parms1.put("formato", req.getParameter("formato"));
                log.info("Formato : " + req.getParameter("formato"));
                parms1.put("SUBREPORT_DIR", getServletContext().getRealPath("Reportes"));
            }
            /**
             * *****************************************************************************************************
             * 			Programa Anual de Adquisiciones CUCOPS sin documentos asociados
             * *******************************************************************************************************
             */
            if (req.getParameter("rn").equals("rpt_ProgramaAnualdeAdquisicionesCUCOPSsinDocumentosAsociados.jasper")) {
                //System.out.println("Esta obteniendo los parametros del repote Anual acumulado");
                //parms1.put ("fechainicio", new Date() );
                parms1.put("cIdUnidadEjecutora", req.getParameter("cIdUnidadEjecutora"));
                parms1.put("formato", req.getParameter("formato"));
                log.info("Formato : " + req.getParameter("formato"));
                parms1.put("SUBREPORT_DIR", getServletContext().getRealPath("Reportes"));
            }
            /**
             * *****************************************************************************************************
             * 			Programa Anual Conciliacion presupuiestal
             * *******************************************************************************************************
             */
            if (req.getParameter("rn").equals("fn_mProgramaAnualConciliacionPresupuestal.jasper")) {
                //System.out.println("Esta obteniendo los parametros del repote Anual acumulado");
                //parms1.put ("fechainicio", new Date() );
                parms1.put("cIdUnidadEjecutora", req.getParameter("cIdUnidadEjecutora"));
                parms1.put("formato", req.getParameter("formato"));
                log.info("Formato : " + req.getParameter("formato"));
                parms1.put("SUBREPORT_DIR", getServletContext().getRealPath("Reportes"));
            }
            /**
             * *****************************************************************************************************
             * 			Reporte del Consolidado
             * 			/********************************************************************************************************
             */
            if (req.getParameter("rn").equals("ReporteConsolidado.jasper")) {
                parms1.put("cIdConsolidado", req.getParameter("cIdCons"));
                parms1.put("formato", req.getParameter("formato"));
                log.info("Formato : " + req.getParameter("formato"));
                parms1.put("SUBREPORT_DIR", getServletContext().getRealPath("Reportes"));
            }
            /**
             * *****************************************************************************************************
             * 			Reporte del OC Detallado
             * 			/********************************************************************************************************
             */
            if (req.getParameter("rn").equals("reporteOCDetallado.jasper") || "reporteOCDetalladoASF.jasper".equalsIgnoreCase(req.getParameter("rn"))) {
                parms1.put("cIdUnidadEjecutora", req.getParameter("cIdUnidadEjecutora"));
                parms1.put("formato", req.getParameter("formato"));
                log.info("Formato : " + req.getParameter("formato"));
                parms1.put("SUBREPORT_DIR", getServletContext().getRealPath("Reportes"));
            }
            /**
             * *****************************************************************************************************
             * 			Reporte del OC Detallado
             * 			/********************************************************************************************************
             */
            if (req.getParameter("rn").equals("reporteMIPyMes.jasper")) {
                parms1.put("cIdUnidadEjecutora", req.getParameter("cIdUnidadEjecutora"));
                parms1.put("formato", req.getParameter("formato"));
                log.info("Formato : " + req.getParameter("formato"));
                parms1.put("SUBREPORT_DIR", getServletContext().getRealPath("Reportes"));
            }
            /**
             * *****************************************************************************************************
             * 			Reporte del OC Partida
             * 			/********************************************************************************************************
             */
            if (req.getParameter("rn").equals("reporteOCPartida.jasper")) {
                parms1.put("cIdUnidadEjecutora", req.getParameter("cIdUnidadEjecutora"));
                parms1.put("formato", req.getParameter("formato"));
                log.info("Formato : " + req.getParameter("formato"));
                parms1.put("SUBREPORT_DIR", getServletContext().getRealPath("Reportes"));
            }
            /**
             * *****************************************************************************************************
             * 			Reporte del OC CUCOP
             * 			/********************************************************************************************************
             */
            if (req.getParameter("rn").equals("reporteOCCUCOP.jasper")) {
                parms1.put("cIdUnidadEjecutora", req.getParameter("cIdUnidadEjecutora"));
                parms1.put("formato", req.getParameter("formato"));
                log.info("Formato : " + req.getParameter("formato"));
                parms1.put("SUBREPORT_DIR", getServletContext().getRealPath("Reportes"));
            }
            /**
             * *****************************************************************************************************
             * 			Bitácora de movimientos de Materiales
             * 			/********************************************************************************************************
             */
            if (req.getParameter("rn").equals("reporteBitacora.jasper")) {
                parms1.put("cIdUnidadEjecutora", req.getParameter("cIdUnidadEjecutora"));
                parms1.put("formato", req.getParameter("formato"));
                parms1.put("documento_", req.getParameter("documento_"));
                parms1.put("usuario", req.getParameter("usuario"));
                parms1.put("rangoFechas", req.getParameter("rangoFechas"));
                log.info("Formato : " + req.getParameter("formato"));
                parms1.put("SUBREPORT_DIR", getServletContext().getRealPath("Reportes"));
            }
            /**
             * *****************************************************************************************************
             * 			Reporte de presentación de propuestas en el procedimiento
             * 			/********************************************************************************************************
             */
            if (req.getParameter("rn").equalsIgnoreCase("rptActaApte.jasper")) {
                Statement stmt = null;
                ResultSet rs = null;
                try {
                    ArrayList<APTEProveedor> proveedores = new ArrayList<APTEProveedor>();
                    ArrayList<APTEProveedorTotales> totales = new ArrayList<APTEProveedorTotales>();
                    HashMap<String, APTEProveedor> provMap = new HashMap<String, APTEProveedor>();
                    HashMap<String, APTEProveedorTotales> partidasMap = new HashMap<String, APTEProveedorTotales>();
                    HashMap<Integer, Integer> cantidadMap = new HashMap<Integer, Integer>();
                    HashMap<Integer, String> monedaMap = new HashMap<Integer, String>();
                    String idProcedimiento = req.getParameter("cIdProcedimiento");
                    String idConsolidado = req.getParameter("cIdConsolidado");
                    String ue = idProcedimiento.split("-")[1];
                    String ejercicio = Calendar.getInstance().get(Calendar.YEAR) + "";
                    conn = DataSourceManager.getConnection(jniName);
                    stmt = conn.createStatement();
                    parms1.put("SUBREPORT_DIR", getServletContext().getRealPath("Reportes"));
                    //CARGA EL EJERCICIO FISCAL
                    String queryCons = "SELECT cValor FROM mSistema WITH (NOLOCK) where cParametro = 'cEjercicio'";
                    rs = stmt.executeQuery(queryCons);
                    if (rs.next()) {
                        ejercicio = rs.getString(1);
                    }
                    //CARGA LOS PARAMETROS DEL PROCEDIMIENTO Y LA UE
                    queryCons = "SELECT * FROM v_mUnidadesAdministrativas WITH (NOLOCK) where claveUnidadAdministrativa = '" + ue + "'";
                    rs = stmt.executeQuery(queryCons);
                    if (rs.next()) {
                        String descripcionUE = rs.getString(3);
                        if (descripcionUE == null)
                            descripcionUE = "";
                        String direccionUE = "";
                        String calle = rs.getString(4);
                        String nuext = rs.getString(5);
                        String nuint = rs.getString(6);
                        String colonia = rs.getString(7);
                        String municipio = rs.getString(8);
                        String cp = rs.getString(9);
                        String ciudad = rs.getString(10);
                        String estado = rs.getString(11);
                        if (calle != null)
                            direccionUE += calle.trim();
                        if (nuext != null)
                            direccionUE += " No. " + nuext.trim();
                        if (nuint != null)
                            direccionUE += " int. " + nuint.trim();
                        if (colonia != null)
                            direccionUE += ", Col. " + colonia.trim();
                        if (municipio != null)
                            direccionUE += ", " + municipio.trim();
                        if (cp != null)
                            direccionUE += ", C.P. " + cp.trim();
                        if (ciudad != null)
                            direccionUE += " " + ciudad.trim();
                        if (estado != null)
                            direccionUE += ", " + estado.trim();
                        parms1.put("NOMBRE_UE", descripcionUE);
                        parms1.put("DIRECCION_UE", direccionUE);
                    }
                    queryCons = "SELECT p.cDescripcion, cp.cCategoria FROM mProcedimiento as p WITH (NOLOCK), mCatalogoCategoriaProcedimiento as cp WITH (NOLOCK) where p.cIdProcedimiento = '" + idProcedimiento + "' AND cp.nIdCategoria = p.nIdCategoria AND p.cEjercicio = '" + ejercicio + "'";
                    rs = stmt.executeQuery(queryCons);
                    if (rs.next()) {
                        String desProcedimiento = rs.getString(1);
                        if (desProcedimiento == null)
                            desProcedimiento = "";
                        String catProcedimiento = rs.getString(2);
                        if (catProcedimiento == null)
                            catProcedimiento = "";
                        parms1.put("DESCRIPCION_P", desProcedimiento);
                        parms1.put("CATEGORIA_P", catProcedimiento);
                    }
                    queryCons = "SELECT fecha FROM mProcedimientoFechas WITH (NOLOCK) where nIdProcedimiento = '" + idProcedimiento + "' AND nIdFecha = 4";
                    rs = stmt.executeQuery(queryCons);
                    if (rs.next()) {
                        Date fechaPP = rs.getDate(1);
                        String fechaPPS = "";
                        if (fechaPP != null) {
                            DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, new Locale("es", "MX"));
                            fechaPPS = df.format(fechaPP);
                        }
                        parms1.put("FECHA_PP", fechaPPS);
                    }
                    queryCons = "SELECT DISTINCT cIdSolicitud FROM mConsolidadoSolicitud WITH (NOLOCK) where cIdConsolidado = '" + idConsolidado + "' AND cEjercicio = '" + ejercicio + "'";
                    rs = stmt.executeQuery(queryCons);
                    String reqs = "";
                    Integer cont = 0;
                    while (rs.next()) {
                        if (cont < 2) {
                            String requi = rs.getString(1);
                            if (requi != null) {
                                if (reqs.length() > 0)
                                    reqs += " y " + requi;
                                else
                                    reqs += requi;
                                cont++;
                            }
                        } else {
                            reqs = "VARIAS";
                            break;
                        }
                    }
                    parms1.put("REQUISICIONES", reqs);
                    /**
                     * CHECKLIST DE DOCUMENTOS *
                     */
                    //CARGA LOS PROVEEDORES Y SUS DOCS
                    queryCons = "SELECT DISTINCT pa.cIdRFC, cp.cRazonSocial FROM mProcedimientoAdjudicacion as pa WITH (NOLOCK), mCatalogoProveedor as cp WITH (NOLOCK) where cEstadoProveedor = 1 and cIdProcedimiento = '" + idProcedimiento + "' AND pa.cIdRFC = cp.cIdRFC AND pa.cEjercicio = '" + ejercicio + "'";
                    rs = stmt.executeQuery(queryCons);
                    while (rs.next()) {
                        APTEProveedor prov = new APTEProveedor();
                        prov.setRfc(rs.getString(1).trim());
                        prov.setProveedor(rs.getString(2));
                        proveedores.add(prov);
                        provMap.put(prov.getRfc(), prov);
                    }
                    queryCons = "SELECT * FROM mCatalogoDocumentos cd WITH (NOLOCK), mProcedimientoDocumentos pd WITH(NOLOCK) WHERE cd.nIdDocumento=pd.nIdDocumento and pd.cIdProcedimiento='" + idProcedimiento + "' and pd.cIdRFC in (SELECT TOP 1 PD2.cIdRFC FROM mProcedimientoDocumentos pd2 WITH(NOLOCK) WHERE pd2.cIdProcedimiento='" + idProcedimiento + "')";
                    rs = stmt.executeQuery(queryCons);
                    ArrayList<ArrayList<String>> catalogoDocs = new ArrayList<ArrayList<String>>();
                    while (rs.next()) {
                        ArrayList<String> catdoc = new ArrayList<String>();
                        catdoc.add(rs.getInt(1) + "");
                        catdoc.add(rs.getString(2));
                        catdoc.add(rs.getString(3));
                        catalogoDocs.add(catdoc);
                    }
                    APTEProveedor.llenarDocs(catalogoDocs, proveedores);
                    //queryCons = "SELECT cp.cIdRFC, pd.nIdDocumento, pd.descripcion FROM mProcedimientoDocumentos as pd WITH (NOLOCK), mCatalogoProveedor as cp WITH (NOLOCK) WHERE cIdProcedimiento = '" + idProcedimiento + "' AND pd.cIdRFC = cp.cIdRFC;";
                    queryCons = "SELECT cp.cIdRFC, pd.nIdDocumento, pd.descripcion, pd.cPresentado " + "FROM mProcedimientoDocumentos as pd WITH (NOLOCK), mCatalogoProveedor as cp WITH (NOLOCK), mProcedimientoAdjudicacion pa WITH(NOLOCK) " + "WHERE pd.cIdProcedimiento=pa.cIdProcedimiento AND pd.cIdRFC=pa.cIdRFC AND cEstadoProveedor = 1 AND pd.cIdProcedimiento = '" + idProcedimiento + "' AND pd.cIdRFC = cp.cIdRFC";
                    rs = stmt.executeQuery(queryCons);
                    while (rs.next()) {
                        String rfcProv = rs.getString(1).trim();
                        if (rfcProv != null)
                            rfcProv = rfcProv.trim();
                        String docId = rs.getInt(2) + "";
                        String docDesc = rs.getString(3);
                        APTEProveedor prov = provMap.get(rfcProv.trim());
                        if (rs.getInt(4) == 1)
                            prov.addDocPresentado(docId, docDesc, true);
                        else
                            prov.addDocPresentado(docId, docDesc, false);
                    }
                    /**
                     * CARGAR LOS DATOS PARA LAS PARTIDAS COTIZADAS POR PROVEEDOR *
                     */
                    //CARGA EL CATALOGO DE TIPO MONEDA
                    queryCons = "SELECT * FROM mCatalogoTipoMoneda WITH (NOLOCK) ";
                    rs = stmt.executeQuery(queryCons);
                    while (rs.next()) {
                        monedaMap.put(Integer.parseInt(rs.getString(1)), rs.getString(3));
                    }
                    //CARGA EL IVA DEL PROCEDIMIENTO
                    Double iva = 1.0;
                    queryCons = "SELECT nPorcentajeIVA FROM mProcedimientoAdjudicacion WITH (NOLOCK) WHERE cIdProcedimiento = '" + idProcedimiento + "' AND cEjercicio = '" + ejercicio + "'";
                    rs = stmt.executeQuery(queryCons);
                    if (rs.next()) {
                        Integer pIva = rs.getInt(1);
                        if (pIva != null)
                            iva = iva + (pIva / 100.0);
                    }
                    //CARGA LAS CANTIDADES DE CADA PARTIDA DEL PROCEDIMIENTO
                    //queryCons = "SELECT DISTINCT (sl.nIdLineaSolicitud), sl.nCantidad FROM mConsolidadoSolicitud cs WITH (NOLOCK), mSolicitudLineas sl WITH (NOLOCK) WHERE cs.cEjercicio = sl.cEjercicio AND cs.cIdSolicitud = sl.cIdSolicitud AND cs.cIdConsolidado = '" + idConsolidado + "' AND cs.cEjercicio = '" + ejercicio + "'";
                    queryCons = "SELECT DISTINCT cl.nIdLineaConsolidado,sum(sl.nCantidad) FROM mConsolidadoSolicitud cs WITH (NOLOCK), " + " mSolicitudLineas sl WITH (NOLOCK), " + "mConsolidadoLineas cl WITH (NOLOCK) " + " WHERE cs.cEjercicio = sl.cEjercicio  " + " AND cl.cIdConsolidado=cs.cIdConsolidado  " + " and cl.nIdLineaConsolidado=cs.nIdLineaConsolidado  " + " and cs.cIdSolicitud = sl.cIdSolicitud  " + " and sl.nIdLineaSolicitud=cs.nIdLineaSolicitud " + " AND cs.cIdConsolidado= '" + idConsolidado + "' AND cs.cEjercicio = '" + ejercicio + "' GROUP BY cl.nIdLineaConsolidado";
                    rs = stmt.executeQuery(queryCons);
                    while (rs.next()) {
                        cantidadMap.put(rs.getInt(1), rs.getInt(2));
                    }
                    //CARGA LAS PARTIDAS COTIZADAS POR PROVEEDOR
                    queryCons = "SELECT * FROM mProcedimientoCompleto WITH (NOLOCK) WHERE mMontoMinimo > 0.0 AND mMontoMaximo > 0.0 AND cIdProcedimiento = '" + idProcedimiento + "' AND cEjercicio = '" + ejercicio + "'";
                    rs = stmt.executeQuery(queryCons);
                    while (rs.next()) {
                        String rfc = rs.getString(5);
                        Integer idLinea = rs.getInt(8);
                        Float precio = rs.getFloat(9);
                        if (precio == null)
                            precio = new Float(0);
                        String tipoMoneda = rs.getString(18);
                        if (tipoMoneda == null)
                            tipoMoneda = "M.N.";
                        else
                            tipoMoneda = monedaMap.get(Integer.parseInt(tipoMoneda.trim()));
                        Float tipoCambio = rs.getFloat(19);
                        if (tipoCambio == null)
                            tipoCambio = new Float(1.0);
                        if (rfc != null) {
                            APTEProveedorTotales provtot = partidasMap.get(rfc.trim() + "-" + tipoMoneda);
                            if (provtot == null) {
                                provtot = new APTEProveedorTotales();
                                provtot.setRfc(rfc.trim());
                                APTEProveedor prov = provMap.get(provtot.getRfc().trim());
                                if (prov != null) {
                                    provtot.setProveedor(prov.getProveedor());
                                    provtot.setTipoMoneda(tipoMoneda);
                                    totales.add(provtot);
                                    partidasMap.put(provtot.getRfc() + "-" + tipoMoneda, provtot);
                                }
                            }
                            if (idLinea != null) {
                                APTEProveedorPartida partida = new APTEProveedorPartida();
                                partida.setIdLinea(idLinea);
                                partida.setPrecio(precio.doubleValue());
                                partida.setTipoCambio(tipoCambio.doubleValue());
                                partida.setTipoMoneda(tipoMoneda);
                                provtot.getPartidas().add(partida);
                            }
                        }
                    }
                    for (APTEProveedorTotales provtot : totales) {
                        provtot.calcTotals(iva, cantidadMap);
                    }
                    APTEJRDataSource ds = new APTEJRDataSource();
                    ds.setProveedores(proveedores);
                    ds.setPartidas(totales);
                    dataSource = ds;
                } catch (Exception exc) {
                    exc.printStackTrace();
                    throw new ServletException(exc);
                } finally {
                    CloseObject.closeObject(conn);
                    CloseObject.closeObject(rs);
                    CloseObject.closeObject(stmt);
                }
                break;
            }
            if (req.getParameter("rn").equalsIgnoreCase("rptActaFallo.jasper")) {
                Statement stmt = null;
                ResultSet rs = null;
                try {
                    String idProcedimiento = req.getParameter("cIdProcedimiento");
                    conn = DataSourceManager.getConnection(jniName);
                    stmt = conn.createStatement();
                    parms1.put("DIR_REPORT", getServletContext().getRealPath("Reportes"));
                    parms1.put("IdProcedimiento", idProcedimiento);
                } catch (Exception exc) {
                    throw new ServletException(exc);
                } finally {
                    try {
                        if (conn != null)
                            conn.close();
                        if (stmt != null)
                            stmt.close();
                        if (rs != null)
                            rs.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    conn = null;
                }
                break;
            }
            /**
             * *****************************************************************************************************
             * 			Reporte de Modificación al Pedido REVERSO
             * 			/********************************************************************************************************
             */
            /**
             * *****************************************************************************************************
             * 			Reporte de Modificación al Pedido
             * 			/********************************************************************************************************
             */
            if (req.getParameter("rn").equalsIgnoreCase("PedidoModificado.jasper")) {
                Statement stmt = null;
                ResultSet rs = null;
                try {
                    ArrayList<PedidoModLinea> original = new ArrayList<PedidoModLinea>();
                    ArrayList<PedidoModLinea> modificacion = new ArrayList<PedidoModLinea>();
                    HashMap<Integer, PedidoModLinea> originalMap = new HashMap<Integer, PedidoModLinea>();
                    HashMap<Integer, PedidoModLinea> modificacionMap = new HashMap<Integer, PedidoModLinea>();
                    HashMap<Integer, String> numPartidaMap = new HashMap<Integer, String>();
                    Integer consPartida = 1;
                    String cPedidoDefinitivo = req.getParameter("cPedidoDefinitivo");
                    String nConsecutivoModif = req.getParameter("nConsecutivoMod");
                    String cPedido = cPedidoDefinitivo.split("/")[0];
                    String cEjercicio = cPedidoDefinitivo.split("/")[1];
                    conn = DataSourceManager.getConnection(jniName);
                    stmt = conn.createStatement();
                    parms1.put("PEDIDO_DEFINITIVO", cPedidoDefinitivo);
                    //CARGA LOS PARAMETROS DE LA UE
                    String query = "SELECT * FROM v_mUnidadesAdministrativas WITH (NOLOCK) where claveUnidadAdministrativa = '" + cPedido.split("-")[1] + "'";
                    rs = stmt.executeQuery(query);
                    if (rs.next()) {
                        String descripcionUE = rs.getString(3);
                        if (descripcionUE == null)
                            descripcionUE = "";
                        String direccionUE = "";
                        String calle = rs.getString(4);
                        String nuext = rs.getString(5);
                        String nuint = rs.getString(6);
                        String colonia = rs.getString(7);
                        String municipio = rs.getString(8);
                        String cp = rs.getString(9);
                        String ciudad = rs.getString(10);
                        String estado = rs.getString(11);
                        if (calle != null)
                            direccionUE += calle.trim();
                        if (nuext != null)
                            direccionUE += " No. " + nuext.trim();
                        if (nuint != null)
                            direccionUE += " int. " + nuint.trim();
                        if (colonia != null)
                            direccionUE += ", Col. " + colonia.trim();
                        if (municipio != null)
                            direccionUE += ", " + municipio.trim();
                        if (cp != null)
                            direccionUE += ", C.P. " + cp.trim();
                        if (ciudad != null)
                            direccionUE += " " + ciudad.trim();
                        if (estado != null)
                            direccionUE += ", " + estado.trim();
                        parms1.put("NOMBRE_UE", descripcionUE);
                        parms1.put("DIRECCION_UE", direccionUE);
                    }
                    String cIdProcedimiento = "";
                    String rfcProv = "";
                    query = "SELECT p.cIdProcedimiento, p.cIdRFC, p.fFormalizacion from mPedido as p WITH (NOLOCK) where cIdPedidoDefinitivo = '" + cPedidoDefinitivo + "'";
                    rs = stmt.executeQuery(query);
                    if (rs.next()) {
                        cIdProcedimiento = rs.getString(1);
                        rfcProv = rs.getString(2);
                        if (rfcProv == null)
                            rfcProv = "";
                        Date fechaPedido = rs.getDate(3);
                        Calendar cal = Calendar.getInstance(new Locale("es_MX"));
                        cal.setTime(fechaPedido);
                        String dia = "0";
                        String mes = "0";
                        if (cal.get(Calendar.DAY_OF_MONTH) < 10)
                            dia += cal.get(Calendar.DAY_OF_MONTH);
                        else
                            dia = cal.get(Calendar.DAY_OF_MONTH) + "";
                        if ((cal.get(Calendar.MONTH) + 1) < 10)
                            mes += (cal.get(Calendar.MONTH) + 1);
                        else
                            mes = (cal.get(Calendar.MONTH) + 1) + "";
                        parms1.put("RFC", rfcProv.trim());
                        parms1.put("DIA", dia);
                        parms1.put("MES", mes);
                    }
                    query = "SELECT pr.cRepresentanteLegal from mPedido as p WITH (NOLOCK) inner join mPedidoRepresentante as pr WITH (NOLOCK) on p.cIdPedidoDefinitivo = pr.cIdPedido where cIdPedidoDefinitivo = '" + cPedidoDefinitivo + "'";
                    rs = stmt.executeQuery(query);
                    if (rs.next()) {
                        String repLegal = rs.getString(1);
                        if (repLegal == null)
                            repLegal = "";
                        parms1.put("PERSONA_CONF", repLegal);
                    }
                    query = "SELECT * from mCatalogoProveedor as cp WITH (NOLOCK) inner join mCatalogoEntidadFederativa as cef WITH (NOLOCK) on cp.cIdEntidadFederativa = cef.cIdEntidadFederativa inner join mCatalogoProveedorTelefono as cpt WITH (NOLOCK) on cp.cIdRFC = cpt.cIdRFC where cp.cIdRFC = '" + rfcProv + "'";
                    rs = stmt.executeQuery(query);
                    if (rs.next()) {
                        String proveedor = rs.getString(2);
                        String direccionProv = "";
                        String calle = rs.getString(7);
                        String nuext = rs.getString(8);
                        String nuint = rs.getString(9);
                        if (calle != null)
                            direccionProv += calle.trim();
                        if (nuext != null && nuext.length() > 0)
                            direccionProv += " No. " + nuext.trim();
                        if (nuint != null && nuint.length() > 0)
                            direccionProv += " int. " + nuint.trim();
                        String colonia = rs.getString(10);
                        if (colonia != null)
                            colonia = colonia.trim();
                        else
                            colonia = "";
                        String municipio = rs.getString(11);
                        if (municipio != null)
                            municipio = municipio.trim();
                        else
                            municipio = "";
                        String cp = rs.getString(12);
                        if (cp != null)
                            cp = cp.trim();
                        else
                            cp = "";
                        String estado = rs.getString(19);
                        if (estado != null)
                            estado = estado.trim();
                        else
                            estado = "";
                        cp += " " + municipio;
                        if (estado.length() > 0) {
                            if (cp.length() > 0)
                                cp += ",";
                            cp += " " + estado;
                        }
                        String tel = rs.getString(23);
                        if (tel != null)
                            tel = tel.trim();
                        String repLegal = rs.getString(5);
                        if (repLegal != null)
                            repLegal = repLegal.trim();
                        parms1.put("PROVEEDOR", proveedor);
                        parms1.put("DOMICILIO", direccionProv);
                        parms1.put("COL", colonia);
                        parms1.put("CP", cp);
                        parms1.put("TEL", tel);
                    }
                    query = "SELECT DISTINCT cIdSolicitud FROM mConsolidadoSolicitud as cs WITH (NOLOCK) inner join mProcedimiento as p WITH (NOLOCK) on cs.cIdConsolidado = p.cIdConsolidado where cIdProcedimiento = '" + cIdProcedimiento + "' AND p.cEjercicio = '" + cEjercicio + "'";
                    rs = stmt.executeQuery(query);
                    String reqs = "";
                    Integer cont = 0;
                    while (rs.next()) {
                        if (cont < 2) {
                            String requi = rs.getString(1);
                            if (requi != null) {
                                if (reqs.length() > 0)
                                    reqs += " y " + requi;
                                else
                                    reqs += requi;
                                cont++;
                            }
                        } else {
                            reqs = "VARIAS";
                            break;
                        }
                    }
                    parms1.put("SOLICITUD_COM", reqs);
                    parms1.put("NUM_PE", cPedido);
                    parms1.put("ANIO", cEjercicio);
                    parms1.put("NUM_MOD", Formatter.numberToTextTimes(Integer.parseInt(nConsecutivoModif)));
                    String extImg = "";
                    query = "SELECT '$' + convert(varchar, mTotalAnterior, 1) as totalA, '$' + convert(varchar, mTotalModificacion,1) as totalM, '$' + CONVERT(varchar, mTotalNuevo, 1) as totalN, cep.cEstado, '$' + CONVERT(varchar, cast(mTotalAnterior / ((p.nIVA / 100.0) + 1) as money), 1) as subA, '$' + CONVERT(varchar, cast(mTotalNuevo / ((p.nIVA / 100.0) + 1) as money), 1) as subN, '$' + convert(varchar, cast(mTotalAnterior / ((p.nIVA / 100.0) + 1) * (p.nIVA / 100.0) as money), 1) as ivaA, '$' + CONVERT(varchar, cast(mTotalNuevo / ((p.nIVA / 100.0) + 1) * (p.nIVA / 100.0) as money), 1) as ivaN, pm.mTotalNuevo, pm.fMod, pm.cExtImagen from mPedidoModificado as pm WITH (NOLOCK) inner join mPedido as p WITH (NOLOCK) on pm.cIdPedidoDefinitivo = p.cIdPedidoDefinitivo inner join mCatalogoEstadoPedido as cep WITH (NOLOCK) on nEstado = cep.nIdEstado where pm.cIdPedidoDefinitivo = '" + cPedidoDefinitivo + "' and pm.nConsecutivoModificacion = " + nConsecutivoModif;
                    rs = stmt.executeQuery(query);
                    if (rs.next()) {
                        String totalAnterior = rs.getString(1);
                        String totalMod = rs.getString(2);
                        String totalNuevo = rs.getString(3);
                        String estado = rs.getString(4);
                        String subAnterior = rs.getString(5);
                        String subNuevo = rs.getString(6);
                        String ivaAnterior = rs.getString(7);
                        String ivaNuevo = rs.getString(8);
                        Double totalNuevoD = rs.getDouble(9);
                        parms1.put("IMPORTE_A", totalAnterior);
                        parms1.put("IMPORTE_TM", totalMod);
                        parms1.put("IMPORTE_T", totalNuevo);
                        parms1.put("IMPORTE_T_LETRA", Formatter.numberToText(totalNuevoD));
                        parms1.put("SUBTOTAL_A", subAnterior);
                        parms1.put("SUBTOTAL_T", subNuevo);
                        parms1.put("IVA_A", ivaAnterior);
                        parms1.put("IVA_T", ivaNuevo);
                        parms1.put("ESTADO", estado);
                        //REVERSO
                        Date fecha = rs.getDate(10);
                        String fechaS = "";
                        if (fecha != null) {
                            DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, new Locale("es-MX"));
                            fechaS = df.format(fecha);
                        }
                        extImg = rs.getString(11);
                        if (extImg == null)
                            extImg = "";
                        parms1.put("FECHA_MOD", fechaS);
                    }
                    query = "select * from mPedidoFirmantes as pf inner join mCatalogoFirmantes as cf on pf.nIdFirmante = cf.nIdFirmante and pf.cIdUnidadEjecutora = cf.cIdUnidadEjecutora where pf.cIdPedido = '" + cPedido + "' order by pf.nNumeroFirmante";
                    rs = stmt.executeQuery(query);
                    while (rs.next()) {
                        Integer tipo = rs.getInt(5);
                        String nombre = rs.getString(10);
                        String paterno = rs.getString(11);
                        if (paterno != null)
                            nombre += " " + paterno;
                        String materno = rs.getString(12);
                        if (materno != null)
                            nombre += " " + materno;
                        String puesto = rs.getString(13);
                        if (puesto != null)
                            puesto = puesto.trim();
                        else
                            puesto = "";
                        if (tipo.equals(1)) {
                            parms1.put("NOMBRE_E", nombre);
                            parms1.put("PUESTO_E", puesto);
                        } else if (tipo.equals(2)) {
                            parms1.put("NOMBRE_A", nombre);
                            parms1.put("PUESTO_A", puesto);
                        } else if (tipo.equals(3)) {
                            parms1.put("NOMBRE_R", nombre);
                            parms1.put("PUESTO_R", puesto);
                        }
                    }
                    query = "select p.cIdProcedimiento, ccp.cCategoria, p.cOficio from mProcedimiento as p WITH (NOLOCK) inner join mCatalogoCategoriaProcedimiento as ccp WITH (NOLOCK) on p.nIdCategoria = ccp.nIdCategoria where p.cIdProcedimiento = '" + cIdProcedimiento + "' and p.cEjercicio = '" + cEjercicio + "'";
                    rs = stmt.executeQuery(query);
                    if (rs.next()) {
                        String categoria = rs.getString(2);
                        if (categoria != null)
                            categoria.trim();
                        else
                            categoria = "";
                        String oficio = rs.getString(3);
                        if (oficio != null)
                            oficio.trim();
                        else
                            oficio = "";
                        parms1.put("CATEGORIA_PROCE", categoria);
                        parms1.put("OFICIO_PROCE", oficio);
                        parms1.put("ID_PROCE", cIdProcedimiento);
                    }
                    query = "select * from fn_mPedidoModificadoPartidasTodas('" + cEjercicio + "','" + cPedido + "','" + cPedidoDefinitivo + "'," + nConsecutivoModif + ")";
                    rs = stmt.executeQuery(query);
                    while (rs.next()) {
                        Integer lineaCons = rs.getInt(2);
                        String descripcion = rs.getString(4);
                        Integer cantidad = rs.getInt(5);
                        String unidad = rs.getString(11);
                        String precio = rs.getString(12);
                        String total = rs.getString(8);
                        String partida = "0";
                        if (consPartida < 10)
                            partida += consPartida;
                        else
                            partida = consPartida.toString();
                        numPartidaMap.put(lineaCons, partida);
                        consPartida++;
                        PedidoModLinea pml = new PedidoModLinea();
                        pml.setnIdLineaConsolidado(lineaCons);
                        pml.setTipo("A");
                        pml.setPartida(partida);
                        pml.setDescripcion(descripcion);
                        pml.setCantidad(cantidad);
                        pml.setUnidad(unidad);
                        pml.setPrecioUN(precio);
                        pml.setPrecioTN(total);
                        original.add(pml);
                        originalMap.put(pml.getnIdLineaConsolidado(), pml);
                    }
                    query = "select * from fn_mPedidoModificadoPartidasModificadas('" + cPedidoDefinitivo + "'," + nConsecutivoModif + ")";
                    rs = stmt.executeQuery(query);
                    while (rs.next()) {
                        Integer idLineaCons = rs.getInt(4);
                        String descripcion = rs.getString(13);
                        Integer cantidad = rs.getInt(9);
                        String unidad = rs.getString(15);
                        String precio = rs.getString(14);
                        String total = rs.getString(12);
                        PedidoModLinea pmlo = originalMap.get(idLineaCons);
                        PedidoModLinea pml = new PedidoModLinea();
                        pml.setnIdLineaConsolidado(idLineaCons);
                        if (cantidad < 0)
                            pml.setTipo("B");
                        else
                            pml.setTipo("C");
                        pml.setPartida(numPartidaMap.get(idLineaCons));
                        pml.setDescripcion(descripcion);
                        pml.setCantidad(pmlo.getCantidad() + cantidad);
                        pml.setUnidad(unidad);
                        pml.setPrecioUN(precio);
                        Double totalD = new Double(total.replace("$", "").replace(",", "")) + new Double(pmlo.getPrecioTN().replace("$", "").replace(",", ""));
                        Formatter formatter = new Formatter();
                        pml.setPrecioTN(formatter.currency(new Float(totalD)));
                        modificacion.add(pml);
                        modificacionMap.put(pml.getnIdLineaConsolidado(), pml);
                    }
                    PedidoModLinea.groupArrays(original, modificacion, originalMap, modificacionMap);
                    PedidoModLinea.fixLines(original, modificacion);
                    ArrayList<PedidoModHoja> detalles = PedidoModHoja.fixDetails(original, modificacion);
                    PedidoModJRDataSource ds = new PedidoModJRDataSource();
                    ds.setDetalles(detalles);
                    dataSource = ds;
                    String imgfullpath = getServletContext().getRealPath("docs") + File.separator + "pedidoMod" + File.separator;
                    imgfullpath += cPedidoDefinitivo.replace("/", "-") + "-" + nConsecutivoModif + "." + extImg;
                    parms1.put("IMAGE", imgfullpath);
                    parms1.put("SUBREPORT_DIR", getServletContext().getRealPath("Reportes"));
                } catch (Exception exc) {
                    throw new ServletException(exc);
                } finally {
                    try {
                        if (conn != null)
                            conn.close();
                        if (stmt != null)
                            stmt.close();
                        if (rs != null)
                            rs.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    conn = null;
                }
                break;
            }
            if (req.getParameter("rn").equalsIgnoreCase("rptJuntaAclaracion.jasper")) {
                Statement stmt = null;
                ResultSet rs = null;
                try {
                    String idProcedimiento = req.getParameter("cIdProcedimiento");
                    String idConsolidado = req.getParameter("cIdConsolidado");
                    String ejercicio = Calendar.getInstance().get(Calendar.YEAR) + "";
                    conn = DataSourceManager.getConnection(jniName);
                    stmt = conn.createStatement();
                    parms1.put("DIR_REPORT", getServletContext().getRealPath("Reportes"));
                    parms1.put("cIdProcedimiento", idProcedimiento);
                    //CARGA EL EJERCICIO FISCAL
                    String queryCons = "select vUA.D_DESCRIPCION as cUnidadAdministrativa, rtrim(ltrim(ccp.cCategoria)) as cCategoria, p.cDescripcion as cDescripcionProcedimiento, vUA.calle+' No. '+vUA.numExterior+(CASE WHEN rtrim(ltrim(vUA.numInterior))='' THEN '' ELSE CASE WHEN vUA.numInterior IS NULL THEN '' ELSE ' Int. '+vUA.numInterior END END)+' Col. '+vUA.colonia+', '+vUA.delegacionMunicipio+', '+CAST(vUA.codigoPostal AS VARCHAR)+', '+vUA.estado AS cDireccion " + "from mProcedimiento p WITH (NOLOCK), v_mUnidadesAdministrativas vUA WITH (NOLOCK), mCatalogoCategoriaProcedimiento ccp WITH (NOLOCK) " + "where p.cIdUnidadEjecutora=vUA.claveUnidadAdministrativa AND p.nIdCategoria=ccp.nIdCategoria AND p.cIdProcedimiento  = '" + idProcedimiento + "'";
                    rs = stmt.executeQuery(queryCons);
                    if (rs.next()) {
                        parms1.put("UNIDAD_EJECUTORA", rs.getString(1));
                        parms1.put("CATEGORIA_PROCEDIMIENTO", rs.getString(2));
                        parms1.put("DESCRIPCION_PROCEDIMIENTO", rs.getString(3));
                        parms1.put("DIRECCION", rs.getString(4));
                    }
                    queryCons = "SELECT DISTINCT cIdSolicitud FROM mConsolidadoSolicitud WITH (NOLOCK) where cIdConsolidado = '" + idConsolidado + "' AND cEjercicio = '" + ejercicio + "'";
                    rs = stmt.executeQuery(queryCons);
                    String reqs = "";
                    Integer cont = 0;
                    while (rs.next()) {
                        if (cont < 2) {
                            String requi = rs.getString(1);
                            if (requi != null) {
                                if (reqs.length() > 0)
                                    reqs += " y " + requi;
                                else
                                    reqs += requi;
                                cont++;
                            }
                        } else {
                            reqs = "VARIAS";
                            break;
                        }
                    }
                    parms1.put("REQUISICIONES", reqs);
                } catch (Exception exc) {
                    throw new ServletException(exc);
                } finally {
                    try {
                        if (conn != null)
                            conn.close();
                        if (stmt != null)
                            stmt.close();
                        if (rs != null)
                            rs.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    conn = null;
                }
                break;
            }
            if (req.getParameter("rn").equals("rptRequisiciones.jasper")) {
                parms1.put("cEjercicio", req.getParameter("cEjercicio"));
                parms1.put("cIdUnidadEjecutora", req.getParameter("cIdUnidadEjecutora"));
                parms1.put("nIdConsecutivo", req.getParameter("nIdConsecutivo"));
                parms1.put("cIdTipoSolicitud", req.getParameter("cIdTipoSolicitud"));
                parms1.put("formato", req.getParameter("formato"));
                parms1.put("SUBREPORT_DIR", getServletContext().getRealPath("Reportes"));
            }
            if (req.getParameter("rn").equals("Pedido.jasper")) {
                parms1.put("cEjercicio", req.getParameter("cEjercicio"));
                parms1.put("cIdUnidadEjecutora", req.getParameter("cIdUnidadEjecutora"));
                parms1.put("nIdConsecutivo", req.getParameter("nIdConsecutivo"));
                parms1.put("cIdTipoPedido", req.getParameter("cIdTipoPedido"));
                parms1.put("formato", req.getParameter("formato"));
                System.out.println("formato del pedido: " + req.getParameter("formato"));
                parms1.put("SUBREPORT_DIR", getServletContext().getRealPath("Reportes"));
            }
            if (req.getParameter("rn").equals("rptPedidoAnexo1.jasper")) {
                parms1.put("cEjercicio", req.getParameter("cEjercicio"));
                parms1.put("cIdUnidadEjecutora", req.getParameter("cIdUnidadEjecutora"));
                parms1.put("nIdConsecutivo", req.getParameter("nIdConsecutivo"));
                parms1.put("cIdTipoPedido", req.getParameter("cIdTipoPedido"));
                parms1.put("formato", req.getParameter("formato"));
                System.out.println("formato del pedido: " + req.getParameter("formato"));
                parms1.put("SUBREPORT_DIR", getServletContext().getRealPath("Reportes"));
            }
            if (req.getParameter("rn").equals("rptPedidoAnexo2.jasper")) {
                parms1.put("cEjercicio", req.getParameter("cEjercicio"));
                parms1.put("cIdUnidadEjecutora", req.getParameter("cIdUnidadEjecutora"));
                parms1.put("nIdConsecutivo", req.getParameter("nIdConsecutivo"));
                parms1.put("cIdTipoPedido", req.getParameter("cIdTipoPedido"));
                parms1.put("formato", req.getParameter("formato"));
                System.out.println("formato del pedido: " + req.getParameter("formato"));
                parms1.put("SUBREPORT_DIR", getServletContext().getRealPath("Reportes"));
            }
            if (req.getParameter("rn").equals("rptContratoAnexo.jasper")) {
                parms1.put("cEjercicio", req.getParameter("cEjercicio"));
                parms1.put("cIdUnidadEjecutora", req.getParameter("cIdUnidadEjecutora"));
                parms1.put("nIdConsecutivo", req.getParameter("nIdConsecutivo"));
                parms1.put("cIdTipoContrato", req.getParameter("cIdTipoContrato"));
                parms1.put("formato", req.getParameter("formato"));
                parms1.put("SUBREPORT_DIR", getServletContext().getRealPath("Reportes"));
            }
            if (req.getParameter("rn").equals("rptContratoAnexo1.jasper")) {
                parms1.put("cEjercicio", req.getParameter("cEjercicio"));
                parms1.put("cIdUnidadEjecutora", req.getParameter("cIdUnidadEjecutora"));
                parms1.put("nIdConsecutivo", req.getParameter("nIdConsecutivo"));
                parms1.put("cIdTipoContrato", req.getParameter("cIdTipoContrato"));
                parms1.put("formato", req.getParameter("formato"));
                parms1.put("SUBREPORT_DIR", getServletContext().getRealPath("Reportes"));
            }
            if (req.getParameter("rn").equals("rptContratoArrendamiento.jasper")) {
                parms1.put("cEjercicio", req.getParameter("cEjercicio"));
                parms1.put("cIdUnidadEjecutora", req.getParameter("cIdUnidadEjecutora"));
                parms1.put("nIdConsecutivo", req.getParameter("nIdConsecutivo"));
                parms1.put("cIdTipoContrato", req.getParameter("cIdTipoContrato"));
                parms1.put("formato", req.getParameter("formato"));
                parms1.put("SUBREPORT_DIR", getServletContext().getRealPath("Reportes"));
            }
            if (req.getParameter("rn").equals("rptPedidoClavesComplementarias.jasper")) {
                parms1.put("cEjercicio", req.getParameter("cEjercicio"));
                parms1.put("cIdUnidadEjecutora", req.getParameter("cIdUnidadEjecutora"));
                parms1.put("nIdConsecutivo", req.getParameter("nIdConsecutivo"));
                parms1.put("cIdTipoPedido", req.getParameter("cIdTipoPedido"));
                parms1.put("formato", req.getParameter("formato"));
                //System.out.println("formato del pedido: "+req.getParameter("formato"));
                parms1.put("SUBREPORT_DIR", getServletContext().getRealPath("Reportes"));
            }
            if (req.getParameter("rn").equals("ConsolidadoPartidaExcel.jasper")) {
                parms1.put("cEjercicio", req.getParameter("cEjercicio"));
                parms1.put("cIdUnidadEjecutora", req.getParameter("cIdUnidadEjecutora"));
                parms1.put("nIdConsecutivo", req.getParameter("nIdConsecutivo"));
                parms1.put("cIdTipoConsolidado", req.getParameter("cIdTipoConsolidado"));
            }
            if (req.getParameter("rn").equals("rptVolanteDevolucion.jasper")) {
                String reportPath2 = getServletContext().getRealPath("/Reportes/");
                File reportsDir = new File(reportPath2);
                if (!reportsDir.exists()) {
                    throw new FileNotFoundException(String.valueOf(reportPath2));
                }
                //parms1.put( JRParameter.REPORT_FILE_RESOLVER, new SimpleFileResolver(reportsDir));
                //parms1.put("cIdContrato", req.getParameter("cIdContrato"));
            }
            // clausulas
            if (req.getParameter("rn").equals("clausulas.jasper")) {
                parms1.put("cPedidoDefinitivo", req.getParameter("cPedidoDefinitivo"));
                parms1.put("SUBREPORT_DIR", getServletContext().getRealPath("Reportes"));
            }
            //facturas relacion gastos
            if (req.getParameter("rn").equals("relacionGastosReporteFacturas.jasper")) {
                parms1.put("cIdDocumento", req.getParameter("cIdDocumento"));
                parms1.put("clabeInterBancaria", req.getParameter("clabeInterBancaria"));
                parms1.put("SUBREPORT_DIR", getServletContext().getRealPath("Reportes"));
            }
            //Resumen de Claves Presupuestales de Pedidos y Contratos
            if (req.getParameter("rn").equalsIgnoreCase("rptDocumentoResumenClaves.jasper")) {
                try {
                    parms1.put("unidadEjecutoraStr", req.getParameter("unidadEjecutoraStr"));
                    parms1.put("cIdDocumento", req.getParameter("documento"));
                    parms1.put("documentoDefinitivo", req.getParameter("documentoDefinitivo"));
                    parms1.put("cEjercicioFiscal", req.getParameter("cEjercicio"));
                    parms1.put("SUBREPORT_DIR", getServletContext().getRealPath("Reportes"));
                } catch (Exception e) {
                    log.error("Error al obtener los parametros.");
                    e.printStackTrace();
                }
            }
            //pago directo
            if (req.getParameter("rn").equals("pagoDirectoReporte.jasper")) {
                parms1.put("cIdDocumento", req.getParameter("cIdDocumento"));
                parms1.put("SUBREPORT_DIR", getServletContext().getRealPath("Reportes"));
            }
            //Reporte de requisiciones
            if (req.getParameter("rn").equals("ReporteRequisiciones.jasper")) {
                parms1.put("formato", req.getParameter("formato"));
                parms1.put("where_", req.getParameter("where_"));
                parms1.put("SUBREPORT_DIR", getServletContext().getRealPath("Reportes"));
                log.info("Ejecutando el reporte : " + req.getParameter("rn") + " con los parametros :" + req.getParameter("where_"));
            }
            //Reporte de consolidados
            if (req.getParameter("rn").equals("ReporteConsultaCons.jasper")) {
                parms1.put("formato", req.getParameter("formato"));
                parms1.put("where_", req.getParameter("where_"));
                parms1.put("SUBREPORT_DIR", getServletContext().getRealPath("Reportes"));
                log.info("Ejecutando el reporte : " + req.getParameter("rn") + " con los parametros :" + req.getParameter("where_"));
            }
            //Reporte de procedimientos
            if (req.getParameter("rn").equals("ReporteConsultaProcedimientos.jasper")) {
                parms1.put("formato", req.getParameter("formato"));
                parms1.put("where_", req.getParameter("where_"));
                parms1.put("SUBREPORT_DIR", getServletContext().getRealPath("Reportes"));
                log.info("Ejecutando el reporte : " + req.getParameter("rn") + " con los parametros :" + req.getParameter("where_"));
            }
            //Reporte de Pedidos
            if (req.getParameter("rn").equals("ReporteConsultaPedidos.jasper")) {
                parms1.put("formato", req.getParameter("formato"));
                parms1.put("where_", req.getParameter("where_"));
                parms1.put("SUBREPORT_DIR", getServletContext().getRealPath("Reportes"));
                log.info("Ejecutando el reporte : " + req.getParameter("rn") + " con los parametros :" + req.getParameter("where_"));
            }
            //Reporte de Contratos
            if (req.getParameter("rn").equals("ReporteConsultaContratos.jasper")) {
                parms1.put("formato", req.getParameter("formato"));
                parms1.put("where_", req.getParameter("where_"));
                parms1.put("SUBREPORT_DIR", getServletContext().getRealPath("Reportes"));
                log.info("Ejecutando el reporte : " + req.getParameter("rn") + " con los parametros :" + req.getParameter("where_"));
            }
        }
        try {
            conn = DataSourceManager.getConnection(jniName);
            //HttpSession sesion= req.getSession();
            //Carga el reporte en BUFFER
            if (req.getParameter("directorio") == null) {
                out = resp.getOutputStream();
                in = new FileInputStream(reportPath1 + "\\" + reportName);
                if (req.getParameter("xls") != null && "SI".equals(req.getParameter("xls"))) {
                    resp.setContentType("application/vnd.ms-excel");
                    byte[] bytes = null;
                    JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath1 + "\\" + reportName, parms1, conn);
                    bytes = convertJasperPrintToExcel(jasperPrint);
                    resp.setContentLength(bytes.length);
                    out.write(bytes, 0, bytes.length);
                } else //&& req.getParameter("formato")!=null
                if ("xls".equalsIgnoreCase(req.getParameter("formato")) && req.getParameter("formato") != null) {
                    String filename = "Reporte.xls";
                    resp.setContentType("application/vnd.ms-excel");
                    String disposition = "attachment; fileName=" + filename;
                    resp.setHeader("Content-Disposition", disposition);
                    //resp.setContentType("application/vnd.ms-excel");
                    byte[] bytes = null;
                    JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath1 + "\\" + reportName, parms1, conn);
                    bytes = convertJasperPrintToExcel(jasperPrint);
                    resp.setContentLength(bytes.length);
                    out.write(bytes, 0, bytes.length);
                } else //&& req.getParameter("formato")!=null
                if ("doc".equalsIgnoreCase(req.getParameter("formato")) && req.getParameter("formato") != null) {
                    resp.addHeader("Content-disposition", "attachment; filename=report.docx");
                    resp.setContentType("application/doc");
                    byte[] bytes = null;
                    JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath1 + "\\" + reportName, parms1, conn);
                    bytes = convertJasperPrintToWRD(jasperPrint);
                    resp.setContentLength(bytes.length);
                    out.write(bytes, 0, bytes.length);
                } else if (req.getParameter("formato") != null && req.getParameter("formato").equalsIgnoreCase("dsdoc")) {
                    resp.addHeader("Content-disposition", "attachment; filename=report.docx");
                    resp.setContentType("application/doc");
                    byte[] bytes = null;
                    JasperPrint jasperPrint = null;
                    if (req.getParameter("rn").equalsIgnoreCase("rptActaApte.jasper") || req.getParameter("rn").equalsIgnoreCase("PedidoModificado.jasper")) {
                        jasperPrint = JasperFillManager.fillReport(in, parms1, dataSource);
                    }
                    if (req.getParameter("rn").equalsIgnoreCase("rptActaFallo.jasper")) {
                        jasperPrint = JasperFillManager.fillReport(in, parms1, conn);
                    }
                    if (req.getParameter("rn").equalsIgnoreCase("rptJuntaAclaracion.jasper")) {
                        jasperPrint = JasperFillManager.fillReport(in, parms1, conn);
                    }
                    bytes = convertJasperPrintToWRD(jasperPrint);
                    resp.setContentLength(bytes.length);
                    out.write(bytes, 0, bytes.length);
                } else if (req.getParameter("formato") != null && req.getParameter("formato").equalsIgnoreCase("dspdf")) {
                    resp.setContentType("application/pdf");
                    //Elena Angeles: Agregado para los reportes...
                    resp.setHeader("Content-Disposition", "attachment; filename=\"" + ("".equals(reportName) ? "Reporte" : reportName) + ".pdf\"");
                    JasperRunManager.runReportToPdfStream(in, out, (Map<String, Object>) parms1, dataSource);
                } else {
                    resp.setContentType("application/pdf");
                    //Elena Angeles: Agregado para los reportes...
                    resp.setHeader("Content-Disposition", "attachment; filename=\"" + ("".equals(reportName) ? "Reporte" : reportName) + ".pdf\"");
                    JasperRunManager.runReportToPdfStream(in, out, (Map<String, Object>) parms1, conn);
                    //sesion.setAttribute("Termino", "uno");
                }
                out.flush();
                out.close();
            } else //Carga el archivo en directorio TEMPORAL
            if (req.getParameter("directorio").toUpperCase().equals("TEMPORAL")) {
                if ("pdf".equals(req.getParameter("formato")) && req.getParameter("formato") != null) {
                    File file = new File(System.getProperty("java.io.tmpdir") + File.separatorChar + reportName.replace(".jasper", ".pdf"));
                    JasperReport reporte = (JasperReport) JRLoader.loadObjectFromFile(reportPath1 + "\\" + reportName);
                    JasperPrint jp = JasperFillManager.fillReport(reporte, parms1, conn);
                    JRExporter je = new JRPdfExporter();
                    je.setParameter(JRExporterParameter.JASPER_PRINT, jp);
                    je.setParameter(JRExporterParameter.OUTPUT_FILE, file);
                    je.exportReport();
                    if (req.getParameter("rn").equals("Pedido.jasper")) {
                        file = new File(System.getProperty("java.io.tmpdir") + File.separatorChar + "clausulado.pdf");
                        parms1.clear();
                        parms1.put("SUBREPORT_DIR", getServletContext().getRealPath("Reportes"));
                        parms1.put("cPedidoDefinitivo", req.getParameter("cPedidoDefinitivo"));
                        parms1.put("cIdRFC", req.getParameter("cIdRFC"));
                        reporte = (JasperReport) JRLoader.loadObjectFromFile(reportPath1 + "\\clausulado.jasper");
                        jp = JasperFillManager.fillReport(reporte, parms1, conn);
                        je = new JRPdfExporter();
                        je.setParameter(JRExporterParameter.JASPER_PRINT, jp);
                        je.setParameter(JRExporterParameter.OUTPUT_FILE, file);
                        je.exportReport();
                        IntercalarPedidoPDF ipp = new IntercalarPedidoPDF(System.getProperty("java.io.tmpdir") + File.separatorChar + reportName.replace(".jasper", ".pdf"), System.getProperty("java.io.tmpdir") + File.separatorChar + "clausulado.pdf", System.getProperty("java.io.tmpdir") + File.separatorChar + "pdfUnido.pdf");
                        ipp.execute();
                        //mostrar archivo
                        resp.setContentType("application/pdf");
                        resp.setHeader("Content-Disposition", "attachment; filename=" + ipp.getRutaFileIntercalado());
                        ServletOutputStream outS = resp.getOutputStream();
                        FileInputStream fileInput1 = new FileInputStream(ipp.getRutaFileIntercalado());
                        BufferedInputStream bufferedInput1 = new BufferedInputStream(fileInput1);
                        BufferedOutputStream bufferedOutput = new BufferedOutputStream(outS);
                        int leidos1 = 0;
                        // Bucle para leer de un fichero y escribir en el otro.
                        byte[] array1 = new byte[(1024 * 1024) * 6];
                        leidos1 = bufferedInput1.read(array1);
                        while (leidos1 > 0) {
                            bufferedOutput.write(array1, 0, leidos1);
                            leidos1 = bufferedInput1.read(array1);
                        }
                        bufferedOutput.flush();
                        bufferedOutput.close();
                        bufferedInput1.close();
                        ipp.deleteFile(ipp.getRutaPDF1());
                        ipp.deleteFile(ipp.getRutaPDF2());
                        ipp.deleteFile(ipp.getRutaFileIntercalado());
                    }
                }
                if (req.getParameter("formato") != null && req.getParameter("formato").equals("dspdf")) {
                    if (req.getParameter("rn").equals("PedidoModificado.jasper")) {
                        File file = new File(System.getProperty("java.io.tmpdir") + File.separatorChar + "PedidoModificado.pdf");
                        JasperReport reporte = (JasperReport) JRLoader.loadObjectFromFile(reportPath1 + "PedidoModificado.jasper");
                        JasperPrint jp = JasperFillManager.fillReport(reporte, parms1, dataSource);
                        JRExporter je = new JRPdfExporter();
                        je.setParameter(JRExporterParameter.JASPER_PRINT, jp);
                        je.setParameter(JRExporterParameter.OUTPUT_FILE, file);
                        je.exportReport();
                        file = new File(System.getProperty("java.io.tmpdir") + File.separatorChar + "PedidoModificadoObservaciones.pdf");
                        reporte = (JasperReport) JRLoader.loadObjectFromFile(reportPath1 + "PedidoModificadoObservaciones.jasper");
                        jp = JasperFillManager.fillReport(reporte, parms1, conn);
                        je = new JRPdfExporter();
                        je.setParameter(JRExporterParameter.JASPER_PRINT, jp);
                        je.setParameter(JRExporterParameter.OUTPUT_FILE, file);
                        je.exportReport();
                        IntercalarPedidoPDF ipp = new IntercalarPedidoPDF(System.getProperty("java.io.tmpdir") + File.separatorChar + "PedidoModificado.pdf", System.getProperty("java.io.tmpdir") + File.separatorChar + "PedidoModificadoObservaciones.pdf", System.getProperty("java.io.tmpdir") + File.separatorChar + "pdfUnido.pdf");
                        ipp.setPeidoModificado(true);
                        ipp.execute();
                        //mostrar archivo
                        resp.setContentType("application/pdf");
                        resp.setHeader("Content-Disposition", "attachment; filename=PedidoModificado.pdf");
                        ServletOutputStream outS = resp.getOutputStream();
                        FileInputStream fileInput1 = new FileInputStream(ipp.getRutaFileIntercalado());
                        BufferedInputStream bufferedInput1 = new BufferedInputStream(fileInput1);
                        BufferedOutputStream bufferedOutput = new BufferedOutputStream(outS);
                        int leidos1 = 0;
                        // Bucle para leer de un fichero y escribir en el otro.
                        byte[] array1 = new byte[(1024 * 1024) * 6];
                        leidos1 = bufferedInput1.read(array1);
                        while (leidos1 > 0) {
                            bufferedOutput.write(array1, 0, leidos1);
                            leidos1 = bufferedInput1.read(array1);
                        }
                        bufferedOutput.flush();
                        bufferedOutput.close();
                        bufferedInput1.close();
                        ipp.deleteFile(ipp.getRutaPDF1());
                        ipp.deleteFile(ipp.getRutaPDF2());
                        ipp.deleteFile(ipp.getRutaFileIntercalado());
                    }
                }
            }
        } catch (IOException exc) {
            exc.printStackTrace();
            throw new ServletException(exc);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.close();
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            conn = null;
            in = null;
            out = null;
            //resp.sendRedirect("../Generador/SAICYS/Procesando.jsp?indicaMensaje=1");
        }
    }

    public void runReport(HttpServletRequest req, HttpServletResponse resp, String reportName, Map parms) throws ServletException {
        Connection conn = null;
        try {
            conn = DataSourceManager.getConnection(jniName);
            new Reportes().execute(conn, req, resp, reportName, parms);
        } catch (Exception exc) {
            throw new ServletException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            conn = null;
        }
    }

    private byte[] convertJasperPrintToExcel(JasperPrint jasperPrint) throws JRException {
        try {
            if (jasperPrint == null)
                throw new NullPointerException();
            ByteArrayOutputStream outExcel = new ByteArrayOutputStream();
            JRXlsExporter exporter = new JRXlsExporter();
            exporter.setParameter(JRXlsExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, outExcel);
            exporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
            exporter.exportReport();
            return outExcel.toByteArray();
        } catch (Exception ex) {
            throw new ReportsException(ex);
        }
    }

    //Convertir a WORD
    private byte[] convertJasperPrintToWRD(JasperPrint jasperPrint) throws JRException {
        try {
            if (jasperPrint == null)
                throw new NullPointerException();
            ByteArrayOutputStream outWRD = new ByteArrayOutputStream();
            JRDocxExporter exporter = new JRDocxExporter();
            exporter.setParameter(JRDocxExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRDocxExporterParameter.CHARACTER_ENCODING, "UTF-8");
            exporter.setParameter(JRDocxExporterParameter.OUTPUT_STREAM, outWRD);
            //exporter.setParameter(JRDocxExporterParameter.FLEXIBLE_ROW_HEIGHT, Boolean.TRUE);
            exporter.exportReport();
            return outWRD.toByteArray();
        } catch (Exception e) {
            // TODO: handle exception
            throw new ReportsException(e);
        }
    }
}
