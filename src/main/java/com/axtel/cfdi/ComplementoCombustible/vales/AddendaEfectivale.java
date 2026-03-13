package com.axtel.cfdi.ComplementoCombustible.vales;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement( name = "cfdi:Addenda" )
public class AddendaEfectivale {

	private Dispersion dispersion;

	@XmlElement( name = "Dispersion" )
	public Dispersion getDispersion() {
		return dispersion;
	}

	public void setDispersion( Dispersion dispersion ) {
		this.dispersion = dispersion;
	}

}
