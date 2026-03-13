package com.axtel.egresos.compromiso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.google.gson.Gson;
import com.syc.contable.CompromisoBussinessLogic;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.contabilidad.servlet.ResponseSender;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "CompromisoServlet", urlPatterns = { "/compromiso/guardarCalendarioServlet", "/compromiso/guardarCompromiso", "/compromiso/eliminarRenglon" })
public class CompromisoCalendarioServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(CompromisoCalendarioServlet.class);

    private static final String GUARDAR_CALENDARIO = "guardarCalendarioServlet";

    private static final String ELIMINAR_REGISTRO = "eliminarRenglon";

    private static final String GUARDAR_COMPROMISO = "guardarCompromiso";

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String msgRetorno = "";
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute(ATT_USER) == null) {
            ResponseSender.sendClientSimpleMessage(resp, false, "Su sesión ha caducado. Ingrese nuevamente.");
            return;
        }
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        String accion = req.getRequestURI().substring(req.getRequestURI().lastIndexOf("/") + 1);
        try {
            CompromisoBussinessLogic cbl = new CompromisoBussinessLogic(GestionInterface.ATT_CONEXION);
            if (GUARDAR_CALENDARIO.equals(accion)) {
                BufferedReader reader = req.getReader();
                StringBuilder jsonBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonBuilder.append(line);
                }
                String json = jsonBuilder.toString();
                Gson gson = new Gson();
                CalendarioEncabezado request = gson.fromJson(json, CalendarioEncabezado.class);
                String ep = request.getEp();
                String tipo = request.getTipo();
                String folio = request.getFolio();
                List<CalendarioDetalle> datos = request.getDatos();
                cbl.guardarImportesCalendario(datos, ep, Integer.parseInt(folio), tipo);
                // Respuesta de éxito
                ResponseSender.sendClientSimpleMessage(resp, true, "Datos guardados correctamente.");
            } else if (GUARDAR_COMPROMISO.equals(accion)) {
                String cxp = req.getParameter("cxp");
                int folio = Integer.parseInt(req.getParameter("folioPago"));
                String folioCaso = req.getParameter("folio");
                cbl.guardarCompromisoCalendario(folio, cxp, u, folioCaso);
                ResponseSender.sendClientSimpleMessage(resp, true, "Datos guardados correctamente.");
            } else if (ELIMINAR_REGISTRO.equals(accion)) {
                int folio = Integer.parseInt(req.getParameter("folio"));
                String ep = req.getParameter("ep");
                String mes = req.getParameter("mes");
                cbl.eliminaRegistroCalendario(folio, ep, mes);
                ResponseSender.sendClientSimpleMessage(resp, true, "Datos guardados correctamente.");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            msgRetorno = "Ocurrio el siguiente error al generar el layout: " + e.getMessage();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            // Configura el tipo de contenido como JSON
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            // Escribe la respuesta de error en formato JSON
            PrintWriter out = resp.getWriter();
            out.print("{\"error\": \"" + msgRetorno.replace("\"", "\\\"") + "\"}");
            out.flush();
        }
    }
}
