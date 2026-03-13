package com.syc.sai.contabilidad.servlet;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
import org.apache.log4j.Logger;

import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.CargaPolizaManualBusinessLogic;



public class CargaPolizaManualServlet extends HttpServlet   {

	private static final Logger	log					= Logger.getLogger(CargaPolizaManualServlet.class);
	private static final long	serialVersionUID	= 1L;

	/**
	 * Directorio temporal donde se almacenara el archivo de carga.
	 */
	private static String		TEMP_DIR			= "";

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String FolioDocumento=getValor( req.getParameter("folioDocumento"));
		String CentroContable=getValor(req.getParameter("cCentroContable"));
		String Usuario=getValor(req.getParameter("Usuario"));
		String FechaCaptura= getValor(req.getParameter("fechaCaptura"));
		String FechaAplicacion= getValor(req.getParameter("fechaAplicacion"));
		String UnidadResponsable= getValor(req.getParameter("UnidadResponsable"));
		String Mes= getValor(req.getParameter("Mes"));
		String EjercicioFiscal= getValor(req.getParameter("EjerFisc"));
		String Concepto= getValor(req.getParameter("ConceptoPolizas"));
		String CasoOrigen= getValor(req.getParameter("Id_Caso"));
	

		
		
		String mensajeRetorno = "";

		String accion = req.getRequestURI().indexOf("CargaMasiva") > 0 ? "CargaMasiva" : "CapturaManual"; // ExportaExcel
		HttpSession session = req.getSession(false);
		if (session == null) {
			log.warn("Peticion sin sesion o sesion invalida");
			resp.sendRedirect("../index.jsp");
			return;
		}
		Usuario u = (Usuario) session.getAttribute("usuario");
		if (u == null) {
			log.warn("Peticion sin usuario o sesion invalida");
			resp.sendRedirect("../index.jsp");
			return;
		}
		if ("CargaMasiva".equals(accion)) {
			List<?> fileItems = null;
			Iterator<?> iter = null;
			InputStream archivoCargaIS = null;
			DataInputStream archivoCargaStream = null;
			String nombreDestino = "";

			try {
				fileItems = Util.parseRequest(req, CargaPolizaManualServlet.TEMP_DIR, -1);
				iter = fileItems.iterator();
				String nombreArchivo = "";
				CargaPolizaManualBusinessLogic cpmbl = new CargaPolizaManualBusinessLogic(u.getLogin());

				while (iter.hasNext()) {

					FileItem item = (FileItem) iter.next();

					if (item.isFormField())
						continue;

					archivoCargaIS = item.getInputStream();
					archivoCargaStream = new DataInputStream(item.getInputStream());
					nombreArchivo = item.getName();
					String extension = Util.getFileExtencion(nombreArchivo);

					if (!"xls".equalsIgnoreCase(extension))
						throw new Exception("No se puede procesar archivos [" + extension + "] Corrija e intente de nuevo");

					nombreDestino = CargaPolizaManualServlet.TEMP_DIR + "CARGA_POLIZAMANUAL_" + System.currentTimeMillis() + "." + extension;

					log.info("Copiando archivo :" + nombreArchivo);
					Util.copiaArchivo(archivoCargaStream, nombreDestino);

					item.delete();

					log.debug("Procesando archivo:" + nombreArchivo);
	
					List<String> mensajes = cpmbl.cargaExcelPolizaManual(nombreDestino,FolioDocumento,CentroContable,EjercicioFiscal);
	
					if(mensajes.size()>0){
						
						for( int i = 0; i < mensajes.size(); i++)
							mensajeRetorno += " " + mensajes.get(i).replace("'", "" ).replace('"', ' ' ) ;
						if(mensajeRetorno.length()>100)
							mensajeRetorno="El archivo tiene demasiados errores en cuentas y subcuentas, favor de verificar antes de cargarlo ";
						else
						mensajeRetorno = "Error de carga: "+mensajeRetorno;
						
						cpmbl.generaRegistroDocPoliza("NO",Usuario,FolioDocumento,CentroContable,FechaCaptura,FechaAplicacion,UnidadResponsable,Mes,EjercicioFiscal,mensajeRetorno,CasoOrigen);
						
					}else
					{

						mensajeRetorno = "Archivo cargado exitosamente";
						cpmbl.generaRegistroDocPoliza("SI",Usuario,FolioDocumento,CentroContable,FechaCaptura,FechaAplicacion,UnidadResponsable,Mes,EjercicioFiscal,Concepto,CasoOrigen);
					}
					/*
					 * Solo se espera un archivo por carga, por lo que al leerlo
					 * no es necesario continuar con el ciclo.
					 */

					break;
				}
			} catch (Exception exc) {
				log.error(exc, exc);
				mensajeRetorno = "No se pudo procesar el excel debido al siguiente error:\\n" + exc;
			} finally {
				if (archivoCargaStream != null)
					try {
						archivoCargaStream.close();
					} catch (Exception e) {
						log.error("Error cerrando flujo DataInputStream" + e);
					}
				if (archivoCargaIS != null)
					try {
						archivoCargaIS.close();
					} catch (Exception e) {
						log.error("Error cerrando flujo InputStream" + e);
					}
				archivoCargaIS = null;
				archivoCargaStream = null;

				if (!"".equals(nombreDestino)) {
					File toDelete = new File(nombreDestino);
					if (!toDelete.delete())
						toDelete.deleteOnExit();
				}

			}
			
			session.setAttribute("folio",FolioDocumento );
			session.setAttribute(GestionInterface.ATT_FOLIOPOLIZA,FolioDocumento );
			session.setAttribute("MENSAJE_CARGA", mensajeRetorno);
			
			log.info(mensajeRetorno);
			resp.sendRedirect("../../Generador/PolizasEvento.jsp");
			}// fin del if de accion
	}// fin del DoPost

	
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

		
	}
	
	private String getValor(String val) {
		if (val == null)
			return "";
		else
			return new String(val.trim());
	}

}
