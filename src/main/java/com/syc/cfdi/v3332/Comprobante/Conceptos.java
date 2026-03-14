package com.syc.cfdi.v3332.Comprobante;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import com.syc.cfdi.core.Traslado;
import java.util.Base64;

public class Conceptos {

    private List<Concepto> conceptos;

    mx.grupocorasa.sat.cfd._40.Comprobante.Conceptos conceptos40;

    mx.grupocorasa.sat.cfd._33.Comprobante.Conceptos conceptos33;

    public Conceptos(Object conceptos) {
        if (conceptos instanceof mx.grupocorasa.sat.cfd._40.Comprobante.Conceptos)
            this.conceptos40 = (mx.grupocorasa.sat.cfd._40.Comprobante.Conceptos) conceptos;
        else if (conceptos instanceof mx.grupocorasa.sat.cfd._33.Comprobante.Conceptos)
            this.conceptos33 = (mx.grupocorasa.sat.cfd._33.Comprobante.Conceptos) conceptos;
        setConceptos();
    }

    private void setConceptos() {
        conceptos = new LinkedList<>();
        Iterator<?> conceptIterator = null;
        if (this.conceptos40 != null)
            conceptIterator = this.conceptos40.getConcepto().iterator();
        else if (this.conceptos33 != null)
            conceptIterator = this.conceptos33.getConcepto().iterator();
        else
            throw new RuntimeException("No se ha establecido el objeto conceptos.");
        while (conceptIterator.hasNext()) {
            conceptos.add(new Concepto(conceptIterator.next()));
        }
    }

    public List<Concepto> getConceptos() {
        return this.conceptos;
    }

    public BigDecimal getTotalTraslados() {
        BigDecimal totalTraslados = new BigDecimal(0.00);
        for (Concepto concepto : getConceptos()) for (Traslado t : concepto.getTraslados().getTraslados()) totalTraslados = totalTraslados.add(t.getImporte());
        return totalTraslados;
    }
}
