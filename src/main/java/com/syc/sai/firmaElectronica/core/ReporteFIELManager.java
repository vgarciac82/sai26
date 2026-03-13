package com.syc.sai.firmaElectronica.core;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.axtel.contratos.ProcesoEnteraSatisfaccionBusinessLogic;
import com.syc.fortimax.core.Documento;
import com.syc.fortimax.core.DocumentoManager;
import com.syc.obrapublica.core.EstimacionObraFIEL;
import com.syc.sai.contabilidad.utils.db.CloseObject;


public class ReporteFIELManager {

	public static String getPathReporte( Connection conn, int folioReporte ) throws Exception {
		StringBuilder query = new StringBuilder();
		query.append( "SELECT	cRutaArchivo " );
		query.append( "  FROM	tEdoFinancieroFirmaElectronica " );
		query.append( " WHERE	nIDEdoFinanciero = ?" );

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement( query.toString() );
			ps.setInt( 1, folioReporte );
			rs = ps.executeQuery();
			if ( rs.next() ) {
				return rs.getString( 1 );
			} else
				throw new Exception( "No se encontro reporte con folio: " + folioReporte );

		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}
	}

	public static String getPathAcuse( Connection conn, int folioReporte ) throws Exception {
		StringBuilder query = new StringBuilder();
		query.append( "SELECT	cRutaAcuse " );
		query.append( "  FROM	tEdoFinancieroFirmaElectronica " );
		query.append( " WHERE	nIDEdoFinanciero = ?" );

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement( query.toString() );
			ps.setInt( 1, folioReporte );
			rs = ps.executeQuery();
			if ( rs.next() ) {
				return rs.getString( 1 );
			} else
				throw new Exception( "No se encontro reporte con folio: " + folioReporte );

		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}
	}

	public static String getPathConciliacion( Connection conn, int folioReporte ) throws Exception {
		StringBuilder query = new StringBuilder();
		query.append( "SELECT	cRutaArchivo " );
		query.append( "  FROM	tConciliacionFirmaElectronica WITH (NOLOCK)  " );
		query.append( " WHERE	nConciliacion = ? and cEstatus = 'E'" );

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement( query.toString() );
			ps.setInt( 1, folioReporte );
			rs = ps.executeQuery();
			if ( rs.next() ) {
				return rs.getString( 1 );
			} else
				throw new Exception( "No se encontro reporte con folio: " + folioReporte );

		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}
	}

	public static String getPathAcuseConciliacion( Connection conn, int folioReporte ) throws Exception {
		StringBuilder query = new StringBuilder();
		query.append( "SELECT	cRutaAcuse " );
		query.append( "  FROM	tConciliacionFirmaElectronica WITH (NOLOCK)  " );
		query.append( " WHERE	nConciliacion = ? and cEstatus = 'E'" );

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement( query.toString() );
			ps.setInt( 1, folioReporte );
			rs = ps.executeQuery();
			
			if ( rs.next() ) {
				return rs.getString( 1 );
			} else
				throw new Exception( "No se encontro reporte con folio: " + folioReporte );

		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );
		}
	}

	public static Documento getPathNotaRM( Connection conn, RecepcionMaterialFIEL drf, int idGabinete ) throws SQLException {
		String nombreDocumento = "Atenta Nota " + drf.getRecepcionMaterial().getcIdRecepcionMat();
		Documento d = DocumentoManager.buscaDocumento( conn, drf.getDocument(), idGabinete, nombreDocumento );
		return d;
	}
	public static Documento getPathNotaEstimacion( Connection conn, EstimacionObraFIEL estFiel, int idGabinete ) throws SQLException {
		String nombreDocumento="Atenta Nota ".concat( (estFiel.getEstimacionObra().getnEstimacion()==0)?"Anticipo 0":"Estimacion "+ String.valueOf( estFiel.getEstimacionObra().getnEstimacion()) );
		Documento d = DocumentoManager.buscaDocumento( conn, estFiel.getDocument(), idGabinete, nombreDocumento );
		return d;
	}
	public static Documento getPathCoctosENSA( Connection conn, ProcesoEnteraSatisfaccionBusinessLogic ensa, int idGabinete ) throws SQLException {
		Documento d = DocumentoManager.buscaDocumento( conn, ensa.getDocument(), idGabinete, ensa.getDocName() );
		return d;
	}

}
