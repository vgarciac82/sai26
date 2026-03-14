package com.axtel.contratos.core;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;

/**
 * @author hfariasr
 */
public class GarantiaContrato {

    private String cIdContratoDefinitivo;

    private ArrayList<Garantia> listGarantia;

    private String cUsuarioCaptura;

    private String cUsuarioActualiza;

    private String cNombreArchivo;

    private String cExtencion;

    private InputStream archivoStream;

    private String cNombreArchivoDestino;

    private int nTipoProcesoGarantia;

    public String getcIdContratoDefinitivo() {
        return cIdContratoDefinitivo;
    }

    public void setcIdContratoDefinitivo(String cIdContratoDefinitivo) {
        this.cIdContratoDefinitivo = cIdContratoDefinitivo;
    }

    public ArrayList<Garantia> getListGarantia() {
        return listGarantia;
    }

    public void setListGarantia(ArrayList<Garantia> listGarantia) {
        this.listGarantia = listGarantia;
    }

    public String getcUsuarioCaptura() {
        return cUsuarioCaptura;
    }

    public void setcUsuarioCaptura(String cUsuarioCaptura) {
        this.cUsuarioCaptura = cUsuarioCaptura;
    }

    public String getcUsuarioActualiza() {
        return cUsuarioActualiza;
    }

    public void setcUsuarioActualiza(String cUsuarioActualiza) {
        this.cUsuarioActualiza = cUsuarioActualiza;
    }

    public String getcNombreArchivo() {
        return cNombreArchivo;
    }

    public void setcNombreArchivo(String cNombreArchivo) {
        this.cNombreArchivo = cNombreArchivo;
    }

    public String getcExtencion() {
        return cExtencion;
    }

    public void setcExtencion(String cExtencion) {
        this.cExtencion = cExtencion;
    }

    public InputStream getArchivoStream() {
        return archivoStream;
    }

    public void setArchivoStream(InputStream archivoStream) {
        this.archivoStream = archivoStream;
    }

    public String getcNombreArchivoDestino() {
        return cNombreArchivoDestino;
    }

    public void setcNombreArchivoDestino(String cNombreArchivoDestino) {
        this.cNombreArchivoDestino = cNombreArchivoDestino;
    }

    public int getnTipoProcesoGarantia() {
        return nTipoProcesoGarantia;
    }

    public void setnTipoProcesoGarantia(int nTipoProcesoGarantia) {
        this.nTipoProcesoGarantia = nTipoProcesoGarantia;
    }
}
