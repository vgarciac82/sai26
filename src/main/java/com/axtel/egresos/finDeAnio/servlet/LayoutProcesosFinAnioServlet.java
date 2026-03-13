package com.axtel.egresos.finDeAnio.servlet;

import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.axtel.egresos.finDeAnio.procesosFinAnioBussinessLogic;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;

public class LayoutProcesosFinAnioServlet extends HttpServlet {
	
	private static final long			serialVersionUID	= 6723127616859732249L;
	private static final Logger			log					= Logger.getLogger(LayoutProcesosFinAnioServlet.class);
	private static String				jniName				= "jdbc/gestion";
	private static Map<String, String>	plantillas			= null;	

	/**
	 * Constructor of the object.
	 */
	public LayoutProcesosFinAnioServlet() {
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
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		procesosFinAnioBussinessLogic BL =new procesosFinAnioBussinessLogic(GestionInterface.ATT_CONEXION);
		HttpSession session = request.getSession(false);
		Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
		
		
		String archivo = (request.getParameter("archivo")!= null)? request.getParameter("archivo").trim(): "";
		String evento = (request.getParameter("evento")!= null)? request.getParameter("evento").trim(): "";
		String folios = (request.getParameter("cFolio")!= null)? request.getParameter("cFolio").trim(): "";
		String tipoPago = (request.getParameter("cTipoPago")!= null)? request.getParameter("cTipoPago").trim(): "";
		String sUsuario = usuario.getLogin();
		
		int valorE = evento.length() - 1;
		int valorF = folios.length() - 1;
		int valorP = tipoPago.length() - 1;
		
		String eventoQuery = evento.substring(0, valorE);
		String foliosQuery = folios.substring(0, valorF);
		String pagosQuery = tipoPago.substring(0, valorP);
				
		if(archivo == null){
			throw new ServletException("El archivo no debe ir nulo");
		}
				
		if(archivo.equals("1")){
			try{
				BL.buscaPagos( eventoQuery, foliosQuery, pagosQuery, response, plantillas, sUsuario );
				
			}
		    catch (FileNotFoundException ex) {
		    	ex.printStackTrace();
		    }
			
		    catch(Exception e){
		    	e.printStackTrace();	
		    }	
		}
		
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
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {		
		// Put your code here
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
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
		
		synchronized (this) {
			if (plantillas == null){
				plantillas = new HashMap<String, String>();	
				
			plantillas.put("LAYOUTFINANIO", getServletContext().getRealPath("Reportes" + File.separator + "LayOutSNPFinAnio.xls"));
			}								
		}
	}
}
