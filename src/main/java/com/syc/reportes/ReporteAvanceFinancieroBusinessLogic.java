package com.syc.reportes;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.reportes.reportes;
import com.syc.gestion.util.Util;
import com.syc.reportes.core.ReporteAvanceFinancieroManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;

@SuppressWarnings("unused")
public class ReporteAvanceFinancieroBusinessLogic extends DataSourceManager {

	public ReporteAvanceFinancieroBusinessLogic(String jniName) {
		super.init(jniName);
	}

	public void generaReporte(HttpServletRequest req, HttpServletResponse response, Map<String, String> plantilla) throws Exception {
		Connection conn = null;
		String file = null;
		try {
			conn = getConnection();

			String fechaInicio = req.getParameter("fecha_inicio");			
			int mesIni = Integer.parseInt(fechaInicio.substring(3,5));
			int anio = Integer.parseInt(fechaInicio.substring(6,10));
			//fechaInicio.substring(6,10);

			file = ReporteAvanceFinancieroManager.ReporteAvanceFinancieroManager(conn, mesIni, anio, plantilla);
			
			File f = new File(file);
			response.setContentType("application/vnd.ms-excel");
			response.addHeader("Content-Disposition", "inline; filename=\"" + f.getName() + "\"; ");

			ServletOutputStream out = response.getOutputStream();

			Util.doDownload(out, file, file, "");

			out.flush();
			out.close(); 

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
