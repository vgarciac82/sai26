package com.syc.reportes;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.reportes.reporteSIIWebFlujoEfectivoManager;
import com.syc.gestion.reportes.reportes;
import com.syc.gestion.util.Util;
import com.syc.reportes.core.ReporteAC01Manager;
import com.syc.sai.contabilidad.utils.db.CloseObject;

@SuppressWarnings("unused")
public class ReporteAC01BusinessLogic extends DataSourceManager {

	public ReporteAC01BusinessLogic(String jniName) {
		super.init(jniName);
	}


public String generaReporte(HttpServletRequest req, HttpServletResponse resp, Map<String, String>  plantillas) throws Exception {
		Connection conn = null;
		String fecha = req.getParameter("fecha_fin");
		String anio = req.getParameter("anio");
		int cAnio = Integer.parseInt(anio);

		try {
			conn = getConnection();			
			String ingreso = req.getParameter("TIPO_INGRESO");
			return ReporteAC01Manager.generaReporte(conn, cAnio,  fecha,  ingreso, plantillas);						
			
		} finally {
			conn.commit();
			CloseObject.closeObject(conn, false);
		}
	}	
}
