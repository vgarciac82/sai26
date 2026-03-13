package com.syc.cfdi.v3332.Comprobante;


import java.math.BigDecimal;


public class Concepto {

	private String		claveProdServ;
	private BigDecimal	cantidad;
	private String		claveUnidad;
	private String		descripcion;
	private BigDecimal	valorUnitario;
	private BigDecimal	importe;
	private Traslados	traslados;
	private long		idConcepto;

	public Concepto( Object concepto ) {
		if ( concepto instanceof mx.grupocorasa.sat.cfd._40.Comprobante.Conceptos.Concepto ) {
			claveProdServ = ( ( mx.grupocorasa.sat.cfd._40.Comprobante.Conceptos.Concepto ) concepto ).getClaveProdServ();
			cantidad = ( ( mx.grupocorasa.sat.cfd._40.Comprobante.Conceptos.Concepto ) concepto ).getCantidad();
			if ( (( mx.grupocorasa.sat.cfd._40.Comprobante.Conceptos.Concepto ) concepto ).getClaveUnidad() != null)
				claveUnidad = ( ( mx.grupocorasa.sat.cfd._40.Comprobante.Conceptos.Concepto ) concepto ).getClaveUnidad().value();
			else 
				claveUnidad = "";
			descripcion = ( ( mx.grupocorasa.sat.cfd._40.Comprobante.Conceptos.Concepto ) concepto ).getDescripcion();
			valorUnitario = ( ( mx.grupocorasa.sat.cfd._40.Comprobante.Conceptos.Concepto ) concepto ).getValorUnitario();
			importe = ( ( mx.grupocorasa.sat.cfd._40.Comprobante.Conceptos.Concepto ) concepto ).getImporte();
			mx.grupocorasa.sat.cfd._40.Comprobante.Conceptos.Concepto.Impuestos impuestos = ( ( mx.grupocorasa.sat.cfd._40.Comprobante.Conceptos.Concepto ) concepto ).getImpuestos();
			if ( impuestos != null )
				setTraslados( new Traslados( ( ( mx.grupocorasa.sat.cfd._40.Comprobante.Conceptos.Concepto ) concepto ).getImpuestos().getTraslados() ) );

		} else if ( concepto instanceof mx.grupocorasa.sat.cfd._33.Comprobante.Conceptos.Concepto ) {
			claveProdServ = ( ( mx.grupocorasa.sat.cfd._33.Comprobante.Conceptos.Concepto ) concepto ).getClaveProdServ();
			cantidad = ( ( mx.grupocorasa.sat.cfd._33.Comprobante.Conceptos.Concepto ) concepto ).getCantidad();
			if(  ( ( mx.grupocorasa.sat.cfd._33.Comprobante.Conceptos.Concepto ) concepto ).getClaveUnidad() != null)
				claveUnidad = ( ( mx.grupocorasa.sat.cfd._33.Comprobante.Conceptos.Concepto ) concepto ).getClaveUnidad().value();
			else
				claveUnidad = "";
			descripcion = ( ( mx.grupocorasa.sat.cfd._33.Comprobante.Conceptos.Concepto ) concepto ).getDescripcion();
			valorUnitario = ( ( mx.grupocorasa.sat.cfd._33.Comprobante.Conceptos.Concepto ) concepto ).getValorUnitario();
			importe = ( ( mx.grupocorasa.sat.cfd._33.Comprobante.Conceptos.Concepto ) concepto ).getImporte();
			mx.grupocorasa.sat.cfd._33.Comprobante.Conceptos.Concepto.Impuestos impuestos = ( ( mx.grupocorasa.sat.cfd._33.Comprobante.Conceptos.Concepto ) concepto ).getImpuestos();
			if ( impuestos != null )
				setTraslados( new Traslados( impuestos.getTraslados() ) );
		} else
			throw new RuntimeException( "No es posible procesar el objeto: " + concepto.getClass() );
	}

	/**
	 * @return the claveProdServ
	 */
	public String getClaveProdServ() {
		return claveProdServ;
	}

	/**
	 * @return the cantidad
	 */
	public BigDecimal getCantidad() {
		return cantidad;
	}

	/**
	 * @return the claveUnidad
	 */
	public String getClaveUnidad() {
		return claveUnidad;
	}

	/**
	 * @return the descripcion
	 */
	public String getDescripcion() {
		return descripcion;
	}

	/**
	 * @return the valorUnitario
	 */
	public BigDecimal getValorUnitario() {
		return valorUnitario;
	}

	/**
	 * @return the importe
	 */
	public BigDecimal getImporte() {
		return importe;
	}

	public Traslados getTraslados() {
		return traslados;
	}

	public void setTraslados( Traslados traslados ) {
		this.traslados = traslados;
	}

	public void setIdConcepto( long idConcepto ) {
		this.idConcepto = idConcepto;
	}

	/**
	 * @return the idConcepto
	 */
	public long getIdConcepto() {
		return idConcepto;
	}

	/**
	 * @param claveProdServ
	 *            the claveProdServ to set
	 */
	public void setClaveProdServ( String claveProdServ ) {
		this.claveProdServ = claveProdServ;
	}

	/**
	 * @param cantidad
	 *            the cantidad to set
	 */
	public void setCantidad( BigDecimal cantidad ) {
		this.cantidad = cantidad;
	}

	/**
	 * @param claveUnidad
	 *            the claveUnidad to set
	 */
	public void setClaveUnidad( String claveUnidad ) {
		this.claveUnidad = claveUnidad;
	}

	/**
	 * @param descripcion
	 *            the descripcion to set
	 */
	public void setDescripcion( String descripcion ) {
		this.descripcion = descripcion;
	}

	/**
	 * @param valorUnitario
	 *            the valorUnitario to set
	 */
	public void setValorUnitario( BigDecimal valorUnitario ) {
		this.valorUnitario = valorUnitario;
	}

	/**
	 * @param importe
	 *            the importe to set
	 */
	public void setImporte( BigDecimal importe ) {
		this.importe = importe;
	}

	@Override
	public String toString() {
		return "Concepto [claveProdServ=" + claveProdServ + ", cantidad=" + cantidad + ", claveUnidad=" + claveUnidad + ", descripcion=" + descripcion + ", valorUnitario=" + valorUnitario + ", importe=" + importe + ", traslados=" + traslados + ", idConcepto=" + idConcepto + "]";
	}

}
