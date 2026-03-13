package com.axtel.ws.clients;


import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.axtel.web.clients.InvoiceDTO;
import com.axtel.web.clients.TimbrarCFDIViaticos;
import com.axtel.web.clients.ViaticoCFDI;
import com.axtel.web.clients.WSClient;
import com.axtel.ws.exceptions.WSException;
import com.syc.cfdi.utils.CloseObject;


public class NotificaCFDIClientes extends WSClient {

	private static final Logger	log	= LogManager.getLogger( NotificaCFDIClientes.class );
	
	public NotificaCFDIClientes( String urlService ) {
		
		super();
		log.info( "Creado cliente para servicio de notificacion de creacion de CFDI. URL[" + urlService + "]" );
		setUrlService( urlService );
				
	}

	public static InvoiceDTO readCFDIViaticos (Connection conn, String cxp) throws Exception {
		InvoiceDTO invoiceDto = new InvoiceDTO();
		ViaticoCFDI viaticoCFDI = new ViaticoCFDI();
		PreparedStatement pst = null;
		ResultSet rs = null;
		StringBuilder query = new StringBuilder();
		try {
			
			query.append( "SELECT ben.nIdEmpleado" );
			query.append( ", RIGHT('00'+ CAST(DAY(faplicacion) as varchar(2)),2) + '/' + RIGHT('00'+ CAST(MONTH(faplicacion) as varchar(2)),2) + '/' +  CAST(YEAR(faplicacion) as varchar(4)) fAplicacion");
			query.append( "	, caNoContrarrecibo, nFolioRELACIONGASTOS  " );
			query.append( "	, SUBSTRING(cConcepto,1,50) concepto " );
			query.append( "	, mImporteNeto " );
			query.append( "	FROM tRELACIONGASTOSEncabezado rg WITH (NOLOCK) " );
			query.append( " INNER JOIN tBeneficiario ben WITH (NOLOCK) ON rg.RFC = ben.dRFC ");
			query.append( "	WHERE cDocumentoHaplicado = 'S' and caNoContrarrecibo = ? ");
			
			pst = conn.prepareStatement( query.toString() );
			pst.setString( 1, cxp );
			rs = pst.executeQuery();
			
			if (rs.next()) {
				viaticoCFDI.setIdEmpleado( rs.getInt( "nIdEmpleado" ));
				viaticoCFDI.setFechaPago( rs.getString( "fAplicacion") );
				viaticoCFDI.setContraRecibo( cxp );
				viaticoCFDI.setFolioSAI( rs.getString( "nFolioRELACIONGASTOS") );
				viaticoCFDI.setTipoDocumento( rs.getString( "concepto" ));
				viaticoCFDI.setA200( rs.getBigDecimal( "mImporteNeto" ));
				viaticoCFDI.setI200( rs.getBigDecimal( "mImporteNeto" ));
				viaticoCFDI.setE200( rs.getBigDecimal( "mImporteNeto" ));
				viaticoCFDI.setE201( new BigDecimal( "0.00" ));
				viaticoCFDI.setE202( new BigDecimal( "0.00" ));
				viaticoCFDI.setSubproceso( 1 );
				
			}
			
			invoiceDto.setViaticoCFDI( viaticoCFDI );
			invoiceDto.setAction( "generarCFDI" );
			
		} finally {
			
			CloseObject.closeObject( pst, false );
			CloseObject.closeObject( rs, false );
		}
		
		
		return invoiceDto;
	}

	public ViaticoCFDIRespuesta generaCFDI( Connection conn, InvoiceDTO generarCFDIViaticos ) throws Exception {

		log.debug( "Iniciando envio de registro de creacion de CFDI: " + generarCFDIViaticos.getViaticoCFDI().getContraRecibo() );

		log.info( "Se consumira servicio en: " + getUrlService()  );
		WebTarget target = getClient().target( getUrlService() );
		Builder requestBuilder = target.request( MediaType.APPLICATION_JSON );
		Entity<InvoiceDTO> msgJSON = Entity.json( generarCFDIViaticos );
		log.trace( "Enviando: " + msgJSON );
		Response rsp = requestBuilder.post( msgJSON );

		ViaticoCFDIRespuesta viaticoCFDIRespuesta = null;
		viaticoCFDIRespuesta = rsp.readEntity( ViaticoCFDIRespuesta.class );
		
		if ( rsp.getStatus() != HttpServletResponse.SC_OK  || ! viaticoCFDIRespuesta.isResult() ) {
			String motivoError = "";
			
			try {
				guardaRespuesta(conn, generarCFDIViaticos.getViaticoCFDI().getContraRecibo(), viaticoCFDIRespuesta);
				motivoError = viaticoCFDIRespuesta.getMessage();
			
			} catch ( Exception e ) {
				log.warn( e, e );
			}

			throw new WSException( "Problemas en request a endpoint[] Status: " + rsp.getStatus() + " - " + motivoError );
		}

		guardaRespuesta(conn, generarCFDIViaticos.getViaticoCFDI().getContraRecibo(), viaticoCFDIRespuesta);
		
		log.info( "Registro de CFDI terminado. Resultado:" + rsp );
		return viaticoCFDIRespuesta;

	}
	
	public int guardaRespuesta(Connection conn, String cxp, ViaticoCFDIRespuesta respuesta) throws Exception {
		int inserta = 0;
		PreparedStatement pst = null;
		String sql = "INSERT INTO tRespuestaGeneraCFDI (nPeriodo, cMensaje, cResultado, cxp) VALUES (?, ?, ?, ?)";

		try {
			pst = conn.prepareStatement( sql );
			pst.setInt( 1, respuesta.getPeriodo() );
			pst.setString( 2, respuesta.getMessage() );
			pst.setString( 3, (respuesta.isResult() == true ? "S" : "N" ) );
			pst.setString( 4, cxp );
			
			inserta = pst.executeUpdate();
			
		} finally {
			CloseObject.closeObject( pst, false );
		}
		
		return inserta;
	}
	
	public int guardaRespuestaNoEncontrado(Connection conn, String cxp, String cMensaje) throws Exception {
		int inserta = 0;
		PreparedStatement pst = null;
		String sql = "INSERT INTO tRespuestaGeneraCFDI (nPeriodo, cMensaje, cResultado, cxp) VALUES (?, ?, ?, ?)";

		try {
			pst = conn.prepareStatement( sql );
			pst.setInt( 1, 0 );
			pst.setString( 2, cMensaje );
			pst.setString( 3, "N");
			pst.setString( 4, cxp );
			
			inserta = pst.executeUpdate();
			
		} finally {
			CloseObject.closeObject( pst, false );
		}
		
		return inserta;
	}
	
	public static TimbrarCFDIViaticos readTimbrarCFDI (Connection conn, String cxp) throws Exception {
		TimbrarCFDIViaticos timbre = new TimbrarCFDIViaticos();
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			pst = conn.prepareStatement( "SELECT nPeriodo FROM tRespuestaGeneraCFDI where cxp = ? and cResultado = 'S'" );
			pst.setString( 1, cxp );
			rs = pst.executeQuery();
			
			if (rs.next()) {
				timbre.setPeriodo( rs.getInt( "nPeriodo" )  );
			}
			
		} finally {
			
			CloseObject.closeObject( pst, false );
			CloseObject.closeObject( rs, false );
		}
		
		
		return timbre;
	}

	
	public RespuestaTimbradoCFDI timbrarCFDI( Connection conn, TimbrarCFDIViaticos timbreCFDI, String cxp ) throws Exception {

		log.debug( "Iniciando envio de registro de timbrado de CFDI del periodo: " + timbreCFDI.getPeriodo());

		log.info( "Se consumira servicio en: " + getUrlService()  );
		WebTarget target = getClient().target( getUrlService() );
		Builder requestBuilder = target.request( MediaType.APPLICATION_JSON );
		Entity<TimbrarCFDIViaticos> msgJSON = Entity.json( timbreCFDI );
		log.trace( "Enviando: " + msgJSON );
		Response rsp = requestBuilder.post( msgJSON );

		RespuestaTimbradoCFDI repuestaTimbrado = null;
		repuestaTimbrado = rsp.readEntity( RespuestaTimbradoCFDI.class );
		if ( rsp.getStatus() != HttpServletResponse.SC_OK || repuestaTimbrado.isResult() ==false ) {
			
			try {
				guardaRespuestaTimbrado(conn, cxp, repuestaTimbrado);
				
			
			} catch ( Exception e ) {
				log.warn( e, e );
			}

		} else {
			
			guardaRespuestaTimbrado(conn, cxp, repuestaTimbrado);
			
		}
		
		log.info( "Registro de CFDI terminado. Resultado:" + rsp );
		return repuestaTimbrado;

	}
	
	public int guardaRespuestaTimbrado(Connection conn, String cxp, RespuestaTimbradoCFDI respuesta) throws Exception {
		int inserta = 0;
		PreparedStatement pst = null;
		String sql = "UPDATE tRespuestaGeneraCFDI SET cTimbrado = ? , cRespuestaTimbrado = ? WHERE cxp = ?";

		try {
			pst = conn.prepareStatement( sql );
			pst.setString( 1, (respuesta.isResult() == false || respuesta == null ?  "N" : "S" ) );
			pst.setString( 2, respuesta.getMessage() );
			pst.setString( 3, cxp );
			
			inserta = pst.executeUpdate();
			
		} finally {
			CloseObject.closeObject( pst, false );
		}
		
		return inserta;
	}

	public int esSolicitudViaticos(Connection conn, int folio) throws Exception {
		int inserta = 0;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String sql = "SELECT COUNT(*) FROM tRELACIONGASTOSEncabezado  WITH (NOLOCK) WHERE cIdRelacion LIKE 'VIATICOS%' AND nFolioRELACIONGASTOS = ? ";

		try {
			pst = conn.prepareStatement( sql );
			pst.setInt( 1, folio );
			
			rs = pst.executeQuery();
			
			if(rs.next()) {
				inserta = rs.getInt( 1 );
			}
			
		} finally {
			CloseObject.closeObject( pst, false );
		}
		
		return inserta;
	}

}
