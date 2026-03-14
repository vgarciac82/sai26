package com.syc.admin.servlet;

import java.io.IOException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.syc.gestion.SeguridadBusinessLogic;
import com.syc.gestion.core.Empleado;
import com.syc.gestion.core.EmpleadoArea;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Grupo;
import com.syc.gestion.core.GrupoPropiedades;
import com.syc.gestion.core.Role;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.core.UsuarioGrupo;
import com.syc.gestion.core.UsuarioPropiedades;
import com.syc.gestion.core.UsuarioRole;
import com.syc.gestion.servlet.GestionServlet;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "SeguridadServlet", urlPatterns = { "/admin/SeguridadAdmin" })
public class SeguridadServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private String jniName = null;

    private static Logger log = LoggerFactory.getLogger(GestionServlet.class);

    public SeguridadServlet() {
        super();
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

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String fullContextPath = request.getScheme() + "://" + request.getServerName() + ((request.getServerPort() == 80) ? "" : ":" + request.getServerPort()) + request.getContextPath() + "/";
        boolean doEncrypt = false;
        String charEnc = request.getCharacterEncoding();
        if (charEnc == null) {
            charEnc = response.getCharacterEncoding();
        }
        if (charEnc == null) {
            log.error("no encontro el encoding en request ni en response... procede con default 'ISO-8859-1'");
            charEnc = "ISO-8859-1";
            //charEnc = "UTF-8";
        }
        String myMessage = "SeguridadServlet.doPost() charEnc=[" + charEnc + "]";
        System.out.println(myMessage);
        log.debug("Object: {}", myMessage);
        int intAccion = -1;
        int intCatalogo = -1;
        String Accion = request.getParameter("accion");
        String Catalogo = request.getParameter("catalogo");
        if (Accion.equals("add")) {
            intAccion = 0;
        }
        if (Accion.equals("up")) {
            intAccion = 1;
        }
        if (Accion.equals("del")) {
            intAccion = 2;
        }
        Usuario u = null;
        Empleado e = null;
        UsuarioGrupo ug = null;
        UsuarioPropiedades up = null;
        UsuarioRole ur = null;
        Grupo gr = null;
        Role ro = null;
        GrupoPropiedades go = null;
        EmpleadoArea ea = null;
        Usuario uFrom = null;
        Usuario uTo = null;
        //Empleado eFrom = null;
        //Empleado eTo = null;
        if (Catalogo.equals("USUARIOS")) {
            intCatalogo = 0;
            e = new Empleado();
            /*
			txtIdUsuario
			txtPwd
			txtSaludo
			txtAPaterno
			txtAMaterno
			txtCargo
			*/
            e.setNombre(request.getParameter("txtNombres"));
            e.setApellidoPaterno(request.getParameter("txtAPaterno"));
            e.setApellidoMaterno(request.getParameter("txtAMaterno"));
            e.setCargo(request.getParameter("txtCargo"));
            e.setClaveUsuario(request.getParameter("txtIdUsuario"));
            e.setSalutacion(request.getParameter("txtSaludo"));
            //se calcula con EmpleadoManager.getMaxIdEmpleado()
            //e.setId(id);
            e.setClaveArea(request.getParameter("txtDirigidoA"));
            u = new Usuario();
            u.setLogin(request.getParameter("txtIdUsuario"));
            u.setPassword(request.getParameter("txtPwd"));
            u.setNombre(e.getNombreCompleto());
            u.setDescripcion(e.getCargo());
        } else if (Catalogo.equals("USUARIOS-GRUPOS")) {
            intCatalogo = 1;
            ug = new UsuarioGrupo();
            ug.setLogin(request.getParameter("txtPersona3"));
            ug.setNombre(request.getParameter("txtGrupoUsuario"));
        } else if (Catalogo.equals("PROPIEDADES")) {
            intCatalogo = 2;
            up = new UsuarioPropiedades();
            up.setLogin(request.getParameter("txtIdUsuarioProp"));
            up.setNombre(request.getParameter("txtNombreProp"));
            up.setValor(request.getParameter("txtValorProp"));
        } else if (Catalogo.equals("ROLE")) {
            intCatalogo = 3;
            ur = new UsuarioRole();
            ur.setLogin(request.getParameter("txtIdUsuarioRole"));
            ur.setNombre(request.getParameter("txtNombreRole"));
        } else if (Catalogo.equals("MTTO. GRUPOS")) {
            intCatalogo = 4;
            gr = new Grupo();
            gr.setNombre(request.getParameter("txtNombreGrupo"));
            gr.setDescripcion(request.getParameter("txtDescripcionGrupo"));
        } else if (Catalogo.equals("MTTO. ROLES")) {
            intCatalogo = 5;
            ro = new Role();
            ro.setNombre(request.getParameter("txtMNombreRole"));
            ro.setDescripcion(request.getParameter("txtDescripcionRole"));
        } else if (Catalogo.equals("MTTO. GRUPOS PROP")) {
            intCatalogo = 6;
            go = new GrupoPropiedades();
            go.setGrupo(request.getParameter("txtNombreGrupoPropG"));
            go.setNombre(request.getParameter("txtNombrePropiedadPropG"));
            go.setValor(request.getParameter("txtValorPropiedadGrupoPropG"));
        } else if (Catalogo.equals("AREAS")) {
            intCatalogo = 7;
            ea = new EmpleadoArea();
            ea.setId(request.getParameter("txtMClaveArea"));
            ea.setDescripcion(request.getParameter("txtMDescripcionArea"));
            int unTipo = 0;
            try {
                unTipo = Integer.parseInt(request.getParameter("txtTipoArea"));
            } catch (NumberFormatException nfe) {
                //do nothing!
            }
            ea.setTipoArea(unTipo);
            ea.setPrefijoFolio(request.getParameter("txtMPrefijoFolio"));
            ea.setAreaPadre(request.getParameter("txtAreaPadre"));
            boolean bce = request.getParameter("txtBandejaCompartidaEntrada").equals("S");
            ea.setBandejaEntradaCompartida(bce);
            boolean bcs = request.getParameter("txtBandejaCompartidaSalida").equals("S");
            ea.setBandejaSalidaCompartida(bcs);
        } else //USUARIO_COPY_MOVE
        if (Catalogo.equals("USUARIO_COPY_MOVE")) {
            intCatalogo = 8;
            uFrom = new Usuario();
            uTo = new Usuario();
            uFrom.setLogin(request.getParameter("txtUCMFrom"));
            uTo.setLogin(request.getParameter("txtUCMTo"));
        }
        SeguridadBusinessLogic segBs = new SeguridadBusinessLogic(jniName);
        switch(intAccion) {
            case //insert
            0:
                try {
                    switch(intCatalogo) {
                        case // Usuarios
                        0:
                            int retval = segBs.agregaEmpleado(e);
                            if (retval > -1) {
                                segBs.agregaUsuario(u, doEncrypt);
                            }
                            break;
                        case // Grupos
                        1:
                            segBs.agregaGrupo(ug);
                            break;
                        case // Propiedades
                        2:
                            segBs.agregaPropiedades(up);
                            break;
                        case // Roles
                        3:
                            segBs.agregaRole(ur);
                            break;
                        case // MTTO. GRUPOS
                        4:
                            segBs.agregaMttoGrupo(gr);
                            break;
                        case // MTTO. ROLES
                        5:
                            segBs.agregaMttoRole(ro);
                            break;
                        case // MTTO. GRUPOS PROP
                        6:
                            segBs.agregaMttoGpoProp(go);
                            break;
                        case // AREAS
                        7:
                            segBs.agregaEmpleadoArea(ea);
                            break;
                        case // Usuario Copiar/Mover
                        8:
                            //segBs.transfiereUsuario(uFrom, uTo, charEnc, fullContextPath);
                            break;
                    }
                } catch (GestionException exc) {
                    log.error("Agregando " + Catalogo, exc);
                    throw new ServletException(exc);
                }
                break;
            case //update
            1:
                {
                    try {
                        switch(intCatalogo) {
                            case // Usuarios
                            0:
                                int retval = segBs.actualizaEmpleado(e);
                                if (retval > -1) {
                                    segBs.actualizaUsuario(u, doEncrypt);
                                }
                                break;
                            case // Grupos
                            1:
                                segBs.actualizaGrupo(ug);
                                break;
                            case // Propiedades
                            2:
                                segBs.actualizaPropiedades(up);
                                break;
                            case // Roles
                            3:
                                segBs.actualizaRole(ur);
                                break;
                            case // MTTO. GRUPOS
                            4:
                                segBs.actualizaMttoGrupo(gr);
                                break;
                            case // MTTO. ROLES
                            5:
                                segBs.actualizaMttoRole(ro);
                                break;
                            case // MTTO. GRUPOS PROP
                            6:
                                segBs.actualizaMttoGpoProp(go);
                                break;
                            case // AREAS
                            7:
                                segBs.actualizaEmpleadoArea(ea);
                                break;
                            case // Usuario Copiar/Mover
                            8:
                                //segBs.transfiereUsuario(uFrom, uTo, charEnc, fullContextPath);
                                break;
                        }
                    } catch (GestionException exc) {
                        log.error("Actualizando " + Catalogo, exc);
                        throw new ServletException(exc);
                    }
                    break;
                }
            case //delete
            2:
                {
                    try {
                        switch(intCatalogo) {
                            case // Usuarios
                            0:
                                int retval = segBs.borraEmpleado(e);
                                if (retval > -1) {
                                    segBs.borraUsuario(u);
                                }
                                break;
                            case // Grupos
                            1:
                                segBs.borraGrupo(ug);
                                break;
                            case // Propiedades
                            2:
                                segBs.borraPropiedades(up);
                                break;
                            case // Roles
                            3:
                                segBs.borraRole(ur);
                                break;
                            case // MTTO. GRUPOS
                            4:
                                segBs.borraMttoGrupo(gr);
                                break;
                            case // MTTO. ROLES
                            5:
                                segBs.borraMttoRole(ro);
                                break;
                            case // MTTO. GRUPOS PROP
                            6:
                                segBs.borraMttoGpoProp(go);
                                break;
                            case // AREAS
                            7:
                                segBs.borraEmpleadoArea(ea);
                                break;
                        }
                    } catch (GestionException exc) {
                        log.error("Eliminando " + Catalogo, exc);
                        throw new ServletException(exc);
                    }
                    break;
                }
        }
        StringBuffer xmlResp = getRespuesta("status");
        response.setContentType("text/xml");
        response.setHeader("Cache-Control", "no-cache");
        response.getWriter().write(xmlResp.toString());
        System.out.println(xmlResp.toString());
    }

    private StringBuffer getRespuesta(String resp) {
        StringBuffer xmlOut = new StringBuffer();
        xmlOut.append("<respuesta>");
        xmlOut.append("<estado valor='");
        xmlOut.append(resp);
        xmlOut.append("'/></respuesta>");
        return xmlOut;
    }
}
