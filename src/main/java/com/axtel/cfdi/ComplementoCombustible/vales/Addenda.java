package com.axtel.cfdi.ComplementoCombustible.vales;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.Base64;

@XmlRootElement(name = "cfdi:Addenda")
public class Addenda {

    private Emision emision;

    @XmlElement(name = "emision", namespace = "http://www.edenred.com.mx/cfdi/3/")
    public Emision getEmision() {
        return emision;
    }

    public void setEmision(Emision emision) {
        this.emision = emision;
    }
}
