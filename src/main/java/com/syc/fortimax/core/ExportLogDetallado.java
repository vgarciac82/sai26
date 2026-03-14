package com.syc.fortimax.core;

import java.util.Base64;

public class ExportLogDetallado extends ExportLog {

    String cuentaPorPagar;

    String carpeta;

    String documento;

    String pathDocumento;

    boolean cumple;

    String log = "";

    /**
     * @return the cuentaPorPagar
     */
    public String getCuentaPorPagar() {
        return cuentaPorPagar;
    }

    /**
     * @param cuentaPorPagar
     *            the cuentaPorPagar to set
     */
    public void setCuentaPorPagar(String cuentaPorPagar) {
        this.cuentaPorPagar = cuentaPorPagar;
    }

    /**
     * @return the carpeta
     */
    public String getCarpeta() {
        return carpeta;
    }

    /**
     * @param carpeta
     *            the carpeta to set
     */
    public void setCarpeta(String carpeta) {
        this.carpeta = carpeta;
    }

    /**
     * @return the documento
     */
    public String getDocumento() {
        return documento;
    }

    /**
     * @param documento
     *            the documento to set
     */
    public void setDocumento(String documento) {
        this.documento = documento;
    }

    /**
     * @return the pathDocumento
     */
    public String getPathDocumento() {
        return pathDocumento;
    }

    /**
     * @param pathDocumento
     *            the pathDocumento to set
     */
    public void setPathDocumento(String pathDocumento) {
        this.pathDocumento = pathDocumento;
    }

    /**
     * @return the cumple
     */
    public boolean isCumple() {
        return cumple;
    }

    /**
     * @param cumple
     *            the cumple to set
     */
    public void setCumple(boolean cumple) {
        this.cumple = cumple;
    }

    /**
     * @return the log
     */
    public String getLog() {
        return log;
    }

    /**
     * @param log
     *            the log to set
     */
    public void setLog(String log) {
        this.log = log;
    }

    public String toCSV() {
        return cuentaPorPagar.replaceAll(",", "") + "," + carpeta.replaceAll(",", "") + "," + documento.replaceAll(",", "") + "," + pathDocumento.replaceAll(",", "") + "," + cumple + "," + log;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
    @Override
    public String toString() {
        return "ExportLogDetallado [cuentaPorPagar=" + cuentaPorPagar + ", carpeta=" + carpeta + ", documento=" + documento + ", pathDocumento=" + pathDocumento + ", cumple=" + cumple + ", log=" + log + "]";
    }
}
