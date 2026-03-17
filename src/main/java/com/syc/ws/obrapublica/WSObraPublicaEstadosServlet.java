package com.syc.ws.obrapublica;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;
import com.syc.contable.AdecuacionBusinessLogic;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.contabilidad.servlet.ResponseSender;
import com.syc.ws.obrapublica.core.EstimacionObra;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@WebServlet(name = "WSObraPublicaEstadosServlet", urlPatterns = { "/obrapublica/WSObraPublicaEstadosServlet", "/obrapublica/CreaEstimacionObra" })
public class WSObraPublicaEstadosServlet extends HttpServlet {

    private static final long serialVersionUID = 3830246252504144684L;

    private static final Logger log = LoggerFactory.getLogger(WSObraPublicaEstadosServlet.class);

    private String jniName = null;

    private static String folioGenerator = null;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    AdecuacionBusinessLogic adecProy = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            log.warn("No se logro crear la sesion");
            throw new ServletException("No se logro crear la sesion");
        }
        String uri = request.getRequestURI();
        String action = uri.substring(uri.lastIndexOf("/") + 1, uri.length());
        log.debug("Object: " + String.valueOf(action));
        Usuario u = (Usuario) session.getAttribute(com.syc.gestion.servlet.GestionInterface.ATT_USER);
        if ("CreaEstimacionObra".equals(action)) {
            WSObraPublicaEstadosBusinessLogic WSObra = new WSObraPublicaEstadosBusinessLogic(jniName);
            try {
                EstimacionObra estimacion = EstimacionObra.instanceFromRequest(request);
                WSObra.registraEstimacionObra(estimacion, u);
                ResponseSender.sendClientSimpleMessage(response, true, "Estimacion registrada exitosamente.");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                ResponseSender.sendClientSimpleMessage(response, false, "Ocurrio el siguiente error al insertar la estimacion de obra: " + e);
            }
            return;
        } else {
            String DATE_FORMAT = "dd/MM/yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            // today
            Calendar c1 = Calendar.getInstance();
            String today = sdf.format(c1.getTime());
            //String sEstado=request.getParameter("estado");
            int nIdEstado = ((request.getParameter("nIdEstado") == null) || "".equals(request.getParameter("nIdEstado")) ? -1 : Integer.parseInt(request.getParameter("nIdEstado")));
            JSONObject jsonResp = new JSONObject();
            JSONArray arrayObj = new JSONArray();
            PrintWriter out = null;
            AdecuacionBusinessLogic adecProy = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
            String aEjercicioFiscal = "";
            String destino;
            try {
                aEjercicioFiscal = adecProy.obtenEjercicioFiscal();
            } catch (Exception e) {
                throw new ServletException(e);
            }
            if (Integer.parseInt(aEjercicioFiscal) != c1.get(Calendar.YEAR)) {
                today = "31/12/" + aEjercicioFiscal;
            }
            try {
                out = response.getWriter();
                WSObraPublicaEstadosBusinessLogic WSObra = new WSObraPublicaEstadosBusinessLogic(jniName);
                jsonResp = WSObra.ObtenerJsonObraPublicaEstados(nIdEstado);
            } catch (Exception e) {
                log.error("Error occurred", "Error al consumir Web Service: " + e.getMessage());
            } finally {
                destino = arrayObj.put(jsonResp).toString();
                out.println(new String(destino.getBytes("ISO-8859-1"), "UTF-8"));
            }
        }
    }

    /**
     * Initialization of the servlet. <br>
     *
     * @throws ServletException
     *             if an error occurs
     */
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
        try {
            InitialContext ic = new InitialContext();
            folioGenerator = (String) ic.lookup("java:comp/env/folioGeneratorInterface");
            if (folioGenerator == null) {
                folioGenerator = "com.syc.gestion.custom.DefaultFolioGenerator";
                log.info("Object: {}", "Environment Entry \"folioGeneratorInterface\" nula usando default \"" + folioGenerator + "\"");
            } else
                log.info("Object: {}", "folioGeneratorInterface=" + folioGenerator);
        } catch (NamingException exc) {
            folioGenerator = "com.syc.gestion.custom.DefaultFolioGenerator";
            log.info("Object: {}", "Environment Entry \"folioGeneratorInterface\" no definida usando default \"" + folioGenerator + "\"");
        }
    }
}
