package com.syc.contable;

import java.sql.Connection;
import java.sql.SQLException;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.core.GestionException;
import com.syc.obrapublica.core.ObraPublicaContract;
import com.syc.obrapublica.core.ObraPublicaContractManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComsocAutorizacionBussinesLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(ComsocAutorizacionBussinesLogic.class);

    private String jniName = null;

    private static final int ID_TIPO_CASO_OBRA = 10;

    private static final int ID_TIPO_CASO_COMP = 7;

    public ComsocAutorizacionBussinesLogic() {
    }

    public ComsocAutorizacionBussinesLogic(String jniName) {
        super.init(jniName);
        this.jniName = jniName;
    }

    public byte MoveToAuthorize(String folioSAI) throws GestionException {
        Connection conn = null;
        byte retVal;
        try {
            conn = getConnection();
            ObraPublicaContract opc = new ObraPublicaContract();
            opc.setFolioSAI(folioSAI);
            retVal = ObraPublicaContractManager.SelectIdPreCompromisoHeaderAndEventos(conn, opc);
            if (retVal == 0) {
                retVal = ObraPublicaContractManager.ApplyPreCompr(conn, opc);
                if (retVal == 0) {
                    retVal = ObraPublicaContractManager.SetStatusPreCompromiso(conn, opc.getIdPreCompromisoHeader(), (byte) 3);
                    if (retVal == 0)
                        conn.commit();
                }
            } else {
                if (retVal == -1) {
                    opc.setMessage("Error enc base de datos al aplicar el motor contable");
                } else if (retVal == -2)
                    opc.setMessage("El documento compromiso con el folio SAI no fue encontrado en la base de datos");
            }
        } catch (SQLException exc) {
            log.error("Actualizando Mensaje", exc);
            throw new GestionException(exc);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException exc) {
                log.warn("Cerrando conexion a base de datos", exc);
            }
            conn = null;
        }
        return retVal;
    }

    public byte InsertaComsoc(int intFolioPago, String cTipoPago, String cFolioSai, String cEjercicio, String cCentroContable) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            byte bInserto = ComsocAutorizacionManager.InsertaComsoc(conn, intFolioPago, cTipoPago, cFolioSai, cEjercicio, cCentroContable);
            return bInserto;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            if (conn != null)
                try {
                    conn.close();
                } catch (Exception e2) {
                    log.warn("Problemas cerrando conexion a DB " + e2);
                }
        }
    }
}
