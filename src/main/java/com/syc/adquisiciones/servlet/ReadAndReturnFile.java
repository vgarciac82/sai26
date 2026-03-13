package com.syc.adquisiciones.servlet;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

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

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.syc.adquisiciones.core.DatosArchivo;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.util.Util;


public class ReadAndReturnFile extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log=Logger.getLogger( ReadAndReturnFile.class );
	private static String tempDir="";
	private static String jniName = null;
	private JSONArray arrayObj=null;
	private JSONObject jsonObj=null;
    public ReadAndReturnFile() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding( "UTF-8" );
		HttpSession session = request.getSession(false);
		String cCentroContable = "";
		if (session == null) {
			log.warn("No hay sesion");
			response.sendRedirect("../index.jsp");
			return;
		}
		Usuario u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
		if (u == null)
			throw new ServletException("Su session a caducado");
		if (u.getPropiedades() != null && u.getPropiedades().containsKey("CCENTROCONTABLE")) {
			cCentroContable = u.getPropiedad("CCENTROCONTABLE").getValor();
		}
		
		if (cCentroContable.isEmpty() || cCentroContable.equals("")) {
			throw new ServletException("Error: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion presupuestal, Consulte a su administrador.");
		}
		List<?> fileItems = null;
		Iterator<?> iter = null;
		DatosArchivo datosArchivo=null;
		
		File file = null;
		String file_name = System.getProperty( "java.io.tmpdir" ) + File.separatorChar  + "reporteExample" + System.currentTimeMillis() + "_" + String.valueOf( ( int ) ( Math.random() * 100 ) ) + ".xlsx";
		ServletOutputStream out = null;
		ServletContext context = getServletConfig().getServletContext();
		String mimetype = null;
		boolean resp=false;
		String msg="";
		try {
			fileItems = Util.parseRequest(request, ReadAndReturnFile.tempDir, -1);
			iter = fileItems.iterator();
			datosArchivo=fillObject( iter );
			arrayObj = new JSONArray();
			jsonObj = new JSONObject();
			switch (datosArchivo.getnOperacion()) {
				case 1://Cargar de de contrataciones del CAAS
					log.info( "Inicia la carga de contrataciones del CAAS por layout." );
					if (!"xls".equalsIgnoreCase(datosArchivo.getcExtencion()) && !"xlsx".equalsIgnoreCase(datosArchivo.getcExtencion())){
						throw new Exception("No se puede procesar archivos [" + datosArchivo.getcExtencion() + "] Corrija e intente de nuevo.");
					}
					Util.copiaArchivo( datosArchivo.getArchivoStream(), file_name );
					file = new File( file_name );
					mimetype = context.getMimeType( file.getName() );
					resp=true;
					msg="Todo bien";
					break;
				default:
					log.warn( "Operación desconocida en la carga de archivos." );
			}
			if ( resp ) {
				response.setContentType( ( mimetype != null ) ? mimetype : "application/octet-stream" );
				response.addHeader( "Content-Disposition", "inline; filename=\"" + file.getName() + "\"; " );

				out = response.getOutputStream();
				Util.doDownload( out, file.getAbsolutePath(), file.getName(), mimetype );
			}
		} catch ( Exception e ) {
			log.error( e );
			msg=e.getMessage().toString();
		}finally {
			try {
				jsonObj.put("MSG",msg);
				jsonObj.put("ISCORRECT",resp);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			cCentroContable=null;
			u=null;
			datosArchivo=null;
			iter = null;
			fileItems = null;
			String destino = new String(arrayObj.put(jsonObj).toString().getBytes("UTF-8"),"ISO-8859-1") ;
			out.flush();
			if ( out != null ) {
				out.close();
			}
			if ( !file.delete() )
				file.deleteOnExit();
			
			context=null;
			mimetype = null;
			out = null;
		}
	}
	public DatosArchivo fillObject(Iterator<?> iter) throws Exception{
		DatosArchivo datosArchivo=new DatosArchivo();
		datosArchivo.setJniName( jniName );
		while (iter.hasNext()) {
			FileItem item = (FileItem) iter.next();
			if (item.isFormField()) {
				if ("operacion".equals(item.getFieldName())){
					datosArchivo.setnOperacion( (null==item.getString() || "".equals(item.getString()))?0:Integer.parseInt(item.getString()) );
				}
				item.delete();
				continue;
			}else{
				datosArchivo.setArchivoStream(item.getInputStream());
				datosArchivo.setcNombreArchivo(item.getName());
				datosArchivo.setcExtencion(Util.getFileExtencion(item.getName()));
			}
		}
		return datosArchivo;
	}
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		tempDir = config.getInitParameter("tempDir");
		if (tempDir == null) {
			tempDir = config.getServletContext().getRealPath("/") + ".." + File.separator + "upload" + File.separator;
			File fDir = new File(tempDir);
			if (!fDir.exists())
				if (!fDir.mkdirs())
					throw new ServletException("No se pudo crear el directorio " + tempDir);
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
	}

}
