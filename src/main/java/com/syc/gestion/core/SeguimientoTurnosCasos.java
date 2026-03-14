package com.syc.gestion.core;

import java.io.Serializable;
import java.util.Vector;
import java.util.Base64;

public class SeguimientoTurnosCasos implements Serializable {

    private final static long serialVersionUID = 1;

    private Vector SeguimientoTurnosCaso = new Vector();

    public void SegumientoTurnosCaso() {
        SeguimientoTurnosCaso.clear();
    }

    public void add(SeguimientoTurnoCaso rc) {
        SeguimientoTurnosCaso.add(rc);
    }

    public SeguimientoTurnoCaso getRecuperaCaso(int idx) {
        return (SeguimientoTurnoCaso) SeguimientoTurnosCaso.elementAt(idx);
    }

    public int getLength() {
        return SeguimientoTurnosCaso.size();
    }
}
