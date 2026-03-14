package com.syc.contable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import com.syc.contable.core.CadenasPManager;
import com.syc.crud.dsmngr.DataSourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class CadenasPBussinessLogic extends DataSourceManager {

    private static Logger log = LoggerFactory.getLogger(AdecuacionBusinessLogic.class);

    public CadenasPBussinessLogic(String jniName) {
        super.init(jniName);
    }

    public ArrayList<String> buscaCompromisos(String listaFolios, String listaCuentaBancaria, String listaFechas, String sUsuario, String sCadena) throws Exception {
        ArrayList<String> arrListaComp = null;
        Connection conn = null;
        try {
            conn = getConnection();
            arrListaComp = CadenasPManager.BuscaCompromisos(conn, listaFolios, listaCuentaBancaria, listaFechas, sUsuario, sCadena);
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
    /*public boolean ActualizaStatus(String listaFolios ) throws Exception{
		ArrayList<String> arrListaComp = null;
		
		Connection conn=null;
		
		try{
			conn=getConnection();
			CadenasPManager.UpdateStatus(conn,listaFolios);
		}
		catch (SQLException e) {
			if(conn != null){
				e.printStackTrace();
				conn.rollback();
				//throw  new GestionException(e.getMessage());
			}
		}
		finally{
			if (conn!=null){
				conn.close();
			}
			conn=null;
		}
		return true;
	}*/
}
