package com.syc.gestion.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.syc.fortimax.core.Descripcion;
import com.syc.fortimax.core.Fortimax;
import com.syc.gestion.FortimaxBusinessLogic;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActualizaAplicacionServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(ActualizaAplicacionServlet.class);

    private String jniName = null;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            InitialContext ic = new InitialContext();
            jniName = (String) ic.lookup("java:comp/env/dataSourceRefName");
            if (jniName == null) {
                jniName = "jdbc/gestion";
                log.info("Object: {}", "Environment Entry \"dataSourceRefName\" nula usando default \"" + jniName + "\"");
            } else
                log.info("Object: {}", "dataSourceRefName=" + jniName);
        } catch (NamingException exc) {
            jniName = "jdbc/gestion";
            log.info("Object: {}", "Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
        }
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI().substring(req.getRequestURI().lastIndexOf("/") + 1);
        String nombre_carpeta = null;
        boolean insert = "createexpedient".equals(uri);
        boolean update = "updexpedient".equals(uri);
        boolean delete = "delexpedient".equals(uri);
        resp.setContentType("text/html");
        HttpSession session = req.getSession(false);
        if (session == null) {
            PrintWriter out = resp.getWriter();
            out.println("<script language=\"javascript\">top.location.href(\"index.jsp\")</script>");
            out.flush();
            out.close();
            return;
        }
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null) {
            PrintWriter out = resp.getWriter();
            out.println("<script language=\"javascript\">top.location.href(\"index.jsp\")</script>");
            out.flush();
            out.close();
            return;
        }
        String selectId = req.getParameter("select");
        if (selectId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Sin nodo seleccionado");
            return;
        }
        Fortimax fimx = new Fortimax(selectId);
        try {
            FortimaxBusinessLogic fbl = new FortimaxBusinessLogic(jniName);
            Descripcion[] dsc = fbl.getDescripcion(fimx.getTituloAplicacion());
            Map map = new HashMap();
            for (int i = 0; ((!delete) && (i < dsc.length)); i++) {
                String value = null;
                String name = dsc[i].getNombreCampoLower();
                switch(dsc[i].getIdTipoDatos()) {
                    // Small Integer
                    case 3:
                    // Long Integer
                    case 4:
                    // Decimal
                    case 5:
                    // Double, Float
                    case 7:
                    // String
                    case 10:
                    case // Long String
                    12:
                        value = req.getParameter(dsc[i].getNombreCampoLower());
                        break;
                    case // Fecha
                    8:
                        String dia = req.getParameter("_day_" + dsc[i].getNombreCampoLower());
                        String mes = req.getParameter("_mes_" + dsc[i].getNombreCampoLower());
                        String year = req.getParameter("_year_" + dsc[i].getNombreCampoLower());
                        value = year + "-" + mes + "-" + dia;
                        break;
                }
                if (update || ((value != null) && (!"".equals(value)) && (!"--".equals(value)))) {
                    String[] values = { String.valueOf(dsc[i].getIdTipoDatos()), String.valueOf(dsc[i].getIndiceTipo()), ((value == null) ? "" : value), dsc[i].getNombreColumna() };
                    map.put(name, values);
                    if (i == 0)
                        nombre_carpeta = value;
                }
            }
            String[] msg = new String[1];
            String paramOk = "ok=true";
            if (dsc == null) {
                msg[0] = "<h3>La gaveta no tiene campos definidos</h3>";
                session.setAttribute("msg", msg);
                resp.sendRedirect("imgmng/Messages.jsp?ok=false");
                return;
            }
            String[] result = null;
            boolean actionOk = false;
            if (insert) {
                result = fbl.insertAplicacion(fimx.getTituloAplicacion(), u.getLogin(), nombre_carpeta, map);
                actionOk = (result[1] == null);
            } else if (delete) {
                actionOk = fbl.deleteAplicacion(fimx.getTituloAplicacion(), fimx.getIdGabinete());
            } else if (update) {
                actionOk = fbl.updateAplicacion(fimx.getTituloAplicacion(), fimx.getIdGabinete(), map);
            }
            if (actionOk) {
                String[] scrptPrfx = new String[3];
                scrptPrfx[0] = "function actualiza(){";
                if (insert || update) {
                    scrptPrfx[1] = "	window.open(\"../getexpedient?select=" + fimx.getTituloAplicacion() + "&id_gabinete=" + ((insert) ? result[0] : String.valueOf(fimx.getIdGabinete())) + "\",\"left\");";
                } else if (delete) {
                    // session.removeAttribute(TREE_EXP_KEY);
                    scrptPrfx[1] = "	window.open(\"ArbolExpediente.jsp?select=" + fimx.getTituloAplicacion() + "&arbol.tipo=g\",\"left\");";
                }
                scrptPrfx[2] = "}";
                String action = insert ? "creado" : delete ? "eliminado" : "modificado";
                msg[0] = "<h3>Expediente " + action + " con exito !</h3>";
                session.setAttribute("scriptPrefix", scrptPrfx);
                session.setAttribute("bodyAttributes", "onload=\"setTimeout('actualiza()',2000)\"");
            } else {
                String action = insert ? "crear" : delete ? "eliminar" : "modificar";
                paramOk = "ok=false";
                msg[0] = "<h3>No se logr&oacute; " + action + " el expdiente !</h3>";
                if (result != null) {
                    String[] fields = result[1].split("\\|");
                    msg = new String[fields.length + 4];
                    msg[0] = "<h3>No se logr&oacute; " + action + " el expdiente !</h3>";
                    msg[1] = "<h3>Los siguientes campos deben contener valores &uacute;nicos</h3>";
                    msg[2] = "<ul>";
                    for (int i = 0; i < fields.length; i++) {
                        msg[i + 3] = "<li>" + fields[i] + "</li>";
                    }
                    msg[fields.length + 3] = "</ul>";
                }
            }
            session.setAttribute("msg", msg);
            resp.sendRedirect("imgmng/Messages.jsp?" + paramOk);
        } catch (GestionException exc) {
            throw new ServletException(exc);
        }
    }
}
