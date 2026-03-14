package com.axtel.cfdi.ComplementoCombustible.vales;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.Base64;

@XmlRootElement(name = "cfdi:Addenda")
public class AddendaEfectivale {

    private Dispersion dispersion;

    @XmlElement(name = "Dispersion")
    public Dispersion getDispersion() {
        return dispersion;
    }

    public void setDispersion(Dispersion dispersion) {
        this.dispersion = dispersion;
    }
}
