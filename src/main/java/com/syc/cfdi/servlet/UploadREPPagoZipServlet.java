package com.syc.cfdi.servlet;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.syc.cfdi.ComponentesFactura;
import com.syc.cfdi.FacturaBusinessLogic;
import com.syc.cfdi.core.ExtraccionFacturas;
import com.syc.cfdi.utils.FacturaUtils;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;

public class UploadREPPagoZipServlet extends HttpServlet implements GestionInterface {
	private static final long	serialVersionUID	= -8876688669675838892L;
	private String				jniName				= "";
	private static final Logger	log					= Logger.getLogger(UploadREPPagoZipServlet.class);
	private static final String	TEMP_DIR			= System.getProperty("java.io.tmpdir");

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		HttpSession session = req.getSession(false);
		String msgRetorno = "";
		Usuario u = null;

		boolean validaContraSAT = false;
		boolean notificaFacturasInvalidasSAT = false;
		boolean notificaFacturasEFA = false;

		if (session == null) {
			msgRetorno = "Su sesion a terminado. Por favor ingrese nuevamente al sistema.";
		} else {
			u = (Usuario) session.getAttribute(ATT_USER);
			if (u == null) {
				msgRetorno = "Su sesion a terminado. Por favor ingrese nuevamente al sistema.";
			}
		}
		Map<String,String> infoPago = new HashMap<String, String>();
		if ("".equals(msgRetorno)) {

			ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic(jniName);

			validaContraSAT = "S".equalsIgnoreCase(cabl.getSystemSetting("ACTIVA_VALIDACION_SAT"));
			notificaFacturasEFA = "S".equalsIgnoreCase(cabl.getSystemSetting("NOTIFICA_ERROR_VALIDACION_EFA"));
			notificaFacturasInvalidasSAT = "S".equalsIgnoreCase(cabl.getSystemSetting("NOTIFICA_ERROR_VALIDACION_SAT"));

			List<?> fileItems = null;
			Iterator<?> iter = null;
			DataInputStream archivoCargaStream = null;
			String nombreDestino = "";
			
			try {
				fileItems = Util.parseRequest(req, TEMP_DIR, -1);
				iter = fileItems.iterator();
				String nombreArchivo = "";

				while (iter.hasNext()) {
					FileItem item = (FileItem) iter.next();

					if (!item.isFormField()) {
						archivoCargaStream = new DataInputStream(item.getInputStream());
						nombreArchivo = item.getName();
						String extension = Util.getFileExtencion(nombreArchivo);

						if (!"zip".equalsIgnoreCase(extension))
							throw new Exception("No se puede procesar archivos [" + extension + "] Corrija e intente de nuevo");

						log.info("Copiando archivo :" + nombreArchivo);
						nombreDestino = FacturaUtils.generaNombreZip(TEMP_DIR, extension);
						Util.copiaArchivo(archivoCargaStream, nombreDestino);

						item.delete();
					}else {
						infoPago.put(item.getFieldName(),item.getString());
					}
					
				}
				
				FacturaBusinessLogic fbl = new FacturaBusinessLogic(jniName, validaContraSAT, u);
				fbl.setNotificaErroresSAT(notificaFacturasInvalidasSAT);
				fbl.setNotificaErroresEFA(notificaFacturasEFA);
				fbl.setPermiteVersionAnterior(false);
				fbl.setUsuario( u );
				ExtraccionFacturas ef = fbl.extraeComprobantesPago(nombreDestino);

				if (ef.getErrores().size() == 0) {
					Map<String, ComponentesFactura> comprobantesPago = ef.getFacturas();
					fbl.insertaComprobantesDePago(comprobantesPago, infoPago.get("tipoContrato"), infoPago.get("tipoPago"),Integer.parseInt( infoPago.get("folioPago") ) );
				} else {
					String token = "";
					for (int i = 0; i < ef.getErrores().size(); i++) {
						msgRetorno += token + ef.getErrores().get(i);
						token = "\n";
					}
					throw new Exception(msgRetorno);
				}
				msgRetorno = "Archivo cargado exitosamente";
			} catch (Exception e) {
				log.error(e, e);
				msgRetorno = "Ocurrio el siguiente error al cargar el archivo:" + e.getMessage();
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
		} 

		session.setAttribute("RESULT", msgRetorno);
		resp.sendRedirect(
					"procesos/adjuntaREPPago.jsp?tipoPago=" 
							+ 
								( StringUtils.isBlank( infoPago.get("tipoPago") ) ? "" :
									infoPago.get("tipoPago") 
								)
							+
							"&folioPago="
							+
							( StringUtils.isBlank( infoPago.get("folioPago") ) ? "" :
								infoPago.get("folioPago") 
							)
									
				);

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
	}

}
