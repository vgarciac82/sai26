package com.axtel.cfdi.ComplementoCombustible.vales;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Comprobante", namespace = "http://www.sat.gob.mx/cfd/4")
public class Comprobante {

    private AddendaEfectivale addenda;

    @XmlElement(name = "Addenda", namespace = "http://www.sat.gob.mx/cfd/4")
    public AddendaEfectivale getAddenda() {
        return addenda;
    }

    public void setAddenda(AddendaEfectivale addenda) {
        this.addenda = addenda;
    }
}
