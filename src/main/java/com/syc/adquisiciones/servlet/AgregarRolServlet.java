package com.syc.adquisiciones.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.syc.adquisiciones.core.UnificacionUsuariosManager;
import jakarta.servlet.annotation.WebServlet;
import java.util.Base64;

@WebServlet(name = "AgregarRolServlet", urlPatterns = { "/AgregarRolServlet" })
public class AgregarRolServlet extends HttpServlet {

    /**
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor of the object.
     */
    public AgregarRolServlet() {
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
        doPost(request, response);
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
        String nombre = request.getParameter("nombre");
        String desc = request.getParameter("desc");
        String duenio = request.getParameter("duenio");
        String rolBuscar = request.getParameter("rolBuscar");
        String queryRolOpciones = null;
        String queryRol = "INSERT INTO cg_role (r_nombre,r_descripcion,admin_dueno) VALUES ('" + nombre + "','" + desc + "','" + duenio + "')";
        if (//Con opciones
        request.getParameter("ConOpciones") != null)
            queryRolOpciones = "insert into CG_ROLE_OPCION (R_NOMBRE, ID_OPCION, RO_STATUS) ((SELECT DISTINCT '" + nombre + "', ID_OPCION, RO_STATUS FROM CG_ROLE_OPCION where R_NOMBRE='" + rolBuscar + "'))";
        else
            queryRolOpciones = " insert into CG_ROLE_OPCION (R_NOMBRE, ID_OPCION) ((select distinct '" + nombre + "',[ID_OPCION] FROM v_mOpcionesDisponibles))";
        try {
            UnificacionUsuariosManager.insertaRol(queryRol, queryRolOpciones, nombre);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        response.sendRedirect("/Generador/SAICYS/Roles.jsp?tab=0");
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
