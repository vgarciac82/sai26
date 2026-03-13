package com.axtel.contratos.core;


/**
 * @author hfariasr
 *
 */
public class ContratoAmpliacion {
	private String cIdContratoDef;
	private String cIdUnidadEjecutora;
	private String cEjercicio;
	private int nIdConsecutivoAmpliacion;
	private int nIdEstado;
	private String cFolioPre;
	private int nConsecutivoPrecom;
	private String cUsuarioCreacion;
	private String cUsuarioCancelacion;
	private String cIdUnidadEjecutoraRequi;
	private String cNumCuentaDisponible;
	private int nCantidad;
	private double mMontoNeto;
	private double mSubtotal;
	private double mMontoIVA;
	
	public String getcIdContratoDef() {
		return cIdContratoDef;
	}
	
	public void setcIdContratoDef( String cIdContratoDef ) {
		this.cIdContratoDef = cIdContratoDef;
	}
	
	public String getcIdUnidadEjecutora() {
		return cIdUnidadEjecutora;
	}
	
	public void setcIdUnidadEjecutora( String cIdUnidadEjecutora ) {
		this.cIdUnidadEjecutora = cIdUnidadEjecutora;
	}
	
	public String getcEjercicio() {
		return cEjercicio;
	}
	
	public void setcEjercicio( String cEjercicio ) {
		this.cEjercicio = cEjercicio;
	}
	
	public int getnIdConsecutivoAmpliacion() {
		return nIdConsecutivoAmpliacion;
	}
	
	public void setnIdConsecutivoAmpliacion( int nIdConsecutivoAmpliacion ) {
		this.nIdConsecutivoAmpliacion = nIdConsecutivoAmpliacion;
	}
	
	public int getnIdEstado() {
		return nIdEstado;
	}
	
	public void setnIdEstado( int nIdEstado ) {
		this.nIdEstado = nIdEstado;
	}
	
	public String getcFolioPre() {
		return cFolioPre;
	}
	
	public void setcFolioPre( String cFolioPre ) {
		this.cFolioPre = cFolioPre;
	}
	
	public int getnConsecutivoPrecom() {
		return nConsecutivoPrecom;
	}
	
	public void setnConsecutivoPrecom( int nConsecutivoPrecom ) {
		this.nConsecutivoPrecom = nConsecutivoPrecom;
	}
	
	public String getcUsuarioCreacion() {
		return cUsuarioCreacion;
	}
	
	public void setcUsuarioCreacion( String cUsuarioCreacion ) {
		this.cUsuarioCreacion = cUsuarioCreacion;
	}
	
	public String getcUsuarioCancelacion() {
		return cUsuarioCancelacion;
	}
	
	public void setcUsuarioCancelacion( String cUsuarioCancelacion ) {
		this.cUsuarioCancelacion = cUsuarioCancelacion;
	}
	
	public String getcIdUnidadEjecutoraRequi() {
		return cIdUnidadEjecutoraRequi;
	}
	
	public void setcIdUnidadEjecutoraRequi( String cIdUnidadEjecutoraRequi ) {
		this.cIdUnidadEjecutoraRequi = cIdUnidadEjecutoraRequi;
	}
	
	public String getcNumCuentaDisponible() {
		return cNumCuentaDisponible;
	}
	
	public void setcNumCuentaDisponible( String cNumCuentaDisponible ) {
		this.cNumCuentaDisponible = cNumCuentaDisponible;
	}
	
	public int getnCantidad() {
		return nCantidad;
	}
	
	public void setnCantidad( int nCantidad ) {
		this.nCantidad = nCantidad;
	}
	
	public double getmMontoNeto() {
		return mMontoNeto;
	}
	
	public void setmMontoNeto( double mMontoNeto ) {
		this.mMontoNeto = mMontoNeto;
	}

	public double getmSubtotal() {
		return mSubtotal;
	}

	public void setmSubtotal( double mSubtotal ) {
		this.mSubtotal = mSubtotal;
	}

	public double getmMontoIVA() {
		return mMontoIVA;
	}

	public void setmMontoIVA( double mMontoIVA ) {
		this.mMontoIVA = mMontoIVA;
	}
		
}
