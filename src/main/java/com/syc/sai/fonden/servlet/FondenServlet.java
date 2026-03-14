package com.syc.sai.fonden.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.syc.sai.contabilidad.CuentaContableBusinessLogic;
import com.syc.sai.contabilidad.servlet.CuentaContableServlet;
import com.syc.sai.contabilidad.servlet.ResponseSender;
import com.syc.sai.fonden.Fonden;
import com.syc.sai.fonden.FondenFacturacion;
import com.syc.sai.fonden.FondenMovimiento;
import com.syc.sai.fonden.model.FondenBusinessLogic;
import com.syc.sai.fonden.model.FondenFacturacionBusinessLogic;
import com.syc.sai.fonden.model.FondenMovimientoBusinessLogic;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@WebServlet(name = "FondenServlet", urlPatterns = { "/Fonden", "/Fonden/readFonden", "/FondenMovimiento/readFondenMovimiento", "/FondenMovimiento/saveFondenMovimiento", "/FondenFacturacion/saveFondenFacturacion", "/FondenMovimiento/validateNoGreaterThanImporteAnual", "/Fonden/saveFonden", "/FondenMovimiento/getFondenReporteGeneral" })
public class FondenServlet extends HttpServlet {

    /**
     */
    private static final long serialVersionUID = -4346303594908652764L;

    private static final Logger log = LoggerFactory.getLogger(FondenServlet.class);

    /**
     * Constructor of the object.
     */
    public FondenServlet() {
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
     * @param request
     *            the request send by the client to the server
     * @param response
     *            the response send by the server to the client
     * @throws ServletException
     *             if an error occurred
     * @throws IOException
     *             if an error occurred
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    private void readFonden(String term, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Fonden aux = new Fonden();
        FondenBusinessLogic bl = new FondenBusinessLogic();
        List<Fonden> l = bl.readFonden(2014);
        String arr = "";
        String token = "";
        String json = "{\"value\":\"%s\",\"label\":\"%s\",\"descripcion\":\"%s\"}";
        for (int i = 0; i < l.size(); i++) {
            arr += token + String.format(json, l.get(i).getCidFonden(), l.get(i).getCidFonden() + "(" + l.get(i).getNimporteAnual() + ")", l.get(i).getDfechaCaptura());
            token = ",";
        }
        arr = "[" + arr + "]";
        log.debug("Object: {}", arr);
        resp.setContentType("text/html");
        PrintWriter o = resp.getWriter();
        o.print(arr);
        o.flush();
        o.close();
    }

    private void saveFonden(String term, HttpServletRequest req, HttpServletResponse resp) {
        String arr = "";
        String json = "{\"value\":\"%s\",\"descripcion\":\"%s\"}";
        resp.setContentType("text/html");
        PrintWriter o = null;
        try {
            o = resp.getWriter();
            Fonden aux = new Fonden();
            FondenBusinessLogic bl = new FondenBusinessLogic();
            int numRows = bl.saveOrUpdateFonden(req);
            String token = "";
            if (numRows > 0) {
                json = String.format(json, numRows, "Se ha guardado exitosamente");
            } else {
                json = String.format(json, numRows, "Hubo un error al guardar");
            }
            arr = "[" + json + "]";
        } catch (Exception e) {
            json = String.format(json, "0", e.getMessage());
            arr = "[" + json + "]";
        }
        log.debug("Object: {}", arr);
        o.print(arr);
        o.flush();
        o.close();
    }

    private void readFondenMovimiento(String term, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        FondenMovimiento aux = new FondenMovimiento();
        FondenMovimientoBusinessLogic bl = new FondenMovimientoBusinessLogic();
        /*
		 * List<FondenMovimiento> l = bl.readFondenMovimiento(); String arr =
		 * ""; String token = ""; String json =
		 * "{\"value\":\"%s\",\"label\":\"%s\",\"descripcion\":\"%s\"}"; for
		 * (int i = 0; i < l.size(); i++) { arr += token + String.format(json,
		 * l.get(i)., l.get(i). + "(" + l.get(i). + ")", l.get(i).); token =
		 * ","; } arr = "[" + arr + "]"; log.debug(arr);
		 * resp.setContentType("text/html"); PrintWriter o = resp.getWriter();
		 * o.print(arr); o.flush(); o.close();
		 */
    }

    private void saveFondenMovimiento(String term, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        FondenMovimiento aux = new FondenMovimiento();
        resp.setContentType("text/html");
        PrintWriter o = resp.getWriter();
        String arr = "";
        String token = "";
        String json = "{\"value\":\"%s\",\"descripcion\":\"%s\"}";
        try {
            FondenMovimientoBusinessLogic bl = new FondenMovimientoBusinessLogic();
            int numRows = bl.saveOrUpdateFondenMovimiento(req);
            if (numRows > 0) {
                json = String.format(json, numRows, "Se ha guardado exitosamente");
            } else {
                json = String.format(json, numRows, "Hubo un error al guardar");
            }
            arr = "[" + json + "]";
        } catch (Exception e) {
            e.printStackTrace();
            json = String.format(json, "0", e.getMessage());
        }
        log.debug("Object: {}", arr);
        o.print(arr);
        o.flush();
        o.close();
    }

    private void saveFondenFacturacion(String term, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        FondenFacturacion aux = new FondenFacturacion();
        FondenFacturacionBusinessLogic bl = new FondenFacturacionBusinessLogic();
        int numRows = bl.saveOrUpdateFondenFacturacion(req);
        String arr = "";
        String token = "";
        String json = "{\"value\":\"%s\",\"descripcion\":\"%s\"}";
        if (numRows > 0) {
            json = String.format(json, numRows, "Se ha guardado exitosamente");
        } else {
            json = String.format(json, numRows, "Hubo un error al guardar");
        }
        arr = "[" + json + "]";
        log.debug("Object: {}", arr);
        resp.setContentType("text/html");
        PrintWriter o = resp.getWriter();
        o.print(arr);
        o.flush();
        o.close();
    }

    private void validateNoGreaterThanImporteAnual(String term, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        FondenFacturacion aux = new FondenFacturacion();
        FondenMovimientoBusinessLogic bl = new FondenMovimientoBusinessLogic();
        boolean success = bl.validateNoGreaterThanImporteAnual(req);
        String arr = "";
        String token = "";
        String json = "{\"value\":\"%s\",\"descripcion\":\"%s\"}";
        if (success) {
            json = String.format(json, success, "");
        } else {
            json = String.format(json, success, "El monto total es mayor al Importe anual FONDEN");
        }
        arr = "[" + json + "]";
        log.debug("Object: {}", arr);
        resp.setContentType("text/html");
        PrintWriter o = resp.getWriter();
        o.print(arr);
        o.flush();
        o.close();
    }

    private void getFondenReporteGeneral(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        FondenMovimientoBusinessLogic bl = new FondenMovimientoBusinessLogic();
        bl.getFondenReporteCSV(req, resp, getServletConfig().getServletContext());
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            ResponseSender.sendError(resp, "Sin session. Por favor reingrese al sistema.");
            return;
        }
        String accion = req.getParameter("accion");
        if ("".equals(accion) || null == accion) {
            try {
                accion = req.getRequestURI().substring(req.getRequestURI().lastIndexOf("/") + 1);
            } catch (Exception e) {
                log.warn("Error occurred", "Error obteniendo substring " + e);
            }
            if ("".equals(accion) || null == accion) {
                ResponseSender.sendError(resp, "No se recibio el parametro \"accion\" reporte al administrador");
                return;
            }
        }
        CuentaContableBusinessLogic ccbl = new CuentaContableBusinessLogic();
        // Servicios
        if ("default".equals(accion)) {
        } else if ("readFonden".equals(accion)) {
            try {
                readFonden("", req, resp);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        } else if ("selected".equals(accion)) {
            String key = req.getParameter("key");
            String value = req.getParameter("value");
            req.getSession().setAttribute(key, value);
        } else if ("saveFonden".equals(accion)) {
            try {
                saveFonden(null, req, resp);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        } else if ("readFondenMovimiento".equals(accion)) {
            try {
                readFondenMovimiento(null, req, resp);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        } else if ("saveFondenMovimiento".equals(accion)) {
            try {
                saveFondenMovimiento(null, req, resp);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        } else if ("saveFondenFacturacion".equals(accion)) {
            try {
                saveFondenFacturacion(null, req, resp);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        } else if ("validateNoGreaterThanImporteAnual".equals(accion)) {
            try {
                validateNoGreaterThanImporteAnual(null, req, resp);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        } else if ("getFondenReporteGeneral".equals(accion)) {
            try {
                getFondenReporteGeneral(req, resp);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public void init() throws ServletException {
        // Put your code here
    }
}
