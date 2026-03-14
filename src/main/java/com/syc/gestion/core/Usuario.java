package com.syc.gestion.core;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import java.util.Base64;

public class Usuario implements Serializable {

    /**
     */
    private static final long serialVersionUID = 8413031756492440669L;

    private String u_login;

    private String u_password;

    private String u_nombre;

    private String u_descripcion;

    private String u_estatus;

    private String u_email;

    private String numeroEmpleado;

    private Map<String, UsuarioPropiedades> usrProperties = new Hashtable<String, UsuarioPropiedades>();

    private Map<String, Grupo> grupos = new Hashtable<String, Grupo>();

    private Map<String, Role> roles = new Hashtable<String, Role>();

    private byte[] bPk = new byte[0];

    private String u_UR;

    private String u_UR_Orig;

    private String u_Ramo;

    private String nombre_equipo_login;

    private String ip_equipo_login;

    private String uRFC;

    public Usuario() {
    }

    public Usuario(String u_login) {
        this.u_login = u_login;
    }

    /**
     * @return the uRFC
     */
    public String getuRFC() {
        return uRFC;
    }

    /**
     * @param uRFC
     *            the uRFC to set
     */
    public void setuRFC(String uRFC) {
        this.uRFC = uRFC;
    }

    public String getNombre_equipo_login() {
        return nombre_equipo_login;
    }

    public void setNombre_equipo_login(String nombre_equipo_login) {
        this.nombre_equipo_login = nombre_equipo_login;
    }

    public String getIp_equipo_login() {
        return ip_equipo_login;
    }

    public void setIp_equipo_login(String ip_equipo_login) {
        this.ip_equipo_login = ip_equipo_login;
    }

    public String getU_UR() {
        return u_UR;
    }

    public void setU_UR(String uUR) {
        u_UR = uUR;
    }

    public String getU_Ramo() {
        return u_Ramo;
    }

    public void setU_Ramo(String uRamo) {
        u_Ramo = uRamo;
    }

    public String getU_email() {
        return u_email;
    }

    public void setU_email(String uEmail) {
        u_email = uEmail;
    }

    public byte[] getBPk() {
        return bPk;
    }

    public void setBPk(byte[] pk) {
        bPk = pk;
    }

    public String getLogin() {
        return u_login;
    }

    public void setLogin(String u_login) {
        this.u_login = u_login;
    }

    public String getPassword() {
        return u_password;
    }

    public void setPassword(String u_password) {
        this.u_password = u_password;
    }

    public String getNombre() {
        return u_nombre;
    }

    public void setNombre(String u_nombre) {
        this.u_nombre = u_nombre;
    }

    public String getDescripcion() {
        return u_descripcion;
    }

    public void setDescripcion(String u_descripcion) {
        this.u_descripcion = u_descripcion;
    }

    public void setEstatus(String u_estatus) {
        this.u_estatus = u_estatus;
    }

    public String getEstatus() {
        return u_estatus;
    }

    public Map<String, UsuarioPropiedades> getPropiedades() {
        return usrProperties;
    }

    public UsuarioPropiedades getPropiedad(String name) {
        return (UsuarioPropiedades) usrProperties.get(name);
    }

    public void setPropiedades(Map<String, UsuarioPropiedades> properties) {
        this.usrProperties = properties;
    }

    public void setPropiedad(String name, UsuarioPropiedades prop) {
        usrProperties.put(name, prop);
    }

    public Map<String, Grupo> getGrupos() {
        return grupos;
    }

    public Grupo getGrupo(String name) {
        return (Grupo) grupos.get(name);
    }

    public void setGrupos(Map<String, Grupo> grupos) {
        this.grupos = grupos;
    }

    public void setGrupo(String name, Grupo grupo) {
        grupos.put(name, grupo);
    }

    public Map<String, Role> getRoles() {
        return roles;
    }

    public Role getRole(String name) {
        return (Role) roles.get(name);
    }

    public void setRoles(Map<String, Role> roles) {
        this.roles = roles;
    }

    public void setRole(String name, Role role) {
        roles.put(name, role);
    }

    public String getU_UR_Orig() {
        return u_UR_Orig;
    }

    public void setU_UR_Orig(String u_UR_Orig) {
        this.u_UR_Orig = u_UR_Orig;
    }

    public Element toXML() {
        Element elUser = new Element("usuario");
        elUser.setAttribute("login", u_login);
        Element elNombre = new Element("nombre").addContent(u_nombre);
        elUser.addContent(elNombre);
        Element elDescripcion = new Element("descripcion").addContent(u_descripcion);
        elUser.addContent(elDescripcion);
        return elUser;
    }

    public Element toXML(List<Operacion> opers) {
        Element elUser = toXML();
        Element elOpers = new Element("opers");
        for (Iterator<Operacion> iter = opers.iterator(); iter.hasNext(); ) elOpers.addContent(((Operacion) iter.next()).toXML());
        elUser.addContent(elOpers);
        return elUser;
    }

    /**
     * @return the numeroEmpleado
     */
    public String getNumeroEmpleado() {
        return StringUtils.trimToNull(numeroEmpleado);
    }

    /**
     * @param numeroEmpleado
     *            the numeroEmpleado to set
     */
    public void setNumeroEmpleado(String numeroEmpleado) {
        this.numeroEmpleado = numeroEmpleado;
    }
}
