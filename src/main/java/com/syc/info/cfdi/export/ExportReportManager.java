package com.syc.info.cfdi.export;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.syc.cfdi.db.CloseObject;
import com.syc.info.cfdi.export.config.ReportConfig;


public final class ExportReportManager {

	private static int	SHEET_AT_INFO			= 0;
	private static int	INITIAL_ROW				= 0;
	private static int	POLICIE_TYPE_CELL		= 0;
	private static int	POLICIE_NUMBER_CELL		= 0;
	private static int	POLICIE_COSTCENTER_CELL	= 0;
	private static int	FOLIO_CELL	= 0;

	private static final void init() throws Exception {
		ExportReportManager.SHEET_AT_INFO = ReportConfig.getReportPropertie( "SHEET_AT_INFO" ) == null ? 0 : Integer.parseInt( ReportConfig.getReportPropertie( "SHEET_AT_INFO" ) );
		ExportReportManager.INITIAL_ROW = ReportConfig.getReportPropertie( "INITIAL_ROW" ) == null ? 9 : Integer.parseInt( ReportConfig.getReportPropertie( "INITIAL_ROW" ) );
		ExportReportManager.POLICIE_TYPE_CELL = ReportConfig.getReportPropertie( "POLICIE_TYPE_CELL" ) == null ? 9 : Integer.parseInt( ReportConfig.getReportPropertie( "POLICIE_TYPE_CELL" ) );
		ExportReportManager.POLICIE_NUMBER_CELL = ReportConfig.getReportPropertie( "POLICIE_NUMBER_CELL" ) == null ? 9 : Integer.parseInt( ReportConfig.getReportPropertie( "POLICIE_NUMBER_CELL" ) );
		ExportReportManager.POLICIE_COSTCENTER_CELL = ReportConfig.getReportPropertie( "POLICIE_COSTCENTER_CELL" ) == null ? 9 : Integer.parseInt( ReportConfig.getReportPropertie( "POLICIE_COSTCENTER_CELL" ) );
		ExportReportManager.FOLIO_CELL = ReportConfig.getReportPropertie( "FOLIO_CELL" ) == null ? 15 : Integer.parseInt( ReportConfig.getReportPropertie( "FOLIO_CELL" ) );
	}
/*
	public static List<String> readAndUpdateReport( Connection conn, String reportPath ) throws Exception {
		init();
		List<String> folios = new ArrayList<String>();
		InputStream inp = null;
		try {

			inp = new FileInputStream( reportPath );

			Workbook wb = WorkbookFactory.create( inp );
			Sheet sheet = wb.getSheetAt( ExportReportManager.SHEET_AT_INFO );

			int limit = sheet.getLastRowNum();
			for ( int i = ExportReportManager.INITIAL_ROW; i <= limit; i++ ) {
				Row row = sheet.getRow( i );

				String policieType = row.getCell( POLICIE_TYPE_CELL ).getStringCellValue();
				int policieNumber = Integer.parseInt( row.getCell( POLICIE_NUMBER_CELL ).getStringCellValue() );
				String policieCostCenter = row.getCell( POLICIE_COSTCENTER_CELL ).getStringCellValue();
				
				System.out.println( String.format( "Procesando renglon. %d Tipo de poliza %s Numero %d CC %s", i, policieType, policieNumber, policieCostCenter ) );
				
				String folio = searchByPolicie(conn, policieType, policieNumber, policieCostCenter );
				
				if( row.getCell( FOLIO_CELL ) == null )
					row.createCell( FOLIO_CELL );
				row.getCell( FOLIO_CELL ).setCellValue( folio );
			}
			
			FileOutputStream fileOut = new FileOutputStream(reportPath);
		    wb.write(fileOut);
		    fileOut.flush();
		    fileOut.close();
		    
			return folios;
		} finally {
			if ( inp != null ) {
				try {
					inp.close();
					inp = null;
				} catch ( Exception e ) {
					System.out.println( "Problemas cerrando el stream. La operacion continua." + e );
				}
			}
		}
	}
*/
	private static String searchByPolicie( Connection conn, String policieType, int policieNumber, String policieCostCenter ) {
		
		String documentSearchQuery = "SELECT	TOP 1 cTipoDocumento, " 
								   + "      	cFolioDocumentoMovimiento " 
								   + "  FROM	tMovimiento WITH(NOLOCK)  " 
								   + " WHERE	nFolioPoliza = ?  " 
								   + "   AND	ccentrocontable = ? " 
								   + "   AND	ctipoPoliza = ?";
		
		String folioSearchQuery = "SELECT	folio, cdocumentohaplicado "
								   + "  FROM	vTramitesExportar WITH(NOLOCK) "
								   + " WHERE	cdocumento = ? "
								   + "   AND	nfolio = ? "
								   + "   AND	cdocumentohaplicado IN ('S','C')";
		
		String searchByPaymentQuery = "SELECT	cTipoPago, "
								   + "      	nFolioPAGO  "
								   + "  FROM	tPagadoEncabezado WITH(NOLOCK) "
								   + " WHERE	nFolioPagado = ?";
		
		
		PreparedStatement psDocumentSearch = null;
		PreparedStatement psFolioSearch = null;
		PreparedStatement psSearchByPayment = null;
		
		ResultSet rsDocumentSearch = null;
		ResultSet rsFolioSearch = null;
		ResultSet rsSearchByPayment = null;
		
		String folio = "";
		
		try {

			psSearchByPayment = conn.prepareStatement( searchByPaymentQuery );
			psFolioSearch = conn.prepareStatement( folioSearchQuery );
			psDocumentSearch = conn.prepareStatement( documentSearchQuery );
			psDocumentSearch.setInt( 1, policieNumber );
			psDocumentSearch.setString( 2, policieCostCenter );
			psDocumentSearch.setString( 3, policieType );

			rsDocumentSearch = psDocumentSearch.executeQuery();
			if ( rsDocumentSearch.next() ) {
				String docName = rsDocumentSearch.getString( "cTipoDocumento" );
				int folioNumber = rsDocumentSearch.getInt( "cFolioDocumentoMovimiento" );

				if("PAGADO".equalsIgnoreCase( docName ) ){
					psSearchByPayment.setInt( 1, folioNumber );
					rsSearchByPayment = psSearchByPayment.executeQuery();
					
					if( rsSearchByPayment.next() ){
						docName = rsSearchByPayment.getString( "cTipoPago");
						folioNumber = rsSearchByPayment.getInt( "nFolioPAGO");		         	
					}else{
						return String.format( "[ERROR] No se encontro informacion para la poliza %s%s%d con documento %s y folio %d", policieCostCenter, policieType, policieNumber,docName, folioNumber );
					}
				}
				
				System.out.println( String.format( "Buscando informacion para documento: %s con folio %d", docName, folioNumber ) );
				psFolioSearch.setString( 1, docName );
				psFolioSearch.setInt( 2, folioNumber );
				rsFolioSearch = psFolioSearch.executeQuery();

				if ( rsFolioSearch.next() ) {
					String cdocumentohaplicado = rsFolioSearch.getString( "cdocumentohaplicado" );
					if( "C".equalsIgnoreCase( StringUtils.trimToNull(  cdocumentohaplicado ) ) )
						folio = String.format( "Folio Cancelado" );
					else
						folio = rsFolioSearch.getString( 1 );
				} else {
					folio = String.format( "[ERROR] No se encontro folio para el documento %s con folio %d", docName, folioNumber );
				}

			} else {
				folio = String.format( "[ERROR] No se encontro informacion para la poliza %s%s%d", policieCostCenter, policieType, policieNumber );
			}
		} catch ( Exception e ) {
			folio = String.format( "[ERROR] Ocurrio el siguiente erro al buscar la poliza %s%s%d [%s]", policieCostCenter, policieType, policieNumber,e.toString() );
		} finally {

			CloseObject.closeObject( rsDocumentSearch );
			CloseObject.closeObject( rsFolioSearch );
			CloseObject.closeObject( psDocumentSearch );
			CloseObject.closeObject( psFolioSearch );

		}
		return folio;
		
	}
}
