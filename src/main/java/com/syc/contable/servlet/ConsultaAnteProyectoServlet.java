package com.syc.contable.servlet;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.syc.contable.anteproyecto.ConsultaAnteProyectoBusinessLogic;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;

public class ConsultaAnteProyectoServlet extends HttpServlet implements GestionInterface {

	private static final Logger	log					= Logger.getLogger(ConsultaAnteProyectoServlet.class);
	private static final long	serialVersionUID	= 1L;
	/**
	 * Directorio temporal donde se almacenara el archivo de carga.
	 */
	private static String		TEMP_DIR			= "";

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		ConsultaAnteProyectoBusinessLogic cssBL = new ConsultaAnteProyectoBusinessLogic(GestionInterface.ATT_CONEXION);
		String mensajeRetorno = "";

		HttpSession session = req.getSession(false);
		if (session == null) {
			log.warn("Peticion sin sesion o sesion invalida");
			resp.sendRedirect("../index.jsp");
			return;
		}
		Usuario u = (Usuario) session.getAttribute(ATT_USER);
		if (u == null) {
			log.warn("Peticion sin usuario o sesion invalida");
			resp.sendRedirect("../index.jsp");
			return;
		}

			String operacion = req.getParameter("accion");

			if ("ExportaExcel".equals(operacion)) {
				try {
					exportaReporteExcel(req, resp);
					return;
				} catch (Exception exc) {
					log.error(exc, exc);
					mensajeRetorno = "No fue posible realizar la exportación" + exc;
				}
			}
			
			String strRedirect = "";
			try {
				strRedirect = "../../admin/ConsultaAnteProyecto.jsp?";
			} catch (Exception e) {
				e.printStackTrace();
			}
			log.debug("Redirect: " + strRedirect);
			resp.sendRedirect(strRedirect);

			log.debug("SALE  DE LA CONSULTA");
		}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
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

	public synchronized void exportaReporteExcel(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ConsultaAnteProyectoBusinessLogic cssBL = new ConsultaAnteProyectoBusinessLogic(GestionInterface.ATT_CONEXION);
		
		String cUE = request.getParameter("cUE");
		String cUN = request.getParameter("cUN");
		String cEP = request.getParameter("cEP");
		String mMontoCalculado = request.getParameter("mMontoCalculado"); 
		String mMontoOptimo = request.getParameter("mMontoOptimo");
		String mMontoIrreductible = request.getParameter("mMontoIrreductible");
		
		String file_name = "ConsultaAnteProyecto" + "_" + System.currentTimeMillis() + "_" + String.valueOf((int) (Math.random() * 100)) + ".xls";

		String cFileExcelPlantilla = getServletContext().getRealPath("Reportes" + File.separator + "plantilla_consulta_anteproyecto.xls");
		InputStream fs = new FileInputStream(cFileExcelPlantilla);
		Util.copiaArchivo(fs, file_name);
		fs.close();

		InputStream fsArchivo = new FileInputStream(file_name);

		Workbook workbook = new HSSFWorkbook(fsArchivo);
		Sheet sheet = workbook.getSheetAt(0);
		
		sheet = cssBL.consultaReporteExcel(sheet, cUE, cUN, cEP, mMontoCalculado, mMontoOptimo, mMontoIrreductible );

		fsArchivo.close();

		File fsalida = new File(file_name);

		response.setContentType("application/vnd.ms-excel");
		response.addHeader("Content-Disposition", "inline; filename=\"" + file_name + "\"; ");

		FileOutputStream fos = new FileOutputStream(fsalida);
		BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
		workbook.write(bos);

		/* Cierra Flujos */
		bos.flush();
		bos.close();
		fos.close();

		ServletOutputStream out = response.getOutputStream();

		ServletContext context = getServletConfig().getServletContext();
		String mimetype = context.getMimeType(file_name);

		Util.doDownload(out, file_name, file_name, mimetype);

		out.flush();
		out.close();
		if (!fsalida.delete()) {
			fsalida.deleteOnExit();
		}
	}

}

