package com.syc.gestion.core;

import java.io.Serializable;
import com.syc.gestion.BitacoraCasoBusinessLogic;
import com.syc.gestion.BitacoraOperacionBusinessLogic;
import java.util.Base64;

public class BitacoraTotal implements Serializable {

    private final static long serialVersionUID = 1;

    private BitacoraCaso caso = null;

    private BitacoraOperacion[] operaciones = null;

    public BitacoraTotal() {
        super();
    }

    public void getBitacora(int idCaso) {
        //esto deberia obtenerlo del contexto!!!
        BitacoraCasoBusinessLogic bcbl = new BitacoraCasoBusinessLogic("jdbc/gestion");
        BitacoraOperacionBusinessLogic bobl = new BitacoraOperacionBusinessLogic("jdbc/gestion");
        //CasoBusinessLogic cbl = new CasoBusinessLogic("jdbc/gestion");
        try {
            this.caso = bcbl.getBitacoraCaso(idCaso);
        } catch (GestionException ge) {
            ge.printStackTrace();
        }
        //Caso c = cbl.getCaso(idCaso);
        //BitacoraCaso bc 		= getBitacora(c);
        try {
            this.operaciones = bobl.getBitacoraOperaciones(idCaso);
        } catch (GestionException ge) {
            ge.printStackTrace();
        }
    }

    public BitacoraCaso getCaso() {
        return caso;
    }

    public BitacoraOperacion[] getOperaciones() {
        return operaciones;
    }
}
