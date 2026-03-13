package com.syc.reportes.core;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;

@SuppressWarnings("unused")
public class ReporteAC01Manager implements Serializable {

	private final static long serialVersionUID = 1;

	public static String generaReporte(Connection conn, int anio, String fecha, String ingreso,  Map<String, String>  plantillas) throws Exception {

		CallableStatement cs = null, cs2 =null, cs3 = null;
		ResultSet rs = null, rs2 = null , rs3 = null;
		String query = "", query2 = "", query3 = "";
		String fileName = null;
			
		if("IngFiscal".equals(ingreso)){
			query = "{CALL dbo.sp_Formato_AC01_calendario ( ? ) }";
			query2 = "{CALL dbo.sp_Formato_AC01_flujoEgreso ( ?, ? ) }";
			query3 = "{CALL dbo.sp_Formato_AC01_flujoIngreso ( ?, ? ) }";
		}
		else if("IP".equals(ingreso)){
			query = "{CALL dbo.sp_Formato_AC01_calendarioIP  ( ? ) }";
			query2 = "{CALL dbo.sp_Formato_AC01_flujoEgreso ( ?, ? ) }";
			query3 = "{CALL dbo.sp_Formato_AC01_flujoIngreso ( ?, ? ) }";
		}			
		try {	
			cs = conn.prepareCall(query);							
			cs.setString(1, fecha);
			rs = cs.executeQuery();
			
			cs2 = conn.prepareCall(query2);
			cs2.setInt(1, anio);
			cs2.setString(2, fecha);								
			rs2 = cs2.executeQuery();
			
			cs3 = conn.prepareCall(query3);
			cs3.setInt(1, anio);
			cs3.setString(2, fecha);								
			rs3 = cs3.executeQuery();

			fileName = generaReporteAc01(rs, rs2, rs3 , plantillas.get("FORMATOAC01"), fecha);
			return fileName;
			
		} finally {
			CloseObject.closeObject(rs, false);
			CloseObject.closeObject(cs, false);	
			CloseObject.closeObject(rs2, false);
			CloseObject.closeObject(cs2, false);	
			CloseObject.closeObject(rs3, false);
			CloseObject.closeObject(cs3, false);	
		}

	}	
	private static String generaReporteAc01(ResultSet rs, ResultSet rs2, ResultSet rs3, String plantillaPath, String fecha) throws Exception {
		File cFileExcelPlantilla = new File(plantillaPath);
		String file_name = System.getProperty("java.io.tmpdir") + "/" +  "REPORTE_AC01" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";

		InputStream fs = new FileInputStream(cFileExcelPlantilla);
		Util.copiaArchivo(fs, file_name);
		fs.close();

		InputStream fsArchivo = new FileInputStream(cFileExcelPlantilla);
		Workbook workbook = new HSSFWorkbook(fsArchivo);
		fsArchivo.close();

		Sheet sheet0 = workbook.getSheetAt(0);
		Sheet sheet1 = workbook.getSheetAt(1);

		int cnt1 = 0, cnt2 = 0, cnt3 = 0;
		
		ResultSetMetaData rsMetadata = rs.getMetaData();
		ResultSetMetaData rsMetadata2 = rs2.getMetaData();
		ResultSetMetaData rsMetadata3 = rs3.getMetaData();
		int renglonInicio = 5;
		int renglonInicio2 =20;
			
		CellStyle estiloTabla = workbook.createCellStyle();
		estiloTabla.setBorderRight(BorderStyle.HAIR);
		estiloTabla.setBorderLeft(BorderStyle.HAIR);
		estiloTabla.setBorderTop(BorderStyle.HAIR);
		estiloTabla.setBorderBottom(BorderStyle.HAIR);	
		
		while (rs.next()) {
			Row rw = (sheet0.getRow(renglonInicio + cnt1) == null ? sheet0.createRow(renglonInicio + cnt1) : sheet0.getRow(renglonInicio + cnt1));
			for (int i = 0; i < rsMetadata.getColumnCount(); i++) {
				Util.createExcelCellRep(i, rw, rs, rsMetadata.getColumnName(i + 1), rsMetadata.getColumnType(i + 1), estiloTabla);							
			}		
			cnt1++;
		}
		
		while (rs2.next()) {
			Row rw2 = (sheet1.getRow(renglonInicio + cnt2) == null ? sheet1.createRow(renglonInicio + cnt2) : sheet1.getRow(renglonInicio + cnt2));
			for (int i = 0; i < rsMetadata2.getColumnCount(); i++) {
				Util.createExcelCellRep(i, rw2, rs2, rsMetadata2.getColumnName(i + 1), rsMetadata2.getColumnType(i + 1), estiloTabla);							
			}		
			cnt2++;
		}	
		
		while (rs3.next()) {
			Row rw3 = (sheet1.getRow(renglonInicio2 + cnt3) == null ? sheet1.createRow(renglonInicio2 + cnt3) : sheet1.getRow(renglonInicio2 + cnt3));
			for (int i = 0; i < rsMetadata3.getColumnCount(); i++) {
				Util.createExcelCellRep(i, rw3, rs3, rsMetadata3.getColumnName(i + 1), rsMetadata3.getColumnType(i + 1), estiloTabla);							
			}		
			cnt3++;
		}	
		File fsalida = new File(file_name);

		FileOutputStream fos = new FileOutputStream(fsalida);
		BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
		workbook.write(bos);

	/* Cierra Flujos */
	workbook.close();
	bos.flush();
	bos.close();
	fos.close();
	return fsalida.getAbsolutePath();
	}
}
