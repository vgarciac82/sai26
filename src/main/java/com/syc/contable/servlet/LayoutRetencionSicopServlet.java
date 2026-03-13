package com.syc.contable.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jfree.util.Log;

import com.syc.contable.RetencionBusinessLogic;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;

public class LayoutRetencionSicopServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public LayoutRetencionSicopServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		File layoutSicop = null;
		File layoutCompromiso = null;
		try {
	    	HttpSession session = request.getSession(false);

			Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
			if (usuario == null) {
				response.sendRedirect("../index.jsp");
				return;
			}
			RetencionBusinessLogic retbl = new RetencionBusinessLogic(GestionInterface.ATT_CONEXION);
			SimpleDateFormat fecha = new SimpleDateFormat("yyyyMMddhhmm");
			String sufijo = fecha.format(new Date(System.currentTimeMillis()));
			
			//Crea el Layout del pago
			layoutSicop = retbl.creaSicop(request, response);
			//Crea el Layout del compromiso
			layoutCompromiso = retbl.compromisoSicop( request, response );
			
			//Genera el archivo en zip
			File layoutSicopZip = new File (System.getProperty( "java.io.tmpdir" ) + File.separatorChar +  "LayoutsSicop" +  "_" + sufijo.trim() + ".zip");
			
			File[] filenames = new File[] { layoutSicop, layoutCompromiso }; // These are the files to include in the ZIP file
			ServletOutputStream ouputStream;
			byte[] buf = new byte[1024]; // Create a buffer for reading the files
			response.setContentType("application/zip");
			response.setHeader("Content-Disposition", "attachment;filename=\"" + layoutSicopZip.getName() + "\"");
			try {
				ouputStream = response.getOutputStream();
				ZipOutputStream outZIP = new ZipOutputStream(ouputStream); // Create the ZIP file
				
				for (int i = 0; i < filenames.length; i++){ 
					FileInputStream in = new FileInputStream(filenames[i]);
					outZIP.putNextEntry( new ZipEntry( filenames[i].getName() ) );
					int len;
					
					while ((len = in.read(buf)) > 0){ // Transfer bytes from the file to the ZIP file
						outZIP.write(buf, 0, len);
					}
					outZIP.closeEntry(); // Complete the entry
					in.close();
				}
				
				outZIP.finish();
				outZIP.close(); // Complete the ZIP file
			} catch (IOException e) {
				Log.error( e, e );
				throw new Exception (e);
			}

			//Borrar los archivos
			layoutSicop.delete();
			layoutCompromiso.delete();

		
		} catch(Exception e){
			Log.error( e, e );
			throw new ServletException(e);
		}
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}
	
}
