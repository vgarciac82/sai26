package com.syc.contable.core;

import java.io.Serializable;
import java.util.Base64;

public class AnteProyectoAut implements Serializable {

    private final static long serialVersionUID = 1;

    //Parametros de la PK del Encabezado y PK compuesta del Detalle
    private String FolioAnteProyectoAut;

    private String EjercicioFiscal;

    private String UnidadResponsableEP;

    //Campos del Encabezado
    private String H_FechaCarga;

    private String H_FolioAnteProyectoAutAnterior;

    private String H_EjercicioFiscalAnterior;

    private String Cuenta;

    private String H_NIncremento;

    private String H_NDecremento;

    private String H_CCampoCalculo;

    private String H_CRevisor;

    private String H_CAutorizado;

    private String H_CDescripcion;

    private String H_CUnidadNormativa;

    //Parametro de la PK compuesta del Detalle
    private String D_NConsecutivo;

    private String D_NFolioAnteProyecto;

    private String D_CUnidadEjecutora;

    private String D_CClaveSiaff;

    private String D_CClaveInterna;

    private String D_MCalculado;

    private String D_MOptimo;

    private String D_MIreductible;

    private String D_MPorcentajeReduccion;

    private String D_MPorcentajeIncremento;

    private String D_CMotivoRechazo;

    /*
	private String ep;
	private String UnidadEjecutora;
	private String RamoEP;
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
	*/
    public String getFolioAnteProyectoAut() {
        return FolioAnteProyectoAut;
    }

    public void setFolioAnteProyectoAut(String folioAnteProyectoAut) {
        FolioAnteProyectoAut = folioAnteProyectoAut;
    }

    public String getEjercicioFiscal() {
        return EjercicioFiscal;
    }

    public void setEjercicioFiscal(String ejercicioFiscal) {
        EjercicioFiscal = ejercicioFiscal;
    }

    public String getUnidadResponsableEP() {
        return UnidadResponsableEP;
    }

    public void setUnidadResponsableEP(String unidadResponsableEP) {
        UnidadResponsableEP = unidadResponsableEP;
    }

    public String getH_FechaCarga() {
        return H_FechaCarga;
    }

    public void setH_FechaCarga(String fCarga) {
        H_FechaCarga = fCarga;
    }

    public String getH_FolioAnteProyectoAutAnterior() {
        return H_FolioAnteProyectoAutAnterior;
    }

    public void setH_FolioAnteProyectoAutAnterior(String folioAnteProyectoAutAnterior) {
        H_FolioAnteProyectoAutAnterior = folioAnteProyectoAutAnterior;
    }

    public String getH_EjercicioFiscalAnterior() {
        return H_EjercicioFiscalAnterior;
    }

    public void setH_EjercicioFiscalAnterior(String ejercicioFiscalAnterior) {
        H_EjercicioFiscalAnterior = ejercicioFiscalAnterior;
    }

    public String getCuenta() {
        return Cuenta;
    }

    public void setCuenta(String cuenta) {
        Cuenta = cuenta;
    }

    public String getH_NIncremento() {
        return H_NIncremento;
    }

    public void setH_NIncremento(String nIncremento) {
        H_NIncremento = nIncremento;
    }

    public String getH_NDecremento() {
        return H_NDecremento;
    }

    public void setH_NDecremento(String nDecremento) {
        H_NDecremento = nDecremento;
    }

    public String getH_CCampoCalculo() {
        return H_CCampoCalculo;
    }

    public void setH_CCampoCalculo(String cCampoCalculo) {
        H_CCampoCalculo = cCampoCalculo;
    }

    public String getH_CRevisor() {
        return H_CRevisor;
    }

    public void setH_CRevisor(String cRevisor) {
        H_CRevisor = cRevisor;
    }

    public String getH_CAutorizado() {
        return H_CAutorizado;
    }

    public void setH_CAutorizado(String cAutorizado) {
        H_CAutorizado = cAutorizado;
    }

    public String getH_CDescripcion() {
        return H_CDescripcion;
    }

    public void setH_CDescripcion(String cDescripcion) {
        H_CDescripcion = cDescripcion;
    }

    public String getH_CUnidadNormativa() {
        return H_CUnidadNormativa;
    }

    public void setH_CUnidadNormativa(String cUnidadNormativa) {
        H_CUnidadNormativa = cUnidadNormativa;
    }

    public String getD_NConsecutivo() {
        return D_NConsecutivo;
    }

    public void setD_NConsecutivo(String nConsecutivo) {
        D_NConsecutivo = nConsecutivo;
    }

    public String getD_NFolioAnteProyecto() {
        return D_NFolioAnteProyecto;
    }

    public void setD_NFolioAnteProyecto(String nFolioAnteProyecto) {
        D_NFolioAnteProyecto = nFolioAnteProyecto;
    }

    public String getD_CUnidadEjecutora() {
        return D_CUnidadEjecutora;
    }

    public void setD_CUnidadEjecutora(String cUnidadEjecutora) {
        D_CUnidadEjecutora = cUnidadEjecutora;
    }

    public String getD_CClaveSiaff() {
        return D_CClaveSiaff;
    }

    public void setD_CClaveSIAFF(String cClaveSiaff) {
        D_CClaveSiaff = cClaveSiaff;
    }

    public String getD_CClaveInterna() {
        return D_CClaveInterna;
    }

    public void setD_CClaveInterna(String cClaveInterna) {
        D_CClaveInterna = cClaveInterna;
    }

    public String getD_MCalculado() {
        return D_MCalculado;
    }

    public void setD_MCalculado(String mCalculado) {
        D_MCalculado = mCalculado;
    }

    public String getD_MOptimo() {
        return D_MOptimo;
    }

    public void setD_MOptimo(String mOptimo) {
        D_MOptimo = mOptimo;
    }

    public String getD_MIreductible() {
        return D_MIreductible;
    }

    public void setD_MIreductible(String mIreductible) {
        D_MIreductible = mIreductible;
    }

    public String getD_MPorcentajeReduccion() {
        return D_MPorcentajeReduccion;
    }

    public void setD_MPorcentajeReduccion(String mPorcentajeReduccion) {
        D_MPorcentajeReduccion = mPorcentajeReduccion;
    }

    public String getD_MPorcentajeIncremento() {
        return D_MPorcentajeIncremento;
    }

    public void setD_MPorcentajeIncremento(String mPorcentajeIncremento) {
        D_MPorcentajeIncremento = mPorcentajeIncremento;
    }

    public String getD_CMotivoRechazo() {
        return D_CMotivoRechazo;
    }

    public void setD_CMotivoRechazo(String motivoRechazo) {
        D_CMotivoRechazo = motivoRechazo;
    }

    /*
	public String getEp() {
		return ep;
	}
	public void setEp(String ep) {
		this.ep = ep;
	}
	
	public String getDcuenta() {
		return dCuenta;
	}
	public void setDcuenta(String dCuenta) {
		this.dCuenta = dCuenta;
	}
	
	
	
	public String getRamoEP() {
		return RamoEP;
	}
	public void setRamoEP(String ramoEP) {
		RamoEP = ramoEP;
	}
	
	/*
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
	*/
    public static long getSerialversionuid() {
        return serialVersionUID;
    }
}
