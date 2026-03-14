package com.syc.adquisiciones.core;

import java.io.InputStream;
import java.util.Base64;

public class DatosArchivo {

    private String cNombreArchivo;

    private String cExtencion;

    private InputStream archivoStream;

    private String cNombreArchivoDestino;

    private String cDominio;

    private String[] cRuta;

    private String namePlantilla;

    private String cIdContratoDefinitivo;

    private int nOperacion;

    private int nIdCaso;

    private String cContratoCNET;

    private String cFolio;

    private int nTipoTerminacionCont;

    private String cCausa;

    private String fFechaTermino;

    private String fFechaLimitePagoPendiente;

    private String cPrefixPath;

    private String jniName;

    private String fechaNotificacionUAF;

    private String cNumProcedimientoCNET;

    private String cNumExpedienteCNET;

    private String cCodigoContratoCNET;

    private String cIdRFC;

    private String cEjercicioContrato;

    private String cNameDB;

    private int nTienePagoPendiente;

    private String cNameCarpeta;

    private String cTituloAplicacion;

    private String cIdsolicitud;

    public String getFechaNotificacionUAF() {
        return fechaNotificacionUAF;
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

    public String getcNombreArchivoDestino() {
        return cNombreArchivoDestino;
    }

    public void setcNombreArchivoDestino(String cNombreArchivoDestino) {
        this.cNombreArchivoDestino = cNombreArchivoDestino;
    }

    public String getcDominio() {
        return cDominio;
    }

    public void setcDominio(String cDominio) {
        this.cDominio = cDominio;
    }

    public String[] getcRuta() {
        return cRuta;
    }

    public void setcRuta(String[] cRuta) {
        this.cRuta = cRuta;
    }

    public InputStream getArchivoStream() {
        return archivoStream;
    }

    public void setArchivoStream(InputStream archivoStream) {
        this.archivoStream = archivoStream;
    }

    public String getNamePlantilla() {
        return namePlantilla;
    }

    public void setNamePlantilla(String namePlantilla) {
        this.namePlantilla = namePlantilla;
    }

    public String getcIdContratoDefinitivo() {
        return cIdContratoDefinitivo;
    }

    public void setcIdContratoDefinitivo(String cIdContratoDefinitivo) {
        this.cIdContratoDefinitivo = cIdContratoDefinitivo;
    }

    public int getnOperacion() {
        return nOperacion;
    }

    public void setnOperacion(int nOperacion) {
        this.nOperacion = nOperacion;
    }

    public int getnIdCaso() {
        return nIdCaso;
    }

    public void setnIdCaso(int nIdCaso) {
        this.nIdCaso = nIdCaso;
    }

    public String getcContratoCNET() {
        return cContratoCNET;
    }

    public void setcContratoCNET(String cContratoCNET) {
        this.cContratoCNET = cContratoCNET;
    }

    public String getcFolio() {
        return cFolio;
    }

    public void setcFolio(String cFolio) {
        this.cFolio = cFolio;
    }

    public int getnTipoTerminacionCont() {
        return nTipoTerminacionCont;
    }

    public void setnTipoTerminacionCont(int nTipoTerminacionCont) {
        this.nTipoTerminacionCont = nTipoTerminacionCont;
    }

    public String getcCausa() {
        return cCausa;
    }

    public void setcCausa(String cCausa) {
        this.cCausa = cCausa;
    }

    public String getfFechaTermino() {
        return fFechaTermino;
    }

    public void setfFechaTermino(String fFechaTermino) {
        this.fFechaTermino = fFechaTermino;
    }

    public String getcPrefixPath() {
        return cPrefixPath;
    }

    public void setcPrefixPath(String cPrefixPath) {
        this.cPrefixPath = cPrefixPath;
    }

    public String getJniName() {
        return jniName;
    }

    public void setJniName(String jniName) {
        this.jniName = jniName;
    }

    public String getfFechaLimitePagoPendiente() {
        return fFechaLimitePagoPendiente;
    }

    public void setFechaNotificacionUAF(String fechaNotificacionUAF) {
        this.fechaNotificacionUAF = fechaNotificacionUAF;
    }

    public void setfFechaLimitePagoPendiente(String fFechaLimitePagoPendiente) {
        this.fFechaLimitePagoPendiente = fFechaLimitePagoPendiente;
    }

    public String getcNumProcedimientoCNET() {
        return cNumProcedimientoCNET;
    }

    public void setcNumProcedimientoCNET(String cNumProcedimientoCNET) {
        this.cNumProcedimientoCNET = cNumProcedimientoCNET;
    }

    public String getcNumExpedienteCNET() {
        return cNumExpedienteCNET;
    }

    public void setcNumExpedienteCNET(String cNumExpedienteCNET) {
        this.cNumExpedienteCNET = cNumExpedienteCNET;
    }

    public String getcCodigoContratoCNET() {
        return cCodigoContratoCNET;
    }

    public void setcCodigoContratoCNET(String cCodigoContratoCNET) {
        this.cCodigoContratoCNET = cCodigoContratoCNET;
    }

    public String getcIdRFC() {
        return cIdRFC;
    }

    public void setcIdRFC(String cIdRFC) {
        this.cIdRFC = cIdRFC;
    }

    public String getcEjercicioContrato() {
        return cEjercicioContrato;
    }

    public void setcEjercicioContrato(String cEjercicioContrato) {
        this.cEjercicioContrato = cEjercicioContrato;
    }

    public String getcNameDB() {
        return cNameDB;
    }

    public void setcNameDB(String cNameDB) {
        this.cNameDB = cNameDB;
    }

    public int getnTienePagoPendiente() {
        return nTienePagoPendiente;
    }

    public void setnTienePagoPendiente(int nTienePagoPendiente) {
        this.nTienePagoPendiente = nTienePagoPendiente;
    }

    public String getcNameCarpeta() {
        return cNameCarpeta;
    }

    public void setcNameCarpeta(String cNameCarpeta) {
        this.cNameCarpeta = cNameCarpeta;
    }

    public String getcTituloAplicacion() {
        return cTituloAplicacion;
    }

    public void setcTituloAplicacion(String cTituloAplicacion) {
        this.cTituloAplicacion = cTituloAplicacion;
    }

    public String getcIdsolicitud() {
        return cIdsolicitud;
    }

    public void setcIdsolicitud(String cIdsolicitud) {
        this.cIdsolicitud = cIdsolicitud;
    }
}
