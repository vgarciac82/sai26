package com.syc.cfdi.v3332.Comprobante;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.syc.cfdi.core.Traslado;


public class Traslados {

	private List<Traslado> traslados;

	public Traslados( Object traslados ) {

		this.setTraslados( new ArrayList<>() );
		if ( traslados != null ) {

			Iterator<?> iterator = null;

			if ( traslados instanceof mx.grupocorasa.sat.cfd._40.Comprobante.Conceptos.Concepto.Impuestos.Traslados )
				iterator = ( ( mx.grupocorasa.sat.cfd._40.Comprobante.Conceptos.Concepto.Impuestos.Traslados ) traslados ).getTraslado().iterator();
			if ( traslados instanceof mx.grupocorasa.sat.cfd._33.Comprobante.Conceptos.Concepto.Impuestos.Traslados )
				iterator = ( ( mx.grupocorasa.sat.cfd._33.Comprobante.Conceptos.Concepto.Impuestos.Traslados ) traslados ).getTraslado().iterator();

			while ( iterator.hasNext() ) {
				Object trasladoObj = iterator.next();
				if ( trasladoObj instanceof mx.grupocorasa.sat.cfd._40.Comprobante.Conceptos.Concepto.Impuestos.Traslados.Traslado )
					this.getTraslados().add( new Traslado( ( mx.grupocorasa.sat.cfd._40.Comprobante.Conceptos.Concepto.Impuestos.Traslados.Traslado ) trasladoObj ) );
				else if ( trasladoObj instanceof mx.grupocorasa.sat.cfd._33.Comprobante.Conceptos.Concepto.Impuestos.Traslados.Traslado )
					this.getTraslados().add( new Traslado( ( mx.grupocorasa.sat.cfd._33.Comprobante.Conceptos.Concepto.Impuestos.Traslados.Traslado ) trasladoObj ) );
				else
					throw new RuntimeException( "No se pueden procesar objetos: " + trasladoObj.getClass().getName() );

			}
		}
	}

	public List<Traslado> getTraslados() {
		return traslados;
	}

	public void setTraslados( List<Traslado> traslados ) {
		this.traslados = traslados;
	}

}
