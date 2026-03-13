package com.axtel.egresos.compromiso;

import java.util.List;

public class Compromiso {

	List<CompromisoDetalle>	detalle;
	CompromisoEncabezado	encabezado;

	/**
	 * @return the detalle
	 */
	public List<CompromisoDetalle> getDetalle() {
		return detalle;
	}

	/**
	 * @return the encabezado
	 */
	public CompromisoEncabezado getEncabezado() {
		return encabezado;
	}

	/**
	 * @param detalle
	 *            the detalle to set
	 */
	public void setDetalle( List<CompromisoDetalle> detalle ) {
		this.detalle = detalle;
	}

	/**
	 * @param encabezado
	 *            the encabezado to set
	 */
	public void setEncabezado( CompromisoEncabezado encabezado ) {
		this.encabezado = encabezado;
	}

	public void setFolioCompromiso( int nFolioCompromiso ) {
		getEncabezado().setnFolioCompromiso( nFolioCompromiso );
		for ( int i = 0; i < getDetalle().size(); i++ )
			getDetalle().get( i ).setnFolioCompromiso( nFolioCompromiso );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Compromiso [encabezado=" + encabezado + ", detalle=" + detalle + "]";
	}

}
