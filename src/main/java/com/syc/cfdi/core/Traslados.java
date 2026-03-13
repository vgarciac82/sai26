package com.syc.cfdi.core;


import java.util.ArrayList;
import java.util.List;


public class Traslados {

	private boolean														ver40	= false;
	private boolean														ver33	= true;
	private mx.grupocorasa.sat.cfd._33.Comprobante.Impuestos.Traslados	traslados33;
	private mx.grupocorasa.sat.cfd._40.Comprobante.Impuestos.Traslados	traslados40;

	/**
	 * @return the ver40
	 */
	public boolean isVer40() {
		return ver40;
	}

	/**
	 * @param ver40
	 *            the ver40 to set
	 */
	public void setVer40( boolean ver40 ) {
		this.ver40 = ver40;
	}

	/**
	 * @return the ver33
	 */
	public boolean isVer33() {
		return ver33;
	}

	/**
	 * @param ver33
	 *            the ver33 to set
	 */
	public void setVer33( boolean ver33 ) {
		this.ver33 = ver33;
	}

	public List<?> getTraslado() {
		if ( isVer40() )
			return traslados40 == null ? new ArrayList<>() : traslados40.getTraslado();
		else
			return traslados33 == null ? new ArrayList<>() : traslados33.getTraslado();
	}

	/**
	 * @return the traslados33
	 */
	public mx.grupocorasa.sat.cfd._33.Comprobante.Impuestos.Traslados getTraslados33() {
		return traslados33;
	}

	/**
	 * @param traslados33
	 *            the traslados33 to set
	 */
	public void setTraslados33( mx.grupocorasa.sat.cfd._33.Comprobante.Impuestos.Traslados traslados33 ) {
		this.traslados33 = traslados33;
	}

	/**
	 * @return the traslados40
	 */
	public mx.grupocorasa.sat.cfd._40.Comprobante.Impuestos.Traslados getTraslados40() {
		return traslados40;
	}

	/**
	 * @param traslados40
	 *            the traslados40 to set
	 */
	public void setTraslados40( mx.grupocorasa.sat.cfd._40.Comprobante.Impuestos.Traslados traslados40 ) {
		this.traslados40 = traslados40;
	}

}
