/**
 */
package com.syc.contable;

import java.sql.Connection;
import java.sql.SQLException;
import com.syc.altaproveedor.AltaProveedorManager;
import com.syc.contable.core.CuentaBancariaManager;
import com.syc.crud.dsmngr.DataSourceManager;
import java.util.Base64;

/**
 * @author Martha Aurora Sánchez Valdivieso
 * para SYC Constructores de Sistemas
 * desarrollo gestion_conagua_sif
 * México D.F. 16/03/2012
 */
public class CuentaBancariaBussinessLogic extends DataSourceManager {

    /**
     */
    public CuentaBancariaBussinessLogic() {
        // TODO Auto-generated constructor stub
    }

    public CuentaBancariaBussinessLogic(String jniName) {
        super.init(jniName);
    }

    public StringBuffer buscaCuentasB(String BeneficiariosCuentasBancarias) throws Exception {
        StringBuffer RScuentasBancarias = new StringBuffer();
        Connection conn = null;
        try {
            conn = getConnection();
            RScuentasBancarias = CuentaBancariaManager.BuscaCuentas(conn, BeneficiariosCuentasBancarias);
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
        return RScuentasBancarias;
    }

    public boolean UpdateStatusBCB(String BeneficiariosCuentasBancarias, String listaFolios, String cLogin) throws Exception {
        Connection conn = null;
        boolean actualizaStatus = false;
        AltaProveedorManager manager = null;
        try {
            conn = getConnection();
            manager = new AltaProveedorManager();
            int row = CuentaBancariaManager.UpdateStatusBCB(conn, BeneficiariosCuentasBancarias);
            if (row > 0) {
                manager.saveBitacoraAltaProveedorVarios(conn, listaFolios, "Carga layout cuenta bancaria", "S", "V", 5, 4, cLogin);
                actualizaStatus = true;
                conn.commit();
            } else {
                actualizaStatus = false;
                conn.rollback();
            }
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
            manager = null;
        }
        return actualizaStatus;
    }
}
