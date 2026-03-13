package com.syc.reportes.servlet;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.reportes.ReporteLibrosBusinessLogic;

public class ReporteLibrosServlet extends HttpServlet implements GestionInterface {

	private static final long			serialVersionUID	= 6723127616859732249L;
	private static final Logger			log					= Logger.getLogger(ReporteLibrosServlet.class);
	private static String				jniName				= "jdbc/gestion";
	private static Map<String, String>	plantillas			= null;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		HttpSession session = req.getSession(false);
		if (session == null)
			throw new ServletException("Su session a caducado");

		Usuario u = (Usuario) session.getAttribute(ATT_USER);
		if (u == null)
			throw new ServletException("Su session a caducado");
	   
		String tipoReporte = req.getParameter("tipo_reporte");
		String reportPath = getServletContext().getRealPath("Reportes" + File.separator + tipoReporte);
		String ruta = getServletContext().getRealPath("Reportes");
		String fecha = "";
		int anio = 0;
		int mes = 0;
		
		if ("libroMayor.jasper".equals(tipoReporte) || "libroDiario.jasper".equals(tipoReporte) || "libroDeInventarios.jasper".equals(tipoReporte)){		
			fecha = req.getParameter("fecha_inicio");
			anio =  Integer.parseInt(fecha.substring(6,10));
			mes = Integer.parseInt(fecha.substring(3,5));
		}
		
		if (StringUtils.isEmpty(tipoReporte))
			throw new ServletException("No se recibio el parametro TIPO_REPORTE");

		ReporteLibrosBusinessLogic rrs = new ReporteLibrosBusinessLogic(jniName);
		try {
			if ("libroMayor.jasper".equals(tipoReporte) || "libroDiario.jasper".equals(tipoReporte))				
				rrs.generaLibros(req, resp, reportPath, ruta, anio, mes);		
			else if("libroDeInventarios.jasper".equals(tipoReporte))
				rrs.generaLibroDeInventarios(req, resp, reportPath, ruta, anio, mes);
			else if ("PlanDecuentas.jasper".equals(tipoReporte))
				rrs.generaPlan(req, resp, reportPath, ruta);
		} catch (Exception e) {
			log.error(e, e);
			throw new ServletException(e);
		}
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		synchronized (this) {
			if (plantillas == null){
				plantillas = new HashMap<String, String>();

			plantillas.put("MASIVO", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_ReporteMomentosMasivo.xls"));			
			}									
		}

	}
}
