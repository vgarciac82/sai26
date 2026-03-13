package com.axtel.cfdi.ComplementoCombustible.custom;


import java.math.BigDecimal;
import java.util.List;

import com.axtel.cfdi.ComplementoCombustible.Bonificacion;
import com.axtel.cfdi.ComplementoCombustible.CargoECC;


/**
 * 
 * @author vicente.garcia
 *
 */
public class AdendaECC {

	/**
	 * @param bonificaciones
	 * @param cargos
	 */
	public AdendaECC( List<Bonificacion> bonificaciones, List<CargoECC> cargos ) {
		super();
		this.bonificaciones = bonificaciones;
		this.cargos = cargos;
	}

	private List<Bonificacion>	bonificaciones;
	private List<CargoECC>		cargos;

	/**
	 * @return the bonificaciones
	 */
	public List<Bonificacion> getBonificaciones() {
		return bonificaciones;
	}

	/**
	 * @param bonificaciones
	 *            the bonificaciones to set
	 */
	public void setBonificaciones( List<Bonificacion> bonificaciones ) {
		this.bonificaciones = bonificaciones;
	}

	/**
	 * @return the cargos
	 */
	public List<CargoECC> getCargos() {
		return cargos;
	}

	/**
	 * @param cargos
	 *            the cargos to set
	 */
	public void setCargos( List<CargoECC> cargos ) {
		this.cargos = cargos;
	}

	public BigDecimal getSubTotal() {
		BigDecimal importeBruto = new BigDecimal( 0.0 );

		for ( CargoECC cargo : getCargos() ) {
			importeBruto = importeBruto.add( cargo.getImporte() );
		}
		return importeBruto;
	}

	public BigDecimal getTotal() {
		BigDecimal importeTotal = new BigDecimal( 0.0 );

		for ( CargoECC cargo : getCargos() ) {
			importeTotal = importeTotal.add( cargo.getImporte() ).add(cargo.getTraslado());
		}
		
		return importeTotal;
	}

	public BigDecimal getTraslados() {
		BigDecimal importeTraslados = new BigDecimal( 0.0 );

		for ( CargoECC cargo : getCargos() ) {
			importeTraslados = importeTraslados.add( cargo.getTraslado() );
		}
		
		return importeTraslados;
	}

	public BigDecimal getDescuento() {
		BigDecimal importeDescuento = new BigDecimal( 0.0 );

		for ( CargoECC cargo : getCargos() ) {
			importeDescuento = importeDescuento.add( cargo.getDescuento() );
		}
		
		return importeDescuento;
	}

}
