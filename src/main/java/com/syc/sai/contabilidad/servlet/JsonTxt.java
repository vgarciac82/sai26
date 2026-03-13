package com.syc.sai.contabilidad.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.syc.gestion.TablasBusinessLogic;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "JsonTxt", urlPatterns = { "/export/GeneraJsonTxt" })
public class JsonTxt extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = -5034769853645642993L;

    private static final Logger log = LoggerFactory.getLogger(JsonTxt.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            sendError(response, "Session Terminada. Ingrese nuevamente al sistema");
            return;
        }
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null) {
            sendError(response, "No hay usuario en session. Ingrese nuevamente al sistema");
            return;
        }
        TablasBusinessLogic tb = null;
        String nFolioDocPolizaDetalle = request.getParameter("more_data");
        tb = new TablasBusinessLogic("jdbc/gestion");
        if (request.getParameter("otro_param") != null) {
            DatosPolizaManual(response, request, tb.getDatosDocPoliza(request.getParameter("nFolioDocPolizaParam")));
        } else if (request.getParameter("cancelaDocPol") != null) {
            tb.getCancelaDocPoliza(request.getParameter("nfolioDocPoliza"), request, response);
        } else if (nFolioDocPolizaDetalle != null) {
            generarJsonTxt(response, request, tb.getDetalleDocPoliza(nFolioDocPolizaDetalle));
        } else if (request.getParameter("editaPoliza") != null) {
            String nfolioPoliza = request.getParameter("nfolioPoliza");
            String cCentroContable = request.getParameter("cCentroContable");
            String cTipoPoliza = request.getParameter("cTipoPoliza");
            String nfolioDocumento = tb.getNfoliodocPoliza(nfolioPoliza, cCentroContable, cTipoPoliza);
            session.setAttribute(GestionInterface.ATT_CASE, null);
            tb.getCancelaDocPoliza(nfolioDocumento, request, response);
            // response.sendRedirect("../gstnmngr/gestion?cmd=1");
            /*
			 * RequestDispatcher rd =
			 * request.getRequestDispatcher("../gstnmngr/gestion?cmd=1");
			 * rd.forward(request, response);
			 */
        }
    }

    protected void generarJsonTxt(HttpServletResponse response, HttpServletRequest request, ArrayList<String> data) throws UnsupportedEncodingException, ServletException, IOException {
        DecimalFormat formato = new DecimalFormat("###,###,##0.00");
        response.setContentType("text/html");
        response.setHeader("Content-Disposition", "attachment;filename=mijson.txt");
        PrintWriter out = null;
        try {
            response.setCharacterEncoding("UTF-8");
            out = response.getWriter();
            int rows = data.size() / 16;
            out.write("{ \"aaData\": [");
            int puntero = -1;
            String coma = ",";
            for (int i = 0; i < rows; i++) log.trace("[\"" + data.get(++puntero) + "\",\"" + data.get(++puntero) + "\",\"" + data.get(++puntero) + "\",\"" + data.get(++puntero) + "\",\"" + data.get(++puntero) + "\",\"" + formato.format(Double.parseDouble(data.get(++puntero))) + "\",\"" + formato.format(Double.parseDouble(data.get(++puntero))) + "\",\"" + data.get(++puntero) + "\",\"" + data.get(++puntero) + "\",\"" + data.get(++puntero) + "\",\"" + data.get(++puntero) + "\",\"" + data.get(++puntero) + "\",\"" + data.get(++puntero) + "\",\"" + data.get(++puntero) + "\",\"" + data.get(++puntero) + "\",\"" + data.get(++puntero) + "\"]");
            puntero = -1;
            for (int i = 0; i < rows; i++) {
                if (i + 1 == rows)
                    coma = "";
                out.write("[\"" + data.get(++puntero) + "\",\"" + data.get(++puntero) + "\",\"" + data.get(++puntero) + "\",\"" + data.get(++puntero) + "\",\"" + data.get(++puntero) + "\",\"" + formato.format(Double.parseDouble(data.get(++puntero))) + "\",\"" + formato.format(Double.parseDouble(data.get(++puntero))) + "\",\"" + data.get(++puntero) + "\",\"" + data.get(++puntero) + "\",\"" + data.get(++puntero) + "\",\"" + data.get(++puntero) + "\",\"" + data.get(++puntero) + "\",\"" + data.get(++puntero) + "\",\"" + data.get(++puntero) + "\",\"" + data.get(++puntero) + "\",\"" + data.get(++puntero) + "\"]" + coma + "");
            }
            out.write("] }");
        } finally {
            out.flush();
            out.close();
        }
    }

    protected void DatosPolizaManual(HttpServletResponse response, HttpServletRequest request, String[] data) throws UnsupportedEncodingException, ServletException, IOException {
        DecimalFormat formato = new DecimalFormat("###,###,##0.00");
        response.setContentType("text/html");
        response.setHeader("Content-Disposition", "attachment;filename=mijson.txt");
        PrintWriter out = null;
        try {
            response.setCharacterEncoding("UTF-8");
            out = response.getWriter();
            String Array = data[0] + "//" + formato.format(Double.parseDouble(data[1])) + "//" + formato.format(Double.parseDouble(data[2])) + "//" + data[3] + "//" + data[4] + "//" + data[5] + "//" + data[6] + "//" + data[7] + "//" + data[8] + "//" + data[9] + "//" + data[10] + "//" + data[11] + "//" + data[12] + "";
            out.write(Array);
        } finally {
            out.flush();
            out.close();
        }
    }

    protected static void sendError(HttpServletResponse resp, String msg) throws IOException {
        PrintWriter out = resp.getWriter();
        out.println("<html>");
        out.println("\t<body>");
        out.println("\t\t<h1>Se presento el siguiente problema mientras se llenaba el reporte</h1><br>");
        out.println("\t\t<br>" + msg + "<br>");
        out.println("\t</body>");
        out.println("</html>");
        out.flush();
        out.close();
    }
}
