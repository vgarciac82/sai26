package com.syc.sai.procesosAutomaticos;

import java.io.IOException;
import java.io.PrintWriter;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.syc.ejercido.pagado.CLCAttachmentBusinessLogic;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "AdjuntaSolicitudDePagoServlet", urlPatterns = { "/AdjuntaSolPago" })
public class AdjuntaSolicitudDePagoServlet extends HttpServlet implements GestionInterface, Runnable {

    private static final long serialVersionUID = 3438918300404645375L;

    private static String jniName = "jdbc/gestion";

    private static Logger log = LoggerFactory.getLogger(AdjuntaCLCServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Thread t = new Thread(this);
            t.start();
            PrintWriter out = resp.getWriter();
            out.println("Se ha lanzado el proceso. Por favor cierre esta ventana. ");
            out.flush();
            out.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
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

    public void run() {
        CLCAttachmentBusinessLogic clcabl = new CLCAttachmentBusinessLogic(jniName);
        clcabl.attachDocumento("C:\\solpago.zip", "c:\\tmp", "Solicitud de Pago", "Solicitud de Pago Firmada", true);
    }
}
