package com.syc.contable.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "LayoutCadenasPConLayoutServlet", urlPatterns = { "/gstnmngr/CadenasPConLayout" })
public class LayoutCadenasPConLayoutServlet extends HttpServlet {

    /**
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor of the object.
     */
    public LayoutCadenasPConLayoutServlet() {
        super();
    }

    /**
     * Destruction of the servlet. <br>
     */
    public void destroy() {
        // Just puts "destroy" string in log
        super.destroy();
        // Put your code here
    }

    /**
     * The doDelete method of the servlet. <br>
     *
     * This method is called when a HTTP delete request is received.
     *
     * @param request the request send by the client to the server
     * @param response the response send by the server to the client
     * @throws ServletException if an error occurred
     * @throws IOException if an error occurred
     */
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
    /*public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
		//String path = request.getContextPath();
		//String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
		
        //recupero los campos caNoCompromisos de la lista de los que serán enviados
		String sFolio = (request.getParameter("contrarecibo")!= null)? request.getParameter("contrarecibo").trim(): "";
		
		
		//int valor = sFolio.length() - 1;
		//String sFolioQuery = sFolio.substring(0, valor);
		ArrayList<String> arrListComp=null;
		CadenasPBussinessLogic cmpBL =new CadenasPBussinessLogic(GestionInterface.ATT_CONEXION);
		try{
			//Actualizamos el status de los folios seleccionados
			cmpBL.ActualizaStatus(sFolio );
			
		    response.setContentType("text/html");
		    PrintWriter out = response.getWriter();
		    out.println("<script type='text/javascript' charset='utf-8'>window.location.href = '../Generador/IntegraLayoutCadenasP.jsp';</script>");

		}
		
		catch (FileNotFoundException ex) {
    		ex.printStackTrace();
    	}
		
		catch(Exception e){
			e.printStackTrace();
			
		}
	}*/
    /**
     * Initialization of the servlet. <br>
     *
     * @throws ServletException if an error occurs
     */
    public void init() throws ServletException {
        // Put your code here
    }
}
