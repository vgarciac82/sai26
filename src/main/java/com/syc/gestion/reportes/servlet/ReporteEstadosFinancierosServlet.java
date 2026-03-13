package com.syc.gestion.reportes.servlet;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.syc.gestion.reportes.reportesBussinesObject;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.reportes.ReportePolizasBusinessLogic;


public class ReporteEstadosFinancierosServlet extends HttpServlet implements GestionInterface {

	private static final long			serialVersionUID	= -6810376574978594871L;
	String								jniName;
	String								jniName2;
	String								jniName3;
	private static final Logger			log					= Logger.getLogger( ReporteEstadosFinancierosServlet.class );
	private static Map<String, String>	plantillas			= null;

	@Override
	protected void doGet( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {
		
		ReportePolizasBusinessLogic reporte = new ReportePolizasBusinessLogic( jniName );
		Integer mes = Integer.parseInt( req.getParameter( "mes" ));
		
		try {
			
			reporte.actualizarSaldos( mes );			
			Util.sendJSONResponse( resp, "success" );
			
		} catch (Exception e) {
			log.error( e, e );
			try {
				Util.sendHTMLErrorMsg( resp, e );
			} catch ( Exception e2 ) {
				log.warn( "No se pudo notificar la causa de la excepcion: " + e2, e2 );
			}
		}
		
	}

	@Override
	protected void doPost( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {
		reportesBussinesObject objReporte = new reportesBussinesObject( jniName );

		String subreporte = req.getParameter( "imprimeCaracteristicas" );
		String reporteDetalle = req.getParameter( "imprimeDetalle" );
		String tipoFormato = req.getParameter( "excel" );
		String desagregado = req.getParameter( "desagregada" );
		String reporte = "";
		String ruta = getServletContext().getRealPath( "Reportes" );
		String cEsFiel = req.getParameter( "esFiel" );

		try {
			
			if ( "excel".equals( tipoFormato ) ) {
				objReporte.generaExcel( req, resp, plantillas );
			} else if ( "desagregada".equals( desagregado ) ) {

				reporte = objReporte.validaReporte( req, resp );
				String reportPath = getServletContext().getRealPath( "Reportes" + File.separator + reporte );

				if ( ( "ConciliacionCONAC.jasper" ).equals( reporte ) || ( "ConciliacionCONACEg.jasper" ).equals( reporte ) ) {
					objReporte.generaExcel( req, resp, plantillas );
				} else {
					objReporte.reporteEstadosFinancieros( req, resp, reportPath, ruta, subreporte, reporteDetalle, false );
				}
			} else if ( "S".equalsIgnoreCase( cEsFiel ) ) {
				reporte = objReporte.validaReporteFiel( req, resp );
				String reportPath = getServletContext().getRealPath( "Reportes" + File.separator );
				
				objReporte.reporteEstadosFinancieros( req, resp, reportPath, reporte, subreporte, reporteDetalle,  true );
				Util.sendHTMLSuccessMsg(resp);
			} else {
				reporte = objReporte.validaReporte( req, resp );
				String reportPath = getServletContext().getRealPath( "Reportes" + File.separator );

				objReporte.reporteEstadosFinancieros( req, resp, reportPath, reporte, subreporte, reporteDetalle, false );
			}
		} catch ( Exception e ) {
			log.error( e, e );
			try {
				Util.sendHTMLErrorMsg( resp, e );
			} catch ( Exception e2 ) {
				log.warn( "No se pudo notificar la causa de la excepcion: " + e2, e2 );
			}
		}
	}

	public void init( ServletConfig config ) throws ServletException {
		super.init( config );

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

		synchronized ( this ) {
			if ( plantillas == null ) {
				plantillas = new HashMap<String, String>();
				plantillas.put( "excel", getServletContext().getRealPath( "Reportes" + File.separator + "Plantilla_EstadosFinancieros.xls" ) );
				plantillas.put( "excelIng", getServletContext().getRealPath( "Reportes" + File.separator + "Plantilla_EstadosFinancieros_ingresos.xls" ) );

				// plantillas.put("DEUDORES",
				// getServletContext().getRealPath("Reportes" + File.separator +
				// "Plantilla_CedulaDeudores.xls"));
			}
		}
	}

}
