package com.syc.contable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import com.syc.contable.core.ExtraeOAManager;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.core.GestionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtraeOABussinessLogic extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(AdecuacionBusinessLogic.class);

    public ExtraeOABussinessLogic(String jniName) {
        super.init(jniName);
    }

    public ArrayList<String> buscaCompromisos(String szTemp, String szTabla) throws Exception {
        ArrayList<String> arrListaComp = null;
        Connection conn = null;
        try {
            conn = getConnection();
            arrListaComp = ExtraeOAManager.BuscaCompromisos(conn, szTemp, szTabla);
        } catch (SQLException e) {
            if (conn != null) {
                e.printStackTrace();
                conn.rollback();
                //throw  new GestionException(e.getMessage());
            }
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
        return arrListaComp;
    }
}
