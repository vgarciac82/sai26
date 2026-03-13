package com.syc.sai.procesos;


import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.syc.cfdi.core.Factura;
import com.syc.cfdi.core.FacturaManager;
import com.syc.cfdi.db.CloseObject;
import com.syc.cfdi.utils.FacturaUtils;
import com.syc.cfdi.v3332.Comprobante.Comprobante;


public class ReinsertaRetencionesCFDI extends ProcesoSAI {

	private static final Logger log = LogManager.getLogger( ReinsertaRetencionesCFDI.class );
	
	private final ScalarHandler<Integer>	intScalarHandler	= new ScalarHandler<>();
	private final QueryRunner				runner				= new QueryRunner();

	public ReinsertaRetencionesCFDI( String urlConn, String driverName, String user, String pass ) {
		super( urlConn, driverName, user, pass );
	}

	public static void main( String[] args ) throws ClassNotFoundException, SQLException {
		ReinsertaRetencionesCFDI process = new ReinsertaRetencionesCFDI( args[0], args[1], args[2], args[3] );

		process.execute();
	}

	private void execute() throws ClassNotFoundException, SQLException {

		List<ExpedienteInfo> expedientes = selectExpedients();

		for ( ExpedienteInfo expedient : expedientes ) {
			Connection conn = null;
			try {
				conn = createConn();
				log.info("Procesando: " + expedient );
				if ( tramiteAplicado( conn, expedient.getTituloAplicacion(), expedient.getIdGabinete() ) )
					reinsertaRetenciones( conn, expedient );
				conn.commit();
			} catch ( Exception e ) {

				if ( conn != null )
					try {
						conn.rollback();
					} catch ( Exception e2 ) {
						System.out.println( "Problemas en rollback: " + e2 );
					}
				System.out.println( "No fue posible procesar las facturas de: " + expedient + " Causa: " + e.toString() );
			} finally {
				CloseObject.closeObject( conn );
			}
		}
	}

	private void reinsertaRetenciones( Connection connection, ExpedienteInfo expedient ) throws Exception {
		List<String> rutaArchivos = getArchivos( connection, expedient );
		int folioPago = getFolioPago( connection, expedient.getTituloAplicacion(), expedient.getIdGabinete() );
		FacturaManager.eliminaImpuestosFacturas( connection, expedient.getTituloAplicacion(), folioPago );
		FacturaManager.eliminaRetencionFacturas( connection, expedient.getTituloAplicacion(), folioPago );
		
		for ( String rutaArchivo : rutaArchivos ) {

			Comprobante comprobante = FacturaManager.cargaComprobante( new File( rutaArchivo ) );
			Factura facturaRetenImp = FacturaUtils.cargaCFDI( comprobante );
			FacturaManager.insertaRetencionesFactura( connection, expedient.getTituloAplicacion(), folioPago, facturaRetenImp );
			FacturaManager.insertaImpuestosFactura( connection, expedient.getTituloAplicacion(), folioPago, facturaRetenImp );

		}
	}

	private List<String> getArchivos( Connection connection, ExpedienteInfo expedient ) throws SQLException {

		StringBuilder query = new StringBuilder();
		query.append( " select v.UNIDAD_DISCO + v.RUTA_BASE + v.RUTA_DIRECTORIO + p.NOM_ARCHIVO_VOL as archivo " );
		query.append( " from " );
		query.append( " imx_carpeta c " );
		query.append( " inner join  " );
		query.append( " IMX_DOCUMENTO d  " );
		query.append( " on c.TITULO_APLICACION = d.TITULO_APLICACION " );
		query.append( " and c.ID_GABINETE = d.ID_GABINETE " );
		query.append( " and c.ID_CARPETA = d.ID_CARPETA_PADRE " );
		query.append( " inner join imx_pagina p " );
		query.append( " on d.TITULO_APLICACION = p.TITULO_APLICACION " );
		query.append( " and d.ID_GABINETE = p.ID_GABINETE " );
		query.append( " and d.ID_CARPETA_PADRE = p.ID_CARPETA_PADRE " );
		query.append( " and d.ID_DOCUMENTO = p.ID_DOCUMENTO " );
		query.append( " inner join IMX_VOLUMEN v " );
		query.append( " on  " );
		query.append( " p.VOLUMEN = v.VOLUMEN " );
		query.append( " where d.TITULO_APLICACION = '" + expedient.getTituloAplicacion() + "' " );
		query.append( " and d.ID_GABINETE = " + expedient.getIdGabinete() );
		query.append( " and c.NOMBRE_CARPETA = 'CFDI' " );
		query.append( " and NOMBRE_DOCUMENTO like '%.xml' " );

		List<String> rutaArchivo = runner.query( connection, query.toString(), new ColumnListHandler<String>( 1 ) );

		return rutaArchivo;
	}

	private boolean tramiteAplicado( Connection connection, String tituloAplicacion, int idGabinete ) throws SQLException {
		int folioPago = getFolioPago( connection, tituloAplicacion, idGabinete );
		StringBuilder query = new StringBuilder( "SELECT COUNT(*) FROM t" + tituloAplicacion + "Encabezado WHERE nFolio" + tituloAplicacion + " = " + folioPago );
		int count = runner.query( connection, query.toString(), intScalarHandler );
		return count > 0;
	}

	private int getFolioPago( Connection connection, String tituloAplicacion, int idGabinete ) throws SQLException {
		StringBuilder query = new StringBuilder( "SELECT CONVERT( INT, SUBSTRING(folio,10,10) ) FROM imx" + tituloAplicacion + " WHERE id_gabinete = " + idGabinete );
		Integer folio = runner.query( connection, query.toString(), intScalarHandler );
		return folio;
	}

	private List<ExpedienteInfo> selectExpedients() throws ClassNotFoundException, SQLException {

		Connection connection = null;
		try {
			connection = createConn();
			BeanListHandler<ExpedienteInfo> beanListHandler = new BeanListHandler<>( ExpedienteInfo.class );
			List<ExpedienteInfo> expedientInfoList = runner.query( connection, "select DISTINCT titulo_aplicacion AS tituloAplicacion, id_gabinete AS idGabinete from imx_carpeta where NOMBRE_CARPETA = 'CFDI' order by TITULO_APLICACION, ID_GABINETE", beanListHandler );
			return expedientInfoList;
		} finally {
			CloseObject.closeObject( connection );
		}

	}

}
