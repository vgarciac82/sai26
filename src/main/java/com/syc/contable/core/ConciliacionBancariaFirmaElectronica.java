package com.syc.contable.core;


import java.sql.Connection;

import com.syc.fortimax.core.Carpeta;
import com.syc.fortimax.core.Volumen;
import com.syc.sai.firmaElectronica.exceptions.FirmaElectronicaException;
import com.syc.sai.firmaElectronica.interfaces.SolicitudFirmaElectronica;


public class ConciliacionBancariaFirmaElectronica extends SolicitudFirmaElectronica {

	@Override
	public String generaArchivoFirma( Connection conn, Volumen vol, Carpeta folder, boolean isSignedCopy ) throws FirmaElectronicaException {
		return null;
	}

	@Override
	public String getAutLegend( Connection conn ) throws Exception {
		return null;
	}

	@Override
	public String getCuerpoCorreoAutoriza( Connection conn ) throws Exception {
		return null;
	}

	@Override
	public String getCuerpoCorreoVistoBueno( Connection conn ) throws Exception {
		return null;
	}

	@Override
	public String getImporteStr( Connection conn ) throws Exception {
		return null;
	}

	@Override
	public String getVoBoLegend( Connection conn ) throws Exception {
		return null;
	}

	@Override
	public void notificaOperacionMasivaPendiente( Connection conn, String operacion ) throws Exception {

	}

	@Override
	public String notificaOperacionPendiente( Connection conn, String tipoAutorizacion ) throws Exception {
		return null;
	}

	@Override
	public void onCancelaTramite( Connection conn, String reason ) throws Exception {

	}

	@Override
	public void onFinishAut( Connection conn ) throws Exception {

	}

	@Override
	public void onFinishVoBo( Connection conn ) throws Exception {

	}

	@Override
	public void onGeneraArchivosMasivo( Connection conn ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String generaArchivoInformeComision( Connection conn, Volumen vol, Carpeta folder, boolean isSignedCopy ) throws FirmaElectronicaException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getVoBoLegendWithName( Connection conn , String nombre, String puesto) throws Exception {
		String voLegend = VO_BO_LEGEND_PREFIX.concat( " Firmado por: " ).concat( nombre ).concat( " | " ).concat ( puesto );
	
		if ( tieneDelegatorioVoBO( conn ) ) {
			voLegend = VO_BO_LEGEND_PREFIX + " Firma " + getTipoSuplencia() + " de " + getNombreEmpleadoSuplido() + " con fundamento en el oficio: " + getFolioOficioVoBo() + " de fecha: " + getFechaOficioVoBo();
		} 
		
		return voLegend;
	}
	
	@Override
	public String getAutLegendWithName( Connection conn, String nombre, String puesto ) {
		try {
			String autLegend = AUT_LEGEND_PREFIX.concat( " Firmado por: " ).concat( nombre ).concat( " | " ).concat ( puesto );
			
			if ( tieneDelegatorioAut( conn ) ) {
				autLegend = AUT_LEGEND_PREFIX + ". Firma " + getTipoSuplenciaAut() + " de " + getNombreEmpleadoSuplidoAut() + " con fundamento en el oficio: " + getFolioOficioAut() + " de fecha: " + getFechaOficioAut();
			} 
				
			return autLegend;
			
		} catch ( Exception e ) {
			throw new RuntimeException( e );
		}
	}

	@Override
	public String notificaPrefirmante( Connection conn ) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
