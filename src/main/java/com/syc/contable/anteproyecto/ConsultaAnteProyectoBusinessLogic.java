package com.syc.contable.anteproyecto;

import java.sql.Connection;
import org.apache.poi.ss.usermodel.Sheet;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsultaAnteProyectoBusinessLogic extends DataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(ConsultaAnteProyectoBusinessLogic.class);

    private String uLoginCarga = "";

    /**
     * Construye una nueva instancia del objeto.
     */
    public ConsultaAnteProyectoBusinessLogic(String uLogin) {
        super.init();
        this.uLoginCarga = uLogin;
    }

    public Sheet consultaReporteExcel(Sheet hoja, String cUE, String cUN, String cEP, String mMontoCalculado, String mMontoOptimo, String mMontoIrreductible) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            hoja = ConsultaAnteProyectoManager.consultaExportaReporteExcel(conn, hoja, cUE, cUN, cEP, mMontoCalculado, mMontoOptimo, mMontoIrreductible);
        } finally {
            CloseObject.closeObject(conn, false);
        }
        return hoja;
    }
}
