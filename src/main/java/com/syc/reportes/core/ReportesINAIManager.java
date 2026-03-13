package com.syc.reportes.core;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;

import com.syc.contable.AdecuacionBusinessLogic;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.reportes.servlet.ReportesINAIServlet;
import com.syc.sai.contabilidad.utils.db.CloseObject;


public class ReportesINAIManager {
	static AdecuacionBusinessLogic adecProy = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
	
	private static final Logger			log					= Logger.getLogger(ReportesINAIServlet.class);
	
	public static String generaReportesINAI(Connection conn, Map<String, String> plantillas,int tipoReporte) throws Exception {
		String query=generaQuery(tipoReporte);
		PreparedStatement ps = null;
		ResultSet rst = null;		
		try {
			ps = conn.prepareStatement(query);
			rst = ps.executeQuery();	
			log.debug(ps);
			String fileName = generaReportesINAI(rst, plantillas.get("INAI"),tipoReporte,conn);
			
			return fileName;
		} finally {
			CloseObject.closeObject(rst, false);
			CloseObject.closeObject(ps, false);
		}
	}
	public String generaReporteLicitacionesInvitaciones(Connection conn, Map<String, String> plantillas)throws Exception{
		PreparedStatement ps = null;
		ResultSet rst = null;
		ResultSetMetaData rsMetadata=null;
		
		File cFileExcelPlantilla = new File(plantillas.get("INAI"));
		String nombreReporte="AdjLicitacionesEInvitaciones";
		int totalcolumnas=0;
		String file_name = System.getProperty( "java.io.tmpdir" ) + File.separatorChar +"ReporteINAI_"+nombreReporte+ "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xlsx";

		InputStream fs = new FileInputStream(cFileExcelPlantilla);
		Util.copiaArchivo(fs, file_name);
		InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
		XSSFWorkbook workbook = new XSSFWorkbook(fsArchivo);
		
		String query="";
		int sheet=0;
		int renglonInicio=7;
		BufferedOutputStream bos=null;
		FileOutputStream fos=null;
		int datosHoja[][]={{0,7},{12,3},{14,3},{16,3},{18,3},{20,3},{21,3},{22,3}};
		try {
			int j=0;
			while (j<datosHoja.length) {
				if(j==0){//Query principal
					query=queryLicitacionesInv(plantillas.get("fInicio"),plantillas.get("fFin"));
				}else if(j==1){
					query=queryPosiblesContratantes(plantillas.get("fInicio"),plantillas.get("fFin"));
				}else if(j==2){
					query=queryPersonasFisMoralesconPropuestas(plantillas.get("fInicio"),plantillas.get("fFin"));
				}else if(j==3){
					query=queryAsistentesJuntaAclaraciones(plantillas.get("fInicio"),plantillas.get("fFin"));
				}else if(j==4){
					query=queryServidoresPublicos(plantillas.get("fInicio"),plantillas.get("fFin"));
				}else if(j==5){
					query=querypersonasBeneficiarias(plantillas.get("fInicio"),plantillas.get("fFin"));
				}
				else if(j==6) {
					query=queryPartidasPresupuestales(plantillas.get("fInicio"),plantillas.get("fFin"));
				}else {
					query=queryConveniosModificatorios(plantillas.get("fInicio"),plantillas.get("fFin"));
				}
				log.info(query);
				ps = conn.prepareStatement(query) ;
				rst = ps.executeQuery();
				rsMetadata = rst.getMetaData();
				if(j==0) {
					totalcolumnas=rsMetadata.getColumnCount()-5;
				}else {
					totalcolumnas=rsMetadata.getColumnCount();
				}
				sheet=datosHoja[j][0];
				renglonInicio=datosHoja[j][1];
				escribeExcel(sheet, workbook, renglonInicio,rst , totalcolumnas);
				rst=null;
				rsMetadata =null;
				j++;
			}
			File fsalida = new File(file_name);
			fos = new FileOutputStream(fsalida);
			bos = new BufferedOutputStream(fos, 1024);
			workbook.write(bos);
			bos.flush();
		}finally{
			if(cFileExcelPlantilla.isFile()){
				cFileExcelPlantilla.delete();
			}
			if(fs!=null){
				fs.close();
			}
			if(fsArchivo!=null){
				fsArchivo.close();
			}
			if(rst!=null){
				rst.close();
			}
			if(ps!=null){
				ps.close();
			}
			if(bos!=null){
				bos.close();
			}
			if(fos!=null){
				fos.close();
			}
			if(workbook!=null){
				workbook.close();
			}
			fos=null;
			bos=null;
			rst=null;
			rsMetadata=null;
			ps=null;
			fs=null;
			fsArchivo=null;
			cFileExcelPlantilla=null;
			workbook=null;
		}
		return file_name;
	}
	public String generaReporteFormato7(Connection conn, Map<String, String> plantillas,int tipoReporte)throws Exception{
		PreparedStatement ps = null;
		ResultSet rst = null;
		ResultSetMetaData rsMetadata=null;
		File cFileExcelPlantilla = null;
		int totalcolumnas=0;
		String file_name = System.getProperty( "java.io.tmpdir" ) + File.separatorChar +"Formato7_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xlsx";
		if(tipoReporte==9) {
			file_name = System.getProperty( "java.io.tmpdir" ) + File.separatorChar +"Formato7ConPluris_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xlsx";
		}
		String fechaIni[]=plantillas.get("FechaInicial").split("/");
		String fechaFin[]=plantillas.get("FechaFinal").split("/");
		String numCard=("01".equals(fechaIni[0])?"1°":fechaIni[0]);
		String cadenaFecha="Período: "+numCard+" de "+com.syc.adquisiciones.util.Util.getNameMonth(Integer.parseInt(fechaIni[1]))+" al "+fechaFin[0]+" de "+com.syc.adquisiciones.util.Util.getNameMonth(Integer.parseInt(fechaFin[1]))
		+" "+fechaIni[2]+" (*) (1)";
		InputStream fs=null;
		InputStream fsArchivo = null;
		XSSFWorkbook workbook=null;
		CellStyle estiloTabla=null;
		CellStyle estilo=null;
		String query="";
		int sheet=0;
		int renglonInicio=9;
		BufferedOutputStream bos=null;
		FileOutputStream fos=null;
		CellStyle estiloCell = null;
		String cEjercicioActual="2024";
		DataFormat format=null;
		int nCantidadColumnFinal=1;
		boolean write=true;
		JSONArray arrayObj =null;
		try{
			cFileExcelPlantilla = new File(plantillas.get("nameReport"));
			fsArchivo = new FileInputStream(cFileExcelPlantilla);
			fs = new FileInputStream(cFileExcelPlantilla);
			cEjercicioActual=com.syc.adquisiciones.util.Util.obtieneEjercicioFiscalActivo( conn );
			Util.copiaArchivo(fs, file_name);
			workbook = new XSSFWorkbook(fsArchivo);
			estiloCell = workbook.createCellStyle();
			format= workbook.createDataFormat();
			estiloCell.setDataFormat( format.getFormat("_($* #,##0.00_);_($* (#,##0.00);_($* \"-\"??_);_(@_)"));//formato de moneda _($* #,##0.00_);_($* (#,##0.00);_($* "-"??_);_(@_)  formato de numero #,##0.00
			
			estiloCell.setBorderRight(BorderStyle.THIN);
			estiloCell.setBorderLeft(BorderStyle.THIN);
			estiloCell.setBorderTop(BorderStyle.THIN);
			estiloCell.setBorderBottom(BorderStyle.THIN);
			
			estiloTabla = workbook.createCellStyle();
			estiloTabla.setBorderRight(BorderStyle.THIN);
			estiloTabla.setBorderLeft(BorderStyle.THIN);
			estiloTabla.setBorderTop(BorderStyle.THIN);
			estiloTabla.setBorderBottom(BorderStyle.THIN);
			query="select *from fn_mReporteFormato7('"+plantillas.get("FechaInicial")+"','"+plantillas.get("FechaFinal")+"')";
			if(tipoReporte==9) {
				nCantidadColumnFinal=2;
				query="select *from fn_mReporteFormato7PLU('"+plantillas.get("FechaInicial")+"','"+plantillas.get("FechaFinal")+"')";
			}
			ps = conn.prepareStatement(query) ;
			rst = ps.executeQuery();
			rsMetadata = rst.getMetaData();
			totalcolumnas=rsMetadata.getColumnCount();
			//Escribe una etiqueta en el encabezado
			Sheet sheet0 = workbook.getSheetAt(sheet);
			Row rw = sheet0.createRow(2);
			Cell celdarsad = (rw.getCell(0) == null? rw.createCell(0): rw.getCell(0));
			celdarsad.setCellValue(cadenaFecha);
			CellStyle cellStyle=workbook.createCellStyle();
			cellStyle.setAlignment(HorizontalAlignment.CENTER);
			celdarsad.setCellStyle(cellStyle);
			//Escribe el detalle
			int rows=0;
			int j=0;
			int cnt=0;
			int cantRowsFinal=17;
			int SumaLP=0;
			int SumaI3P=0;
			int SumaAD=0;
			double sumaMonto=0.0;
			double sumaMontoConv=0.0;
			double sumaMontoTotal=0.0;
			double sumaMontoPagado=0.0;
			while (rst.next()) {
				rows = renglonInicio + cnt;
				if(j>5){
					sheet0.shiftRows(rows, rows+cantRowsFinal, 1);
					rw = (sheet0.getRow(rows) == null ? sheet0.createRow(rows) : sheet0.getRow(rows));
				}else{
					rw = (sheet0.getRow(rows) == null ? sheet0.createRow(rows) : sheet0.getRow(rows));
				}
				j++;
				for (int i = 0; i < totalcolumnas-nCantidadColumnFinal; i++) {
					estilo=estiloTabla;
					write=true;
					if(i==2){
						if(1==rst.getInt( i+1 )) {
							SumaLP++;
						}else {
							write=false;
						}
					}
					if(i==3 ){
						if(1==rst.getInt( i+1 )) {
							SumaI3P++;
						}else {
							write=false;
						}
					}
					if(i==4 ){
						if(1==rst.getInt( i+1 )) {
							SumaAD++;
						}else {
							write=false;
						}
					}
					if(i==13){
						sumaMonto=sumaMonto+rst.getDouble(i+1);
						estilo=estiloCell;
					}
					if(i==14){
						sumaMontoConv=sumaMontoConv+rst.getDouble(i+1);
						estilo=estiloCell;
					}
					if(i==15){
						sumaMontoTotal=sumaMontoTotal+rst.getDouble(i+1);
						estilo=estiloCell;
					}
					if(i==16){
						sumaMontoPagado=sumaMontoPagado+rst.getDouble(i+1);
						estilo=estiloCell;
					}
					if(i==17){
						estilo=estiloCell;
					}
					if(write) {
						com.syc.gestion.util.Util.createExcelCellRep(i, rw, rst, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estilo);
					}else {
						com.syc.gestion.util.Util.createExcelCellRep( i, rw, "", estiloTabla );
					}
				}
				cnt++;
			}
			//Footer
			rows = renglonInicio + cnt;
			rw = sheet0.getRow(rows);// sheet0.createRow(rows);
			celdarsad = (rw.getCell(2) == null? rw.createCell(2): rw.getCell(2));
			celdarsad.setCellValue(SumaLP);
			celdarsad = (rw.getCell(3) == null? rw.createCell(3): rw.getCell(3));
			celdarsad.setCellValue(SumaI3P);
			celdarsad = (rw.getCell(4) == null? rw.createCell(4): rw.getCell(4));
			celdarsad.setCellValue(SumaAD);
			celdarsad = (rw.getCell(13) == null? rw.createCell(13): rw.getCell(13));
			celdarsad.setCellValue(com.syc.adquisiciones.util.Util.redondearDecimales( sumaMonto, 2 ));
			celdarsad = (rw.getCell(14) == null? rw.createCell(14): rw.getCell(14));
			celdarsad.setCellValue(com.syc.adquisiciones.util.Util.redondearDecimales( sumaMontoConv, 2 ) );
			
			celdarsad = (rw.getCell(15) == null? rw.createCell(15): rw.getCell(15));
			celdarsad.setCellValue(com.syc.adquisiciones.util.Util.redondearDecimales( sumaMontoTotal, 2 ) );
			celdarsad = (rw.getCell(16) == null? rw.createCell(16): rw.getCell(16));
			celdarsad.setCellValue(com.syc.adquisiciones.util.Util.redondearDecimales( sumaMontoPagado, 2 ) );
			celdarsad = (rw.getCell(17) == null? rw.createCell(17): rw.getCell(17));
			celdarsad.setCellValue(com.syc.adquisiciones.util.Util.redondearDecimales( sumaMontoPagado, 2 ) );
			
			rw = sheet0.getRow(rows+1);
			celdarsad = (rw.getCell(3));
			celdarsad.setCellValue(SumaLP+SumaI3P+SumaAD);
			
			//Montos de actuación
			rw = sheet0.getRow(rows+3);
			celdarsad = (rw.getCell(0) == null? rw.createCell(0): rw.getCell(0));
			
			celdarsad.setCellValue("Montos máximos de adjudicación autorizados de conformidad con lo señalado en el Presupuesto de Egresos de la Federación para el Ejercicio "+cEjercicioActual+": ");

			arrayObj=com.syc.adquisiciones.util.Util.datGuardadosTable( conn, "select nIdTipoProcedimiento,cDescripcionCorta,mMontoMinimoSinFrmt,mMontoMaximoSinFrmt from v_mMontosActuacionADQ where nIdTipoProcedimiento in(2,4) order by nIdTipoProcedimiento" );
			rw = sheet0.getRow(rows+5);
			celdarsad = (rw.getCell(10)== null? rw.createCell(10): rw.getCell(10));
			celdarsad.setCellValue(arrayObj.getJSONObject( 0 ).getDouble( "mMontoMaximoSinFrmt" ));
			
			rw = sheet0.getRow(rows+6);
			celdarsad = (rw.getCell(10)== null? rw.createCell(10): rw.getCell(10));
			celdarsad.setCellValue(arrayObj.getJSONObject( 1 ).getDouble( "mMontoMaximoSinFrmt" ));
			
			File fsalida = new File(file_name);
			fos = new FileOutputStream(fsalida);
			bos = new BufferedOutputStream(fos, 1024);
			workbook.write(bos);
			bos.flush();
		}finally{
			log.info("Proceso terminado");
			if(cFileExcelPlantilla.isFile()){
				cFileExcelPlantilla.delete();
			}
			if(fs!=null){
				fs.close();
			}
			if(fsArchivo!=null){
				fsArchivo.close();
			}
			if(rst!=null){
					rst.close();
			}
			if(ps!=null){
				ps.close();
			}
			if(bos!=null){
				bos.close();
			}
			if(fos!=null){
				fos.close();
			}
			if(workbook!=null){
				workbook.close();
			}
			fos=null;
			bos=null;
			rst=null;
			rsMetadata=null;
			ps=null;
			fs=null;
			fsArchivo=null;
			cFileExcelPlantilla=null;
			workbook=null;
			estiloCell = null;
			format=null;
			estilo=null;
			arrayObj=null;
		}
		return file_name;
	}
	public String generaReporteFormato14(Connection conn, Map<String, String> plantillas, int tipoReporte)throws Exception{
		PreparedStatement ps = null;
		ResultSet rst = null;
		ResultSetMetaData rsMetadata=null;
		PreparedStatement ps2 = null;
		ResultSet rst2 = null;
		ResultSetMetaData rsMetadata2=null;
		File cFileExcelPlantilla = null;
		int totalcolumnas=0;
		String query="select *from fn_mReporteFormato14('"+plantillas.get("FechaInicial")+"','"+plantillas.get("FechaFinal")+"')";
		String file_name = System.getProperty( "java.io.tmpdir" ) + File.separatorChar +"Formato14_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xlsx";
		if(tipoReporte==10) {
			file_name = System.getProperty( "java.io.tmpdir" ) + File.separatorChar +"Formato14ConPluris_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xlsx";
			query="select *from fn_mReporteFormato14PLU('"+plantillas.get("FechaInicial")+"','"+plantillas.get("FechaFinal")+"')";
		}
		String fechaIni[]=plantillas.get("FechaInicial").split("/");
		String fechaFin[]=plantillas.get("FechaFinal").split("/");
		String numCard=("01".equals(fechaIni[0])?"1°":fechaIni[0]);
		String cadenaFecha="Período: "+numCard+" de "+com.syc.adquisiciones.util.Util.getNameMonth(Integer.parseInt(fechaIni[1]))+" al "+fechaFin[0]+" de "+com.syc.adquisiciones.util.Util.getNameMonth(Integer.parseInt(fechaFin[1]))
		+" "+fechaIni[2]+" (*) (1)";
		InputStream fs=null;
		InputStream fsArchivo = null;
		XSSFWorkbook workbook = null;
		XSSFCellStyle estiloTabla=null;
		int sheet=0;
		int renglonInicio=11;
		BufferedOutputStream bos=null;
		FileOutputStream fos=null;
		CellStyle estilo=null;
		CellStyle estiloCell = null;
		DataFormat format=null;
		boolean write=true;
		String cEjercioActivo="2022";
		try{
			cEjercioActivo=com.syc.adquisiciones.util.Util.obtieneEjercicioFiscalActivo( conn );
			cFileExcelPlantilla = new File(plantillas.get("nameReport"));
			fsArchivo = new FileInputStream(cFileExcelPlantilla);
			fs = new FileInputStream(cFileExcelPlantilla);
			Util.copiaArchivo(fs, file_name);
			workbook = new XSSFWorkbook( fsArchivo );
			estiloCell = workbook.createCellStyle();
			format= workbook.createDataFormat();
			estiloCell.setDataFormat( format.getFormat("_($* #,##0.00_);_($* (#,##0.00);_($* \"-\"??_);_(@_)"));//formato de moneda _($* #,##0.00_);_($* (#,##0.00);_($* "-"??_);_(@_)  formato de numero #,##0.00
			estiloCell.setBorderRight(BorderStyle.THIN);
			estiloCell.setBorderLeft(BorderStyle.THIN);
			estiloCell.setBorderTop(BorderStyle.THIN);
			estiloCell.setBorderBottom(BorderStyle.THIN);
			
			estiloTabla = workbook.createCellStyle();
			estiloTabla.setBorderRight(BorderStyle.THIN);
			estiloTabla.setBorderLeft(BorderStyle.THIN);
			estiloTabla.setBorderTop(BorderStyle.THIN);
			estiloTabla.setBorderBottom(BorderStyle.THIN);
			
			ps = conn.prepareStatement(query) ;
			rst = ps.executeQuery();
			rsMetadata = rst.getMetaData();
			totalcolumnas=rsMetadata.getColumnCount();
			//Escribe una etiqueta en el encabezado
			Sheet sheet0 = workbook.getSheetAt(sheet);
			Row rw = sheet0.createRow(2);
			Cell celdarsad = (rw.getCell(0) == null? rw.createCell(0): rw.getCell(0));
			celdarsad.setCellValue(cadenaFecha);
			CellStyle cellStyle=workbook.createCellStyle();
			cellStyle.setAlignment(HorizontalAlignment.CENTER);
			celdarsad.setCellStyle(cellStyle);
			//Escribe el detalle
			int rows=0;
			int j=0;
			int cnt=0;
			int cantRowsFinal=13;
			int SumaLP=0;
			int SumaI3P=0;
			int SumaAD=0;
			double sumaMonto=0.0;
			double sumaMontoConv=0.0;
			double sumaMontoTotal=0.0;
			double sumaMontoPagado=0.0;
			while (rst.next()) {
				rows = renglonInicio + cnt;
				if(j>0){
					sheet0.shiftRows(rows, rows+cantRowsFinal, 1);
					rw = (sheet0.getRow(rows) == null ? sheet0.createRow(rows) : sheet0.getRow(rows));
				}else{
					rw =  sheet0.getRow(rows);
				}
				j++;
				for (int i = 0; i < totalcolumnas-1; i++) {
					write=true;
					estilo=estiloTabla;
					if(i==2){
						if(1==rst.getInt( i+1 )) {
							SumaLP++;
						}else {
							write=false;
						}
					}
					if(i==3 ){
						if(1==rst.getInt( i+1 )) {
							SumaI3P++;
						}else {
							write=false;
						}
					}
					if(i==4 ){
						if(1==rst.getInt( i+1 )) {
							SumaAD++;
						}else {
							write=false;
						}
					}
					if(i==13){
						sumaMonto=sumaMonto+rst.getDouble(i+1);
						estilo=estiloCell;
					}
					if(i==14){
						sumaMontoConv=sumaMontoConv+rst.getDouble(i+1);
						estilo=estiloCell;
					}
					if(i==15){
						sumaMontoTotal=sumaMontoTotal+rst.getDouble(i+1);
						estilo=estiloCell;
					}
					if(i==16){
						sumaMontoPagado=sumaMontoPagado+rst.getDouble(i+1);
						estilo=estiloCell;
					}
					if(i==17){
						estilo=estiloCell;
					}
					if(write) {
						createExcelCellRep(i, rw, rst, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estilo);
					}else {
						com.syc.gestion.util.Util.createExcelCellRep( i, rw, "", estiloTabla );
					}
				}
				cnt++;
			}
			//Footer
			if(cnt==0)
				cnt=1;
			rows = renglonInicio + cnt;
			rw = sheet0.getRow(rows);// sheet0.createRow(rows);
			celdarsad = (rw.getCell(2) == null? rw.createCell(2): rw.getCell(2));
			celdarsad.setCellValue(SumaLP);
			celdarsad = (rw.getCell(3) == null? rw.createCell(3): rw.getCell(3));
			celdarsad.setCellValue(SumaI3P);
			celdarsad = (rw.getCell(4) == null? rw.createCell(4): rw.getCell(4));
			celdarsad.setCellValue(SumaAD);
			celdarsad = (rw.getCell(13) == null? rw.createCell(13): rw.getCell(13));
			celdarsad.setCellValue(com.syc.adquisiciones.util.Util.redondearDecimales( sumaMonto, 2 ));
			
			celdarsad = (rw.getCell(14) == null? rw.createCell(14): rw.getCell(14));
			celdarsad.setCellValue(com.syc.adquisiciones.util.Util.redondearDecimales( sumaMontoConv, 2 ) );
			
			celdarsad = (rw.getCell(15) == null? rw.createCell(15): rw.getCell(15));
			celdarsad.setCellValue(com.syc.adquisiciones.util.Util.redondearDecimales( sumaMontoTotal, 2 ) );
			
			celdarsad = (rw.getCell(16) == null? rw.createCell(16): rw.getCell(16));
			celdarsad.setCellValue(com.syc.adquisiciones.util.Util.redondearDecimales( sumaMontoPagado, 2 ) );
			
			celdarsad = (rw.getCell(17) == null? rw.createCell(17): rw.getCell(17));
			celdarsad.setCellValue(com.syc.adquisiciones.util.Util.redondearDecimales( sumaMontoPagado, 2 ) );
			
			
			rw = sheet0.getRow(rows+1);
			celdarsad = (rw.getCell(2));
			celdarsad.setCellValue(SumaLP+SumaI3P+SumaAD);
			
			rw = sheet0.getRow(rows+3);
			celdarsad = (rw.getCell(0) == null? rw.createCell(0): rw.getCell(0));
			celdarsad.setCellValue("Montos Máximos de Adjudicación Autorizados de conformidad con lo señalado en el Presupuesto de Egresos de la Federación para el Ejercicio "+cEjercioActivo+": ");
			
			//Montos maximos de adjudicación
			query="select mMontoMaximoObraSinFormato,mMontoMaximoServicioSinFormato,cDescripSIPOT from v_MontosActuacionObra order by cIdTAdjudicacion";
			ps2 = conn.prepareStatement(query) ;
			rst2 = ps2.executeQuery();
			rsMetadata2 = rst2.getMetaData();
			totalcolumnas=rsMetadata2.getColumnCount();
			renglonInicio=rows+6;
			int k=0;
			int z=0;
			while (rst2.next()) {
				rows = renglonInicio + k;
				rw = (sheet0.getRow(rows) == null ? sheet0.createRow(rows) : sheet0.getRow(rows));
				for (int i = 0; i < totalcolumnas-1; i++) {
					if(i==0)
						z=8;
					else
						z=10;
					createExcelCellRep(i+z, rw, rst2, rsMetadata2.getColumnName(i + 1), rsMetadata2.getColumnType(i + 1), estiloCell);
				}
				k++;
			}
			File fsalida = new File(file_name);
			fos = new FileOutputStream(fsalida);
			bos = new BufferedOutputStream(fos, 1024);
			workbook.write(bos);
			bos.flush();
		}finally{
			log.info("Proceso terminado");
			if(cFileExcelPlantilla.isFile()){
				cFileExcelPlantilla.delete();
			}
			if(fs!=null){
				fs.close();
			}
			if(fsArchivo!=null){
				fsArchivo.close();
			}
			if(rst!=null){
					rst.close();
			}
			if(ps!=null){
				ps.close();
			}
			if(rst2!=null){
				rst2.close();
			}
			if(ps2!=null){
				ps2.close();
			}
			if(bos!=null){
				bos.close();
			}
			if(fos!=null){
				fos.close();
			}
			if(workbook!=null){
				workbook.close();
			}
			fos=null;
			bos=null;
			rst=null;
			rsMetadata=null;
			ps=null;
			rst2=null;
			rsMetadata2=null;
			ps2=null;
			fs=null;
			fsArchivo=null;
			cFileExcelPlantilla=null;
			workbook=null;
			estilo=null;
			estiloCell = null;
			format=null;
		}
		return file_name;
	}
	public String generaReporteAnexo3(Connection conn, Map<String, String> plantillas)throws Exception{
		PreparedStatement ps = null;
		ResultSet rst = null;
		ResultSetMetaData rsMetadata=null;
		File cFileExcelPlantilla = new File(plantillas.get("nameReport"));
		int totalcolumnas=0;
		String file_name = System.getProperty( "java.io.tmpdir" ) + File.separatorChar +"ReporteAnexo3_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";

		InputStream fs = new FileInputStream(cFileExcelPlantilla);
		Util.copiaArchivo(fs, file_name);
		InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
		Workbook workbook = new HSSFWorkbook(fsArchivo);
		CellStyle estiloTabla = workbook.createCellStyle();
		estiloTabla.setBorderRight(BorderStyle.THIN);
		estiloTabla.setBorderLeft(BorderStyle.THIN);
		estiloTabla.setBorderTop(BorderStyle.THIN);
		estiloTabla.setBorderBottom(BorderStyle.THIN);
		String query="";
		int sheet=0;
		int renglonInicio=12;
		BufferedOutputStream bos=null;
		FileOutputStream fos=null;
		try {
			query="select  *from v_mReportePresidenciaAnexo3 with(Nolock)";
			ps = conn.prepareStatement(query) ;
			rst = ps.executeQuery();
			rsMetadata = rst.getMetaData();
			totalcolumnas=rsMetadata.getColumnCount();
			escribeExcelClonaRows(sheet, workbook, renglonInicio,rst , totalcolumnas,estiloTabla,1);
			File fsalida = new File(file_name);
			fos = new FileOutputStream(fsalida);
			bos = new BufferedOutputStream(fos, 1024);
			workbook.write(bos);
			bos.flush();
		}finally{
			log.info("Proceso terminado");
			if(cFileExcelPlantilla.isFile()){
				cFileExcelPlantilla.delete();
			}
			if(fs!=null){
				fs.close();
			}
			if(fsArchivo!=null){
				fsArchivo.close();
			}
			if(rst!=null){
					rst.close();
			}
			if(ps!=null){
				ps.close();
			}
			if(bos!=null){
				bos.close();
			}
			if(fos!=null){
				fos.close();
			}
			if(workbook!=null){
				workbook.close();
			}
			fos=null;
			bos=null;
			rst=null;
			rsMetadata=null;
			ps=null;
			fs=null;
			fsArchivo=null;
			cFileExcelPlantilla=null;
			workbook=null;
		}
		return file_name;
	}
	public String generaReporteV2(Connection conn, Map<String, String> plantillas,int tipoReporte)throws Exception{
		PreparedStatement ps = null;
		ResultSet rst = null;
		ResultSetMetaData rsMetadata=null;
		File cFileExcelPlantilla = new File(plantillas.get("nameReport"));
		int totalcolumnas=0;
		String file_name = System.getProperty( "java.io.tmpdir" ) + File.separatorChar +"Formato_V2_MATRIZ_DE_CONTRATOS_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";

		InputStream fs = new FileInputStream(cFileExcelPlantilla);
		Util.copiaArchivo(fs, file_name);
		InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
		Workbook workbook = new HSSFWorkbook(fsArchivo);
		
		String query="";
		int sheet=0;
		int renglonInicio=2;
		BufferedOutputStream bos=null;
		FileOutputStream fos=null;
		try {
			String cEjercicioAnt=plantillas.get("cEjercicioAnt");
			String cEjercicioAct=com.syc.adquisiciones.util.Util.obtieneEjercicioFiscalActivo(conn);
			if(tipoReporte==4){
				query=queryFormatoV2(plantillas.get("nameDB") ,plantillas.get("cMes") );
			}else{
				query=queryFormatoV2Backup(Integer.parseInt(plantillas.get("cMes")));
			}
			log.info(query);
			ps = conn.prepareStatement(query) ;
			rst = ps.executeQuery();
			rsMetadata = rst.getMetaData();
			totalcolumnas=rsMetadata.getColumnCount();
			String[] encabezado={"Monto del contrato ejercicio "+cEjercicioAnt+"\n(30)","Pagos del contrato en mes seleccionado "+cEjercicioAnt+"\n(31)","Pagos del contrato en  mes seleccionado "+cEjercicioAct+"\n(32)"};
			escribeEncabezado(sheet, workbook, renglonInicio-1, encabezado,29);
			escribeExcelRestandolasUltimasColumnas(sheet, workbook, renglonInicio,rst , totalcolumnas);
			File fsalida = new File(file_name);
			fos = new FileOutputStream(fsalida);
			bos = new BufferedOutputStream(fos, 1024);
			workbook.write(bos);
			bos.flush();
		}finally{
			log.info("Proceso terminado");
			if(cFileExcelPlantilla.isFile()){
				cFileExcelPlantilla.delete();
			}
			if(fs!=null){
				fs.close();
			}
			if(fsArchivo!=null){
				fsArchivo.close();
			}
			if(rst!=null){
					rst.close();
			}
			if(ps!=null){
				ps.close();
			}
			if(bos!=null){
				bos.close();
			}
			if(fos!=null){
				fos.close();
			}
			if(workbook!=null){
				workbook.close();
			}
			fos=null;
			bos=null;
			rst=null;
			rsMetadata=null;
			ps=null;
			fs=null;
			fsArchivo=null;
			cFileExcelPlantilla=null;
			workbook=null;
		}
		return file_name;
	}
	private String queryFormatoV2Backup(int nMes){
		String where=" ";
		if(nMes>0){
			where=" Where nMesComparado="+nMes+" ";
		}
		return"select "
			+"'16'ramo  "
			+",'RHQ'ur  "
			+",'CONAFOR'dependencia "
			+",cIdcontratoDefinitivo "
			+",cConceptoContrato "
			+",nCantidadClavesCucop "
			+",cCucop "
			+",nCantidadPartidas "
			+",cPartidas"
			+",mMontoContratado "
			+",cTipoMoneda"
			+",bMonedaExtranjera "
			+",fFechaFallo "
			+",fFechaInicio "
			+",fFechaTermino "
			+",cEsPluri "
			+",cEstatus "
			+",cExisteClausulaPenalizacion "
			+",cExisteClausulaGarantia "
			+",cPorcentajeGarantia "
			+",nNumeroProveedores "
			+",cRFCProveedores "
			+",cRazonSocial "
			+",cTipoProveedor "
			+",cProcedimientoContratación "
			+",cFundamentoLegal "
			+",cRFCServPublicoSolicitante"
			+",cRFCServPublicoAut "
			+",cRFCServPublicoJuridico"
			+",mMontoContratadoEjercicioAnt"
			+",mPagoContEjercicioAnt"
			+",mPagoContEjercicioAct "
			+"from mV2MatrizContratos with(nolock) "+where;
	}
	private String queryFormatoV2(String nameDB,String cMes){
		return "select   "+
			"'16'ramo  "+
			",'RHQ'ur  "+
			",'CONAFOR'dependencia  "+
			",sub.cIdContrato,sub.cConceptoContrato,numeroClavesCucop,cucop,numeroDePartidas,pardidas,montoContrato,tipoMoneda,monedaExtranjera  "+
			",fechaFallo,FechaInicio,FechaTermino,esPluri,ESTATUS,existeClausulaPenalizacion,existeClausulaGarantia,porcentajeGarantia  "+
			",numProveedores,rfcProveedor,razonSocial,tipoProveedor,procedimientoContratacion,fundamentoLeg,rfcServPubSolicitante  "+
			",rfcServPubAut,rfcServPubJuridico  "+
			",isnull((select "+nameDB+".[dbo].[fn_mMontoContratadoProveedor]( rfcProveedor,nIdCategoria, nIdFundamentoLeg, cucop )),0) montoContratoEjercicioAnt  "+
			",isnull((select "+nameDB+".[dbo].[fn_mMontoPagadoProveedor]( rfcProveedor,nIdCategoria, nIdFundamentoLeg, cucop ,"+cMes+")),0) pagosContEjercicioAnt  "+
			",pagosContEjercicioAct  "+
			"from  "+
			"	(select   "+
			"	contDiv.cIdContrato  "+
			"	,replace(REPLACE(REPLACE(contratos.cConceptoContrato,CHAR(10),''),CHAR(13),''),',','')  cConceptoContrato  "+
			"	,case when contratos.tipoProced=1 then(select dbo.fn_mCantidadCUCOP(contratos.cIdConsolidado))  "+
			"	else (select dbo.fn_mCantidadCUCOP2(contDiv.cIdContrato)) end numeroClavesCucop  "+
			"	,case when contratos.tipoProced=1 then rtrim((select dbo.fn_mCadenaCUCOP(contratos.cIdConsolidado)))   "+
			"		else rtrim((select dbo.fn_mCadenaCUCOP2(contDiv.cIdContrato))) end cucop  "+
			"	,(select [dbo].[fn_mCantidadPartidasContrato](contDiv.cIdContrato)) numeroDePartidas  "+
			"	,(select [dbo].[fn_mCadenaPartidasContrato](contDiv.cIdContrato))pardidas  "+
			"	,contratos.montoContrato+isnull(convenios.totalMod,0) montoContrato  "+
			"	,'PESOS'tipoMoneda  "+
			"	,0 monedaExtranjera  "+
			"	,convert(date,contratos.fFallo) fechaFallo  "+
			"	,convert(date,fContratoIni) FechaInicio  "+
			"	,convert(date,fContratoFin)FechaTermino  "+
			"	,case when contDiv.lEsPlurianual=1 then 'SI' else 'NO' end esPluri  "+
			"	,case when GETDATE()>convert(date,fContratoFin) then 'CONCLUIDO' ELSE 'VIGENTE'END ESTATUS  "+
			"	,'No se cuenta con la información'existeClausulaPenalizacion  "+
			"	,case when contratos.mTotalGarantias>0 then 'SI' else 'NO'end existeClausulaGarantia  "+
			"	,case when contratos.mTotalGarantias>0 then convert(varchar,round(contratos.mTotalGarantias*100/contratos.montoContrato,2))+' %' else '0 %'end porcentajeGarantia  "+
			"	,1 numProveedores  "+
			"	,contratos.cIdRFC rfcProveedor  "+
			"	,catProveedor.cRazonSocial razonSocial  "+
			"	,case when catProveedor.nIdPyme=6 then 'PUBLICO' else 'PRIVADO' end tipoProveedor  "+
			"	,catCategoriaProced.cDescripcionCorta procedimientoContratacion  "+
			"	,catFundamentoLeg.cFundamentoLegal fundamentoLeg  "+
			"	,''rfcServPubSolicitante  "+
			"	,''rfcServPubAut  "+
			"	,''rfcServPubJuridico  "+
			"	,ISNULL(pagos.Pagado,0) pagosContEjercicioAct  "+
			"	,catCategoriaProced.nIdCategoria  "+
			"	,catFundamentoLeg.nIdFundamentoLeg  "+
			"	from pContratoDiverso contDiv with(Nolock)  "+
			"	inner join(  "+
			"		select cIdContratoDefinitivo,cont.cIdProcedimiento,cont.cIdRFC,fFallo  "+
			"		,case when isnull(adjPart.montoMaximo,0)>isnull(adjPart.minimo,0) then isnull(adjPart.montoMaximo,0) else isnull(adjPart.minimo,0) end montoContrato  "+
			"		,cont.cConceptoContrato,adjPart.cIdConsolidado,isnull(documentacion.mTotalGarantias,0)mTotalGarantias  "+
			"		,adjPart.nIdFundamentoLeg,adjPart.nIdCategoria  "+
			"		,mMontoTotalPlurianual  "+
			"		,1 tipoProced  "+
			"		from mcontrato as cont with(Nolock)  "+
			"		inner join mDocumentacionContrato as documentacion with(Nolock) on documentacion.cIdContrato=cont.cIdContratoDefinitivo  "+
			"		inner join(select adjPart.cIdProcedimiento,SUM(mMontoNetoMinimo) as minimo,SUM(mMontoNetoLineaMax)as montoMaximo,procedAdj.cIdRFC,adjPart.cIdConsolidado   "+
			"			,procedAdj.nIdFundamentoLeg,proced.nIdCategoria  "+
			"			,proced.cEjercicio,proced.cIdUnidadEjecutora,proced.cIdTipoProcedimiento, proced.nIdConsecutivo  "+
			"			,procedAdj.mMontoTotalPlurianual,adjPart.nIdconsecutivoAdj  "+
			"			from mProcedimientoAdjudicacionPartidas as adjPart with(Nolock)   "+
			"			INNER JOIN mProcedimiento as proced with(Nolock) on proced.cEjercicio=adjPart.cEjercicio  "+
			"			and proced.cIdUnidadEjecutora=adjPart.cIdUnidadEjecutora  "+
			"			and proced.cIdTipoProcedimiento=adjPart.cIdTipoProcedimiento  "+
			"			and proced.nIdConsecutivo=adjPart.nIdConsecutivo  "+
			"			and proced.cIdProcedimiento=adjPart.cIdProcedimiento  "+
			"			INNER JOIN mProcedimientoAdjudicacion AS procedAdj with(Nolock) on procedAdj.cEjercicio=adjPart.cEjercicio  "+
			"			and procedAdj.cIdUnidadEjecutora=adjPart.cIdUnidadEjecutora  "+
			"			and procedAdj.cIdTipoProcedimiento=adjPart.cIdTipoProcedimiento  "+
			"			and procedAdj.nIdConsecutivo=adjPart.nIdConsecutivo  "+
			"			and procedAdj.cIdProcedimiento=adjPart.cIdProcedimiento  "+
			"     		and procedAdj.cIdRFC=adjPart.cIdRFC  "+
			"     		and procedAdj.nIdconsecutivoAdj=adjPart.nIdconsecutivoAdj  "+
			"			group by adjPart.cIdProcedimiento,procedAdj.cIdRFC,adjPart.cIdConsolidado,procedAdj.nIdFundamentoLeg,proced.nIdCategoria  "+
			"			,proced.cEjercicio,proced.cIdUnidadEjecutora,proced.cIdTipoProcedimiento, proced.nIdConsecutivo  "+
			"			,procedAdj.mMontoTotalPlurianual,adjPart.nIdconsecutivoAdj  "+
			"		)adjPart on adjPart.cEjercicio=cont.cEjercicio   "+
			"		and  adjPart.cIdUnidadEjecutora=cont.cIdUnidadEjecutora  "+
			"		and adjPart.cIdTipoProcedimiento=cont.cIdTipoProcedimiento  "+
			"		and adjPart.nIdConsecutivo=cont.nIdConsecutivoProcedimiento  "+
			"		and adjPart.cIdProcedimiento=cont.cIdProcedimiento and cont.cIdRFC=adjPart.cIdRFC  and  cont.nIdconsecutivoAdj=adjPart.nIdconsecutivoAdj"+
			"		where cont.nIdEstado=4  "+
			/*"		-----------------Plurianuales---------------  "+*/
			"		union  "+
			"		select   "+
			"		pluriContrato.cidcontratodefinitivo  "+
			"		,'N/A'cidProcedimiento  "+
			"		,pluriContrato.cidrfc  "+
			"		,convert(date,pluriContrato.fFallo)fFallo  "+
			"		,pluriContrato.mMontoNeto  "+
			"		,pluriContrato.cConceptoContrato  "+
			"		,'N/A'cidconsolidado  "+
			"		,0 totalGarantias  "+
			"		,pluriContrato.nIdFundamentoLeg  "+
			"		,pluriContrato.nIdCategoria  "+
			"		,contPlu.mMontoTotalPlurianual totalPluri  "+
			"		,2tipoProced  "+
			"		from mplurianualidadContrato pluriContrato with(Nolock)  "+
			"		inner join mContratoPlurianualidad as contPlu with(Nolock)  "+
			"		on contPlu.cIdContratoDefinitivo=pluriContrato.cIdContratoDefinitivo  "+
			"		where pluriContrato.nIdEstado=4  "+
			/*"		-----------------------Remanentes--------------  "+*/
			"		Union  "+
			"		select   "+
			"		cIdContratoDefinitivo  "+
			"		,'N/A'cidProcedimiento  "+
			"		,case when len(remanente.cidrfc)=12 then substring(remanente.cidrfc,1,3)+'-'+ substring(remanente.cidrfc,4,6)+'-'+ substring(remanente.cidrfc,10,LEN(remanente.cidrfc))  "+
			"		else substring(remanente.cidrfc,1,4)+'-'+ substring(remanente.cidrfc,5,6)+'-'+ substring(remanente.cidrfc,11,LEN(remanente.cidrfc)) end rfcComp  "+
			"		,convert(date,remanente.fFallo)fFallo  "+
			"		,remanente.mTotalRemanente  "+
			"		,remanente.cConcepto  "+
			"		,'N/A'cidconsolidado  "+
			"		,0 totalGarantias  "+
			"		,remanente.nIdFundamentoLeg  "+
			"		,remanente.nIdCategoria  "+
			"		,0 totalPluri  "+
			"		,3 tipoProced  "+
			"		from mContratoRemanenteEjercicioAnterior as remanente with(Nolock)  "+
			"		where nEstado=4  "+
			/*"		-------------------Convenios de ejercicios anteriores---------------  "+*/
			"		union  "+
			"		select   "+
			"			cIdContratoDefinitivo  "+
			"			,'N/A'cidProcedimiento  "+
			"			,cIdRFC  "+
			"			,convert(date,convEjerAnt.fFallo)fFallo  "+
			"			,convEjerAnt.mTotalModificacion  "+
			"			,convEjerAnt.cObjetoConvenio  "+
			"			,'N/A'cidconsolidado  "+
			"			,0 totalGarantias  "+
			"			,convEjerAnt.nIdFundamentoLeg  "+
			"			,convEjerAnt.nIdCategoria  "+
			"			,0 totalPluri  "+
			"			,4 tipoProced  "+
			"		from mContratoModificado convEjerAnt with(Nolock)  "+
			"		where nEstado=4 and isConvEjercicioAnt=1  "+
			"		and tipoMod=0  "+
			"	)contratos on contratos.cIdContratoDefinitivo=contDiv.cIdContrato  "+
			"	inner join mCatalogoProveedor catProveedor with(nolock) on catProveedor.cIdRFC=contratos.cIdRFC  "+
			"	inner join mCatalogoCategoriaProcedimiento as catCategoriaProced with(Nolock) on catCategoriaProced.nIdCategoria=contratos.nIdCategoria  "+
			"	inner join mCatalogoFundamentoLegal as catFundamentoLeg with(Nolock) on catFundamentoLeg.nIdCategoria=contratos.nIdCategoria  "+
			"	and catFundamentoLeg.nIdFundamentoLeg=contratos.nIdFundamentoLeg  "+
			/*"	----Convenios-----  "+*/
			"	LEFT join(select   "+
			"		contMod.cIdContratoDefinitivo  "+
			"		,sum(isnull(contMod.mTotalModificacion,0))as totalMod  "+
			"		From mContratoModificado as contMod  "+
			"			where contMod.nEstado=4   "+
			"			and isConvEjercicioAnt=0  "+
			"			and tipoMod=0  "+
			"			group by contMod.cIdContratoDefinitivo  "+
			"	)convenios	on convenios.cIdContratoDefinitivo=contratos.cIdContratoDefinitivo  "+
			"	left outer JOIN (		  "+
			"					SELECT   "+
			"						SUM(ISNULL(sub.pagos,0)-ISNULL(sub.totalReintegro,0))Pagado  "+
			"						,sub.cFolioPAGODIVERSO folioContPed  "+
			"						FROM	  "+
			"							(SELECT   "+
			"								CONVERT(VARCHAR,sum(isnull(d.mImporteMasIva,0)),1)pagos  "+
			"								,ISNULL(reintegro.totalReintegro,0)totalReintegro  "+
			"								,e.cFolioPAGODIVERSO  "+
			"								FROM dbo.tPAGODIVERSOEncabezado e WITH (NOLOCK)	  "+
			"								INNER JOIN dbo.tPAGODIVERSODetalle d WITH (NOLOCK) ON e.nFolioPAGODIVERSO = d.nFolioPAGODIVERSO AND e.cDocumentoHaplicado='S'	  "+
			"								inner join tPagadoEncabezado as pe with(Nolock) on pe.caNoContrarrecibo=e.caNoContrarrecibo and pe.cDocumentoHaplicado='S'  "+
			"								LEFT join (  "+
			"											SELECT   "+
			"											SUM(mImporte) totalReintegro,cxp  "+
			"											FROM  tReintegroEncabezado as reintEnc with(Nolock)  "+
			"											inner join tReintegroDetalle as reintDet with(Nolock)   "+
			"											on reintDet.nFolioReintegro=reintEnc.nFolioReintegro and reintEnc.cDocumentoHaplicado='S'  "+
			"											where month(reintEnc.fAplicacion)="+cMes+"	GROUP BY cxp  "+
			"								)reintegro ON reintegro.cxp=e.caNoContrarrecibo  "+
			"								WHERE month(e.faplicacion)="+cMes+"  "+
			"								GROUP BY reintegro.totalReintegro,e.cFolioPAGODIVERSO  "+
			"							)sub  "+
			"						  "+
			"							GROUP BY sub.cFolioPAGODIVERSO	  "+
			"				) AS pagos ON pagos.folioContPed=contratos.cIdContratoDefinitivo  "+
			"	)sub order by sub.fechaFallo ";
	}
	public String generaReporteProveedores(Connection conn, Map<String, String> plantillas)throws Exception{
		PreparedStatement ps = null;
		ResultSet rst = null;
		ResultSetMetaData rsMetadata=null;
		
		File cFileExcelPlantilla = new File(plantillas.get("INAI"));
		String nombreReporte="PadronProveedores";
		int totalcolumnas=0;
		String file_name = System.getProperty( "java.io.tmpdir" ) + File.separatorChar +"ReporteINAI_"+nombreReporte+ "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xlsx";

		InputStream fs = new FileInputStream(cFileExcelPlantilla);
		Util.copiaArchivo(fs, file_name);
		InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
		XSSFWorkbook workbook = new XSSFWorkbook(fsArchivo);
		String query="";
		int sheet=0;
		int renglonInicio=7;
		BufferedOutputStream bos=null;
		FileOutputStream fos=null;
		try {
			query=queryProveedores(plantillas.get("fInicio"),plantillas.get("fFin"));
			log.info(query);
			ps = conn.prepareStatement(query) ;
			rst = ps.executeQuery();
			rsMetadata = rst.getMetaData();
			totalcolumnas=rsMetadata.getColumnCount();
			escribeExcel(sheet, workbook, renglonInicio,rst , totalcolumnas);
			File fsalida = new File(file_name);
			fos = new FileOutputStream(fsalida);
			bos = new BufferedOutputStream(fos, 1024);
			workbook.write(bos);
			bos.flush();
		}finally{
			if(cFileExcelPlantilla.isFile()){
				cFileExcelPlantilla.delete();
			}
			if(fs!=null){
				fs.close();
			}
			if(fsArchivo!=null){
				fsArchivo.close();
			}
			if(rst!=null){
				rst.close();
			}
			if(ps!=null){
				ps.close();
			}
			if(bos!=null){
				bos.close();
			}
			if(fos!=null){
				fos.close();
			}
			if(workbook!=null){
				workbook.close();
			}
			fos=null;
			bos=null;
			rst=null;
			rsMetadata=null;
			ps=null;
			fs=null;
			fsArchivo=null;
			cFileExcelPlantilla=null;
			workbook=null;
		}
		return file_name;
	}
	public String generaReporteADJ(Connection conn, Map<String, String> plantillas)throws Exception{
		PreparedStatement ps = null;
		ResultSet rst = null;
		ResultSetMetaData rsMetadata=null;
		
		File cFileExcelPlantilla = new File(plantillas.get("INAI"));
		String nombreReporte="AdjudicacionDirecta";
		int totalcolumnas=0;
		String file_name = System.getProperty( "java.io.tmpdir" ) + File.separatorChar +"ReporteINAI_"+nombreReporte+ "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xlsx";

		InputStream fs = new FileInputStream(cFileExcelPlantilla);
		Util.copiaArchivo(fs, file_name);
		

		InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
		XSSFWorkbook workbook = new XSSFWorkbook(fsArchivo);
		
		String query="";
		int sheet=0;
		int renglonInicio=7;
		BufferedOutputStream bos=null;
		FileOutputStream fos=null;
		int datosHoja[][]={{0,7},{9,3},{11,3},{13,3}};
		try {
			int j=0;
			while (j<datosHoja.length) {
				if(j==0){//Query principal
					query=queryPrincipalADJ(plantillas.get("fInicio"),plantillas.get("fFin"));
				}else if(j==1){
					query=queryCotizacionesADJ(plantillas.get("fInicio"),plantillas.get("fFin"));
				}else if(j==2){
					query=queryDatosObraADJ(plantillas.get("fInicio"),plantillas.get("fFin"));
				}else {
					query=queryConveniosADJ(plantillas.get("fInicio"),plantillas.get("fFin"));
				}
				log.info(query);
				ps = conn.prepareStatement(query) ;
				rst = ps.executeQuery();
				rsMetadata = rst.getMetaData();
				if(j==0) {
					totalcolumnas=rsMetadata.getColumnCount()-6;
				}else {
					totalcolumnas=rsMetadata.getColumnCount();
				}
				sheet=datosHoja[j][0];
				renglonInicio=datosHoja[j][1];
				escribeExcel(sheet, workbook, renglonInicio,rst , totalcolumnas);
				rst=null;
				rsMetadata =null;
				j++;
			}
			File fsalida = new File(file_name);

			fos = new FileOutputStream(fsalida);
			bos = new BufferedOutputStream(fos, 1024);
			workbook.write(bos);
			bos.flush();
			
		}finally{
			if(cFileExcelPlantilla.isFile()){
				cFileExcelPlantilla.delete();
			}
			if(fs!=null){
				fs.close();
			}
			if(fsArchivo!=null){
				fsArchivo.close();
			}
			if(rst!=null){
				rst.close();
			}
			if(ps!=null){
				ps.close();
			}
			if(bos!=null){
				bos.close();
			}
			if(fos!=null){
				fos.close();
			}
			if(workbook!=null){
				workbook.close();
			}
			fos=null;
			bos=null;
			rst=null;
			rsMetadata=null;
			ps=null;
			fs=null;
			fsArchivo=null;
			datosHoja=null;
			cFileExcelPlantilla=null;
			workbook=null;
		}
		return file_name;
	}
	
	
	private static  String generaQuery(int tipoReporte) {
		String query="";
		switch (tipoReporte){
			case 1://Contratistas y Proveedores
				query= "SELECT "
					+ "(SELECT TOP(1) Datepart (year, b_co_fecha_ini) "
					+ " FROM   cg_bitacora WITH (nolock) "
					+ " WHERE  b_c_folio = cfolio "
					+ "        AND b_co_responsable_sigte = 'Autoriza_Proveedor')      AS Ejercicio, "
					+ "(SELECT TOP(1)  CONVERT(VARCHAR, fInicio, 103)fInicio FROM  mCatalogoTrimestre WITH (nolock) "
					+ "WHERE nIdTrimestre=(Isnull(Datepart(quarter, (SELECT TOP(1) CONVERT(VARCHAR, b_co_fecha_ini, 103) " 
					+ "                          FROM   cg_bitacora WITH (nolock)  "
					+ "                          WHERE  b_c_folio = cfolio  "
					+ "                                 AND b_co_responsable_sigte = " 
					+ "                                     'Autoriza_Proveedor')), 1)))fInicioTrimestre, " 
					+ "(SELECT TOP(1)  CONVERT(VARCHAR, fFin, 103)fFin FROM  mCatalogoTrimestre WITH (nolock) "
					+ "WHERE nIdTrimestre=(Isnull(Datepart(quarter, (SELECT TOP(1) CONVERT(VARCHAR, b_co_fecha_ini, 103) " 
					+ "                          FROM   cg_bitacora WITH (nolock)  "
					+ "                          WHERE  b_c_folio = cfolio  "
					+ "                                 AND b_co_responsable_sigte = " 
					+ "                                     'Autoriza_Proveedor')), 1)))fFinTrimestre, "
					+ "CASE "
					+ "  WHEN cidtipopersona = 2 THEN 'Física' "
					+ "  ELSE 'Moral' "
					+ "END AS Tipo, "
					+ "CASE "
					+ "  WHEN cidtipopersona = 1 THEN '' "
					+ "  ELSE alta.cnombre "
					+ "END AS nombre, "
					+ "CASE "
					+ "  WHEN cidtipopersona = 1 THEN '' "
					+ "  ELSE alta.capellidopaterno "
					+ "END AS APaterno, "
					+ "CASE "
					+ "  WHEN cidtipopersona = 1 THEN '' "
					+ "  ELSE alta.capellidomaterno "
					+ "END AS AMaterno, "
					+ "CASE "
					+ "  WHEN cidtipopersona = 1 THEN alta.crazonsocial "
					+ "  ELSE '' "
					+ "END AS RazonSocial, "
					+ "pyme.cpyme AS "
					+ "Estratificacion, "
					+ "CASE "
					+ "  WHEN cextranjero = 1 THEN 'Internacional' "
					+ "  ELSE 'Nacional' "
					+ "END AS Origen, "
					+ "ef.centidadfederativa "
					+ "EntidadFederativa, "
					+ "cpais      Pais, "
					+ "CASE "
					+ "  WHEN cidtipopersona = 1 THEN alta.cidrfc "
					+ "  ELSE '' "
					+ "END AS RFC, "
					+ "'No'       AS "
					+ "Subcontrataciones, "
					+ "alta.cgiro AS giro, "
					+ "CASE "
					+ "  WHEN cidtipopersona = 1 THEN 'Calle' "
					+ "  ELSE '' "
					+ "END AS tipoVialidad, "
					+ "CASE "
					+ "  WHEN cidtipopersona = 1 THEN alta.ccalle "
					+ "  ELSE '' "
					+ "END AS Calle, "
					+ "CASE "
					+ "  WHEN cidtipopersona = 1 THEN alta.cnumeroexterno "
					+ "  ELSE '' "
					+ "END AS NumExterno, "
					+ "CASE "
					+ "  WHEN cidtipopersona = 1 THEN alta.cnumerointerno "
					+ "  ELSE '' "
					+ "END AS NumInterno, "
					+ "CASE "
					+ "  WHEN cidtipopersona = 1 THEN 'Ciudad' "
					+ "  ELSE '' "
					+ "END AS "
					+ "tipoAsentamiento, "
					+ "CASE "
					+ "  WHEN cidtipopersona = 1 THEN mun.mpo_nombre "
					+ "  ELSE '' "
					+ "END AS "
					+ "nombreAsentamiento, "
					+ "CASE "
					+ "  WHEN cidtipopersona = 1 THEN mun.mpo_abreviatura "
					+ "  ELSE '' "
					+ "END AS "
					+ "claveLocalidad, "
					+ "CASE "
					+ "  WHEN cidtipopersona = 1 THEN mun.mpo_nombre "
					+ "  ELSE '' "
					+ "END AS "
					+ "NombreLocalidad, "
					+ "CASE "
					+ "  WHEN cidtipopersona = 1 THEN mun.mpo_abreviatura "
					+ "  ELSE '' "
					+ "END AS "
					+ "claveMunicipio, "
					+ "CASE "
					+ "  WHEN cidtipopersona = 1 THEN mun.mpo_nombre "
					+ "  ELSE '' "
					+ "END AS "
					+ "NombreMunicipio, "
					+ "CASE "
					+ "  WHEN cidtipopersona = 1 THEN mun.mpo_abreviatura "
					+ "  ELSE '' "
					+ "END AS "
					+ "claveEntidadFederativa, "
					+ "CASE "
					+ "  WHEN cidtipopersona = 1 THEN ef.centidadfederativa "
					+ "  ELSE '' "
					+ "END AS "
					+ "EntidadFederativa, "
					+ "CASE "
					+ "  WHEN cidtipopersona = 1 THEN alta.ccodigopostal "
					+ "  ELSE '' "
					+ "END AS CP, "
					+ "CASE "
					+ "  WHEN cidtipopersona = 1 THEN alta.cnombre "
					+ "  ELSE '' "
					+ "END AS nombre, "
					+ "CASE "
					+ "  WHEN cidtipopersona = 1 THEN alta.capellidopaterno "
					+ "  ELSE '' "
					+ "END AS APaterno, "
					+ "CASE "
					+ "  WHEN cidtipopersona = 1 THEN alta.capellidomaterno "
					+ "  ELSE '' "
					+ "END AS AMaterno, "
					+ "''  AS telefono, "
					+ "''  AS correo, "
					+ "CASE "
					+ "  WHEN cidtipopersona = 1 THEN 'IDENTIFICACION' "
					+ "  ELSE '' "
					+ "END AS "
					+ "tipoAcreditacion, "
					+ "CASE "
					+ "  WHEN cidtipopersona = 1 THEN alta.curl "
					+ "  ELSE '' "
					+ "END AS WEB, "
					+ "''  AS Telefono, "
					+ "''  AS correo, "
					+ "'https://sites.google.com/site/cnetrupc/rupc ' AS "
					+ "HiperVinculoRegistro, "
					+ "'http://directoriosancionados.funcionpublica.gob.mx/SanFicTec/jsp/Ficha_Tecnica/SancionadosN.htm' "
					+ "HiperVinculoSancionados, "
					+ "(SELECT TOP(1) CONVERT(VARCHAR, b_co_fecha_ini, 103) "
					+ "FROM   cg_bitacora WITH (nolock) "
					+ "WHERE  b_c_folio = cfolio "
					+ "AND b_co_responsable_sigte = 'Autoriza_Proveedor')      AS fechaValidacion, "
					+ "'Coordinación General de Administración' AS "
					+ "ResponsableInformacion, "
					+ "(SELECT TOP(1) Datepart (year, b_co_fecha_ini) "
					+ "FROM   cg_bitacora  WITH (nolock) "
					+ "WHERE  b_c_folio = cfolio "
					+ "AND b_co_responsable_sigte = 'Autoriza_Proveedor')      AS Año, "
					+ "CONVERT(VARCHAR, Getdate(), 103)  AS "
					+ "FechaActualizacion, "
					+ "CASE "
					+ "WHEN cidtipopersona = 1 THEN "
					+ "'Los datos de las columnas AE,AF,AI y AJ no se proporcionan toda vez que son datos personales que se encuentran protegidos de conformidad con los principios contenidos en el artículo 16 y lo establecido en el artículo 1 , párrafo cuarto de la Ley General de Protección de Datos Personales en Posesión de los Sujetos Obligados, así como en los artículos 68, fracción VI, 100 y 116 de la Ley General de Transparencia y Acceso a la Información Pública, 97 y 113, fracción I de la Ley Federal de Transparencia y Acceso a la Información Pública, y el trigésimo octavo, fracción I de los Lineamientos Generales en materia de clasificación y desclasificación de la Información, así como para la elaboración de versiones públicas.' "
					+ "ELSE "
					+ "'Los datos de las columnas  L, de la O a la AA, AE,AF,AI y AJ no se proporcionan toda vez que son datos personales que se encuentran protegidos de conformidad con los principios contenidos en el artículo 16 y lo establecido en el artículo 1 , párrafo cuarto de la Ley General de Protección de Datos Personales en Posesión de los Sujetos Obligados, así como en los artículos 68, fracción VI, 100 y 116 de la Ley General de Transparencia y Acceso a la Información Pública, 97 y 113, fracción I de la Ley Federal de Transparencia y Acceso a la Información Pública, y el trigésimo octavo, fracción I de los Lineamientos Generales en materia de clasificación y desclasificación de la Información, así como para la elaboración de versiones públicas.' "
					+ "END AS Nota "
					+ "FROM   taltaproveedor alta WITH (nolock) "
					+ "       INNER JOIN mcatalogoproveedor catProv WITH (nolock) "
					+ "               ON alta.cidrfc = catProv.cidrfc "
					+ "       INNER JOIN mcatalogoentidadfederativa EF WITH (nolock) "
					+ "               ON alta.cidentidadfederativa = ef.cidentidadfederativa "
					+ "       INNER JOIN cat_municipio mun WITH (nolock) "
					+ "               ON mun.id_municipio = alta.cidmunicipio "
					+ "       INNER JOIN mcatalogopyme pyme WITH (nolock) "
					+ "               ON pyme.nidpyme = alta.nidpyme "
					+ "WHERE  catProv.lhabilitado = 1 "
					+ "       AND cdocumentohaplicado IN ( 'S', 'V', 'R', 'P', "
					+ "                                    'M', 'A', 'D' ) "
					//+"  and alta.cFolio in ('PROV-A02-7989','PROV-A02-7988','PROV-A02-8167' )"
					;


				break;
			case 2://Adjudicacion Directa
					query= "SELECT "+
							" * FROM v_Reporte_INAI_Adj_Directa ";
				break;
			
		}
		return query;
	}
	public static String generaReportesINAI(ResultSet rs, String plantillaPath, int tipoReporte,Connection conn) throws Exception {
		File cFileExcelPlantilla = new File(plantillaPath);
		String nombreReporte="",query="";
		int totalcolumnas=0;
		
		String file_name = "ReporteINAI"+nombreReporte+ "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";

		InputStream fs = new FileInputStream(cFileExcelPlantilla);
		Util.copiaArchivo(fs, file_name);
		fs.close();

		InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
		Workbook workbook = new HSSFWorkbook(fsArchivo);
		fsArchivo.close();
		PreparedStatement ps2 = null;
		ResultSet rst2 = null;
		FileOutputStream fos =null;
		BufferedOutputStream bos =null;
		try {
			
		switch (tipoReporte){
			case 1://Contratistas y Proveedores
				nombreReporte="ContratistasProveedores";
				totalcolumnas=43;
				
				break;
			case 2://Adjudicacion Directa
				nombreReporte="AdjudicacionDirecta";
				totalcolumnas=41;
				query="SELECT vconv.cidprocedimiento,cnombre,capellidopaterno,capellidomaterno,crazonsocial,cmonto "+
						" FROM mCotizacionesProcedimiento proce with (nolock) "+
						" inner join v_Reporte_INAI_Adj_Directa vconv "+
						" on vconv.cIdProcedimiento=proce.cIdProcedimiento "+
						" order by cidprocedimiento ";
				ps2 = conn.prepareStatement(query);
				rst2 = ps2.executeQuery();	
				log.debug(ps2);
				
				escribeExcel(4, workbook, 3,rst2 , 5);
				
				query="select vista.cben, "+ 
						"case  "+
							"when cIdTipoPersonaRFC=1 "+ 
							"	then ''  "+
							"when cIdTipoPersonaRFC=2 "+
							"	then dNombre "+
							"end as Nombre, "+
						"case  "+
							"when cIdTipoPersonaRFC=1 "+ 
							"	then ''  "+
							"when cIdTipoPersonaRFC=2 "+
							"	then dApellidoPaterno "+
							"end as Paterno, "+
						"case  "+
						"	when cIdTipoPersonaRFC=1 "+ 
						"		then ''  "+
						"	when cIdTipoPersonaRFC=2 "+
						"		then dApellidoMaterno "+
						"	end as Materno, "+
						"case  "+
						"	when cIdTipoPersonaRFC=1 "+ 
						"		then dNombre  "+
						"	when cIdTipoPersonaRFC=2 "+
						"		then '' "+
						"	end as RazonSocial "+  
					"from tBeneficiario ben with (nolock) "+
					"inner join v_Reporte_INAI_Adj_Directa vista with (nolock) "+
					"on ben.CBEN=vista.cben";
				
				ps2 = conn.prepareStatement(query);
				rst2 = ps2.executeQuery();	
				log.debug(ps2);
				
				escribeExcel(5, workbook, 3,rst2 , 4);
				
				query="select	"+
								"modi.cIdContratoDefinitivo, "+
								"cNoConvenio, "+
								"cObjetoConvenio, "+
								"conv.fFirmaContrato, "+
								"isnull(doc.cHipDocConv,'') as HiperVinculo  "+
						"from mContratoModificado modi with (nolock) "+
						"inner join pContratoDiversoConvenio conv with (nolock) "+
						"on modi.cIdContratoDefinitivo=conv.cIdContrato "+
						"inner join v_Reporte_INAI_Adj_Directa vconv "+
						"on vconv.idconv=modi.cContratoDefinitivo "+
						"left join mDocumentacionConvenio doc with (nolock) "+
						"on modi.cContratoDefinitivo=doc.cContratoDefinitivo";
			
			ps2 = conn.prepareStatement(query);
			rst2 = ps2.executeQuery();	
			log.debug(ps2);
			
			escribeExcel(7, workbook, 3,rst2 , 4);
				
				break;
		}
		int renglonInicio = 7;
		escribeExcel(0, workbook, renglonInicio, rs, totalcolumnas);	 
		
		File fsalida = new File(file_name);

		fos = new FileOutputStream(fsalida);
		bos = new BufferedOutputStream(fos, 1024);
		workbook.write(bos);

		/* Cierra Flujos */
		bos.flush();
		return file_name;
		
		} finally {
			CloseObject.closeObject(bos, false);
			CloseObject.closeObject(fos, false);
			CloseObject.closeObject(workbook, false);
			CloseObject.closeObject(rst2, false);
			CloseObject.closeObject(ps2, false);
		}
	}
	
	public static void escribeExcel(int sheet,Workbook workbook,int renglonInicio,ResultSet rs, int totalcolumnas ) throws Exception{
		Sheet sheet0 = workbook.getSheetAt(sheet);
		int cnt = 0;
		Row rw;
		ResultSetMetaData rsMetadata=null;
		int rows;
		try {
			rsMetadata = rs.getMetaData();
			while (rs.next()) {
				rows = renglonInicio + cnt;
				rw = (sheet0.getRow(rows) == null ? sheet0.createRow(rows) : sheet0.getRow(rows));
				for (int i = 0; i < totalcolumnas; i++) {	
					com.syc.adquisiciones.util.Util.createExcelCellRep( i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1) );
				}
				cnt++;
			}
		} finally {
			rsMetadata=null;
		}
	}
	public static void escribeExcelClonaRows(Sheet sheet0,int renglonInicio,ResultSet rs, int totalcolumnas,CellStyle estiloTabla,int cantRowsFinal ) throws Exception{	
		int cnt = 0;
		Row rw;
		int rows;
		int j=0;
		ResultSetMetaData rsMetadata=null;
		rsMetadata = rs.getMetaData();
		try{
			while (rs.next()) {
				rows = renglonInicio + cnt;
				if(j>5){
					sheet0.shiftRows(rows, rows+cantRowsFinal, 1);
					rw = (sheet0.getRow(rows) == null ? sheet0.createRow(rows) : sheet0.getRow(rows));
				}else{
					rw = (sheet0.getRow(rows) == null ? sheet0.createRow(rows) : sheet0.getRow(rows));
				}
				j++;
				for (int i = 0; i < totalcolumnas; i++) {
					com.syc.gestion.util.Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
				}
				cnt++;
			}
		}finally{
			rsMetadata=null;
		}
	}
	public static void escribeExcelClonaRows(int sheet,Workbook workbook,int renglonInicio,ResultSet rs, int totalcolumnas,CellStyle estiloTabla,int cantRowsFinal ) throws Exception{		
		Sheet sheet0 = workbook.getSheetAt(sheet);
		int cnt = 0;
		Row rw;
		int rows;
		int j=0;
		ResultSetMetaData rsMetadata=null;
		rsMetadata = rs.getMetaData();
		try{
			while (rs.next()) {
				rows = renglonInicio + cnt;
				if(j>5){
					sheet0.shiftRows(rows, rows+cantRowsFinal, 1);
					rw = (sheet0.getRow(rows) == null ? sheet0.createRow(rows) : sheet0.getRow(rows));
				}else{
					rw = (sheet0.getRow(rows) == null ? sheet0.createRow(rows) : sheet0.getRow(rows));
				}
				j++;
				for (int i = 0; i < totalcolumnas; i++) {
					com.syc.gestion.util.Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);
				}
				cnt++;
			}
		}finally{
			rsMetadata=null;
		}
			
	}
	public static void escribeExcelRestandolasUltimasColumnas(int sheet,Workbook workbook,int renglonInicio,ResultSet rs, int totalcolumnas ) throws SQLException{
		Sheet sheet0 = workbook.getSheetAt(sheet);
		int cnt = 0;
		Row rw;
		Cell celdarsad;
		int rows;
			while (rs.next()) {
				
				rows = renglonInicio + cnt;
				rw = (sheet0.getRow(rows) == null ? sheet0.createRow(rows) : sheet0.getRow(rows));
				int i = 0;
				for (i = 0; i < totalcolumnas; i++) {
					celdarsad = (rw.getCell(i) == null? rw.createCell(i): rw.getCell(i));
					celdarsad.setCellValue(rs.getString(i+1));
				}
				celdarsad = (rw.getCell(totalcolumnas) == null? rw.createCell(totalcolumnas): rw.getCell(totalcolumnas));
				celdarsad.setCellValue((rs.getDouble(totalcolumnas))-(rs.getDouble(totalcolumnas-1)));
				cnt++;
			}
	}
	public static void escribeEncabezado(int sheet,Workbook workbook,int renglonInicio,String[] encabezado,int columnInicio ) throws SQLException{
		Sheet sheet0 = workbook.getSheetAt(sheet);
		Row rw;
		Cell celdarsad;
		int rows=renglonInicio;
		rw = (sheet0.getRow(rows) == null ? sheet0.createRow(rows) : sheet0.getRow(rows));
		int i = 0;
		for (i = 0; i < encabezado.length; i++) {
			celdarsad = (rw.getCell(i+columnInicio) == null? rw.createCell(i+columnInicio): rw.getCell(i+columnInicio));
			celdarsad.setCellValue(encabezado[i]);
		}
	}
	private String queryPrincipalADJ(String fIni,String fFin){
		return "select *from fn_mINAIADJ('"+fIni+"','"+fFin+"')";
	}
	private String queryCotizacionesADJ(String fIni,String fFin){
		return "SELECT  \r\n" + 
				"	fun.cotizaciones\r\n" + 
				"	,cotizaciones.cNombre \r\n" + 
				"	,cotizaciones.cApellidoPaterno \r\n" + 
				"	,cotizaciones.cApellidoMaterno \r\n" + 
				"	,cotizaciones.cRazonSocial \r\n" + 
				"	,fun.sexo,cIdRFC \r\n" + 
				"	,cotizaciones.cMonto \r\n" + 
				"	FROM mCotizacionesProcedimiento AS cotizaciones WITH(NOLOCK) \r\n" + 
				"	inner join fn_mINAIADJ('"+fIni+"','"+fFin+"') as fun \r\n" + 
				"	on fun.cIdProcedimiento=cotizaciones.cIdProcedimiento ";
	}
	private String queryDatosObraADJ(String fIni,String fFin){
		return "select \r\n"
				+ "tabla_334255\r\n"
				+ ",cLugarRealizaObra\r\n"
				+ "from [fn_mINAIADJ]('"+fIni+"','"+fFin+"')\r\n"
				+ "where CONVERT(int,tabla_334255)>0";
	}
	private String queryConveniosADJ(String fIni,String fFin){
		return " SELECT  \r\n" + 
				"	fun.tabla_334268 \r\n" + 
				"	,conv.cNoConvenio \r\n" + 
				"	,conv.cObjetoConvenio \r\n" + 
				"	,CONVERT(VARCHAR,contratoConv.fFirmaContrato,103)fFirmaContrato \r\n" + 
				"	,cUrlSIPOT+'filesconafor/userfiles/LGTAIP/'+fun.cEjercicio+'/art_70/fra_XXVIII/inci_B/UAF/GRM/ADJDIR/'\r\n" + 
				"	+convert(varchar,fun.nIdTrimestre)+'T/8_documento_del_convenio/'\r\n" + 
				"	+isnull(replace((SUBSTRING(conv.cNoConvenio,CHARINDEX('-E', conv.cNoConvenio)+1,LEN(conv.cNoConvenio) ) ),'/','-'),'No_Cuenta_ConProced_CNET')\r\n" + 
				"	+'-CONV.pdf'  AS himpervinculo \r\n" + 
				"	FROM fn_mINAIADJ('"+fIni+"','"+fFin+"') fun\r\n" + 
				"	INNER JOIN dbo.mContratoModificado AS conv WITH(NOLOCK) \r\n" + 
				"	ON conv.cIdContratoDefinitivo=fun.cidcontratoDefinitivo \r\n" + 
				"	INNER JOIN pContratoDiversoConvenio AS contratoConv WITH(NOLOCK) \r\n" + 
				"	ON contratoConv.cIdContrato=conv.cIdContratoDefinitivo \r\n" + 
				"	AND conv.cContratoDefinitivo=contratoConv.cIdModificacion \r\n" + 
				"	AND contratoConv.nConsecutivoModificacion=conv.nConsecutivoModificacion \r\n" + 
				"	WHERE nEstado=4";
	}
	private String queryProveedores(String fIni,String fFin){
		return " select *from v_mReporteProveedoresINAI where convert(date,fInicioTrimestre)>=convert(date,'"+fIni+"')\r\n"
				+ "and convert(date,fInicioTrimestre)<=convert(date,'"+fFin+"') ";
	}
	private String queryLicitacionesInv(String fIni,String fFin){
		return "select *from fn_mINAILicitacionesInvitaciones('"+fIni+"','"+fFin+"') ";
				
	}
	private String queryPosiblesContratantes(String fIni,String fFin){
		return "SELECT cont.posiblesContratantes588029 id\r\n" + 
				"	,ISNULL(cNombre,'')nombre,ISNULL(cApellidoPat,'')apellidoPat,ISNULL(cApellidoMat,'')apellidoMat "+
				"   ,case when asist.nIdSexo=0 then '' else sexo.cSexo end cSexo,ISNULL(cRazonSocial,'')razonSocial,asist.cIdRFC  rfc\r\n" + 
				"	FROM mAsistentesProcedimiento asist WITH(NOLOCK) \r\n" +
				"   inner join mcatalogoSexo as sexo with(Nolock) on sexo.nIdSexo=asist.nIdSexo \r\n"+
				"	inner join fn_mOrdenContratosINAI('"+fIni+"','"+fFin+"') as cont  on asist.cIdTipoProcedimiento+'-'+asist.cIdUnidadEjecutora+'-'+CONVERT(VARCHAR,asist.nIdConsecutivoProced)=cont.cIdProcedimiento\r\n" + 
				"	WHERE nIdTipoAsistente=1\r\n" + 
				"	ORDER BY cont.posiblesContratantes588029  ";
	}
	private String queryPersonasFisMoralesconPropuestas(String fIni,String fFin){
		return "SELECT   \r\n"
				+ "			cont.cotizaciones_588056 id \r\n"
				+ "			,cotizaciones.cNombre  \r\n"
				+ "			,cotizaciones.cApellidoPaterno  \r\n"
				+ "			,cotizaciones.cApellidoMaterno  \r\n"
				+ "			,case when cotizaciones.nIdSexo=0 then '' else sexo.cSexo end cSexo,cotizaciones.cRazonSocial \r\n"
				+ "			,cotizaciones.cIdRFC rfc  \r\n"
				+ "			FROM mCotizacionesProcedimiento AS cotizaciones WITH(NOLOCK)  \r\n"
				+ "	       inner join mcatalogoSexo as sexo with(Nolock) on sexo.nIdSexo=cotizaciones.nIdSexo \r\n"
				+ "			inner join fn_mOrdenContratosINAI('"+fIni+"','"+fFin+"') as cont on cont.cIdProcedimiento=cotizaciones.cIdProcedimiento \r\n"
				+ "			ORDER BY cont.cotizaciones_588056 ";
	}
	private String queryAsistentesJuntaAclaraciones(String fIni,String fFin){
		return "select *from 	\r\n" + 
				"	(SELECT   \r\n" + 
				"		cont.relacionAsistentesJuntaAclaraciones_588057 id  \r\n" + 
				"		,ISNULL(cNombre,'')nombre,ISNULL(cApellidoPat,'')apellidoPat  \r\n" + 
				"		,ISNULL(cApellidoMat,'')apellidoMat,case when asist.nIdSexo=0 then '' else sexo.cSexo end cSexo  \r\n" + 
				"		,CASE WHEN LEN(asist.cIdRFC)=15 THEN '' ELSE  ISNULL(prov.cRazonSocial,'') END razonSocial  \r\n" + 
				"		,asist.cIdRFC   \r\n" + 
				"		FROM fn_mOrdenContratosINAI('"+fIni+"','"+fFin+"') as cont  \r\n" + 
				"		inner join mAsistentesProcedimiento asist WITH(NOLOCK)   \r\n" + 
				"		on cont.cIdProcedimiento=cIdTipoProcedimiento+'-'+asist.cIdUnidadEjecutora+'-'+CONVERT(VARCHAR,nIdConsecutivoProced)   \r\n" + 
				"       inner join mcatalogoSexo as sexo with(Nolock) on sexo.nIdSexo=asist.nIdSexo \r\n"+
				"		inner join mCatalogoProveedor as prov WITH(NOLOCK)  \r\n" + 
				"		on prov.cIdRFC=asist.cIdRFC  \r\n" + 
				"		WHERE nIdTipoAsistente=2  \r\n" + 
				"	)sub\r\n" + 
				"	order by id";
	}
	private String queryServidoresPublicos(String fIni,String fFin){//tabla 588058
		return "SELECT relacionDatosServidoresPublicos_588058 id \r\n" + 
				",ISNULL(cNombre,'')nombre,ISNULL(cApellidoPat,'')apellidoPat,ISNULL(cApellidoMat,'')apellidoMat \r\n"+
				",case when asist.nIdSexo=0 then '' else sexo.cSexo end cSexo,cIdRFC rfc,cPlaza\r\n" + 
				"FROM mAsistentesProcedimiento asist WITH(NOLOCK) \r\n" +
				"inner join mcatalogoSexo as sexo with(Nolock) on sexo.nIdSexo=asist.nIdSexo \r\n"+
				"inner join fn_mOrdenContratosINAI('"+fIni+"','"+fFin+"') as cont\r\n" + 
				"on cont.cIdProcedimiento=cIdTipoProcedimiento+'-'+cIdUnidadEjecutora+'-'+CONVERT(VARCHAR,nIdConsecutivoProced)\r\n" + 
				"WHERE nIdTipoAsistente=3\r\n" + 
				"order by relacionDatosServidoresPublicos_588058";
	}
	private String querypersonasBeneficiarias(String fIni,String fFin){//tabla 588026
		return " select nombreCompletoPersonaBeneficiaria_508026,nombre,dApellidoPaterno,dApellidoMaterno "
				+ " from [fn_mOrdenContratosINAIBenef] ('"+fIni+"','"+fFin+"') ";
	}
	private String queryPartidasPresupuestales(String fIni,String fFin){
		return "select \r\n"
				+ "partidaPresupuestal_588059\r\n"
				+ ",isnull((select dbo.fn_mCadenaPartidasContrato(cidcontratoDefinitivo)),'')partida\r\n"
				+ "from [fn_mOrdenContratosINAI] ('"+fIni+"','"+fFin+"')";
	}
	private String queryConveniosModificatorios(String fIni,String fFin){
		return " select \r\n"
				+ "		cont.datosConvenio_588060 id\r\n"
				+ "		,conv.cNoConvenio\r\n"
				+ "		,conv.cObjetoConvenio\r\n"
				+ "		,convert(varchar,contratoConv.fFirmaContrato,103)fFirmaContrato\r\n"
				+ "		,cont.cUrlSIPOT+'filesconafor/userfiles/LGTAIP/'+cont.cEjercicio+'/art_70/fra_XXVIII/inci_A/UAF/GRM/'\r\n"
				+ "			+case when cont.nIdCategoria<5 then'LP/'else case when cont.nIdCategoria >=10 then 'ADJDIR/' else  'ICTP/' end end\r\n"
				+ "			+convert(varchar,cont.nIdTrimestre)+'T/12_documento_del_convenio/'\r\n"
				+ "			+replace((SUBSTRING(conv.cNoConvenio,CHARINDEX('-E', conv.cNoConvenio)+1,LEN(conv.cNoConvenio) ) ),'/','-')+'-CONV.pdf' AS himpervinculo\r\n"
				+ "		from mContratoModificado as conv with(Nolock)\r\n"
				+ "		INNER JOIN pContratoDiversoConvenio AS contratoConv WITH(NOLOCK)\r\n"
				+ "			ON contratoConv.cIdContrato=conv.cIdContratoDefinitivo\r\n"
				+ "			and conv.cContratoDefinitivo=contratoConv.cIdModificacion\r\n"
				+ "			AND contratoConv.nConsecutivoModificacion=conv.nConsecutivoModificacion\r\n"
				+ "		inner join fn_mOrdenContratosINAI('"+fIni+"','"+fFin+"') as cont\r\n"
				+ "		on cont.cIdContratoDefinitivo=conv.cIdContratoDefinitivo\r\n"
				+ "		where conv.nEstado=4\r\n"
				+ "	union select \r\n"
				+ "		cont.datosConvenio_588060 id\r\n"
				+ "		,conv.cNoConvenio\r\n"
				+ "		,conv.cDescripcionConvenio\r\n"
				+ "		,convert(varchar,convert(date,fFechaConvenio),103) AS fFechaConvenio\r\n"
				+ "		,cont.cUrlSIPOT+'filesconafor/userfiles/LGTAIP/'+cont.cEjercicio+'/art_70/fra_XXVIII/inci_A/UAF/GRM/'\r\n"
				+ "			+case when cont.nIdCategoria='01' then'LP/'else case when cont.nIdCategoria='23' then 'ADJDIR/' else    'ICTP/' end end\r\n"
				+ "			+convert(varchar,cont.nIdTrimestre)+'T/12_documento_del_convenio/'\r\n"
				+ "			+replace((SUBSTRING(conv.cNoConvenio,CHARINDEX('-E', conv.cNoConvenio)+1,LEN(conv.cNoConvenio) ) ),'/','-')+'-CONV.pdf' AS himpervinculo\r\n"
				+ "	from fn_mOrdenContratosINAI('"+fIni+"','"+fFin+"')as cont\r\n"
				+ "	inner join tobrapublicaconvmodifencabezado as conv with(Nolock)\r\n"
				+ "	on conv.cCveContrato=cont.cidcontratoDefinitivo\r\n"
				+ "	where cdocumentohaplicado = 'S' ";
	}
	public static Cell createExcelCellRep( int index, Row fila, ResultSet rs, String cellName, int tipoDato, CellStyle estiloTabla ) throws Exception {

		//Cell cell = fila.createCell( index );
		Cell cell=(fila.getCell(index) == null? fila.createCell(index): fila.getCell(index));
		if ( tipoDato == Types.BIGINT || tipoDato == Types.BIT || tipoDato == Types.INTEGER || tipoDato == Types.SMALLINT || tipoDato == Types.TINYINT ) {
			// Tipos de dato enteros
			int val = rs.getInt( cellName );

			cell.setCellValue( val );
			cell.setCellStyle( estiloTabla );
			return cell;
		} else if ( tipoDato == Types.DECIMAL || tipoDato == Types.DOUBLE || tipoDato == Types.FLOAT || tipoDato == Types.NUMERIC || tipoDato == Types.REAL ) {
			// Tipos de dato reales
			double val = rs.getDouble( cellName );

			cell.setCellValue( val );
			cell.setCellStyle( estiloTabla );
			return cell;
		} else if ( tipoDato == Types.DATE || tipoDato == Types.TIME || tipoDato == Types.TIMESTAMP ) {

			if ( rs.getDate( cellName ) != null ) {
				// Tipo de dato fecha
				Date d = new Date( rs.getDate( cellName ).getTime() );

				cell.setCellValue( d );
				cell.setCellStyle( estiloTabla );
			}
			return cell;
		} else {
			String val = rs.getString( cellName );
			cell.setCellValue( val );
			cell.setCellStyle( estiloTabla );
			return cell;
		}
	}
}
