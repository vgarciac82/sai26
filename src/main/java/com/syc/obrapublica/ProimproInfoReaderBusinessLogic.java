package com.syc.obrapublica;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProimproInfoReaderBusinessLogic {

    private static final Logger log = LoggerFactory.getLogger(ProimproInfoReaderBusinessLogic.class);

    private static DataSource ds = null;

    public ProimproInfoReaderBusinessLogic() {
        if (ds != null)
            return;
        Context initContext;
        try {
            initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            ds = (DataSource) envContext.lookup("jdbc/proinpro");
        } catch (NamingException ne) {
            throw new RuntimeException("No se encontro la fuente 'jdbc/proinpro'");
        }
    }

    public List<String> getEPs(String cartera, String oli, String ur, String ue) throws Exception {
        Connection conn = null;
        List<String> eps = new ArrayList<String>();
        try {
            EjercicioFiscalBusinessLogic efbl = new EjercicioFiscalBusinessLogic();
            EjercicioFiscal efActivo = efbl.getEjercicioFiscalActivo();
            if (efActivo == null)
                throw new Exception("No se ha definido Ejercicio Fiscal Activo");
            conn = ds.getConnection();
            eps = ProimproInfoReaderManager.getEPs(conn, efActivo.getaEjercicioFiscal(), cartera, oli, ur, ue);
            return eps;
        } catch (Exception e) {
            throw e;
        } finally {
            if (conn != null)
                try {
                    CloseObject.closeObject(conn, true);
                } catch (Exception e2) {
                    log.warn("Error cerrando la DB.", e2);
                }
        }
    }

    public List<String> getOlis(String cartera, String ur, String ue) throws Exception {
        Connection conn = null;
        List<String> carteras = new ArrayList<String>();
        try {
            conn = ds.getConnection();
            carteras = ProimproInfoReaderManager.getOlis(conn, cartera, ur, ue);
            return carteras;
        } catch (Exception e) {
            throw e;
        } finally {
            if (conn != null)
                try {
                    CloseObject.closeObject(conn, true);
                } catch (Exception e2) {
                    log.warn("Error cerrando la DB.", e2);
                }
        }
    }

    public String getImporteOliCarteraURUE(String cartera, String capitulo, String oli, String ur, String ue) throws Exception {
        Connection conn = null;
        String importeOli = "0";
        try {
            conn = ds.getConnection();
            importeOli = ProimproInfoReaderManager.getImporteOliCarteraURUE(conn, cartera, capitulo, oli, ur, ue);
            return importeOli;
        } catch (Exception e) {
            throw e;
        } finally {
            if (conn != null)
                try {
                    CloseObject.closeObject(conn, true);
                } catch (Exception e2) {
                    log.warn("Error cerrando la DB.", e2);
                }
        }
    }

    public List<String> getCarterasProyecto(String cartera, String ur, String ue) throws Exception {
        Connection conn = null;
        List<String> carteras = new ArrayList<String>();
        try {
            conn = ds.getConnection();
            carteras = ProimproInfoReaderManager.getCarterasProyecto(conn, cartera, ur, ue);
            return carteras;
        } catch (Exception e) {
            throw e;
        } finally {
            if (conn != null)
                try {
                    CloseObject.closeObject(conn, true);
                } catch (Exception e2) {
                    log.warn("Error cerrando la DB.", e2);
                }
        }
    }
}
