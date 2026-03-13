package com.axtel.egresos.core;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import com.axtel.egresos.viaticos.Agenda;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.gestion.servlet.GestionServlet;
import com.syc.gestion.util.Util;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "AgendaServlet", urlPatterns = { "/viaticos/consultaAgenda", "/viaticos/guardarDatos", "/viaticos/actualizarDatosChecador" })
public class ViaticosServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 5496072673639795577L;

    private static final Logger log = LogManager.getLogger(ViaticosServlet.class);

    private static final String CONSULTA = "consultaAgenda";

    private static final String GUARDAR_DATOS = "guardarDatos";

    private static final String ACTUALIZAR_DATOS = "actualizarDatosChecador";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null)
            throw new ServletException("Su session a caducado");
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null)
            throw new ServletException("Su session a caducado");
        String accion = req.getRequestURI().substring(req.getRequestURI().lastIndexOf("/") + 1);
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        try {
            if (CONSULTA.equals(accion)) {
                int idEmpleado = Integer.parseInt(req.getParameter("idEmpleado"));
                AgendaBusinessLogic abl = new AgendaBusinessLogic("jdbc/viaticos");
                List<Agenda> agenda = abl.listaAgendas(idEmpleado);
                Map<String, List<Agenda>> listaAgenda = new HashMap<>();
                listaAgenda.put("agenda", agenda);
                JSONObject resultJSON = Util.toJson(listaAgenda);
                resp.setStatus(HttpServletResponse.SC_OK);
                PrintWriter out = resp.getWriter();
                out.println(resultJSON.toString());
                out.flush();
                out.close();
            } else if (GUARDAR_DATOS.equals(accion)) {
                int idAgenda = Integer.parseInt(req.getParameter("idAgenda"));
                AgendaBusinessLogic abl = new AgendaBusinessLogic("jdbc/viaticos");
                String nombreComision = req.getParameter("nombreComision");
                Agenda agenda = abl.consultaAgenda(idAgenda);
                agenda.setNombreComision(nombreComision);
                AgendaBusinessLogic abSAI = new AgendaBusinessLogic(GestionServlet.ATT_CONEXION);
                String folio = abSAI.guardaAgenda(agenda);
                Map<String, String> comision = new HashMap<>();
                comision.put("idComision", folio);
                comision.put("idComisionReloj", String.valueOf(idAgenda));
                JSONObject resultJSON = Util.toJson(comision);
                resp.setStatus(HttpServletResponse.SC_OK);
                PrintWriter out = resp.getWriter();
                out.println(resultJSON.toString());
                out.flush();
                out.close();
            } else if (ACTUALIZAR_DATOS.equals(accion)) {
                int idComision = Integer.parseInt((("").equals(req.getParameter("idComision")) ? "0" : req.getParameter("idComision")));
                int comisionReloj = Integer.parseInt((("").equals(req.getParameter("comisionReloj")) ? "0" : req.getParameter("comisionReloj")));
                Map<String, String> comision = new HashMap<>();
                if (comisionReloj > 0) {
                    AgendaBusinessLogic abl = new AgendaBusinessLogic("jdbc/viaticos");
                    Agenda agenda = abl.consultaAgenda(comisionReloj);
                    AgendaBusinessLogic abSAI = new AgendaBusinessLogic(GestionServlet.ATT_CONEXION);
                    boolean actualizo = abSAI.actualizaAgenda(agenda, idComision);
                    if (actualizo)
                        comision.put("success", "Se actualizaron los datos correctamente");
                    else
                        comision.put("success", "No se encontro la comisión para actualizar");
                } else {
                    comision.put("success", "Esta solicitud no esta ligada al Reloj Checador, No se actualizó!");
                }
                JSONObject resultJSON = Util.toJson(comision);
                resp.setStatus(HttpServletResponse.SC_OK);
                PrintWriter out = resp.getWriter();
                out.println(resultJSON.toString());
                out.flush();
                out.close();
            }
        } catch (Exception e) {
            log.error(e, e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, String> errorObj = new HashMap<>();
            errorObj.put("causa", e.toString());
            PrintWriter out = resp.getWriter();
            out.println(errorObj.toString());
            out.flush();
            out.close();
        }
    }
}
