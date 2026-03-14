package com.syc.adquisiciones.servlet;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.syc.gestion.servlet.GestionInterface;
import java.util.Base64;

public class generaXML extends HttpServlet {

    /**
     * Constructor of the object.
     */
    public generaXML() {
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
        HttpSession session = request.getSession();
        String tipoArchivo = session.getAttribute(GestionInterface.ATT_ProcTipoArchivo).toString();
        String cIdConsolidado = session.getAttribute(GestionInterface.ATT_ConTipoConsolidado).toString() + "-" + session.getAttribute(GestionInterface.ATT_ConUnidadEjec) + "-" + session.getAttribute(GestionInterface.ATT_ConConsecutivo);
        String ruta = getServletContext().getRealPath("/") + "docs" + File.separator + tipoArchivo + File.separator + session.getAttribute(GestionInterface.ATT_ProTipoProcedimiento) + "-" + session.getAttribute(GestionInterface.ATT_ProUnidadEjecutora) + "-" + session.getAttribute(GestionInterface.ATT_ProConsecutivo) + "-" + session.getAttribute(GestionInterface.ATT_ProEjercicio);
        String contentType = request.getContentType();
        if ((contentType != null) && (contentType.indexOf("multipart/form-data") >= 0)) {
            try {
                DataInputStream in = new DataInputStream(request.getInputStream());
                int formDataLength = request.getContentLength();
                byte[] dataBytes = new byte[formDataLength];
                int byteRead = 0;
                int totalBytesRead = 0;
                while (totalBytesRead < formDataLength) {
                    byteRead = in.read(dataBytes, totalBytesRead, formDataLength);
                    totalBytesRead += byteRead;
                }
                String file = new String(dataBytes);
                int lastIndex = contentType.lastIndexOf("=");
                String boundary = contentType.substring(lastIndex + 1, contentType.length());
                int pos;
                pos = file.indexOf("filename=\"");
                pos = file.indexOf("\n", pos) + 1;
                pos = file.indexOf("\n", pos) + 1;
                pos = file.indexOf("\n", pos) + 1;
                int boundaryLocation = file.indexOf(boundary, pos) - 4;
                int startPos = ((file.substring(0, pos)).getBytes()).length;
                int endPos = ((file.substring(0, boundaryLocation)).getBytes()).length;
                FileOutputStream fileOut = new FileOutputStream(ruta + ".doc");
                fileOut.write(dataBytes, startPos, (endPos - startPos));
                fileOut.flush();
                fileOut.close();
                response.sendRedirect("../Generador/SAICYS/ArchivosProcedimiento.jsp?cIdProcedimientoArchivo=" + session.getAttribute(GestionInterface.ATT_ProTipoProcedimiento) + "-" + session.getAttribute(GestionInterface.ATT_ProUnidadEjecutora) + "-" + session.getAttribute(GestionInterface.ATT_ProConsecutivo) + "&cEjercicioArchivo=" + session.getAttribute(GestionInterface.ATT_ProEjercicio) + "&cIdConsolidadoArchivo=" + cIdConsolidado + "&cTipoArchivo=" + tipoArchivo + "&cMensaje=El archivo se cargo correctamente.");
            } catch (Exception e) {
                response.sendRedirect("../Generador/SAICYS/ArchivosProcedimiento.jsp?cIdProcedimientoArchivo=" + session.getAttribute(GestionInterface.ATT_ProTipoProcedimiento) + "-" + session.getAttribute(GestionInterface.ATT_ProUnidadEjecutora) + "-" + session.getAttribute(GestionInterface.ATT_ProConsecutivo) + "&cEjercicioArchivo=" + session.getAttribute(GestionInterface.ATT_ProEjercicio) + "&cIdConsolidadoArchivo=" + cIdConsolidado + "&cTipoArchivo=" + tipoArchivo + "&cMensaje=Ocurrio un error al cargar el archivo.");
                e.printStackTrace();
            }
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

    public int generaArchivo(String cadena, String sFichero, String ruta) throws ServletException, IOException {
        /*String sFichero = "presupuestoUnidad.xml";
		File fichero = new File(sFichero);
		
		FileWriter fichero1 = null;
		PrintWriter pw = null;
		String ruta="C:\\Users\\Lupita\\Workspaces\\MyEclipse 8.6\\gestion_conagua_sif_pruebas\\WebContent\\Generador\\SAICYS\\bin-release\\data\\"+sFichero;
		System.out.println(cadena);	*/
        FileWriter fichero = null;
        PrintWriter pw = null;
        try {
            ruta = ruta + "\\SAICYS\\bin-release\\data\\" + sFichero;
            fichero = new FileWriter(ruta);
            pw = new PrintWriter(fichero);
            pw.println(cadena);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != fichero)
                    fichero.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return 1;
    }
}
