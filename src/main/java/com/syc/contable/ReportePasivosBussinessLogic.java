package com.syc.contable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import com.syc.contable.core.ReportePasivosManager;
import com.syc.crud.dsmngr.DataSourceManager;
import java.util.Base64;

public class ReportePasivosBussinessLogic extends DataSourceManager {

    //private static Logger log = Logger.getLogger(AdecuacionBusinessLogic.class);
    public ReportePasivosBussinessLogic(String jniName) {
        super.init(jniName);
    }

    public ArrayList<String> buscaCompromisos(String szTemp) throws Exception {
        ArrayList<String> arrListaComp = null;
        Connection conn = null;
        try {
            conn = getConnection();
            arrListaComp = ReportePasivosManager.BuscaCompromisos(conn, szTemp);
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
