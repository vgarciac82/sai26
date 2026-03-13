package com.syc.gestion.reportes.servlet;

import java.io.File;
import java.io.IOException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.syc.gestion.core.Usuario;
import com.syc.gestion.reportes.anteproyecto.ReporteTomaDeDesicionBusinessLogic;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.servlet.ResponseSender;

/**
 * Reporte para la toma de desicion.
 * 
 * @author Vicente Garcia Carrillo
 * 
 */
public class ReporteTomaDeDesicionServlet extends HttpServlet implements GestionInterface {

	/**
	 * Serial Generado.
	 */
	private static final long	serialVersionUID	= -8746272634947908587L;

	private static final Logger	log					= Logger.getLogger(ReporteTomaDeDesicionServlet.class);
	/**
	 * Directorio temporal donde se almacenara el archivo de carga.
	 */
	private static String		TEMP_DIR			= "";

	private String				jniName;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = null;
		session = req.getSession(false);
		if (session == null) {
			resp.sendRedirect("../index.jsp");
			return;
		}

		Usuario u = (Usuario) session.getAttribute(ATT_USER);
		if (u == null) {
			resp.sendRedirect("../index.jsp");
			return;
		}

		String accion = req.getParameter("accion");
		if ("DESCARGA_EXCEL".equals(accion)) {

			ServletOutputStream out = null;
			File fOut = null;

			try {
				ReporteTomaDeDesicionBusinessLogic rtdbl = new ReporteTomaDeDesicionBusinessLogic();

				fOut = rtdbl.generaArchivoInformacionCongelada();

				if (fOut != null) {
					ServletContext context = getServletConfig().getServletContext();
					String mimetype = context.getMimeType(fOut.getName());

					out = resp.getOutputStream();
					resp.setContentType((mimetype != null) ? mimetype : "application/octet-stream");
					resp.setContentLength((int) fOut.length());
					resp.addHeader("Content-Disposition", "inline; filename=\"" + fOut.getName() + "\";");

					Util.doDownload(resp, fOut.getAbsolutePath(), fOut.getName(), mimetype);

					if (!fOut.delete())
						fOut.deleteOnExit();
				}
			} catch (Exception e) {
				log.error("Error realizando descarga de archivo de proyecto " + e, e);
				try {
					if (out == null)
						out = resp.getOutputStream();
					out.print("Ocurrio el siguiente error al intentar descargar el archivo de proyecto: " + e);
				} catch (Exception e2) {
					log.fatal("Ocurrio un error al intentar notificar al usuario: " + e2, e2);
				}

			} finally {
				out.flush();
				out.close();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = null;
		session = req.getSession(false);
		if (session == null) {
			resp.sendRedirect("../index.jsp");
			return;
		}

		Usuario u = (Usuario) session.getAttribute(ATT_USER);
		if (u == null) {
			resp.sendRedirect("../index.jsp");
			return;
		}

		String accion = req.getParameter("accion");

		if ("CONGELA_INFO".equals(accion)) {
			try {
				ReporteTomaDeDesicionBusinessLogic rtdbl = new ReporteTomaDeDesicionBusinessLogic(jniName);
				int afectados = rtdbl.congelaInformacion();
				ResponseSender.sendResult(resp, String.valueOf(afectados));
			} catch (Exception e) {
				log.error(e, e);
				ResponseSender.sendError(resp, "Error congelando informacion: " + e);
			}
		} else if ("GENERA_REPORTE".equals(accion)) {
			String tipoReporte = req.getParameter("TIPO_REPORTE");
			String nombreDestino = TEMP_DIR;
			String[] momentosPresupuestal = req.getParameterValues("MOMENTO_PRESUPUESTAL");
			String[] capitulos = req.getParameterValues("CAPITULO");
			String mesCorte = req.getParameter("MesCorte");
			ServletOutputStream out = resp.getOutputStream();
			File fOut = null;

			try {

				if ("CE".equals(tipoReporte)) {

					nombreDestino += "REPORTE_CE.xls";
					ReporteTomaDeDesicionBusinessLogic rtdbl = new ReporteTomaDeDesicionBusinessLogic(jniName);
					fOut = rtdbl.generaReporteEstructuraEconomica(nombreDestino, mesCorte, (momentosPresupuestal == null ? new String[0] : momentosPresupuestal), (capitulos == null ? new String[0] : capitulos));

					ServletContext context = getServletConfig().getServletContext();
					String mimetype = context.getMimeType(fOut.getName());
					Util.doDownload(resp, fOut.getAbsolutePath(), fOut.getName(), mimetype);

					if (!fOut.delete())
						fOut.deleteOnExit();

				} else if ("SFN".equals(tipoReporte)) {
					nombreDestino += "REPORTE_SFN.xls";
					ReporteTomaDeDesicionBusinessLogic rtdbl = new ReporteTomaDeDesicionBusinessLogic(jniName);
					fOut = rtdbl.generaReporteSubFuncion(nombreDestino, mesCorte, (momentosPresupuestal == null ? new String[0] : momentosPresupuestal), (capitulos == null ? new String[0] : capitulos));

					ServletContext context = getServletConfig().getServletContext();
					String mimetype = context.getMimeType(fOut.getName());
					Util.doDownload(resp, fOut.getAbsolutePath(), fOut.getName(), mimetype);

					if (!fOut.delete())
						fOut.deleteOnExit();

				} else if ("UE".equals(tipoReporte)) {
					nombreDestino += "REPORTE_UE.xls";
					ReporteTomaDeDesicionBusinessLogic rtdbl = new ReporteTomaDeDesicionBusinessLogic(jniName);
					fOut = rtdbl.generaReporteUE(nombreDestino, mesCorte, (momentosPresupuestal == null ? new String[0] : momentosPresupuestal), (capitulos == null ? new String[0] : capitulos));

					ServletContext context = getServletConfig().getServletContext();
					String mimetype = context.getMimeType(fOut.getName());
					Util.doDownload(resp, fOut.getAbsolutePath(), fOut.getName(), mimetype);

					if (!fOut.delete())
						fOut.deleteOnExit();
				} else if ("UN".equals(tipoReporte)) {
					nombreDestino += "REPORTE_UN.xls";
					ReporteTomaDeDesicionBusinessLogic rtdbl = new ReporteTomaDeDesicionBusinessLogic(jniName);
					fOut = rtdbl.generaReporteUN(nombreDestino, mesCorte, (momentosPresupuestal == null ? new String[0] : momentosPresupuestal), (capitulos == null ? new String[0] : capitulos));
					ServletContext context = getServletConfig().getServletContext();
					String mimetype = context.getMimeType(fOut.getName());
					Util.doDownload(resp, fOut.getAbsolutePath(), fOut.getName(), mimetype);

					if (!fOut.delete())
						fOut.deleteOnExit();
				} else if ("EF".equals(tipoReporte)) {
					nombreDestino += "REPORTE_EF.xls";
					ReporteTomaDeDesicionBusinessLogic rtdbl = new ReporteTomaDeDesicionBusinessLogic(jniName);
					fOut = rtdbl.generaReporteEF(nombreDestino, mesCorte, (momentosPresupuestal == null ? new String[0] : momentosPresupuestal), (capitulos == null ? new String[0] : capitulos));

					ServletContext context = getServletConfig().getServletContext();
					String mimetype = context.getMimeType(fOut.getName());
					Util.doDownload(resp, fOut.getAbsolutePath(), fOut.getName(), mimetype);

					if (!fOut.delete())
						fOut.deleteOnExit();
				}

			} catch (Exception e) {
				log.error("Error realizando descarga de archivo de proyecto " + e, e);
				try {
					if (out == null)
						out = resp.getOutputStream();
					out.print("Ocurrio el siguiente error al intentar descargar el archivo de proyecto: " + e);
				} catch (Exception e2) {
					log.fatal("Ocurrio un error al intentar notificar al usuario: " + e2, e2);
				}
			} finally {
				out.flush();
				out.close();
			}

		}

	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		try {
			InitialContext ic = new InitialContext();
			jniName = (String) ic.lookup("java:comp/env/dataSourceRefName");

			if (jniName == null) {
				jniName = "jdbc/gestion";
				log.info("Environment Entry \"dataSourceRefName\" nula usando default \"" + jniName + "\"");
			} else
				log.info("dataSourceRefName=" + jniName);
		} catch (NamingException exc) {
			jniName = "jdbc/gestion";
			log.info("Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
		}
		try {
			InitialContext ic = new InitialContext();
			TEMP_DIR = (String) ic.lookup("java:comp/env/TemporaryDirectory");
			if (TEMP_DIR == null) {
				TEMP_DIR = "../upload/PEF/";
				log.info("Environment Entry \"TEMP_DIR\" nula usando default \"" + TEMP_DIR + "\"");
			} else
				log.info("dataSourceRefName=" + TEMP_DIR);
		} catch (NamingException exc) {
			TEMP_DIR = "../upload/PEF/";
			log.info("Ocurrio un error que evito que se cargara la entrada \"TEMP_DIR\"" + exc);
			log.info("Environment Entry \"dataSourceRefName\" no definida usando default \"" + TEMP_DIR + "\"");
		}

		try {
			File f = new File(TEMP_DIR);
			if (!f.exists())
				if (!f.mkdirs())
					throw new Exception("No se puede crear el directorio temporal " + TEMP_DIR);
		} catch (Exception e) {
			log.error("No fue posible crear automaticamente el directorio temporal: " + TEMP_DIR + " Solicite su creacion manual");
		}
	}

}