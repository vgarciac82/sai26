package com.syc.contable.servlet;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.syc.contable.ProgFederalizadosBussinessLogic;
import com.syc.gestion.servlet.GestionInterface;


public class LayoutPagoProgFederalizadosConLayoutServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor of the object.
	 */
	public LayoutPagoProgFederalizadosConLayoutServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	
	public void doDelete(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// Put your code here
	}

	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out
				.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		out.println("<HTML>");
		out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
		out.println("  <BODY>");
		out.print("    This is ");
		out.print(this.getClass());
		out.println(", using the GET method");
		out.println("  </BODY>");
		out.println("</HTML>");
		out.flush();
		out.close();
	}

	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{

		String sFolio = (request.getParameter("sDataFolios")!= null)? request.getParameter("sDataFolios").trim(): "";
		
		int valor = sFolio.length() - 1;
		String sFolioQuery = sFolio.substring(0, valor);
		
		ProgFederalizadosBussinessLogic cmpBL =new ProgFederalizadosBussinessLogic(GestionInterface.ATT_CONEXION);
		try{
			//Actualizamos el status de los folios seleccionados
			cmpBL.ActualizaStatus(sFolioQuery);
		}
		catch (FileNotFoundException ex) {
    		ex.printStackTrace();
    	}
		
		catch(Exception e){
			e.printStackTrace();
			
		}
	}

	
	public void init() throws ServletException {
		// Put your code here
	}
	
}
