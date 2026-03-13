package com.syc.reportes;

import java.io.File;
import java.sql.Connection;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.util.Util;
import com.syc.reportes.core.CedulaBancosManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;

public class CedulaBancosBusinessLogic extends DataSourceManager {

	public CedulaBancosBusinessLogic(String jniName) {
		super.init(jniName);
	}

	public void generaCedulaBancos(HttpServletRequest req, HttpServletResponse resp, Map<String, String> plantillas) throws Exception {
		Connection conn = null;
		String file = null;
		try {
			conn = getConnection();
			
			int mes = Integer.parseInt(req.getParameter("nMes"));
			int anio = Integer.parseInt(req.getParameter("nAnio"));
			
			file = CedulaBancosManager.generaCedulaBancos(conn, mes, anio, plantillas);
			File f = new File(file);
			resp.setContentType("application/vnd.ms-excel");
			resp.addHeader("Content-Disposition", "inline; filename=\"" + f.getName() + "\"; ");

			ServletOutputStream out = resp.getOutputStream();

			Util.doDownload(out, file, file, "");

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

	}
}
