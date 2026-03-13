package com.syc.ejercido.pagado.core;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import com.syc.ejercido.pagado.core.BoletajeAereoManager.Boletos;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.contabilidad.servlet.ResponseSender;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "BoletajeAereoServlet", urlPatterns = { "/vuelos/agregaBoletos", "/vuelos/leerBoletos" })
public class BoletajeAereoServlet extends HttpServlet {

    private static final long serialVersionUID = -295716982774251014L;

    private static final Logger log = Logger.getLogger(BoletajeAereoServlet.class);

    private String jniName = "";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            ResponseSender.sendError(resp, "Sin session. Por favor reingrese al sistema.");
            return;
        }
        Usuario u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        if (u == null) {
            ResponseSender.sendError(resp, "Sin session. Por favor reingrese al sistema.");
            return;
        }
        try {
            BoletaAereoBusinessLogic babl = new BoletaAereoBusinessLogic(jniName);
            JSONObject obj = new JSONObject();
            int nFolioPago = Integer.parseInt(req.getParameter("nFolioPago"));
            obj.put("success", "true");
            JSONArray arreglo = new JSONArray();
            List<Map<String, String>> boletos = babl.leePagos(nFolioPago);
            for (int i = 0; i < boletos.size(); i++) {
                Map<String, String> info = boletos.get(i);
                JSONObject objBol = new JSONObject();
                for (Iterator<String> iter = info.keySet().iterator(); iter.hasNext(); ) {
                    String key = iter.next();
                    String val = info.get(key);
                    objBol.put(key, val);
                }
                arreglo.put(objBol);
            }
            obj.put("boletos", arreglo);
            resp.setContentType("application/json");
            ServletOutputStream out = resp.getOutputStream();
            out.print(obj.toString());
            out.flush();
            out.close();
        } catch (Exception e) {
            try {
                JSONObject obj = new JSONObject(), objjResult = new JSONObject();
                obj.put("success", "false");
                objjResult.put("result", "Ocurrio el siguiente error." + e.toString());
                obj.put("data_1", objjResult);
                ServletOutputStream out = resp.getOutputStream();
                out.print(obj.toString());
                out.flush();
                out.close();
            } catch (Exception e2) {
                throw new ServletException(e2);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            ResponseSender.sendError(resp, "Sin session. Por favor reingrese al sistema.");
            return;
        }
        Usuario u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        if (u == null) {
            ResponseSender.sendError(resp, "Sin session. Por favor reingrese al sistema.");
            return;
        }
        try {
            List<Boletos> info = BoletajeAereoManager.parseBoletajeAereo(req);
            BoletaAereoBusinessLogic babl = new BoletaAereoBusinessLogic(jniName);
            int insertados = babl.insertaBoletos(info);
            ResponseSender.sendClientSimpleMessage(resp, true, "Se insertaron exitosamente " + insertados + " registros.");
        } catch (Exception e) {
            log.error(e, e);
            ResponseSender.sendError(resp, e.toString());
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            InitialContext ic = new InitialContext();
            jniName = (String) ic.lookup("java:comp/env/dataSourceRefName");
            if (jniName == null) {
                jniName = "jdbc/gestion";
                log.info("Environment Entry \"dataSourceRefName\" nula usando default \"" + jniName + "\"");
            } else
                log.info("dataSourceRefName=" + jniName);
        } catch (NamingException exc) {
            jniName = "jdbc/gestion";
            log.info("Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
        }
    }
}
