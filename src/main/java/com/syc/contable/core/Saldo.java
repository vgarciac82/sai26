package com.syc.contable.core;
import java.io.Serializable;

public class Saldo implements Serializable {

	private final static long serialVersionUID = 1;
	private String proyecto;
	private String dCuenta;
	private String ep;
	private String EjercicioFiscal;
	private String Cuenta;
	private String UnidadEjecutora;
	private String RamoEP;
	private String UnidadResponsableEP;
	private String GrupoFuncional;
	private String Funcion;
	private String SubFuncion;
	private String ProgramaGeneral;
	private String ProgramaPresupuestario;
	private String ActividadInstitucional;
	private String Partida;
	private String TipoGasto;
	private String FuenteFinanciamiento;
	private String EntidadFederativa;
	private String Cartera;
	private String UnidadNorativa;
	private String ClaveCNA;
	private String Mes;
	private String SaldoArrastre;
	private String ClaveSIAFF;
	private String ClaveInterna;
	private String MontoEnero;
	private String MontoFebrero;
	private String MontoMarzo;
	private String MontoAbril;
	private String MontoMayo;
	private String MontoJunio;
	private String MontoJulio;
	private String MontoAgosto;
	private String MontoSeptiembre;
	private String MontoOctubre;
	private String MontoNoviembre;
	private String MontoDiciembre;
	private String MontoAnual;
	private String MontoOriginal;
	private String MontoModificado;
	private String MontoAmpliacionAutorizada;
	private String MontoReduccionAutorizada;
	private String MontoAmpliacionTramite;
	private String MontoReduccionTramite;
	private String MontoApartado;
	private String MontoPrecomprometido;
	private String MontoComprometido;
	private String MontoDevengado;
	private String MontoEjercidoPagado;
	private String MontoEjercidoNoPagado;
	private String MontoDisponibleNeto;
	private String MontoDisponibleNetoRadicado;
	private String MontoDisponibleBruto;
	private String MensajeSecuencia;
	private String MontoReintegroTramite;	
	private String MontoRectificacion;
	private String MontoReduccionSHCPTramite;
	private String MontoReduccionSHCPAplicada;
	private String MontoPrecompromisoMateriales;
	
	public String getMensajeSecuencia() {
		return MensajeSecuencia;
	}
	public void setMensajeSecuencia(String mensajeSecuencia) {
		MensajeSecuencia = mensajeSecuencia;
	}
	public String getMontoOriginal() {
		return MontoOriginal;
	}
	public void setMontoOriginal(String montoOriginal) {
		MontoOriginal = montoOriginal;
	}
	public String getMontoModificado() {
		return MontoModificado;
	}
	public void setMontoModificado(String montoModificado) {
		MontoModificado = montoModificado;
	}
	public String getMontoAmpliacionAutorizada() {
		return MontoAmpliacionAutorizada;
	}
	public void setMontoAmpliacionAutorizada(String montoAmpliacionAutorizada) {
		MontoAmpliacionAutorizada = montoAmpliacionAutorizada;
	}
	public String getMontoReduccionAutorizada() {
		return MontoReduccionAutorizada;
	}
	public void setMontoReduccionAutorizada(String montoReduccionAutorizada) {
		MontoReduccionAutorizada = montoReduccionAutorizada;
	}
	public String getMontoAmpliacionTramite() {
		return MontoAmpliacionTramite;
	}
	public void setMontoAmpliacionTramite(String montoAmpliacionTramite) {
		MontoAmpliacionTramite = montoAmpliacionTramite;
	}
	public String getMontoReduccionTramite() {
		return MontoReduccionTramite;
	}
	public void setMontoReduccionTramite(String montoReduccionTramite) {
		MontoReduccionTramite = montoReduccionTramite;
	}
	public String getMontoApartado() {
		return MontoApartado;
	}
	public void setMontoApartado(String montoApartado) {
		MontoApartado = montoApartado;
	}
	public String getMontoPrecomprometido() {
		return MontoPrecomprometido;
	}
	public void setMontoPrecomprometido(String montoPrecomprometido) {
		MontoPrecomprometido = montoPrecomprometido;
	}
	public String getMontoComprometido() {
		return MontoComprometido;
	}
	public void setMontoComprometido(String montoComprometido) {
		MontoComprometido = montoComprometido;
	}
	public String getMontoDevengado() {
		return MontoDevengado;
	}
	public void setMontoDevengado(String montoDevengado) {
		MontoDevengado = montoDevengado;
	}
	public String getMontoEjercidoPagado() {
		return MontoEjercidoPagado;
	}
	public void setMontoEjercidoPagado(String montoEjercidoPagado) {
		MontoEjercidoPagado = montoEjercidoPagado;
	}
	public String getMontoEjercidoNoPagado() {
		return MontoEjercidoNoPagado;
	}
	public void setMontoEjercidoNoPagado(String montoEjercidoNoPagado) {
		 MontoEjercidoNoPagado = montoEjercidoNoPagado;
	}
	public String getMontoDisponibleNeto() {
		return MontoDisponibleNeto;
	}
	public void setMontoDisponibleNeto(String montoDisponibleNeto) {
		MontoDisponibleNeto = montoDisponibleNeto;
	}
	public String getMontoDisponibleBruto() {
		return MontoDisponibleBruto;
	}
	public void setMontoDisponibleBruto(String montoDisponibleBruto) {
		MontoDisponibleBruto = montoDisponibleBruto;
	}

	public String getMontoReintegroTramite() {
		return MontoReintegroTramite;
	}
	public void setMontoReintegroTramite(String montoReintegroTramite) {
		MontoReintegroTramite = montoReintegroTramite;
	}
	public String getMontoRectificacion() {
		return MontoRectificacion;
	}
	public void setMontoRectificacion(String montoRectificacion) {
		MontoRectificacion = montoRectificacion;
	}
	public String getMontoReduccionSHCPTramite() {
		return MontoReduccionSHCPTramite;
	}
	public void setMontoReduccionSHCPTramite(String montoReduccionSHCPTramite) {
		MontoReduccionSHCPTramite = montoReduccionSHCPTramite;
	}
	public String getMontoReduccionSHCPAplicada() {
		return MontoReduccionSHCPAplicada;
	}
	public void setMontoReduccionSHCPAplicada(String montoReduccionSHCPAplicada) {
		MontoReduccionSHCPAplicada = montoReduccionSHCPAplicada;
	}
	public String getMontoPrecompromisoMateriales() {
		return MontoPrecompromisoMateriales;
	}
	public void setMontoPrecompromisoMateriales(String montoPrecompromisoMateriales) {
		MontoPrecompromisoMateriales = montoPrecompromisoMateriales;
	}
	public String getClaveSIAFF() {
		return ClaveSIAFF;
	}
	public void setClaveSIAFF(String claveSIAFF) {
		ClaveSIAFF = claveSIAFF;
	}
	public String getClaveInterna() {
		return ClaveInterna;
	}
	public void setClaveInterna(String claveInterna) {
		ClaveInterna = claveInterna;
	}
	public String getMontoEnero() {
		return MontoEnero;
	}
	public void setMontoEnero(String montoEnero) {
		MontoEnero = montoEnero;
	}
	public String getMontoFebrero() {
		return MontoFebrero;
	}
	public void setMontoFebrero(String montoFebrero) {
		MontoFebrero = montoFebrero;
	}
	public String getMontoMarzo() {
		return MontoMarzo;
	}
	public void setMontoMarzo(String montoMarzo) {
		MontoMarzo = montoMarzo;
	}
	public String getMontoAbril() {
		return MontoAbril;
	}
	public void setMontoAbril(String montoAbril) {
		MontoAbril = montoAbril;
	}
	public String getMontoMayo() {
		return MontoMayo;
	}
	public void setMontoMayo(String montoMayo) {
		MontoMayo = montoMayo;
	}
	public String getMontoJunio() {
		return MontoJunio;
	}
	public void setMontoJunio(String montoJunio) {
		MontoJunio = montoJunio;
	}
	public String getMontoJulio() {
		return MontoJulio;
	}
	public void setMontoJulio(String montoJulio) {
		MontoJulio = montoJulio;
	}
	public String getMontoAgosto() {
		return MontoAgosto;
	}
	public void setMontoAgosto(String montoAgosto) {
		MontoAgosto = montoAgosto;
	}
	public String getMontoSeptiembre() {
		return MontoSeptiembre;
	}
	public void setMontoSeptiembre(String montoSeptiembre) {
		MontoSeptiembre = montoSeptiembre;
	}
	public String getMontoOctubre() {
		return MontoOctubre;
	}
	public void setMontoOctubre(String montoOctubre) {
		MontoOctubre = montoOctubre;
	}
	public String getMontoNoviembre() {
		return MontoNoviembre;
	}
	public void setMontoNoviembre(String montoNoviembre) {
		MontoNoviembre = montoNoviembre;
	}
	public String getMontoDiciembre() {
		return MontoDiciembre;
	}
	public void setMontoDiciembre(String montoDiciembre) {
		MontoDiciembre = montoDiciembre;
	}
	public String getMontoAnual() {
		return MontoAnual;
	}
	public void setMontoAnual(String montoAnual) {
		MontoAnual = montoAnual;
	}
	public String getSaldoArrastre() {
		return SaldoArrastre;
	}
	public void setSaldoArrastre(String saldoArrastre) {
		SaldoArrastre = saldoArrastre;
	}
	public String getDcuenta() {
		return dCuenta;
	}
	public void setDcuenta(String dCuenta) {
		this.dCuenta = dCuenta;
	}
	public String getEp() {
		return ep;
	}
	public void setEp(String ep) {
		this.ep = ep;
	}
	public String getEjercicioFiscal() {
		return EjercicioFiscal;
	}
	public void setEjercicioFiscal(String ejercicioFiscal) {
		EjercicioFiscal = ejercicioFiscal;
	}
	public String getCuenta() {
		return Cuenta;
	}
	public void setCuenta(String cuenta) {
		Cuenta = cuenta;
	}
	public String getUnidadEjecutora() {
		return UnidadEjecutora;
	}
	public void setUnidadEjecutora(String unidadEjecutora) {
		UnidadEjecutora = unidadEjecutora;
	}
	public String getRamoEP() {
		return RamoEP;
	}
	public void setRamoEP(String ramoEP) {
		RamoEP = ramoEP;
	}
	public String getUnidadResponsableEP() {
		return UnidadResponsableEP;
	}
	public void setUnidadResponsableEP(String unidadResponsableEP) {
		UnidadResponsableEP = unidadResponsableEP;
	}
	public String getGrupoFuncional() {
		return GrupoFuncional;
	}
	public void setGrupoFuncional(String grupoFuncional) {
		GrupoFuncional = grupoFuncional;
	}
	public String getFuncion() {
		return Funcion;
	}
	public void setFuncion(String funcion) {
		Funcion = funcion;
	}
	public String getSubFuncion() {
		return SubFuncion;
	}
	public void setSubFuncion(String subFuncion) {
		SubFuncion = subFuncion;
	}
	public String getProgramaGeneral() {
		return ProgramaGeneral;
	}
	public void setProgramaGeneral(String programaGeneral) {
		ProgramaGeneral = programaGeneral;
	}
	public String getProgramaPresupuestario() {
		return ProgramaPresupuestario;
	}
	public void setProgramaPresupuestario(String programaPresupuestario) {
		ProgramaPresupuestario = programaPresupuestario;
	}
	public String getActividadInstitucional() {
		return ActividadInstitucional;
	}
	public void setActividadInstitucional(String actividadInstitucional) {
		ActividadInstitucional = actividadInstitucional;
	}
	public String getPartida() {
		return Partida;
	}
	public void setPartida(String partida) {
		Partida = partida;
	}
	public String getTipoGasto() {
		return TipoGasto;
	}
	public void setTipoGasto(String tipoGasto) {
		TipoGasto = tipoGasto;
	}
	public String getFuenteFinanciamiento() {
		return FuenteFinanciamiento;
	}
	public void setFuenteFinanciamiento(String fuenteFinanciamiento) {
		FuenteFinanciamiento = fuenteFinanciamiento;
	}
	public String getEntidadFederativa() {
		return EntidadFederativa;
	}
	public void setEntidadFederativa(String entidadFederativa) {
		EntidadFederativa = entidadFederativa;
	}
	public String getCartera() {
		return Cartera;
	}
	public void setCartera(String cartera) {
		Cartera = cartera;
	}
	public String getUnidadNorativa() {
		return UnidadNorativa;
	}
	public void setUnidadNorativa(String unidadNorativa) {
		UnidadNorativa = unidadNorativa;
	}
	public String getClaveCNA() {
		return ClaveCNA;
	}
	public void setClaveCNA(String claveCNA) {
		ClaveCNA = claveCNA;
	}
	public String getMes() {
		return Mes;
	}
	public void setMes(String mes) {
		Mes = mes;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setProyecto( String proyecto ){
		this.proyecto = proyecto;
	}
	
	public String getProyecto(){
		return this.proyecto;
	}
	public void setMontoDisponibleNetoRadicado(String montoDisponibleNetoRadicado) {
		MontoDisponibleNetoRadicado = montoDisponibleNetoRadicado;
		
	}
	public String getMontoDisponibleNetoRadicado() {
		return MontoDisponibleNetoRadicado;
	}
	
}
