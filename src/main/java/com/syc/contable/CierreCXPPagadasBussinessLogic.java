package com.syc.contable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import com.syc.contable.core.CierreCxPPagadasManager;
import com.syc.crud.dsmngr.DataSourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class CierreCXPPagadasBussinessLogic extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(AdecuacionBusinessLogic.class);

    public CierreCXPPagadasBussinessLogic(String jniName) {
        super.init(jniName);
    }

    public ArrayList<String> buscaCompromisos(String sTipoDoc, String scContra, String scRFC, String sUsuario) throws Exception {
        ArrayList<String> arrListaComp = null;
        Connection conn = null;
        try {
            conn = getConnection();
            arrListaComp = CierreCxPPagadasManager.BuscaCompromisos(conn, sTipoDoc, scContra, scRFC, sUsuario);
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

    public boolean ActualizaStatus(String listaFolios) throws Exception {
        ArrayList<String> arrListaComp = null;
        Connection conn = null;
        try {
            conn = getConnection();
            //CadenasPManager.UpdateStatus(conn,listaFolios);
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
        return true;
    }

    public ArrayList<String> ArmaDocumentoComprobatorio(String listaIds) throws Exception {
        ArrayList<String> arrListaComp = null;
        Connection conn = null;
        try {
            conn = getConnection();
            //arrListaComp = OperacionAjenaManager.CreaDocumentacionComprobatoria(conn, listaIds);
            //CadenasPManager.updateHeaderCompromisos(conn,listaIds);
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
