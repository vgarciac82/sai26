package com.syc.admin.servlet;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.syc.admin.TableroAlertaSubejercicioBusinessLogic;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import com.syc.subejercicio.Subejercicio;
import com.syc.subejercicio.UsuarioCorreo;
import com.syc.utils.mail.MailSender;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

/**
 * Servlet alerta de subejercicio
 */
@WebServlet(name = "AlertaSubejercicio", urlPatterns = { "/admin/AlertaSubejercicio" })
public class TableroAlertaSubejercicioServlet extends HttpServlet implements GestionInterface {

    private static final Logger log = LoggerFactory.getLogger(TableroAlertaSubejercicioServlet.class);

    public boolean correoProduccion = false;

    private static final long serialVersionUID = 1L;

    /**
     * Directorio temporal donde se almacenara el archivo de descarga.
     */
    public static String TEMP_DIR = "";

    /**
     * JNDI para el DataSource
     */
    private static String jniName = "";

    /**
     * Capitulos a excluir.
     */
    public static final int[] EXCLUYE_CAPITULOS = new int[] { 1 };

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            log.info("Acceso sin sesion");
            resp.sendRedirect("../index.jsp");
        }
        Usuario u = (Usuario) session.getAttribute(ATT_USER);
        if (u == null) {
            log.info("Sesion sin usuario.");
            resp.sendRedirect("../index.jsp");
        }
        String accion = req.getParameter("accion");
        String entre = req.getParameter("entre");
        String hasta = req.getParameter("hasta");
        if ("ASIGNA_CONSULTA".equals(accion)) {
            log.debug("Object: {}", "asignando valores y realizando consulta: " + accion);
            TableroAlertaSubejercicioBusinessLogic tasebl = new TableroAlertaSubejercicioBusinessLogic(jniName);
            List<Map<String, String>> table = null;
            try {
                table = tasebl.getDatos();
            } catch (Exception e) {
                e.printStackTrace();
            }
            session.setAttribute("RESULTADO", table);
            resp.sendRedirect("../plantillasCasos/tableroAlertaSubejercicio.jsp?entre=" + entre + "&hasta=" + hasta);
        }
        if ("ENVIA_CORREOS".equals(accion)) {
            log.debug("Object: {}", "entra a envia correo: " + accion);
            TableroAlertaSubejercicioBusinessLogic tasebl = new TableroAlertaSubejercicioBusinessLogic(jniName);
            Connection conn = null;
            String ipNombreServidor = java.net.InetAddress.getByName(req.getServerName()).toString();
            String[] ipServidor = ipNombreServidor.split("/");
            if (ipServidor[1].equals(SYS_IP_PRODUCCION))
                correoProduccion = true;
            try {
                conn = tasebl.getConnection();
                Map<String, Subejercicio> correos = tasebl.generaDestinatarios(conn, Double.parseDouble(hasta));
                for (Iterator<String> i = correos.keySet().iterator(); i.hasNext(); ) {
                    String ue = i.next();
                    String destinatario = "";
                    String mensaje = "";
                    String token = ";";
                    log.debug("Object: {}", "Procesando U.E. " + ue);
                    Subejercicio sub2 = correos.get(ue);
                    List<UsuarioCorreo> usuarios = sub2.getUsuarios();
                    List<String> claves = sub2.getClaves();
                    for (Iterator<UsuarioCorreo> uIter = usuarios.iterator(); uIter.hasNext(); ) {
                        UsuarioCorreo uc = uIter.next();
                        if (uc.getCorreo() == null || "".equals(uc.getCorreo()))
                            continue;
                        log.debug("Object: {}", "llegaria correo a " + uc.getNombreCompleto() + " correo " + uc.getCorreo());
                        destinatario += uc.getCorreo();
                        destinatario += token;
                    }
                    if (!correoProduccion) {
                        mensaje += (!correoProduccion ? "CORREO DE PRUEBA <br>" : "") + (!correoProduccion ? "este correo le hubiera llegado a: " + destinatario + "<br> <br>" : "") + " Claves con Subejercicio de la Unidad ejecutora: " + ue + "<br>";
                        for (Iterator<String> claveIter = claves.iterator(); claveIter.hasNext(); ) {
                            mensaje += claveIter.next() + "<br>";
                            log.debug("Object: {}", mensaje);
                        }
                    }
                    if (!correoProduccion)
                        destinatario = "vgarciac@axtel.com.mx";
                    MailSender.sendMailMultipleRecipients("mse", destinatario.split(";"), mensaje, "Cuentas con subejercicio");
                }
                resp.sendRedirect("../plantillasCasos/tableroAlertaSubejercicio.jsp?entre=" + entre + "&hasta=" + hasta);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    CloseObject.closeObject(conn, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            InitialContext ic = new InitialContext();
            TEMP_DIR = (String) ic.lookup("java:comp/env/TemporaryDirectory");
            if (TEMP_DIR == null) {
                TEMP_DIR = "../upload/PEF/";
                log.info("Object: {}", "Environment Entry \"TEMP_DIR\" nula usando default \"" + TEMP_DIR + "\"");
            } else
                log.info("Object: {}", "dataSourceRefName=" + TEMP_DIR);
        } catch (NamingException exc) {
            TEMP_DIR = "../upload/PEF/";
            log.info("Error occurred", "Ocurrio un error que evito que se cargara la entrada \"TEMP_DIR\"" + exc);
            log.info("Object: {}", "Environment Entry \"dataSourceRefName\" no definida usando default \"" + TEMP_DIR + "\"");
        }
        try {
            File f = new File(TEMP_DIR);
            if (!f.exists())
                if (!f.mkdirs())
                    throw new Exception("No se puede crear el directorio temporal " + TEMP_DIR);
        } catch (Exception e) {
            log.error("Object: {}", "No fue posible crear automaticamente el directorio temporal: " + TEMP_DIR + " Solicite su creacion manual");
        }
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
