package com.syc.altaproveedor;

import java.util.List;
import java.util.Base64;

/**
 * @author SYC
 */
public class DatosProveedor {

    private String cFolio;

    private int nIdCaso;

    private int nTipoPersona;

    private int nIdOper;

    private String cTipoPB;

    private int nNumEmpleado;

    private String rfc;

    private String rfc1;

    private String rfc2;

    private String rfc3;

    private String cCurp;

    private int nExtranjero;

    private String cRazonSocial;

    private String cApellidoPat;

    private String cApellidoMat;

    private String cNombre;

    private String cGiro;

    private int nPyme;

    private String cPais;

    private int nEntidadFederativa;

    private int nMunicipio;

    private String cCalle;

    private String cNumeroExt;

    private String cNumeroInt;

    private String cColonia;

    private String cCodigoPost;

    private String cPaginaWeb;

    private String cEmail;

    private String cTelefono;

    private int nTipoTelefono;

    private String cTituloAplicacion;

    private String cDocumentoHaplicado;

    private int nProveedorHabilitado;

    private String cObservaciones;

    private boolean autoriza;

    private List<DatosCtaBancaria> cuentasBancarias;

    private int idRegimenFiscal;

    private String numRepse;

    private String esAltaRapida = "N";

    private String cLocalidad;

    public String getEsAltaRapida() {
        return esAltaRapida;
    }

    public void setEsAltaRapida(String esAltaRapida) {
        this.esAltaRapida = esAltaRapida;
    }

    public int getIdRegimenFiscal() {
        return idRegimenFiscal;
    }

    public void setIdRegimenFiscal(int idRegimenFiscal) {
        this.idRegimenFiscal = idRegimenFiscal;
    }

    public String getNumRepse() {
        return numRepse;
    }

    public void setNumRepse(String numRepse) {
        this.numRepse = numRepse;
    }

    public String getcFolio() {
        return cFolio;
    }

    public void setcFolio(String cFolio) {
        this.cFolio = cFolio;
    }

    public int getnIdCaso() {
        return nIdCaso;
    }

    public void setnIdCaso(int nIdCaso) {
        this.nIdCaso = nIdCaso;
    }

    public int getnTipoPersona() {
        return nTipoPersona;
    }

    public void setnTipoPersona(int nTipoPersona) {
        this.nTipoPersona = nTipoPersona;
    }

    public int getnIdOper() {
        return nIdOper;
    }

    public void setnIdOper(int nIdOper) {
        this.nIdOper = nIdOper;
    }

    public String getcTipoPB() {
        return cTipoPB;
    }

    public void setcTipoPB(String cTipoPB) {
        this.cTipoPB = cTipoPB;
    }

    public int getnNumEmpleado() {
        return nNumEmpleado;
    }

    public void setnNumEmpleado(int nNumEmpleado) {
        this.nNumEmpleado = nNumEmpleado;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public String getcCurp() {
        return cCurp;
    }

    public void setcCurp(String cCurp) {
        this.cCurp = cCurp;
    }

    public String getcRazonSocial() {
        return cRazonSocial;
    }

    public void setcRazonSocial(String cRazonSocial) {
        this.cRazonSocial = cRazonSocial;
    }

    public String getcApellidoPat() {
        return cApellidoPat;
    }

    public void setcApellidoPat(String cApellidoPat) {
        this.cApellidoPat = cApellidoPat;
    }

    public String getcApellidoMat() {
        return cApellidoMat;
    }

    public void setcApellidoMat(String cApellidoMat) {
        this.cApellidoMat = cApellidoMat;
    }

    public String getcNombre() {
        return cNombre;
    }

    public void setcNombre(String cNombre) {
        this.cNombre = cNombre;
    }

    public String getcGiro() {
        return cGiro;
    }

    public void setcGiro(String cGiro) {
        this.cGiro = cGiro;
    }

    public int getnPyme() {
        return nPyme;
    }

    public void setnPyme(int nPyme) {
        this.nPyme = nPyme;
    }

    public String getcPais() {
        return cPais;
    }

    public void setcPais(String cPais) {
        this.cPais = cPais;
    }

    public int getnEntidadFederativa() {
        return nEntidadFederativa;
    }

    public void setnEntidadFederativa(int nEntidadFederativa) {
        this.nEntidadFederativa = nEntidadFederativa;
    }

    public int getnMunicipio() {
        return nMunicipio;
    }

    public void setnMunicipio(int nMunicipio) {
        this.nMunicipio = nMunicipio;
    }

    public String getcCalle() {
        return cCalle;
    }

    public void setcCalle(String cCalle) {
        this.cCalle = cCalle;
    }

    public String getcNumeroExt() {
        return cNumeroExt;
    }

    public void setcNumeroExt(String cNumeroExt) {
        this.cNumeroExt = cNumeroExt;
    }

    public String getcNumeroInt() {
        return cNumeroInt;
    }

    public void setcNumeroInt(String cNumeroInt) {
        this.cNumeroInt = cNumeroInt;
    }

    public String getcColonia() {
        return cColonia;
    }

    public void setcColonia(String cColonia) {
        this.cColonia = cColonia;
    }

    public String getcCodigoPost() {
        return cCodigoPost;
    }

    public void setcCodigoPost(String cCodigoPost) {
        this.cCodigoPost = cCodigoPost;
    }

    public String getcPaginaWeb() {
        return cPaginaWeb;
    }

    public void setcPaginaWeb(String cPaginaWeb) {
        this.cPaginaWeb = cPaginaWeb;
    }

    public String getcEmail() {
        return cEmail;
    }

    public void setcEmail(String cEmail) {
        this.cEmail = cEmail;
    }

    public String getcTelefono() {
        return cTelefono;
    }

    public void setcTelefono(String cTelefono) {
        this.cTelefono = cTelefono;
    }

    public String getRfc1() {
        return rfc1;
    }

    public void setRfc1(String rfc1) {
        this.rfc1 = rfc1;
    }

    public String getRfc2() {
        return rfc2;
    }

    public void setRfc2(String rfc2) {
        this.rfc2 = rfc2;
    }

    public String getRfc3() {
        return rfc3;
    }

    public void setRfc3(String rfc3) {
        this.rfc3 = rfc3;
    }

    public int getnTipoTelefono() {
        return nTipoTelefono;
    }

    public void setnTipoTelefono(int nTipoTelefono) {
        this.nTipoTelefono = nTipoTelefono;
    }

    public int getnExtranjero() {
        return nExtranjero;
    }

    public void setnExtranjero(int nExtranjero) {
        this.nExtranjero = nExtranjero;
    }

    public String getcTituloAplicacion() {
        return cTituloAplicacion;
    }

    public void setcTituloAplicacion(String cTituloAplicacion) {
        this.cTituloAplicacion = cTituloAplicacion;
    }

    public String getcDocumentoHaplicado() {
        return cDocumentoHaplicado;
    }

    public void setcDocumentoHaplicado(String cDocumentoHaplicado) {
        this.cDocumentoHaplicado = cDocumentoHaplicado;
    }

    public int getnProveedorHabilitado() {
        return nProveedorHabilitado;
    }

    public void setnProveedorHabilitado(int nProveedorHabilitado) {
        this.nProveedorHabilitado = nProveedorHabilitado;
    }

    public String getcObservaciones() {
        return cObservaciones;
    }

    public void setcObservaciones(String cObservaciones) {
        this.cObservaciones = cObservaciones;
    }

    public boolean isAutoriza() {
        return autoriza;
    }

    public void setAutoriza(boolean autoriza) {
        this.autoriza = autoriza;
    }

    public List<DatosCtaBancaria> getCuentasBancarias() {
        return cuentasBancarias;
    }

    public void setCuentasBancarias(List<DatosCtaBancaria> cuentasBancarias) {
        this.cuentasBancarias = cuentasBancarias;
    }

    public String getcLocalidad() {
        return cLocalidad;
    }

    public void setcLocalidad(String cLocalidad) {
        this.cLocalidad = cLocalidad;
    }
}
