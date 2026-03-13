package com.axtel.cfdi.ComplementoCombustible.vales;

import java.util.List;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "conceptos", namespace = "http://www.edenred.com.mx/cfdi/3/")
public class Conceptos {

    private List<Concepto> conceptoList;

    @XmlElement(name = "concepto")
    public List<Concepto> getConceptoList() {
        return conceptoList;
    }

    public void setConceptoList(List<Concepto> conceptoList) {
        this.conceptoList = conceptoList;
    }
}
