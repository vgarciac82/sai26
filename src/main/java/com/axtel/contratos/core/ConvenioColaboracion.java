package com.axtel.contratos.core;


import java.util.List;


/**
 * Clase para convenio de colaboracion
 * 
 * @author vicente.garcia
 *
 */
public class ConvenioColaboracion {

	public static final String					ID_TIPO_CONVENIO			= "7";
	public static final String					ID_TIPO_CONVENIO_PAGO		= "8";

	public static final String					TIPO_CONTRATO				= "CN";
	public static final String					TIPO_CONVENIO_PAGO			= "CP";

	public static final String					HEADER						= "H";

	public static final String					DETAIL						= "D";
	public static final String					JUSTIFICACION_PAGO_BENEF	= "COMPENSACION A BRIGADISTAS";

	private ConvenioColaboracionEncabezado		encabezado;
	private List<ConvenioColaboracionDetalle>	detalle;

	public ConvenioColaboracion( ConvenioColaboracionEncabezado encabezado, List<ConvenioColaboracionDetalle> detalle ) {
		this.encabezado = encabezado;
		this.detalle = detalle;
	}

	/**
	 * @return the encabezado
	 */
	public ConvenioColaboracionEncabezado getEncabezado() {
		return encabezado;
	}

	/**
	 * @param encabezado
	 *            the encabezado to set
	 */
	public void setEncabezado( ConvenioColaboracionEncabezado encabezado ) {
		this.encabezado = encabezado;
	}

	/**
	 * @return the detalle
	 */
	public List<ConvenioColaboracionDetalle> getDetalle() {
		return detalle;
	}

	/**
	 * @param detalle
	 *            the detalle to set
	 */
	public void setDetalle( List<ConvenioColaboracionDetalle> detalle ) {
		this.detalle = detalle;
	}

	public void setFolioConvenio( int folio ) {
		if ( this.getEncabezado() != null )
			this.getEncabezado().setFolioConvenioColaboracion( folio );
		if ( this.getDetalle() != null )
			for ( ConvenioColaboracionDetalle detalle : getDetalle() ) {
				detalle.setFolioConvenioColaboracion( folio );
			}
	}

	public static StringBuilder toSummaryString( List<ConvenioColaboracion> convenios) {
		StringBuilder detalle  = new StringBuilder();

		for (ConvenioColaboracion convenio: convenios ) {
			detalle.append( "[Folios Cargados Exitosamente]" );
			detalle.append( "[Folio], [Folio Contrato], [R.F.C.], [Importe Total] " );
			detalle.append( convenio.getEncabezado().getFolioConvenioColaboracion()  +", ");
			detalle.append( convenio.getEncabezado().getIdContrato()  +", ");
			detalle.append( convenio.getEncabezado().getRfc()  +", ");
			detalle.append( convenio.getEncabezado().getmImporteTotal()  +", \n");
		}
		
		return detalle;
	}
	
	

}
