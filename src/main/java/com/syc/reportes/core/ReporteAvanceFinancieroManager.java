package com.syc.reportes.core;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.syc.egresos.firmante.servlet.Firmante;
import com.syc.gestion.reportes.ConciliacionFirma;
import com.syc.gestion.reportes.EstadosFinancierosFirma;
import com.syc.gestion.reportes.FirmaElectronicaReporte;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;


@SuppressWarnings( "unused" )
public class ReporteAvanceFinancieroManager {

	public static String ReporteAvanceFinancieroManager( Connection conn, int mesIni, int anio, Map<String, String> plantillas ) throws Exception {

		CallableStatement cs = null;
		ResultSet rs = null;

		String query = "{CALL dbo.sp_Avance_Financiero ( ?, ? )}";

		String fileName = "";

		try {

			cs = conn.prepareCall( query );
			cs.setInt( 1, anio );
			cs.setInt( 2, mesIni );
			rs = cs.executeQuery();

			if ( anio == 2015 )
				fileName = generaReporte2015( rs, plantillas.get( "AvFin2015" ), mesIni, anio );
			else // if(anio == 2016)
				fileName = generaReporte2016( rs, plantillas.get( "AvFin2016" ), mesIni, anio );
			return fileName;
		} finally {
			CloseObject.closeObject( rs, false );

			CloseObject.closeObject( cs, false );
		}

	}

	private static String generaReporte2015( ResultSet rs, String plantillaPath, int mesIni, int anio ) throws Exception {
		File cFileExcelPlantilla = new File( plantillaPath );
		String file_name = System.getProperty( "java.io.tmpdir" ) + File.separatorChar + "ReporteAvanceFinanciero" + "_" + System.currentTimeMillis() + "_" + String.valueOf( ( int ) ( Math.random() * 100 ) ) + ".xls";

		InputStream fs = new FileInputStream( cFileExcelPlantilla );
		Util.copiaArchivo( fs, file_name );
		fs.close();

		InputStream fsArchivo = new FileInputStream( cFileExcelPlantilla );
		Workbook workbook = new HSSFWorkbook( fsArchivo );
		fsArchivo.close();

		Sheet sheet0 = workbook.getSheetAt( 0 );
		int renglonInicio = 12;
		int renglonFin = 90;
		int columnInicio = 5;
		int columnFin = 14;
		int columnaLetra = 2;
		int columnaNumero = 4;

		String Encabezado = "AVANCE FINANCIERO " + Util.NOMBRE_MESES_MX[mesIni - 1] + " " + anio;
		String MesTrabajo = Util.NOMBRE_MESES_MX[mesIni - 1] + " 1/";

		Row rwEnc1 = ( sheet0.getRow( 0 ) == null ? sheet0.createRow( 0 ) : sheet0.getRow( 0 ) );
		Cell cell1 = ( rwEnc1.getCell( 0 ) == null ? rwEnc1.createCell( 0 ) : rwEnc1.getCell( 0 ) );
		cell1.setCellValue( Encabezado );

		Row rwEnc2 = ( sheet0.getRow( 4 ) == null ? sheet0.createRow( 4 ) : sheet0.getRow( 4 ) );
		Cell cell2 = ( rwEnc2.getCell( 9 ) == null ? rwEnc2.createCell( 9 ) : rwEnc2.getCell( 9 ) );
		cell2.setCellValue( MesTrabajo );

		while ( rs.next() ) {
			String programa = rs.getString( "P" );
			String letra = programa.substring( 0, 1 );
			int numero = Integer.parseInt( programa.substring( 1, 4 ) );

			int renglon = Util.buscaPrimerCoincidencia( sheet0, renglonInicio, columnaLetra, letra );
			int renglonrs = Util.buscaNumero( sheet0, renglon + 1, columnaNumero, numero );
			sheet0 = Util.resultSetToExcelRow( rs, sheet0, renglonrs, 5, 1, false );
		}

		sheet0 = Util.EvaluaFormula( workbook, sheet0, renglonInicio - 1, renglonFin, columnInicio, columnFin );

		File fsalida = new File( file_name );

		FileOutputStream fos = new FileOutputStream( fsalida );
		BufferedOutputStream bos = new BufferedOutputStream( fos, 1024 );
		workbook.write( bos );

		/* Cierra Flujos */
		bos.flush();
		bos.close();
		fos.close();
		return file_name;
	}

	private static String generaReporte2016( ResultSet rs, String plantillaPath, int mesIni, int anio ) throws Exception {
		File cFileExcelPlantilla = new File( plantillaPath );
		String file_name =System.getProperty( "java.io.tmpdir" ) + File.separatorChar + "ReporteAvanceFinanciero" + "_" + System.currentTimeMillis() + "_" + String.valueOf( ( int ) ( Math.random() * 100 ) ) + ".xls";

		InputStream fs = new FileInputStream( cFileExcelPlantilla );
		Util.copiaArchivo( fs, file_name );
		fs.close();

		InputStream fsArchivo = new FileInputStream( cFileExcelPlantilla );
		Workbook workbook = new HSSFWorkbook( fsArchivo );
		fsArchivo.close();

		Sheet sheet0 = workbook.getSheetAt( 0 );
		int renglonInicio = 12;
		int renglonFin = 90;
		int columnInicio = 5;
		int columnFin = 14;
		int columnaLetra = 2;
		int columnaNumero = 4;

		String Encabezado = "AVANCE FINANCIERO " + Util.NOMBRE_MESES_MX[mesIni - 1] + " " + anio;
		String MesTrabajo = Util.NOMBRE_MESES_MX[mesIni - 1] + " 1/";

		Row rwEnc1 = ( sheet0.getRow( 0 ) == null ? sheet0.createRow( 0 ) : sheet0.getRow( 0 ) );
		Cell cell1 = ( rwEnc1.getCell( 0 ) == null ? rwEnc1.createCell( 0 ) : rwEnc1.getCell( 0 ) );
		cell1.setCellValue( Encabezado );

		Row rwEnc2 = ( sheet0.getRow( 4 ) == null ? sheet0.createRow( 4 ) : sheet0.getRow( 4 ) );
		Cell cell2 = ( rwEnc2.getCell( 9 ) == null ? rwEnc2.createCell( 9 ) : rwEnc2.getCell( 9 ) );
		cell2.setCellValue( MesTrabajo );

		while ( rs.next() ) {
			String programa = rs.getString( "P" );
			String letra = programa.substring( 0, 1 );
			int numero = Integer.parseInt( programa.substring( 1, 4 ) );

			int renglon = Util.buscaPrimerCoincidencia( sheet0, renglonInicio, columnaLetra, letra );
			int renglonrs = Util.buscaNumero( sheet0, renglon + 1, columnaNumero, numero );
			sheet0 = Util.resultSetToExcelRow( rs, sheet0, renglonrs, 5, 1, false );

		}

		sheet0 = Util.EvaluaFormula( workbook, sheet0, renglonInicio - 1, renglonFin, columnInicio, columnFin );

		File fsalida = new File( file_name );

		FileOutputStream fos = new FileOutputStream( fsalida );
		BufferedOutputStream bos = new BufferedOutputStream( fos, 1024 );
		workbook.write( bos );

		/* Cierra Flujos */
		bos.flush();
		bos.close();
		fos.close();
		return file_name;
	}

	public static int insertaEdoFinancieroFIEL( Connection conn, EstadosFinancierosFirma reporte ) throws Exception {
		PreparedStatement ps = null;
		String generatedColumns[] = { "ID" };
		StringBuilder query = new StringBuilder();
		query.append( "INSERT INTO tEdoFinancieroFirmaElectronica( nIDReporte, nIDMoneda, nIDNivel, cRutaArchivo, cRutaAcuse, nMes  )" );
		query.append( "VALUES( ?, ?, ?, ?, ?, ? )" );

		try {
			int i = 1;
			ps = conn.prepareStatement( query.toString(), generatedColumns );
			ps.setInt( i++, reporte.getIdTipoReporte() );
			ps.setInt( i++, reporte.getIdTipoMoneda() );
			ps.setInt( i++, reporte.getIdNivel() );
			ps.setString( i++, reporte.getRutaReporteImpreso() );
			ps.setString( i++, reporte.getRutaAcuseImpreso() );
			ps.setInt( i++, reporte.getMes() );

			ps.executeUpdate();
			
			ResultSet generatedKeys = ps.getGeneratedKeys();
			generatedKeys.next();
			int idEstadoFinanciero = generatedKeys.getInt( 1 );
			generatedKeys.close();
			generatedKeys = null;
			
			return idEstadoFinanciero;

		} finally {
			CloseObject.closeObject( ps );
		}
	}

	public static int insertaConciliacionFIEL( Connection conn, ConciliacionFirma reporte ) throws Exception {
		PreparedStatement ps = null;
		int idEstadoFinanciero;
		StringBuilder query = new StringBuilder();
		query.append( "INSERT INTO tConciliacionFirmaElectronica( nConciliacion, cEstatus, cRutaArchivo, cRutaAcuse )" );
		query.append( "VALUES( ?, ?, ?, ?  )" );

		try {
			int i = 1;
			ps = conn.prepareStatement( query.toString() );
			ps.setInt( i++, reporte.getIdConciliacion() );
			ps.setString( i++, "Guardado" );
			ps.setString( i++, reporte.getRutaReporteImpreso() );
			ps.setString( i++, reporte.getRutaAcuseImpreso() );

			ps.executeUpdate();

			ResultSet generatedKeys = ps.getGeneratedKeys();
			generatedKeys.next();
			idEstadoFinanciero = generatedKeys.getInt( 1 );
			generatedKeys.close();
			generatedKeys = null;
			
			return idEstadoFinanciero;

		} finally {
			CloseObject.closeObject( ps );
		}
	}

	public static int insertaFirmanteEdoFinanciero( Connection conn, int idReporte, Firmante[] empleados, int firmantes ) throws Exception {

		PreparedStatement ps = null;

		StringBuilder query = new StringBuilder();
		query.append( "INSERT INTO tFirmanteReporte(nIDEdoFinanciero, cNumeroEmpleado, nIDTipoFirmante, nOrden, cEstatus)" );
		query.append( "VALUES( ?, ?, ?, ?, ?) " );

		int insertados = 0;
		try {
			ps = conn.prepareStatement( query.toString() );
			int i = 1;

			for ( int nFirmante = 0; nFirmante < empleados.length; nFirmante++ ) {

				int numeroEmpleado = empleados[nFirmante].getNumeroEmpleado();
				String tipoFirmante = empleados[nFirmante].getTipoAutorizador();

				ps.setInt( i++, idReporte );
				ps.setInt( i++, numeroEmpleado );
				ps.setInt( i++, Integer.parseInt( tipoFirmante ) );
				ps.setInt( i++, nFirmante + 1  );
				ps.setString( i++, FirmaElectronicaReporte.ESPERA_FIRMA );
				i = 1;

				insertados += ps.executeUpdate();
				ps.clearParameters();
			}

			return insertados;

		} finally {
			CloseObject.closeObject( ps );
		}
	}
	

}
