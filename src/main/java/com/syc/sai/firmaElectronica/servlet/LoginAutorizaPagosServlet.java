package com.syc.sai.firmaElectronica.servlet;

import java.io.IOException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import com.syc.gestion.UsuarioBusinessLogic;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.implementacion.tesoreria.EgresosInterface;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "LoginAutorizaPagosServlet", urlPatterns = { "/egresos/VoBoPago", "/egresos/VoBoPagoMasivo", "/egresos/AutPagoMasivo", "/egresos/AutPago", "/reportes/FirmaReporte", "/egresos/AutRM", "/egresos/AutENSA", "/egresos/AutEst", "/egresos/AutRequisicion", "/egresos/RVoBoPago", "/egresos/RAutPago", "/egresos/AutViaticos", "/egresos/CompruebaViaticos", "/sicove/AutFueling", "/sicove/ValidateFueling", "/sicove/AutAccountFueling" })
public class LoginAutorizaPagosServlet extends HttpServlet implements GestionInterface {

    private static final long serialVersionUID = 519575280028911727L;

    public static final Logger log = Logger.getLogger(LoginAutorizaPagosServlet.class);

    private String jniName;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(true);
        String action = "";
        try {
            action = req.getRequestURI().substring(req.getRequestURI().lastIndexOf("/") + 1);
            String employNum = StringUtils.reverse(req.getParameter(EgresosInterface.USER_PRM));
            String document = req.getParameter(EgresosInterface.DOCUMENT_PRM);
            int folio = Integer.parseInt(StringUtils.reverse(req.getParameter(EgresosInterface.FOLIO_PRM)));
            int orden = -1;
            if (!StringUtils.isBlank(req.getParameter(EgresosInterface.ORDEN)))
                orden = Integer.parseInt(req.getParameter(EgresosInterface.ORDEN));
            UsuarioBusinessLogic ubl = new UsuarioBusinessLogic(jniName);
            Usuario usuario = new Usuario();
            usuario.setNumeroEmpleado(employNum);
            usuario = ubl.getUsuario(usuario);
            if (usuario != null) {
                session.setAttribute(ATT_LOGIN, usuario.getLogin());
                session.setAttribute(EgresosInterface.USER_PRM, employNum);
                session.setAttribute(EgresosInterface.DOCUMENT_PRM, document);
                session.setAttribute(EgresosInterface.FOLIO_PRM, folio);
                session.setAttribute(EgresosInterface.ORDEN, orden);
                session.setAttribute(ATT_CMD_AUT, action);
            } else
                throw new Exception("No se encontro usuario en el sistema para el empleado con numero: " + employNum + ". Por favor solicite el alta de usuario con el administrador");
        } catch (Exception e) {
            log.error(e, e);
            session.setAttribute("login.aviso", "No puede ingresar al sistema debido a:\n" + e.toString());
        }
        session.setAttribute(ATT_CMD_AUT, action);
        resp.sendRedirect("../index.jsp");
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
}
