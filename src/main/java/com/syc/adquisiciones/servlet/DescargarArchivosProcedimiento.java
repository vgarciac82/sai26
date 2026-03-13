package com.syc.adquisiciones.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "DescargarArchivosProcedimiento", urlPatterns = { "/servlet/DescargarArchivosProcedimiento" })
public class DescargarArchivosProcedimiento extends HttpServlet {

    /**
     * Constructor of the object.
     */
    public DescargarArchivosProcedimiento() {
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
        HttpSession session = request.getSession();
        String cIdConsolidado = session.getAttribute(GestionInterface.ATT_ConTipoConsolidado).toString() + "-" + session.getAttribute(GestionInterface.ATT_ConUnidadEjec) + "-" + session.getAttribute(GestionInterface.ATT_ConConsecutivo);
        try {
            String ruta = getServletContext().getRealPath("/") + "docs" + File.separator + session.getAttribute(GestionInterface.ATT_ProcTipoArchivo).toString() + File.separator + session.getAttribute(GestionInterface.ATT_ProTipoProcedimiento) + "-" + session.getAttribute(GestionInterface.ATT_ProUnidadEjecutora) + "-" + session.getAttribute(GestionInterface.ATT_ProConsecutivo) + "-" + session.getAttribute(GestionInterface.ATT_ProEjercicio);
            FileInputStream archivo = new FileInputStream(ruta + ".doc");
            int longitud = archivo.available();
            byte[] datos = new byte[longitud];
            archivo.read(datos);
            archivo.close();
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + session.getAttribute(GestionInterface.ATT_ProTipoProcedimiento) + "-" + session.getAttribute(GestionInterface.ATT_ProUnidadEjecutora) + "-" + session.getAttribute(GestionInterface.ATT_ProConsecutivo) + "-" + session.getAttribute(GestionInterface.ATT_ProEjercicio) + ".doc");
            ServletOutputStream ouputStream = response.getOutputStream();
            ouputStream.write(datos);
            ouputStream.flush();
            ouputStream.close();
            response.sendRedirect("../Generador/SAICYS/ArchivosProcedimiento.jsp?cIdProcedimientoArchivo=" + session.getAttribute(GestionInterface.ATT_ProTipoProcedimiento) + "-" + session.getAttribute(GestionInterface.ATT_ProUnidadEjecutora) + "-" + session.getAttribute(GestionInterface.ATT_ProConsecutivo) + "&cEjercicioArchivo=" + session.getAttribute(GestionInterface.ATT_ProEjercicio) + "&cIdConsolidadoArchivo=" + cIdConsolidado + "&cTipoArchivo=" + session.getAttribute(GestionInterface.ATT_ProcTipoArchivo).toString() + "&cMensaje=");
        } catch (Exception e) {
            response.sendRedirect("../Generador/SAICYS/ArchivosProcedimiento.jsp?cIdProcedimientoArchivo=" + session.getAttribute(GestionInterface.ATT_ProTipoProcedimiento) + "-" + session.getAttribute(GestionInterface.ATT_ProUnidadEjecutora) + "-" + session.getAttribute(GestionInterface.ATT_ProConsecutivo) + "&cEjercicioArchivo=" + session.getAttribute(GestionInterface.ATT_ProEjercicio) + "&cIdConsolidadoArchivo=" + cIdConsolidado + "&cTipoArchivo=" + session.getAttribute(GestionInterface.ATT_ProcTipoArchivo).toString() + "&cMensaje=");
            e.printStackTrace();
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
