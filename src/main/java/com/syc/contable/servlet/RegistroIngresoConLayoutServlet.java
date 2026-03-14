package com.syc.contable.servlet;

import java.io.FileNotFoundException;
import java.io.IOException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.registroingresos.RegistroIngresosBussinesLogic;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@WebServlet(name = "RegistroIngresoConLayoutServlet", urlPatterns = { "/gstnmngr/RegistroIngresoConLayoutServlet" })
public class RegistroIngresoConLayoutServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(RegistroIngresoConLayoutServlet.class);

    private String folioGenerator;

    public RegistroIngresoConLayoutServlet() {
        super();
    }

    public void destroy() {
        super.destroy();
    }

    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String sFolio = request.getParameter("sDataFolios");
        String u_login = request.getParameter("u_login");
        // - 1;
        int valor = sFolio.length();
        String sFolioQuery = sFolio.substring(0, valor);
        RegistroIngresosBussinesLogic RegIngBL = new RegistroIngresosBussinesLogic(GestionInterface.ATT_CONEXION);
        try {
            RegIngBL.ActualizaStatus(sFolioQuery, u_login);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
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
