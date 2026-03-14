package com.syc.registroingresos;

import java.util.Base64;

public class RegistroIngresoRazonSocial {

    private int nFolioRegistroIngreso;

    private String cClave;

    //fecha
    private String fRecepcionRecurso;

    private String cOrigenTransferencia;

    private String cNombreGestion;

    private int nesExtranjero;

    private String cesDonativo;

    private String cComprobanteFiscal;

    public int getnFolioRegistroIngreso() {
        return nFolioRegistroIngreso;
    }

    public void setnFolioRegistroIngreso(int nFolioRegistroIngreso) {
        this.nFolioRegistroIngreso = nFolioRegistroIngreso;
    }

    public String getcClave() {
        return cClave;
    }

    public void setcClave(String cClave) {
        this.cClave = cClave;
    }

    public String getfRecepcionRecurso() {
        return fRecepcionRecurso;
    }

    public void setfRecepcionRecurso(String fRecepcionRecurso) {
        this.fRecepcionRecurso = fRecepcionRecurso;
    }

    public String getcOrigenTransferencia() {
        return cOrigenTransferencia;
    }

    public void setcOrigenTransferencia(String cOrigenTransferencia) {
        this.cOrigenTransferencia = cOrigenTransferencia;
    }

    public String getcNombreGestion() {
        return cNombreGestion;
    }

    public void setcNombreGestion(String cNombreGestion) {
        this.cNombreGestion = cNombreGestion;
    }

    public int getNesExtranjero() {
        return nesExtranjero;
    }

    public void setNesExtranjero(int nesExtranjero) {
        this.nesExtranjero = nesExtranjero;
    }

    public String getCesDonativo() {
        return cesDonativo;
    }

    public void setCesDonativo(String cesDonativo) {
        this.cesDonativo = cesDonativo;
    }

    public String getcComprobanteFiscal() {
        return cComprobanteFiscal;
    }

    public void setcComprobanteFiscal(String cComprobanteFiscal) {
        this.cComprobanteFiscal = cComprobanteFiscal;
    }
}
