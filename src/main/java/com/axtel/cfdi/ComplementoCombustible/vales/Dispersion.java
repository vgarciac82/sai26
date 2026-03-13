package com.axtel.cfdi.ComplementoCombustible.vales;


import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;

import javax.xml.bind.annotation.XmlAttribute;


public class Dispersion {

	private String		claveProdServ;

	private int			cantidad;

	private String		claveUnidad;

	private String		unidad;

	private String		descripcion;

	private BigDecimal	valorUnitario;

	private BigDecimal	importe;

	@XmlAttribute( name = "ClaveProdServ" )
	public String getClaveProdServ() {
		return claveProdServ;
	}

	public void setClaveProdServ( String claveProdServ ) {
		this.claveProdServ = claveProdServ;
	}

	@XmlAttribute( name = "Cantidad" )
	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad( int cantidad ) {
		this.cantidad = cantidad;
	}

	@XmlAttribute( name = "ClaveUnidad" )
	public String getClaveUnidad() {
		return claveUnidad;
	}

	public void setClaveUnidad( String claveUnidad ) {
		this.claveUnidad = claveUnidad;
	}

	@XmlAttribute( name = "Unidad" )
	public String getUnidad() {
		return unidad;
	}

	public void setUnidad( String unidad ) {
		this.unidad = unidad;
	}

	@XmlAttribute( name = "Descripcion" )
	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion( String descripcion ) {
		this.descripcion = descripcion;
	}

	@XmlAttribute( name = "ValorUnitario" )
	public BigDecimal getValorUnitario() {
		return valorUnitario;
	}

	public void setValorUnitario( BigDecimal valorUnitario ) {
		this.valorUnitario = valorUnitario;
	}

	@XmlAttribute( name = "Importe" )
	public BigDecimal getImporte() {
		return importe;
	}

	public void setImporte( BigDecimal importe ) {
		this.importe = importe;
	}

	public static BigDecimal parseBigDecimal( String value ) {
		if ( value == null || value.isEmpty() ) {
			return null;
		}

		DecimalFormat decimalFormat = new DecimalFormat( "###,###,###.00" );
		try {
			return new BigDecimal( decimalFormat.parse( value ).doubleValue() );
		} catch ( ParseException e ) {
			e.printStackTrace(); // Manejo de errores según tu lógica de
									// aplicación
			return null;
		}
	}
}
