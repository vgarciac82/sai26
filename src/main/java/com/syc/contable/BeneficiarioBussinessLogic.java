package com.syc.contable;

import java.sql.Connection;
import java.sql.SQLException;
import com.syc.altaproveedor.AltaProveedorManager;
import com.syc.contable.core.BeneficiarioManager;
import com.syc.crud.dsmngr.DataSourceManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import java.util.Base64;

/**
 * @author Martha Aurora Sánchez Valdivieso
 * para SYC Constructores de Sistemas
 * desarrollo gestion_conagua_sif
 * México D.F. 16/02/2012
 */
public class BeneficiarioBussinessLogic extends DataSourceManager {

    public BeneficiarioBussinessLogic(String jniName) {
        super.init(jniName);
    }

    public StringBuffer buscaBeneficiarios(String listaCBEN) throws Exception {
        StringBuffer sb = new StringBuffer();
        Connection conn = null;
        try {
            conn = getConnection();
            sb = BeneficiarioManager.BuscaBeneficiarios(conn, listaCBEN);
        } catch (SQLException e) {
            if (conn != null) {
                e.printStackTrace();
                conn.rollback();
                //throw  new GestionException(e.getMessage());
            }
        } finally {
            CloseObject.closeObject(conn);
        }
        return sb;
    }

    public StringBuffer BuscaBenDocCom(String listaCBEN) throws Exception {
        StringBuffer sb2 = new StringBuffer();
        Connection conn = null;
        try {
            conn = getConnection();
            sb2 = BeneficiarioManager.BuscaBenDocCom(conn, listaCBEN);
        } catch (SQLException e) {
            if (conn != null) {
                e.printStackTrace();
                conn.rollback();
                //throw  new GestionException(e.getMessage());
            }
        } finally {
            CloseObject.closeObject(conn);
        }
        return sb2;
    }

    public StringBuffer BuscaRDocCom(String listaCBEN) throws Exception {
        StringBuffer sb3 = new StringBuffer();
        Connection conn = null;
        try {
            conn = getConnection();
            sb3 = BeneficiarioManager.BuscaRDocCom(conn, listaCBEN);
        } catch (SQLException e) {
            if (conn != null) {
                e.printStackTrace();
                conn.rollback();
                //throw  new GestionException(e.getMessage());
            }
        } finally {
            CloseObject.closeObject(conn);
        }
        return sb3;
    }

    public StringBuffer BuscaBCB(String listaCBEN) throws Exception {
        StringBuffer sb4 = new StringBuffer();
        Connection conn = null;
        try {
            conn = getConnection();
            sb4 = BeneficiarioManager.BuscaBenCBan(conn, listaCBEN);
        } catch (SQLException e) {
            if (conn != null) {
                e.printStackTrace();
                conn.rollback();
                //throw  new GestionException(e.getMessage());
            }
        } finally {
            CloseObject.closeObject(conn);
        }
        return sb4;
    }

    public boolean UpdateStatusBen(String listaCBEN, String listaFolios, String cLogin) throws Exception {
        Connection conn = null;
        boolean actualizaStatus = false;
        AltaProveedorManager manager = null;
        try {
            conn = getConnection();
            manager = new AltaProveedorManager();
            int row = BeneficiarioManager.updateStatusBen(conn, listaCBEN);
            if (row > 0) {
                actualizaStatus = true;
                manager.saveBitacoraAltaProveedorVarios(conn, listaFolios, "Layout beneficiario cargado", "S", "V", 5, 4, cLogin);
                BeneficiarioManager.envioAlertas(conn, listaCBEN);
                conn.commit();
            } else {
                actualizaStatus = false;
                conn.rollback();
            }
        } catch (Exception e) {
            conn.rollback();
            throw new Exception(e);
        } finally {
            CloseObject.closeObject(conn);
        }
        return actualizaStatus;
    }
}
