package com.syc.contable.servlet;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.syc.contable.AdecuacionBusinessLogic;
import com.syc.contable.IAnteProyectoBusinessLogic;
import com.syc.contable.core.IAnteproyectoEncabezado;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.EmpleadoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoDatoManager;
import com.syc.gestion.core.Empleado;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.contabilidad.servlet.ResponseSender;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@WebServlet(name = "EnviarAnteproyectoServlet", urlPatterns = { "/anteproyecto/enviaUN" })
public class EnviaAnteProyectoUN extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = -7839978082279482815L;

    String jniName = "";

    Logger log = LoggerFactory.getLogger(EnviaAnteProyectoUN.class);

    String folioGenerator = null;

    //para obtener el ejercicio fiscal en diferentes funciones
    private static AdecuacionBusinessLogic adecProy = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);

    String DATE_FORMAT = "dd/MM/yyyy";

    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

    // today
    Calendar c1 = Calendar.getInstance();

    String today = sdf.format(c1.getTime());

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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        IAnteProyectoBusinessLogic iapbl = new IAnteProyectoBusinessLogic(jniName);
        String nFolio = req.getParameter("FOLIO");
        Caso caso = (Caso) session.getAttribute(ATT_CASE);
        FolioGeneratorInterface fg = null;
        try {
            ClassLoader cl = getClass().getClassLoader();
            Class<?> clase = cl.loadClass(folioGenerator);
            fg = (FolioGeneratorInterface) clase.newInstance();
        } catch (ClassNotFoundException exc) {
            log.error("Generador de folios", exc);
            throw new ServletException(exc);
        } catch (InstantiationException exc) {
            log.error("Generador de folios", exc);
            throw new ServletException(exc);
        } catch (IllegalAccessException exc) {
            log.error("Generador de folios", exc);
            throw new ServletException(exc);
        }
        try {
            Empleado e = new Empleado();
            EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(GestionInterface.ATT_CONEXION);
            e.setClaveUsuario(u.getLogin());
            e = ebl.getEmpleado(e);
            Caso c = null;
            IAnteproyectoEncabezado enc = new IAnteproyectoEncabezado();
            enc.setaEjercicioFiscal(adecProy.obtenEjercicioFiscal());
            enc.setcArea(e.getClaveArea());
            enc.setcUnidadNormativa(u.getU_UR());
            enc.setcUnidadResponsable(u.getU_UR());
            enc.setfCarga(today);
            enc.setResponsable("INT_ANTPROYECTO_UN_" + u.getU_UR());
            enc.setResponsableInt("INTEGRADOR_ANTPROYECTO_UN_" + u.getU_UR());
            c = iapbl.dispersaIntegracionUN(Integer.parseInt(nFolio), u, fg, jniName, enc, today);
            CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
            Map<?, ?> m = CasoDatoManager.readValuesCasoDato(req, caso.getCasoDato(), true);
            String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
            cbl.avanzaCaso(caso, u.getLogin(), "", new String[] { "CONSULTA_INTANTEPROYECTO" }, new String[] { "consulta_intanteproyecto" }, m, prefixPath);
            ResponseSender.sendClientSimpleMessage(resp, true, "1");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
