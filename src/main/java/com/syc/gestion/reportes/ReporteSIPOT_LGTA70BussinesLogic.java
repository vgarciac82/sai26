package com.syc.gestion.reportes;

//

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JasperRunManager;

import org.apache.log4j.Logger;

import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.util.Util;
import com.syc.gestion.reportes.ReporteSIPOT_LGTA70Manager;
import com.syc.sai.contabilidad.utils.db.CloseObject;

@SuppressWarnings("unused")
public class ReporteSIPOT_LGTA70BussinesLogic extends DataSourceManager {	
	private static final Logger	log	= Logger.getLogger(ReporteSIPOT_LGTA70BussinesLogic.class);
	//parametro globales
	
	public ReporteSIPOT_LGTA70BussinesLogic(String jniName) {
		super.init(jniName);
	}	
	
	
	public void generaPlantillaExcel(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantillas) throws Exception {
		Connection conn = null;
		String file = null;
		try {
			conn = getConnection();
			
			String mes = req.getParameter("mes");
			int mesIni = Integer.parseInt(mes);
			String tipoPlantilla = req.getParameter("reporte");			
			String ejercicioFiscal = req.getParameter("ejercicioFiscal");
			int anio = Integer.parseInt(ejercicioFiscal);
						
			file = ReporteSIPOT_LGTA70Manager.ReportesExcel(conn, mesIni, anio, tipoPlantilla, plantillas);			
			File f = new File(file);
			resp.setContentType("application/vnd.ms-excel");
			resp.addHeader("Content-Disposition", "inline; filename=\"" + f.getName() + "\"; ");

			ServletOutputStream out = resp.getOutputStream();

			Util.doDownload(out, file, file, "");

			out.flush();
			out.close();

		} finally {
			conn.commit();
			CloseObject.closeObject(conn, false);
			if (file != null) {
				File f = new File(file);
				if (!f.delete())
					f.deleteOnExit();
			}
		}
	}

}
