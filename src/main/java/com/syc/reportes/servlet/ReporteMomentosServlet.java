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
import com.syc.reportes.ReporteMomentosBusinessLogic;

public class ReporteMomentosServlet extends HttpServlet implements GestionInterface {

	private static final long			serialVersionUID	= 6723127616859732249L;
	private static final Logger			log					= Logger.getLogger(ReporteMomentosServlet.class);
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
	   
		String tipoReporte = req.getParameter("TIPO_REPORTE");	
		String tipoReporte2 = req.getParameter("TIPO_REPORTE2");
		String tipoReporte3 = req.getParameter("TIPO_REPORTE3");
		String tipoReporte4 = req.getParameter("TIPO_REPORTE4");
		String reportPath = getServletContext().getRealPath("Reportes" + File.separator + tipoReporte);
		String reportPath2 = getServletContext().getRealPath("Reportes" + File.separator + tipoReporte2);
		String reportPath3 = getServletContext().getRealPath("Reportes" + File.separator + tipoReporte3);
		String reportPath4 = getServletContext().getRealPath("Reportes" + File.separator + tipoReporte4);
		String ruta = getServletContext().getRealPath("Reportes" + File.separator);		

		if (StringUtils.isEmpty(tipoReporte))
			throw new ServletException("No se recibio el parametro TIPO_REPORTE");

		ReporteMomentosBusinessLogic rrs = new ReporteMomentosBusinessLogic(jniName);
		try {
			if ("2".equals(tipoReporte))
				rrs.generaReporteMasivo(req, resp, plantillas);		
			else if("3".equals(tipoReporte))
				rrs.generaReporteMasivoPorSolicitud(req, resp, reportPath, reportPath2, ruta);
			else //ReporteMomentosSolicitud.jaspe
				rrs.generaReportePorSolicitud(req, resp, reportPath, reportPath2, reportPath3, reportPath4, ruta);
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

			plantillas.put("MASIVO", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_ReporteMomentosMasivo.xlsx"));			
			}									
		}

	}
}
