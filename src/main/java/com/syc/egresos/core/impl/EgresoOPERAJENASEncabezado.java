package com.syc.egresos.core.impl;


import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.axtel.egresos.entities.EgresoExcedeUMA;
import com.syc.contable.core.OperacionAjenaManager;
import com.syc.egresos.core.Amortizacion;
import com.syc.egresos.core.EgresoEncabezado;
import com.syc.sai.contabilidad.utils.db.CloseObject;


public class EgresoOPERAJENASEncabezado extends EgresoEncabezado {

	private String		beneficiario;
	private Date		fechaDesde;
	private Date		fechaHasta;
	private int			folioSICOP;
	private BigDecimal	importes;

	public String getBeneficiario() {
		return beneficiario;
	}

	public void setBeneficiario( String beneficiario ) {
		this.beneficiario = beneficiario;
	}

	public Date getFechaDesde() {
		return fechaDesde;
	}

	public void setFechaDesde( Date fechaDesde ) {
		this.fechaDesde = fechaDesde;
	}

	public Date getFechaHasta() {
		return fechaHasta;
	}

	public void setFechaHasta( Date fechaHasta ) {
		this.fechaHasta = fechaHasta;
	}

	public int getFolioSICOP() {
		return folioSICOP;
	}

	public void setFolioSICOP( int folioSICOP ) {
		this.folioSICOP = folioSICOP;
	}

	public BigDecimal getImportes() {
		return importes;
	}

	public void setImportes( BigDecimal importes ) {
		this.importes = importes;
	}

	@Override
	public int actualizaMontosRetencion( Connection conn ) throws Exception {
		throw new Exception( "actualizaMontosRetencion  no Implementado " );
	}

	@Override
	public int actualizaRetencion( Connection conn, int idTipoRetencion, BigDecimal valorRetencion ) throws Exception {
		throw new Exception( "actualizaRetencion  no Implementado " );
	}

	@Override
	public int avanzaEstatus( Connection conn ) throws Exception {
		throw new Exception( "avanzaEstatus  no Implementado " );
	}

	@Override
	public EgresoEncabezado cargaEncabezado( HttpServletRequest req ) throws Exception {
		throw new Exception( "cargaEncabezado  no Implementado " );
	}

	@Override
	public EgresoEncabezado cargaEncabezado( int folioEgreso ) throws Exception {
		Connection conn = null;
		try {
			conn = getConnection();
			return OperacionAjenaManager.cargEncabezado( conn, folioEgreso );
		} finally {
			CloseObject.closeObject( conn );
		}
	}

	public EgresoEncabezado cargaEncabezado( Connection conn, int folioEgreso ) throws Exception {
			return OperacionAjenaManager.cargEncabezado( conn, folioEgreso );
	}
	
	@Override
	public int delete( Connection conn ) throws Exception {
		throw new Exception( "delete  no Implementado " );
	}

	@Override
	public int eliminaRetencion( Connection conn, int idTipoRetencion ) throws Exception {
		throw new Exception( "eliminaRetencion  no Implementado " );
	}

	@Override
	public int generaRetenciones( Connection conn ) throws Exception {
		throw new Exception( "generaRetenciones  no Implementado " );
	}

	@Override
	public String getNombreAnexo() {
		return null;
	}

	@Override
	public String getNombreSolicitudPago() {
		return null;
	}

	@Override
	public String getPrefijoCR( Connection conn ) throws Exception {
		return null;
	}

	@Override
	public void rechazaPago( Connection conn, String motivoRechazo ) throws Exception {
		throw new Exception( "rechazaPago  no Implementado " );

	}

	@Override
	public Map<String, String> resumenConcepto( Connection conn ) throws Exception {
		throw new Exception( "resumenConcepto  no Implementado " );
	}

	@Override
	public Map<String, String> resumenPago( Connection conn ) throws Exception {
		throw new Exception( "resumenPago  no Implementado " );
	}

	@Override
	public List<Map<String, String>> resumenRetenciones( Connection conn ) throws Exception {
		throw new Exception( " resumenRetenciones no Implementado " );
	}

	@Override
	public int save( Connection conn ) throws Exception {
		throw new Exception( "save  no Implementado " );
	}

	@Override
	public Amortizacion getAmortizacion( Connection conn ) throws Exception {
		throw new Exception( "getAmortizacion  no Implementado " );
	}

	@Override
	public boolean retencionEliminable( Connection conn, int idRetencion ) throws Exception {
		throw new RuntimeException( "retencionEliminable No Implementado." );
	}
	
	@Override
	public List<EgresoExcedeUMA> validaTopeUMASUnidad( Connection conn, String rfc2 ) {
		return new ArrayList<EgresoExcedeUMA>();
	}
}
