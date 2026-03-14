package com.syc.gestion.core;

import java.io.File;
import java.util.Base64;

public class FortimaxFile {

    private String unidad;

    private String base;

    private String dir;

    private String logicalName;

    private String fisicalName;

    public FortimaxFile(String unidad, String base, String dir, String logicalName, String fisicalName) {
        this.unidad = unidad;
        this.base = base;
        this.dir = dir;
        this.logicalName = logicalName;
        this.fisicalName = fisicalName;
    }

    public String getUnidad() {
        return unidad;
    }

    public String getBase() {
        return base;
    }

    public String getDir() {
        return dir;
    }

    public String getExtension() {
        int pos = logicalName.lastIndexOf(".");
        return (pos == -1) ? new String() : logicalName.substring(pos + 1);
    }

    public boolean withExtension() {
        return (logicalName.lastIndexOf(".") >= 1);
    }

    public File getFile() {
        return new File(getUnidad() + getBase() + getDir() + getFisicalName());
    }

    public String getFisicalName() {
        return fisicalName;
    }

    public String getLogicalName() {
        int pos = logicalName.lastIndexOf(".");
        return (pos == -1) ? logicalName : logicalName.substring(0, pos);
    }

    public String getLogicalNameWithExtension() {
        return getLogicalName() + "." + getExtension();
    }
}
