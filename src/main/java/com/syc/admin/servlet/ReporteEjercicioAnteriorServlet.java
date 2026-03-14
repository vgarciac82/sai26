package com.syc.admin.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.syc.contable.ReporteEjercicioAnteriorBussinessLogic;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;
import java.util.Base64;

@WebServlet(name = "ReporteEjercicioAnteriorServlet", urlPatterns = { "/servlet/ReporteEjercicioAnteriorServlet" })
public class ReporteEjercicioAnteriorServlet extends HttpServlet {

    /**
     * Constructor of the object.
     */
    public ReporteEjercicioAnteriorServlet() {
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
        String unidad = "";
        String fuente = "";
        String ejercicio = "";
        String partida = "";
        List<String> capitulo = new ArrayList<String>();
        if (request.getParameter("cUnidadEjecutora") != null && !request.getParameter("cUnidadEjecutora").equals("")) {
            System.out.println("unidad " + request.getParameter("cUnidadEjecutora"));
            unidad = request.getParameter("cUnidadEjecutora");
        }
        if (request.getParameter("FuenteFinanciamiento") != null && !request.getParameter("FuenteFinanciamiento").equals("")) {
            System.out.println("fuente " + request.getParameter("FuenteFinanciamiento"));
            fuente = request.getParameter("FuenteFinanciamiento");
        }
        if (request.getParameter("Partida") != null && !request.getParameter("Partida").equals("")) {
            System.out.println("partida " + request.getParameter("Partida"));
            partida = request.getParameter("Partida");
        }
        if (request.getParameter("EjercicioFiscal3") != null && !request.getParameter("EjercicioFiscal3").equals("")) {
            System.out.println("ejercicio " + request.getParameter("EjercicioFiscal3"));
            ejercicio = request.getParameter("EjercicioFiscal3");
        }
        if (request.getParameter("EjercicioFiscal2") != null && !request.getParameter("EjercicioFiscal2").equals("")) {
            System.out.println("ejercicio " + request.getParameter("EjercicioFiscal2"));
            //ejercicio=request.getParameter("EjercicioFiscal2");
        }
        if (request.getParameterValues("CAPITULO") != null && request.getParameterValues("CAPITULO").length != 0) {
            //String [] capitulos=request.getParameter("Capitulo").split(",");
            capitulo = Arrays.asList(request.getParameterValues("CAPITULO"));
            System.out.println(capitulo.get(0));
        }
        ReporteEjercicioAnteriorBussinessLogic ReporteEjercicioAnteriorBussinessLogic = new ReporteEjercicioAnteriorBussinessLogic(GestionInterface.ATT_CONEXION);
        ReporteEjercicioAnteriorBussinessLogic.prueba(capitulo, unidad, fuente, partida, request.getParameter("EjercicioFiscal"), response, request);
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
