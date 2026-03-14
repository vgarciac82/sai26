package com.syc.ejercido.pagado;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import com.syc.contable.AdecuacionBusinessLogic;
import com.syc.crud.dsmngr.DataSourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class LayoutGeneralBussinessLogic extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(AdecuacionBusinessLogic.class);

    public LayoutGeneralBussinessLogic(String jniName) {
        super.init(jniName);
    }

    public ArrayList<String> consultaSaldosCompromisoCapituloMil(String compromiso, String estatus) throws Exception {
        ArrayList<String> arrDoc = null;
        Connection conn = null;
        try {
            conn = getConnection();
            arrDoc = LayoutGeneralManager.SaldosCompromisoCapituloMil(conn, compromiso, estatus);
        } catch (SQLException e) {
            if (conn != null) {
                e.printStackTrace();
                conn.rollback();
            }
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
        return arrDoc;
    }

    public ArrayList<String> consultaSaldosCapituloMil(String caNoContrarrecibo, String conceptoFiltro, String movimientoFiltro, String estatusTxt) throws Exception {
        ArrayList<String> arrDoc = null;
        Connection conn = null;
        try {
            conn = getConnection();
            arrDoc = LayoutGeneralManager.SaldosCapituloMil(conn, caNoContrarrecibo, conceptoFiltro, movimientoFiltro, estatusTxt);
        } catch (SQLException e) {
            if (conn != null) {
                e.printStackTrace();
                conn.rollback();
            }
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
        return arrDoc;
    }

    public ArrayList<String> ConsultaIngresosEgreso(String sWhereCla) throws Exception {
        ArrayList<String> arrDoc = null;
        Connection conn = null;
        try {
            conn = getConnection();
            arrDoc = LayoutGeneralManager.ConsultaIngresosEgreso(conn, sWhereCla);
        } catch (SQLException e) {
            if (conn != null) {
                e.printStackTrace();
                conn.rollback();
            }
        } finally {
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
        return arrDoc;
    }
}
