package com.syc.sai.contabilidad.polizamanual.model;


import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.syc.sai.contabilidad.polizamanual.DocPolizaEncabezado;
import com.syc.sai.contabilidad.polizamanual.DocPolizaEncabezadoEngineException;
import com.syc.sai.contabilidad.utils.db.CloseObject;

import net.sf.jasperreports.engine.JasperRunManager;


public class DocPolizaEncabezadoManager {

	private static Logger log = Logger.getLogger( DocPolizaEncabezadoManager.class );

	public static List<DocPolizaEncabezado> readAllDocPolizaEncabezado( Connection conn ) throws DocPolizaEncabezadoEngineException {
		return readDocPolizaEncabezadoBy( conn, null );
	}

	public static List<DocPolizaEncabezado> readDocPolizaEncabezadoBy( Connection conn, String restrictions ) throws DocPolizaEncabezadoEngineException {
		PreparedStatement pStatement = null;
		ResultSet rs = null;
		List<DocPolizaEncabezado> l = new ArrayList<DocPolizaEncabezado>();
		try {
			String qry = "";
			if ( restrictions != null && !restrictions.equals( "" ) ) {
				qry = "select * from tDocPolizaEncabezado with(nolock) where " + restrictions + ";";
			} else {
				qry = "select * from tDocPolizaEncabezado with(nolock) ;";
			}

			pStatement = conn.prepareStatement( qry );
			int cnt = 1;
			rs = pStatement.executeQuery();

			while ( rs.next() ) {
				l.add( extraeDocPolizaEncabezado( rs ) );
			}
			return l;
		} catch ( Exception e ) {
			throw new DocPolizaEncabezadoEngineException( e );
		} finally {
			if ( pStatement != null )
				try {
					pStatement.close();
				} catch ( Exception e2 ) {
					log.warn( "Problemas cerrando PreparedStatement " + e2.toString() );
				}
			if ( rs != null )
				try {
					rs.close();
				} catch ( Exception e2 ) {
					log.warn( "Problemas cerrando ResultSet " + e2.toString() );
				}
		}
	}

	public static int saveDocPolizaEncabezado( Connection conn, DocPolizaEncabezado docPolizaEncabezado ) throws DocPolizaEncabezadoEngineException {
		PreparedStatement pStatement = null;
		ResultSet rs = null;
		try {
			String qry = "INSERT INTO tDocPolizaEncabezado (fcarga, faplicacion, cramo, cunidadResponsable, cdocumentoHaplicado, nfolioPoliza, nmes, crevisado, cunidadResponsableContable, nfolioPolizaCancelacion, fcancelacion, cdescripcionPoliza, cconcepto, cidUsuarioCaptura, cidUsuarioRevision, cidUsuarioAprobacion, cidOrigen, mtotalCargos, mtotalAbonos, ctipoDocumento, ccomentarios, ncambio, nidCasoOrigen, periodo13, adefas, ntipoAjuste, aejercicioFiscal, ccentroContable, nfolioDocPoliza, ctipoPoliza, nFormatoPoliza) values( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? );";
			pStatement = conn.prepareStatement( qry );
			int cnt = 1;
			pStatement.setString( cnt++, docPolizaEncabezado.getFcarga() );
			pStatement.setString( cnt++, docPolizaEncabezado.getFaplicacion() );
			pStatement.setString( cnt++, docPolizaEncabezado.getCramo() );
			pStatement.setString( cnt++, docPolizaEncabezado.getCunidadResponsable() );
			pStatement.setString( cnt++, docPolizaEncabezado.getCdocumentoHaplicado() );
			pStatement.setInt( cnt++, docPolizaEncabezado.getNfolioPoliza() );
			pStatement.setShort( cnt++, docPolizaEncabezado.getNmes() );
			pStatement.setString( cnt++, docPolizaEncabezado.getCrevisado() );
			pStatement.setString( cnt++, docPolizaEncabezado.getCunidadResponsableContable() );
			pStatement.setInt( cnt++, docPolizaEncabezado.getNfolioPolizaCancelacion() );
			pStatement.setString( cnt++, docPolizaEncabezado.getFcancelacion() );
			pStatement.setString( cnt++, docPolizaEncabezado.getCdescripcionPoliza() );
			pStatement.setString( cnt++, docPolizaEncabezado.getCconcepto() );
			pStatement.setString( cnt++, docPolizaEncabezado.getCidUsuarioCaptura() );
			pStatement.setString( cnt++, docPolizaEncabezado.getCidUsuarioRevision() );
			pStatement.setString( cnt++, docPolizaEncabezado.getCidUsuarioAprobacion() );
			pStatement.setString( cnt++, docPolizaEncabezado.getCidOrigen() );
			pStatement.setDouble( cnt++, docPolizaEncabezado.getMtotalCargos() );
			pStatement.setDouble( cnt++, docPolizaEncabezado.getMtotalAbonos() );
			pStatement.setString( cnt++, docPolizaEncabezado.getCtipoDocumento() );
			pStatement.setString( cnt++, docPolizaEncabezado.getCcomentarios() );
			pStatement.setInt( cnt++, docPolizaEncabezado.getNcambio() );
			pStatement.setBigDecimal( cnt++, docPolizaEncabezado.getNidCasoOrigen() );
			pStatement.setString( cnt++, docPolizaEncabezado.getPeriodo13() );
			pStatement.setString( cnt++, docPolizaEncabezado.getAdefas() );
			pStatement.setInt( cnt++, docPolizaEncabezado.getNtipoAjuste() );
			pStatement.setString( cnt++, docPolizaEncabezado.getAejercicioFiscal() );
			pStatement.setString( cnt++, docPolizaEncabezado.getCcentroContable() );
			pStatement.setInt( cnt++, docPolizaEncabezado.getNfolioDocPoliza() );
			pStatement.setString( cnt++, docPolizaEncabezado.getCtipoPoliza() );
			pStatement.setInt( cnt++, docPolizaEncabezado.getnFormatoPoliza() );

			int nRows = pStatement.executeUpdate();
			conn.commit();
			return nRows;
		} catch ( Exception e ) {
			throw new DocPolizaEncabezadoEngineException( e );
		} finally {
			try {
				conn.close();
			} catch ( Exception e ) {
				e.printStackTrace();
			}
			if ( pStatement != null )
				try {
					pStatement.close();
				} catch ( Exception e2 ) {
					log.warn( "Problemas cerrando PreparedStatement " + e2.toString() );
				}
			if ( rs != null )
				try {
					rs.close();
				} catch ( Exception e2 ) {
					log.warn( "Problemas cerrando ResultSet " + e2.toString() );
				}
		}
	}

	public static int updateDocPolizaEncabezado( Connection conn, DocPolizaEncabezado docPolizaEncabezado ) throws DocPolizaEncabezadoEngineException {
		PreparedStatement pStatement = null;
		ResultSet rs = null;
		try {
			String qry = "UPDATE tDocPolizaEncabezado SET fcarga=?, faplicacion=?, cramo=?, cunidadResponsable=?, cdocumentoHaplicado=?, nfolioPoliza=?, nmes=?, crevisado=?, cunidadResponsableContable=?, nfolioPolizaCancelacion=?, fcancelacion=?, cdescripcionPoliza=?, cconcepto=?, cidUsuarioCaptura=?, cidUsuarioRevision=?, cidUsuarioAprobacion=?, cidOrigen=?, mtotalCargos=?, mtotalAbonos=?, ctipoDocumento=?, ccomentarios=?, ncambio=?, nidCasoOrigen=?, periodo13=?, adefas=?, ntipoAjuste=?,nFormatoPoliza=? where aejercicioFiscal=?, ccentroContable=?, nfolioDocPoliza=?, ctipoPoliza=?;";
			pStatement = conn.prepareStatement( qry );
			int cnt = 1;
			pStatement.setString( cnt++, docPolizaEncabezado.getFcarga() );
			pStatement.setString( cnt++, docPolizaEncabezado.getFaplicacion() );
			pStatement.setString( cnt++, docPolizaEncabezado.getCramo() );
			pStatement.setString( cnt++, docPolizaEncabezado.getCunidadResponsable() );
			pStatement.setString( cnt++, docPolizaEncabezado.getCdocumentoHaplicado() );
			pStatement.setInt( cnt++, docPolizaEncabezado.getNfolioPoliza() );
			pStatement.setShort( cnt++, docPolizaEncabezado.getNmes() );
			pStatement.setString( cnt++, docPolizaEncabezado.getCrevisado() );
			pStatement.setString( cnt++, docPolizaEncabezado.getCunidadResponsableContable() );
			pStatement.setInt( cnt++, docPolizaEncabezado.getNfolioPolizaCancelacion() );
			pStatement.setString( cnt++, docPolizaEncabezado.getFcancelacion() );
			pStatement.setString( cnt++, docPolizaEncabezado.getCdescripcionPoliza() );
			pStatement.setString( cnt++, docPolizaEncabezado.getCconcepto() );
			pStatement.setString( cnt++, docPolizaEncabezado.getCidUsuarioCaptura() );
			pStatement.setString( cnt++, docPolizaEncabezado.getCidUsuarioRevision() );
			pStatement.setString( cnt++, docPolizaEncabezado.getCidUsuarioAprobacion() );
			pStatement.setString( cnt++, docPolizaEncabezado.getCidOrigen() );
			pStatement.setDouble( cnt++, docPolizaEncabezado.getMtotalCargos() );
			pStatement.setDouble( cnt++, docPolizaEncabezado.getMtotalAbonos() );
			pStatement.setString( cnt++, docPolizaEncabezado.getCtipoDocumento() );
			pStatement.setString( cnt++, docPolizaEncabezado.getCcomentarios() );
			pStatement.setInt( cnt++, docPolizaEncabezado.getNcambio() );
			pStatement.setBigDecimal( cnt++, docPolizaEncabezado.getNidCasoOrigen() );
			pStatement.setString( cnt++, docPolizaEncabezado.getPeriodo13() );
			pStatement.setString( cnt++, docPolizaEncabezado.getAdefas() );
			pStatement.setInt( cnt++, docPolizaEncabezado.getNtipoAjuste() );
			pStatement.setInt( cnt++, docPolizaEncabezado.getnFormatoPoliza() );

			pStatement.setString( cnt++, docPolizaEncabezado.getAejercicioFiscal() );
			pStatement.setString( cnt++, docPolizaEncabezado.getCcentroContable() );
			pStatement.setInt( cnt++, docPolizaEncabezado.getNfolioDocPoliza() );
			pStatement.setString( cnt++, docPolizaEncabezado.getCtipoPoliza() );

			int nRows = pStatement.executeUpdate();
			conn.commit();
			return nRows;
		} catch ( Exception e ) {
			throw new DocPolizaEncabezadoEngineException( e );
		} finally {
			try {
				conn.close();
			} catch ( Exception e ) {
				e.printStackTrace();
			}
			if ( pStatement != null )
				try {
					pStatement.close();
				} catch ( Exception e2 ) {
					log.warn( "Problemas cerrando PreparedStatement " + e2.toString() );
				}
			if ( rs != null )
				try {
					rs.close();
				} catch ( Exception e2 ) {
					log.warn( "Problemas cerrando ResultSet " + e2.toString() );
				}
		}
	}

	public static int saveOrUpdateDocPolizaEncabezado( Connection conn, DocPolizaEncabezado docPolizaEncabezado ) throws DocPolizaEncabezadoEngineException {
		String restrictions = "nFolioDocPoliza = " + docPolizaEncabezado.getNfolioDocPoliza();
		restrictions += " AND cCentroContable = " + docPolizaEncabezado.getCcentroContable();
		restrictions += " AND cTipoPoliza = " + docPolizaEncabezado.getCtipoPoliza();
		restrictions += " AND aEjercicioFiscal = " + docPolizaEncabezado.getAejercicioFiscal();
		if ( readDocPolizaEncabezadoBy( conn, restrictions ).isEmpty() ) {
			return saveDocPolizaEncabezado( conn, docPolizaEncabezado );
		} else {
			return updateDocPolizaEncabezado( conn, docPolizaEncabezado );
		}
	}

	private static DocPolizaEncabezado extraeDocPolizaEncabezado( ResultSet rs ) throws DocPolizaEncabezadoEngineException {
		try {
			DocPolizaEncabezado pojo = new DocPolizaEncabezado();
			pojo.setFcarga( rs.getString( "fcarga" ) );
			pojo.setFaplicacion( rs.getString( "faplicacion" ) );
			pojo.setCramo( rs.getString( "cramo" ) );
			pojo.setCunidadResponsable( rs.getString( "cunidadResponsable" ) );
			pojo.setCdocumentoHaplicado( rs.getString( "cdocumentoHaplicado" ) );
			pojo.setNfolioPoliza( rs.getInt( "nfolioPoliza" ) );
			pojo.setNmes( rs.getShort( "nmes" ) );
			pojo.setCrevisado( rs.getString( "crevisado" ) );
			pojo.setCunidadResponsableContable( rs.getString( "cunidadResponsableContable" ) );
			pojo.setNfolioPolizaCancelacion( rs.getInt( "nfolioPolizaCancelacion" ) );
			pojo.setFcancelacion( rs.getString( "fcancelacion" ) );
			pojo.setCdescripcionPoliza( rs.getString( "cdescripcionPoliza" ) );
			pojo.setCconcepto( rs.getString( "cconcepto" ) );
			pojo.setCidUsuarioCaptura( rs.getString( "cidUsuarioCaptura" ) );
			pojo.setCidUsuarioRevision( rs.getString( "cidUsuarioRevision" ) );
			pojo.setCidUsuarioAprobacion( rs.getString( "cidUsuarioAprobacion" ) );
			pojo.setCidOrigen( rs.getString( "cidOrigen" ) );
			pojo.setMtotalCargos( rs.getDouble( "mtotalCargos" ) );
			pojo.setMtotalAbonos( rs.getDouble( "mtotalAbonos" ) );
			pojo.setCtipoDocumento( rs.getString( "ctipoDocumento" ) );
			pojo.setCcomentarios( rs.getString( "ccomentarios" ) );
			pojo.setNcambio( rs.getInt( "ncambio" ) );
			pojo.setNidCasoOrigen( rs.getBigDecimal( "nidCasoOrigen" ) );
			pojo.setPeriodo13( rs.getString( "periodo13" ) );
			pojo.setAdefas( rs.getString( "adefas" ) );
			pojo.setNtipoAjuste( rs.getInt( "ntipoAjuste" ) );
			pojo.setAejercicioFiscal( rs.getString( "aejercicioFiscal" ) );
			pojo.setCcentroContable( rs.getString( "ccentroContable" ) );
			pojo.setNfolioDocPoliza( rs.getInt( "nfolioDocPoliza" ) );
			pojo.setCtipoPoliza( rs.getString( "ctipoPoliza" ) );
			pojo.setnFormatoPoliza( rs.getInt( "nFormatoPoliza" ) );
			return pojo;
		} catch ( Exception e ) {
			throw new DocPolizaEncabezadoEngineException( e );
		}
	}

	
	public static int readnMesAbierto(Connection conn, String cCentroContable, String cUR) throws DocPolizaEncabezadoEngineException {

		PreparedStatement pStatement = null, ps = null;
		ResultSet rs = null, rs2 = null;

		try {
			
			String query = "SELECT aEjercicioFiscal FROM tEjercicioFiscal WHERE cActivo = 1";
			ps = conn.prepareStatement(query);			
			rs2 = ps.executeQuery();			
			String qry = null;
			int aEjercicioFiscal = 0;
			
			if (rs2.next()) {
				aEjercicioFiscal = rs2.getInt("aEjercicioFiscal");
			}

			if(aEjercicioFiscal > 2021)
				qry ="select nMes from tMesesContables WITH (NOLOCK) where mesAbierto='S' and cCentroContable='" + cCentroContable + "' AND cUnidadResponsable='" + cUR + "'";
			else
				qry ="select nMes from tMesesContables WITH (NOLOCK) where mesAbierto='S' and cCentroContable='" + cCentroContable + "'";
			
			int nmes=0;
			
			pStatement = conn.prepareStatement(qry);			
			rs = pStatement.executeQuery();

			while ( rs.next() ) {
				return rs.getInt( "nMes" );
			}

			return nmes;

		} catch ( Exception e ) {
			throw new DocPolizaEncabezadoEngineException( e );
		} finally {
			if ( pStatement != null )
				try {
					pStatement.close();
				} catch ( Exception e2 ) {
					log.warn( "Problemas cerrando PreparedStatement " + e2.toString() );
				}
			if ( rs != null )
				try {
					rs.close();
				} catch ( Exception e2 ) {
					log.warn( "Problemas cerrando ResultSet " + e2.toString() );
				}
		}
	}

	public static void ImprimirPoliza( Connection conn, HttpServletResponse resp, String tipoReporte, String ruta, Map<String, Object> parms ) {

		FileInputStream in = null;
		ServletOutputStream out = null;

		try {
			out = resp.getOutputStream();
			in = new FileInputStream( tipoReporte );
			JasperRunManager.runReportToPdfStream( in, out, parms, conn );
			resp.setContentType( "application/pdf" );
			out.flush();
			out.close();
		} catch ( Exception exc ) {
			exc.printStackTrace();
		} finally {
			try {
				if ( in != null )
					in.close();

				if ( out != null )
					out.close();
			} catch ( Exception exc ) {
				exc.printStackTrace();
			}

			in = null;
			out = null;
		}

	}

	/**
	 * Devuelve si la poliza actual es firmado por FIEL o Autografa
	 * 
	 * @param conn
	 *            COnexion activa a la DB
	 * @param nfolioD
	 *            Folio de poliza
	 * @return true si y solo si la poliza fue firmada con FIEL
	 * @throws Exception
	 */
	public static boolean esAutorizacionFirmaElectronica( Connection conn, String nfolioD ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuilder query = new StringBuilder();
		query.append( "SELECT ISNULL( cEsFirmaElectronica, 'N') AS cEsFirmaElectronica " );
		query.append( " FROM tDocPolizaEncabezado WITH(NOLOCK) " );
		query.append( " WHERE nFolioDocPoliza = ?" );
		try {
			ps = conn.prepareStatement( query.toString() );
			ps.setInt( 1, Integer.parseInt( nfolioD ) );

			rs = ps.executeQuery();

			return rs.next() && "S".equals( rs.getString( "cEsFirmaElectronica" ) );

		} finally {
			CloseObject.closeObject( rs );
			CloseObject.closeObject( ps );

		}
	}
}
