package com.syc.contable.anteproyecto;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Map;
import com.syc.gestion.util.Util;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RelacionProgramaClasifEconomicaManager {

    private static Logger log = LoggerFactory.getLogger(RelacionProgramaClasifEconomicaManager.class);

    public static int insertaRenglonProgramaClasifEconomica(Connection conn, Map<String, String> infoRenglon) throws Exception {
        Statement stmnt = null;
        int r = 0;
        try {
            log.trace("Iniciando insercion de renglon");
            stmnt = conn.createStatement();
            r = stmnt.executeUpdate(Util.genInsertFromMap("tCatalogoProgramaClasifEconomica", infoRenglon));
            log.trace("Object: {}", "Se inserto " + r + "registros");
            return r;
        } finally {
            CloseObject.closeObject(stmnt, false);
        }
    }
}
