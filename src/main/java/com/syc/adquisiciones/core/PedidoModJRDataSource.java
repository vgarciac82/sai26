package com.syc.adquisiciones.core;

import java.util.ArrayList;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import java.util.Base64;

public class PedidoModJRDataSource implements JRDataSource {

    private Integer cont = -1;

    private ArrayList<PedidoModHoja> detalles = new ArrayList<PedidoModHoja>();

    public ArrayList<PedidoModHoja> getDetalles() {
        return detalles;
    }

    public void setDetalles(ArrayList<PedidoModHoja> detalles) {
        this.detalles = detalles;
    }

    public Object getFieldValue(JRField arg0) throws JRException {
        if (arg0.getName().equals("original")) {
            return this.detalles.get(this.cont).getOriginal();
        } else if (arg0.getName().equals("modificado")) {
            return this.detalles.get(this.cont).getModificado();
        } else if (arg0.getName().equals("totales")) {
            return this.detalles.get(this.cont).getTotales();
        }
        return null;
    }

    public boolean next() throws JRException {
        this.cont++;
        if (this.cont < this.detalles.size()) {
            return true;
        }
        return false;
    }
}
