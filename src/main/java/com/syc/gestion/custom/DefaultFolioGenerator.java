package com.syc.gestion.custom;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.syc.contable.core.AdecuacionManager;
import com.syc.gestion.core.CFSequenceManager;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.core.UsuarioManager;


public final class DefaultFolioGenerator implements FolioGeneratorInterface {

	private static Logger log = Logger.getLogger( DefaultFolioGenerator.class );

	public String getNextFolio( Connection conn, String u_login, Caso c ) throws SQLException, GestionException {

		int id = -1;
		String prefijo = "";
		ResultSet rs = null;
		String retval = null;
		PreparedStatement pstmnt = null;
		Usuario u = new Usuario();
		u.setLogin( u_login );
		u = UsuarioManager.select( conn, u );
		// para completar el objeto usuario
		u = UsuarioManager.getRamoUR( conn, u );

		String UR = u.getU_UR();
		String cCentroContable = "";
		if ( u.getPropiedades() != null && u.getPropiedades().containsKey( "CCENTROCONTABLE" ) ) {
			cCentroContable = u.getPropiedad( "CCENTROCONTABLE" ).getValor();
		}

		/*
		 * Para diferentes prefijos segun el tipo de caso OJO los prefijos antes
		 * del primer guion deben ser de 4 posiciones
		 */
		try {
			switch ( c.getCasoOperacion( 0 ).getIdTC() ) {
				case 1:
					prefijo = "HV-";
				break;
				case 2:
					prefijo = "CPRE-" + UR + "-";
				break;
				case 3:
					prefijo = AdecuacionManager.obtenEjercicioFiscal( conn ) + "-" + UR + "-";
				break;
				case 4:
					prefijo = "PDIR-" + UR + "-";
				break;
				case 5:
					prefijo = "POBR-" + UR + "-";
				break;
				case 6:
					prefijo = "PDIV-" + UR + "-";
				break;
				case 7:
					prefijo = "COMP-" + UR + "-";
				break;
				case 8:
					prefijo = "GES-";
				break;
				case 9:
					prefijo = "CDIV-" + UR + "-";// Contrato de Diversos
				break;
				case 10:
					prefijo = "COBR-" + UR + "-";// Contratos de Obra
				break;
				case 11:
					prefijo = "RELG-" + UR + "-";
				break;
				case 12:
					prefijo = "NOMI-" + UR + "-";
				break;
				case 13:
					prefijo = "POLI-C" + cCentroContable + "-";
				break;
				// Folio para el precompromiso del modulo de materiales
				case 14:
					prefijo = "PRCP-" + UR + "-";
				break;
				case 15:
					prefijo = "AVI -" + UR + "-";// NO quitar el espacio antes
													// del primer guion
				break;
				case 16:
					prefijo = "PRES-" + UR + "-";// Se crea para Ante Proyecto
				break;
				case 17:
					prefijo = "ORE -" + UR + "-";// NO quitar el espacio antes
													// del primer guion
				break;
				case 18:
					prefijo = "OPAJ-" + UR + "-";// Se crea para operaciones
													// ajenas
				break;
				case 19:
					prefijo = "CALP-" + UR + "-";// Se crea para Ante Proyecto
				break;
				case 20:
					prefijo = "CFED-" + UR + "-";// Se crea para Contratos
													// Federalizados
				break;
				case 21:
					prefijo = "PFED-" + UR + "-";// Se crea para Pagos
													// Federalzados
				break;
				case 22:
					prefijo = "IADE-" + UR + "-";// Se crea para Integracion de
													// Adecuaciones
				break;
				case 23:
					prefijo = "OBRP-" + UR + "-";// Se crea para Obra Publica
				break;
				case 24:
					prefijo = "CHEQ-" + UR + "-";// Se crea para Emisión de
													// Cheques
				break;
				case 25:
					prefijo = "APTD-" + UR + "-";
				break;
				case 26:
					prefijo = "AVIN-" + UR + "-";
				break;
				case 27:
					prefijo = "OREN-" + UR + "-";
				break;
				case 28:
					prefijo = "PLUA-" + UR + "-";
				break;
				case 29:
					prefijo = "PANT-" + UR + "-";
				break;
				case 30:
					prefijo = "OAIN-" + UR + "-";// Se crea para la Integración
													// de Operaciones ajenas
				break;
				case 31:
					prefijo = "SRCB-" + UR + "-";// Se crea para solicitud de
													// registro de cuentas
													// bancarias
				break;
				case 32:
					prefijo = "MOCB-" + UR + "-";// Se crea para solicitud de
													// modificacion de cuentas
													// bancarias
				break;
				case 33:
					prefijo = "BACB-" + UR + "-";// Se crea para solicitud de
													// baja de cuentas bancarias
				break;
				case 34:
					prefijo = "FIAF-" + UR + "-";// Se crea para solicitud de
													// baja de cuentas bancarias
				break;
				case 35:
					prefijo = "PRMT-" + UR + "-";// Se crea para generar folio
													// de precompromiso de
													// materiales
				break;
				case 36:
					prefijo = "FNDN-" + UR + "-";// Se crea para solicitud de
													// baja de cuentas bancarias
				break;
				case 37:
					prefijo = "RDBA-" + UR + "-";// Se crea para solicitud de
													// registro diario bancos
				break;
				case 38:
					prefijo = "RIJS-" + UR + "-";// Subsidios temporalmente se
													// da este folio
				break;
				case 39:
					prefijo = "COMS-" + UR + "-";// Se crea para solicitud de
													// Autorizacion Comsoc
				break;
				case 40:
					prefijo = "IANT-" + UR + "-";// Se crea para solicitud de
													// Integracion de
													// Anteproyecto
				break;
				case 42:
					prefijo = "SNP -" + UR + "-";// Se crea para solicitud de
													// caja no presupuestal
				break;
				case 43:
					prefijo = "PDIV-" + UR + "-";
				break;
				case 44:
					prefijo = "INRG-" + UR + "-";
				break;
				case 45:
					prefijo = "INOA-" + UR + "-";
				break;
				case 46:
					prefijo = "DISD-" + UR + "-";
				break;
				case 47:
					prefijo = "PAAS-" + UR + "-";
				break;
				case 48:
					prefijo = "RECP-" + UR + "-";
				break;
				case 49:
					prefijo = "PROV-" + UR + "-";
				break;
				case 50:
					prefijo = "REIA-" + UR + "-";
				break;
				case 51:
					prefijo = "RECA-" + UR + "-";
				break;
				case 52:
					prefijo = "AVIA-" + UR + "-";
				break;
				case 53:
					prefijo = "PDF -" + UR + "-";
				break;
				case 54:
					prefijo = "COBA-" + UR + "-";
				break;
				case 55:
					prefijo = "INGR-" + UR + "-";
				break;
				case 56:
					prefijo = "RELG-" + UR + "-";
				break;
				case 57:
					prefijo = "PENA-" + UR + "-";
				break;
				case 58:
					prefijo = "CCON-" + UR + "-";
				break;
				case 59:
					prefijo = "REII-" + UR + "-";
				break;
				case 60:
					prefijo = "COMV-" + UR + "-";
				break;
				case 61:
					prefijo = "RTIN-" + UR + "-";
				break;
				case 62:
					prefijo = "PPCI-" + UR + "-";
				break;
				case 63:
					prefijo = "CFDI-" + UR + "-";
				break;
				case 64:
					prefijo = "CPLU-" + UR + "-";
				break;
				case 65:
					prefijo = "VIAT-" + UR + "-";
				break;
				case 67:
					prefijo = "MCT -" + UR + "-";
				break;
				case 68:
					prefijo = "MPLU-" + UR + "-";
				break;
				case 69:
					prefijo = "RETE-" + UR + "-";
				break;
				case 70:
					prefijo = "COCO-" + UR + "-";
				break;
				case 71:
					prefijo = "RECA-" + UR + "-";
				break;
				case 73:
					prefijo = "VIAT-" + UR + "-";
				break;
				case 75:
					prefijo = "COMP-" + UR + "-";
				break;
				case 76:
					prefijo = "PDIR-" + UR + "-";
				break;
				case 96:
					prefijo = "SUFI-" + UR + "-";
				break;
				case 97:
					prefijo = "PROY-" + UR + "-";
				break;
				case 98:
					prefijo = "PROV-" + UR + "-";
				break;
				case 99:
					prefijo = "ABCO-" + UR + "-";
				break;
				case 100:
					prefijo = "FUEL-" + UR + "-";
				break;
				case 101:
					prefijo = "ENSA-" + UR + "-";
				break;
				default:
					prefijo = "GES-";
				break;
			}

			CFSequenceManager cfm = CFSequenceManager.getInstance();
			
			if ( c.getTipoCaso().getIdTC() == 98 )
				id = cfm.nextVal( "PROVEEDORES" );
			else
				id = cfm.nextVal( c.getTipoCaso().getGavetaAsociada() );

			retval = prefijo + id;

		} finally {
			if ( rs != null )
				rs.close();

			if ( pstmnt != null )
				pstmnt.close();

			rs = null;
			pstmnt = null;
		}

		if ( log.isDebugEnabled() )
			log.debug( "Folio generado " + retval );

		return retval;
	}

	public String getNextFolioUsr( Connection conn, Usuario u, Caso c ) throws SQLException, GestionException {

		int id = -1;
		String prefijo = "";
		ResultSet rs = null;
		String retval = null;
		PreparedStatement pstmnt = null;

		String UR = u.getU_UR();
		String cCentroContable = "";
		if ( u.getPropiedades() != null && u.getPropiedades().containsKey( "CCENTROCONTABLE" ) ) {
			cCentroContable = u.getPropiedad( "CCENTROCONTABLE" ).getValor();
		}
		/*
		 * Para diferentes prefijos segun el tipo de caso OJO los prefijos antes
		 * del primer guion deben ser de 4 posiciones
		 */
		try {
			switch ( c.getCasoOperacion( 0 ).getIdTC() ) {
				case 1:
					prefijo = "HV-";
				break;
				case 2:
					prefijo = "CPRE-" + UR + "-";
				break;
				case 3:
					prefijo = AdecuacionManager.obtenEjercicioFiscal( conn ) + "-" + UR + "-";
				break;
				case 4:
					prefijo = "PDIR-" + UR + "-";
				break;
				case 5:
					prefijo = "POBR-" + UR + "-";
				break;
				case 6:
					prefijo = "PDIV-" + UR + "-";
				break;
				case 7:
					prefijo = "COMP-" + UR + "-";
				break;
				case 8:
					prefijo = "GES-";
				break;
				case 9:
					prefijo = "CDIV-" + UR + "-";
				break;
				case 10:
					prefijo = "COBR-" + UR + "-";
				break;
				case 11:
					prefijo = "RELG-" + UR + "-";
				break;
				case 12:
					prefijo = "NOMI-" + UR + "-";
				break;
				case 13:
					prefijo = "POLI-C" + cCentroContable + "-";
				break;
				// Folio para el precompromiso del modulo de materiales
				case 14:
					prefijo = "PRCP-" + UR + "-";
				break;
				case 15:
					prefijo = "AVI -" + UR + "-";// NO quitar el espacio antes
													// del primer guion
				break;
				case 16:
					prefijo = "PRES-" + UR + "-";// Se crea para Ante Proyecto
				break;
				case 17:
					prefijo = "ORE -" + UR + "-";// NO quitar el espacio antes
													// del primer guion
				break;
				case 18:
					prefijo = "OPAJ-" + UR + "-";// Se crea para Ante Proyecto
				break;
				case 19:
					prefijo = "CALP-" + UR + "-";// Se crea para Ante Proyecto
				break;
				case 20:
					prefijo = "CFED-" + UR + "-";// Se crea para Ante Proyecto
				break;
				case 21:
					prefijo = "PFED-" + UR + "-";// Se crea para Ante Proyecto
				break;
				case 22:
					prefijo = "IADE-" + UR + "-";// Se crea para Integracion de
													// Adecuaciones
				break;
				case 23:
					prefijo = "OBRP-" + UR + "-";// Se crea para Obra Publica
				break;
				case 24:
					prefijo = "CHEQ-" + UR + "-";// Se crea para Emisión de
													// Cheques
				break;
				case 25:
					prefijo = "APTD-" + UR + "-";
				break;
				case 26:
					prefijo = "AVIN-" + UR + "-";
				break;
				case 27:
					prefijo = "OREN-" + UR + "-";
				break;
				case 28:
					prefijo = "PLUA-" + UR + "-";
				break;
				case 29:
					prefijo = "PANT-" + UR + "-";
				break;
				case 30:
					prefijo = "OAIN-" + UR + "-";// Se crea para la Integración
													// de Operaciones ajenas
				break;
				case 31:
					prefijo = "SRCB-" + UR + "-";// Se crea para solicitud de
													// registro de cuentas
													// bancarias
				break;
				case 32:
					prefijo = "MOCB-" + UR + "-";// Se crea para solicitud de
													// modificacion de cuentas
													// bancarias
				break;
				case 33:
					prefijo = "BACB-" + UR + "-";// Se crea para solicitud de
													// baja de cuentas bancarias
				break;
				case 34:
					prefijo = "FIAF-" + UR + "-";// Se crea para solicitud de
													// baja de cuentas bancarias
				break;
				case 35:
					prefijo = "PRMT-" + UR + "-";// Se crea para generar folio
													// de precompromiso de
													// materiales
				break;
				case 36:
					prefijo = "FNDN-" + UR + "-";// Se crea para solicitud de
													// baja de cuentas bancarias
				break;
				case 37:
					prefijo = "RDBA-" + UR + "-";// Se crea para solicitud de
													// registro diario bancos
				break;
				case 38:
					prefijo = "RIJS-" + UR + "-";// Subsidios temporalmente se
													// da este folio
				break;
				case 39:
					prefijo = "COMS-" + UR + "-";// Se crea para solicitud de
													// Autorizacion Comsoc
				break;
				case 40:
					prefijo = "IANT-" + UR + "-";// Se crea para solicitud de
													// Integracion de
													// Anteproyecto
				break;
				case 42:
					prefijo = "SNP -" + UR + "-";// Se crea para solicitud de
													// caja no presupuestal
				break;
				case 43:
					prefijo = "PDIV-" + UR + "-";
				break;
				case 44:
					prefijo = "INRG-" + UR + "-";
				break;
				case 45:
					prefijo = "INOA-" + UR + "-";
				break;
				case 46:
					prefijo = "DISD-" + UR + "-";
				break;
				case 47:
					prefijo = "PAAS-" + UR + "-";
				break;
				case 48:
					prefijo = "RECP-" + UR + "-";
				break;
				case 49:
					prefijo = "PROV-" + UR + "-";
				break;
				case 50:
					prefijo = "REIA-" + UR + "-";
				break;
				case 51:
					prefijo = "RECA-" + UR + "-";
				break;
				case 52:
					prefijo = "AVIA-" + UR + "-";
				break;
				case 53:
					prefijo = "PDF -" + UR + "-";
				break;
				case 54:
					prefijo = "COBA-" + UR + "-";
				break;
				case 55:
					prefijo = "INGR-" + UR + "-";
				break;
				case 56:
					prefijo = "LAUD-" + UR + "-";
				break;
				case 57:
					prefijo = "PENA-" + UR + "-";
				break;
				case 58:
					prefijo = "CCON-" + UR + "-";
				break;
				case 59:
					prefijo = "REII-" + UR + "-";
				break;
				case 60:
					prefijo = "COMV-" + UR + "-";
				break;
				case 61:
					prefijo = "RTIN-" + UR + "-";
				break;
				case 62:
					prefijo = "PPCI-" + UR + "-";
				break;
				case 63:
					prefijo = "CFDI-" + UR + "-";
				break;
				case 64:
					prefijo = "CPLU-" + UR + "-";
				break;
				case 65:
					prefijo = "VIAT-" + UR + "-";
				break;
				case 67:
					prefijo = "MCT -" + UR + "-";
				break;
				case 68:
					prefijo = "MPLU-" + UR + "-";
				break;
				case 69:
					prefijo = "RETE-" + UR + "-";
				break;
				case 70:
					prefijo = "COCO-" + UR + "-";
				break;
				case 71:
					prefijo = "RECA-" + UR + "-";
				break;
				case 72:
					prefijo = "PECO-" + UR + "-";
				break;
				case 73:
					prefijo = "VIAT-" + UR + "-";
				break;
				case 75:
					prefijo = "COMP-" + UR + "-";
				break;
				case 76:
					prefijo = "PDIR-" + UR + "-";
				break;
				case 96:
					prefijo = "SUFI-" + UR + "-";
				break;
				case 97:
					prefijo = "PROY-" + UR + "-";
				break;
				case 98:
					prefijo = "PROV-" + UR + "-";
				break;
				case 99:
					prefijo = "ABCO-" + UR + "-";
				break;
				case 100:
					prefijo = "FUEL-" + UR + "-";
				break;
				case 101:
					prefijo = "ENSA-" + UR + "-";
				break;
				default:
					prefijo = "GES-";
				break;
			}

			CFSequenceManager cfm = CFSequenceManager.getInstance();
			
			if ( c.getTipoCaso().getIdTC() == 98 )
				id = cfm.nextVal( "PROVEEDORES" );
			else
				id = cfm.nextVal( c.getTipoCaso().getGavetaAsociada() );
			
			retval = prefijo + id;

		} finally {
			if ( rs != null )
				rs.close();

			if ( pstmnt != null )
				pstmnt.close();

			rs = null;
			pstmnt = null;
		}

		if ( log.isDebugEnabled() )
			log.debug( "Folio generado " + retval );

		return retval;
	}

	// Metodo para generar folios para el pago descentralizado
	public static String getNextFolioDes( Connection conn, String UR, Caso c ) throws SQLException, GestionException {

		int id = -1;
		String prefijo = ""; // Ethiel
		ResultSet rs = null;
		String retval = null;
		PreparedStatement pstmnt = null;
		/*
		 * Para diferentes prefijos segun el tipo de caso OJO los prefijos antes
		 * del primer guion deben ser de 4 posiciones
		 */
		try {
			switch ( c.getCasoOperacion( 0 ).getIdTC() ) {
				case 1:
					prefijo = "HV-";
				break;
				case 2:
					prefijo = "CPRE-" + UR + "-";
				break;
				case 3:
					prefijo = AdecuacionManager.obtenEjercicioFiscal( conn ) + "-" + UR + "-";
				break;
				case 4:
					prefijo = "PDIR-" + UR + "-";
				break;
				case 5:
					prefijo = "POBR-" + UR + "-";
				break;
				case 6:
					prefijo = "PDIV-" + UR + "-";
				break;
				case 7:
					prefijo = "COMP-" + UR + "-";
				break;
				case 8:
					prefijo = "GES-";
				break;
				case 9:
					prefijo = "CDIV-" + UR + "-";
				break;
				case 10:
					prefijo = "COBR-" + UR + "-";
				break;
				case 11:
					prefijo = "RELG-" + UR + "-";
				break;
				case 12:
					prefijo = "NOMI-" + UR + "-";
				break;
				case 13:
					prefijo = "POLI-" + UR + "-";
				break;
				// Folio para el precompromiso del modulo de materiales
				case 14:
					prefijo = "PRCP-" + UR + "-";
				break;
				case 15:
					prefijo = "AVI -" + UR + "-";// NO quitar el espacio antes
													// del primer guion
				break;
				case 16:
					prefijo = "PRES-" + UR + "-";// Se crea para Ante Proyecto
				break;
				case 17:
					prefijo = "ORE -" + UR + "-";// NO quitar el espacio antes
													// del primer guion
				break;
				case 18:
					prefijo = "OPAJ-" + UR + "-";// Se crea para Ante Proyecto
				break;
				case 19:
					prefijo = "CALP-" + UR + "-";// Se crea para Ante Proyecto
				break;
				case 20:
					prefijo = "CFED-" + UR + "-";// Se crea para Ante Proyecto
				break;
				case 21:
					prefijo = "PFED-" + UR + "-";// Se crea para Ante Proyecto
				break;
				case 22:
					prefijo = "IADE-" + UR + "-";// Se crea para Integracion de
													// Adecuaciones
				break;
				case 23:
					prefijo = "OBRP-" + UR + "-";// Se crea para Obra Publica
				break;
				case 24:
					prefijo = "CHEQ-" + UR + "-";// Se crea para Emisión de
													// Cheques
				break;
				case 25:
					prefijo = "APTD-" + UR + "-";
				break;
				case 34:
					prefijo = "FIAF-" + UR + "-";// Se crea para solicitud de
													// baja de cuentas bancarias
				break;
				case 35:
					prefijo = "PRMT-" + UR + "-";// Se crea para generar folio
													// de precompromiso de
													// materiales
				break;
				case 36:
					prefijo = "FNDN-" + UR + "-";// Se crea para solicitud de
													// baja de cuentas bancarias
				break;
				case 39:
					prefijo = "COMS-" + UR + "-";// Se crea para solicitud de
													// Autorizacion Comsoc
				break;
				case 40:
					prefijo = "IANT-" + UR + "-";// Se crea para solicitud de
													// Integracion de
													// Anteproyecto
				break;
				case 42:
					prefijo = "SNP -" + UR + "-";// Se crea para solicitud de
													// caja no presupuestal
				break;
				case 43:
					prefijo = "PDIV-" + UR + "-";
				break;
				case 44:
					prefijo = "INRG-" + UR + "-";
				break;
				case 45:
					prefijo = "INOA-" + UR + "-";
				break;
				case 46:
					prefijo = "DISD-" + UR + "-";
				break;
				case 47:
					prefijo = "PAAS-" + UR + "-";
				break;
				case 48:
					prefijo = "RECP-" + UR + "-";
				break;
				case 49:
					prefijo = "PROV-" + UR + "-";
				break;
				case 50:
					prefijo = "REIA-" + UR + "-";
				break;
				case 51:
					prefijo = "RECA-" + UR + "-";
				break;
				case 52:
					prefijo = "AVIA-" + UR + "-";
				break;
				case 53:
					prefijo = "PDF -" + UR + "-";
				break;
				case 54:
					prefijo = "COBA-" + UR + "-";
				break;
				case 55:
					prefijo = "INGR-" + UR + "-";
				break;
				case 56:
					prefijo = "LAUD-" + UR + "-";
				break;
				case 57:
					prefijo = "PENA-" + UR + "-";
				break;
				case 58:
					prefijo = "CCON-" + UR + "-";
				break;
				case 59:
					prefijo = "REII-" + UR + "-";
				break;
				case 60:
					prefijo = "COMV-" + UR + "-";
				break;
				case 61:
					prefijo = "RTIN-" + UR + "-";
				break;
				case 62:
					prefijo = "PPCI-" + UR + "-";
				break;
				case 63:
					prefijo = "CFDI-" + UR + "-";
				break;
				case 64:
					prefijo = "CPLU-" + UR + "-";
				break;
				case 65:
					prefijo = "CONC-" + UR + "-";
				break;
				case 68:
					prefijo = "MPLU-" + UR + "-";
				break;
				case 69:
					prefijo = "RETE-" + UR + "-";
				break;
				case 70:
					prefijo = "COCO-" + UR + "-";
				break;
				case 71:
					prefijo = "RECA-" + UR + "-";
				break;
				case 72:
					prefijo = "PECO-" + UR + "-";
				break;
				case 73:
					prefijo = "VIAT-" + UR + "-";
				break;
			 
				case 75:
					prefijo = "COMP-" + UR + "-";
				break;
				case 96:
					prefijo = "SUFI-" + UR + "-";
				break;
				case 99:
					prefijo = "ABCO-" + UR + "-";
				break;
				case 100:
					prefijo = "FUEL-" + UR + "-";
				break;
				case 101:
					prefijo = "ENSA-" + UR + "-";
				break;
				default:
					prefijo = "GES-";
				break;
			}

			CFSequenceManager cfm = CFSequenceManager.getInstance();
			id = cfm.nextVal( c.getTipoCaso().getGavetaAsociada() );
			retval = prefijo + id;

		} finally {
			if ( rs != null )
				rs.close();

			if ( pstmnt != null )
				pstmnt.close();

			rs = null;
			pstmnt = null;
		}

		if ( log.isDebugEnabled() )
			log.debug( "Folio generado " + retval );

		return retval;
	}
}
