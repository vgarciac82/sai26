package com.syc.reportes;

import java.io.File;
import java.sql.Connection;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.util.Util;
import com.syc.reportes.core.ReporteIngresosManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;

public class ReporteIngresosBusinessLogic extends DataSourceManager {

	public ReporteIngresosBusinessLogic(String jniName) {	
		super.init(jniName);
	}
	
	Logger log = Logger.getLogger(ReporteIngresosBusinessLogic.class);
		
	public void generaReporte(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantillas) throws Exception {
		Connection conn = null;
		String file = null;		
		
		try {
			conn = getConnection();

			String fechaInicio = req.getParameter("fecha_inicio");
			String fechaFin = req.getParameter("fecha_fin");
			String cClave = req.getParameter("cClave");
			String columnas = req.getParameter("columnas");
			String SNP = req.getParameter("SNP");
			String INGM = req.getParameter("INGM");
			file = ReporteIngresosManager.ReporteIngresos(conn, fechaInicio, fechaFin, cClave, columnas, SNP, INGM, plantillas);
			conn.commit();
			
			File f = new File (file);
			resp.setContentType("application/vnd.ms-excel");
			resp.addHeader("Content-Disposition", "inline; filename=\"" + f.getName() + "\"; ");

			ServletOutputStream out = resp.getOutputStream();

			Util.doDownload(out, file, file, "");

			out.flush();
			out.close();
			f =null;
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
