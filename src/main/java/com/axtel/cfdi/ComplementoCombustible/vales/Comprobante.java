package com.axtel.cfdi.ComplementoCombustible.vales;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement( name = "Comprobante", namespace = "http://www.sat.gob.mx/cfd/4" )
public class Comprobante {

	private AddendaEfectivale addenda;

	@XmlElement( name = "Addenda", namespace = "http://www.sat.gob.mx/cfd/4" )
	public AddendaEfectivale getAddenda() {
		return addenda;
	}

	public void setAddenda( AddendaEfectivale addenda ) {
		this.addenda = addenda;
	}

}
