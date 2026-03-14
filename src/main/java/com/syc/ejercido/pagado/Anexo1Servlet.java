package com.syc.ejercido.pagado;

import java.io.IOException;
import java.util.List;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.contabilidad.servlet.ResponseSender;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "GuardaAnexo1", urlPatterns = { "/Anexo1/crear" })
public class Anexo1Servlet extends HttpServlet implements GestionInterface {

    /**
     */
    private static final long serialVersionUID = -6209553370385667777L;

    private static final Logger log = LoggerFactory.getLogger(Anexo1Servlet.class);

    private String jniName;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession sesion = req.getSession(false);
        if (sesion == null) {
            return;
        }
        Usuario u = (Usuario) sesion.getAttribute(ATT_USER);
        if (u == null) {
            return;
        }
        try {
            String sAccion = (req.getParameter("accion") != null) ? req.getParameter("accion").trim() : "";
            String sFolio = (req.getParameter("id_caso") != null) ? req.getParameter("id_caso").trim() : "";
            if (sAccion.equals("-1")) {
                Anexo1BusinessLogic anexoBL = new Anexo1BusinessLogic(GestionInterface.ATT_CONEXION);
                anexoBL.recorreRenglones(Integer.parseInt(sFolio, 10));
                //anexoBL.insertUpdateApartado(Integer.parseInt(sFolio,10), "UPDATE"); //ACTUALIZA APARTADO
            } else {
                log.info("Insertando encabezado y detalle del Anexo 1");
                Anexo1Encabezado encabezado = Anexo1BusinessLogic.instanceHeaderFromRequest(req);
                List<Anexo1Detalle> detalle = Anexo1BusinessLogic.instanceDetailFromRequest(req);
                Anexo1BusinessLogic anexoBL = new Anexo1BusinessLogic(jniName);
                anexoBL.setEncabezado(encabezado);
                anexoBL.setDetalle(detalle);
                int r = anexoBL.insert();
                anexoBL.recorreRenglones(Integer.parseInt(sFolio, 10));
                //anexoBL.insertUpdateApartado(Integer.parseInt(sFolio,10), "INSERT");//INSERTA APARTADO
                ResponseSender.sendClientSimpleMessage(resp, true, String.valueOf(r));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ResponseSender.sendClientSimpleMessage(resp, false, "Ocurrio el siguiente error insertando la informacion: " + e);
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
                log.info("Object: {}", "Environment Entry \"dataSourceRefName\" nula usando default \"" + jniName + "\"");
            } else
                log.info("Object: {}", "dataSourceRefName=" + jniName);
        } catch (NamingException exc) {
            jniName = "jdbc/gestion";
            log.info("Object: {}", "Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
        }
    }
}
