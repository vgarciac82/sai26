package com.axtel.egresos.core;


import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;


public class RGMasiva extends MasiveOperation {

	private int		folioApartado		= 0;
	private int		tipoCarga			= 0;
	private double	importeISRLaudos	= 0.0d;

	public int getFolioApartado() {
		return folioApartado;
	}

	public void setFolioApartado( int folioApartado ) {
		this.folioApartado = folioApartado;
	}

	public int getTipoCarga() {
		return tipoCarga;
	}

	public void setTipoCarga( int tipoCarga ) {
		this.tipoCarga = tipoCarga;
	}

	public double getImporteISRLaudos() {
		return importeISRLaudos;
	}

	public void setImporteISRLaudos( double importeISRLaudos ) {
		this.importeISRLaudos = importeISRLaudos;
	}

	public RGMasiva( ) {
		setApplication( "RELACIONGASTOS" );
	}

	public RGMasiva( ResultSet rs ) throws SQLException {
		this();
		setFolioTramiteTemporal( rs.getInt( "nFolioRELACIONGASTOS" ) );
		setCentroContable( rs.getString( "cCentroContable" ) );
		setEjercicioFiscal( rs.getString( "aEjercicioFiscal" ) );
		setRadicado( rs.getString( "cRadicado" ) );
		setTipoCarga( rs.getInt( "nTipoCarga" ) );

		if ( StringUtils.isBlank( getRadicado() ) )
			setRadicado( "N" );

	}

}
