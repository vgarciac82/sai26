package com.syc.adquisiciones.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.syc.gestion.documental.CatalogosBusinessLogic;

public class AvisoUsuariosServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor of the object.
	 */
	public AvisoUsuariosServlet() {
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

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
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

		String cParametro = "nMensajeUsuarios";
		String cEjercicioFiscal = request.getParameter("txtGridaEjercicioFiscal");
		String cValor = request.getParameter("cValor").trim();
		HttpSession session = request.getSession(false);
		String aviso = null;
		CatalogosBusinessLogic cbl = new CatalogosBusinessLogic("jdbc/gestion");
		
		
		
		
		if(request.getParameter("buscaranio")!=null){
			
			aviso = cbl.selectAvisoUsuario("SELECT CVALOR FROM MSISTEMA WHERE CPARAMETRO = '"+ cParametro +"' ", cEjercicioFiscal);
			response.sendRedirect(request.getContextPath() +"/caso/avisoUsuarios.jsp?aviso="+aviso);
			return;
		}
		
		Object[] obj = new Object[3];
 		obj[0] = cValor;
 		obj[1] = cParametro;
 		obj[2] = session;
		cbl.InsertMantoCatalogos("UPDATE mSistema set cValor = '"+ cValor +"' where cParametro = 'nMensajeUsuarios'", cEjercicioFiscal,obj,5);
		
		response.sendRedirect(request.getContextPath() +"/caso/avisoUsuarios.jsp?msg=1");
		
		
		
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
