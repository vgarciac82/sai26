package com.syc.adquisiciones.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import org.json.JSONException;
import org.json.JSONObject;
import com.syc.adquisiciones.businessLogic.GeneraLayoutBusinessLogic;
import com.syc.adquisiciones.core.Respuesta;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "GeneraLayoutServlet", urlPatterns = { "/GeneraLayoutServlet" })
public class GeneraLayoutServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static Logger log = Logger.getLogger(GeneraLayoutServlet.class);

    private static String jndiName = null;

    private JSONArray arrayObj;

    private JSONObject jsonObj;

    private PrintWriter out = null;

    public void init(ServletConfig config) throws ServletException {
        //Crea la conexión a BD
        super.init(config);
        try {
            InitialContext ic = new InitialContext();
            jndiName = (String) ic.lookup("java:comp/env/dataSourceRefName");
            if (jndiName == null) {
                jndiName = "jdbc/gestion";
                log.info("Environment Entry \"dataSourceRefName\" nula usando default \"" + jndiName + "\"");
            } else
                log.info("dataSourceRefName=" + jndiName);
        } catch (NamingException exc) {
            jndiName = "jdbc/gestion";
            log.info("Environment Entry \"dataSourceRefName\" no definida usando default \"" + jndiName + "\"");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.info("doPost");
        HttpSession session = request.getSession(false);
        if (session == null) {
            log.warn("No hay sesion");
            response.sendRedirect("../index.jsp");
            return;
        }
        Usuario u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        if (u == null)
            throw new ServletException("Su session a caducado");
        int nTipoLayout = (null == request.getParameter("nTipoLayout") || "".equalsIgnoreCase(request.getParameter("nTipoLayout")) ? -1 : Integer.parseInt(request.getParameter("nTipoLayout")));
        try {
            switch(nTipoLayout) {
                case //Genera Layout de apartado
                1:
                    writeFileApartado(request, response);
                    break;
                case //cambia el estatus
                2:
                    IniciaEstatusLAyoutApartado(request, response);
                    break;
                default:
                    log.warn("Opción desconocida.");
                    break;
            }
        } catch (Exception e) {
            log.error(e);
        }
    }

    private void IniciaEstatusLAyoutApartado(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        GeneraLayoutBusinessLogic business = null;
        String cFolios = "";
        arrayObj = new JSONArray();
        jsonObj = new JSONObject();
        out = response.getWriter();
        String mensaje = "";
        boolean respuesta = false;
        Respuesta resp = null;
        try {
            cFolios = request.getParameter("cFolios");
            if (null == cFolios || "".equalsIgnoreCase(cFolios)) {
                throw new Exception("No se recibieron los folios.");
            }
            business = new GeneraLayoutBusinessLogic();
            resp = business.InitEstatusLayoutApartado(jndiName, cFolios);
            respuesta = resp.isResp();
            mensaje = resp.getMsg();
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

    private void writeFileApartado(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        GeneraLayoutBusinessLogic business = null;
        ServletOutputStream outS = null;
        FileInputStream fileInput1 = null;
        BufferedInputStream bufferedInput1 = null;
        BufferedOutputStream bufferedOutput = null;
        String cFolios = "";
        FileWriter fw = null;
        StringBuffer archivoLayout = null;
        int leidos1 = 0;
        try {
            cFolios = request.getParameter("cFolios");
            if (null == cFolios || "".equalsIgnoreCase(cFolios)) {
                throw new Exception("No se recibieron los folios.");
            }
            business = new GeneraLayoutBusinessLogic();
            // Da el nombre del archivo
            DateFormat fecha = new SimpleDateFormat("yyyyMMddhhmmsss");
            String sufijo = fecha.format(new Date(System.currentTimeMillis()));
            String filename = "LayoutApartado" + sufijo + ".csv";
            //Se genera el archivo
            archivoLayout = business.generaLayoutApartado(jndiName, cFolios);
            fw = new FileWriter(filename);
            fw.append(archivoLayout.toString());
            fw.flush();
            fw.close();
            response.setContentType("application/csv");
            String disposition = "attachment; fileName=" + filename;
            response.setHeader("Content-Disposition", disposition);
            outS = response.getOutputStream();
            fileInput1 = new FileInputStream(filename);
            bufferedInput1 = new BufferedInputStream(fileInput1);
            bufferedOutput = new BufferedOutputStream(outS);
            // Bucle para leer de un fichero y escribir en el otro.
            byte[] array1 = new byte[bufferedInput1.available()];
            leidos1 = bufferedInput1.read(array1);
            //leidos1=bufferedInput1.read();
            while (leidos1 > 0) {
                bufferedOutput.write(array1, 0, leidos1);
                leidos1 = bufferedInput1.read(array1);
            }
        } catch (Exception e) {
            log.error(e);
            e.printStackTrace();
        } finally {
            bufferedInput1.close();
            bufferedOutput.close();
        }
    }
}
