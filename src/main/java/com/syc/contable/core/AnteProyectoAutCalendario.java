package com.syc.contable.core;

import java.io.Serializable;
import java.util.Base64;

public class AnteProyectoAutCalendario implements Serializable {

    private final static long serialVersionUID = 1;

    //Parametros de la PK
    private String NConsecutivo;

    private String NFolioAnteProyectoAut;

    private String AEjercicioFiscal;

    private String CUnidadResponsable;

    //Campos
    private String CClaveSiaff;

    private String CClaveInterna;

    private String CCentroContable;

    private String NFolioAnteProyecto;

    private String MAnualAutorizado;

    private String MEnero;

    private String MFebrero;

    private String MMarzo;

    private String MAbril;

    private String MMayo;

    private String MJunio;

    private String MJulio;

    private String MAgosto;

    private String MSeptiembre;

    private String MOctubre;

    private String MNoviembre;

    private String MDiciembre;

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
    public String getNConsecutivo() {
        return NConsecutivo;
    }

    public void setNConsecutivo(String nConsecutivo) {
        NConsecutivo = nConsecutivo;
    }

    public String getNFolioAnteProyectoAut() {
        return NFolioAnteProyectoAut;
    }

    public void setNFolioAnteProyectoAut(String nFolioAnteProyectoAut) {
        NFolioAnteProyectoAut = nFolioAnteProyectoAut;
    }

    public String getAEjercicioFiscal() {
        return AEjercicioFiscal;
    }

    public void setAEjercicioFiscal(String aEjercicioFiscal) {
        AEjercicioFiscal = aEjercicioFiscal;
    }

    public String getCUnidadResponsable() {
        return CUnidadResponsable;
    }

    public void setCUnidadResponsable(String cUnidadResponsable) {
        CUnidadResponsable = cUnidadResponsable;
    }

    public String getCClaveSiaff() {
        return CClaveSiaff;
    }

    public void setCClaveSiaff(String cClaveSiaff) {
        CClaveSiaff = cClaveSiaff;
    }

    public String getCClaveInterna() {
        return CClaveInterna;
    }

    public void setCClaveInterna(String cClaveInterna) {
        CClaveInterna = cClaveInterna;
    }

    public String getCCentroContable() {
        return CCentroContable;
    }

    public void setCCentroContable(String cCentroContable) {
        CCentroContable = cCentroContable;
    }

    public String getNFolioAnteProyecto() {
        return NFolioAnteProyecto;
    }

    public void setNFolioAnteProyecto(String nFolioAnteProyecto) {
        NFolioAnteProyecto = nFolioAnteProyecto;
    }

    public String getMAnualAutorizado() {
        return MAnualAutorizado;
    }

    public void setMAnualAutorizado(String mAnualAutorizado) {
        MAnualAutorizado = mAnualAutorizado;
    }

    public String getMEnero() {
        return MEnero;
    }

    public void setMEnero(String mEnero) {
        MEnero = mEnero;
    }

    public String getMFebrero() {
        return MFebrero;
    }

    public void setMFebrero(String mFebrero) {
        MFebrero = mFebrero;
    }

    public String getMMarzo() {
        return MMarzo;
    }

    public void setMMarzo(String mMarzo) {
        MMarzo = mMarzo;
    }

    public String getMAbril() {
        return MAbril;
    }

    public void setMAbril(String mAbril) {
        MAbril = mAbril;
    }

    public String getMMayo() {
        return MMayo;
    }

    public void setMMayo(String mMayo) {
        MMayo = mMayo;
    }

    public String getMJunio() {
        return MJunio;
    }

    public void setMJunio(String mJunio) {
        MJunio = mJunio;
    }

    public String getMJulio() {
        return MJulio;
    }

    public void setMJulio(String mJulio) {
        MJulio = mJulio;
    }

    public String getMAgosto() {
        return MAgosto;
    }

    public void setMAgosto(String mAgosto) {
        MAgosto = mAgosto;
    }

    public String getMSeptiembre() {
        return MSeptiembre;
    }

    public void setMSeptiembre(String mSeptiembre) {
        MSeptiembre = mSeptiembre;
    }

    public String getMOctubre() {
        return MOctubre;
    }

    public void setMOctubre(String mOctubre) {
        MOctubre = mOctubre;
    }

    public String getMNoviembre() {
        return MNoviembre;
    }

    public void setMNoviembre(String mNoviembre) {
        MNoviembre = mNoviembre;
    }

    public String getMDiciembre() {
        return MDiciembre;
    }

    public void setMDiciembre(String mDiciembre) {
        MDiciembre = mDiciembre;
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
