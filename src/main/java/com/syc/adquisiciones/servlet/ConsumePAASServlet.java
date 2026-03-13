package com.syc.adquisiciones.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.syc.adquisiciones.ConsumePAASInterface;
import com.syc.adquisiciones.businessLogic.ConsumePAASImpl;
import com.syc.adquisiciones.core.DatosPagoDirectoPAAS;
import com.syc.adquisiciones.core.Respuesta;
import com.syc.ejercido.pagado.CierrePresupuestal;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "ConsumePAASServlet", urlPatterns = { "/servlet/ConsumePAASServlet" })
public class ConsumePAASServlet extends HttpServlet {

    private static final long serialVersionUID = -6042318702903800401L;

    private static Logger log = LoggerFactory.getLogger(ConsumePAASServlet.class);

    private JSONArray arrayObj;

    private JSONObject jsonObj;

    private PrintWriter out = null;

    private ConsumePAASInterface consumePAAS;

    private String jniName;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        int tipoOperacion = Integer.parseInt(request.getParameter("operacion"));
        String mensaje = "";
        boolean respuesta = false;
        try {
            if (session == null) {
                log.warn("No hay sesion");
                response.sendRedirect("../index.jsp");
                return;
            }
            Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
            log.info("Usuario: " + usuario.getNombre());
            arrayObj = new JSONArray();
            jsonObj = new JSONObject();
            out = response.getWriter();
            String cadTabla = "";
            String[] arrayTabla = null;
            ArrayList<List<String>> tabla = null;
            Respuesta resp = null;
            DatosPagoDirectoPAAS datPagoDirectoPaas = null;
            switch(tipoOperacion) {
                case // Agrega lineas pago directo paas
                1:
                    cadTabla = request.getParameter("tablaDatos");
                    arrayTabla = cadTabla.split(",");
                    tabla = creaArray(arrayTabla);
                    datPagoDirectoPaas = llenaObjectPagoDirectoPAAS(request, response);
                    resp = consumePAAS.agregaLineasPAASPagoDirecto(usuario, tabla, datPagoDirectoPaas);
                    mensaje = resp.getMsg();
                    respuesta = resp.isResp();
                    break;
                case // actualiza Lineas pago directo paas
                2:
                    cadTabla = request.getParameter("tablaDatos");
                    arrayTabla = cadTabla.split("#");
                    tabla = creaArray(arrayTabla);
                    datPagoDirectoPaas = llenaObjectPagoDirectoPAAS(request, response);
                    resp = consumePAAS.actualizaLineasPAASPagoDirecto(usuario, tabla, datPagoDirectoPaas);
                    mensaje = resp.getMsg();
                    respuesta = resp.isResp();
                    break;
                case // Elimina lineas pago directo paas
                3:
                    datPagoDirectoPaas = llenaObjectPagoDirectoPAAS(request, response);
                    resp = consumePAAS.eliminaLineasPAASPagoDirecto(usuario, datPagoDirectoPaas);
                    mensaje = resp.getMsg();
                    respuesta = resp.isResp();
                    break;
                default:
                    log.warn("Operaci\u00f3n desconocida");
                    mensaje = "Operaci\u00f3n desconocida.";
                    respuesta = false;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage().toString());
            mensaje = e.getMessage().toString();
        } finally {
            try {
                jsonObj.put("RESPUESTA", respuesta);
                jsonObj.put("MENSAJE", mensaje);
                String destino = new String(arrayObj.put(jsonObj).toString().getBytes("UTF-8"), "ISO-8859-1");
                out.println(destino);
            } catch (JSONException e) {
                e.printStackTrace();
                log.error(e.getMessage());
            }
        }
    }

    private ArrayList<List<String>> creaArray(String[] arrayTabla) throws UnsupportedEncodingException {
        ArrayList<List<String>> tabla = new ArrayList<List<String>>();
        List<String> fila = new ArrayList<String>();
        String cadena = "";
        for (int i = 0; i < arrayTabla.length; i++) {
            if ("|".equals(arrayTabla[i].trim())) {
                tabla.add(fila);
                fila = null;
                fila = new ArrayList<String>();
            } else {
                cadena = arrayTabla[i].trim();
                fila.add(new String(cadena.getBytes("ISO-8859-1"), "UTF-8"));
            }
        }
        return tabla;
    }

    private DatosPagoDirectoPAAS llenaObjectPagoDirectoPAAS(HttpServletRequest request, HttpServletResponse response) {
        DatosPagoDirectoPAAS datPagoDirectoPaas = new DatosPagoDirectoPAAS();
        datPagoDirectoPaas.setcAlmacenEntrega(request.getParameter("almacenEntrega"));
        datPagoDirectoPaas.setcPartida(request.getParameter("partida"));
        datPagoDirectoPaas.setfFechaFin(request.getParameter("fechaFin"));
        datPagoDirectoPaas.setfFechaInicio(request.getParameter("fechaIni"));
        datPagoDirectoPaas.setcEjercicio(request.getParameter("cEjercicio"));
        datPagoDirectoPaas.setcIdUnidadEjecutora(request.getParameter("cIdunidadEjecutora"));
        datPagoDirectoPaas.setcCabm(request.getParameter("cabm"));
        datPagoDirectoPaas.setnCapitulo((request.getParameter("capitulo") != null ? Integer.parseInt(request.getParameter("capitulo")) : 0));
        datPagoDirectoPaas.setnFundamentoLegal((request.getParameter("fundamentoLeg") != null ? Integer.parseInt(request.getParameter("fundamentoLeg")) : 0));
        datPagoDirectoPaas.setnTipoAdjudicacion((request.getParameter("tipoProcedimiento") != null ? Integer.parseInt(request.getParameter("tipoProcedimiento")) : 0));
        datPagoDirectoPaas.setnFolioPago((request.getParameter("nFolio") != null ? Integer.parseInt(request.getParameter("nFolio")) : 0));
        datPagoDirectoPaas.setnLinea((request.getParameter("nLinea") != null ? Integer.parseInt(request.getParameter("nLinea")) : -1));
        return datPagoDirectoPaas;
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
        consumePAAS = new ConsumePAASImpl(jniName);
    }
}
