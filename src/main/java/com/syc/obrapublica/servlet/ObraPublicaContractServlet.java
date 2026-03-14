package com.syc.obrapublica.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.*;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.obrapublica.core.ObraPublicaContract;
import com.syc.obrapublica.ObraPublicaContractBusinessLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class ObraPublicaContractServlet extends HttpServlet {

    /**
     */
    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(ObraPublicaContractServlet.class);

    DataSource pool;

    /**
     * Initialization of the servlet. <br>
     *
     * @throws ServletException if an error occurs
     */
    public void init() throws ServletException {
        // Put your code here
    }

    /**
     * Constructor of the object.
     */
    public ObraPublicaContractServlet() {
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
        out.print("Parámetros del servlet uno:" + request.getParameter("opcion"));
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
        response.setContentType("text/html");
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        ObraPublicaContract opc = new ObraPublicaContract();
        opc.setTypeDocument(Byte.parseByte(request.getSession().getAttribute("typeDocument").toString()));
        opc.setTypeAction(Byte.parseByte(request.getSession().getAttribute("typeAction").toString()));
        opc.setApplyAccountigEngine(Byte.parseByte(request.getSession().getAttribute("applyAccountingEngine").toString()));
        String[][] listDetail = (String[][]) request.getSession().getAttribute("listDetail[]");
        String[] listHeader = (String[]) request.getSession().getAttribute("listHeader");
        /*out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		out.println("<HTML>");
		out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
		out.println("  <BODY>");
		out.print("    This is ");
		out.print(this.getClass());
		*/
        byte valRetorno = ProcessRequest(opc, listHeader, listDetail);
        String valueReturnJSON = "false";
        String messageJSON = "Error al efectuar la operacion de registro";
        if (valRetorno == 0) {
            valueReturnJSON = "true";
            messageJSON = "Operación exitosa";
        }
        /*out.println(", using the POST method");
		out.println("  </BODY>");
		out.println("</HTML>");
		*/
        /*JSONObject json = new JSONObject();
		try {
			json.put("success", valueReturnJSON);
			json.put("message", messageJSON);
			
			} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		*/
        //out.print(json);
        out.print("{\"success\":\"" + valueReturnJSON + "\"," + "\"message\":\"" + messageJSON + "\"}");
        out.flush();
        out.close();
    }

    private byte ProcessRequest(ObraPublicaContract opc, String[] listHeader, String[][] listDetail) {
        byte result = -1;
        ObraPublicaContractBusinessLogic opcbl = new ObraPublicaContractBusinessLogic(GestionInterface.ATT_CONEXION);
        byte typeDocument = opc.getTypeDocument();
        byte typeAction = opc.getTypeAction();
        try {
            if (typeDocument == 1) {
                if (typeAction == 1) {
                    opcbl.SaveApartado(opc, listHeader, listDetail);
                } else {
                }
            } else if (typeDocument == 2) {
                if (typeAction == 1) {
                    opcbl.SavePreCompromiso(opc, listHeader, listDetail);
                } else {
                }
            } else if (typeDocument == 3) {
                if (typeAction == 1) {
                    opcbl.SaveCompromiso(opc, listHeader, listDetail);
                } else {
                }
            }
            result = 0;
            opc.setMessage("Operación exitosa");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            opc.setMessage("Error al guardar registro");
        }
        return result;
    }
}
