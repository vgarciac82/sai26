package com.syc.contable.servlet;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.syc.contable.core.RectificacionIngresoFiscalEncabezado;
import com.syc.contable.core.ReintegroDetalle;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "LayOutRectificaIngresoFiscal", urlPatterns = { "/gstnmngr/LayoutRectificaIngresoFiscalServlet" })
public class LayoutRectificaIngresoFiscalServlet extends HttpServlet {

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
        try {
            creaSicop(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void creaSicop(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession(false);
        Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
        Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        if (usuario == null) {
            response.sendRedirect("../index.jsp");
            return;
        }
        final int folio = Integer.parseInt(c.getFolio().split("-")[2]);
        RectificacionIngresoFiscalBusinessLogic rectificacionBL = new RectificacionIngresoFiscalBusinessLogic(GestionInterface.ATT_CONEXION);
        RectificacionIngresoFiscalEncabezado re = new RectificacionIngresoFiscalEncabezado();
        String detalles = "";
        re = rectificacionBL.getRectificacionEncabezadoSicop(folio);
        detalles = rectificacionBL.getRectificacionDetalleSicop(folio);
        ReintegroDetalle reintegro = new ReintegroDetalle();
        ArrayList<ReintegroDetalle> reintegrosDetalle = new ArrayList<ReintegroDetalle>();
        ;
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(response.getOutputStream()));
        String file_name = c.getFolio();
        response.setContentType("application/vnd.ms-excel");
        response.addHeader("Content-Disposition", "inline; filename=\"" + file_name + ".csv\";");
        // Para escribir en el archivo la fecha de tipo dd/mm/yyyy
        Date date = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fecha = sdf.format(date);
        String archivo = "";
        try {
            archivo = re.getPosicion1();
            archivo += ",";
            archivo += re.getfAplicacion() == null ? "" : sdf.format(new java.util.Date(re.getfAplicacion().replaceAll("-", "/")));
            archivo += ",";
            archivo += re.getfExp() == null ? "" : sdf.format(new java.util.Date(re.getfExp().replaceAll("-", "/")));
            archivo += "," + re.getcRamo().trim();
            archivo += "," + re.getcRamo().trim();
            archivo += "," + re.getcRamo().trim();
            archivo += ",RHQ";
            archivo += ",RHQ";
            archivo += ",RHQ";
            archivo += "," + re.getcTipoMovto();
            archivo += "," + re.getnOrigenPPTO();
            archivo += "," + re.getcConceptoRectificacion();
            archivo += "," + re.getCtr_int();
            archivo += "," + c.getFolio().trim().replace(" ", "");
            archivo += "," + c.getFolio().trim().replace(" ", "");
            archivo += ",";
            archivo += ",";
            archivo += ",";
            archivo += ",";
            archivo += ",";
            archivo += ",";
            archivo += ",";
            archivo += ",";
            archivo += ",";
            archivo += ",";
            archivo += ",";
            archivo += ",";
            archivo += ",";
            archivo += ",";
            archivo += ",";
            archivo += ",";
            archivo += ",";
            archivo += ",";
            archivo += ",";
            archivo += ",";
            archivo += ",";
            archivo += ",";
            archivo += ",";
            archivo += ",";
            archivo += ",";
        } catch (Exception jxlex) {
            jxlex.printStackTrace();
        }
        archivo += detalles.replace(" ", "");
        try {
            bw.write(archivo);
            bw.flush();
            bw.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
