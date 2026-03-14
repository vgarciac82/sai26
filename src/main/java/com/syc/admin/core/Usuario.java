package com.syc.admin.core;

import java.io.Serializable;
import java.util.Base64;

public class Usuario implements Serializable {

    private final static long serialVersionUID = 1;

    private String u_login;

    private String u_password;

    private String u_nombre;

    private String u_descripcion;

    //private Map usrProperties = new Hashtable();
    //private Map grupos = new Hashtable();
    //private Map roles = new Hashtable();
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
    /*
	public Map getPropiedades() {
		return usrProperties;
	}

	
	public UsuarioPropiedades getPropiedad(String name) {
		return (UsuarioPropiedades) usrProperties.get(name);
	}

	public void setPropiedades(Map properties) {
		this.usrProperties = properties;
	}

	public void setPropiedad(String name, UsuarioPropiedades prop) {
		usrProperties.put(name, prop);
	}

	public Map getGrupos() {
		return grupos;
	}

	public Grupo getGrupo(String name) {
		return (Grupo) grupos.get(name);
	}

	public void setGrupos(Map grupos) {
		this.grupos = grupos;
	}

	public void setGrupo(String name, Role grupo) {
		grupos.put(name, grupo);
	}

	public Map getRoles() {
		return roles;
	}

	public Role getRole(String name) {
		return (Role) roles.get(name);
	}

	public void setRoles(Map roles) {
		this.roles = roles;
	}

	public void setRole(String name, Role role) {
		roles.put(name, role);
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

	public Element toXML(List opers) {

		Element elUser = toXML();
		Element elOpers = new Element("opers");

		for (Iterator iter = opers.iterator(); iter.hasNext();)
			elOpers.addContent(((Operacion) iter.next()).toXML());

		elUser.addContent(elOpers);

		return elUser;
	}
	*/
}
