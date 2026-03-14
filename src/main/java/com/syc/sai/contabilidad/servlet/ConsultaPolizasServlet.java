package com.syc.sai.contabilidad.servlet;

import java.io.IOException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.syc.gestion.reportes.ReporteBussinesLogic;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "ConsultaPolizas", urlPatterns = { "/CuentaContable/ConsultaPoliza" })
public class ConsultaPolizasServlet extends HttpServlet {

    private static final long serialVersionUID = -8479019109451536636L;

    private String jniName = null;

    private static final Logger log = LoggerFactory.getLogger(ConsultaPolizasServlet.class);

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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String PolCtroContable = getValor(req.getParameter("PolCtroContable"));
        String PolEjercicioFiscal = getValor(req.getParameter("PolEjercicioFiscal"));
        String PolStatus = getValor(req.getParameter("PolStatus"));
        String PolFechCapturaIni = getValor(req.getParameter("PolFechCapturaIni"));
        String PolFechCapturaFin = getValor(req.getParameter("PolFechCapturaFin"));
        String PolTipo = getValor(req.getParameter("PolTipo"));
        String PolFechAplicacionIni = getValor(req.getParameter("PolFechAplicacionIni"));
        String PolFechAplicacionFin = getValor(req.getParameter("PolFechAplicacionFin"));
        String PolAutorizo = getValor(req.getParameter("PolAutorizo"));
        String PolNumeroIni = getValor(req.getParameter("PolNumeroIni"));
        String PolNumeroFin = getValor(req.getParameter("PolNumeroFin"));
        String PolMontoIni = getValor(req.getParameter("PolMontoIni"));
        String PolMontoFin = getValor(req.getParameter("PolMontoFin"));
        String PolOrigen = getValor(req.getParameter("PolOrigen"));
        String PolAutomatica = getValor(req.getParameter("PolAutomatica"));
        //String PolIdOper=getValor(req.getParameter("PolIdOper"));
        if (PolCtroContable == "" & PolEjercicioFiscal == "" & PolStatus == "" & PolFechCapturaIni == "" & PolFechCapturaFin == "" & PolTipo == "" & PolFechAplicacionIni == "" & PolFechCapturaFin == "" & PolAutorizo == "" & PolNumeroIni == "" & PolNumeroFin == "" & PolMontoIni == "" & PolMontoFin == "" & PolOrigen == "" & PolAutomatica == "") {
            String json = new String("{\"aaData\":[]}");
            resp.setContentType("application/json");
            ServletOutputStream out = resp.getOutputStream();
            log.debug("Object: {}", json);
            out.print(json);
            out.flush();
            out.close();
        } else {
            ReporteBussinesLogic rbl = new ReporteBussinesLogic(jniName);
            //,PolIdOper);
            String //,PolIdOper);
            json = //,PolIdOper);
            rbl.//,PolIdOper);
            ConsultaPolizasResultadoJSON(//,PolIdOper);
            PolCtroContable, //,PolIdOper);
            PolEjercicioFiscal, //,PolIdOper);
            PolStatus, //,PolIdOper);
            PolFechCapturaIni, //,PolIdOper);
            PolFechCapturaFin, //,PolIdOper);
            PolTipo, //,PolIdOper);
            PolFechAplicacionIni, //,PolIdOper);
            PolFechAplicacionFin, //,PolIdOper);
            PolAutorizo, //,PolIdOper);
            PolNumeroIni, //,PolIdOper);
            PolNumeroFin, //,PolIdOper);
            PolMontoIni, //,PolIdOper);
            PolMontoFin, PolOrigen, PolAutomatica);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            ServletOutputStream out = resp.getOutputStream();
            log.debug("Object: {}", json);
            out.print(new String(json.getBytes("UTF-8"), "ISO-8859-1"));
            out.flush();
            out.close();
        }
    }

    private String getValor(String val) {
        if (val == null)
            return "";
        else
            return new String(val.trim());
    }
}
