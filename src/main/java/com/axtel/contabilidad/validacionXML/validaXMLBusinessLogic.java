package com.axtel.contabilidad.validacionXML;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.syc.contable.AccountingEngine;
import com.syc.crud.dsmngr.DataSourceManager;

import com.syc.gestion.core.Usuario;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;

public class validaXMLBusinessLogic extends DataSourceManager {
	
	private static Logger log = Logger.getLogger(validaXMLBusinessLogic.class);

	public validaXMLBusinessLogic(String jniName) {
		super.init(jniName);
	}

	public void procesaArchivoMasivo(HttpServletRequest req, HttpServletResponse resp, String nombreDestino, Usuario u ) throws Exception {
		InputStream fs = null;
		Workbook workbook = null;
		String condicion = "IN ('";
		int cont;
		String file = null;
		
		Connection conn = null;
		
		try {
			
			conn = getConnection();	

			fs = new FileInputStream( nombreDestino );
			workbook = new HSSFWorkbook( fs );
	
			Sheet hoja = workbook.getSheetAt( 0 );
					
			AccountingEngine ae = new AccountingEngine();
			ae.setValidaInsuficienciaDeSaldo(true);
			
			for ( int i = 0; i <= hoja.getLastRowNum(); i++ ) {
				Row fila = hoja.getRow( i );
				if(i == hoja.getLastRowNum() )
					condicion = condicion + fila.getCell( 0 ).getStringCellValue() + "')";
				else
					condicion = condicion + fila.getCell( 0 ).getStringCellValue() + "','";			
			}
			log.trace( condicion );
					
			file = validaXMLManager.buscaXML(conn, condicion, nombreDestino);		
			
			File f = new File (file);
			resp.setContentType("application/vnd.ms-excel");
			resp.addHeader("Content-Disposition", "inline; filename=\"" + f.getName() + "\"; ");
	
			ServletOutputStream out = resp.getOutputStream();
	
			Util.doDownload(out, file, file, "");
	
			out.flush();
			out.close();
			f =null;
			
		} catch ( Exception e ) {
			throw e;
		} finally {
			CloseObject.closeObject(conn, false);
			if (file != null) {
				File f = new File(file);
				if (!f.delete())
					f.deleteOnExit();
			}
		}
				
	}
	
}
