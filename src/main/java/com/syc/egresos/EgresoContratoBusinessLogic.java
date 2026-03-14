package com.syc.egresos;

import java.sql.Connection;
import com.syc.ejercido.pagado.EgresosBusinessLogic;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EgresoContratoBusinessLogic extends EgresosBusinessLogic {

    private static final Logger log = LoggerFactory.getLogger(EgresoContratoBusinessLogic.class);

    public EgresoContratoBusinessLogic() {
    }

    public EgresoContratoBusinessLogic(String jniName) {
        super(jniName);
    }

    public String getNumeroContratoEgreso(Connection conn, String tipoPago, int folioPago) throws Exception {
        log.info("Object: {}", "Buscando numero de contrato para el folio : " + folioPago + " en el pago " + tipoPago);
        return EgresoContratoManager.getNumeroContratoEgreso(conn, tipoPago, folioPago);
    }

    public String getNumeroContratoEgreso(String tipoPago, int folioPago) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            return getNumeroContratoEgreso(conn, tipoPago, folioPago);
        } finally {
            CloseObject.closeObject(conn);
        }
    }
}
