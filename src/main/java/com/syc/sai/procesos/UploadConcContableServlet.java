package com.syc.sai.procesos;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;

import com.syc.cfdi.utils.FacturaUtils;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.sai.procesosAutomaticos.UploadConciliacionBusinessLogic;
import common.Logger;

public class UploadConcContableServlet extends HttpServlet implements GestionInterface {

	private static final long	serialVersionUID	= 126320606200256721L;
	private static final Logger	log					= Logger.getLogger(UploadConcContableServlet.class);
	private String				jniName				= "";
	private String				folioGenerator;
	private static final String	TEMP_DIR			= System.getProperty("java.io.tmpdir") + File.separatorChar + "ConBanTMP" + File.separatorChar;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		HttpSession session = req.getSession(false);
		String msgRetorno = "";
		Usuario u = null;

		if (session == null) {
			msgRetorno = "Su sesion a terminado. Por favor ingrese nuevamente al sistema.";
		} else {

			u = (Usuario) session.getAttribute(ATT_USER);
			if (u == null) {
				msgRetorno = "Su sesion a terminado. Por favor ingrese nuevamente al sistema.";
			}

		}

		if ("".equals(msgRetorno)) {

			/* Variables para la carga de archivo. */
			List<?> fileItems = null;
			Iterator<?> iter = null;
			DataInputStream archivoCargaStream = null;
			String nombreDestino = "";

			try {

				fileItems = Util.parseRequest(req, TEMP_DIR, -1);
				iter = fileItems.iterator();

				int conciliacion = -1;
				int nMes = -1;
				String nombreCarpeta = "";
				String nombreArchivo = "";

				while (iter.hasNext()) {

					FileItem item = (FileItem) iter.next();

					/* Lee los elementos del formulario */
					if (item.isFormField()) {
						if ("nombreCarpeta".equals(item.getFieldName()))
							nombreCarpeta = item.getString();
						else if ("nMes".equals(item.getFieldName()))
							nMes = Integer.parseInt(item.getString());
						else if ("conciliacion".equals(item.getFieldName()))
							conciliacion = Integer.parseInt(item.getString());

						item.delete();

						continue;

					} else {

						/* Carga el archivo */
						archivoCargaStream = new DataInputStream(item.getInputStream());
						nombreArchivo = item.getName();
						String extension = Util.getFileExtencion(nombreArchivo);

						nombreDestino = FacturaUtils.generaNombreArchivoTemporal(TEMP_DIR, nombreArchivo, extension);

						log.info("Copiando archivo: [" + nombreArchivo + "] a [" + nombreDestino + "]");
						Util.copiaArchivo(archivoCargaStream, nombreDestino);

						item.delete();
						break;

					}
				}

				UploadConciliacionBusinessLogic ucbl = new UploadConciliacionBusinessLogic(jniName, folioGenerator);
				msgRetorno = ucbl.uploadConciliacion(u, nombreCarpeta, nombreDestino, nMes, conciliacion);

			} catch (Exception e) {
				log.error(e, e);
				msgRetorno = "Ocurrio el siguiente error al cargar el archivo: <br>" + e.getMessage();
			} finally {
				if (archivoCargaStream != null)
					try {
						archivoCargaStream.close();
					} catch (Exception e) {
						log.error("Error cerrando flujo DataInputStream" + e);
					}
				archivoCargaStream = null;

				if (!"".equals(nombreDestino)) {
					File toDelete = new File(nombreDestino);
					if (!toDelete.delete())
						toDelete.deleteOnExit();
				}
			}
		} else {
			resp.sendRedirect("Generador/CargaCB.jsp?msgError=" + msgRetorno);
		}

		session.setAttribute("RESULT", msgRetorno);
		resp.sendRedirect("Generador/CargaCB.jsp?RESPUESTA=S");
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
			File f = new File(TEMP_DIR);
			if (!f.exists())
				if (!f.mkdirs())
					throw new Exception("No se logro crear la carpeta temporal " + TEMP_DIR + " . Notifique a soporte.");
		} catch (Exception e) {
			throw new ServletException(e);
		}
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
			folioGenerator = (String) ic.lookup("java:comp/env/folioGeneratorInterface");

			if (folioGenerator == null) {
				folioGenerator = "com.syc.gestion.custom.DefaultFolioGenerator";
				log.info("Environment Entry \"folioGeneratorInterface\" nula usando default \"" + folioGenerator + "\"");
			} else
				log.info("folioGeneratorInterface=" + folioGenerator);
		} catch (NamingException exc) {
			folioGenerator = "com.syc.gestion.custom.DefaultFolioGenerator";
			log.info("Environment Entry \"folioGeneratorInterface\" no definida usando default \"" + folioGenerator + "\"");
		}

	}
}
