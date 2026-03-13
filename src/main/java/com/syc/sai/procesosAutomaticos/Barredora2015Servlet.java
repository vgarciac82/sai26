/**
 */
package com.syc.sai.procesosAutomaticos;

import java.io.IOException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.syc.contable.CompromisoBussinessLogic;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.gestion.servlet.GestionInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Propietario
 */
public class Barredora2015Servlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = -899769474769752144L;

    private String folioGenerator;

    private static final Logger log = LoggerFactory.getLogger(Barredora2015Servlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            HttpSession session = req.getSession(false);
            Usuario u = (Usuario) session.getAttribute(ATT_USER);
            FolioGeneratorInterface fg = null;
            ClassLoader cl = getClass().getClassLoader();
            Class<?> clase = cl.loadClass(folioGenerator);
            fg = (FolioGeneratorInterface) clase.newInstance();
            CompromisoBussinessLogic cmpBL = new CompromisoBussinessLogic(ATT_CONEXION);
            int renglonesCancelados = cmpBL.barredoraNoMil(u, fg);
            ServletOutputStream out = resp.getOutputStream();
            out.println("Se creo el compromiso " + renglonesCancelados + " y se libero exitosamente. Se ignoro capitulo mil");
            out.flush();
            out.close();
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        try {
            InitialContext ic = new InitialContext();
            folioGenerator = (String) ic.lookup("java:comp/env/folioGeneratorInterface");
            if (folioGenerator == null) {
                folioGenerator = "com.syc.gestion.custom.DefaultFolioGenerator";
                log.info("Environment Entry \"folioGeneratorInterface\" nula usando default \"" + folioGenerator + "\"");
            } else
                log.info("folioGeneratorInterface=" + folioGenerator);
        } catch (NamingException exc) {
            folioGenerator = "com.syc.gestion.custom.DefaultFolioGenerator";
            log.info("Environment Entry \"folioGeneratorInterface\" no definida usando default \"" + folioGenerator + "\"");
        }
    }
}
