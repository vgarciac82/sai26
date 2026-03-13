package com.syc.obrapublica;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.syc.gestion.core.Usuario;
import com.syc.sai.firmaElectronica.interfaces.SolicitudFirmaElectronica;

public interface FirmaAutorizacionObraInterface {
	public void enviaAutorizacion(Usuario usuario,HttpServletRequest request, String REPORT_PATH)throws Exception;
	public List<String> autoriza(SolicitudFirmaElectronica sfe, String cerFileName, String keyFileName)throws Exception;
	public List<String> rechaza(Usuario u,Map<String, String> objMap)throws Exception;
}
