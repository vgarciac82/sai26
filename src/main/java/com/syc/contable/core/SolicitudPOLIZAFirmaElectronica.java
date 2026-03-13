package com.syc.contable.core;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.syc.contable.DocPolizaEncabezado;
import com.syc.fortimax.core.Carpeta;
import com.syc.fortimax.core.Documento;
import com.syc.fortimax.core.Volumen;
import com.syc.gestion.core.AlarmaManager;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import com.syc.obrapublica.core.ConfiguraAplicativoManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.firmaElectronica.core.FirmaElectronicaManager;
import com.syc.sai.firmaElectronica.exceptions.FirmaElectronicaException;
import com.syc.sai.firmaElectronica.exceptions.NotEmptyDocumentException;
import com.syc.sai.firmaElectronica.interfaces.SolicitudFirmaElectronica;


public class SolicitudPOLIZAFirmaElectronica extends SolicitudFirmaElectronica {

	private static final Logger log = Logger.getLogger( SolicitudPOLIZAFirmaElectronica.class );

	public SolicitudPOLIZAFirmaElectronica( ) {
		setFolder( "Poliza Firmada" );
	}

	@Override
	public String generaArchivoFirma( Connection conn, Volumen vol, Carpeta folder, boolean isSignedCopy ) throws FirmaElectronicaException {

		String filename = null;
		String reportName = FirmaElectronicaManager.REPORTES_SOL_PAGO.get( ( isSignedCopy ? "ACUSE_" : "" ) + getDocument() + "_FIEL" );
		DocPolizaEncabezado encabezado;
		try {
			encabezado = getEncabezado( conn );
		} catch ( Exception e1 ) {
			throw new FirmaElectronicaException( e1 );
		}

		Map<String, Object> parametrosReporte = new HashMap<String, Object>();
		parametrosReporte.put( "SUBREPORT_DIR", getReportPath() );
		parametrosReporte.put( "poliza", encabezado.getnFolioPoliza() );
		parametrosReporte.put( "cc", encabezado.getcCentroContable() );

		try {
			Documento d = getOrCreateDocument( conn, vol, folder, getDocName(), getFileExtension(), getUsuario().getLogin() );
			filename = d.getFullPathFilesNames()[0];
			runReport( conn, getReportPath(), reportName, null, filename, parametrosReporte );
		} catch ( NotEmptyDocumentException nede ) {
			log.warn( "El documento no esta vacio. Se ignora" + nede );
		} catch ( Exception e ) {
			throw new FirmaElectronicaException( e );
		}

		return filename;

	}

	@Override
	public String getAutLegend( Connection conn ) throws Exception {
		if ( tieneDelegatorioVoBO( conn ) ) {
			String voLegend = "AUTORIZ\u00D3 " + " Firma " + getTipoSuplencia() + " de " + getNombreEmpleadoSuplido() + " con fundamento en el oficio: " + getFolioOficioVoBo() + " de fecha: " + getFechaOficioVoBo();
			log.info( voLegend );
			return voLegend;
		} else
			return "AUTORIZ\u00D3";
	}

	@Override
	public String getCuerpoCorreoAutoriza( Connection conn ) throws Exception {
		ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic( GestionInterface.ATT_CONEXION );
		String urlAutorizacion = cabl.getSystemSetting( "URL_SAI" ) + "/egresos/AutPago";

		String nombreCompleto = getAutNombre( conn );
		String puesto = getAutPuesto( conn );
		String concepto = getConcepto( conn );
		String importe = getImporteStr( conn );

		int numeroEmpleadoAut = getNumEmpleadoAutSuplencia( conn );
		int numeroEmpleado = ( numeroEmpleadoAut > 0 ? numeroEmpleadoAut : getAutNumEmpleado( conn ) );

		DocPolizaEncabezado encabezado = getEncabezado( conn );

		String mailBody = "<html>";
		mailBody += "\n\t<head>";
		mailBody += "\n\t<meta charset=\"UTF-8\">";
		mailBody += "\n\t<style type=\"text/css\">";
		mailBody += "\n\tbody {";
		mailBody += "\n\t\t	font-family: verdana, arial, sans-serif;";
		mailBody += "\n\t\t	font-size: 12px;";
		mailBody += "\n\t}";

		mailBody += "\n\ttable {";
		mailBody += "\n\t\tfont-size: 12px;";
		mailBody += "\n\t\tcolor: #333333;";
		mailBody += "\n\t\tborder-width: 1px;";
		mailBody += "\n\t\tborder-color: #666666;";
		mailBody += "\n\t\tborder-collapse: collapse;";
		mailBody += "\n\t}";

		mailBody += "\n\ttable th {";
		mailBody += "\n\t\tborder-width: 1px;";
		mailBody += "\n\t\tpadding: 8px;";
		mailBody += "\n\t\tborder-style: solid;";
		mailBody += "\n\t\tborder-color: #666666;";
		mailBody += "\n\t\tbackground-color: #dedede;";
		mailBody += "\n\t}";

		mailBody += "\n\ttable td {";
		mailBody += "\n\t\tborder-width: 1px;";
		mailBody += "\n\t\tpadding: 8px;";
		mailBody += "\n\t\tborder-style: solid;";
		mailBody += "\n\t\tborder-color: #666666;";
		mailBody += "\n\t\tbackground-color: #ffffff;";
		mailBody += "\n\t}";
		mailBody += "\n\t</style>";
		mailBody += "</head>";
		mailBody += "\n\t<body>";
		mailBody += "\n\t\t<form id=\"Form\" name=\"FormViaticos\" >";
		mailBody += "	<b> C." + nombreCompleto + "</b>";
		mailBody += "	<br>";
		mailBody += "	<b>" + puesto + "</b>";
		mailBody += "	<br />";
		mailBody += "	<p>";
		mailBody += "		Se solicita de su autorización de la siguiente p&oacute;liza manual:";
		mailBody += "	</p>";

		mailBody += "	<table>";
		mailBody += "		<thead>";
		mailBody += "			<tr>";
		mailBody += "				<th>Folio</th>";
		mailBody += "				<th>Concepto</th>";
		mailBody += "				<th>Total (Cargos y Abonos)</th>";
		mailBody += "			</tr>";
		mailBody += "		</thead>";
		mailBody += "		<tbody>";

		mailBody += "<tr>";
		mailBody += "\n<td>" + encabezado.getnFolioDocPoliza() + " - CC -" + encabezado.getcCentroContable() + "</td>";
		mailBody += "\n<td>" + concepto + "</td>";
		mailBody += "\n<td>" + importe + "</td>";
		mailBody += "</tr>";
		mailBody += "		</tbody>";
		mailBody += "	</table>";
		mailBody += "	<br />";
		mailBody += "	<br />";
		mailBody += "\n<b>Para autorizar o rechazar esta solicitud por favor de click <a href=\"" + urlAutorizacion + generaAccessoAutToken( numeroEmpleado, getDocument(), getIdField() ) + "\" > aquí </a>.</b>";
		mailBody += "	<br />";
		mailBody += "	<br />";
		mailBody += "	<p>";
		mailBody += "		Notificaciones Automaticas<br />Sistema de Administracion Integral<br />" + Util.getToday();
		mailBody += "	</p>";
		mailBody += "	</form>";
		mailBody += "</body>";
		mailBody += "</html>";

		return mailBody;
	}

	@Override
	public String getCuerpoCorreoVistoBueno( Connection conn ) throws Exception {

		String urlAutorizacion = ConfiguraAplicativoManager.getSystemSetting( conn, "URL_SAI" ) + "/egresos/VoBoPago";

		String nombreCompleto = getVoBoNombre( conn );
		String puesto = getVoBoPuesto( conn );
		String concepto = getConcepto( conn );
		String importe = getImporteStr( conn );

		int numeroEmpleadoVoBo = getNumEmpleadoVistoBueno( conn );
		int numeroEmpleado = numeroEmpleadoVoBo > 0 ? numeroEmpleadoVoBo : getVoBoNumEmpleado( conn );

		DocPolizaEncabezado encabezado = getEncabezado( conn );

		String mailBody = "<html>";
		mailBody += "\n\t<head>";
		mailBody += "\n\t<meta charset=\"UTF-8\">";
		mailBody += "\n\t<style type=\"text/css\">";
		mailBody += "\n\tbody {";
		mailBody += "\n\t\t	font-family: verdana, arial, sans-serif;";
		mailBody += "\n\t\t	font-size: 12px;";
		mailBody += "\n\t}";

		mailBody += "\n\ttable {";
		mailBody += "\n\t\tfont-size: 12px;";
		mailBody += "\n\t\tcolor: #333333;";
		mailBody += "\n\t\tborder-width: 1px;";
		mailBody += "\n\t\tborder-color: #666666;";
		mailBody += "\n\t\tborder-collapse: collapse;";
		mailBody += "\n\t}";

		mailBody += "\n\ttable th {";
		mailBody += "\n\t\tborder-width: 1px;";
		mailBody += "\n\t\tpadding: 8px;";
		mailBody += "\n\t\tborder-style: solid;";
		mailBody += "\n\t\tborder-color: #666666;";
		mailBody += "\n\t\tbackground-color: #dedede;";
		mailBody += "\n\t}";

		mailBody += "\n\ttable td {";
		mailBody += "\n\t\tborder-width: 1px;";
		mailBody += "\n\t\tpadding: 8px;";
		mailBody += "\n\t\tborder-style: solid;";
		mailBody += "\n\t\tborder-color: #666666;";
		mailBody += "\n\t\tbackground-color: #ffffff;";
		mailBody += "\n\t}";
		mailBody += "\n\t</style>";
		mailBody += "</head>";
		mailBody += "\n\t<body>";
		mailBody += "\n\t\t<form id=\"Form\" name=\"FormViaticos\" >";
		mailBody += "	<b> C." + nombreCompleto + "</b>";
		mailBody += "	<br>";
		mailBody += "	<b>" + puesto + "</b>";
		mailBody += "	<br />";
		mailBody += "	<p>";
		mailBody += "		Se solicita de su autorización de la siguiente p&oacute;liza manual:";
		mailBody += "	</p>";

		mailBody += "	<table>";
		mailBody += "		<thead>";
		mailBody += "			<tr>";
		mailBody += "				<th>Folio</th>";
		mailBody += "				<th>Concepto</th>";
		mailBody += "				<th>Total (Cargos y Abonos)</th>";
		mailBody += "			</tr>";
		mailBody += "		</thead>";
		mailBody += "		<tbody>";

		mailBody += "<tr>";
		mailBody += "\n<td>" + encabezado.getnFolioDocPoliza() + " - CC -" + encabezado.getcCentroContable() + "</td>";
		mailBody += "\n<td>" + concepto + "</td>";
		mailBody += "\n<td>" + importe + "</td>";
		mailBody += "</tr>";
		mailBody += "		</tbody>";
		mailBody += "	</table>";
		mailBody += "	<br />";
		mailBody += "	<br />";
		mailBody += "\n<b>Para autorizar o rechazar esta solicitud por favor de click <a href=\"" + urlAutorizacion + generaAccessoAutToken( numeroEmpleado, getDocument(), getIdField() ) + "\" > aquí </a>.</b>";
		mailBody += "	<br />";
		mailBody += "	<br />";
		mailBody += "	<p>";
		mailBody += "		Notificaciones Automaticas<br />Sistema de Administracion Integral<br />" + Util.getToday();
		mailBody += "	</p>";
		mailBody += "	</form>";
		mailBody += "</body>";
		mailBody += "</html>";

		return mailBody;
	}

	private DocPolizaEncabezado getEncabezado( Connection conn ) throws Exception {
		PreparedStatement psDatosPoliza = null;
		ResultSet rsDatosPoliza = null;
		StringBuilder queryDatosPoliza = new StringBuilder( "SELECT nfoliopoliza, cCentroContable, nFolioDocPoliza FROM tDocPolizaEncabezado WHERE nFolioDocPoliza = ?" );
		try {

			psDatosPoliza = conn.prepareStatement( queryDatosPoliza.toString() );
			psDatosPoliza.setInt( 1, getIdField() );
			rsDatosPoliza = psDatosPoliza.executeQuery();
			if ( rsDatosPoliza.next() ) {
				DocPolizaEncabezado encabezado = new DocPolizaEncabezado();
				encabezado.setnFolioPoliza( rsDatosPoliza.getInt( "nfoliopoliza" ) );
				encabezado.setcCentroContable( rsDatosPoliza.getString( "cCentroContable" ) );
				encabezado.setnFolioDocPoliza( rsDatosPoliza.getInt( "nFolioDocPoliza" ) );
				return encabezado;
			} else
				throw new Exception( "No se encontro informacion para la poliza manual con folio " + getIdField() + " en la tabla " + getHeader() );
		} finally {
			CloseObject.closeObject( rsDatosPoliza );
			CloseObject.closeObject( psDatosPoliza );
		}
	}

	@Override
	public String getImporteStr( Connection conn ) throws Exception {
		StringBuilder query = new StringBuilder();
		query.append( "SELECT	mImporteMasIva " );
		query.append( "  FROM	vPagoProveedor WITH(NOLOCK) " );
		query.append( " WHERE	ctipoPago = ? " );
		query.append( " AND nFolio = ? " );

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement( query.toString() );
			ps.setString( 1, getDocument() );
			ps.setInt( 2, getIdField() );
			rs = ps.executeQuery();

			if ( rs.next() )
				return Util.formatNumber( rs.getBigDecimal( 1 ) );
			else
				throw new Exception( "No fue posible encontrar el beneficiario para el folio " + getIdField() + " en el documento" + getDocument() );

		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}
	}

	@Override
	public String getVoBoLegend( Connection conn ) throws Exception {
		if ( tieneDelegatorioVoBO( conn ) ) {
			String voLegend = "REVIS\u00D3 " + " Firma " + getTipoSuplencia() + " de " + getNombreEmpleadoSuplido() + " con fundamento en el oficio: " + getFolioOficioVoBo() + " de fecha: " + getFechaOficioVoBo();
			log.info( voLegend );
			return voLegend;
		} else
			return "REVIS\u00D3";
	}

	@Override
	public void notificaOperacionMasivaPendiente( Connection conn, String operacion ) throws Exception {
		throw new Exception( "Metodo no notificaOperacionMasivaPendiente no implementado" );
	}

	@Override
	public String notificaOperacionPendiente( Connection conn, String tipoAutorizacion ) throws Exception {

		if ( VO_BO.equals( tipoAutorizacion ) ) {
			AlarmaManager.procesaAlarmaCNF( conn, "", null, null, "Solicitud de visto bueno de poliza manual", getCorreoVistoBueno( conn ), getCuerpoCorreoVistoBueno( conn ) );
			FirmaElectronicaManager.avanzaEstatusSICOP( conn, this, SolicitudFirmaElectronica.VO_BO_SICOP );
		}
		if ( AUTORIZA.equals( tipoAutorizacion ) )
			AlarmaManager.procesaAlarmaCNF( conn, "", null, null, "Solicitud de autorizacion de pago", getCorreoAutoriza( conn ), getCuerpoCorreoAutoriza( conn ) );

		return "success";

	}

	@Override
	public void onCancelaTramite( Connection conn, String reason ) throws Exception {
		FirmaElectronicaManager.registraBitacoraCancelacion( conn, reason, this );

	}

	@Override
	public void onFinishAut( Connection conn ) throws Exception {

	}

	@Override
	public void onFinishVoBo( Connection conn ) throws Exception {

	}

	@Override
	public void onGeneraArchivosMasivo( Connection conn ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String generaArchivoInformeComision( Connection conn, Volumen vol, Carpeta folder, boolean isSignedCopy ) throws FirmaElectronicaException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getVoBoLegendWithName( Connection conn, String nombre, String puesto ) throws Exception {
		String voLegend = VO_BO_LEGEND_PREFIX.concat( " Firmado por: " ).concat( nombre  ).concat( " | " ).concat ( puesto );
	
		if ( tieneDelegatorioVoBO( conn ) ) {
			voLegend = VO_BO_LEGEND_PREFIX + " Firma " + getTipoSuplencia() + " de " + getNombreEmpleadoSuplido() + " con fundamento en el oficio: " + getFolioOficioVoBo() + " de fecha: " + getFechaOficioVoBo();
			log.info( voLegend );
		} 
		return voLegend;

	}
	
	@Override
	public String getAutLegendWithName( Connection conn , String nombre, String puesto) {
		try {
			String autLegend = AUT_LEGEND_PREFIX.concat( " Firmado por: " ).concat( nombre  ).concat( " | " ).concat ( puesto );
			
			if ( tieneDelegatorioAut( conn ) ) {
				autLegend = AUT_LEGEND_PREFIX + ". Firma " + getTipoSuplenciaAut() + " de " + getNombreEmpleadoSuplidoAut() + " con fundamento en el oficio: " + getFolioOficioAut() + " de fecha: " + getFechaOficioAut();
				log.info( autLegend );
			} 
			
			return autLegend;
		} catch ( Exception e ) {
			throw new RuntimeException( e );
		}
	}

	@Override
	public String notificaPrefirmante( Connection conn ) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
