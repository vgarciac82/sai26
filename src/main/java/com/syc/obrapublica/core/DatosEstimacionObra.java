package com.syc.obrapublica.core;

import java.util.Arrays;
import java.util.Date;
import java.util.Base64;

public class DatosEstimacionObra {

    private String contratoCNET;

    private Date fechaNota = new Date();

    private String folioNota;

    private int idEstatusEstimacion;

    private int idNota;

    private String motivoAutorizacion;

    private int numeroEmpleado;

    private int numeroEmpleadoJefe;

    private int numeroEmpleadoSubgerente;

    private String cFolioObra;

    private String nFolioObra;

    private int nEstimacion;

    private String cLogin;

    private int nEstimacionCancelado;

    private String folioNotaCancelada;

    private String[] montos;

    private int numFirmante;

    public String getContratoCNET() {
        return contratoCNET;
    }

    public void setContratoCNET(String contratoCNET) {
        this.contratoCNET = contratoCNET;
    }

    public Date getFechaNota() {
        return fechaNota;
    }

    public void setFechaNota(Date fechaNota) {
        this.fechaNota = fechaNota;
    }

    public String getFolioNota() {
        return folioNota;
    }

    public void setFolioNota(String folioNota) {
        this.folioNota = folioNota;
    }

    public int getIdEstatusEstimacion() {
        return idEstatusEstimacion;
    }

    public void setIdEstatusEstimacion(int idEstatusEstimacion) {
        this.idEstatusEstimacion = idEstatusEstimacion;
    }

    public int getIdNota() {
        return idNota;
    }

    public void setIdNota(int idNota) {
        this.idNota = idNota;
    }

    public String getMotivoAutorizacion() {
        return motivoAutorizacion;
    }

    public void setMotivoAutorizacion(String motivoAutorizacion) {
        this.motivoAutorizacion = motivoAutorizacion;
    }

    public int getNumeroEmpleado() {
        return numeroEmpleado;
    }

    public void setNumeroEmpleado(int numeroEmpleado) {
        this.numeroEmpleado = numeroEmpleado;
    }

    public String getcFolioObra() {
        return cFolioObra;
    }

    public void setcFolioObra(String cFolioObra) {
        this.cFolioObra = cFolioObra;
    }

    public String getnFolioObra() {
        return nFolioObra;
    }

    public void setnFolioObra(String nFolioObra) {
        this.nFolioObra = nFolioObra;
    }

    public int getnEstimacion() {
        return nEstimacion;
    }

    public void setnEstimacion(int nEstimacion) {
        this.nEstimacion = nEstimacion;
    }

    public String getcLogin() {
        return cLogin;
    }

    public void setcLogin(String cLogin) {
        this.cLogin = cLogin;
    }

    public int getnEstimacionCancelado() {
        return nEstimacionCancelado;
    }

    public void setnEstimacionCancelado(int nEstimacionCancelado) {
        this.nEstimacionCancelado = nEstimacionCancelado;
    }

    public String getFolioNotaCancelada() {
        return folioNotaCancelada;
    }

    public void setFolioNotaCancelada(String folioNotaCancelada) {
        this.folioNotaCancelada = folioNotaCancelada;
    }

    public String[] getMontos() {
        return montos;
    }

    public void setMontos(String[] montos) {
        this.montos = montos;
    }

    public int getNumeroEmpleadoJefe() {
        return numeroEmpleadoJefe;
    }

    public void setNumeroEmpleadoJefe(int numeroEmpleadoJefe) {
        this.numeroEmpleadoJefe = numeroEmpleadoJefe;
    }

    public int getNumeroEmpleadoSubgerente() {
        return numeroEmpleadoSubgerente;
    }

    public void setNumeroEmpleadoSubgerente(int numeroEmpleadoSubgerente) {
        this.numeroEmpleadoSubgerente = numeroEmpleadoSubgerente;
    }

    public int getNumFirmante() {
        return numFirmante;
    }

    public void setNumFirmante(int numFirmante) {
        this.numFirmante = numFirmante;
    }

    @Override
    public String toString() {
        return "DatosEstimacionObra [contratoCNET=" + contratoCNET + ", fechaNota=" + fechaNota + ", folioNota=" + folioNota + ", idEstatusEstimacion=" + idEstatusEstimacion + ", idNota=" + idNota + ", motivoAutorizacion=" + motivoAutorizacion + ", numeroEmpleado=" + numeroEmpleado + ", numeroEmpleadoJefe=" + numeroEmpleadoJefe + ", numeroEmpleadoSubgerente=" + numeroEmpleadoSubgerente + ", cFolioObra=" + cFolioObra + ", nFolioObra=" + nFolioObra + ", nEstimacion=" + nEstimacion + ", cLogin=" + cLogin + ", nEstimacionCancelado=" + nEstimacionCancelado + ", folioNotaCancelada=" + folioNotaCancelada + ", montos=" + Arrays.toString(montos) + ", numFirmante=" + numFirmante + "]";
    }
}
