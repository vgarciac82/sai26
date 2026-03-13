/**
 * 
 */
package com.syc.reportes;

import java.io.File;
import java.sql.Connection;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.util.Util;
import com.syc.reportes.core.cedulasSIIWEBManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;

public class GeneraCedulasBusinessLogic extends DataSourceManager {

	public GeneraCedulasBusinessLogic(String jniName) {
		super.init(jniName);
	}

	public String generacedulasSIIWEB(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantilla) throws Exception {
		Connection conn = null;
		String file = null;
		
		try {
			conn = getConnection();
			String nMes= req.getParameter("nMes");
			String nCedula = req.getParameter("nCedula");
			String nfolio = req.getParameter("nFolioeCeroSeis");
			
			file = cedulasSIIWEBManager.generacedulasSIIWEB(conn, nfolio, nMes, nCedula, plantilla);
			
			File f = new File(file);
			resp.setContentType("application/vnd.ms-excel");
			resp.addHeader("Content-Disposition", "inline; filename=\"" + f.getName() + "\"; ");

			ServletOutputStream out = resp.getOutputStream();

			Util.doDownload(out, file, file, ".xls");

			out.flush();
			out.close();
			f = null;
		} finally {
			CloseObject.closeObject(conn, false);
			if (file != null) {
				File f = new File(file);
				if (!f.delete())
					f.deleteOnExit();
			}
		}
		return file;

	}

}
