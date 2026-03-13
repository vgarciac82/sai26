package com.syc.gestion.servlet;

import java.io.IOException;
import java.util.List;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.bouncycastle.crypto.RuntimeCryptoException;
import com.axtel.user.entities.Employee;
import com.axtel.web.clients.EmployeeClient;
import com.syc.auditoria.AuditoriaBusinessLogic;
import com.syc.gestion.EmpleadoBusinessLogic;
import com.syc.gestion.UsuarioBusinessLogic;
import com.syc.gestion.core.Empleado;
import com.syc.gestion.core.EmpleadoArea;
import com.syc.gestion.core.Usuario;
import com.syc.implementacion.tesoreria.EgresosInterface;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "GestionLoginServlet", urlPatterns = { "/gstnmngr/login" })
public class GestionLoginServlet extends HttpServlet implements GestionInterface, Servlet {

    public static final long serialVersionUID = 1L;

    private static Logger log = Logger.getLogger(GestionLoginServlet.class);

    private String jniName = null;

    private String tiempoActasConsultar = null;

    private String tempDir;

    private EmployeeClient employeeClient;

    private EmpleadoBusinessLogic ebl;

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
        try {
            tempDir = new String(getServletConfig().getInitParameter("tempDir"));
            if (tempDir == null)
                throw new ServletException();
        } catch (Exception e) {
            throw new ServletException("No esta definido el directorio temporal del servletFirmaProfesor");
        }
        try {
            tiempoActasConsultar = new String(getServletConfig().getInitParameter("tiempoActasConsultar"));
            if (tiempoActasConsultar == null)
                throw new ServletException();
        } catch (Exception e) {
            System.out.println("No esta definido el parametro tiempoActasConsultar, se asume 10 dias");
            tiempoActasConsultar = "10";
        }
        try {
            ConfiguraAplicativoBusinessLogic systemConfig = new ConfiguraAplicativoBusinessLogic(jniName);
            employeeClient = new EmployeeClient(systemConfig.getSystemSetting("HR_EMPLOYEE_URL"), systemConfig.getSystemSetting("HR_EMPLOYEE_USER"), systemConfig.getSystemSetting("HR_EMPLOYEE_CODE"));
            ebl = new EmpleadoBusinessLogic(jniName);
        } catch (Exception e) {
            log.error(e, e);
            throw new ServletException("Error iniciando configuracion de sistema: " + e.toString());
        }
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(true);
        if (session == null) {
            log.warn("No se logro crear la sesion");
            throw new ServletException("No se logro crear la sesion");
        }
        session.setMaxInactiveInterval(-1);
        String action = StringUtils.trimToEmpty((String) req.getParameter("a"));
        String ul = null;
        String un = null;
        String d = null;
        Integer f = null;
        Integer o = null;
        if (!StringUtils.isBlank(action)) {
            ul = StringUtils.trimToEmpty((String) req.getParameter("ul"));
            un = StringUtils.trimToEmpty((String) req.getParameter("un"));
            d = StringUtils.trimToEmpty((String) req.getParameter("d"));
            f = Integer.parseInt(req.getParameter("f"));
            if (StringUtils.isBlank(req.getParameter("o")))
                o = -1;
            else
                o = Integer.parseInt(req.getParameter("o"));
            session.setAttribute(ATT_LOGIN, ul);
            session.setAttribute(EgresosInterface.USER_PRM, un);
            session.setAttribute(EgresosInterface.DOCUMENT_PRM, d);
            session.setAttribute(EgresosInterface.FOLIO_PRM, f);
            session.setAttribute(EgresosInterface.ORDEN, o);
            session.setAttribute(ATT_CMD_AUT, action);
        }
        // Ethiel, para login normal. Parametrizar cuando haya chance
        String uLogin = req.getParameter("login");
        if ((uLogin == null) || ("".equals(uLogin))) {
            log.info("usuario vacio");
            session.setAttribute("login.message", "Debe proporcionar el usuario");
            resp.sendRedirect("../index.jsp");
            return;
        }
        String redireccionaEjercicio = "";
        redireccionaEjercicio = (req.getParameter("redirEjer") != null ? req.getParameter("redirEjer") : "si");
        log.debug("redirecciona=" + redireccionaEjercicio);
        String uPassword = req.getParameter("password");
        if ("si".equals(redireccionaEjercicio)) {
            // cuando viene de redireccionar ya no se requiere password
            if ((uPassword == null) || ("".equals(uPassword))) {
                log.info("password vacio");
                session.setAttribute("login.message", "Debe proporcionar la contraseña de usuario");
                resp.sendRedirect("../index.jsp");
                return;
            }
        }
        // Ethiel, termina para login normal
        String ejercicio = "";
        if (req.getParameter("ejercicio") != null)
            ejercicio = req.getParameter("ejercicio");
        /*
		 * en caso de que quieran entrar sin usar el index
		 */
        if ("".equals(ejercicio)) {
            log.info("ejercicio vacio");
            session.setAttribute("login.message", "Debe proporcionar el ejercicio");
            resp.sendRedirect("../index.jsp");
            return;
        }
        Usuario u = null;
        log.info("JNI NAMEe: " + jniName);
        UsuarioBusinessLogic uc = new UsuarioBusinessLogic(jniName);
        try {
            log.info("Password 1: " + uc.convertCMD5("1"));
            uPassword = uc.convertCMD5(uPassword);
            /*
			 * Ethiel, para login normal
			 */
            u = uc.validaCredenciales(uLogin, "".equals(uPassword) ? null : uPassword);
        } catch (Exception exc) {
            throw new ServletException(exc);
        }
        if (u == null) {
            log.info("El usuario/contraseña es invalido");
            session.setAttribute("login.message", "El usuario/contraseña es invalido, re-intente");
            resp.sendRedirect("../index.jsp");
            return;
        }
        // no permitir usuarios dados de baja
        if (u.getEstatus().equals("B")) {
            log.info("El usuario a sido dado de baja");
            session.setAttribute("login.message", "El usuario a sido dado de baja, consulte a su administrador");
            resp.sendRedirect("../index.jsp");
            return;
        }
        /*
		 * no permitir usuarios inactivos
		 */
        if (u.getEstatus().equals("I") || !u.getEstatus().equals("A")) {
            log.info("El usuario se encuentra inactivo");
            session.setAttribute("login.message", "El usuario se encuentra inactivo, consulte a su administrador");
            resp.sendRedirect("../index.jsp");
            return;
        }
        /*
		 * 2015 esta harcode porque es el año inicial del sistema y no tenia
		 * sufijo en la url
		 */
        if ("si".equals(redireccionaEjercicio) && !"2015".equals(ejercicio)) {
            String url = req.getScheme() + "://" + req.getServerName() + (req.getServerPort() == 80 ? "" : ":" + req.getServerPort()) + req.getContextPath() + (!"2015".equals(ejercicio) ? "_" + ejercicio : "") + "/gstnmngr/login?login=" + uLogin + "&redirEjer=no&ejercicio=" + ejercicio + (!StringUtils.isBlank(action) ? "&a=" + action : "") + (!StringUtils.isBlank(ul) ? "&ul=" + ul : "") + (!StringUtils.isBlank(un) ? "&un=" + un : "") + (!StringUtils.isBlank(d) ? "&d=" + d : "") + (!(f == null) ? "&f=" + f : "") + (!(o == null) ? "&o=" + o : "");
            log.debug(url);
            resp.sendRedirect(url);
            return;
        }
        session.setAttribute(ATT_USER, u);
        try {
            Empleado e = null;
            EmpleadoArea ea = null;
            if (StringUtils.isNotBlank(u.getNumeroEmpleado()) && Integer.parseInt(u.getNumeroEmpleado()) <= 0) {
                e = new Empleado();
                e.setClaveUsuario(u.getLogin());
                e = ebl.getEmpleado(e);
                u = uc.setUserUR(u);
                ea = new EmpleadoArea();
                ea.setId(e.getClaveArea());
                ea = ebl.getEmpleadoArea(ea);
                e.setArea(ea);
            } else {
                try {
                    Employee employee = employeeClient.fetchEmployee(Integer.valueOf(u.getNumeroEmpleado()));
                    u.setU_UR(employee.getJob().getJobAdditional().getExecutingUnit().getBudgetExecutingUnit());
                    u.setU_UR_Orig(employee.getJob().getJobAdditional().getExecutingUnit().getBudgetExecutingUnit());
                    e = new Empleado();
                    e.setSalutacion("C");
                    e.setApellidoMaterno(employee.getSecondSurname());
                    e.setApellidoPaterno(employee.getFirstSurname());
                    e.setCargo(employee.getJob().getDescription());
                    e.setClaveArea(String.valueOf(employee.getJob().getId()));
                    e.setClaveUsuario(u.getLogin());
                    e.setId(String.valueOf(employee.getIdEmployee()));
                    e.setNombre(employee.getName());
                    ea = new EmpleadoArea();
                    ea.setAreaPadre(String.valueOf(employee.getSupervisor()));
                    ea.setBandejaEntradaCompartida(false);
                    ea.setBandejaSalidaCompartida(false);
                    ea.setDescripcion(employee.getJob().getDescription());
                    ea.setId(String.valueOf(employee.getJob().getId()));
                    e.setArea(ea);
                } catch (Exception ex) {
                    log.warn(ex.toString(), ex);
                    e = getEmpleadoDB(u.getLogin());
                    u = uc.getUsuarioUR(u);
                    //session.setAttribute( ATT_USER, u );
                }
            }
            session.setAttribute("empleado", e);
            session.setAttribute(ATT_EMPLEADO, e);
            AuditoriaBusinessLogic ABL = new AuditoriaBusinessLogic(GestionInterface.ATT_CONEXION);
            ABL.agregaAuditoria(u.getLogin(), ea.getId(), GestionInterface.ATT_CONEXION.substring(GestionInterface.ATT_CONEXION.indexOf("/") + 1), "Acceso", "Login", u.getLogin(), u.getLogin(), "SELECT * FROM CG_USUARIO WHERE U_LOGIN=" + u.getLogin(), u.getLogin());
        } catch (Exception audex) {
            log.error("Error escribiendo en la bitacora de accesos al intentar login de:" + u.getLogin());
            audex.printStackTrace();
        }
        if (log.isDebugEnabled())
            log.debug("Forward a gestion?" + PRM_CMD + "=" + CMD_MAIN);
        req.getRequestDispatcher("gestion?" + PRM_CMD + "=" + CMD_MAIN).forward(req, resp);
    }

    private Empleado getEmpleadoDB(String numeroEmpleado) {
        try {
            Empleado e = new Empleado();
            EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(GestionInterface.ATT_CONEXION);
            e.setClaveUsuario(numeroEmpleado);
            e = ebl.getEmpleado(e);
            EmpleadoArea ea = new EmpleadoArea();
            ea.setId(e.getClaveArea());
            ea = ebl.getEmpleadoArea(ea);
            e.setArea(ea);
            return e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected List<?> parseRequest(HttpServletRequest req) throws ServletException {
        DiskFileUpload upload = new DiskFileUpload();
        // Directorio temporal de carga de
        upload.setRepositoryPath(tempDir);
        // archivos
        // Si el archivo excede este tamaño, ocurre un excepcion
        // FileUploadException
        // -1 sin limite
        upload.setSizeMax(-1);
        try {
            return upload.parseRequest(req);
        } catch (FileUploadException fe) {
            fe.printStackTrace();
            throw new ServletException("Error de recepcion " + fe.getMessage());
        }
    }
}
