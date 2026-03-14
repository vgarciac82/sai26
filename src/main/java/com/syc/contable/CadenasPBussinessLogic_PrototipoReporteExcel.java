package com.syc.contable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import com.syc.contable.core.CadenasPManager_PrototipoReporteExcel;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.gestion.core.GestionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class CadenasPBussinessLogic_PrototipoReporteExcel extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(AdecuacionBusinessLogic.class);

    public CadenasPBussinessLogic_PrototipoReporteExcel(String jniName) {
        super.init(jniName);
    }

    public ArrayList<String> buscaCompromisos(String sProveedor, String sEstatus, String sCentroCon, String sDigitoIde, String sfEmision, String sfEmisionf, String sUsuario) throws Exception {
        ArrayList<String> arrListaComp = null;
        Connection conn = null;
        try {
            conn = getConnection();
            arrListaComp = CadenasPManager_PrototipoReporteExcel.BuscaCompromisos(conn, sProveedor, sEstatus, sCentroCon, sDigitoIde, sfEmision, sfEmisionf, sUsuario);
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
