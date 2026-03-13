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

import org.apache.log4j.Logger;

import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.reportes.ReporteSipotBusinessLogic;

public class ReporteSipotServlet extends HttpServlet implements GestionInterface {

	private static final long			serialVersionUID	= 6723127616859732249L;
	private static final Logger			log					= Logger.getLogger(ReporteSipotServlet.class);
	private static String				jniName				= "jdbc/gestion";
	private static Map<String, String>	plantillas			= null;
	// private static Boolean tipo_plantilla = false;

	// @SuppressWarnings("unused")
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		HttpSession session = req.getSession(false);
		String tipoFormato = req.getParameter("excel");
		String reporte = req.getParameter("TIPO_REPORTE");
		String ruta = getServletContext().getRealPath("Reportes");
	
		if (session == null)
			throw new ServletException("Su session a caducado");

		Usuario u = (Usuario) session.getAttribute(ATT_USER);
		if (u == null)
			throw new ServletException("Su session a caducado");

		ReporteSipotBusinessLogic rrs = new ReporteSipotBusinessLogic(jniName);
		try {
			if ("excel".equals(tipoFormato)) {
				rrs.generaReporteSipotVarios(req, resp, plantillas);
			} else {
				reporte = reporte + ".jasper";
				String reportPath = getServletContext().getRealPath("Reportes" + File.separator + reporte);
				rrs.reporteSipot(req, resp, reportPath, ruta);
			}
		} catch (Exception e) {
			log.error(e, e);
			throw new ServletException(e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession(false);
		String tipoFormato = req.getParameter("excel");
		String reporte = req.getParameter("TIPO_REPORTE");
		String ruta = getServletContext().getRealPath("Reportes");
	
		if (session == null)
			throw new ServletException("Su session a caducado");

		Usuario u = (Usuario) session.getAttribute(ATT_USER);
		if (u == null)
			throw new ServletException("Su session a caducado");

		ReporteSipotBusinessLogic rrs = new ReporteSipotBusinessLogic(jniName);
		try {
			if ("excel".equals(tipoFormato)) {
				rrs.generaReporteSipotVarios(req, resp, plantillas);
			} else {
				reporte = reporte + ".jasper";
				String reportPath = getServletContext().getRealPath("Reportes" + File.separator + reporte);
				rrs.reporteSipot(req, resp, reportPath, ruta);
			}
		} catch (Exception e) {
			log.error(e, e);
			throw new ServletException(e);
		}
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		synchronized (this) {
			if (plantillas == null) {
				plantillas = new HashMap<String, String>();
				plantillas.put("FLUJOOBGT", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_flujoOBGT.xls"));//
				plantillas.put("FLUJOCA", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_flujoCE.xls"));//
				plantillas.put("FLUJOCAA", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_flujoCEA.xls"));//
				plantillas.put("Flujoecon", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_flujoEcon.xls"));//
				plantillas.put("FLUJOFUNC", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_flujoFunc.xls"));//
				plantillas.put("FLUJOPROG", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_FlujoPrograma.xls"));//
				plantillas.put("FLUJOBGTCE", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_flujoOBGTCE.xls"));//
				plantillas.put("FLUJOCFPE", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_FlujoCFPE.xls"));//
				plantillas.put("FLUJOCFPEA", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_FlujoCFPEArmonizado.xls"));//
				plantillas.put("FLUJOIFE", getServletContext().getRealPath("Reportes" + File.separator + "plantilla_ingresosFlujoEfectivo.xls"));//
				plantillas.put("FLUJOEFE", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_flujoEfectivoEgresos.xls"));//
				plantillas.put("ANALITING", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_Analitico_Ingreso.xls"));//
				plantillas.put("ANALITING2", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_Analitico_Ingreso2.xls"));//
				plantillas.put("AvanceFinancieroProg", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_proyectos_inversion.xls"));//
				plantillas.put("AvanceFin2", getServletContext().getRealPath("Reportes" + File.separator + "Plantilla_proyectos_inversion2.xls"));//
			}

		}

	}
}
