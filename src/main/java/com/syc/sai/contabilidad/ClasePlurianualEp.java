package com.syc.sai.contabilidad;

import java.util.Base64;

public class ClasePlurianualEp {

    /**
     * El el centro contable considerado LOCAL, los restantes centros contables
     * se consideran foraneos.
     */
    private String nFolioContratoPlurianual;

    private String ep;

    private double nImporte;

    private double nImporteModificado;

    private double Construccion;

    private double Supervision;

    public String getnFolioContratoPlurianual() {
        return nFolioContratoPlurianual;
    }

    public String getep() {
        return ep;
    }

    public double getnImporte() {
        return nImporte;
    }

    public double getdnImporteModificado() {
        return nImporteModificado;
    }

    public double getConstruccion() {
        return Construccion;
    }

    public double getSupervision() {
        return Supervision;
    }

    public void setnFolioContratoPlurianual(String nFolioContratoPlurianual) {
        this.nFolioContratoPlurianual = nFolioContratoPlurianual;
    }

    public void setep(String ep) {
        this.ep = ep;
    }

    public void setnImporte(double nImporte) {
        this.nImporte = nImporte;
    }

    public void setnImporteModificado(double nImporteModificado) {
        this.nImporteModificado = nImporteModificado;
    }

    public void setConstruccion(double Construccion) {
        this.Construccion = Construccion;
    }

    public void setSupervision(double Supervision) {
        this.Supervision = Supervision;
    }
}
