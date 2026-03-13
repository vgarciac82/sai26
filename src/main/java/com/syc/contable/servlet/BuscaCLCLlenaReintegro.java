package com.syc.contable.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.syc.contable.ReintegrosBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;

public class BuscaCLCLlenaReintegro extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Constructor of the object.
     */
    public BuscaCLCLlenaReintegro() {
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

	/*response.setContentType("text/html");
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
	out.close();*/
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

	HttpSession session = request.getSession(false);

	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	
	String pathURL = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName()
			+ ":" + request.getServerPort() + pathURL + "/";
	
	ReintegrosBusinessLogic reintegroBL = new ReintegrosBusinessLogic(GestionInterface.ATT_CONEXION);

	String nclc = "";
	if(request.getParameter("accion").equals("1")){
        	nclc = request.getParameter("clc");
        	
        	try {
        	    String retVal = reintegroBL.clcPagada(Integer.parseInt(nclc),"");
        	    PrintWriter out = response.getWriter();
        	    if(retVal=="")
        		out.println("La CLC no esta pagada");
        	    else{
        		//reintegroBL.getReintegroCLC(Integer.parseInt(nclc));
        	    }
        	    out.flush();
        	    out.close();
        	} catch (NumberFormatException e) {
        	    e.printStackTrace();
        	} catch (Exception e) {
        	    e.printStackTrace();
        	}
	}else if(request.getParameter("accion").equals("2")){
	    
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
