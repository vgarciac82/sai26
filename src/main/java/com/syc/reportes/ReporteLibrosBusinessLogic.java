package com.syc.reportes;

import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.reportes.core.ReporteLibrosManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;

public class ReporteLibrosBusinessLogic extends DataSourceManager {

	public ReporteLibrosBusinessLogic(String jniName) {
		super.init(jniName);
	}

	public void generaLibroMayor(HttpServletRequest req, HttpServletResponse resp, String tipoReporte, String ruta) throws Exception {
		Connection conn = null;
		String numfirmas = (req.getParameter("chk_firmas") == null ? "3" : req.getParameter("chk_firmas"));
		try {			
			conn = getConnection();
			
			String tipoSol = req.getParameter("tipoReporte");			
			String fecha = req.getParameter("fecha_inicio");
			String nombre1 = new String(req.getParameter("nombre1").getBytes("ISO-8859-1"), "UTF-8");
			String puesto1 = new String(req.getParameter("cargo1").getBytes("ISO-8859-1"), "UTF-8");
			String nombre2 = new String(req.getParameter("nombre2").getBytes("ISO-8859-1"), "UTF-8");
			String puesto2 = new String(req.getParameter("cargo2").getBytes("ISO-8859-1"), "UTF-8");
			String nombre3 = new String(req.getParameter("nombre3").getBytes("ISO-8859-1"), "UTF-8");
			String puesto3 = new String(req.getParameter("cargo3").getBytes("ISO-8859-1"), "UTF-8");
			String nombre4 = new String(req.getParameter("nombre4").getBytes("ISO-8859-1"), "UTF-8");
			String puesto4 = new String(req.getParameter("cargo4").getBytes("ISO-8859-1"), "UTF-8");
			
			Map<String, Object> parms = new LinkedHashMap<String, Object>();
			
			parms.put("tipoSol",tipoSol);
			parms.put("fecha", fecha);
			parms.put("SUBREPORT_DIR", ruta);
			
			parms.put("nfirmas", Integer.parseInt(numfirmas,10));
			parms.put("nombre1", nombre1);
			parms.put("puesto1", puesto1);
			parms.put("nombre2", nombre2);
			parms.put("puesto2", puesto2);
			parms.put("nombre3", nombre3);
			parms.put("puesto3", puesto3);
			parms.put("nombre4", nombre4);
			parms.put("puesto4", puesto4);
			System.out.println(ruta);
			//parms.put("SUBREPORT_DIR", ruta);
			
			ReporteLibrosManager.ReporteLibros(conn, resp, tipoReporte, ruta, parms);
			
		}finally{
			CloseObject.closeObject(conn, false);
			}
	}
	
	public void generaLibros(HttpServletRequest req, HttpServletResponse resp, String tipoReporte, String ruta, int anio, int mes) throws Exception {
		Connection conn = null;
		String numfirmas = (req.getParameter("chk_firmas") == null ? "3" : req.getParameter("chk_firmas"));
		try {			
			conn = getConnection();
			
			String tipoSol = req.getParameter("tipo_reporte");			
			String fecha = req.getParameter("fecha_inicio");
			String nombre1 = new String(req.getParameter("nombre1").getBytes("ISO-8859-1"), "UTF-8");
			String puesto1 = new String(req.getParameter("cargo1").getBytes("ISO-8859-1"), "UTF-8");
			String nombre2 = new String(req.getParameter("nombre2").getBytes("ISO-8859-1"), "UTF-8");
			String puesto2 = new String(req.getParameter("cargo2").getBytes("ISO-8859-1"), "UTF-8");
			String nombre3 = new String(req.getParameter("nombre3").getBytes("ISO-8859-1"), "UTF-8");
			String puesto3 = new String(req.getParameter("cargo3").getBytes("ISO-8859-1"), "UTF-8");
			String nombre4 = new String(req.getParameter("nombre4").getBytes("ISO-8859-1"), "UTF-8");
			String puesto4 = new String(req.getParameter("cargo4").getBytes("ISO-8859-1"), "UTF-8");
			
			Map<String, Object> parms = new LinkedHashMap<String, Object>();
			
			parms.put("tipoSol",tipoSol);
			parms.put("fecha", fecha);
			parms.put("anio", anio);
			parms.put("mes", mes);
			parms.put("SUBREPORT_DIR", ruta);
			
			parms.put("nfirmas", Integer.parseInt(numfirmas,10));
			parms.put("nombre1", nombre1);
			parms.put("puesto1", puesto1);
			parms.put("nombre2", nombre2);
			parms.put("puesto2", puesto2);
			parms.put("nombre3", nombre3);
			parms.put("puesto3", puesto3);
			parms.put("nombre4", nombre4);
			parms.put("puesto4", puesto4);
			System.out.println(ruta);
			//parms.put("SUBREPORT_DIR", ruta);
			
			ReporteLibrosManager.ReporteLibros(conn, resp, tipoReporte, ruta, parms);
			
		}finally{
			CloseObject.closeObject(conn, false);
			}
	}
	
	public void generaPlan(HttpServletRequest req, HttpServletResponse resp, String tipoReporte, String ruta) throws Exception {
		Connection conn = null;
		String numfirmas = (req.getParameter("chk_firmas") == null ? "3" : req.getParameter("chk_firmas"));
		try {			
			conn = getConnection();
			
			String tipoSol = req.getParameter("tipo_reporte");						
			String nombre1 = new String(req.getParameter("nombre1").getBytes("ISO-8859-1"), "UTF-8");
			String puesto1 = new String(req.getParameter("cargo1").getBytes("ISO-8859-1"), "UTF-8");
			String nombre2 = new String(req.getParameter("nombre2").getBytes("ISO-8859-1"), "UTF-8");
			String puesto2 = new String(req.getParameter("cargo2").getBytes("ISO-8859-1"), "UTF-8");
			String nombre3 = new String(req.getParameter("nombre3").getBytes("ISO-8859-1"), "UTF-8");
			String puesto3 = new String(req.getParameter("cargo3").getBytes("ISO-8859-1"), "UTF-8");
			String nombre4 = new String(req.getParameter("nombre4").getBytes("ISO-8859-1"), "UTF-8");
			String puesto4 = new String(req.getParameter("cargo4").getBytes("ISO-8859-1"), "UTF-8");
			
			Map<String, Object> parms = new LinkedHashMap<String, Object>();
			
			parms.put("tipoSol",tipoSol);
			parms.put("SUBREPORT_DIR", ruta );
			
			parms.put("nfirmas", Integer.parseInt(numfirmas,10));
			parms.put("nombre1", nombre1);
			parms.put("puesto1", puesto1);
			parms.put("nombre2", nombre2);
			parms.put("puesto2", puesto2);
			parms.put("nombre3", nombre3);
			parms.put("puesto3", puesto3);
			parms.put("nombre4", nombre4);
			parms.put("puesto4", puesto4);
			System.out.println(ruta);
			//parms.put("SUBREPORT_DIR", ruta);
			
			ReporteLibrosManager.ReporteLibros(conn, resp, tipoReporte, ruta, parms);
			
		}finally{
			CloseObject.closeObject(conn, false);
			}
	}

	public void generaLibroDeInventarios(HttpServletRequest req, HttpServletResponse resp, String reportPath, String ruta, int anio, int mes) throws Exception {
		Connection conn = null;
		
		try {			
			conn = getConnection();
			
			String tipoSol = req.getParameter("tipo_reporte");						
			
			Map<String, Object> parms = new LinkedHashMap<String, Object>();
			
			parms.put("tipoSol",tipoSol);
			parms.put("SUBREPORT_DIR", ruta );
			parms.put("anio", anio );
			parms.put("mes", mes );
		
			System.out.println(ruta);
			
			ReporteLibrosManager.ReporteLibros(conn, resp, reportPath, ruta, parms);
			
		}finally{
			CloseObject.closeObject(conn, false);
			}
		
	}
	
}
