
package com.syc.contable.core;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.syc.gestion.core.AlarmaManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;


/**
 * @author Martha Aurora Sánchez Valdivieso para SYC Constructores de Sistemas
 *         desarrollo gestion_conagua_sif México D.F. 16/02/2012
 *
 */

public class BeneficiarioManager {

	public static SimpleDateFormat	fecha			= new SimpleDateFormat( "dd/MM/yyyy" );
	public static String			fechaEfectiva	= fecha.format( new Date( System.currentTimeMillis() ) );
	private static final Logger		log				= Logger.getLogger( BeneficiarioManager.class );

	public static StringBuffer BuscaBenCBan( Connection conn, String listaCBEN ) throws Exception {
		StringBuffer archivoBenCtasBan = new StringBuffer();
		PreparedStatement pstmntBCB = null;
		ResultSet rsBCB = null;
		try {
			String SqlBenCBanc = "SELECT estatusL,cRamo,CBEN,clabe,CBAN,sucursal FROM  vListaCuentasBancarias bcb  WITH (nolock)  WHERE bcb.CBEN in (" + listaCBEN + ") AND bcb.nEnviadoSICOP = 0";
			pstmntBCB = conn.prepareStatement( SqlBenCBanc );
			rsBCB = pstmntBCB.executeQuery();
			archivoBenCtasBan.append( "H\r\n" );
			while ( rsBCB.next() ) {
				String detalleCuentas = fechaEfectiva + ",S" + "," + rsBCB.getString( 2 ) + // cRamo
						"," + rsBCB.getString( 3 ) + // CBEN
						"," + rsBCB.getString( 4 ) + // clabe
						"," + rsBCB.getString( 5 ) + // CBAN
						"," + rsBCB.getString( 6 ); // sucursal
				detalleCuentas = detalleCuentas + "\r\n";
				archivoBenCtasBan.append( detalleCuentas );
			}

		} finally {
			CloseObject.closeObject( rsBCB );
			CloseObject.closeObject( pstmntBCB );
		}
		return archivoBenCtasBan;
	}

	//// Datos de layout para bendoccom:
	public static StringBuffer BuscaBenDocCom( Connection conn, String listaCBEN ) throws Exception {
		StringBuffer archivoBenDC = new StringBuffer();
		PreparedStatement pstmntDC = null;
		ResultSet rsDC = null;
		String SqlBDC = "SELECT * FROM  vLayoutBDC ben WHERE ben.CBEN in (" + listaCBEN + ")  ";

		try {
			pstmntDC = conn.prepareStatement( SqlBDC );
			rsDC = pstmntDC.executeQuery();

			archivoBenDC.append( "H\r\n" );
			while ( rsDC.next() ) {
				String detalle2 = fechaEfectiva + // FECHA_EFECTIVA
						",S" + // + rsDC.getString(1) + //ACTIVO
						"," + rsDC.getString( 2 ) + // ID_RAMO
						"," + rsDC.getString( 3 ).trim() + // CBEN
						"," + rsDC.getString( 4 ).trim() + // TIPOPER
						"," + rsDC.getString( 5 ).trim() + // TBEN
						"," + rsDC.getString( 6 ).trim() + // RFC
						"," + rsDC.getString( 7 ).trim() + // CURP
						"," + rsDC.getString( 8 ).trim() + // RAZON_SOC
						"," + rsDC.getString( 9 ).trim() + // CALLE
						"," + rsDC.getString( 10 ).trim() + // NO_EXT
						"," + rsDC.getString( 11 ).trim() + // NO_INT
						"," + rsDC.getString( 12 ).trim() + // COLONIA
						"," + rsDC.getString( 13 ).trim() + // MUNICIPIO
						"," + rsDC.getString( 14 ).trim() + // ESTADO_BEN
						"," + rsDC.getString( 15 ).trim() + // CIUDAD
						"," + rsDC.getString( 16 ).trim() + // PAIS
						"," + rsDC.getString( 17 ) + // CP
						"," + rsDC.getString( 18 ) + // TELEFONO
						"," + rsDC.getString( 19 ) + // FAX
						"," + rsDC.getString( 20 ) + // MOVIL
						"," + rsDC.getString( 21 ); // MAIL
				detalle2 = detalle2 + "\r\n";
				archivoBenDC.append( detalle2 );
			}
		} finally {
			CloseObject.closeObject( rsDC );
			CloseObject.closeObject( pstmntDC );
		}
		return archivoBenDC;
	}

	// Datos de layout para ben_
	public static StringBuffer BuscaBeneficiarios( Connection conn, String listaCBEN ) throws Exception {
		StringBuffer archivoBen = new StringBuffer();
		PreparedStatement pstmntD = null;
		ResultSet rs = null;
		String SqlBen = "SELECT " + "		'S' AS ACTIVO" + "		,'16' as ID_RAMO" + "		,RTRIM(LTRIM(ISNULL(tB.CBEN,''))) as CBEN" + "		,CASE tPer.cTipoPersonaRFC WHEN 'PERSONA MORAL' THEN 'MORAL' " + "			WHEN 'PERSONA FISICA' THEN 'FISICA' " + "			WHEN 'EMPLEADO CNF' THEN 'FISICA' " + "			WHEN 'AREA CNF' THEN 'MORAL' " + "			WHEN 'COMISIÓN NACIONAL FORESTAL' THEN 'MORAL' " + "			ELSE '' END AS TIPOPER " + "		,CASE tB.cExtranjero " + "			WHEN 0 THEN '04'" + "			WHEN 1 THEN '05'" + "			ELSE '00' END AS TBEN" + "		,'16' as RAMO_SIAFF" + "		,RTRIM(LTRIM(ISNULL(tB.dRFC,''))) as RFC" + "		,RTRIM(LTRIM(ISNULL(tB.dCURP,''))) as CURP" + "		,RTRIM(LTRIM(ISNULL(tB.dApellidoPaterno,''))) as APEPAT" + "		,RTRIM(LTRIM(ISNULL(tB.dApellidoMaterno,''))) as APEMAT" + "		,CASE WHEN tB.cIdTipoPersonaRFC IN ('0','1','4') THEN '' ELSE RTRIM(LTRIM(ISNULL(tB.dNombre,''))) END as NOMBRE" + "		,RTRIM(LTRIM(ISNULL(tB.dApellidoPaterno,'') + ' ' + ISNULL(tB.dApellidoMaterno,'') + ' ' + ISNULL(tB.dNombre,'') )) as RAZON_SOC" + "		,RTRIM(LTRIM(ISNULL(tB.dCalleFiscal,''))) as CALLE" + "		,SUBSTRING( RTRIM(LTRIM(ISNULL(tB.dNoDomicilioFiscal,''))), 1, 20) as NO_EXT" + "		,SUBSTRING( RTRIM(LTRIM(ISNULL(tB.dNoInteriorDomicilioFiscal,''))), 1, 20) as NO_INT" + "		,RTRIM(LTRIM(ISNULL(tB.dColoniaFiscal,''))) as COLONIA" + "		,RTRIM(LTRIM(ISNULL(tB.cMunicipioActual,''))) as MUNICIPIO" + "		,RTRIM(LTRIM(ISNULL(catE.EDO_NOMBRE,''))) as ESTADO_BEN" + "		,RTRIM(LTRIM(ISNULL(tB.cMunicipioActual,''))) AS CIUDAD" + "		,'MEX' as PAIS" + "		,RTRIM(LTRIM(ISNULL(tB.dCodigoPostalFiscal,''))) as COD_POSTAL" + "		,CASE WHEN RTRIM(LTRIM(ISNULL(tB.dTelefonoFiscal,'37777000')))='' THEN '37777000' ELSE ISNULL(tB.dTelefonoFiscal,'37777000') END as TELEFONO" + // "
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																								// ,RTRIM(LTRIM(ISNULL(tB.dTelefonoFiscal,'NA')))
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																								// as
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																								// TELEFONO"
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																								// +
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																								// //URVP
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																								// SE
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																								// PONE
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																								// EL
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																								// TELEFONO
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																								// DE
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																								// CNF
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																								// EN
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																								// CASO
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																								// DE
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																								// IR
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																								// VACIO
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																								// DEBIDO
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																								// A
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																								// QUE
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																								// ES
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																								// REQUERIDO
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																								// DICHO
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																								// CAMPO
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																								// EN
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																								// SICOP
				"		,RTRIM(LTRIM(ISNULL(tB.dFaxFiscal,'NA'))) as FAX" + "		,RTRIM(LTRIM(ISNULL(tB.dTelefonoFiscal,'NA'))) as MOVIL" + "		,RTRIM(LTRIM(ISNULL(tB.dEMailFiscal,'NA'))) as MAIL" + "		,RTRIM(LTRIM(ISNULL(tB.dAPaternoApoderado,'NA'))) as APEPAT_CON" + "		,RTRIM(LTRIM(ISNULL(tB.dAMaternoApoderado,'NA'))) as APEMAT_CON" + "		,RTRIM(LTRIM(ISNULL(tB.dNombreApoderado,'NA'))) as NOMBRE_CON" + "		,CASE WHEN RTRIM(LTRIM(ISNULL(tB.dTelefonoApoderado,'37777000')))='' THEN '37777000' ELSE ISNULL(tB.dTelefonoApoderado,'37777000') END as TELEFONO_CON" + "		,RTRIM(LTRIM(ISNULL(tB.dFaxApoderado,'NA'))) as FAX_CON" + "		,CASE WHEN RTRIM(LTRIM(ISNULL(tB.dTelefonoApoderado,'37777000')))='' THEN '37777000' ELSE ISNULL(tB.dTelefonoApoderado,'37777000') END as MOVIL_CON" + "		,RTRIM(LTRIM(ISNULL(tB.dEMailApoderado,'NA'))) as MAIL_CON" + "		,'' as SUSCE" + "		,'' as TIPO" + "  FROM  tBeneficiario tB WITH (NOLOCK),pCatalogoTipoPersonaRFC tPer WITH (NOLOCK),CAT_ESTADOS catE WITH (NOLOCK)" + "  WHERE tB.CBEN in (" + listaCBEN + ") " + "	AND tPer.cIdTipoPersonaRFC = tB.cIdTipoPersonaRFC " + "	AND catE.ID_ESTADO = tB.cEstadoFiscal";
		try {
			pstmntD = conn.prepareStatement( SqlBen );
			// pstmntD.setString(1,listaCBEN);
			rs = pstmntD.executeQuery();

			archivoBen.append( "H\r\n" );
			while ( rs.next() ) {
				archivoBen.append( fechaEfectiva ).append( "," ).append( rs.getString( "ACTIVO" ) ) // ACTIVO
						.append( "," ).append( rs.getString( "ID_RAMO" ) ) // ID_RAMO
						.append( "," ).append( rs.getString( "CBEN" ) ) // CBEN
						.append( "," ).append( rs.getString( "TIPOPER" ) ) // TIPOPER
						.append( "," ).append( rs.getString( "TBEN" ) ) // TBEN
						.append( "," ).append( rs.getString( "RAMO_SIAFF" ) ) // RAM_SIAFF
						.append( "," ).append( rs.getString( "RFC" ) ) // RFC
						.append( "," ).append( rs.getString( "CURP" ) ) // CURP
						.append( "," ).append( rs.getString( "APEPAT" ) ) // APEPAT
						.append( "," ).append( rs.getString( "APEMAT" ) ) // APEMAT
						.append( "," ).append( rs.getString( "NOMBRE" ) ) // NOMBRE
						.append( "," ).append( rs.getString( "RAZON_SOC" ) ) // RAZON_SOC
						.append( "," ).append( rs.getString( "CALLE" ) ) // CALLE
						.append( "," ).append( rs.getString( "NO_EXT" ) ) // NO_EXT
						.append( "," ).append( rs.getString( "NO_INT" ) ) // NO_INT
						.append( "," ).append( rs.getString( "COLONIA" ) ) // COLONIA
						.append( "," ).append( rs.getString( "MUNICIPIO" ) ) // MUNICIPIO
						.append( "," ).append( rs.getString( "ESTADO_BEN" ) ) // ESTADO_BEN
						.append( "," ).append( rs.getString( "CIUDAD" ) ) // CIUDAD
						.append( "," ).append( rs.getString( "PAIS" ) ) // PAIS
						.append( "," ).append( rs.getString( "COD_POSTAL" ) ) // CP
						.append( "," ).append( rs.getString( "TELEFONO" ) ) // TELEFONO
						.append( "," ).append( rs.getString( "FAX" ) ) // FAX
						.append( "," ).append( rs.getString( "MOVIL" ) ) // MOVIL
						.append( "," ).append( rs.getString( "MAIL" ) ) // MAIL
						.append( "," ).append( rs.getString( "APEPAT_CON" ) ) // APEPAT_CON
						.append( "," ).append( rs.getString( "APEMAT_CON" ) ) // APEMAT_CON
						.append( "," ).append( rs.getString( "NOMBRE_CON" ) ) // NOMBRE_CON
						.append( "," ).append( rs.getString( "TELEFONO_CON" ) ) // TELEFONO_CON
						.append( "," ).append( rs.getString( "FAX_CON" ) ) // FAX_CON
						.append( "," ).append( rs.getString( "MOVIL_CON" ) ) // MOVIL_CON
						.append( "," ).append( rs.getString( "MAIL_CON" ) ) // MAIL_CON
						.append( "," ).append( rs.getString( "SUSCE" ) ) // SUSCE
						.append( "," ).append( rs.getString( "TIPO" ) ) // TIPO
						.append( "\r\n" );
			}
		} finally {

			CloseObject.closeObject( rs );
			CloseObject.closeObject( pstmntD );

		}
		return archivoBen;
	}

	//// Datos de layout para benrdoccom_:
	public static StringBuffer BuscaRDocCom( Connection conn, String listaCBEN ) throws Exception {
		StringBuffer archivoBenRDC = new StringBuffer();
		PreparedStatement pstmntRDC = null;
		ResultSet rsRDC = null;
		String SqlRDC = "SELECT * FROM  vLayoutRDC ben WHERE ben.CBEN in (" + listaCBEN + ")  ";

		try {
			pstmntRDC = conn.prepareStatement( SqlRDC );
			rsRDC = pstmntRDC.executeQuery();

			archivoBenRDC.append( "H\r\n" );
			while ( rsRDC.next() ) { // " + rsRDC.getString(1) + "
				String detalleR = fechaEfectiva + ",S," + rsRDC.getString( 2 ).trim() + ",CAP_FI_RHQ_R01," + rsRDC.getString( 3 ).trim() + "," + rsRDC.getString( 4 ).trim() + "," + rsRDC.getString( 5 ).trim() + "," + rsRDC.getString( 6 ).trim() + "\r\n" + fechaEfectiva + ",S," + rsRDC.getString( 2 ).trim() + ",CAP_FI_RHQ_R02," + rsRDC.getString( 3 ).trim() + "," + rsRDC.getString( 4 ).trim() + "," + rsRDC.getString( 5 ).trim() + "," + rsRDC.getString( 6 ).trim() + "\r\n" + fechaEfectiva + ",S," + rsRDC.getString( 2 ).trim() + ",CAP_FI_RHQ_R3," + rsRDC.getString( 3 ).trim() + "," + rsRDC.getString( 4 ).trim() + "," + rsRDC.getString( 5 ).trim() + "," + rsRDC.getString( 6 ).trim() + "\r\n" + fechaEfectiva + ",S," + rsRDC.getString( 2 ).trim() + ",CAP_FI_RHQ_04," + rsRDC.getString( 3 ).trim() + "," + rsRDC.getString( 4 ).trim() + "," + rsRDC.getString( 5 ).trim() + "," + rsRDC.getString( 6 ).trim() + "\r\n" + fechaEfectiva + ",S," + rsRDC.getString( 2 ).trim() + ",CAP_FI_RHQ_R05," + rsRDC.getString( 3 ).trim() + "," + rsRDC.getString( 4 ).trim() + "," + rsRDC.getString( 5 ).trim() + "," + rsRDC.getString( 6 ).trim() + "\r\n" + fechaEfectiva + ",S," + rsRDC.getString( 2 ).trim() + ",CAP_FI_RHQ_R06," + rsRDC.getString( 3 ).trim() + "," + rsRDC.getString( 4 ).trim() + "," + rsRDC.getString( 5 ).trim() + "," + rsRDC.getString( 6 ).trim() + "\r\n" + fechaEfectiva + ",S," + rsRDC.getString( 2 ).trim() + ",CAP_FI_RHQ_R07," + rsRDC.getString( 3 ).trim() + "," + rsRDC.getString( 4 ).trim() + "," + rsRDC.getString( 5 ).trim() + "," + rsRDC.getString( 6 ).trim() + "\r\n" + fechaEfectiva + ",S," + rsRDC.getString( 2 ).trim() + ",CAP_FI_RHQ_R08," + rsRDC.getString( 3 ).trim() + "," + rsRDC.getString( 4 ).trim() + "," + rsRDC.getString( 5 ).trim() + "," + rsRDC.getString( 6 ).trim() + "\r\n" + fechaEfectiva + ",S," + rsRDC.getString( 2 ).trim() + ",CAP_FI_RHQ_R09," + rsRDC.getString( 3 ).trim() + "," + rsRDC.getString( 4 ).trim() + "," + rsRDC.getString( 5 ).trim() + "," + rsRDC.getString( 6 ).trim() + "\r\n" + fechaEfectiva + ",S," + rsRDC.getString( 2 ).trim() + ",CAP_FI_RHQ_R10," + rsRDC.getString( 3 ).trim() + "," + rsRDC.getString( 4 ).trim() + "," + rsRDC.getString( 5 ).trim() + "," + rsRDC.getString( 6 ).trim() + "\r\n" + fechaEfectiva + ",S," + rsRDC.getString( 2 ).trim() + ",CAP_FI_RHQ_R11," + rsRDC.getString( 3 ).trim() + "," + rsRDC.getString( 4 ).trim() + "," + rsRDC.getString( 5 ).trim() + "," + rsRDC.getString( 6 ).trim() + "\r\n" + fechaEfectiva + ",S," + rsRDC.getString( 2 ).trim() + ",CAP_FI_RHQ_R12," + rsRDC.getString( 3 ).trim() + "," + rsRDC.getString( 4 ).trim() + "," + rsRDC.getString( 5 ).trim() + "," + rsRDC.getString( 6 ).trim() + "\r\n" + fechaEfectiva + ",S," + rsRDC.getString( 2 ).trim() + ",CAP_FI_RHQ_R13," + rsRDC.getString( 3 ).trim() + "," + rsRDC.getString( 4 ).trim() + "," + rsRDC.getString( 5 ).trim() + "," + rsRDC.getString( 6 ).trim() + "\r\n" + fechaEfectiva + ",S," + rsRDC.getString( 2 ).trim() + ",CAP_FI_RHQ_JAL," + rsRDC.getString( 3 ).trim() + "," + rsRDC.getString( 4 ).trim() + "," + rsRDC.getString( 5 ).trim() + "," + rsRDC.getString( 6 ).trim() + "\r\n" + fechaEfectiva + ",S," + rsRDC.getString( 2 ).trim() + ",CAP_FI_RHQ_R15," + rsRDC.getString( 3 ).trim() + "," + rsRDC.getString( 4 ).trim() + "," + rsRDC.getString( 5 ).trim() + "," + rsRDC.getString( 6 ).trim() + "\r\n" + fechaEfectiva + ",S," + rsRDC.getString( 2 ).trim() + ",CAP_FI_RHQ_R16," + rsRDC.getString( 3 ).trim() + "," + rsRDC.getString( 4 ).trim() + "," + rsRDC.getString( 5 ).trim() + "," + rsRDC.getString( 6 ).trim() + "\r\n" + fechaEfectiva + ",S," + rsRDC.getString( 2 ).trim() + ",CAP_FI_RHQ_R17," + rsRDC.getString( 3 ).trim() + "," + rsRDC.getString( 4 ).trim() + "," + rsRDC.getString( 5 ).trim() + "," + rsRDC.getString( 6 ).trim() + "\r\n" + fechaEfectiva + ",S," + rsRDC.getString( 2 ).trim() + ",CAP_FI_RHQ_R18," + rsRDC.getString( 3 ).trim() + "," + rsRDC.getString( 4 ).trim() + "," + rsRDC.getString( 5 ).trim() + "," + rsRDC.getString( 6 ).trim() + "\r\n" + fechaEfectiva + ",S," + rsRDC.getString( 2 ).trim() + ",CAP_FI_RHQ_R19," + rsRDC.getString( 3 ).trim() + "," + rsRDC.getString( 4 ).trim() + "," + rsRDC.getString( 5 ).trim() + "," + rsRDC.getString( 6 ).trim() + "\r\n" + fechaEfectiva + ",S," + rsRDC.getString( 2 ).trim() + ",CAP_FI_RHQ_R20," + rsRDC.getString( 3 ).trim() + "," + rsRDC.getString( 4 ).trim() + "," + rsRDC.getString( 5 ).trim() + "," + rsRDC.getString( 6 ).trim() + "\r\n" + fechaEfectiva + ",S," + rsRDC.getString( 2 ).trim() + ",CAP_FI_RHQ_R21," + rsRDC.getString( 3 ).trim() + "," + rsRDC.getString( 4 ).trim() + "," + rsRDC.getString( 5 ).trim() + "," + rsRDC.getString( 6 ).trim() + "\r\n" + fechaEfectiva + ",S," + rsRDC.getString( 2 ).trim() + ",CAP_FI_RHQ_R22," + rsRDC.getString( 3 ).trim() + "," + rsRDC.getString( 4 ).trim() + "," + rsRDC.getString( 5 ).trim() + "," + rsRDC.getString( 6 ).trim() + "\r\n" + fechaEfectiva + ",S," + rsRDC.getString( 2 ).trim() + ",CAP_FI_RHQ_R23," + rsRDC.getString( 3 ).trim() + "," + rsRDC.getString( 4 ).trim() + "," + rsRDC.getString( 5 ).trim() + "," + rsRDC.getString( 6 ).trim() + "\r\n" + fechaEfectiva + ",S," + rsRDC.getString( 2 ).trim() + ",CAP_FI_RHQ_R24," + rsRDC.getString( 3 ).trim() + "," + rsRDC.getString( 4 ).trim() + "," + rsRDC.getString( 5 ).trim() + "," + rsRDC.getString( 6 ).trim() + "\r\n" + fechaEfectiva + ",S," + rsRDC.getString( 2 ).trim() + ",CAP_FI_RHQ_R25," + rsRDC.getString( 3 ).trim() + "," + rsRDC.getString( 4 ).trim() + "," + rsRDC.getString( 5 ).trim() + "," + rsRDC.getString( 6 ).trim() + "\r\n" + fechaEfectiva + ",S," + rsRDC.getString( 2 ).trim() + ",CAP_FI_RHQ_R26," + rsRDC.getString( 3 ).trim() + "," + rsRDC.getString( 4 ).trim() + "," + rsRDC.getString( 5 ).trim() + "," + rsRDC.getString( 6 ).trim() + "\r\n" + fechaEfectiva + ",S," + rsRDC.getString( 2 ).trim() + ",CAP_FI_RHQ_R27," + rsRDC.getString( 3 ).trim() + "," + rsRDC.getString( 4 ).trim() + "," + rsRDC.getString( 5 ).trim() + "," + rsRDC.getString( 6 ).trim() + "\r\n" + fechaEfectiva + ",S," + rsRDC.getString( 2 ).trim() + ",CAP_FI_RHQ_R28," + rsRDC.getString( 3 ).trim() + "," + rsRDC.getString( 4 ).trim() + "," + rsRDC.getString( 5 ).trim() + "," + rsRDC.getString( 6 ).trim() + "\r\n" + fechaEfectiva + ",S," + rsRDC.getString( 2 ).trim() + ",CAP_FI_RHQ_R29," + rsRDC.getString( 3 ).trim() + "," + rsRDC.getString( 4 ).trim() + "," + rsRDC.getString( 5 ).trim() + "," + rsRDC.getString( 6 ).trim() + "\r\n" + fechaEfectiva + ",S," + rsRDC.getString( 2 ).trim() + ",CAP_FI_RHQ_R30," + rsRDC.getString( 3 ).trim() + "," + rsRDC.getString( 4 ).trim() + "," + rsRDC.getString( 5 ).trim() + "," + rsRDC.getString( 6 ).trim() + "\r\n" + fechaEfectiva + ",S," + rsRDC.getString( 2 ).trim() + ",CAP_FI_RHQ_R31," + rsRDC.getString( 3 ).trim() + "," + rsRDC.getString( 4 ).trim() + "," + rsRDC.getString( 5 ).trim() + "," + rsRDC.getString( 6 ).trim() + "\r\n" + fechaEfectiva + ",S," + rsRDC.getString( 2 ).trim() + ",CAP_FI_RHQ_R32," + rsRDC.getString( 3 ).trim() + "," + rsRDC.getString( 4 ).trim() + "," + rsRDC.getString( 5 ).trim() + "," + rsRDC.getString( 6 ).trim() + "\r\n" + fechaEfectiva + ",S," + rsRDC.getString( 2 ).trim() + ",CAP_FI_RHQ," + rsRDC.getString( 3 ).trim() + "," + rsRDC.getString( 4 ).trim() + "," + rsRDC.getString( 5 ).trim() + "," + rsRDC.getString( 6 ).trim();
				detalleR = detalleR + "\r\n";
				archivoBenRDC.append( detalleR );
			}
		} finally {
			CloseObject.closeObject( rsRDC );
			CloseObject.closeObject( pstmntRDC );
		}

		return archivoBenRDC;
	}

	public static void envioAlertas( Connection conn, String listaCBEN ) throws Exception {

		String[] cben = listaCBEN.split( "," );

		ResultSet rs = null;
		PreparedStatement ps = null;
		ResultSet rs2 = null;
		PreparedStatement ps2 = null;
		String dest = null;
		String body = null;
		String subject = "Registro en SICOP de Alta Proveedor";

		log.info( "Correo: " + subject );

		try {

			for ( int i = 0; i < cben.length; i++ ) {

				String queryRFC = "SELECT dRFC FROM tBeneficiario WITH (nolock)  WHERE CBEN in (" + cben[i] + ")";

				String queryDatosAlta = "SELECT cFolio, cIdRFC, " + "CASE WHEN crazonsocial = '' THEN capellidopaterno + ' ' + capellidomaterno + ' '+ cnombre ELSE crazonsocial END as RazonSocial " + " FROM tAltaProveedor WITH (nolock) WHERE REPLACE (cIdRFC,'-','') " + "IN (" + queryRFC + ") ";

				String queryCorreos = "SELECT U_EMAIL FROM CG_USUARIO WITH (nolock) WHERE U_LOGIN IN (SELECT cIdUsuarioCaptura FROM tAltaProveedor WITH (nolock) WHERE REPLACE (cIdRFC,'-','') IN (" + queryRFC + ") )";

				ps = conn.prepareStatement( queryCorreos );
				log.debug( ps );

				rs = ps.executeQuery();

				ps2 = conn.prepareStatement( queryDatosAlta );
				log.debug( ps2 );

				rs2 = ps2.executeQuery();
				while ( rs.next() && rs2.next() ) {
					body = "<B>Atencion</b></br>" + "Se notifica que fue incorporada la información solicitada en trámite con folio [" + rs2.getString( "cFolio" ) + " ] a nombre de " + rs2.getString( "RazonSocial" ) + " con RFC: " + rs2.getString( "cIdRFC" ) + " al catálogo de beneficiarios y cuentas bancarias de la CONAFOR.<br><br>" + "Por lo anterior es necesario que verifique que la información en el  sistema coincide antes de efectuar un trámite de pago a dicho beneficiario/proveedor, " + "en caso de que existiera alguna inconsistencia o cambio favor de realizar trámite de proceso de actualización de proveedor/beneficiario. <br><br>" + "Le recuerdo que es responsabilidad del área que solicita el pago la información contenida en la “solicitud de pago” emitida en el SAI, " + "incluyendo nombre y cuenta bancaria del beneficiario del pago.<br><br>" + "Gracias<br> ";

					dest = rs.getString( "U_EMAIL" );
				}
				try {
					AlarmaManager.procesaAlarmaCNF( conn, "", null, null, subject, dest, body );
				} catch ( Exception e ) {
					log.warn( e, e );
				}
			}

		} catch ( Exception e ) {
			log.warn( e, e );
		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
			CloseObject.closeObject( rs2 );
			CloseObject.closeObject( ps2 );
		}
	}

	private static Beneficiario mapToBeneficiario( ResultSet rs ) throws SQLException {
		Beneficiario beneficiario = new Beneficiario();

		beneficiario.setBeneficiario( rs.getInt( "cBeneficiario" ) );
		beneficiario.setNombre( rs.getString( "dNombre" ) );
		beneficiario.setApellidoPaterno( rs.getString( "dApellidoPaterno" ) );
		beneficiario.setApellidoMaterno( rs.getString( "dApellidoMaterno" ) );
		beneficiario.setRfc( rs.getString( "dRFC" ) );
		beneficiario.setCurp( rs.getString( "dCURP" ) );
		beneficiario.setIdTipoPersonaRFC( rs.getString( "cIdTipoPersonaRFC" ) );
		beneficiario.setCalleFiscal( rs.getString( "dCalleFiscal" ) );
		beneficiario.setNoDomicilioFiscal( rs.getString( "dNoDomicilioFiscal" ) );
		beneficiario.setNoInteriorDomicilioFiscal( rs.getString( "dNoInteriorDomicilioFiscal" ) );
		beneficiario.setOtrosDatosFiscal( rs.getString( "dOtrosDatosFiscal" ) );
		beneficiario.setColoniaFiscal( rs.getString( "dColoniaFiscal" ) );
		beneficiario.setCodigoPostalFiscal( rs.getString( "dCodigoPostalFiscal" ) );
		beneficiario.setMunicipioFiscal( rs.getLong( "cMunicipioFiscal" ) );
		beneficiario.setEstadoFiscal( rs.getLong( "cEstadoFiscal" ) );
		beneficiario.setTelefonoFiscal( rs.getString( "dTelefonoFiscal" ) );
		beneficiario.setFaxFiscal( rs.getString( "dFaxFiscal" ) );
		beneficiario.setEmailFiscal( rs.getString( "dEMailFiscal" ) );
		beneficiario.setCalleActual( rs.getString( "dCalleActual" ) );
		beneficiario.setNoDomicilioActual( rs.getString( "dNoDomicilioActual" ) );
		beneficiario.setNoInteriorDomicilioActual( rs.getString( "dNoInteriorDomicilioActual" ) );
		beneficiario.setOtrosDatosActual( rs.getString( "dOtrosDatosActual" ) );
		beneficiario.setColoniaActual( rs.getString( "dColoniaActual" ) );
		beneficiario.setCodigoPostalActual( rs.getString( "dCodigoPostalActual" ) );
		beneficiario.setMunicipioActual( rs.getLong( "cMunicipioActual" ) );
		beneficiario.setEstadoActual( rs.getLong( "cEstadoActual" ) );
		beneficiario.setTelefonoActual( rs.getString( "dTelefonoActual" ) );
		beneficiario.setFaxActual( rs.getString( "dFaxActual" ) );
		beneficiario.setEmailActual( rs.getString( "dEMailActual" ) );
		beneficiario.setApellidoPaternoApoderado( rs.getString( "dAPaternoApoderado" ) );
		beneficiario.setApellidoMaternoApoderado( rs.getString( "dAMaternoApoderado" ) );
		beneficiario.setNombreApoderado( rs.getString( "dNombreApoderado" ) );
		beneficiario.setTelefonoApoderado( rs.getString( "dTelefonoApoderado" ) );
		beneficiario.setFaxApoderado( rs.getString( "dFaxApoderado" ) );
		beneficiario.setEmailApoderado( rs.getString( "dEMailApoderado" ) );
		beneficiario.setNoOficioPoderLegal( rs.getString( "dNoOficioPoderLegal" ) );
		beneficiario.setCben( rs.getString( "CBEN" ) );
		beneficiario.setExtranjero( rs.getString( "cExtranjero" ) );
		beneficiario.setRfcValido( rs.getString( "cRFCValido" ) );
		beneficiario.setEnviadoSICOP( rs.getInt( "nEnviadoSICOP" ) );
		beneficiario.setFechaBeneficiario( rs.getTimestamp( "fBeneficiario" ) );
		beneficiario.setBeneficiarioStatus( rs.getString( "cBeneficiarioStatus" ) );
		beneficiario.setMunicipioFiscalSIF( rs.getString( "cMunicipioFiscalSIF" ) );
		beneficiario.setMunicipioActualSIF( rs.getString( "cMunicipioActualSIF" ) );
		beneficiario.setIdBancario( rs.getString( "cIdBancario" ) );
		beneficiario.setIdEmpleado( rs.getInt( "nIdEmpleado" ) );
		beneficiario.setAltaRapida( rs.getString( "alta_rapida" ) );
		beneficiario.setRescisionContrato( rs.getString( "rescisionContrato" ) );

		return beneficiario;
	}

	public static Beneficiario selectByRFC( Connection conn, String rfc ) throws SQLException {
		String query = "SELECT * FROM tBeneficiario WITH(NOLOCK) WHERE REPLACE(dRFC,'-','') = REPLACE(?,'-','')";
		Beneficiario beneficiario = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement( query );
			ps.setString( 1, rfc );
			log.trace( "Retrieving beneficiaries for RFC [" + rfc + "]" );
			rs = ps.executeQuery();
			if ( rs.next() ) {
				beneficiario = mapToBeneficiario( rs );
			}
			return beneficiario;
		} catch ( SQLException e ) {
			log.error( "Error retrieving beneficiaries for RFC [" + rfc + "]: " + e, e );
			throw e;
		}

	}

	public static int updateStatusBen( Connection conn, String listaCBEN ) throws SQLException {
		PreparedStatement pstmntB = null;
		PreparedStatement pstmntBCB = null;
		int retval;
		try {
			pstmntB = conn.prepareStatement( "UPDATE tBeneficiario SET nEnviadoSICOP = 1 WHERE CBEN in (" + listaCBEN + ")" );
			log.info( pstmntB );
			retval = pstmntB.executeUpdate();
			log.info( listaCBEN );
			if ( retval > 0 ) {
				String querySel = "SELECT dRFC FROM tBeneficiario WITH (nolock)  WHERE CBEN in (" + listaCBEN + ")";
				log.info( querySel );
				pstmntBCB = conn.prepareStatement( "UPDATE tBeneficiarioCuentasBancarias SET nBCBEnviadoSICOP = 1 WHERE dRFC IN (" + querySel + ")" );
				log.info( pstmntBCB );
				retval = pstmntBCB.executeUpdate();
			}
		} finally {
			CloseObject.closeObject( pstmntBCB );
			CloseObject.closeObject( pstmntB );

		}
		return retval;
	}

	public BeneficiarioManager( ) {
	}
}
