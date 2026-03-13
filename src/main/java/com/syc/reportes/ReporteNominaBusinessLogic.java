
package com.syc.reportes;

import java.io.File;
import java.sql.Connection;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.util.Util;
import com.syc.reportes.core.ReporteNominaManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;

public class ReporteNominaBusinessLogic extends DataSourceManager {

	public ReporteNominaBusinessLogic(String jniName) {
		super.init(jniName);
	}

	public void generaReporteNomina(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantilla) throws Exception {
		Connection conn = null;
		String file = null;
		try {
			conn = getConnection();
			String fecha = req.getParameter( "fecha_fin" );
			
			file = ReporteNominaManager.generaReporteNominaManager(conn, fecha, plantilla);
			
			File f = new File (file);
			resp.setContentType("application/octet-stream");
			resp.addHeader("Content-Disposition", "inline; filename=\"" + f.getName() + "\"; ");
			
			ServletOutputStream out = resp.getOutputStream();

			Util.doDownload(out, file, file, "");

			out.flush();
			out.close();
			conn.commit();
		} finally {
			CloseObject.closeObject(conn, false);
			if (file != null) {
				File f = new File(file);
				if (!f.delete())
					f.deleteOnExit();
			}
		}
		
	}
	
	public void generaReporteSaldosCompromisoMil(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantilla) throws Exception {
		Connection conn = null;
		String file = null;
		try {
			conn = getConnection();
			String cxp = req.getParameter("cxp");
			String ep = req.getParameter("ep");
			
			file = ReporteNominaManager.generaReporteSaldosCompromisoMil(conn, cxp, ep, plantilla);
			
			File f = new File (file);
			resp.setContentType("application/octet-stream");
			resp.addHeader("Content-Disposition", "inline; filename=\"" + f.getName() + "\"; ");
			
			ServletOutputStream out = resp.getOutputStream();

			Util.doDownload(out, file, file, "");

			out.flush();
			out.close();
			conn.commit();
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
