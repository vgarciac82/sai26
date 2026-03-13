package com.syc.admin.servlet;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.syc.adquisiciones.core.IntercalarPedidoPDF;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.servlet.GestionServlet;
import com.syc.gestion.util.Util;
import com.syc.reportes.core.CompromisosManager;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporterParameter;
import net.sf.jasperreports.engine.util.JRLoader;


public class SeguridadCatalogos extends HttpServlet {

	private static final long	serialVersionUID	= 1L;
	private String				jniName				= null;
	private static Logger		log					= Logger.getLogger( GestionServlet.class );
	private static final File	TEMP_DIR			= new File( System.getProperty( "java.io.tmpdir" ) );

	public SeguridadCatalogos( ) {

		super();

		try {
			InitialContext ic = new InitialContext();
			jniName = ( String ) ic.lookup( "java:comp/env/dataSourceRefName" );

			if ( jniName == null ) {
				jniName = "jdbc/gestion";
				log.info( "Environment Entry \"dataSourceRefName\" nula usando default \"" + jniName + "\"" );
			} else
				log.info( "dataSourceRefName=" + jniName );
		} catch ( NamingException exc ) {
			jniName = "jdbc/gestion";
			log.info( "Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"" );
		}
	}

	public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {

		doPost( request, response );
	}

	public void service( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {

		doPost( request, response );
	}

	public void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {

		int intAccion = -1;
		int intCatalogo = -1;
		String Accion = request.getParameter( "accion" );
		String Catalogo = request.getParameter( "catalogo" );

		if ( Accion.equals( "add" ) ) {
			intAccion = 0;
		}

		if ( Accion.equals( "up" ) ) {
			intAccion = 1;
		}

		if ( Accion.equals( "del" ) ) {
			intAccion = 2;
		}

		CatEmpleado ce = null;
		CatCargo cc = null;
		CatArea ca = null;
		CatRemArea cra = null;
		CatRemPersona crp = null;

		if ( Catalogo.equals( "EMPLEADO" ) ) {

			intCatalogo = 0;
			ce = new CatEmpleado();
			ce.setId_empleado( Integer.parseInt( request.getParameter( "txtid_empleado" ) ) );
			ce.setCe_nombre_completo( request.getParameter( "txtce_nombre_completo" ) );
			ce.setCe_ap_paterno( request.getParameter( "txtce_ap_paterno" ) );
			ce.setCe_ap_materno( request.getParameter( "txtce_ap_materno" ) );
			ce.setCe_os_responsable( request.getParameter( "txtce_os_responsable" ) );
			ce.setSalutacion( request.getParameter( "txtsalutacion" ) );
			ce.setTipo_rem_des( request.getParameter( "txttipo_rem_des" ) );

		} else if ( Catalogo.equals( "CARGO" ) ) {
			intCatalogo = 1;
			cc = new CatCargo();
			cc.setId_cargo( Integer.parseInt( request.getParameter( "txtid_cargo" ) ) );
			cc.setCc_descripcion( request.getParameter( "txtcc_descripcion" ) );

		} else if ( Catalogo.equals( "AREA" ) ) {
			intCatalogo = 2;
			ca = new CatArea();

			ca.setId_area( request.getParameter( "txtid_area" ) );
			ca.setD_descripcion( request.getParameter( "txtd_descripcion" ) );
			ca.setTipo_area( request.getParameter( "txttipo_area" ) );
			ca.setPrefijo_folio( request.getParameter( "txtprefijo_folio" ) );

		} else if ( Catalogo.equals( "CONTRARECIBO" ) ) {
			intCatalogo = 2;
		 
		} else if ( Catalogo.equals( "ANEXO" ) || Catalogo.equals( "RETENCIONES" ) ) {
			intCatalogo = 2;

		} else if ( Catalogo.equals( "REMITENTE" ) ) {
			intCatalogo = 3;
			cra = new CatRemArea();

			cra.setCra_id_area( request.getParameter( "txtcra_id_area" ) );
			cra.setCra_descripcion( request.getParameter( "txtcra_descripcion" ) );
			cra.setTipo( request.getParameter( "txttipo" ) );
			cra.setOrden( Integer.parseInt( request.getParameter( "txtorden" ) ) );

		} else if ( Catalogo.equals( "PERSONA" ) ) {
			intCatalogo = 4;
			crp = new CatRemPersona();

			crp.setCrp_id_persona( Integer.parseInt( request.getParameter( "txtcrp_id_persona" ) ) );
			crp.setCrp_nombres( request.getParameter( "txtcrp_nombres" ) );
			crp.setCrp_apaterno( request.getParameter( "txtcrp_apaterno" ) );
			crp.setCrp_amaterno( request.getParameter( "txtcrp_amaterno" ) );
			crp.setCrp_id_area( request.getParameter( "txtcrp_id_area" ) );
			crp.setCrp_puesto( request.getParameter( "txtcrp_puesto" ) );

		}

		if ( !Catalogo.equals( "REPORTE" ) && !Accion.equals( "run" ) ) {

			SeguridadCatalogoBusinessLogic segCa = new SeguridadCatalogoBusinessLogic( jniName );
			switch ( intAccion ) {
				case 0:// instert

					try {
						switch ( intCatalogo ) {
							case 0: // Empleado
								segCa.agregaEmpleado( ce );
							break;
							case 1: // Cargo
								segCa.agregaCargo( cc );
							break;
							case 2: // Area
								segCa.agregaArea( ca );
							break;
							case 3: // Remitente
								segCa.agregaRemArea( cra );
							break;
							case 4: // Persona
								segCa.agregaRemPersona( crp );
							break;
							case 5: // Persona
								segCa.agregaRemPersona( crp );
							break;
						}
					} catch ( GestionException exc ) {
						log.error( "Agregando " + Catalogo, exc );
						throw new ServletException( exc );
					}
				break;

				case 1:// update
				{
					try {
						switch ( intCatalogo ) {
							case 0: // Empleado
								segCa.actualizaEmpleado( ce );
							break;
							case 1: // Cargo
								segCa.actualizaCargo( cc );
							break;
							case 2: // Area
								segCa.actualizaArea( ca );
							break;
							case 3: // Remitente
								segCa.actualizaRemArea( cra );
							break;
							case 4: // Persona
								segCa.actualizaRemPersona( crp );
							break;
						}
					} catch ( GestionException exc ) {
						log.error( "Actualizando " + Catalogo, exc );
						throw new ServletException( exc );
					}
					break;
				}

				case 2:// delete
				{
					try {
						switch ( intCatalogo ) {
							case 0: // Empleado
								segCa.borraEmpleado( ce );
							break;
							case 1: // Cargo
								segCa.borraCargo( cc );
							break;
							case 2: // Areas
								segCa.borraArea( ca );
							break;
							case 3: // Remitente
								segCa.borraRemArea( cra );
							break;
							case 4: // Persona
								segCa.borraRemPersona( crp );
							break;
						}
					} catch ( GestionException exc ) {
						log.error( "Eliminando " + Catalogo, exc );
						throw new ServletException( exc );
					}
					break;
				}
			}

			StringBuffer xmlResp = getRespuesta( "status" );

			response.setContentType( "text/xml" );
			response.setHeader( "Cache-Control", "no-cache" );
			PrintWriter out = response.getWriter();
			out.println( xmlResp.toString() );
			System.out.println( xmlResp.toString() );
		} else {
			RunReport( request, response );
		}
	}

	private StringBuffer getRespuesta( String resp ) {

		StringBuffer xmlOut = new StringBuffer();

		xmlOut.append( "<respuesta>" );
		xmlOut.append( "<estado valor='" );
		xmlOut.append( resp );
		xmlOut.append( "'/></respuesta>" );

		return xmlOut;
	}

	void RunReport(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String reportName = req.getParameter("rn");
		
		if (reportName == null)
			throw new ServletException("Llamada invï¿½lida");

		if ("".equals(reportName))
			throw new ServletException("Valor invï¿½lido");

		String reportPath1 = getServletContext().getRealPath("Reportes" + File.separator);

		if (reportPath1 == null) { // Busca en el WAR
			URL url = getClass().getResource(reportName);
			if (url == null)
				throw new ServletException("No se encontro el reporte " + reportName);

			reportPath1 = url.getPath();
			if ((reportPath1 == null) || (reportPath1.length() == 0))
				throw new ServletException("No se encontro el reporte " + reportName);
		}

		Map <String, Object> parms1 = new HashMap<String, Object>();
		Enumeration pnames = req.getParameterNames();

		//String whereEstruc = ""; //Esteban Badillo. Fecha: 29/Oct/2009. Se agrega para mejorar la condicion where de areas hijas del reporte.
		//String estruc = ""; //Esteban Badillo. Fecha: 29/Oct/2009. Tambien se agrega para el reporte.

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
			 * 	Se agregan los parametros para el Reporte Estadï¿½stico por Area para agregar solamente los asuntos que hayan sido
			 * 	turnados desde el area seleccionada a sus ï¿½reas hijas.
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
			parms1.put(name, StringUtils.trimToEmpty(  value ) );
			parms1.put("SUBREPORT_DIR", reportPath1);
			
			/********************************************************************************************************
			 *SE AÑADE REPORTE DE OFICIO PARA CONTRATO FEDERALIZADO.
			 ********************************************************************************************************/
			if("rptContratoFederalizado.jasper".equals(req.getParameter("rn"))){
				parms1.put("SUBREPORT_DIR", reportPath1);
				parms1.put("nFolioCompromiso",  new Integer( req.getParameter("nFolioCompromiso").trim() ) );
			}
			
			/********************************************************************************************************
			 *SE AÑADE REPORTE DE REGISTRO DE INGRESOS :  INGRESOS PROPIOS -- INGRESOS FONDOS FISCALES.
			 ********************************************************************************************************/
			if("PolizaPenas.jasper".equals(req.getParameter("rn"))){
				parms1.put("SUBREPORT_DIR", reportPath1);								
			}
			if("PolizaRegistroIngresos.jasper".equals(req.getParameter("rn"))){
				parms1.put("SUBREPORT_DIR", reportPath1);	
				parms1.put("whereFolio",  req.getParameter("whereFolio").trim() ) ;
			}
			
			if("PolizaReintegro.jasper".equals(req.getParameter("rn"))){
				parms1.put("SUBREPORT_DIR", reportPath1);				
			}
			
			if("PolizaInformeComision.jasper".equals(req.getParameter("rn"))){
				parms1.put("SUBREPORT_DIR", reportPath1+ File.separatorChar);
				parms1.put("whereFolio",  new Integer( req.getParameter("whereFolio").trim() ) );
			}
			
			/********************************************************************************************************
			 *SE AÑADE COMO PARAMETRO LA DIRECCION DEL SUBREPORTE PARA LAS SOLICITUDES DE PAGO 
			 ********************************************************************************************************/
			if (req.getParameter("rn").equals("PolizaPago.jasper") || req.getParameter("rn").equals("PolizaPagoN.jasper") || req.getParameter("rn").equals("Anexo1.jasper") ) {
				parms1.put("SUBREPORT_DIR", reportPath1);
			}
			
			if( req.getParameter("rn").equals("PolizaPenas.jasper") ){
				parms1.put("SUBREPORT_DIR", reportPath1);
				parms1.put("whereFolio", req.getParameter("whereFolio"));
				
			}
			
			if( req.getParameter("rn").equals("PolizaViaticos_FIEL.jasper") ){
				parms1.put("SUBREPORT_DIR", reportPath1);
				parms1.put("idComision", req.getParameter("whereFolio"));
				
			}
			
			if( req.getParameter("rn").equals("PolizaViaticos.jasper") ){
				parms1.put("SUBREPORT_DIR", reportPath1);
				parms1.put("idComision", req.getParameter("whereFolio"));
				
			}
			
			if( req.getParameter("rn").equals("polizaCertificadoTransito.jasper") ){
				parms1.put("SUBREPORT_DIR", reportPath1);
				parms1.put("whereFolio", req.getParameter("whereFolio"));
				
			}
			/**********************************************************************************************************************
			 *SE AÑADE COMO PARAMETRO LA DIRECCION DEL SUBREPORTE PARA EL REPORTE DE OPERACIONES AJENAS Y DISMINUCION DE DEVENGADO*
			 **********************************************************************************************************************/
			if (req.getParameter("rn").equals("PolizaDisminucionDevengado.jasper") || req.getParameter("rn").equals("PolizaOperAjenas.jasper") || req.getParameter("rn").equals("OperAjenas.jasper") || req.getParameter("rn").equals("PolizaReintegroCaja.jasper")) {
				parms1.put("SUBREPORT_DIR", reportPath1);
			}
			
			/**********************************************************************************************************************
			 *SE AÑADE COMO PARAMETRO LA DIRECCION DEL SUBREPORTE PARA EL REPORTE DEL ANEXO 1
			 **********************************************************************************************************************/
			if (req.getParameter("rn").equals("PolizaRecxPagar.jasper")) {
				parms1.put("SUBREPORT_DIR", reportPath1);
			}
			/***** SE AGREGAN LOS PARAMETROS DEL REPORTE DE RECTIFICACION *****/
			if (req.getParameter("rn").equals("PolizaRectificacion.jasper")) {
				parms1.put("SUBREPORT_DIR", reportPath1);
				parms1.put("whereFolio", req.getParameter("whereFolio"));
				parms1.put("whereTipo", req.getParameter("whereTipo"));
			}

			/********************************************************************************************************
			 PARAMETROS DE REPORTE GENERAL
			*********************************************************************************************************/
			if (req.getParameter("rn").equals("rpt_mProgramaAnualOrdenadoPorPartida_xls.jasper")) {
				//System.out.println("Esta obteniendo los parametros del repote Anual");
				//parms1.put ("fechainicio", new Date() );
				parms1.put("cIdUnidadEjecutora", req.getParameter("cIdUnidadEjecutora"));
				
				parms1.put("formato", req.getParameter("formato"));
			
			}
			
			
			/********************************************************************************************************
			PARAMETROS DE REPORTE GENERAL Acumulado
			*********************************************************************************************************/
			if (req.getParameter("rn").equals("rpt_mProgramaAnualOrdenadoPorPartidaAcumulado.jasper")) {
				//System.out.println("Esta obteniendo los parametros del repote Anual acumulado");
				//parms1.put ("fechainicio", new Date() );
				parms1.put("cIdUnidadEjecutora", req.getParameter("cIdUnidadEjecutora"));
				parms1.put("formato", req.getParameter("formato"));
			
			}
			/********************************************************************************************************
			PARAMETROS DE REPORTE Anual por Partida y trimestre
			*********************************************************************************************************/
			if (req.getParameter("rn").equals("rpt_mProgramaAnualPorPartidayTrimestre.jasper")) {
				//System.out.println("Esta obteniendo los parametros del repote Anual acumulado");
				//parms1.put ("fechainicio", new Date() );
				parms1.put("cIdUnidadEjecutora", req.getParameter("cIdUnidadEjecutora"));
				parms1.put("formato", req.getParameter("formato"));
				//System.out.println("Formato : "+req.getParameter("formato"));
			}
			/********************************************************************************************************
			PARAMETROS DE REPORTE Anual por Partida y trimestre
			*********************************************************************************************************/
			if (req.getParameter("rn").equals("fn_mProgramaAnualMontosPorCapituloReporte.jasper")) {
				//System.out.println("Esta obteniendo los parametros del repote Anual acumulado");
				//parms1.put ("fechainicio", new Date() );
				parms1.put("cIdUnidadEjecutora", req.getParameter("cIdUnidadEjecutora"));
				parms1.put("formato", req.getParameter("formato"));
				System.out.println("Formato : "+req.getParameter("formato"));
			}
			/********************************************************************************************************
			PARAMETROS DE REPORTE Anual por Partida y trimestre
			*********************************************************************************************************/
			if (req.getParameter("rn").equals("rpt_mProgramaAnualResumidoPorUnidadyCapituloRestandoPartidas.jasper")) {
				//System.out.println("Esta obteniendo los parametros del repote Anual acumulado");
				//parms1.put ("fechainicio", new Date() );
				parms1.put("cIdUnidadEjecutora", req.getParameter("cIdUnidadEjecutora"));
				parms1.put("formato", req.getParameter("formato"));
				System.out.println("Formato : "+req.getParameter("formato"));
			}
			/********************************************************************************************************
			PARAMETROS DE REPORTE Anual por Partida y trimestre
			*********************************************************************************************************/
			if (req.getParameter("rn").equals("rpt_CompraNET_GERARDO.jasper")) {
				//System.out.println("Esta obteniendo los parametros del repote Anual acumulado");
				//parms1.put ("fechainicio", new Date() );
				parms1.put("cIdUnidadEjecutora", req.getParameter("cIdUnidadEjecutora"));
				parms1.put("formato", req.getParameter("formato"));
				System.out.println("Formato : "+req.getParameter("formato"));
			}
			/********************************************************************************************************
			Programa Anual de Adquisiciones CUCOPS sin documentos asociados
			*********************************************************************************************************/
			if (req.getParameter("rn").equals("rpt_ProgramaAnualdeAdquisicionesCUCOPSsinDocumentosAsociados.jasper")) {
				//System.out.println("Esta obteniendo los parametros del repote Anual acumulado");
				//parms1.put ("fechainicio", new Date() );
				parms1.put("cIdUnidadEjecutora", req.getParameter("cIdUnidadEjecutora"));
				parms1.put("formato", req.getParameter("formato"));
				System.out.println("Formato : "+req.getParameter("formato"));
			}
			/********************************************************************************************************
				Reporte de Contrato de Arrendamiento
			*********************************************************************************************************/
			if("reporteContratoDiverso.jasper".equals(req.getParameter("rn"))){
				parms1.put("SUBREPORT_DIR", reportPath1);
				log.info("SUBREPORT_DIR: "+reportPath1);
			}
			/********************************************************************************************************
				Reporte de Contrato de OBRA
			*********************************************************************************************************/
			if("rptContratoOBRA.jasper".equals(req.getParameter("rn"))){
				parms1.put("SUBREPORT_DIR", reportPath1);
				log.info("SUBREPORT_DIR: "+reportPath1);
			}
		
		

			/********************************************************************************************************
									 PARAMETROS DE REPORTE DE CONCILIACIONES BANCARIAS EN PDF
			*********************************************************************************************************/
			
			if("ConciliaBancos.jasper".equals(req.getParameter("rn"))){
				parms1.remove( "whereFolio" );
				parms1.put( "nConciliacion", new String ( req.getParameter( "whereFolio" ) ));
				//parms1.put("SUBREPORT_DIR", reportPath1);	
			}
			
			/********************************************************************************************************
			 PARAMETROS DE REPORTE DE CUESTIONARIO 15D
			 *********************************************************************************************************/
			
			if("ReporteCuestionarioFirma.jasper".equals(req.getParameter("rn"))){
				parms1.put("SUBREPORT_DIR", reportPath1+ File.separatorChar + "FIEL" + File.separatorChar);
				parms1.put("cIdSolicitud",   req.getParameter("cIdSolicitud").trim()  );
			}
			
			/********************************************************************************************************
									 PARAMETROS DE REPORTE ESTADISTICO POR AREA
			*********************************************************************************************************/
			/*
			if(req.getParameter("rn").equals("ReporteEstadisticoPorArea.jasper")){
				if(req.getParameter("chkArea").equals("true")){
					parms1.put("estruc", req.getParameter("estruc") + "%");
				}
			}
			*/
			if (req.getParameter("rn").equals("ReporteEstadisticoPorArea.jasper")) {
				/*
				estruc = req.getParameter("estruc");//Esteban Badillo. Fecha: 29/Oct/2009
				if(req.getParameter("chkArea").equals("true")){
					if(req.getParameter("areasHijas").equals("true"))
					{
						whereEstruc = "AND ( area_estructura LIKE '" + estruc + "'";
						parms1.put("estruc", req.getParameter("estruc") + "___");
					*/

				/*******************************************************************************
				* Esteban Badillo. Fecha: 19/Oct/2009.
				* Descripciï¿½n:
				* 	Se agrega una condiciï¿½n extra para las modificaciones solicitadas por
				* Martï¿½n Bonilla en relaciï¿½n al funcionamiento del Reporte Estadï¿½stico por Empleado.
				* Se debe considerar ademï¿½s del detalle original, un filtrado mas extenso sobre
				* los asuntos turnados por el ï¿½rea seleccioanda en el reporte hacia las ï¿½reas
				* hijas.
				* */
				/*
					parms1.put("whereAreasHijas", " and REMITENTE_AREA = " + req.getParameter("idArea") );
					whereEstruc = "AND area_estructura like '" + estruc + "___'";
					parms1.put("estruc", whereEstruc);

				}
				else
				{
					//Esteban Badillo. Se modifica % por ___ para traer solamente las ï¿½reas hijas de la seleccionada.
					//parms1.put("estruc", req.getParameter("estruc") + "%");
					//System.out.println("SeguridadCatalogos.java: estruc = " + req.getParameter("estruc") );

					whereEstruc = "AND ( area_estructura like '" + estruc + "___' OR area_estructura = '"+ estruc + "'  ) ";

					parms1.put("estruc", whereEstruc );
				}
				}
				*/

				/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *  * * * * * * * * * * * *
				 * Esteban Badillo. Fecha: 30/Dic/2009
				 * Descripcion: se elimina toda la logica anterior para el reporte estadistico por area
				 * para una nueva invocaciï¿½n al jasper con nuevos parametros.
				 * */
				if (name.equals("idArea")) {
					try {
						parms1.put("idArea", Integer.parseInt(value));
					} catch (Exception ex) {
						parms1.put("idArea", 0);
					}

				} else if (name.equals("areasHijas")) {

					if (value.equals("true")) {
						System.out.println("Entro a true de areas hijas");
						Integer pAreasHijas = new Integer(1);
						parms1.put(name, pAreasHijas);
					} else {
						Integer pAreasHijas = new Integer(0);
						parms1.put(name, pAreasHijas);
					}

				} else if (name.equals("fechaini")) {
					parms1.put("fechaInicio", value.trim().equals("") ? null : value);
				} else if (name.equals("fechafin")) {
					parms1.put("fechaFin", value.trim().equals("") ? null : value);
				} else if (name.equals("area_sol")) {
					parms1.put("areaSol", value);
				}
			}

			/*******************************************************************************************************
						 			 PARAMETROS DE REPORTE ESTADISTICO CONSOLIDADO
			********************************************************************************************************/
			if (req.getParameter("rn").equals("ReporteEstadisticoConsolidado.jasper")) {
				if (req.getParameter("select").equals("1")) {
					parms1.put("asterisco", ", *");
					parms1.put("whereCol", "WHERE col_1 = 'DIRECCIONES LOCALES'");
				}
				if (req.getParameter("select").equals("2")) {
					parms1.put("asterisco", ", *");
					parms1.put("whereCol", "WHERE col_1 = 'ORGANISMOS DE CUENCA'");
				}
				if (req.getParameter("select").equals("3")) {
					parms1.put("asterisco", ", *");
					parms1.put("whereCol", "WHERE col_1 = 'OFICINAS CENTRALES'");
				}
				if (req.getParameter("select").equals("4")) {
					parms1.put("consolidado",
					        ", col_1, sum(pendientesVencidos) pendientesVencidos, sum(pendientesNoVencidos) pendientesNoVencidos, sum(pendientes) pendientes, sum(cerrado) cerrado, sum(casos) casos");
					parms1.put("groupby", "GROUP BY col_1, hoy1");
				}
				if (req.getParameter("select").equals("5")) {
					parms1.put("asterisco", ", *");
				}
			}

			/*******************************************************************************************************
			 PARAMETROS DE CONTRARECIBO
			 ********************************************************************************************************/
			if (req.getParameter("rn").equals("Contrarecibo.jasper")) {
				if (!req.getParameter("folio").equals("")) {
					parms1.put("whereFolio", " and CR.caNoContrarrecibo = '" + req.getParameter("folio") + "'");
				}
			}
			
			if (req.getParameter("rn").equals("RETENCIONES.jasper")) {
				if (!req.getParameter("folio").equals("")) {
					parms1.put("whereFolio", " and ce.nFolioRetencion= '" + req.getParameter("folio") + "'");
				}
			}
			/*******************************************************************************************************
			 PARAMETROS DE POLIZAPAGO
			 ********************************************************************************************************/
			if (req.getParameter("rn").equals("PolizaPago.jasper")) {
				if (!req.getParameter("folio").equals("")) {
					parms1.put("whereFolio", " and CR.caNoContrarrecibo = '" + req.getParameter("folio").trim() + "'");
				}
			}
			
			if (req.getParameter("rn").equals("Anexo1.jasper")) {
				parms1.put("swhere", req.getParameter("swhere"));
				
			}
			
			if (req.getParameter("rn").equals("ReporteJustificacionesFirmas.jasper")) {
				parms1.put("where2", Integer.parseInt( req.getParameter("sWhere") ) );
			}
			
			if (req.getParameter("rn").equals("PolizaInformeComisionRG.jasper")) {
				parms1.put("whereFolio", req.getParameter("whereFolio")  );
			}
			
			/*******************************************************************************************************
			 PARAMETROS DE OFICIO DE COMPROMISO
			 ********************************************************************************************************/
			
			if (req.getParameter("rn").equals("ReporteCompromisos.jasper")) {
				parms1.put("where2", req.getParameter("where2"));
				parms1.put("concepto", req.getParameter("concepto"));
				
			}
			
			if (req.getParameter("rn").equals("ReporteCompromisosRef.jasper")) {
				parms1.put("where2", req.getParameter("where2"));
				parms1.put("concepto", req.getParameter("concepto"));
				
			}
			/*******************************************************************************************************
			 PARAMETROS DE NOTA SALDOS
			 ********************************************************************************************************/
			
			if (req.getParameter("rn").equals("NotaInformativaContrato.jasper")) {
				parms1.put("where2", req.getParameter("where2"));
				parms1.put("esIntegrada", Integer.parseInt( req.getParameter("esIntegrada")));
				parms1.put("nombre", req.getParameter("nombre"));
				parms1.put("puesto", req.getParameter("puesto"));
			}
			
			if (req.getParameter("rn").equals("NotaInformativaContratoDecremento.jasper")) {
				parms1.put("where2", req.getParameter("where2"));
			
			}
			
			/*******************************************************************************************************
			 PARAMETROS DE NOTA INFORMATIVA NUEVO COMPROMISO
			 ********************************************************************************************************/
			
			if (req.getParameter("rn").equals("NotaInformativaContratoNuevo.jasper")) {
				parms1.put("where2", req.getParameter("where2"));
				parms1.put("esIntegrada", Integer.parseInt( req.getParameter("esIntegrada")));
				parms1.put("nombre", req.getParameter("nombre"));
				parms1.put("puesto", req.getParameter("puesto"));
			}
			
			/*******************************************************************************************************
			 PARAMETROS DE POLIZARECXPAGAR
			 ********************************************************************************************************/
			if (req.getParameter("rn").equals("PolizaRecxPagar.jasper")) {
				if (!req.getParameter("whereFolio").equals("")) {
					parms1.put("whereFolio", req.getParameter("whereFolio") );
				}
			}
			/*******************************************************************************************************
						 			  PARAMETROS DE REPORTE GENERAL
			********************************************************************************************************/
			if (req.getParameter("rn").equals("ReporteGeneral.jasper")) {
				if (!req.getParameter("DPC0_fechaInicial").equals("") && !req.getParameter("DPC0_fechaFinal").equals("")) {
					parms1.put("whereFechaReg", "AND CONVERT(DATETIME, vimx.FECHAREGISTRO, 103) BETWEEN '" + req.getParameter("DPC0_fechaInicial") + "' AND '"
					        + req.getParameter("DPC0_fechaFinal") + "'");
				}
				if (!req.getParameter("DPC1_fechaInicial").equals("") && !req.getParameter("DPC1_fechaFinal").equals("")) {
					parms1.put("whereFechaLim", "AND CONVERT(DATETIME, vimx.FECHAREGISTRO, 103) BETWEEN '" + req.getParameter("DPC1_fechaInicial") + "' AND '"
					        + req.getParameter("DPC1_fechaFinal") + "'");
				}
				if (!req.getParameter("opc").equals("11")) {
					if (!req.getParameter("estruc").equals("")) {
						parms1.put("whereArea", "AND ca.AREA_ESTRUCTURA like '" + req.getParameter("estruc") + "'");
					}
				}

				/*if(req.getParameter("chkDetalle").equals("")){
					parms1.put("whereAreaEstruc", "AND ca.AREA_ESTRUCTURA like '" + req.getParameter("estruc") + "%'");
				}*/

				if (!req.getParameter("opc").equals("")) {
					if (req.getParameter("opc").equals("10")) {
						if (!req.getParameter("idareaTotal").equals("")) {
							parms1.put("whereAreaResp", "AND vimx.RESPONSABLE_AREA ='" + req.getParameter("idareaTotal") + "'");
						}
						if (!req.getParameter("res_u_login").equals("")) {
							parms1.put("whereNombreResp", "AND vimx.RESPONSABLE_ID ='" + req.getParameter("res_u_login") + "'");
						}
					}
					if (req.getParameter("opc").equals("11")) {
						if (!req.getParameter("idareaTotal").equals("")) {
							parms1.put("whereAreaRecibido", "AND vimx.RESPONSABLE_AREA ='" + req.getParameter("idarea") + "' AND vimx.REMITENTE_AREA = '"
							        + req.getParameter("idareaTotal") + "'");
						}
						if (!req.getParameter("res_u_login").equals("")) {
							parms1.put("whereNombreRecibido", "AND vimx.RESPONSABLE_AREA ='" + req.getParameter("idarea") + "' AND vimx.REMITENTE_ID = '"
							        + req.getParameter("res_u_login") + "'");
						}
					}
				}
				if (!req.getParameter("tipoasunto").equals("")) {
					parms1.put("whereTipoAsunto", "AND imx.TIPOASUNTO = '" + req.getParameter("tipoasunto") + "'");
				}
				if (!req.getParameter("tipoasunto").equals("")) {
					if (req.getParameter("tipoasunto").equals("I")) {
						if (!req.getParameter("reminunombre").equals("")) {
							parms1.put("whereRemInterno", "AND imx.REMINUNOMBRE = '" + req.getParameter("reminunombre") + "'");
						}
					}
					if (req.getParameter("tipoasunto").equals("E")) {
						if (!req.getParameter("reminunombre").equals("")) {
							parms1.put("whereRemExterno", "AND imx.RENOMBRE = '" + req.getParameter("reminunombre") + "'");
						}
					}
				}
				if (!req.getParameter("tipo_instruccion").equals("")) {
					parms1.put("whereTipoInstruccion", "AND imx.USER01 LIKE '%" + req.getParameter("tipo_instruccion") + "%'");
				}
				if (!req.getParameter("txt_estatus").equals("")) {
					if (req.getParameter("txt_estatus").equals("1")) {
						parms1.put("whereEstatus", "AND bc.CERRADO = 'S'");
					}
					if (req.getParameter("txt_estatus").equals("2")) {
						parms1.put("whereEstatus", "AND bc.CERRADO = 'N'");
					}
					if (req.getParameter("txt_estatus").equals("3")) {
						parms1.put("whereEstatus", "AND bc.CERRADO = 'N' AND vimx.retraso > 0");
					}
					if (req.getParameter("txt_estatus").equals("4")) {
						parms1.put("whereEstatus", "AND bc.CERRADO = 'N' AND vimx.retraso <= 0");
					}
				}
				if (!req.getParameter("txt_prioridad").equals("")) {
					parms1.put("wherePrioridad", "AND vimx.PRIORIDAD = '" + req.getParameter("txt_prioridad") + "'");
				}
			}
			/*************************************************************************************
			 * 										REPORTE GENERAL -
			 * **********************************************************************/
			if (req.getParameter("rn").equals("ReporteGen.jasper")) {
				//BMEA Eliminado 07/Ene/2010
				/*
				parms1.put("nombre", req.getParameter("nombre"));
				parms1.put("tipo_consulta",req.getParameter("tipo_consulta"));
				if(!req.getParameter("estatus").equals("")){
				String name_estatus[]={"","PENDIENTE VENCIDO","PENDIENTE NO VENCIDO","PENDIENTE","CONCLUIDO"};
				if(req.getParameter("estatus").equals("4"))parms1.put("where_estatus"," AND vimx.cerrado = 'S'"); //Esteban Badillo. Fecha: 29/Oct/2009. Se cambia el campo 'terminada' por 'cerrado'
				if(req.getParameter("estatus").equals("3"))parms1.put("where_estatus"," AND vimx.cerrado = 'N'");//Esteban Badillo. Fecha: 29/Oct/2009. Se cambia el campo 'terminada' por 'cerrado'
				if(req.getParameter("estatus").equals("1"))parms1.put("where_estatus"," AND vimx.cerrado = 'N' AND vimx.fecha_compromiso < GETDATE()");//Esteban Badillo. Fecha: 29/Oct/2009. Se cambia el campo 'terminada' por 'cerrado'
				if(req.getParameter("estatus").equals("2"))parms1.put("where_estatus"," AND vimx.cerrado = 'N' AND vimx.fecha_compromiso > GETDATE()");//Esteban Badillo. Fecha: 29/Oct/2009. Se cambia el campo 'terminada' por 'cerrado'
				parms1.put("estatus",name_estatus[Integer.parseInt(req.getParameter("estatus"))]);
				}
				if(!req.getParameter("tipo_instruccion").equals("")){
				parms1.put("tipo_instruccion",req.getParameter("tipo_instruccion"));
				parms1.put("where_tipo_instruccion"," AND vimx.tipo_instruccion LIKE '%" + req.getParameter("tipo_instruccion") + "%'");
				}
				if(!req.getParameter("tipo_prioridad").equals("")){
				String nombre_prioridad = req.getParameter("tipo_prioridad").equals("N")?"NORMAL":"URGENTE";
				parms1.put("tipo_prioridad",nombre_prioridad);
				parms1.put("where_tipo_prioridad"," AND vimx.prioridad = '" + req.getParameter("tipo_prioridad") + "'");
				}
				if(!req.getParameter("rem_area").equals("")){
				parms1.put("where_area"," AND  vimx.desc_area_rem IS NOT NULL AND vimx.desc_area_rem = '"+ req.getParameter("rem_area") + "'");
				parms1.put("area",req.getParameter("rem_area"));
				}
				if(!req.getParameter("res_area").equals("")){
				parms1.put("where_area"," AND  vimx.desc_area_resp IS NOT NULL AND vimx.desc_area_resp = '"+ req.getParameter("res_area") + "'");
				parms1.put("area",req.getParameter("res_area"));
				}
				if(!req.getParameter("res_nombre").equals("")){
				parms1.put("where_res_nombre"," AND  vimx.nombre_resp = '"+ req.getParameter("res_nombre") + "'");
				if(req.getParameter("tipo_consulta").equals("RESPONSABLE"))
				  parms1.put("empleado", req.getParameter("res_nombre"));
				}
				if(!req.getParameter("rem_nombre").equals("")){
				parms1.put("where_rem_nombre","  AND vimx.nombre_rem = '"+ req.getParameter("rem_nombre") + "'");
				if(req.getParameter("tipo_consulta").equals("REMITENTE"))
				  parms1.put("empleado",req.getParameter("rem_nombre"));
				}
				if(!req.getParameter("fechaini").equals("")  && !req.getParameter("fechafin").equals("")){
				String f= " AND CONVERT(DATETIME, vimx.fechaRegistro, 103) BETWEEN CONVERT(DATETIME, '" +
				req.getParameter("fechaini") + "', 103) AND CONVERT(DATETIME, '" + req.getParameter("fechafin") + "', 103) ";
				parms1.put("whereFechaReg",f);
				parms1.put("fechaini",req.getParameter("fechaini"));
				parms1.put("fechafin",req.getParameter("fechafin"));
				}
				*/

				//BMEA Agregado 07/01/2010
				if (name.equals("fechainicio")) {
					if (value.trim().equals(""))
						parms1.put("fechaini", null);
					else
						parms1.put("fechaini", value);
				} else if (name.equals("fechafin")) {
					//
					if (value.trim().equals(""))
						parms1.put("fechafin", null);
					else
						parms1.put("fechafin", value);
				} else if (name.equals("rem_area"))//Nombre del ï¿½rea remitente.
				{
					if (value.trim().equals(""))
						parms1.put("remArea", null);
					else
						parms1.put("remArea", value);
				} else if (name.equals("rem_idarea"))//ID del ï¿½rea remitente.
				{
					if (value.trim().equals(""))
						parms1.put("remIdArea", null);
					else
						parms1.put("remIdArea", value);
				} else if (name.equals("rem_nombre")) //Nombre del usuario remitente
				{
					if (value.trim().equals(""))
						parms1.put("remNombre", null);
					else
						parms1.put("remNombre", value);
				} else if (name.equals("rem_id")) //ID del usuario remitente
				{
					if (value.trim().equals(""))
						parms1.put("remId", null);
					else
						parms1.put("remId", value);
				} else if (name.equals("tipo_instruccion")) //Tipo de instruccion
				{
					if (value.trim().equals(""))
						parms1.put("tipo_instruccion", null);
					else
						parms1.put("tipo_instruccion", value);
				} else if (name.equals("estatus")) //Status (Pendiente Vencido, Pendiente No Vencido, Pendiente, Concluido)
				{
					//
					if (value.trim().equals(""))
						parms1.put("estatus", null);
					else
						parms1.put("estatus", value);
				} else if (name.equals("tipo_prioridad")) //Prioridad (N: Normal, U: Urgente)
				{
					if (value.trim().equals(""))
						parms1.put("tipo_prioridad", null);
					else
						parms1.put("tipo_prioridad", value);
				} else if (name.equals("res_area")) {
					if (value.trim().equals(""))
						parms1.put("resArea", null);
					else
						parms1.put("resArea", value);
				} else if (name.equals("res_idarea")) {
					if (value.trim().equals(""))
						parms1.put("resIdArea", null);
					else
						parms1.put("resIdArea", value);
				} else if (name.equals("res_nombre")) {
					if (value.trim().equals(""))
						parms1.put("resNombre", null);
					else
						parms1.put("resNombre", value);
				} else if (name.equals("res_id")) {
					if (value.trim().equals(""))
						parms1.put("resId", null);
					else
						parms1.put("resId", value);
				} else if (name.equals("tipo_consulta")) {
					if (value.trim().equals(""))
						parms1.put("tipo_consulta", null);
					else
						parms1.put("tipo_consulta", value);
				} else if (name.equals("nlogin")) //Nombre del usuario registrado en el sistema
				{
					if (value.trim().equals(""))
						parms1.put("nlogin", null);
					else
						parms1.put("nlogin", value);
				} else if (name.equals("nlogine")) //Nombre de la cuenta de suplantaciï¿½n del usuario conectado al sistema
				{
					if (value.trim().equals(""))
						parms1.put("nlogine", null);
					else
						parms1.put("nlogine", value);
				}
			}

			/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
			 * Reporte por empleado
			 * * * * * * * */
			if (req.getParameter("rn").equals("ReporteEstadisticoPorEmpleado.jasper")) {
				/*
				if (!req.getParameter("andLogin").equals("")){
					parms1.put("andLogin", req.getParameter("andLogin"));
					System.out.println("ya mandamos a llamar al jasper y le mandamos =(" + req.getParameter("andLogin") + ")");
					}
				*/

				if (name.equals("idArea")) {
					try {
						parms1.put("idArea", Integer.parseInt(value));
					} catch (Exception ex) {
						parms1.put("idArea", null);
					}
				} else if (name.equals("idEmpleado")) {
					if (value.trim().equals("")) {
						parms1.put("idEmpleado", null);
					} else {
						parms1.put("idEmpleado", value);
					}
				} else if (name.equals("fechaIni")) {
					if (value.trim().equals(""))
						parms1.put("fechaIni", null);
					else
						parms1.put("fechaIni", value);
				} else if (name.equals("fechaFin")) {
					if (value.trim().equals(""))
						parms1.put("fechaFin", null);
					else
						parms1.put("fechaFin", value);
				} else if (name.equals("nlogin")) {
					if (value.trim().equals("")) {
						parms1.put("nlogin", null);
					} else {
						parms1.put("nlogin", value);
					}
				} else if (name.equals("nlogine")) {
					if (value.trim().equals("")) {
						parms1.put("nlogine", null);
					} else {
						parms1.put("nlogine", value);
					}
				}
			}

			/*******************************************************************************************************
						 			 PARAMETROS DE REPORTE DINAMICO
			********************************************************************************************************/
			if (req.getParameter("rn").equals("ReporteDinamico.jasper")) {

				if (!req.getParameter("txt_folio").equals("")) {
					parms1.put("SCfolio", "AND FOLIO like '%" + req.getParameter("txt_folio") + "%' AND FOLIO NOT LIKE 'TMP-%'");
				}
				if (!req.getParameter("txt_tipoDeDocumento").equals("")) {
					parms1.put("SCtipoDeDocumento", "AND TIPO_DOCUMENTO like '%" + req.getParameter("txt_tipoDeDocumento") + "%'");
				}
				if (!req.getParameter("txt_referencia").equals("")) {
					parms1.put("SCreferencia", "AND REFERENCIA like '%" + req.getParameter("txt_referencia") + "%'");
				}
				if (!req.getParameter("txt_remitente").equals("")) {
					parms1.put("SCremitente", "AND REMITENTE_ASUNTO like '%" + req.getParameter("txt_remitente") + "%'");
				}
				if (!req.getParameter("txt_procedencia").equals("")) {
					parms1.put("SCprocedencia", "AND PROCEDENCIA like '%" + req.getParameter("txt_procedencia") + "%'");
				}
				if (!req.getParameter("txt_tipoDeInstruccion").equals("")) {
					parms1.put("SCtipoDeInstruccion", "AND TIPO_INSTRUCCION like '%" + req.getParameter("txt_tipoDeInstruccion") + "%'");
				}
				if (!req.getParameter("txt_detalleDeInstruccion").equals("")) {
					parms1.put("SCdetalleDeInstruccion", "AND DETALLE_INSTRUCCION like '%" + req.getParameter("txt_detalleDeInstruccion") + "%'");
				}
				if (!req.getParameter("txt_destinatario").equals("")) {
					parms1.put("SCdestinatario", "AND REMITENTE_ASUNTO like '%" + req.getParameter("txt_destinatario") + "%'");
				}
				if (!req.getParameter("txt_estatus").equals("")) {
					parms1.put("SCestatus", "AND CERRADO = '" + req.getParameter("txt_estatus") + "'");
				}
				if (!req.getParameter("txt_tipoAsunto").equals("")) {
					parms1.put("SCasunto", "AND TIPO_ASUNTO = '" + req.getParameter("txt_tipoAsunto") + "'");
				}
				if (!req.getParameter("txt_prioridad").equals("")) {
					parms1.put("SCprioridad", "AND PRIORIDAD = '" + req.getParameter("txt_prioridad") + "'");
				}
				if (!req.getParameter("DPC1_fechaInicial").equals("")) {
					parms1.put("SCfechaRegistro", "AND CONVERT(DATETIME," + req.getParameter("chk_fechaRegistro") + ", 103) BETWEEN '" + req.getParameter("DPC1_fechaInicial")
					        + "' AND '" + req.getParameter("DPC1_fechaFinal") + "'");
				}
				if (!req.getParameter("DPC2_fechaInicial").equals("")) {
					parms1.put("SCfechaRecepcion", "AND CONVERT(DATETIME," + req.getParameter("chk_fechaRecepcion") + ", 103) BETWEEN '" + req.getParameter("DPC2_fechaInicial")
					        + "' AND '" + req.getParameter("DPC2_fechaFinal") + "'");
				}
				if (!req.getParameter("DPC3_fechaInicial").equals("")) {
					parms1.put("SCfechaDocumento", "AND CONVERT(DATETIME," + req.getParameter("chk_fechaDocumento") + ", 103) BETWEEN '" + req.getParameter("DPC3_fechaInicial")
					        + "' AND '" + req.getParameter("DPC3_fechaFinal") + "'");
				}
				if (!req.getParameter("DPC4_fechaInicial").equals("")) {
					parms1.put("SCfechaLimite", "AND CONVERT(DATETIME," + req.getParameter("chk_fechaLimite") + ", 103) BETWEEN '" + req.getParameter("DPC4_fechaInicial")
					        + "' AND '" + req.getParameter("DPC4_fechaFinal") + "'");
				}
				if (!req.getParameter("txt_asunto").equals("")) {
					parms1.put("SCasunto", "AND ASUNTO like '%" + req.getParameter("txt_asunto") + "%'");
				}
			}
			/*******************************************************************/
			/**				MODULO DE ADQUISICIONES Y SERVICIOS			  	  **/
			/*******************************************************************/
			if (req.getParameter("rn").equals("rptRequisiciones.jasper")) {
				parms1.put("cEjercicio", req.getParameter("cEjercicio"));
				parms1.put("cIdUnidadEjecutora", req.getParameter("cIdUnidadEjecutora"));
				parms1.put("nIdConsecutivo", req.getParameter("nIdConsecutivo"));
				parms1.put("cIdTipoSolicitud", req.getParameter("cIdTipoSolicitud"));
			}
			if (req.getParameter("rn").equals("Pedido.jasper")) {
				parms1.put("cEjercicio", req.getParameter("cEjercicio"));
				parms1.put("cIdUnidadEjecutora", req.getParameter("cIdUnidadEjecutora"));
				parms1.put("nIdConsecutivo", req.getParameter("nIdConsecutivo"));
				parms1.put("cIdTipoPedido", req.getParameter("cIdTipoPedido"));
				parms1.put("formato", req.getParameter("formato"));
				System.out.println("formato del pedido: "+req.getParameter("formato"));
				parms1.put("SUBREPORT_DIR", getServletContext().getRealPath("Reportes"));
			}
			if (req.getParameter("rn").equals("rptPedidoAnexo1.jasper")) {
				parms1.put("cEjercicio", req.getParameter("cEjercicio"));
				parms1.put("cIdUnidadEjecutora", req.getParameter("cIdUnidadEjecutora"));
				parms1.put("nIdConsecutivo", req.getParameter("nIdConsecutivo"));
				parms1.put("cIdTipoPedido", req.getParameter("cIdTipoPedido"));
				parms1.put("formato", req.getParameter("formato"));
				System.out.println("formato del pedido: "+req.getParameter("formato"));
				parms1.put("SUBREPORT_DIR", getServletContext().getRealPath("Reportes"));
				
			}
			if (req.getParameter("rn").equals("rptPedidoAnexo2.jasper")) {
				parms1.put("cEjercicio", req.getParameter("cEjercicio"));
				parms1.put("cIdUnidadEjecutora", req.getParameter("cIdUnidadEjecutora"));
				parms1.put("nIdConsecutivo", req.getParameter("nIdConsecutivo"));
				parms1.put("cIdTipoPedido", req.getParameter("cIdTipoPedido"));
				parms1.put("formato", req.getParameter("formato"));
				System.out.println("formato del pedido: "+req.getParameter("formato"));
				parms1.put("SUBREPORT_DIR", getServletContext().getRealPath("Reportes"));
			}
			if (req.getParameter("rn").equals("rptContratoAnexo.jasper")) {
				parms1.put("cEjercicio", req.getParameter("cEjercicio"));
				parms1.put("cIdUnidadEjecutora", req.getParameter("cIdUnidadEjecutora"));
				parms1.put("nIdConsecutivo", req.getParameter("nIdConsecutivo"));
				parms1.put("cIdTipoContrato", req.getParameter("cIdTipoContrato"));
				parms1.put("SUBREPORT_DIR", getServletContext().getRealPath("Reportes"));
			}
			if (req.getParameter("rn").equals("rptContratoAnexo1.jasper")) {
				parms1.put("cEjercicio", req.getParameter("cEjercicio"));
				parms1.put("cIdUnidadEjecutora", req.getParameter("cIdUnidadEjecutora"));
				parms1.put("nIdConsecutivo", req.getParameter("nIdConsecutivo"));
				parms1.put("cIdTipoContrato", req.getParameter("cIdTipoContrato"));
				parms1.put("SUBREPORT_DIR", getServletContext().getRealPath("Reportes"));
			}
			
			if (req.getParameter("rn").equals("ConsolidadoPartidaExcel.jasper")) {				
					parms1.put("cEjercicio", req.getParameter("cEjercicio"));
					parms1.put("cIdUnidadEjecutora", req.getParameter("cIdUnidadEjecutora"));
					parms1.put("nIdConsecutivo", req.getParameter("nIdConsecutivo"));	
					parms1.put("cIdTipoConsolidado", req.getParameter("cIdTipoConsolidado"));														
			}
			
			// clausulas
			if (req.getParameter("rn").equals("clausulas.jasper")) {
				parms1.put("cPedidoDefinitivo", req.getParameter("cPedidoDefinitivo"));
				parms1.put("SUBREPORT_DIR", getServletContext().getRealPath("Reportes"));
			}
			// Consulta esttatus de Convenio modificatorio
			if (req.getParameter("rn").equals("ReporteOP_ConvenioModificatorio.jasper")) {
				String strRemplaza = req.getParameter("strCadena");
				String  newStr = strRemplaza.replace("[porc]","%"); 
				parms1.put("strCadena", newStr);
			}
			/********************************************************************************************************
			 *SE AÑADE COMO PARAMETRO LA DIRECCION DEL SUBREPORTE PARA EL AUXILIAR MAYOR 
			 ********************************************************************************************************/
			if (req.getParameter("rn").equals("AuxiliarMayor2.jasper") ) {
				parms1.put("SUBREPORT_DIR", reportPath1);
			}
		}

		Connection conn = null;
		InputStream in = null;
		ServletOutputStream out = null;

		try {
			conn = DataSourceManager.getConnection(jniName);
			//Carga el reporte en BUFFER
			if(req.getParameter("directorio") == null){
				out = resp.getOutputStream();
				if(req.getParameter("rn").equals( "ReporteCuestionarioFirma.jasper" ))
					in = new FileInputStream(reportPath1+ File.separatorChar+ "FIEL" + File.separatorChar +reportName );
				else
					in = new FileInputStream(reportPath1+ File.separatorChar +reportName);
				
				if(req.getParameter("xls")!=null && "SI".equals(req.getParameter("xls"))){
					
					log.info("nombre del reporte: "+reportName);
					resp.setContentType("application/vnd.ms-excel");
					byte[] bytes = null;
					reportPath1=reportPath1+"\\"+reportName;
					log.info("parametros: "+parms1);
					log.info("path: "+reportPath1);
					log.info("valor de la variable in: "+in);
					JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath1, parms1, conn);
					bytes = convertJasperPrintToExcel(jasperPrint);
					resp.setContentLength(bytes.length);
					out.write(bytes,0,bytes.length);
				}
				//&& req.getParameter("formato")!=null
				else if("xls".equalsIgnoreCase(req.getParameter("formato"))&& req.getParameter("formato")!=null){
					
					String filename = "Reporte.xls";
					resp.setContentType("application/vnd.ms-excel");
					String disposition = "attachment; fileName="+filename;
					resp.setHeader("Content-Disposition",disposition);
					
					byte[] bytes = null;
					JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath1, parms1, conn);
					bytes = convertJasperPrintToExcel(jasperPrint);
					resp.setContentLength(bytes.length);
					out.write(bytes,0,bytes.length);
				}
				
				else if("doc".equalsIgnoreCase(req.getParameter("formato")) && req.getParameter("formato")!=null){
					resp.addHeader("Content-disposition", "attachment; filename=report.docx");   
					
					resp.setContentType("application/doc");
					byte[] bytes = null;
					JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath1, parms1, conn);
					bytes = convertJasperPrintToWRD(jasperPrint);
					resp.setContentLength(bytes.length);
					out.write(bytes,0,bytes.length);
				}
				else{
					
					/*JGDS-041021 Para notificar errores primero intenta generar el archivo y si es exitoso entonces lo envia, de otro modo notifica el error*/
					
					File outputFile = File.createTempFile( "rpt_", ".pdf", TEMP_DIR );
					
					try{
						OutputStream reportOut = new FileOutputStream( outputFile );
						JasperRunManager.runReportToPdfStream(in, reportOut , (Map<String, Object>) parms1, conn);
						reportOut.flush();
						reportOut.close();
						
						if("NotaInformativaContratoNuevo.jasper".equals( reportName ) || "NotaInformativaContrato.jasper".equals( reportName ) || "ReporteCompromisos.jasper".equals( reportName ) || "ReporteCompromisosRef.jasper".equals( reportName )) {
							reportName = CompromisosManager.getFileName(conn, req.getParameter("where2"));							 
						} else {
							reportName = Util.getFileWithoutExtencion( reportName ) + ".pdf";
						}
						
						Util.doDownload(resp,outputFile.getAbsolutePath(), reportName + ".pdf", null );
						
					}finally {
						if( outputFile != null)
							if( !outputFile.delete() )
								outputFile.deleteOnExit();
					}
				}
		
				if(out!=null){
					out.flush();
					out.close();
				}
			}	
			//Carga el archivo en directorio TEMPORAL
			else if(req.getParameter("directorio").toUpperCase().equals("TEMPORAL")){
				if("pdf".equals(req.getParameter("formato")) && req.getParameter("formato")!=null){
					File file = new File(reportPath1+"\\tempReportesPedidos\\"+reportName.replace(".jasper", ".pdf"));
					JasperReport reporte = (JasperReport) JRLoader.loadObjectFromFile(reportPath1+"\\"+reportName);
					JasperPrint jp = JasperFillManager.fillReport(reporte, parms1, conn);
					JRExporter je = new JRPdfExporter();
					je.setParameter(JRExporterParameter.JASPER_PRINT, jp);
					je.setParameter(JRExporterParameter.OUTPUT_FILE, file);
					je.exportReport();
					
					if(req.getParameter("rn").equals("Pedido.jasper")){
						file = new File(reportPath1+"\\tempReportesPedidos\\clausulado.pdf");
						parms1.clear();
						parms1.put("SUBREPORT_DIR", getServletContext().getRealPath("Reportes"));
						parms1.put("cPedidoDefinitivo", req.getParameter("cPedidoDefinitivo"));
						parms1.put("cIdRFC", req.getParameter("cIdRFC"));
						reporte = (JasperReport) JRLoader.loadObjectFromFile(reportPath1+"\\clausulado.jasper");
						jp = JasperFillManager.fillReport(reporte, parms1, conn);
						je = new JRPdfExporter();
						je.setParameter(JRExporterParameter.JASPER_PRINT, jp);
						je.setParameter(JRExporterParameter.OUTPUT_FILE, file);
						je.exportReport();
						
						IntercalarPedidoPDF ipp = new IntercalarPedidoPDF(reportPath1+"\\tempReportesPedidos\\"+reportName.replace(".jasper", ".pdf"),reportPath1+"\\tempReportesPedidos\\clausulado.pdf",reportPath1+"\\tempReportesPedidos\\pdfUnido.pdf");
						ipp.execute();
						
						//mostrar archivo
						resp.setContentType("application/pdf");
						resp.setHeader("Content-Disposition", "attachment; filename="+ipp.getRutaFileIntercalado());
					    ServletOutputStream outS = resp.getOutputStream();
					    FileInputStream fileInput1 = new FileInputStream(ipp.getRutaFileIntercalado());
					    BufferedInputStream bufferedInput1 = new BufferedInputStream(fileInput1);
					    BufferedOutputStream bufferedOutput = new BufferedOutputStream(outS);
					    int leidos1 = 0;
					    // Bucle para leer de un fichero y escribir en el otro.
					    byte [] array1 = new byte[(1024*1024)*6];
						leidos1 = bufferedInput1.read(array1);

						while (leidos1 > 0){
							bufferedOutput.write(array1,0,leidos1);
							leidos1=bufferedInput1.read(array1);
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
		} catch (Exception ex) {
			log.error( ex,ex );
			try {
				Util.sendHTMLErrorMsg( resp, ex );
			} catch ( Exception e ) {
				log.error( e , e);
				throw new ServletException(ex);
			}
		} finally {
			try {
				if (conn != null)
					conn.close();

				if (in != null)
					in.close();

				if (out != null)
					out.close();

			} catch (SQLException e) {
				log.error( e,e );
				e.printStackTrace();
			}

			conn = null;

			in = null;
			out = null;
		}
	}

	public void runReport( HttpServletRequest req, HttpServletResponse resp, String reportName, Map<String, Object> parms ) throws ServletException {

		Connection conn = null;

		try {
			conn = DataSourceManager.getConnection( jniName );
			new Reportes().execute( conn, req, resp, reportName, parms );
		} catch ( Exception exc ) {
			throw new ServletException( exc );
		} finally {
			try {
				if ( conn != null )
					conn.close();
			} catch ( SQLException e ) {
				e.printStackTrace();
			}

			conn = null;
		}
	}

	private byte[] convertJasperPrintToExcel( JasperPrint jasperPrint ) throws JRException {
		try {
			if ( jasperPrint == null )
				throw new NullPointerException();

			ByteArrayOutputStream outExcel = new ByteArrayOutputStream();
			JRXlsExporter exporter = new JRXlsExporter();
			exporter.setParameter( JRXlsExporterParameter.JASPER_PRINT, jasperPrint );
			exporter.setParameter( JRXlsExporterParameter.OUTPUT_STREAM, outExcel );
			 
			exporter.setParameter( JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE );
			 

			exporter.exportReport();

			return outExcel.toByteArray();
		} catch ( Exception ex ) {
			throw new ReportsException( ex );
		}
	}

	// Convertir a WORD
	private byte[] convertJasperPrintToWRD( JasperPrint jasperPrint ) throws JRException {
		try {

			if ( jasperPrint == null )
				throw new NullPointerException();
			ByteArrayOutputStream outWRD = new ByteArrayOutputStream();

			JRDocxExporter exporter = new JRDocxExporter();
			exporter.setParameter( JRDocxExporterParameter.JASPER_PRINT, jasperPrint );
			exporter.setParameter( JRDocxExporterParameter.CHARACTER_ENCODING, "UTF-8" );
			exporter.setParameter( JRDocxExporterParameter.OUTPUT_STREAM, outWRD );

			// exporter.setParameter(JRDocxExporterParameter.FLEXIBLE_ROW_HEIGHT,
			// Boolean.TRUE);
			exporter.exportReport();

			return outWRD.toByteArray();

		} catch ( Exception e ) {
			// TODO: handle exception
			throw new ReportsException( e );
		}
	}
	
}
