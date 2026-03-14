package com.syc.gestion.core;

import java.io.Serializable;
import java.util.Base64;

public class NodeInformation implements Serializable {

    private static final long serialVersionUID = 1L;

    private String permisos = new String();

    private String unidad = new String();

    private String base = new String();

    private String dir = new String();

    private String fisicalFilename = new String();

    private String logicFilename = new String();

    private String name = new String();

    private String nombre_usuario = new String();

    public NodeInformation(String permisos, String name, String unidad, String base, String dir, String fisicalName, String logicName, String nombreUsuario) {
        this.permisos = permisos;
        this.unidad = unidad;
        this.base = base;
        this.dir = dir;
        this.fisicalFilename = fisicalName;
        this.logicFilename = logicName;
        this.name = name;
        this.nombre_usuario = nombreUsuario;
    }

    public boolean canCreate() {
        return (permisos.indexOf("c") != -1);
    }

    public boolean canDelete() {
        return (permisos.indexOf("d") != -1);
    }

    public boolean canUpdate() {
        return (permisos.indexOf("u") != -1);
    }

    public String getFileExtension() {
        if (hasExtension())
            return logicFilename.substring(logicFilename.indexOf(".") + 1);
        return null;
    }

    public String getFisicalFilenamePath() {
        return unidad + base + dir + fisicalFilename;
    }

    public String getFisicalName() {
        return fisicalFilename;
    }

    public String getLogicalFilenamePath() {
        return unidad + base + dir + logicFilename;
    }

    public String getLogicName() {
        return logicFilename;
    }

    public String getName() {
        return name;
    }

    public String getNameWithExtension() {
        return (hasExtension() ? name + "." + getFileExtension() : name);
    }

    public boolean hasExtension() {
        if (logicFilename != null)
            return (logicFilename.indexOf(".") != -1);
        return false;
    }

    public String getNombreUsuario() {
        return nombre_usuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        nombre_usuario = nombreUsuario;
    }
}
