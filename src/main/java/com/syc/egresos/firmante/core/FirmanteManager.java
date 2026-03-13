package com.syc.egresos.firmante.core;


import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.axtel.contratos.core.ContractQuestionnaire;
import com.syc.egresos.core.EgresoEncabezado;
import com.syc.egresos.firmante.servlet.Firmante;
import com.syc.egresos.firmante.servlet.FirmanteSuplente;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.sai.contabilidad.utils.db.RSToTable;
import com.syc.sai.firmaElectronica.interfaces.SolicitudFirmaElectronica;


public class FirmanteManager {

	private static final Logger log = Logger.getLogger( FirmanteManager.class );

	public static Firmante cargaFirmante( Connection conn, EgresoEncabezado encabezado, String tipoFirmante ) throws Exception {
		Firmante firmante = null;
		if ( Firmante.ELABORA.equals( tipoFirmante ) ) {
			firmante = cargaFirmanteEleabora( conn, encabezado.getTipoPago(), encabezado.getFolioPago() );
			firmante.setTipoAutorizador( Firmante.ELABORA );

		} else {
			StringBuilder query = new StringBuilder();
			query.append( "SELECT	nNumEmpleado" ).append( tipoFirmante );
			query.append( "  FROM	vTramiteFirmante " );
			query.append( " WHERE	cTipoPago=? " );
			query.append( "   AND	nFolioPago = ?" );

			PreparedStatement ps = null;
			ResultSet rs = null;

			try {
				ps = conn.prepareStatement( query.toString() );
				ps.setString( 1, encabezado.getTipoPago() );
				ps.setInt( 2, encabezado.getFolioPago() );
				rs = ps.executeQuery();
				if ( rs.next() ) {
					int numeroEmpleado = rs.getInt( 1 );
					return cargaFirmante( conn, numeroEmpleado, tipoFirmante.toUpperCase() );
				}
			} finally {
				CloseObject.closeObject( rs );
				CloseObject.closeObject( ps );
			}
		}

		return firmante;
	}

	private static Firmante cargaFirmanteEleabora( Connection conn, String tipoPago, int folioPago ) throws Exception {

		StringBuilder sb = new StringBuilder();
		sb.append( "SELECT	nombreEmpleado AS nombreEmpleado, " );
		sb.append( " 		cargo AS puestoEmpleado " );
		sb.append( "  FROM	v_TramiteUsuarioElabora " );
		sb.append( " WHERE	cTipoPago = ? " );
		sb.append( "   AND	nFolioPago = ?" );

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement( sb.toString() );
			ps.setString( 1, tipoPago );
			ps.setInt( 2, folioPago );

			rs = ps.executeQuery();
			Firmante firmante = new Firmante();
			BeanUtils.populate( firmante, RSToTable.rsToMapCaseSensitive( rs ) );
			return firmante;
		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}

	}

	public static Firmante cargaFirmante( Connection conn, int numeroEmpleado, String tipoFirmante ) throws Exception {
		StringBuilder query = new StringBuilder();
		query.append( "  select nombren + isnull( ' ' + NOMBREP, '') + ISNULL( ' ' + NOMBREM, '' ) AS nombreEmpleado, " );
		query.append( " 		cargo AS puestoEmpleado, " );
		query.append( " 		clave AS numeroEmpleado " );
		query.append( "  from v_empleados_giro where clave= ?" );

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			ps = conn.prepareStatement( query.toString() );
			ps.setInt( 1, numeroEmpleado );

			rs = ps.executeQuery();
			Firmante firmante = new Firmante();
			BeanUtils.populate( firmante, RSToTable.rsToMapCaseSensitive( rs ) );
			firmante.setTipoAutorizador( tipoFirmante );
			return firmante;
		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}
	}

	public static FirmanteSuplente cargaFirmanteSuplente( Connection conn, int numeroEmpleado, String tipoFirmante ) throws Exception {
		FirmanteSuplente suplente = new FirmanteSuplente( cargaFirmante( conn, numeroEmpleado, tipoFirmante ) );
		suplente.setTipoAutorizador( tipoFirmante );
		return suplente;
	}

	public static FirmanteSuplente cargaFirmanteSuplente( Connection conn, EgresoEncabezado encabezado, String tipoFirmante ) throws Exception {
		FirmanteSuplente suplente = new FirmanteSuplente();

		suplente.setTipoAutorizador( tipoFirmante );

		StringBuilder query = new StringBuilder();
		query.append( "SELECT	cNumeroEmpleado AS numeroEmpleado, " );
		query.append( "      	cFolioOficio, " );
		query.append( " 		dFechaOficio, " );
		query.append( " 		firmante.nTipoSuplencia, " );
		query.append( " 		tipoSuplencia.cTipoSuplencia, " );
		query.append( " 		UPPER(tipoSuplencia.cTipoSuplencia) + ' CON BASE AL OFICIO ' + cFolioOficio +' DE FECHA: ' + CONVERT(varchar(32), dFechaOficio, 103) AS motivoSuplencia " );
		query.append( "   FROM	tPagoFirmanteDelegatorio" ).append( tipoFirmante ).append( " firmante " );
		query.append( " 		INNER JOIN  " );
		query.append( " 		tCatTipoSuplencia tipoSuplencia " );
		query.append( " 		ON  " );
		query.append( " 		firmante.nTipoSuplencia = tipoSuplencia.nTipoSuplencia " );
		query.append( " WHERE	firmante.cTipoPago = ? " );
		query.append( "   AND	firmante.nFolioPago = ? " );

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement( query.toString() );
			ps.setString( 1, encabezado.getTipoPago() );
			ps.setInt( 2, encabezado.getFolioPago() );
			rs = ps.executeQuery();

			if ( rs.next() ) {
				suplente = new FirmanteSuplente( cargaFirmante( conn, rs.getInt( "numeroEmpleado" ), tipoFirmante ) );
				suplente.setFolioOficio( rs.getString( "cFolioOficio" ) );
				suplente.setFechaOficio( new Date( rs.getDate( "dFechaOficio" ).getTime() ) );
				suplente.setTipoSuplencia( rs.getString( "nTipoSuplencia" ) );
				suplente.setMotivoSuplencia( rs.getString( "motivoSuplencia" ) );
				suplente.setTipoAutorizador( tipoFirmante );
			} else {
				throw new Exception( "No se encontro registro del firmante." );
			}

		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}
		return suplente;
	}

	public static int saveFirmanteSuplente( Connection conn, FirmanteSuplente firmante, String tipoPago, int folioPago ) throws Exception {

		String table = "SUPVOBO".equalsIgnoreCase( firmante.getTipoAutorizador() ) ? "tPagoFirmanteDelegatorioVoBo" : "tPagoFirmanteDelagatorio";

		StringBuilder query = new StringBuilder();
		query.append( " INSERT INTO " ).append( table ).append( "(cTipoPago, nFolioPago, cFolioOficio, dFechaOficio, cPuestoTitular, nTipoSuplencia, cNumeroEmpleado) " );
		query.append( " VALUES(?, ?, ?, ?, ?, ?, ?) " );

		PreparedStatement ps = null;

		try {
			int i = 1;
			ps = conn.prepareStatement( query.toString() );
			ps.setString( i++, tipoPago );
			ps.setInt( i++, folioPago );
			ps.setString( i++, firmante.getFolioOficio() );
			ps.setDate( i++, new java.sql.Date( firmante.getFechaOficio().getTime() ) );
			ps.setString( i++, firmante.getPuestoEmpleado() );
			ps.setString( i++, firmante.getTipoSuplencia() );
			ps.setInt( i++, firmante.getNumeroEmpleado() );

			return ps.executeUpdate();

		} finally {
			CloseObject.closeObject( ps );
		}

	}

	public static int saveFirmante( Connection conn, Firmante firmante, String tipoPago, int folioPago ) throws Exception {

		String table = StringUtils.isBlank( SolicitudFirmaElectronica.RELACION_TRAMITE_TABLA_E.get( tipoPago ) ) ? ( "t" + tipoPago + "Encabezado" ) : SolicitudFirmaElectronica.RELACION_TRAMITE_TABLA_E.get( tipoPago );
		String llave = StringUtils.isBlank( SolicitudFirmaElectronica.RELACION_TRAMITE_KEY.get( tipoPago ) ) ? ( "nFolio" + tipoPago ) : SolicitudFirmaElectronica.RELACION_TRAMITE_KEY.get( tipoPago );
		String campo = "nNumEmpleado" + firmante.getTipoAutorizador();

		log.trace( "Insertando informacion de firmantes.\n Tabla: " + table + "\nllave:" + llave + "\ncampo:" + campo );

		StringBuilder query = new StringBuilder();
		query.append( " UPDATE " ).append( table ).append( " SET " ).append( campo ).append( " = ? " ).append( " WHERE " ).append( llave ).append( " = ?" );

		log.debug( "Se ejecutara: " + query );

		PreparedStatement ps = null;

		try {
			int i = 1;
			ps = conn.prepareStatement( query.toString() );
			ps.setInt( i++, firmante.getNumeroEmpleado() );
			ps.setInt( i++, folioPago );

			return ps.executeUpdate();

		} finally {
			CloseObject.closeObject( ps );
		}

	}

	public static int actualizaTipoFirma( Connection conn, String tipoPago, int folioPago, boolean esFirmaElectronica ) throws Exception {

		String table = StringUtils.isBlank( SolicitudFirmaElectronica.RELACION_TRAMITE_TABLA_E.get( tipoPago ) ) ? ( "t" + tipoPago + "Encabezado" ) : SolicitudFirmaElectronica.RELACION_TRAMITE_TABLA_E.get( tipoPago );
		String llave = StringUtils.isBlank( SolicitudFirmaElectronica.RELACION_TRAMITE_KEY.get( tipoPago ) ) ? ( "nFolio" + tipoPago ) : SolicitudFirmaElectronica.RELACION_TRAMITE_KEY.get( tipoPago );
		String campo = "cEsFirmaElectronica";

		StringBuilder query = new StringBuilder();
		query.append( " UPDATE " ).append( table ).append( " SET " ).append( campo ).append( " = ? " ).append( " WHERE " ).append( llave ).append( " = ?" );

		PreparedStatement ps = null;

		try {
			int i = 1;
			ps = conn.prepareStatement( query.toString() );
			ps.setString( i++, esFirmaElectronica ? "S" : "N" );
			ps.setInt( i++, folioPago );

			return ps.executeUpdate();

		} finally {
			CloseObject.closeObject( ps );
		}

	}

	public static int deleteFirmanteSuplente( Connection conn, FirmanteSuplente firmante, String tipoPago, int folioPago ) throws Exception {

		String table = "SUPVOBO".equalsIgnoreCase( firmante.getTipoAutorizador() ) ? "tPagoFirmanteDelegatorioVoBo" : "tPagoFirmanteDelagatorio";

		StringBuilder query = new StringBuilder();
		query.append( " DELETE FROM " ).append( table ).append( " WHERE cTipoPago = ? AND nFolioPago = ? " );

		PreparedStatement ps = null;

		try {

			int i = 1;
			ps = conn.prepareStatement( query.toString() );
			ps.setString( i++, tipoPago );
			ps.setInt( i++, folioPago );

			return ps.executeUpdate();

		} finally {
			CloseObject.closeObject( ps );
		}

	}

	public static Firmante readRequestSignatory( Connection conn, ContractQuestionnaire questionnaire ) throws IllegalAccessException, InvocationTargetException, Exception {

		StringBuilder querySel = new StringBuilder();
		querySel.append( "SELECT	dbo.tipoTitulo( NOMBREN + ' ' + NOMBREP + ' ' + NOMBREM) AS nombreEmpleado,  " );
		querySel.append( "		dbo.tipoTitulo( DESCRIPCION_PUESTO ) AS puestoEmpleado,  " );
		querySel.append( "		dbo.tipoTitulo( unidad) AS areaEmpleado,  " );
		querySel.append( "		clave AS numeroEmpleado,  " );
		querySel.append( "		d_email AS correoEmpleado " );
		querySel.append( " FROM	v_empleados_giro " );
		querySel.append( " WHERE	clave = ? " );

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			ps = conn.prepareStatement( querySel.toString() );
			ps.setString( 1, questionnaire.getEmployeeNumber() );

			rs = ps.executeQuery();
			Firmante firmante = new Firmante();
			BeanUtils.populate( firmante, RSToTable.rsToMapCaseSensitive( rs ) );
			return firmante;
		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}

	}

	public static List<Firmante> selectByModule( Connection conn, String modulo, String tipoFirmante, String ur ) {
		List<Firmante> firmantes = new ArrayList<>();
		String query = "SELECT DISTINCT * FROM tFirmanteModulo WITH(NOLOCK)  WHERE cModulo = ? AND cUnidadResponsable = ? AND cTipoFirmante = ?";

		log.trace( "Ejecutando consulta: " + query );
		log.trace( "Parámetros -> Modulo: [" + modulo + "], UR: [" + ur + "], TipoFirmante: [" + tipoFirmante + "]" );

		try ( PreparedStatement ps = conn.prepareStatement( query ) ) {
			
			ps.setString( 1, modulo );
			ps.setString( 2, ur );
			ps.setString( 3, tipoFirmante );

			try ( ResultSet rs = ps.executeQuery() ) {
				while ( rs.next() ) {
					Firmante firmante = new Firmante();
					firmante.setNombreEmpleado( rs.getString( "cNombre" ) + " " + rs.getString( "cApellidoPaterno" ) + " " + rs.getString( "cApellidoMaterno" ) );
					firmante.setNumeroEmpleado( rs.getInt( "nNumEmpleado" ) );
					firmante.setPuestoEmpleado( rs.getString( "cPuesto" ) );
					firmantes.add( firmante );
				}
			}
			log.info( "Se encontraron [" + firmantes.size() + "] firmantes para Modulo: [" + modulo + "]" );
		} catch ( SQLException e ) {
			log.error( "Error al obtener firmantes para Modulo: [" + modulo + "], Login: [" + ur + "], TipoFirmante: [" + tipoFirmante + "]", e );
			throw new RuntimeException( "Error al obtener firmantes", e );
		}

		return firmantes;
	}
}
