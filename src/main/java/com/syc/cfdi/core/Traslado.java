package com.syc.cfdi.core;


import java.math.BigDecimal;


public class Traslado {

	private BigDecimal	base;
	private BigDecimal	importe;
	private String		impuesto;
	private BigDecimal	tasaOCuota;
	private String		tipoFactor;

	public Traslado( Object traslado ) {

		if ( traslado instanceof mx.grupocorasa.sat.cfd._40.Comprobante.Conceptos.Concepto.Impuestos.Traslados.Traslado )
			setTraslado40( ( mx.grupocorasa.sat.cfd._40.Comprobante.Conceptos.Concepto.Impuestos.Traslados.Traslado ) traslado );
		else if ( traslado instanceof mx.grupocorasa.sat.cfd._33.Comprobante.Conceptos.Concepto.Impuestos.Traslados.Traslado )
			setTraslado33( ( mx.grupocorasa.sat.cfd._33.Comprobante.Conceptos.Concepto.Impuestos.Traslados.Traslado ) traslado );
		else
			throw new RuntimeException( "No se puede procesar objetos del tipo " + traslado.getClass().getName() );

	}

	private void setTraslado40( mx.grupocorasa.sat.cfd._40.Comprobante.Conceptos.Concepto.Impuestos.Traslados.Traslado traslado ) {
		this.base = traslado.getBase();
		this.importe = traslado.getImporte();
		this.impuesto = traslado.getImpuesto().value();
		this.tasaOCuota = traslado.getTasaOCuota();
		this.tipoFactor = traslado.getTipoFactor().value();

	}

	private void setTraslado33( mx.grupocorasa.sat.cfd._33.Comprobante.Conceptos.Concepto.Impuestos.Traslados.Traslado traslado ) {
		this.base = traslado.getBase();
		this.importe = traslado.getImporte();
		this.impuesto = traslado.getImpuesto().value();
		this.tasaOCuota = traslado.getTasaOCuota();
		this.tipoFactor = traslado.getTipoFactor().value();

	}

	/**
	 * @return the base
	 */
	public BigDecimal getBase() {
		return base;
	}

	/**
	 * @param base
	 *            the base to set
	 */
	public void setBase( BigDecimal base ) {
		this.base = base;
	}

	/**
	 * @return the importe
	 */
	public BigDecimal getImporte() {
		return importe;
	}

	/**
	 * @param importe
	 *            the importe to set
	 */
	public void setImporte( BigDecimal importe ) {
		this.importe = importe;
	}

	/**
	 * @return the impuesto
	 */
	public String getImpuesto() {
		return impuesto;
	}

	/**
	 * @param impuesto
	 *            the impuesto to set
	 */
	public void setImpuesto( String impuesto ) {
		this.impuesto = impuesto;
	}

	/**
	 * @return the tasaOCuota
	 */
	public BigDecimal getTasaOCuota() {
		return tasaOCuota;
	}

	/**
	 * @param tasaOCuota
	 *            the tasaOCuota to set
	 */
	public void setTasaOCuota( BigDecimal tasaOCuota ) {
		this.tasaOCuota = tasaOCuota;
	}

	/**
	 * @return the tipoFactor
	 */
	public String getTipoFactor() {
		return tipoFactor;
	}

	/**
	 * @param tipoFactor
	 *            the tipoFactor to set
	 */
	public void setTipoFactor( String tipoFactor ) {
		this.tipoFactor = tipoFactor;
	}

	@Override
	public String toString() {
		return "Traslado [base=" + base + ", importe=" + importe + ", impuesto=" + impuesto + ", tasaOCuota=" + tasaOCuota + ", tipoFactor=" + tipoFactor + "]";
	}
}
