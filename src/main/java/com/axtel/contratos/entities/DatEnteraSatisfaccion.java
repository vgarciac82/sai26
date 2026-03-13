package com.axtel.contratos.entities;

import java.util.Arrays;

public class DatEnteraSatisfaccion {
	private int nServicioEnteraSatisfaccion;
	private String cFolio;
	private String cIdContratoDefinitivo;
	private String cNoContratoCNET;
	private int nIdLinea;
	private int nIdPeriodoPago;
	private int nCentroTrabajo;
	private int nNumEmpFirmante;
	private String cNombreEmpFirmante;
	private String cPuestoFirmante;
	private String cFolioFirmante;
	private int nServPrestEnteraSatisfaccion;
	private String cIdRFC;
	private String cObservacionesTramite;
	private String cUsuarioCaptura;
	private String cUsuarioValida;
	private int nIdEstatus;
	private DatAnexo1A anexo;
	private DatActaHechos actaHechos;
	private int nIdCaso;
	private String[] resp;
	private String[] oper;
	private String email;
	
	public String getcFolio() {
		return cFolio;
	}
	
	public void setcFolio( String cFolio ) {
		this.cFolio = cFolio;
	}
	
	public String getcIdContratoDefinitivo() {
		return cIdContratoDefinitivo;
	}
	
	public void setcIdContratoDefinitivo( String cIdContratoDefinitivo ) {
		this.cIdContratoDefinitivo = cIdContratoDefinitivo;
	}
	
	public String getcNoContratoCNET() {
		return cNoContratoCNET;
	}
	
	public void setcNoContratoCNET( String cNoContratoCNET ) {
		this.cNoContratoCNET = cNoContratoCNET;
	}
	
	public int getnIdLinea() {
		return nIdLinea;
	}
	
	public void setnIdLinea( int nIdLinea ) {
		this.nIdLinea = nIdLinea;
	}
	
	public int getnIdPeriodoPago() {
		return nIdPeriodoPago;
	}
	
	public void setnIdPeriodoPago( int nIdPeriodoPago ) {
		this.nIdPeriodoPago = nIdPeriodoPago;
	}
	
	public int getnCentroTrabajo() {
		return nCentroTrabajo;
	}
	
	public void setnCentroTrabajo( int nCentroTrabajo ) {
		this.nCentroTrabajo = nCentroTrabajo;
	}
	
	public int getnNumEmpFirmante() {
		return nNumEmpFirmante;
	}
	
	public void setnNumEmpFirmante( int nNumEmpFirmante ) {
		this.nNumEmpFirmante = nNumEmpFirmante;
	}
	
	public String getcPuestoFirmante() {
		return cPuestoFirmante;
	}
	
	public void setcPuestoFirmante( String cPuestoFirmante ) {
		this.cPuestoFirmante = cPuestoFirmante;
	}
	
	public String getcFolioFirmante() {
		return cFolioFirmante;
	}
	
	public void setcFolioFirmante( String cFolioFirmante ) {
		this.cFolioFirmante = cFolioFirmante;
	}
	
	public int getnServPrestEnteraSatisfaccion() {
		return nServPrestEnteraSatisfaccion;
	}
	
	public void setnServPrestEnteraSatisfaccion( int nServPrestEnteraSatisfaccion ) {
		this.nServPrestEnteraSatisfaccion = nServPrestEnteraSatisfaccion;
	}
	
	public String getcIdRFC() {
		return cIdRFC;
	}
	
	public void setcIdRFC( String cIdRFC ) {
		this.cIdRFC = cIdRFC;
	}
	
	public String getcObservacionesTramite() {
		return cObservacionesTramite;
	}
	
	public void setcObservacionesTramite( String cObservacionesTramite ) {
		this.cObservacionesTramite = cObservacionesTramite;
	}
	
	public String getcUsuarioCaptura() {
		return cUsuarioCaptura;
	}
	
	public void setcUsuarioCaptura( String cUsuarioCaptura ) {
		this.cUsuarioCaptura = cUsuarioCaptura;
	}
	
	public String getcUsuarioValida() {
		return cUsuarioValida;
	}
	
	public void setcUsuarioValida( String cUsuarioValida ) {
		this.cUsuarioValida = cUsuarioValida;
	}
	
	public int getnIdEstatus() {
		return nIdEstatus;
	}
	
	public void setnIdEstatus( int nIdEstatus ) {
		this.nIdEstatus = nIdEstatus;
	}
	
	public DatAnexo1A getAnexo() {
		return anexo;
	}
	
	public void setAnexo( DatAnexo1A anexo ) {
		this.anexo = anexo;
	}
	
	public DatActaHechos getActaHechos() {
		return actaHechos;
	}
	
	public void setActaHechos( DatActaHechos actaHechos ) {
		this.actaHechos = actaHechos;
	}
	
	public int getnServicioEnteraSatisfaccion() {
		return nServicioEnteraSatisfaccion;
	}

	public void setnServicioEnteraSatisfaccion( int nServicioEnteraSatisfaccion ) {
		this.nServicioEnteraSatisfaccion = nServicioEnteraSatisfaccion;
	}
	
	public int getnIdCaso() {
		return nIdCaso;
	}

	public void setnIdCaso( int nIdCaso ) {
		this.nIdCaso = nIdCaso;
	}
	
	public String getcNombreEmpFirmante() {
		return cNombreEmpFirmante;
	}
	
	public void setcNombreEmpFirmante( String cNombreEmpFirmante ) {
		this.cNombreEmpFirmante = cNombreEmpFirmante;
	}

	public String[] getResp() {
		return resp;
	}

	public void setResp( String[] resp ) {
		this.resp = resp;
	}

	public String[] getOper() {
		return oper;
	}

	public void setOper( String[] oper ) {
		this.oper = oper;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail( String email ) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "DatEnteraSatisfaccion [nServicioEnteraSatisfaccion=" + nServicioEnteraSatisfaccion + ", cFolio=" + cFolio + ", cIdContratoDefinitivo=" + cIdContratoDefinitivo + ", cNoContratoCNET=" + cNoContratoCNET + ", nIdLinea=" + nIdLinea + ", nIdPeriodoPago=" + nIdPeriodoPago + ", nCentroTrabajo=" + nCentroTrabajo + ", nNumEmpFirmante=" + nNumEmpFirmante + ", cNombreEmpFirmante=" + cNombreEmpFirmante + ", cPuestoFirmante=" + cPuestoFirmante + ", cFolioFirmante=" + cFolioFirmante + ", nServPrestEnteraSatisfaccion=" + nServPrestEnteraSatisfaccion + ", cIdRFC=" + cIdRFC + ", cObservacionesTramite=" + cObservacionesTramite + ", cUsuarioCaptura=" + cUsuarioCaptura + ", cUsuarioValida=" + cUsuarioValida + ", nIdEstatus=" + nIdEstatus + ", anexo=" + anexo + ", actaHechos=" + actaHechos + ", nIdCaso=" + nIdCaso + ", resp=" + Arrays.toString( resp ) + ", oper=" + Arrays.toString( oper ) + ", email=" + email + "]";
	}

		
}
