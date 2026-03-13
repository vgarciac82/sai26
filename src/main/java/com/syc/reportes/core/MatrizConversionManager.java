package com.syc.reportes.core;

import java.io.FileInputStream;
import java.sql.Connection;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import net.sf.jasperreports.engine.JasperRunManager;

public class MatrizConversionManager {
	
	public static final Logger log = Logger.getLogger(MatrizConversionManager.class);

	public static void cosulta(Connection conn, HttpServletResponse resp, String ruta, Map<String, Object> parms, String reportPath) {
		FileInputStream in = null;
		ServletOutputStream out = null;

		try {
			out = resp.getOutputStream();
			in = new FileInputStream(reportPath);
			JasperRunManager.runReportToPdfStream(in, out, parms, conn);
			resp.setContentType("application/pdf");
			out.flush();
			out.close();
		} catch (Exception exc) {
			exc.printStackTrace();
		} finally {
			try {
				if (in != null)
					in.close();

				if (out != null)
					out.close();
			} catch (Exception exc) {
				exc.printStackTrace();
			}

			in = null;
			out = null;
		}
		
	}
		
}
