package com.syc.contable.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.syc.contable.CompromisoBussinessLogic;
import com.syc.contable.ContableInterface;
import com.syc.contable.core.AplicacionContable;
import com.syc.contable.core.AplicacionContable.AplicarContableReturn;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoDatoManager;
import com.syc.gestion.core.CasoManager;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@WebServlet(name = "AplicacionContableServletAx", urlPatterns = { "/gstnmngr/AppContAx" })
public class AplicacionContableServletAx extends HttpServlet {

    /**
     */
    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(AplicacionContable.class);

    /**
     * Constructor of the object.
     */
    public AplicacionContableServletAx() {
        super();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String validaSaldo = "";
        ArrayList<String> arrLResult = new ArrayList<String>();
        Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
        Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
        if (c == null) {
            response.sendRedirect("../index.jsp");
            return;
        }
        //Valida Centro de Costos
        String cCentroContable = "";
        String mensaje = "";
        String toDialog = "";
        boolean ok = true;
        if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
            cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
        }
        if (cCentroContable.isEmpty() || cCentroContable.equals("")) {
            mensaje = "Error: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Contable, Consulte a su administrador.";
        }
        ContableInterface conInt = new AplicacionContable();
        log.debug("Object: {}", "Inicia aplicacion contable" + new Timestamp(System.currentTimeMillis()));
        CompromisoBussinessLogic cbl = new CompromisoBussinessLogic(GestionInterface.ATT_CONEXION);
        Connection conn = null;
        try {
            conn = cbl.getConnection();
            //Motor ORIGINAL
            /*arrLResult = conInt.aplicarContable(conn, c, "", "", "", 0, "");//el commit se hace aqui adentro, si algo falla tambien el rollback se hace adentro*/
            //Motor NUEVO
            Map m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
            String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
            AplicarContableReturn acr = conInt.aplicarContableNuevo(conn, c, "", "", "", 0, "", m, prefixPath, usuario.getLogin(), validaSaldo);
            arrLResult = (ArrayList) acr.getMessageList();
            if (acr.isSuccess()) {
                conn.commit();
            } else {
                conn.rollback();
            }
            //Recargando el caso
            Caso sc = new Caso();
            sc.setIdCaso(c.getIdCaso());
            c = CasoManager.select(conn, sc);
            log.debug("Object: {}", c.getCasoDato("APLICADO_CONT").getValor());
        } catch (Exception e) {
            log.error("Error occurred", "Error en Aplicacion contable:" + e.getMessage());
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        log.debug("Object: {}", "Termina Aplicacion contable" + new Timestamp(System.currentTimeMillis()));
        if ("true".equals(c.getCasoDato("APLICADO_CONT").getValor()) || mensaje.contains("APLICADO")) {
            toDialog = !"".equals(mensaje) ? mensaje : arrLResult.get(0);
        } else {
            ok = false;
        }
        System.out.println("iendo a msj");
        mensaje(response, toDialog, ok);
    }

    private void mensaje(HttpServletResponse response, String msj, boolean ok) {
        response.setContentType("text/html");
        response.setHeader("Content-Disposition", "attachment;filename=mensaje.txt");
        PrintWriter out = null;
        try {
            response.setCharacterEncoding("UTF-8");
            out = response.getWriter();
            out.write(ok + "//" + msj);
            System.out.println("Msj escrito: " + msj);
        } catch (Exception e) {
        } finally {
            out.flush();
            out.close();
        }
    }
}
