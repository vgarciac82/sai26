package com.syc.obrapublica.core;


import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.fortimax.core.Aplicacion;
import com.syc.fortimax.core.AplicacionManager;
import com.syc.fortimax.core.Carpeta;
import com.syc.fortimax.core.CarpetaManager;
import com.syc.fortimax.core.Documento;
import com.syc.fortimax.core.DocumentoManager;
import com.syc.fortimax.core.Pagina;
import com.syc.fortimax.core.Volumen;
import com.syc.fortimax.core.VolumenManager;
import com.syc.gestion.core.AlarmaManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.CasoOperacion;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import com.syc.obrapublica.core.manager.FirmaAutorizacionObraManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.firmaElectronica.exceptions.EstimacionObraException;
import com.syc.sai.firmaElectronica.exceptions.FirmaElectronicaException;
import com.syc.sai.firmaElectronica.exceptions.NotEmptyDocumentException;
import com.syc.sai.firmaElectronica.interfaces.SolicitudFirmaElectronica;


public class EstimacionObraFIEL extends SolicitudFirmaElectronica {

	private static final Logger	log	= LogManager.getLogger( EstimacionObraFIEL.class );
	private DataSourceManager	ds	= null;
	private String				motivoRechazo;
	private DatosEstimacionObra	estimacionObra;

	public EstimacionObraFIEL( ) {
		super();
		ds = new DataSourceManager() {};
	}

	public void solicitaAutorizacionEstimacion() throws EstimacionObraException {
		boolean error = true;
		Connection conn = null;
		try {
			conn = ds.getConnection();
			saveEstimacionObra( conn );
			solicitaAutorizacionEstimacion( conn );
			conn.commit();
			error = false;
		} catch ( SQLException e ) {
			error = true;
			throw new EstimacionObraException( "Error de base de datos al solicitar autorizacion de la Estimación de obra: " + e.toString(), e );
		} finally {
			if ( error ) {
				if ( conn != null )
					try {
						conn.rollback();
					} catch ( Exception e2 ) {
						log.warn( "Problemas en rollback: " + e2 );
					}
			}
			CloseObject.closeObject( conn );
		}
	}

	private void saveEstimacionObra( Connection conn ) throws SQLException {
		FirmaAutorizacionObraManager.saveAtentaNota( conn, getEstimacionObra() );
	}

	private void solicitaAutorizacionEstimacion( Connection conn ) throws EstimacionObraException {
		try {
			generaArchivoEstimacion( conn, "Estimaciones" );
			notificaOperacionPendiente( conn, SolicitudFirmaElectronica.AUT_EST );
			FirmaAutorizacionObraManager.avanzaEstatus( conn, this.getEstimacionObra() );
		} catch ( Exception e ) {
			throw new EstimacionObraException( "Error solicitando autorizacion de Estimación de Obra Pública: " + e.toString(), e );
		}
	}

	private String generaArchivoEstimacion( Connection conn, String folderName ) throws EstimacionObraException {

		String filename = "";

		try {

			Volumen vol = VolumenManager.getVolumen( conn );
			int idCabinet = -1;

			idCabinet = getIDGabinete( conn );

			if ( idCabinet <= 0 )
				idCabinet = creaExpediente( conn );

			Carpeta folder = CarpetaManager.getCarpetaByName( conn, getDocument(), idCabinet, folderName );
			if ( folder == null )
				folder = createFolder( conn, idCabinet, folderName );

			if ( DocumentoManager.existeDocumentoCapturado( conn, folder.getTituloAplicacion(), folder.getIdGabinete(), folder.getIdCarpeta(), getDocName() ) )
				return DocumentoManager.getDocumento( conn, folder.getTituloAplicacion(), folder.getIdGabinete(), folder.getIdCarpeta(), getDocName() ).getFullPathFilesNames()[0];

			filename = generaArchivoFirma( conn, vol, folder, false );
			return filename;
		} catch ( SQLException e ) {
			throw new EstimacionObraException( "Error de base de datos al generar el documento: " + e.toString(), e );
		} catch ( Exception e ) {
			throw new EstimacionObraException( "Error al generar el documento: " + e.toString(), e );
		}

	}

	public int getIDGabinete( Connection conn ) throws SQLException, EstimacionObraException {
		PreparedStatement ps = null;
		ResultSet rs = null;

		StringBuilder query = new StringBuilder();
		query.append( "SELECT ID_GABINETE FROM imx" );
		query.append( getDocument() );
		query.append( " WITH(NOLOCK) WHERE folio LIKE '%-%-' + ?" );
		try {
			ps = conn.prepareStatement( query.toString() );
			ps.setString( 1, String.valueOf( getIdField() ) );
			rs = ps.executeQuery();
			int idCabinet = -1;

			if ( rs.next() )
				idCabinet = rs.getInt( "ID_GABINETE" );

			return idCabinet;
		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}

	}

	private int creaExpediente( Connection conn ) throws EstimacionObraException {

		try {
			int idCaso = findIDCaso( conn );
			Caso rc = new Caso();
			rc.setIdCaso( idCaso );

			rc = CasoManager.select( conn, rc );

			Aplicacion app = AplicacionManager.select( conn, rc.getTipoCaso().getGavetaAsociada() );
			int idGabiente = AplicacionManager.createExpediente( conn, getUsuario().getLogin(), rc, app );
			rc.setIdGabinete( idGabiente );
			CasoManager.update( conn, rc );
			return idGabiente;
		} catch ( SQLException e ) {
			throw new EstimacionObraException( "Error de base de datos al crear expediente: " + e, e );
		}

	}

	private int findIDCaso( Connection conn ) throws EstimacionObraException {
		StringBuilder query = new StringBuilder();
		query.append( "SELECT	id_caso " );
		query.append( "  FROM	cg_caso WITH(NOLOCK) " );
		query.append( " WHERE	id_tc = 23 " );
		query.append( "   AND	c_folio =  ? " );

		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement( query.toString() );
			ps.setString( 1, getEstimacionObra().getcFolioObra() );
			rs = ps.executeQuery();

			if ( rs.next() )
				return rs.getInt( 1 );
			else
				throw new EstimacionObraException( "No se encontro caso con folio: " + getEstimacionObra().getcFolioObra() + " id_tc = 23 " );
		} catch ( SQLException e ) {
			throw new EstimacionObraException( "Error de base de datos buscando caso para el id: " + getIdField() + " : " + e, e );
		} finally {
			CloseObject.closeObject( ps );
			CloseObject.closeObject( rs );
		}

	}

	public String getMotivoRechazo() {
		return motivoRechazo;
	}

	public void setMotivoRechazo( String motivoRechazo ) {
		this.motivoRechazo = motivoRechazo;
	}

	public DatosEstimacionObra getEstimacionObra() {
		return estimacionObra;
	}

	public void setEstimacionObra( DatosEstimacionObra estimacionObra ) {
		this.estimacionObra = estimacionObra;
	}

	@Override
	public String generaArchivoFirma( Connection conn, Volumen vol, Carpeta folder, boolean isSignedCopy ) throws FirmaElectronicaException {
		Map<String, Object> parametrosReporte = new HashMap<String, Object>();
		parametrosReporte.put( "SUBREPORT_DIR", getReportPath() + File.separatorChar + "FIEL" );
		StringBuilder nameReporte = new StringBuilder();

		parametrosReporte.put( "nIDNota", new Integer( getEstimacionObra().getIdNota() ) );
		nameReporte.append( "rptNotaObra.jasper" );

		String filename = null;
		try {
			filename = generaArchivoFirma( conn, vol, folder, parametrosReporte, "FIEL", nameReporte.toString(), isSignedCopy );
		} catch ( NotEmptyDocumentException nede ) {
			log.warn( "El documento no esta vacio. Se ignora" + nede );
		} catch ( Exception e ) {
			throw new FirmaElectronicaException( e );
		}

		return filename;
	}

	@Override
	public String getAutLegend( Connection conn ) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCuerpoCorreoAutoriza( Connection conn ) throws Exception {
		ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic( GestionInterface.ATT_CONEXION );
		String urlAutorizacion = cabl.getSystemSetting( "URL_SAI" ) + "/egresos/AutEst";
		String urlDescarga = generaURLDescarga( conn );

		String nombreCompleto = getAutNombre( conn );
		String puesto = getAutPuesto( conn );
		String concepto = FirmaAutorizacionObraManager.getConceptoContrato( conn, getEstimacionObra().getContratoCNET() );
		String[] importe = getImporteEstimacion( conn );

		StringBuilder mailBody = new StringBuilder( "<html>" );
		mailBody.append( "\n\t<head> " );
		mailBody.append( "\n\t<meta charset=\"UTF-8\"> " );
		mailBody.append( "\n\t<style type=\"text/css\"> " );
		mailBody.append( "\n\tbody { " );
		mailBody.append( "\n\t\t	font-family: verdana, arial, sans-serif; " );
		mailBody.append( "\n\t\t	font-size: 12px; " );
		mailBody.append( "\n\t} " );

		mailBody.append( "\n\ttable { " );
		mailBody.append( "\n\t\tfont-size: 12px; " );
		mailBody.append( "\n\t\tcolor: #333333; " );
		mailBody.append( "\n\t\tborder-width: 1px; " );
		mailBody.append( "\n\t\tborder-color: #666666; " );
		mailBody.append( "\n\t\tborder-collapse: collapse; " );
		mailBody.append( "\n\t} " );

		mailBody.append( "\n\ttable th { " );
		mailBody.append( "\n\t\tborder-width: 1px; " );
		mailBody.append( "\n\t\tpadding: 8px; " );
		mailBody.append( "\n\t\tborder-style: solid; " );
		mailBody.append( "\n\t\tborder-color: #666666; " );
		mailBody.append( "\n\t\tbackground-color: #dedede; " );
		mailBody.append( "\n\t} " );

		mailBody.append( "\n\ttable td { " );
		mailBody.append( "\n\t\tborder-width: 1px; " );
		mailBody.append( "\n\t\tpadding: 8px; " );
		mailBody.append( "\n\t\tborder-style: solid; " );
		mailBody.append( "\n\t\tborder-color: #666666; " );
		mailBody.append( "\n\t\tbackground-color: #ffffff; " );
		mailBody.append( "\n\t} " );
		mailBody.append( "\n\t</style> " );
		mailBody.append( "</head> " );
		mailBody.append( "\n\t<body> " );
		mailBody.append( "\n\t\t<form id=\"Form\" name=\"FormViaticos\" > " );
		mailBody.append( "	<b> C." + nombreCompleto + "</b> " );
		mailBody.append( "	<br> " );
		mailBody.append( "	<b>" + puesto + "</b> " );
		mailBody.append( "	<br /> " );
		mailBody.append( "		Se solicita la autorización de " );// de la
																	// Estimación
																	// de Obra
																	// Pública:
		mailBody.append( ( getEstimacionObra().getnEstimacion() == 0 ? "Anticipo" : "la Estimación " + String.valueOf( getEstimacionObra().getnEstimacion() ) ) );
		mailBody.append( " " );
		mailBody.append( concepto );

		mailBody.append( "	<table> " );
		mailBody.append( "		<thead> " );
		mailBody.append( "			<tr> " );
		mailBody.append( "				<th>Estimación</th> " );
		mailBody.append( "				<th>Importe Bruto</th> " );
		mailBody.append( "				<th>Impuestos</th> " );
		mailBody.append( "				<th>Total</th> " );
		mailBody.append( "			</tr> " );
		mailBody.append( "		</thead> " );
		mailBody.append( "		<tbody> " );

		mailBody.append( "<tr> " );
		mailBody.append( "\n<td>" + ( getEstimacionObra().getnEstimacion() == 0 ? "Anticipo" : String.valueOf( getEstimacionObra().getnEstimacion() ) ) + "</td> " );
		mailBody.append( "\n<td>" + importe[0] + "</td> " );
		mailBody.append( "\n<td>" + importe[2] + "</td> " );
		mailBody.append( "\n<td>" + importe[1] + "</td> " );
		mailBody.append( "</tr> " );
		mailBody.append( "		</tbody> " );
		mailBody.append( "	</table> " );
		mailBody.append( "	<br /> " );
		mailBody.append( "	<br /> " );

		mailBody.append( generaLigaDoctos( conn, urlDescarga, getEstimacionObra().getNumeroEmpleado(), "Autorizaci&oacute;n" ).toString() );
		mailBody.append( "\n<b>Para autorizar o rechazar esta solicitud por favor de click <a href=\"" );
		mailBody.append( urlAutorizacion );
		mailBody.append( generaAccessoAutToken( getEstimacionObra().getNumeroEmpleado(), getDocument(), getEstimacionObra().getIdNota() ) );
		mailBody.append( "\" > aquí </a>.</b> " );

		mailBody.append( "	<br /> " );
		mailBody.append( "	<br /> " );
		mailBody.append( "	<p> " );
		mailBody.append( "		Notificaciones Automaticas<br />Sistema de Administracion Integral<br />" + Util.getToday() );
		mailBody.append( "	</p> " );
		mailBody.append( "	</form> " );
		mailBody.append( "</body> " );
		mailBody.append( "</html> " );

		return mailBody.toString();

	}

	private StringBuilder generaLigaDoctos( Connection conn, String urlDescarga, int numeroEmpleado, String operacion ) throws Exception {

		StringBuilder expedientBody = new StringBuilder();
		expedientBody.append( "	<br />" );
		expedientBody.append( "	<p>" );
		expedientBody.append( "	En la tabla siguiente puede revisar el documento que sera firmado." );
		expedientBody.append( "	</p>" );
		expedientBody.append( "<table id=\"docsTbl\">" );
		expedientBody.append( "		<thead>" );
		expedientBody.append( "			<tr>" );
		expedientBody.append( "				<th>Archivo a Firmar</th>" );
		expedientBody.append( "				<th>Motivo</th>" );
		expedientBody.append( "			</tr>" );
		expedientBody.append( "		</thead>" );
		expedientBody.append( "		<tbody>" );
		expedientBody.append( "			<tr>" );
		expedientBody.append( "				<td>" );
		expedientBody.append( "				<a href=\"" + urlDescarga + generaRutaArchivo( conn, numeroEmpleado, getDocument(), getIdField() ) + "\" > Atenta Nota </a>" );
		expedientBody.append( "				</td>" );
		expedientBody.append( "				<td>" );
		expedientBody.append( "				Firma de " + operacion + " de la estimaci&oacute;n de obra p&uacute;blica." );
		expedientBody.append( "				</td>" );
		expedientBody.append( "			</tr>" );
		expedientBody.append( "</table>" );

		expedientBody.append( "	<br />" );
		expedientBody.append( "	<br />" );

		return expedientBody;

	}

	private String generaRutaArchivo( Connection conn, int numeroEmpleado, String document, int idField ) throws Exception {

		Caso c = CasoManager.findByFolioLike( conn, document, String.valueOf( idField ) );
		Documento documento = DocumentoManager.buscaDocumento( conn, document, c.getIdGabinete(), getDocName() );

		StringBuilder fortimaxNode = new StringBuilder( document ).append( "_" ).append( "G" ).append( c.getIdGabinete() ).append( "C" ).append( documento.getIdCarpetaPadre() ).append( "D" ).append( documento.getIdDocumento() );

		StringBuilder parametrosReales = null;

		/*
		 * Concatena los parametros. El separador sera el caracter | (pipe)
		 */
		parametrosReales = new StringBuilder( "?" );
		parametrosReales.append( "fortimax=" ).append( fortimaxNode );
		log.debug( "Cadena generada: " + parametrosReales.toString() );

		return parametrosReales.toString();

	}

	private String[] getImporteEstimacion( Connection conn ) throws EstimacionObraException {

		StringBuilder query = new StringBuilder();
		query.append( "SELECT CONVERT(VARCHAR(32), mmontoestimacion, 103) AS mMontoSinIVA, " );
		query.append( "       CONVERT(VARCHAR(32), mMontoEstimacionMasIva, 103) AS mMontoConIVA, " );
		query.append( "       CONVERT(VARCHAR(32), mMontoEstimacionIva, 103)    AS mMontoIVA " );
		query.append( "FROM   tobrapublicaavancefisico  WITH(nolock) " );
		query.append( "WHERE  ccvecontrato = ? " );
		query.append( "       AND foliosai = ? " );
		query.append( "       AND noestimacion = ? " );

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			ps = conn.prepareStatement( query.toString() );
			ps.setString( 1, getEstimacionObra().getContratoCNET() );
			ps.setString( 2, getEstimacionObra().getcFolioObra() );
			ps.setInt( 3, getEstimacionObra().getnEstimacion() );

			rs = ps.executeQuery();
			if ( rs.next() )
				return new String [] { rs.getString( "mMontoSinIVA" ), rs.getString( "mMontoConIVA" ), rs.getString( "mMontoIVA" ) };
			else
				throw new EstimacionObraException( "No se encontro informacion de la RM " );
		} catch ( SQLException e ) {
			throw new EstimacionObraException( "Error de DB mientras se buscaba informacion de la RM: " + e );
		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}

	}

	@Override
	public String getAutNombre( Connection conn ) throws Exception {

		StringBuilder query = new StringBuilder();
		query.append( "SELECT	U_NOMBRE  " );
		query.append( "   FROM	CG_USUARIO WITH(NOLOCK)  " );
		query.append( "  WHERE	cNumeroEmpleado =  ?  " );
		query.append( "    AND	U_ESTATUS = 'A'" );
		PreparedStatement psNombre = null;
		ResultSet rsNombre = null;

		try {

			int numeroEmpleado = getEstimacionObra().getNumeroEmpleado();

			psNombre = conn.prepareStatement( query.toString() );
			psNombre.setInt( 1, numeroEmpleado );
			rsNombre = psNombre.executeQuery();

			if ( rsNombre.next() ) {
				if ( StringUtils.isBlank( rsNombre.getString( "U_NOMBRE" ) ) )
					throw new Exception( "El empleado con Numero: " + numeroEmpleado + " no tiene nombre asignado. Notifique al administrador" );
				return rsNombre.getString( "U_NOMBRE" );
			} else
				throw new Exception( "No se encontro empleado con numero: " + numeroEmpleado );

		} finally {
			CloseObject.closeObject( psNombre );
			CloseObject.closeObject( rsNombre );
		}
	}

	@Override
	public String getAutPuesto( Connection conn ) throws EstimacionObraException {

		StringBuilder queryPuesto = new StringBuilder( "SELECT	CARGO " );
		queryPuesto.append( "  FROM	v_empleados_giro WITH(NOLOCK) " );
		queryPuesto.append( " WHERE	CLAVE = ? " );
		PreparedStatement psPuesto = null;
		ResultSet rsPuesto = null;

		try {

			int numeroEmpleado = getEstimacionObra().getNumeroEmpleado();

			psPuesto = conn.prepareStatement( queryPuesto.toString() );
			psPuesto.setInt( 1, numeroEmpleado );
			rsPuesto = psPuesto.executeQuery();

			if ( rsPuesto.next() ) {
				if ( StringUtils.isBlank( rsPuesto.getString( "CARGO" ) ) )
					throw new EstimacionObraException( "El empleado con Numero: " + numeroEmpleado + " no tiene puesto asignado. Notifique al administrador" );
				return rsPuesto.getString( "CARGO" );
			} else
				throw new EstimacionObraException( "No se encontro empleado con numero: " + numeroEmpleado );

		} catch ( SQLException e ) {
			throw new EstimacionObraException( "Error de base de datos buscando empleado: " + e, e );
		} finally {
			CloseObject.closeObject( psPuesto );
			CloseObject.closeObject( rsPuesto );
		}
	}

	@Override
	public String getCuerpoCorreoVistoBueno( Connection conn ) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getImporteStr( Connection conn ) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getVoBoLegend( Connection conn ) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void notificaOperacionMasivaPendiente( Connection conn, String operacion ) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public String notificaOperacionPendiente( Connection conn, String tipoAutorizacion ) throws Exception {
		try {
			AlarmaManager.procesaAlarmaCNF( conn, "", null, null, "Solicitud de autorizacion de la estimación ".concat( String.valueOf( getEstimacionObra().getnEstimacion() ) ).concat( " del contrato " ).concat( getEstimacionObra().getContratoCNET() ), getCorreoAutoriza( conn, this.getEstimacionObra().getNumeroEmpleado() ), getCuerpoCorreoAutoriza( conn ) );

			return "success";
		} catch ( EstimacionObraException e ) {
			throw e;
		} catch ( Exception e ) {
			throw new EstimacionObraException( "Error notificando operacion pendiente: " + e, e );
		}
	}

	private String getCorreoAutoriza( Connection conn, int numeroEmpleado ) throws EstimacionObraException {
		StringBuilder query = new StringBuilder();
		query.append( "SELECT	U_EMAIL  " );
		query.append( "   FROM	CG_USUARIO WITH(NOLOCK)  " );
		query.append( "  WHERE	cNumeroEmpleado =  ?  " );
		query.append( "    AND	U_ESTATUS = 'A'" );

		PreparedStatement psMail = null;
		ResultSet rsMail = null;

		try {
			psMail = conn.prepareStatement( query.toString() );
			psMail.setInt( 1, numeroEmpleado );

			rsMail = psMail.executeQuery();
			if ( rsMail.next() ) {
				if ( StringUtils.isBlank( rsMail.getString( "U_EMAIL" ) ) )
					throw new EstimacionObraException( "El empleado con Numero: " + numeroEmpleado + " no tiene correo asignado. Notifique al administrador" );
				return rsMail.getString( "U_EMAIL" );
			} else
				throw new EstimacionObraException( "No se encontro empleado con numero: " + numeroEmpleado );

		} catch ( SQLException e ) {
			throw new EstimacionObraException( "Error de base de datos buscando correo de autorizador. Notifique al administrador " + e, e );
		} finally {
			CloseObject.closeObject( psMail );
			CloseObject.closeObject( rsMail );
			query = null;
		}
	}

	public void rechazaEstimacion( Connection conn ) throws Exception {
		this.getEstimacionObra().setIdEstatusEstimacion( SolicitudFirmaElectronica.ESTATUS_ESTIMACION_CANCELADA );
		this.getEstimacionObra().setMontos( getImporteEstimacion( conn ) );
		FirmaAutorizacionObraManager.avanzaEstatus( conn, this.getEstimacionObra() );
		cambiaNoEstimacion( conn );
		cambiaFolioNota( conn );
		cambiaNombreDocumento( conn );
		notificaCancelacion( conn );
		registraBitacora( conn, RECHAZA_ESTIMACION );

	}

	public void registrarBitacora( Connection conn, String estatus ) throws Exception {
		registraBitacora( conn, estatus );
	}

	private void cambiaNombreDocumento( Connection conn ) throws Exception {
		int idGabinete = getIDGabinete( conn );
		String nombreDocumento = "Atenta Nota ".concat( ( getEstimacionObra().getnEstimacion() == 0 ) ? "Anticipo 0" : "Estimacion " + String.valueOf( getEstimacionObra().getnEstimacion() ) );
		Documento d = DocumentoManager.buscaDocumento( conn, getDocument(), idGabinete, nombreDocumento );
		// Cambiar nombre
		d.setDescripcion( nombreDocumento );
		nombreDocumento = "Atenta Nota Estimacion ".concat( String.valueOf( getEstimacionObra().getnEstimacionCancelado() ) );
		d.setNombreDocumento( nombreDocumento );
		DocumentoManager.updateDocumento( conn, d );
	}

	private void cambiaNoEstimacion( Connection conn ) throws SQLException, EstimacionObraException {
		int initMinimo = -1;
		// obtener noEstimacion minimo
		int minimo = FirmaAutorizacionObraManager.getMinimoNoEstimacion( conn, getEstimacionObra() );
		if ( minimo > 0 ) {
			minimo = initMinimo;
		} else {
			--minimo;
		}
		getEstimacionObra().setnEstimacionCancelado( minimo );
		FirmaAutorizacionObraManager.updateIdEstimacionAtentaNota( conn, getEstimacionObra() );
		FirmaAutorizacionObraManager.updateIdEstimacionAvanceFisico( conn, getEstimacionObra() );
	}

	private void cambiaFolioNota( Connection conn ) throws SQLException, EstimacionObraException {
		getEstimacionObra().setFolioNotaCancelada( ESTIMACION_CANCELADA + " " + getEstimacionObra().getnEstimacionCancelado() );
		FirmaAutorizacionObraManager.updateFolioAtentaNota( conn, getEstimacionObra() );
	}

	private void notificaCancelacion( Connection conn ) throws Exception {
		String subject = "Estimación de obra: " + ( getEstimacionObra().getnEstimacion() == 0 ? " Anticipo" : String.valueOf( getEstimacionObra().getnEstimacion() ) ) + " del contrato: " + getEstimacionObra().getContratoCNET() + " rechazado.";
		String[] datosUsuario = getCorreoElabora( conn );
		String nombreCompleto = datosUsuario[0];
		String correo = datosUsuario[1];
		String[] importe = getEstimacionObra().getMontos();

		StringBuilder mailBody = new StringBuilder();
		mailBody.append( "<html>" );
		mailBody.append( "\n\t<head>" );
		mailBody.append( "\n\t<meta charset=\"UTF-8\">" );
		mailBody.append( "\n\t<style type=\"text/css\">" );
		mailBody.append( "\n\tbody {" );
		mailBody.append( "\n\t\t	font-family: verdana, arial, sans-serif;" );
		mailBody.append( "\n\t\t	font-size: 12px;" );
		mailBody.append( "\n\t}" );

		mailBody.append( "\n\ttable {" );
		mailBody.append( "\n\t\tfont-size: 12px;" );
		mailBody.append( "\n\t\tcolor: #333333;" );
		mailBody.append( "\n\t\tborder-width: 1px;" );
		mailBody.append( "\n\t\tborder-color: #666666;" );
		mailBody.append( "\n\t\tborder-collapse: collapse;" );
		mailBody.append( "\n\t}" );

		mailBody.append( "\n\ttable th {" );
		mailBody.append( "\n\t\tborder-width: 1px;" );
		mailBody.append( "\n\t\tpadding: 8px;" );
		mailBody.append( "\n\t\tborder-style: solid;" );
		mailBody.append( "\n\t\tborder-color: #666666;" );
		mailBody.append( "\n\t\tbackground-color: #dedede;" );
		mailBody.append( "\n\t}" );

		mailBody.append( "\n\ttable td {" );
		mailBody.append( "\n\t\tborder-width: 1px;" );
		mailBody.append( "\n\t\tpadding: 8px;" );
		mailBody.append( "\n\t\tborder-style: solid;" );
		mailBody.append( "\n\t\tborder-color: #666666;" );
		mailBody.append( "\n\t\tbackground-color: #ffffff;" );
		mailBody.append( "\n\t}" );
		mailBody.append( "\n\t</style>" );
		mailBody.append( "</head>" );
		mailBody.append( "\n\t<body>" );
		mailBody.append( "\n\t\t<form id=\"Form\" name=\"FormViaticos\" >" );
		mailBody.append( "	<b> C." ).append( nombreCompleto ).append( "</b>" );
		mailBody.append( "	<br>" );
		mailBody.append( "	<p>" );
		mailBody.append( "		Se notifica la estimaci&oacute;n " );
		mailBody.append( ( getEstimacionObra().getnEstimacion() == 0 ? " de Anticipo" : String.valueOf( getEstimacionObra().getnEstimacion() ) ) );
		mailBody.append( " del contrato: " );
		mailBody.append( getEstimacionObra().getContratoCNET() );
		mailBody.append( " fue  <b>RECHAZADA</b>:" );
		mailBody.append( "	</p>" );

		mailBody.append( "	<table>" );
		mailBody.append( "		<thead>" );
		mailBody.append( "			<tr>" );
		mailBody.append( "				<th>Estimaci&oacute;n</th>" );
		mailBody.append( "				<th>Importe Bruto</th>" );
		mailBody.append( "				<th>Impuestos</th>" );
		mailBody.append( "				<th>Total</th>" );
		mailBody.append( "			</tr>" );
		mailBody.append( "		</thead>" );
		mailBody.append( "		<tbody>" );

		mailBody.append( "<tr>" );
		mailBody.append( "\n<td>" + ( getEstimacionObra().getnEstimacion() == 0 ? "Anticipo" : String.valueOf( getEstimacionObra().getnEstimacion() ) ) + "</td>" );
		mailBody.append( "\n<td>" + importe[0] + "</td> " );
		mailBody.append( "\n<td>" + importe[2] + "</td> " );
		mailBody.append( "\n<td>" + importe[1] + "</td> " );
		mailBody.append( "</tr>" );
		mailBody.append( "		</tbody>" );
		mailBody.append( "	</table>" );
		mailBody.append( "	<br />" );

		mailBody.append( "	<p>" );
		mailBody.append( "		Debido a:<br>" );
		mailBody.append( "	</p>" );

		mailBody.append( "	<pre>" );
		mailBody.append( getMotivoRechazo() );
		mailBody.append( "	</pre>" );

		mailBody.append( "	<br />" );
		mailBody.append( "	<br />" );
		mailBody.append( "\n<b>Para los fines que considere convenientes.</b>" );
		mailBody.append( "	<br />" );
		mailBody.append( "	<br />" );
		mailBody.append( "	<p>" );
		mailBody.append( "		Notificaciones Automaticas<br />Sistema de Administracion Integral<br />" + Util.getToday() );
		mailBody.append( "	</p>" );
		mailBody.append( "	</form>" );
		mailBody.append( "</body>" );
		mailBody.append( "</html>" );

		AlarmaManager.procesaAlarmaCNF( conn, "", null, null, subject, correo, mailBody.toString() );
	}

	public void notificaAutorizacion( Connection conn, File file, String newNameDocto ) throws Exception {
		String subject = "Estimación de obra: " + ( getEstimacionObra().getnEstimacion() == 0 ? " Anticipo" : String.valueOf( getEstimacionObra().getnEstimacion() ) ) + " del contrato: " + getEstimacionObra().getContratoCNET() + " autorizado.";
		String[] datosUsuario = getCorreoElabora( conn );
		String nombreCompleto = datosUsuario[0];
		String correo = datosUsuario[1];
		String[] importe = getImporteEstimacion( conn );

		StringBuilder mailBody = new StringBuilder();
		mailBody.append( "<html>" );
		mailBody.append( "\n\t<head>" );
		mailBody.append( "\n\t<meta charset=\"UTF-8\">" );
		mailBody.append( "\n\t<style type=\"text/css\">" );
		mailBody.append( "\n\tbody {" );
		mailBody.append( "\n\t\t	font-family: verdana, arial, sans-serif;" );
		mailBody.append( "\n\t\t	font-size: 12px;" );
		mailBody.append( "\n\t}" );

		mailBody.append( "\n\ttable {" );
		mailBody.append( "\n\t\tfont-size: 12px;" );
		mailBody.append( "\n\t\tcolor: #333333;" );
		mailBody.append( "\n\t\tborder-width: 1px;" );
		mailBody.append( "\n\t\tborder-color: #666666;" );
		mailBody.append( "\n\t\tborder-collapse: collapse;" );
		mailBody.append( "\n\t}" );

		mailBody.append( "\n\ttable th {" );
		mailBody.append( "\n\t\tborder-width: 1px;" );
		mailBody.append( "\n\t\tpadding: 8px;" );
		mailBody.append( "\n\t\tborder-style: solid;" );
		mailBody.append( "\n\t\tborder-color: #666666;" );
		mailBody.append( "\n\t\tbackground-color: #dedede;" );
		mailBody.append( "\n\t}" );

		mailBody.append( "\n\ttable td {" );
		mailBody.append( "\n\t\tborder-width: 1px;" );
		mailBody.append( "\n\t\tpadding: 8px;" );
		mailBody.append( "\n\t\tborder-style: solid;" );
		mailBody.append( "\n\t\tborder-color: #666666;" );
		mailBody.append( "\n\t\tbackground-color: #ffffff;" );
		mailBody.append( "\n\t}" );
		mailBody.append( "\n\t</style>" );
		mailBody.append( "</head>" );
		mailBody.append( "\n\t<body>" );
		mailBody.append( "\n\t\t<form id=\"Form\" name=\"FormViaticos\" >" );
		mailBody.append( "	<b> C." ).append( nombreCompleto ).append( "</b>" );
		mailBody.append( "	<br>" );
		mailBody.append( "	<p>" );
		mailBody.append( "		Se notifica la estimaci&oacute;n " );
		mailBody.append( ( getEstimacionObra().getnEstimacion() == 0 ? " de Anticipo" : String.valueOf( getEstimacionObra().getnEstimacion() ) ) );
		mailBody.append( " del contrato: " );
		mailBody.append( getEstimacionObra().getContratoCNET() );
		mailBody.append( " fue  <b>AUTORIZADA</b>:" );
		mailBody.append( "	</p>" );

		mailBody.append( "	<table>" );
		mailBody.append( "		<thead>" );
		mailBody.append( "			<tr>" );
		mailBody.append( "				<th>Estimaci&oacute;n</th>" );
		mailBody.append( "				<th>Importe Bruto</th>" );
		mailBody.append( "				<th>Impuestos</th>" );
		mailBody.append( "				<th>Total</th>" );
		mailBody.append( "			</tr>" );
		mailBody.append( "		</thead>" );
		mailBody.append( "		<tbody>" );

		mailBody.append( "<tr>" );
		mailBody.append( "\n<td>" + ( getEstimacionObra().getnEstimacion() == 0 ? "Anticipo" : String.valueOf( getEstimacionObra().getnEstimacion() ) ) + "</td>" );
		mailBody.append( "\n<td>" + importe[0] + "</td> " );
		mailBody.append( "\n<td>" + importe[2] + "</td> " );
		mailBody.append( "\n<td>" + importe[1] + "</td> " );
		mailBody.append( "</tr>" );
		mailBody.append( "		</tbody>" );
		mailBody.append( "	</table>" );
		mailBody.append( "	<br />" );

		mailBody.append( "	<br />" );
		mailBody.append( "	<br />" );
		mailBody.append( "\n<b>Para los fines que considere convenientes.</b>" );
		mailBody.append( "	<br />" );
		mailBody.append( "	<br />" );
		mailBody.append( "	<p>" );
		mailBody.append( "		Notificaciones Automaticas<br />Sistema de Administracion Integral<br />" + Util.getToday() );
		mailBody.append( "	</p>" );
		mailBody.append( "	</form>" );
		mailBody.append( "</body>" );
		mailBody.append( "</html>" );

		AlarmaManager.procesaAlarmaAttachmentCNF( conn, null, null, null, subject, correo, mailBody.toString(), file, false );
	}

	private void registraBitacora( Connection conn, String operacion ) throws Exception {

		StringBuilder query = new StringBuilder();
		query.append( "INSERT INTO tBitacoraFirma " );
		query.append( "        ( cTipoDocumento , " );
		query.append( "          nFolioDocumento , " );
		query.append( "          dFechaOperacion , " );
		query.append( "          U_LOGIN , " );
		query.append( "          cOperacion " );
		query.append( "        ) " );
		query.append( "VALUES  ( ?, " );
		query.append( "          ?, " );
		query.append( "          ?, " );
		query.append( "          ?, " );
		query.append( "          ? " );
		query.append( "        ) " );
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement( query.toString() );
			ps.setString( 1, getDocument() );
			ps.setInt( 2, getEstimacionObra().getIdNota() );
			ps.setTimestamp( 3, new Timestamp( System.currentTimeMillis() ) );
			ps.setString( 4, getUsuario().getLogin() );
			ps.setString( 5, operacion );

			ps.executeUpdate();

		} finally {
			CloseObject.closeObject( ps );
		}

	}

	private String[] getCorreoElabora( Connection conn ) throws SQLException, EstimacionObraException {
		StringBuilder query = new StringBuilder();
		query.append( "SELECT u_nombre, u_email " );
		query.append( "FROM   tNotaAutorizaEstimacion est WITH(nolock) " );
		query.append( "       INNER JOIN cg_usuario u WITH(nolock) " );
		query.append( "               ON est.cidusuariocaptura = u.u_login " );
		query.append( "WHERE  nidnota = ?  " );

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement( query.toString() );
			ps.setInt( 1, getEstimacionObra().getIdNota() );
			rs = ps.executeQuery();
			if ( rs.next() )
				return new String [] { rs.getString( 1 ), rs.getString( 2 ) };
			else
				throw new EstimacionObraException( "No se encontro informaci\u00f3n del usuario que registro la estimaci\u00f3n: " + ( getEstimacionObra().getnEstimacion() == 0 ? "Anticipo" : String.valueOf( getEstimacionObra().getnEstimacion() ) ) );

		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}
	}

	public String getRutaReporteImpreso( Connection conn ) throws SQLException, EstimacionObraException {
		return getDocumentoFirma( conn ).getFullPathFilesNames()[0];
	}

	private Documento getDocumentoFirma( Connection conn ) throws SQLException, EstimacionObraException {
		setDocName( "Atenta Nota ".concat( ( getEstimacionObra().getnEstimacion() == 0 ) ? "Anticipo 0" : "Estimacion " + String.valueOf( getEstimacionObra().getnEstimacion() ) ) );
		int idGabinete = getIDGabinete( conn );
		return DocumentoManager.buscaDocumento( conn, getDocument(), idGabinete, getDocName() );
	}

	public void updateSignedDocto( Connection conn, File signedFile ) throws SQLException, EstimacionObraException {

		Documento d = getDocumentoFirma( conn );
		if ( d == null )
			throw new EstimacionObraException( "No se encontro el documento firmado para actualizar." );

		DocumentoManager.respaldaPagina( conn, d );

		Pagina pagina = d.getPaginaDocumento( 0 );
		String nomArchivo = Util.getFileWithoutExtencion( signedFile.getName() );
		pagina.setNomArchivoOrg( nomArchivo + ".pdf" );
		pagina.setNomArchivoVol( nomArchivo + ".tif" );

		d.setPaginasDocumento( new Pagina [] { pagina } );
		DocumentoManager.actualizaRutaPagina( conn, d );

	}

	@Override
	public void onCancelaTramite( Connection conn, String reason ) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFinishAut( Connection conn ) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFinishVoBo( Connection conn ) throws Exception {
		// TODO Auto-generated method stub

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
		String voLegend = VO_BO_LEGEND_PREFIX.concat( " Firmado por: " ).concat( nombre ).concat( " | " ).concat( puesto );

		if ( tieneDelegatorioVoBO( conn ) ) {
			voLegend = VO_BO_LEGEND_PREFIX + " Firma " + getTipoSuplencia() + " de " + getNombreEmpleadoSuplido() + " con fundamento en el oficio: " + getFolioOficioVoBo() + " de fecha: " + getFechaOficioVoBo();
			log.info( voLegend );
		}

		return voLegend;

	}

	@Override
	public String getAutLegendWithName( Connection conn, String nombre, String puesto ) {
		try {
			String autLegend = AUT_LEGEND_PREFIX.concat( " Firmado por: " ).concat( nombre ).concat( " | " ).concat( puesto );

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
